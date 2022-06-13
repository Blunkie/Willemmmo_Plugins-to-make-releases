/*
 * Copyright (c) 2019, Ganom <https://github.com/Ganom>
 * Copyright (c) 2019, Xkylee <https://github.com/xKylee>
 * HUGE ThANK YOU TO THE BOTH OF THEM
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.autogodwars;

import com.google.inject.Provides;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.VarPlayer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import static net.runelite.client.plugins.autogodwars.NPCContainer.BossMonsters.GENERAL_GRAARDOR;
import static net.runelite.client.plugins.autogodwars.NPCContainer.BossMonsters.KRIL_TSUTSAROTH;
import net.runelite.client.plugins.iutils.ActionQueue;
import net.runelite.client.plugins.iutils.CalculationUtils;
import net.runelite.client.plugins.iutils.InventoryUtils;
import net.runelite.client.plugins.iutils.LegacyMenuEntry;
import net.runelite.client.plugins.iutils.MenuUtils;
import net.runelite.client.plugins.iutils.MouseUtils;
import net.runelite.client.plugins.iutils.PrayerUtils;
import net.runelite.client.plugins.iutils.game.Game;
import net.runelite.client.plugins.iutils.iUtils;
import org.pf4j.Extension;

@Extension
@Slf4j
@PluginDependency(iUtils.class)
@PluginDescriptor(
	name = "Auto Godwars",
	description = "Auto Godwars",
	tags = {"Auto", "Godwars", "Willemmmo"}
)
public class AutoGodwarsPlugin extends Plugin
{
	private static final int GENERAL_REGION = 11347;
	private static final int ARMA_REGION = 11346;
	private static final int SARA_REGION = 11602;
	private static final int ZAMMY_REGION = 11603;
	public static final int MINION_AUTO1 = 6154;
	public static final int MINION_AUTO2 = 6156;
	public static final int MINION_AUTO3 = 7071;
	public static final int MINION_AUTO4 = 7073;
	public static final int GENERAL_AUTO1 = 7018;
	public static final int GENERAL_AUTO2 = 7020;
	public static final int GENERAL_AUTO3 = 7021;
	public static final int ZAMMY_GENERIC_AUTO = 64;
	public static final int KRIL_AUTO = 6948;
	public static final int KRIL_SPEC = 6950;
	public static final int ZAKL_AUTO = 7077;
	public static final int BALFRUG_AUTO = 4630;
	public static final int ZILYANA_MELEE_AUTO = 6964;
	public static final int ZILYANA_AUTO = 6967;
	public static final int ZILYANA_SPEC = 6970;
	public static final int STARLIGHT_AUTO = 6376;
	public static final int BREE_AUTO = 7026;
	public static final int GROWLER_AUTO = 7037;
	public static final int KREE_RANGED = 6978;
	public static final int SKREE_AUTO = 6955;
	public static final int GEERIN_AUTO = 6956;
	public static final int GEERIN_FLINCH = 6958;
	public static final int KILISA_AUTO = 6957;
	public static final int Armadyl_Altar = 26365;
	public static final int Bandos_Altar = 26366;
	public static final int Saradomin_Altar = 26364;
	public static final int Zamorak_Altar = 26363;
	@Inject
	private Client client;
	@Inject
	private AutoGodwarsConfig config;
	@Inject
	private AutoGodwarsHotkeyListener hotkeyListener;
	@Inject
	private getStates getStates;
	@Inject
	private KeyManager keyManager;
	@Getter(AccessLevel.PACKAGE)
	private final Set<NPCContainer> npcContainers = new HashSet<>();
	@Getter(AccessLevel.PACKAGE)
	private final Set<GameObjectContainer> gameObjectContainers = new HashSet<>();
	protected boolean enableAutoPrayers = false;
	private boolean validRegion;
	@Getter(AccessLevel.PACKAGE)
	private long lastTickTime;
	@Inject
	private iUtils utils;
	@Inject
	private Game game;
	@Inject
	private PrayerUtils prayerUtils;
	@Inject
	private CalculationUtils calc;
	@Inject
	private MouseUtils mouse;
	@Inject
	private MenuUtils menu;
	@Inject
	private ActionQueue action;
	@Inject
	private InventoryUtils inventoryUtils;

	@Provides
	AutoGodwarsConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AutoGodwarsConfig.class);
	}

	@Override
	public void startUp()
	{
		keyManager.registerKeyListener(hotkeyListener);
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		if (regionCheck())
		{
			npcContainers.clear();
			for (NPC npc : client.getNpcs())
			{
				addNpc(npc);
			}
			validRegion = true;
		}
		else if (!regionCheck())
		{
			validRegion = false;
			npcContainers.clear();
			gameObjectContainers.clear();
		}
	}

	@Override
	public void shutDown()
	{
		npcContainers.clear();
		gameObjectContainers.clear();
		validRegion = false;
		keyManager.unregisterKeyListener(hotkeyListener);
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (regionCheck())
		{
			npcContainers.clear();
			for (NPC npc : client.getNpcs())
			{
				addNpc(npc);
			}
			validRegion = true;
		}
		else if (!regionCheck())
		{
			validRegion = false;
			npcContainers.clear();
		}
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned event)
	{
		if (!validRegion)
		{
			return;
		}
		addNpc(event.getNpc());
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned event)
	{
		if (!validRegion)
		{
			return;
		}
		removeNpc(event.getNpc());
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event)
	{
		if (!validRegion)
		{
			return;
		}
		addGameObject(event.getGameObject());
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event)
	{
		if (!validRegion)
		{
			return;
		}
		removeGameObject(event.getGameObject());
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (event == null)
		{
			log.info("TickEvent is Empty");
		}
		lastTickTime = System.currentTimeMillis();
		if (!validRegion)
		{
			return;
		}
		loadBossesIn();
		//::todo
		if (config.enableAutoEat())
		{
			PlayerStates playerStates = getStates.getPlayerStates();
			States states = getStates.getState();
			if (states == States.lol)
			{
				infoMessage("");
			}
			switch (playerStates)
			{
				case EAT_FOOD:
					break;
				case FUNCTION_FOUND:
					break;
				case ERROR:
					log.info("There is an error");
					break;
				case POISON:
					log.info("Antipoison running");
					break;
			}
		}
		switch (client.getLocalPlayer().getWorldLocation().getRegionID())
		{
			case ARMA_REGION:
				if (config.enableAutoPrayArma())
				{
					checkForPrayers();
					break;
				}
			case ZAMMY_REGION:
				if (config.enableAutoPrayZammy())
				{
					checkForPrayers();
					break;
				}
			case SARA_REGION:
				if (config.enableAutoPraySara())
				{
					checkForPrayers();
					break;
				}
			case GENERAL_REGION:
				if (config.enableAutoPrayBandos())
				{
					checkForPrayers();
					break;
				}
				break;
		}
		/*int is = game.varp(VarPlayer.IS_POISONED.getId());
		log.debug("poison is here : " + is);
		if (VarPlayer.IS_POISONED.getId() != 0)
		{
			log.info("" + VarPlayer.IS_POISONED.getId());
			log.info("a" + game.varp(VarPlayer.IS_POISONED.getId()));
			log.info("Value of poison : " + game.varp(VarPlayer.POISON.getId()));
		}
		defaultTasks();
		defaultTasks();*/
		//hoe pak ik dit aan
		/*
		  check map region == current map region
		  are er monsters>>??
		  nee. ga to default setup + tiles
		 */
	}
	private void loadBossesIn()
	{
		for (NPCContainer npc : getNpcContainers())
		{
			npc.setNpcInteracting(npc.getNpc().getInteracting());
			if (npc.getTicksUntilAttack() >= 0)
			{
				npc.setTicksUntilAttack(npc.getTicksUntilAttack() - 1);
			}
			for (int animation : npc.getAnimations())
			{
				if (animation == npc.getNpc().getAnimation() && npc.getTicksUntilAttack() < 1)
				{
					npc.setTicksUntilAttack(npc.getAttackSpeed());
				}
			}
		}
	}

	private void runArma()
	{
		Set<NPCContainer> container = getNpcContainers();
		boolean kreeAlive = container.stream().anyMatch(x -> x.getID() == NpcID.KREEARRA);
		if (container.isEmpty())
		{
			log.info("There is nothing alive. . . Resetting");
		}
		if (!container.isEmpty())
		{
			log.info("There are Npc's alive. . . Handle them.");
			if (kreeAlive)
			{
				infoMessage("");//focus on kree
			}
			if (!kreeAlive)
			{
				//focus on target
				if (container.stream().anyMatch(x -> x.getNpcInteracting() == client.getLocalPlayer()))
				{
					infoMessage("");
				}
			}
		}
		//log.info("Starting Kree");

		for (NPCContainer npcContainer : getNpcContainers())
		{
			if (npcContainer.getNpc() == null)
			{
				return;
			}
			if (npcContainer.getNpc().getId() == NpcID.KREEARRA)
			{
				log.info("kree is alive");
			}
		}
		Set<NPCContainer> kreeFound = npcContainers.stream().filter(x -> x.getID() == NpcID.KREEARRA).collect(Collectors.toSet());
		if (!kreeFound.isEmpty())
		{
			log.info("StrangeFind");
		}

		for (NPCContainer npc : getNpcContainers())

		{
			if (npc.getNpc() == null)//list is empty
			{
				continue;
			}
			int ticksLeft = npc.getTicksUntilAttack();
			NPCContainer.AttackStyle attackStyle = npc.getAttackStyle();
			if (ticksLeft <= 0)
			{
				continue;
			}
			if (config.ignoreNonAttacking() && npc.getNpcInteracting() != client.getLocalPlayer())
			{
				continue;
			}
			if (ticksLeft == 1)
			{
				//CheckForPrayerSwitch(attackStyle);
			}
		}

	}

	/*private void runBandos()
	{
		final Player player = client.getLocalPlayer();
		final WorldPoint altar_Base = findAltar(Bandos_Altar);
		if (altar_Base == null)
		{
			return;//altar is  not existing ending function
		}
		WorldPoint SW = new WorldPoint(altar_Base.getX() - 6, altar_Base.getY() - 20, altar_Base.getPlane());
		WorldPoint NE = new WorldPoint(altar_Base.getX() + 6, altar_Base.getY() - 2, altar_Base.getPlane());

		if (isFocusAlive(config.focusBandos().name()))
		{
			NPC focusNPC = fetchNPC(config.focusBandos().name());
			if (focusNPC == null)
			{
				return;
			}
			NPC currentNPC = (NPC) player.getInteracting();
			if (currentNPC == null || currentNPC != focusNPC)
			{
				SendAttackOrder(focusNPC);
			}
		}
		if (!isFocusAlive(config.focusBandos().name()))
		{
			NPC killNPC = fetchFirstNPC();
			if (player.getInteracting() != null || killNPC == null)
			{
				return;
			}
			SendAttackOrder(killNPC);
		}
		for (NPCContainer npc : getNpcContainers())
		{
			if (npc.getNpc() == null)
			{
				continue;
			}
			int ticksLeft = npc.getTicksUntilAttack();
			NPCContainer.AttackStyle attackStyle = npc.getAttackStyle();
			if (ticksLeft <= 0)
			{
				continue;
			}
			if (config.ignoreNonAttacking() && npc.getNpcInteracting() != client.getLocalPlayer() && npc.getMonsterType() != GENERAL_GRAARDOR)
			{
				continue;
			}
			if (npc.getMonsterType() == GENERAL_GRAARDOR && npc.getNpcInteracting() != client.getLocalPlayer() && config.prioritiseRange())
			{
				attackStyle = NPCContainer.AttackStyle.RANGE;
			}
			if (ticksLeft == 1)
			{
				CheckForPrayerSwitch(attackStyle);
			}
		}
	}
	 */
	private void runBandos()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			infoMessage("Player not found in RunBandos");
			return;
		}
		if (getNpcContainers().isEmpty())
		{
			disableAllPrayer();
			//nothing alive. return to base
		}
		if (!getNpcContainers().isEmpty())
		{
			infoMessage("");
			//handlePrayers();
			//theres something alive
		}
	}

	private boolean isFocusAlive(String Name)
	{
		AutoGodwarsEnum.Focus focus = AutoGodwarsEnum.Focus.valueOf(Name);
		for (NPCContainer npcContainer : getNpcContainers())
		{
			if (npcContainer.getID() == focus.getId())
			{
				return true;
			}
		}
		return false;
	}

	@Nullable
	private NPC fetchFirstNPC()
	{
		if (!getNpcContainers().isEmpty())
		{
			return Objects.requireNonNull(getNpcContainers().stream().findFirst().orElse(null)).getNpc();
		}
		return null;
	}

	@Nullable
	private NPC fetchNPC(String Name)
	{
		AutoGodwarsEnum.Focus focus = AutoGodwarsEnum.Focus.valueOf(Name);
		for (NPCContainer npcContainer : getNpcContainers())
		{
			if (npcContainer.getID() == focus.getId())
			{
				return npcContainer.getNpc();
			}
		}
		return null;
	}

	private void SendAttackOrder(NPC npc)
	{
		Player player = client.getLocalPlayer();
		if (npc == null)
		{
			return;
		}
		log.info("Setting kill order");
		if (player.getInteracting() == null || !Objects.equals(player.getInteracting().getName(), npc.getName()))
		{
			utils.doNpcActionMsTime(npc, MenuAction.NPC_SECOND_OPTION.getId(), sleepDelay());
		}
	}

	@Nullable
	private WorldPoint findAltar(int Altar_ID)
	{
		WorldPoint worldPoint = null;

		for (GameObjectContainer obj : getGameObjectContainers())
		{
			if (obj.getGameObjectID() == Altar_ID)
			{
				GameObject altar = obj.getGameObject();
				if (altar == null)
				{
					return null;
				}
				worldPoint = altar.getWorldLocation();
			}
		}
		return worldPoint;
	}

	/*private void CheckForPrayerSwitch(NPCContainer.AttackStyle attackStyle)
	{
		if (attackStyle.getName().equals("Melee") && enableAutoPrayers)
		{
			if (!prayerUtils.isActive(Prayer.PROTECT_FROM_MELEE))
			{
				prayerUtils.toggle(Prayer.PROTECT_FROM_MELEE, sleepDelay());
			}
		}
		if (attackStyle.getName().equals("Range") && enableAutoPrayers)
		{
			if (!prayerUtils.isActive(Prayer.PROTECT_FROM_MISSILES))
			{
				prayerUtils.toggle(Prayer.PROTECT_FROM_MISSILES, sleepDelay());
			}
		}
		if (attackStyle.getName().equals("Mage") && enableAutoPrayers)
		{
			if (!prayerUtils.isActive(Prayer.PROTECT_FROM_MAGIC))
			{
				prayerUtils.toggle(Prayer.PROTECT_FROM_MAGIC, sleepDelay());
			}
		}
	}*/

	private void defaultTasks()
	{
		//Poison
		int PoisonValue = game.varp(VarPlayer.IS_POISONED.getId()); //<-40 Antivenom is still running //0 = nothing running //1000000 just got it//44+43 = 9 splat 11:50 left is 40
		//int PoisonVarp = game.varp(VarPlayer.POISON.getId()); // dit is1 op 1 de zelfde value
		if (PoisonValue > 0)
		{
			//AutoGodwarsEnum.Food.ANGLERFISH.getId();
			inventoryUtils.interactWithItem(391, sleepDelay(), "eat");
		}
		for (NPCContainer npc : getNpcContainers())
		{
			if (npc.getNpc() == null)
			{
				continue;
			}
			int ticksLeft = npc.getTicksUntilAttack();
			NPCContainer.AttackStyle attackStyle = npc.getAttackStyle();
			if (ticksLeft <= 0)
			{
				continue;
			}
			if (config.ignoreNonAttacking() && npc.getNpcInteracting() != client.getLocalPlayer() && npc.getMonsterType() != GENERAL_GRAARDOR && npc.getMonsterType() != KRIL_TSUTSAROTH)
			{
				continue;
			}
			if (npc.getMonsterType() == KRIL_TSUTSAROTH && npc.getNpcInteracting() != client.getLocalPlayer() && config.prioritiseMage())
			{
				attackStyle = NPCContainer.AttackStyle.MAGE;
			}
			if (npc.getMonsterType() == GENERAL_GRAARDOR && npc.getNpcInteracting() != client.getLocalPlayer() && config.prioritiseRange())
			{
				attackStyle = NPCContainer.AttackStyle.RANGE;
			}
			if (ticksLeft == 1)//this function switches {
			{
				if (attackStyle.getName().equals("Melee") && config.debug())
				{
					game.sendGameMessage("Melee hit incoming from " + npc.getNpcName());
				}
				if (attackStyle.getName().equals("Range") && config.debug())
				{
					game.sendGameMessage("Range Hit incoming from " + npc.getNpcName());
				}
				if (attackStyle.getName().equals("Mage") && config.debug())
				{
					game.sendGameMessage("Magic Hit incoming from " + npc.getNpcName());
				}
			}
		}
	}

	private boolean regionCheck()
	{
		return Arrays.stream(client.getMapRegions()).anyMatch(
			x -> x == ARMA_REGION || x == GENERAL_REGION || x == ZAMMY_REGION || x == SARA_REGION
		);
	}

	private boolean currentRegionCheck(int region)
	{
		return client.getLocalPlayer().getWorldLocation().getRegionID() == region;
	}

	private void addNpc(NPC npc)
	{
		if (npc == null)
		{
			return;
		}

		switch (npc.getId())
		{
			//adding selected npc's to the list depending on config
			case NpcID.FLIGHT_KILISA:
			case NpcID.FLOCKLEADER_GEERIN:
			case NpcID.KREEARRA:
			case NpcID.WINGMAN_SKREE:
				if (config.enableArma())
				{
					npcContainers.add(new NPCContainer(npc));
				}
			case NpcID.GENERAL_GRAARDOR:
			case NpcID.SERGEANT_GRIMSPIKE:
			case NpcID.SERGEANT_STEELWILL:
			case NpcID.SERGEANT_STRONGSTACK:
				if (config.enableBandos())
				{
					npcContainers.add(new NPCContainer(npc));
				}
			case NpcID.BREE:
			case NpcID.COMMANDER_ZILYANA:
			case NpcID.GROWLER:
			case NpcID.STARLIGHT:
				if (config.enableSara())
				{
					npcContainers.add(new NPCContainer(npc));
				}
			case NpcID.BALFRUG_KREEYATH:
			case NpcID.KRIL_TSUTSAROTH:
			case NpcID.TSTANON_KARLAK:
			case NpcID.ZAKLN_GRITCH:
				if (config.enableZammy())
				{
					npcContainers.add(new NPCContainer(npc));
				}
				break;

		}
	}

	private void addGameObject(GameObject gameObject)
	{
		if (gameObject == null)
		{
			return;
		}
		switch (gameObject.getId())
		{
			case Armadyl_Altar:
				if (config.enableArma())
				{
					gameObjectContainers.add(new GameObjectContainer(gameObject));
				}
			case Bandos_Altar:
				if (config.enableArma())
				{
					gameObjectContainers.add(new GameObjectContainer(gameObject));
				}
			case Saradomin_Altar:
				if (config.enableArma())
				{
					gameObjectContainers.add(new GameObjectContainer(gameObject));
				}
			case Zamorak_Altar:
				if (config.enableArma())
				{
					gameObjectContainers.add(new GameObjectContainer(gameObject));
				}
				break;
		}
	}

	private void removeNpc(NPC npc)
	{
		if (npc == null)
		{
			return;
		}

		switch (npc.getId())
		{
			case NpcID.SERGEANT_STRONGSTACK:
			case NpcID.SERGEANT_STEELWILL:
			case NpcID.SERGEANT_GRIMSPIKE:
			case NpcID.GENERAL_GRAARDOR:
			case NpcID.TSTANON_KARLAK:
			case NpcID.BALFRUG_KREEYATH:
			case NpcID.ZAKLN_GRITCH:
			case NpcID.KRIL_TSUTSAROTH:
			case NpcID.STARLIGHT:
			case NpcID.BREE:
			case NpcID.GROWLER:
			case NpcID.COMMANDER_ZILYANA:
			case NpcID.FLIGHT_KILISA:
			case NpcID.FLOCKLEADER_GEERIN:
			case NpcID.WINGMAN_SKREE:
			case NpcID.KREEARRA:
				npcContainers.removeIf(c -> c.getNpc() == npc);
				break;
		}
	}

	private void removeGameObject(GameObject gameobject)
	{
		if (gameobject == null)
		{
			return;
		}
		switch (gameobject.getId())
		{
			case Armadyl_Altar:
			case Bandos_Altar:
			case Saradomin_Altar:
			case Zamorak_Altar:
				gameObjectContainers.removeIf(c -> c.getGameObject() == gameobject);
				break;
		}
	}

	private long sleepDelay()
	{
		return calc.randomDelay(config.sleepWeightedDistribution(), config.sleepMin(), config.sleepMax(), config.sleepDeviation(), config.sleepTarget());
	}

	private void disableAllPrayer()
	{
		for (Prayer prayer : Prayer.values())
		{
			if (client.getVarbitValue(prayer.getVarbit()) == 1)
			{
				Widget widget = client.getWidget(prayer.getWidgetInfo());
				if (widget != null)
				{
					Point p = mouse.getClickPoint(widget.getBounds());
					LegacyMenuEntry toggle = new LegacyMenuEntry("", "", 1, MenuAction.CC_OP.getId(), -1, widget.getId(), false);
					Runnable runnable = () ->
					{
						menu.setEntry(toggle);
						mouse.handleMouseClick(p);
					};
					action.delayTime(sleepDelay(), runnable);
				}
			}
		}
	}

	private void infoMessage(String Message)
	{
		if (!Objects.equals(Message, ""))
		{
			log.info(Message);
		}
	}
	private void checkForPrayers()
	{
		PrayerEnum state = getStates.shouldSwitchPrayers(npcContainers);
		switch (state)
		{
			case NONE:
				break;
			case PROTECT_FROM_MISSILES:
				setPrayerActive(Prayer.PROTECT_FROM_MISSILES);
				break;
			case PROTECT_FROM_MELEE:
				setPrayerActive(Prayer.PROTECT_FROM_MELEE);
				break;
			case PROTECT_FROM_MAGIC:
				setPrayerActive(Prayer.PROTECT_FROM_MAGIC);
				break;
		}
	}
	private void setPrayerActive(Prayer prayer)
	{
		if (prayerUtils.isActive(prayer) && enableAutoPrayers)
		{
			return;
		}
		prayerUtils.toggle(prayer, sleepDelay());
	}
	//THIS FUNCTION IS TO FIND ANY NPC WITHIN THE NPC CONTAINER
	//          List<NPCContainer> found = npcContainers.stream().filter(x -> x.getID() == GENERAL_GRAARDOR.getNpcID()).collect(Collectors.toList());
	//			Set<NPCContainer> BandosFound = npcContainers.stream().filter(x -> x.getID() == NpcID.GENERAL_GRAARDOR).collect(Collectors.toSet());
	//			log.info("First Result" + BandosFound.stream().findFirst().get().getNpcName());
	//			NPCContainer.BossMonsters npc = NPCContainer.BossMonsters.of(GENERAL_GRAARDOR.getNpcID());
	//			if (npc != null)
	//			{
	//				log.info("id:" + npc.getNpcID());
	//				log.info("Name:" + npc.name());
	//				log.info("AttackStyle:" + npc.getAttackStyle());
	//				npc.getAnimations().forEach(x -> log.info(x.toString()));
	//			}
	//			if (npcContainers.stream().anyMatch(x -> x.getID() == GENERAL_GRAARDOR.getNpcID()))
	//			{
	//				log.info("we have found bandos");
	//			}
}
