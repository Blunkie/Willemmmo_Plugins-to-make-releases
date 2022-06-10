package net.runelite.client.plugins.scorpiaassist;


import com.google.inject.Provides;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
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
import net.runelite.client.plugins.iutils.LegacyMenuEntry;
import net.runelite.client.plugins.iutils.WalkUtils;
import net.runelite.client.plugins.iutils.game.Game;
import net.runelite.client.plugins.iutils.iUtils;
import net.runelite.client.plugins.iutils.scripts.ReflectBreakHandler;
import net.runelite.client.plugins.iutils.scripts.iScript;
import static net.runelite.client.plugins.scorpiaassist.ScorpiaAssistStates.ERROR;
import static net.runelite.client.plugins.scorpiaassist.ScorpiaAssistStates.FREEZE_INTERACTING;
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
	private WalkUtils walk;
	@Inject
	private ReflectBreakHandler breakHandler;
	//declaration of variable's used within the script
	private List<String> safeNames;
	private List<Integer> scorpiaRegion;
	private WorldArea scorpiaArea;
	private WorldPoint swLocation = new WorldPoint(3220, 10332, 0);
	private WorldPoint neLocation = new WorldPoint(3246, 10451, 0);
	private NPC Scorpia;
	private Set<NPC> Targets = new HashSet<>();
	private int timeout;//variable to handle delays of the script
	private boolean startPlugin = false;
	private boolean hasDied;
	private boolean ReceivedPet;
	private boolean isMinion;

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
		isMinion = false;
		Targets.clear();
	}

	public ScorpiaAssistPlugin()
	{
		timeout = 0;
		hasDied = false;
		safeNames = new ArrayList<>();
		scorpiaRegion = Arrays.asList(12704, 12705, 12706, 12960, 12961, 12962, 13216, 13217, 13218);
		scorpiaArea = new WorldArea(swLocation, neLocation);
		isMinion = isMinionAlive();
		//scorpia Area:
		//NW World: 3220, 10351 local: 4672,7104
		//SW World: 3220, 10332 local: 4672,4672 //local == variable
		//NE World: 3246,10351 local: 6976,7104
		//SE World: 3246,10322 local: 6976,4672
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
		if (isAtScorpia())
		{
			isMinion = isMinionAlive();
			int magiclevel = client.getBoostedSkillLevel(Skill.MAGIC);
			int scorp_x = 0;
			int scorp_y = 0;
			//NW World: 3220, 10351 local: 4672,7104
			//SW World: 3220, 10332 local: 4672,4672 //local == variable
			//NE World: 3246,10351 local: 6976,7104
			//SE World: 3246,10322 local: 6976,4672
			if (isScorpiaAlive() && Scorpia != null)
			{
				scorp_x = Scorpia.getWorldLocation().getX();
				scorp_y = Scorpia.getWorldLocation().getY();
			}
			switch (getState())
			{
				case FREEZE_INTERACTING:
					FreezeInteractingFunc();
					break;
				case WALK_SAFE:
					WorldPoint toWalkSafety = null;
					int middle_x = (neLocation.getX() + swLocation.getX()) / 2;
					int middle_y = (neLocation.getY() + swLocation.getY()) / 2;
					if (scorp_x <= middle_x)
					{
						if (scorp_y <= middle_y)//scorpia = in the southwest
						{
							toWalkSafety = new WorldPoint(swLocation.getX(), neLocation.getY(), player.getWorldLocation().getPlane());
						}
						if (scorp_y > middle_y)//scorpia = in the northwest
						{
							toWalkSafety = neLocation;
						}
					}
					if (scorp_x > middle_x)
					{
						if (scorp_y <= middle_y)//scorpia = in the southeast
						{
							toWalkSafety = swLocation;
						}
						if (scorp_y > middle_y)//scorpia = in the northeast
						{
							toWalkSafety = new WorldPoint(neLocation.getX(), swLocation.getY(), player.getWorldLocation().getPlane());//walk to southeast
						}
					}
					if (toWalkSafety != null)
					{
						walk.sceneWalk(toWalkSafety, 0, 0);
					}
			}
		}
		if (!isAtScorpia())
		{
			isMinion = false;

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
			spawnedNpc.getName().equalsIgnoreCase("Scorpia's offspring"))
		{
			Targets.add(spawnedNpc);
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
			deSpawnedNpc.getName().equalsIgnoreCase("Scorpia's offspring"))
		{
			Targets.remove(deSpawnedNpc);
		}
	}

	private boolean isAtScorpia()
	{
		return scorpiaArea.contains(client.getLocalPlayer().getWorldLocation());
	}

	private boolean isMinionAlive()
	{
		for (NPC npclist : Targets)
		{
			if (npclist.getName().equalsIgnoreCase("Scorpia's offspring"))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isScorpiaAlive()
	{
		for (NPC npclist : Targets)
		{
			if (npclist.getName().equalsIgnoreCase("Scorpia"))
			{
				return true;
			}
		}
		return false;
	}

	private NPC Scorpia_NPC()
	{
		NPC ret = null;

		for (NPC npc : Targets)
		{
			if (npc.getName().equalsIgnoreCase("Scorpia"))
			{
				ret = npc;
			}
		}
		return Objects.requireNonNull(ret);
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

	private void FreezeInteractingFunc()
	{
		Player player = client.getLocalPlayer();
		//Actor withwho = player.getInteracting();
		NPC withwho = (NPC) player.getInteracting();
		if (withwho == null)
		{
			return;
		}
		WidgetInfo FreezeSpell = SpellInfo(client.getBoostedSkillLevel(Skill.MAGIC));
		if (withwho.isMoving())
		{
			if (FreezeSpell == null)
			{
				game.sendGameMessage("Spell didnt load");
				return;
			}
			LegacyMenuEntry entry = new LegacyMenuEntry("", "", withwho.getIndex(), MenuAction.WIDGET_TARGET_ON_NPC.getId(), 0, 0, false);//<<------ withwho.getIndex() doesnt exist
			utils.oneClickCastSpell(FreezeSpell, entry, withwho.getConvexHull().getBounds(), 10);
		}
	}

	private WidgetInfo SpellInfo(int magiclevel)
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

	private ScorpiaAssistStates getState()
	{
		Player player = client.getLocalPlayer();
		if (!isAtScorpia() && breakHandler.shouldBreak(this))
		{
			return HANDLE_BREAK;
		}
		if (isAtScorpia() && isScorpiaAlive())
		{
			Scorpia = Scorpia_NPC();
			if (Scorpia == null)
			{
				return ERROR;
			}
		}
		if (isAtScorpia())
		{
			if (Scorpia != null)
			{
				WorldPoint scorpiaLocation = Scorpia.getWorldLocation();
				if (scorpiaLocation.distanceTo(player.getWorldLocation()) < 4)
				{
					return WALK_SAFE;
				}
			}
			if (player.getInteracting() == null)//player is not interacting
			{
				if (isMinionAlive())
				{
					return KILL_MINION;
				}
				if (!isMinionAlive() && isScorpiaAlive())
				{
					return KILL_SCORPIA;
				}
			}
			if (player.getInteracting() != null)//player is interacting
			{
				Actor withWho = player.getInteracting();
				if (withWho.isMoving())
				{
					return FREEZE_INTERACTING;
				}
				if (!withWho.isMoving())
				{
					if (isMinionAlive() && withWho.getName().equalsIgnoreCase("Scorpia"))
					{
						return KILL_MINION;
					}
					if (!isMinionAlive() && withWho.getName().equalsIgnoreCase("Scorpia") && isScorpiaAlive())
					{
						return KILL_SCORPIA;
					}
				}
			}
			//eating function
			//poison functon
			//restore function

		}
		return TIMEOUT;
	}
}

