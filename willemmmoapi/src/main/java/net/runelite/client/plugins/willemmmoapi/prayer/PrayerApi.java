package net.runelite.client.plugins.willemmmoapi.prayer;

import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.willemmmoapi.tasks.ActionQue;
import net.runelite.client.plugins.willemmmoapi.tasks.CreateMenuEntry;
import net.runelite.client.plugins.willemmmoapi.tasks.MenuSupport;
import net.runelite.client.plugins.willemmmoapi.tasks.MouseSupport;

@Slf4j
@Singleton
public class PrayerApi
{
	@Inject
	private Client client;
	@Inject
	private MenuSupport menuSupport;
	@Inject
	private MouseSupport mouseSupport;
	@Inject
	private ActionQue action;

	public int getPrayerPoints()
	{
		return client.getBoostedSkillLevel(Skill.PRAYER);
	}

	public boolean isPrayerActive(Prayer prayer)
	{
		return client.getVarbitValue(prayer.getVarbit()) == 1;
	}

	public void ActivatePrayer(Prayer prayer, long delay)
	{
		Widget widget = client.getWidget(prayer.getWidgetInfo());
		Point p = mouseSupport.getClickPoint(widget.getBounds());
		CreateMenuEntry menuEntry = new CreateMenuEntry("Activate", prayer.name(), 1, MenuAction.CC_OP.getId(), -1, widget.getId(), false);
		Runnable runnable = () ->
		{
			menuSupport.setEntry(menuEntry);
			mouseSupport.handleMouseClick(p);
		};
		action.delayClientTicks(delay, runnable);
	}
}
