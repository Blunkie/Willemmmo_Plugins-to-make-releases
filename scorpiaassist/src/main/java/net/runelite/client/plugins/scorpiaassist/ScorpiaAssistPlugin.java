package net.runelite.client.plugins.scorpiaassist;


import com.google.inject.Provides;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.iutils.CalculationUtils;
import net.runelite.client.plugins.iutils.LegacyMenuEntry;
import net.runelite.client.plugins.iutils.NPCUtils;
import net.runelite.client.plugins.iutils.WalkUtils;
import net.runelite.client.plugins.iutils.game.Game;
import net.runelite.client.plugins.iutils.iUtils;
import net.runelite.client.plugins.iutils.scripts.ReflectBreakHandler;
import net.runelite.client.plugins.iutils.scripts.iScript;
import static net.runelite.client.plugins.scorpiaassist.ScorpiaAssistStates.ERROR;
import static net.runelite.client.plugins.scorpiaassist.ScorpiaAssistStates.FREEZE_SCORPIA;
import static net.runelite.client.plugins.scorpiaassist.ScorpiaAssistStates.HANDLE_BREAK;
import static net.runelite.client.plugins.scorpiaassist.ScorpiaAssistStates.KILL_MINION;
import static net.runelite.client.plugins.scorpiaassist.ScorpiaAssistStates.KILL_SCORPIA;
import static net.runelite.client.plugins.scorpiaassist.ScorpiaAssistStates.TIMEOUT;
import static net.runelite.client.plugins.scorpiaassist.ScorpiaAssistStates.WALK_SAFE;
import org.pf4j.Extension;

@Extension
@Slf4j
@PluginDependency(iUtils.class)
@PluginDescriptor(
	name = "Scorpia Assist",
	description = "Plugin to altscape Scorpia",
	tags = {"Scorpia", "Assist", "Willemmmo"}
)
public class ScorpiaAssistPlugin extends iScript
{
	@Inject
	private ScorpiaAssistConfig config;

	@Inject
	private Client client;

	@Inject
	private iUtils utils;

	@Inject
	private Game game;

	@Inject
	private NPCUtils npcUtils;

	@Inject
	private WalkUtils walk;

	@Inject
	private CalculationUtils calc;
	@Inject
	private ReflectBreakHandler breakHandler;
	//declaration of variable's used within the script
	private List<String> safeNames;
	private List<Integer> scorpiaRegion;
	private WorldArea scorpiaArea;
	private WorldPoint swLocation = new WorldPoint(3219, 10331, 0);
	private WorldPoint neLocation = new WorldPoint(3247, 10352, 0);
	private WorldPoint seLocation = new WorldPoint(3247, 10331, 0);
	private WorldPoint nwLocation = new WorldPoint(3219, 10352, 0);
	private NPC Scorpia;
	private Set<NPC> Targets = new HashSet<>();
	private int timeout;//variable to handle delays of the script
	private long sleepLength;
	private boolean startPlugin = false;
	private boolean hasDied;
	private boolean ReceivedPet;

	@Override
	protected void onStart()
	{
		log.info("ScorpiaAssist Started");
		breakHandler.startPlugin(this);
	}

	@Override
	protected void onStop()
	{
		log.info("ScorpiaAssist Stopped");
		breakHandler.stopPlugin(this);
		timeout = 0;
		hasDied = false;
		startPlugin = false;
		Targets.clear();
	}

	public ScorpiaAssistPlugin()
	{
		timeout = 0;
		hasDied = false;
		safeNames = new ArrayList<>();
		scorpiaRegion = Arrays.asList(12704, 12705, 12706, 12960, 12961, 12962, 13216, 13217, 13218);
		scorpiaArea = new WorldArea(swLocation, neLocation);
	}

	@Provides
	ScorpiaAssistConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ScorpiaAssistConfig.class);
	}

	@Subscribe
	public void onGameTick(GameTick event) // this function handles all the code
	{
		if (!startPlugin || breakHandler.isBreakActive(this))
		{
			return;
		}
		final Player player = client.getLocalPlayer();
		if (config.debug())
		{
			game.sendGameMessage("State : " + getState());
		}
		if (ReceivedPet)
		{
			if (isAtScorpia())
			{
				game.sendGameMessage("YOU GOT PET. LOGOUT");
				game.notify();
				return;
			}
		}
		if (hasDied)
		{
			logoutFunction();
			return;
		}
		switch (getState())
		{
			case HANDLE_BREAK:
				breakHandler.startBreak(this);
				break;
			case ERROR:
				game.sendGameMessage("THERE IS SOMETHING WRONG...");
				game.sendGameMessage("Stopping now...");
				stop();
				break;
		}
		if (timeout > 0)
		{
			switch (getState())
			{
				case WALK_SAFE:
					timeout = 0;
			}
			timeout--;
			return;
		}
		if (isAtScorpia())
		{
			int magiclevel = client.getBoostedSkillLevel(Skill.MAGIC);
			int scorp_x = 0;
			int scorp_y = 0;
			if (isScorpiaAlive() && Scorpia != null)
			{
				scorp_x = Scorpia.getWorldLocation().getX();
				scorp_y = Scorpia.getWorldLocation().getY();
			}
			switch (getState())
			{
				case WALK_SAFE:
					if (!player.isMoving() && isInDangerZone(Scorpia))
					{
						int middle_x = (neLocation.getX() + swLocation.getX()) / 2;
						int middle_y = (neLocation.getY() + swLocation.getY()) / 2;
						if (scorp_x <= middle_x)
						{
							if (scorp_y <= middle_y && !player.isMoving())//scorpia = in the southwest
							{
								log.info("Setting Waypoint to : NORTHWEST");
								walk.sceneWalk(nwLocation, 0, 0);
								break;
							}
							if (scorp_y > middle_y && !player.isMoving())//scorpia = in the northwest
							{
								log.info("Setting Waypoint to : NORTHEAST");
								walk.sceneWalk(neLocation, 0, 0);
								break;
							}
						}
						if (scorp_x > middle_x)
						{
							if (scorp_y <= middle_y && !player.isMoving())//scorpia = in the southeast
							{
								log.info("Setting Waypoint to : SOUTHWEST");
								walk.sceneWalk(swLocation, 0, 0);
								break;
							}
							if (scorp_y > middle_y && !player.isMoving())//scorpia = in the northeast
							{
								log.info("Setting Waypoint to : SOUTHEAST");
								walk.sceneWalk(seLocation, 0, 0);
								break;
							}
						}
					}
				case FREEZE_SCORPIA:
					if (!isInDangerZone(Scorpia))
					{
						useSpell(Scorpia, FreezeSpellInfo(magiclevel));
					}
					timeout += 4;
					break;
				case KILL_MINION:
					NPC minion = npcUtils.findNearestNpc(NpcID.SCORPIAS_GUARDIAN);
					//TODO add function to use trident
					//TODO add function to choose spell or trident
					if (player.getAnimation() == 7856)
					{
						timeout += 3;
						break;
					}
					if (!isInDangerZone(Scorpia))
					{
						useSpell(minion, KillSpellInfo(magiclevel));

					}
					timeout += 4;
					break;
				case KILL_SCORPIA:
					if (player.getAnimation() == 7856)
					{
						timeout += 3;
						break;
					}
					if (!isInDangerZone(Scorpia))
					{
						useSpell(Scorpia, KillSpellInfo(magiclevel));
					}
					timeout += 4;
					break;
				case TIMEOUT:
					break;
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING)
		{
			timeout += 3;
		}
		if (event.getGameState() == GameState.LOGIN_SCREEN && hasDied)
		{
			stop();
		}
	}

	@Override
	protected void loop()
	{
		game.tick();
	}

	@Subscribe
	private void onChatMessage(ChatMessage eventmessage)
	{
		if (!startPlugin || eventmessage.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}
		String message = eventmessage.getMessage();
		String deathMessage = "Oh dear, you are dead!";
		if (message.contains("You have a funny feeling"))
		{
			ReceivedPet = true;
			return;
		}
		if (message.equalsIgnoreCase(deathMessage))
		{
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			Date date = new Date();
			game.sendGameMessage("Died at: " + format.format(date));
			hasDied = true;
		}
	}

	@Subscribe
	private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked)
	{
		if (!configButtonClicked.getGroup().equalsIgnoreCase("ScorpiaAssistConfig"))
		{
			return;
		}
		if (configButtonClicked.getKey().equalsIgnoreCase("startPlugin"))
		{
			if (!startPlugin)
			{
				game.sendGameMessage("Scorpia Assist started. Please wait a moment...");
				startPlugin = true;
				safeNames = Arrays.asList(config.safePlayers().toLowerCase().split("\\s*,\\s*"));
				start();
			}
			else
			{
				game.sendGameMessage("Scorpia Assist stopped");
				startPlugin = false;
				stop();
			}
		}
	}

	@Subscribe
	private void onAnimationChanged(AnimationChanged event)
	{
		if (!startPlugin || event == null)
		{
			return;
		}
		final Player player = client.getLocalPlayer();
		final WorldPoint playerLocation = player.getWorldLocation();
		final LocalPoint playerLocalPoint = player.getLocalLocation();
		final Actor actor = event.getActor();

		if (actor == player)//ensures the animation is on own player
		{

		}
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned event)
	{
		if (event == null)
		{
			return;
		}
		NPC spawnedNpc = event.getNpc();
		if (spawnedNpc == null)
		{
			return;
		}
		if (spawnedNpc.getName().equalsIgnoreCase("Scorpia") ||
			spawnedNpc.getName().equalsIgnoreCase("Scorpia's guardian"))
		{
			Targets.add(spawnedNpc);
		}
		else
		{
			return;
		}
	}

	private void onNpcDespawned(NpcDespawned event)
	{
		if (event == null)
		{
			return;
		}
		NPC deSpawnedNpc = event.getNpc();
		if (deSpawnedNpc.getName().equalsIgnoreCase("Scorpia") ||
			deSpawnedNpc.getName().equalsIgnoreCase("Scorpia's guardian") && event.getNpc().isDead())
		{
			Targets.remove(event.getNpc());
		}
	}

	private boolean isAtScorpia()
	{
		return scorpiaArea.contains(client.getLocalPlayer().getWorldLocation());
	}
	private boolean isMinionAlive()
	{
		NPC minionHere = npcUtils.findNearestNpc(NpcID.SCORPIAS_GUARDIAN);
		return minionHere != null;
		/*for (NPC npclist : Targets)
		{
			if (npclist.getName().equalsIgnoreCase("Scorpia's guardian"))
			{
				return true;
			}
		}
		return
			false;*/
	}
	private boolean isScorpiaAlive()
	{
		NPC scorpiaHere = npcUtils.findNearestNpc(NpcID.SCORPIA);
		return scorpiaHere != null;
		/*if (Targets.isEmpty())
		{
			return false;
		}
		if (!Targets.isEmpty())
		{
			for (NPC npclist : Targets)
			{
				if (npclist.getName().equalsIgnoreCase("Scorpia"))
				{
					return true;
				}
				return false;
			}
		}
		return false;*/
	}
	private NPC Scorpia_NPC()
	{
		return npcUtils.findNearestNpc(NpcID.SCORPIA);
		/*if (!Targets.isEmpty())
		{
			for (NPC npc : Targets)
			{
				if (npc.getName().equalsIgnoreCase("Scorpia") && npc != null)
				{
					return npc;
				}
			}
		}
		return null;*/
	}
	private void logoutFunction()
	{
		if (game.widget(182, 8) != null)
		{
			game.widget(182, 8).interact("Logout");
		}
		else
		{
			game.widget(WidgetInfo.WORLD_SWITCHER_LOGOUT_BUTTON).interact("Logout");
		}
	}

	private void useSpell(NPC killnpc, WidgetInfo widgetInfo)
	{
		if (widgetInfo == null)
		{
			game.sendGameMessage("fatal error: cannot cast spell");
		}
		if (killnpc != null)
		{
			LegacyMenuEntry entry = new LegacyMenuEntry("", "", killnpc.getIndex(), MenuAction.WIDGET_TARGET_ON_NPC.getId(), 0, 0, false);
			utils.oneClickCastSpell(widgetInfo, entry, killnpc.getConvexHull().getBounds(), sleepDelay());
		}
		return;
	}

	private WidgetInfo FreezeSpellInfo(int magiclevel)
	{
		WidgetInfo magespell = null;
		if (magiclevel >= 79)
		{
			magespell = WidgetInfo.SPELL_ENTANGLE;
		}
		if (magiclevel < 79 && magiclevel >= 50)
		{
			magespell = WidgetInfo.SPELL_SNARE;
		}
		if (magiclevel < 50 && magiclevel >= 20)
		{
			magespell = WidgetInfo.SPELL_BIND;
		}
		return magespell;
	}

	private WidgetInfo KillSpellInfo(int magiclevel)
	{
		WidgetInfo magicspel = null;
		if (magiclevel >= 81)
		{
			magicspel = WidgetInfo.SPELL_WIND_SURGE;
		}
		return magicspel;
	}

	private ScorpiaAssistStates getState()
	{
		Player player = client.getLocalPlayer();
		if (!isAtScorpia() && breakHandler.shouldBreak(this))
		{
			return HANDLE_BREAK;
		}
		if (isAtScorpia() && isScorpiaAlive())
		{
			if (!Targets.isEmpty())
			{
				Scorpia = Scorpia_NPC();
			}
			if (Scorpia == null)
			{
				return ERROR;
			}
			if (isInDangerZone(Scorpia))
			{
				return WALK_SAFE;
			}
			if (Scorpia.isMoving() && !isInDangerZone(Scorpia))
			{
				return FREEZE_SCORPIA;
			}
			if (isMinionAlive() && !isInDangerZone(Scorpia))
			{
				return KILL_MINION;
			}
			if (!isMinionAlive() && !Scorpia.isMoving() && !player.isMoving() && !isInDangerZone(Scorpia))
			{
				return KILL_SCORPIA;
			}
		}
		return TIMEOUT;
	}

	private boolean isInDangerZone(NPC npcDangerZone)
	{
		Player player = client.getLocalPlayer();
		if (npcDangerZone == null)
		{
			return false;
		}
		WorldPoint scorpiaLocation = npcDangerZone.getWorldLocation();
		int safeDistance = 5;
		int positiveDistance = 10;
		int scorpia_Start_x = scorpiaLocation.getX() - safeDistance;
		int scorpia_Start_y = scorpiaLocation.getY() - safeDistance;
		int scorpia_End_x = scorpiaLocation.getX() + positiveDistance;
		int scorpia_End_y = scorpiaLocation.getY() + positiveDistance;
		WorldPoint scorpia_sw = new WorldPoint(scorpia_Start_x, scorpia_Start_y, 0);
		WorldPoint scorpia_ne = new WorldPoint(scorpia_End_x, scorpia_End_y, 0);
		WorldArea ScorpiaDangerZone = new WorldArea(scorpia_sw, scorpia_ne);
			/*log.info("Scorpia_x : " + scorpiaLocation.getX());
			log.info("Scorpia_y : " + scorpiaLocation.getY());
			log.info("scorpia_Start_x : " + scorpia_Start_x);
			log.info("scorpia_Start_y : " + scorpia_Start_y);
			log.info("scorpia_End_x : " + scorpia_End_x);
			log.info("scorpia_End_y : " + scorpia_End_y);
			log.info("ScorpiaDangerZone_x : " + ScorpiaDangerZone.getX());
			log.info("ScorpiaDangerZone_y : " + ScorpiaDangerZone.getY());*/
		return ScorpiaDangerZone.contains(player.getWorldLocation());
	}

	private long sleepDelay()
	{
		sleepLength = calc.randomDelay(config.sleepWeightedDistribution(), config.sleepMin(), config.sleepMax(), config.sleepDeviation(), config.sleepTarget());
		return sleepLength;
	}
}

