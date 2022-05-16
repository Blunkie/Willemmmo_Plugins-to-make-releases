package net.runelite.client.plugins.willemmmoapi;

import com.google.inject.Provides;
import java.awt.Rectangle;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
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
		action.onClientTick(event);
	}

	@Subscribe
	public void onGameTick(GameTick event) //use this to preform every tick
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		//if (client.getVarbitValue(Prayer.PROTECT_ITEM.getVarbit()) == 0)
		//{
			//log.info("Enabling Prayer. Value now = " + Prayer.PROTECT_ITEM.getVarbit());
			//prayerApi.ActivatePrayer(Prayer.PROTECT_ITEM, 0);
		//}
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
//        menuOptionClicked.setActionParam(param0);
//        menuOptionClicked.setWidgetId(param1);
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

	public void ActivatePrayer(Prayer prayer)
	{
		prayerApi.ActivatePrayer(prayer, 0);
	}
}