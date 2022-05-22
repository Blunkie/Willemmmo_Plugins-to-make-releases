package net.runelite.client.pluins.corpspec;

import com.google.inject.Provides;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.willemmmoapi.WillemmmoApiPlugin;
import net.runelite.client.plugins.willemmmoapi.tasks.Calculations;
import net.runelite.client.plugins.willemmmoapi.tasks.GameApi;
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

	Player targetplayer;

	GameApi gameApi;

	GameObject POH_POOL;
	GameObject POH_JEWELERYBOX;
	CorpSpecState state;
	Player player;
	NPC currentNPC;
	String NPCName;
	int[] currentMapRegion;
	private static final Set<Integer> CORP_CAVE_REGION = Set.of(11587, 11588, 11589, 11843, 11844, 11845, 12099, 12100, 12101);
	private static final Set<Integer> CORP_CAVE_INSTANCE_REGION = Set.of(11844);
	int timeout;
	int pooltimeout;
	int boxtimeout;
	int instancedelay;
	public static boolean iterating;

	@Provides
	CorpSpecConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(CorpSpecConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
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
		Player player1 = getPlayer();
		if (player1 != null)
		{
			log.info(player1.getName());
		}
		player = client.getLocalPlayer();
		POH_POOL = searchPool();
		POH_JEWELERYBOX = searchBox();
		if (state != null)
		{
			log.info(state.name());
		}
		if (client != null && player != null && client.getGameState() == GameState.LOGGED_IN && client.getGameState() != GameState.LOADING)
		{
			state = getState();
			switch (state)
			{
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
					joinInstance(getTickDelay());
					break;
				case TIMEOUT:
					timeout--;
					log.info(String.valueOf(timeout));
					break;
				case IRERATING:
					break;
				case ATTACK_CORP:
					//attack corp here
					break;
				case IN_COMBAT:
					timeout = getTickDelay();
					break;
			}
		}
	}

	private CorpSpecState getState()
	{
		if (timeout > 0)
		{
			//activate run??
			return CorpSpecState.TIMEOUT;
		}
		if (iterating)
		{
			return CorpSpecState.IRERATING;
		}
		//if (prayerutils.Ismoving)
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
		if (POH_POOL != null && POH_JEWELERYBOX != null)
		{
			if (client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) <= 500 || client.getBoostedSkillLevel(Skill.HITPOINTS) < client.getRealSkillLevel(Skill.HITPOINTS))
			{
				return CorpSpecState.GET_SPEC;
			}
			if (client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) >= 1000)
			{
				pooltimeout = 0;
				return CorpSpecState.TELEPORT_BOX;
			}
			return CorpSpecState.IN_HOUSE;
		}
		if (isInCorp())
		{
			if (inInstance())
			{
				if (client.getLocalPlayer().getLocalLocation().getX() > 7500)
				{
					instancedelay = 0;
					return CorpSpecState.READY_TO_FIGHT;
				}
				return CorpSpecState.IN_INSTANCE;
			}
			return CorpSpecState.AT_CORP;
		}
		return CorpSpecState.STUCK;
	}

	@Nullable
	private GameObject searchPool()
	{
		GameObject Pool = objectApi.findNearestGameObjectMenuWithin(client.getLocalPlayer().getWorldLocation(), config.distance1(), "Drink");
		if (Pool == null)
		{
			return null;
		}
		return Pool;
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
				if (JeweleryBox == null)
				{
					return null;
				}
				return JeweleryBox;
			}
			return JeweleryBox;
		}
		return JeweleryBox;
	}

	@Nullable
	private void TeleportCorp(int delay)
	{
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
				boxtimeout = delay;
			}
			boxtimeout--;
		}
	}

	@Nullable
	private void DrinkPool(int delay)
	{
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
		if (isInCorp() && inInstance() && instancedelay <= 0 && client.getLocalPlayer().getLocalLocation().getX() < 7500)
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
		if (target == null)
		{
			return null;
		}
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
}
