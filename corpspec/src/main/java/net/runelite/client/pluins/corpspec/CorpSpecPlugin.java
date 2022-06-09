package net.runelite.client.pluins.corpspec;

import com.google.inject.Provides;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.willemmmoapi.WillemmmoApiPlugin;
import net.runelite.client.plugins.willemmmoapi.tasks.Calculations;
import net.runelite.client.plugins.willemmmoapi.tasks.CreateMenuEntry;
import net.runelite.client.plugins.willemmmoapi.tasks.GameApi;
import net.runelite.client.plugins.willemmmoapi.tasks.InventoryApi;
import net.runelite.client.plugins.willemmmoapi.tasks.NPCApi;
import net.runelite.client.plugins.willemmmoapi.tasks.ObjectApi;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDependency(WillemmmoApiPlugin.class)
@Slf4j
@PluginDescriptor(
	name = "Corp Spec",
	description = "Plugin to QOL killing Corp",
	tags = {"corp", "Willemmmo", "spec", "QOL", "overlay"},
	enabledByDefault = false
)
public class CorpSpecPlugin extends Plugin
{
	@Inject
	public Client client;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private CorpSpecOverlay overlay;
	@Inject
	private CorpSpecConfig config;
	@Inject
	private ConfigManager configManager;
	@Inject
	private WillemmmoApiPlugin apiPlugin;
	@Inject
	private ObjectApi objectApi;
	@Inject
	private NPCApi npcApi;
	@Inject
	private Calculations calculations;
	@Inject
	private InventoryApi inventory;
	Player targetplayer;

	GameApi gameApi;

	GameObject POH_POOL;
	GameObject POH_JEWELERYBOX;
	GameObject Cannon;
	CorpSpecState state;
	Player player;
	NPC currentNPC;
	String NPCName;
	int[] currentMapRegion;
	private static final Set<Integer> CORP_CAVE_REGION = Set.of(11587, 11588, 11589, 11843, 11844, 11845, 12099, 12100, 12101);
	private static final Set<Integer> CORP_CAVE_INSTANCE_REGION = Set.of(11844);
	private static Set<Integer> NoSpecItems = new HashSet<>();
	private static Set<Integer> DoSpecItems = new HashSet<>();
	int timeout;
	int spelltimeout, attackdelay, pooltimeout, boxtimeout, instancedelay, housetimeout;
	boolean ForceTeleport = false;

	@Provides
	CorpSpecConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(CorpSpecConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		if (!config.spectransferweapons().isEmpty())
		{
			DoSpecItems.addAll(Stream.of(config.spectransferweapons()
					.split(",", -1))
				.map(Integer::parseInt)
				.collect(Collectors.toSet()));
			log.info(DoSpecItems.toString());
		}
		if (!config.nospectransferweapon().isEmpty())
		{
			NoSpecItems.addAll(Stream.of(config.nospectransferweapon()
					.split(",", -1))
				.map(Integer::parseInt)
				.collect(Collectors.toSet()));
			log.info(NoSpecItems.toString());
		}
	}

	@Override
	protected void shutDown()
	{
		log.debug("Stopping Corp Spec Plugin");
		state = null;
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onClientTick(ClientTick event)//use this to do every 0.6 seconds
	{

	}

	@Subscribe
	public void onGameTick(GameTick event)//use this to do every MS
	{
		currentMapRegion = client.getMapRegions();
		targetplayer = getPlayer();
		player = client.getLocalPlayer();
		POH_POOL = searchPool();
		POH_JEWELERYBOX = searchBox();
		Cannon = searchCannon();
		if (client != null && player != null && client.getGameState() == GameState.LOGGED_IN && client.getGameState() != GameState.LOADING)
		{
			state = getState();
			switch (state)
			{
				case TELEPORT_HOUSE:
					TeleportToHouse();
					break;
				case READY_TO_FIGHT:
				case ATTACK_CORP:
					HandleCorpFight();
					break;
				//case IN_COMBAT:
				case IN_HOUSE:
				case TELEPORT_BOX:
					TeleportCorp(getTickDelay());
					break;
				case GET_SPEC:
					DrinkPool(getTickDelay());
					break;
				case IN_INSTANCE:
					EnterPassage(getTickDelay());
					break;
				case AT_CORP:
					if (config.UseInstance())
					{
						joinInstance(getTickDelay());
						break;
					}
					if (!config.UseInstance())
					{
						EnterPassage(getTickDelay());
						break;
					}
					break;
				case TIMEOUT:
					timeout--;
					log.info(String.valueOf(timeout));
					break;
				case IRERATING:
					break;
			}
		}
	}

	private void HandleCorpFight()
	{
		NPC target = findNPC();
		if (target == null)
		{
			if (Cannon != null)
			{
				if (pooltimeout == 0)
				{
					apiPlugin.doGameObjectAction(Cannon, MenuAction.GAME_OBJECT_FIRST_OPTION.getId(), 3);
					pooltimeout = 3;
				}
				pooltimeout--;
			}
			return;
		}
		if (targetplayer != null)
		{
			ItemComposition weapon = client.getItemDefinition(targetplayer.getPlayerComposition().getEquipmentId(KitType.WEAPON));
			if (NoSpecItems.contains(weapon.getId()))
			{
				log.info("NoSpecItem Found...");
				attackNPC(target);
			}
			if (DoSpecItems.contains(weapon.getId()))
			{
				log.info("DoSpecWeapon found...");
				CheckSpell();
			}
		}
		if (targetplayer == null)
		{
			attackNPC(target);
		}
	}

	private void CheckSpell()
	{
		if (targetplayer == null)
		{
			log.debug("no player found");
			return;
		}
		castSpell(targetplayer);
	}


	private CorpSpecState getState()
	{
		if (ForceTeleport || (client.getBoostedSkillLevel(Skill.HITPOINTS) <= config.HpToTeleport() || client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) <= 100) && isInCorp())
		{
			return CorpSpecState.TELEPORT_HOUSE;
		}
		if (timeout > 0)
		{
			//activate run??
			return CorpSpecState.TIMEOUT;
		}
		if (player.getInteracting() != null)
		{
			currentNPC = (NPC) player.getInteracting();
			if (currentNPC != null && currentNPC.getHealthRatio() == -1)//no healthbar on npc
			{
				log.info("NPC doesnt have a healthbar");
				currentNPC = findNPC();
				if (currentNPC != null)
				{
					return CorpSpecState.ATTACK_CORP;
				}
				return CorpSpecState.NPC_NOT_FOUND;
			}
			return CorpSpecState.IN_COMBAT;
		}
		if (POH_POOL != null && POH_JEWELERYBOX != null && !isInCorp())
		{
			ForceTeleport = false;
			if (client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) <= 500 || client.getBoostedSkillLevel(Skill.HITPOINTS) < client.getRealSkillLevel(Skill.HITPOINTS))
			{
				return CorpSpecState.GET_SPEC;
			}
			if (client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) >= 1000)
			{
				pooltimeout = 0;
				ForceTeleport = false;
				return CorpSpecState.TELEPORT_BOX;
			}
			return CorpSpecState.IN_HOUSE;
		}
		if (isInCorp())
		{
			if (inInstance() && config.UseInstance())
			{
				if (client.getLocalPlayer().getLocalLocation().getX() > 7500)
				{
					instancedelay = 0;
					return CorpSpecState.READY_TO_FIGHT;
				}
				return CorpSpecState.IN_INSTANCE;
			}
			if (!inInstance() && !config.UseInstance() && client.getLocalPlayer().getLocalLocation().getX() > 7500)
			{
				instancedelay = 0;
				return CorpSpecState.READY_TO_FIGHT;
			}
			return CorpSpecState.AT_CORP;
		}
		return CorpSpecState.STUCK;
	}

	@Nullable
	private GameObject searchPool()
	{
		GameObject Pool = objectApi.findNearestGameObjectMenuWithin(client.getLocalPlayer().getWorldLocation(), config.distance1(), "Drink");
		return Pool;
	}

	@Nullable
	private GameObject searchCannon()
	{
		GameObject cannon = objectApi.findNearestGameObject(6);
		if (cannon == null)
		{
			cannon = objectApi.findNearestGameObject(14916);//broken
		}
		return cannon;
	}

	@Nullable
	private GameObject searchBox()
	{
		GameObject JeweleryBox = objectApi.findNearestGameObject(29154);
		if (JeweleryBox == null)
		{
			JeweleryBox = objectApi.findNearestGameObject(29155);
			if (JeweleryBox == null)
			{
				JeweleryBox = objectApi.findNearestGameObject(29156);
				return JeweleryBox;
			}
			return JeweleryBox;
		}
		return JeweleryBox;
	}

	@Nullable
	private void TeleportCorp(int delay)
	{
		ForceTeleport = false;
		if (POH_JEWELERYBOX == null)
		{
			log.info("Error... Box not found");
			return;
		}
		if (!isInCorp())
		{
			if (boxtimeout == 0)
			{
				apiPlugin.doGameObjectAction(POH_JEWELERYBOX, MenuAction.GAME_OBJECT_THIRD_OPTION.getId(), delay);
				housetimeout = 0;
				boxtimeout = delay;
			}
			boxtimeout--;
		}
	}

	@Nullable
	private void DrinkPool(int delay)
	{
		ForceTeleport = false;
		if (POH_POOL == null)
		{
			log.info("Error... Pool not found");
			return;
		}
		if (client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) == 1000 && client.getBoostedSkillLevel(Skill.HITPOINTS) >= client.getRealSkillLevel(Skill.HITPOINTS))
		{
			return;
		}
		else
		{
			if (pooltimeout == 0)
			{
				apiPlugin.doGameObjectAction(POH_POOL, MenuAction.GAME_OBJECT_FIRST_OPTION.getId(), delay);
				log.info("Drinking from pool");
				pooltimeout = delay;
				housetimeout = 0;
			}
			pooltimeout--;
		}
	}

	@Nullable
	private void joinInstance(int delay)
	{
		if (isInCorp() && !inInstance() && instancedelay <= 0)
		{
			log.info("entering instance");
			GameObject portal = objectApi.findNearestGameObject(9370);
			apiPlugin.doGameObjectAction(portal, MenuAction.GAME_OBJECT_FIRST_OPTION.getId(), delay);
			instancedelay = delay;
		}
		instancedelay--;
	}

	@Nullable
	private void EnterPassage(int delay)
	{
		if (isInCorp() && instancedelay <= 0 && client.getLocalPlayer().getLocalLocation().getX() < 7500)
		{
			GameObject passage = objectApi.findNearestGameObjectMenuWithin(client.getLocalPlayer().getWorldLocation(), config.distance1(), "Go-through");
			if (passage == null)
			{
				log.info("passage not found");
			}
			apiPlugin.doGameObjectAction(passage, MenuAction.GAME_OBJECT_FIRST_OPTION.getId(), delay);
			instancedelay = delay;
		}
		instancedelay--;
	}

	public boolean isInCorp()
	{
		int[] currentMapRegions = client.getMapRegions();
		if (client.getMapRegions() == null)
		{
			return false;
		}
		for (int region : currentMapRegions)
		{
			if (CORP_CAVE_REGION.contains(region))
			{
				return true;
			}
		}
		return false;
	}

	public boolean inInstance()
	{
		int[] currentMapRegions = client.getMapRegions();
		if (!client.isInInstancedRegion())
		{
			return false;
		}
		for (int region : currentMapRegions)
		{
			if (!CORP_CAVE_INSTANCE_REGION.contains(region))
			{
				return false;
			}
		}
		return true;
	}

	@Nullable
	private NPC findNPC()
	{
		NPCName = "Corporeal Beast";
		NPC target = npcApi.findClosestNPC("Corporeal Beast");
		return target;
	}

	private int getTickDelay()
	{
		return calculations.getRandomIntBetweenRange(6, 10);
	}

	@Nullable
	private Player getPlayer()
	{
		final List<Player> players = client.getPlayers();
		for (final Player player : players)
		{
			if (client.getLocalPlayer() != null && player.getName().equalsIgnoreCase(config.targetaccount()))
			{
				return player;
			}
		}
		return null;
	}

	private void castSpell(Player target)
	{
		if (spelltimeout > 0)
		{
			spelltimeout--;
		}
		if (spelltimeout == 0 && client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) == 1000)
		{
			CreateMenuEntry entry = new CreateMenuEntry("Cast", target.getName(), target.getPlayerId(), MenuAction.WIDGET_TARGET_ON_PLAYER.getId(), 0, 0, false);
			apiPlugin.oneClickCastSpell(WidgetInfo.SPELL_ENERGY_TRANSFER, entry, target.getConvexHull().getBounds(), getTickDelay());
			spelltimeout = 10;
		}
		if (spelltimeout == 6)
		{
			CreateMenuEntry entry = new CreateMenuEntry("Cast", target.getName(), target.getPlayerId(), MenuAction.WIDGET_TARGET_ON_PLAYER.getId(), 0, 0, false);
			apiPlugin.oneClickCastSpell(WidgetInfo.SPELL_HEAL_OTHER, entry, target.getConvexHull().getBounds(), getTickDelay());
			if (client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) == 0)
			{
				ForceTeleport = true;
			}
		}
	}

	private void TeleportToHouse()
	{
		WidgetItem teleporttohouse = inventory.getWidgetItem(8013);
		if (teleporttohouse != null && housetimeout <= 0)
		{
			housetimeout = 5;
			pooltimeout = 0;
			ForceTeleport = false;
			inventory.interactWithItem(teleporttohouse.getId(), getTickDelay(), "Break");
		}
		housetimeout--;
	}

	@Nullable
	private void attackNPC(NPC npc)
	{
		if (attackdelay <= 0)
		{
			if (npc != null)
			{
				log.info("index : " + npc.getIndex());
				CreateMenuEntry entry = new CreateMenuEntry("", " ", npc.getIndex(), MenuAction.NPC_SECOND_OPTION, 0, 0, false);
				log.info(entry.toString());
				if (entry != null)
				{
					apiPlugin.doActionMsTime(entry, npc.getConvexHull().getBounds(), getTickDelay());
					attackdelay = 3;
				}
			}
		}
		attackdelay--;
	}
}
