package net.runelite.client.plugins.willemmmoapi;

import com.google.inject.Provides;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.TileObject;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.willemmmoapi.prayer.PrayerApi;
import net.runelite.client.plugins.willemmmoapi.tasks.ActionQue;
import net.runelite.client.plugins.willemmmoapi.tasks.CreateMenuEntry;
import net.runelite.client.plugins.willemmmoapi.tasks.MenuSupport;
import net.runelite.client.plugins.willemmmoapi.tasks.MouseSupport;
import org.pf4j.Extension;

@Extension
@Slf4j
@PluginDescriptor(
	name = "Willemmmo_Api",
	description = "Willemmmo_Api settings",
	tags = {"Willemmmo", "Api", "settings"},
	enabledByDefault = true
)
@Singleton
@Getter(AccessLevel.PACKAGE)
public class WillemmmoApiPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private WillemmmoApiConfig config;
	@Inject
	private MenuSupport menuSupport;
	@Inject
	private MouseSupport mouseSupport;
	@Inject
	private ActionQue action;
	@Inject
	private PrayerApi prayerApi;

	public int cooldown = 0;

	public final static Set<TileObject> OBJECT_SET = new HashSet<>();
	public final static Set<NPC> NPC_SET = new HashSet<>();

	@Provides
	WillemmmoApiConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WillemmmoApiConfig.class);
	}

	@Override
	protected void startUp()
	{
	}

	@Override
	protected void shutDown()
	{

	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		if (cooldown == 0)
		{
			action.onClientTick(event);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) //use this to preform every tick
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		if (cooldown > 0)
		{
			cooldown--;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() != GameState.LOGGED_IN && gameStateChanged.getGameState() != GameState.CONNECTION_LOST)
		{
			OBJECT_SET.clear();
			NPC_SET.clear();
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		OBJECT_SET.add(event.getGameObject());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		OBJECT_SET.remove(event.getGameObject());
	}

	@Subscribe
	public void npcSpawned(NpcSpawned event)
	{
		NPC_SET.add(event.getNpc());
	}

	@Subscribe
	public void npcDespawned(NpcDespawned event)
	{
		NPC_SET.remove(event.getNpc());
	}

	@Subscribe
	public void npcChanged(NpcChanged event)
	{
		NPC_SET.remove(event.getNpc());
		NPC_SET.add(event.getNpc());
	}

	@Subscribe
	private void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (menuSupport.entry != null)
		{
			client.createMenuEntry(menuSupport.entry.getOption(),
				menuSupport.entry.getTarget(),
				menuSupport.entry.getOpcode(),
				menuSupport.entry.getIdentifier(),
				menuSupport.entry.getParam1(),
				menuSupport.entry.getParam1(),
				menuSupport.entry.isForceLeftClick());
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getMenuAction() == MenuAction.CC_OP && event.getId() == WidgetInfo.WORLD_SWITCHER_LIST.getId())
		{
			menuSupport.entry = null;
			return;
		}
		else
		{
			menuAction(event, menuSupport.entry.getOption(), menuSupport.entry.getTarget(), menuSupport.entry.getIdentifier(), MenuAction.of(menuSupport.entry.getOpcode()), menuSupport.entry.getParam0(), menuSupport.entry.getParam1());

		}
		menuSupport.entry = null;
	}

	public void menuAction(MenuOptionClicked menuOptionClicked, String option, String target, int identifier, MenuAction menuAction, int param0, int param1)
	{
		menuOptionClicked.setMenuOption(option);
		menuOptionClicked.setMenuTarget(target);
		menuOptionClicked.setId(identifier);
		menuOptionClicked.setMenuAction(menuAction);
		menuOptionClicked.setParam0(param0);
		menuOptionClicked.setParam1(param1);
	}

	public void doActionClientTick(CreateMenuEntry entry, Rectangle rect, long delay)
	{
		Point point = mouseSupport.getClickPoint(rect);
		doActionClientTick(entry, point, delay);
	}

	public void doActionClientTick(CreateMenuEntry entry, Point point, long delay)
	{
		Runnable runnable = () -> {
			menuSupport.createEntry(entry);
			mouseSupport.handleMouseClick(point);
		};
		action.delayClientTicks(delay, runnable);
	}

	public void doActionMsTime(CreateMenuEntry entry, Rectangle rect, long delay)
	{
		Point point = mouseSupport.getClickPoint(rect);
		doActionMsTime(entry, point, delay);
	}

	public void doActionMsTime(CreateMenuEntry entry, Point point, long delay)
	{
		Runnable runnable = () -> {
			menuSupport.createEntry(entry);
			mouseSupport.handleMouseClick(point);
		};
		action.delayTime(delay, runnable);
	}

	public void doGameObjectAction(GameObject object, int menuOpcodeID, long delay)
	{
		if (object == null || object.getConvexHull() == null)
		{
			return;
		}
		log.info("Making Menu Entry");
		Rectangle rectangle = (object.getConvexHull().getBounds() != null) ? object.getConvexHull().getBounds() :
			new Rectangle(client.getCenterX() - 50, client.getCenterY() - 50, 100, 100);
		CreateMenuEntry entry = new CreateMenuEntry("", object.getName(), object.getId(), menuOpcodeID, object.getSceneMinLocation().getX(), object.getSceneMinLocation().getY(), true);
		doActionMsTime(entry, rectangle, delay);
	}

	public void ActivatePrayer(Prayer prayer)
	{
		prayerApi.ActivatePrayer(prayer, 0);
	}
}