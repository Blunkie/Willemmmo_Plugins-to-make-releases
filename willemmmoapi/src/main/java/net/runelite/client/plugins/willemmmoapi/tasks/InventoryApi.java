package net.runelite.client.plugins.willemmmoapi.tasks;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.willemmmoapi.WillemmmoApiPlugin;

@Slf4j
@Singleton
public class InventoryApi
{
	@Inject
	private Client client;

	@Inject
	private MenuSupport menuSupport;

	@Inject
	private InventoryAssistant inventoryAssistant;

	@Inject
	private WillemmmoApiPlugin apiPlugin;

	public void interactWithItem(int itemID, long delay, String... option)
	{
		interactWithItem(itemID, false, delay, option);
	}

	public void interactWithItem(int itemID, boolean forceLeftClick, long delay, String... option)
	{
		interactWithItem(new int[]{itemID}, forceLeftClick, delay, option);
	}

	public void interactWithItem(int[] itemID, long delay, String... option)
	{
		interactWithItem(itemID, false, delay, option);
	}

	public void interactWithItem(int[] itemID, boolean forceLeftClick, long delay, String... option)
	{
		List<Integer> boxedIds = Arrays.stream(itemID).boxed().collect(Collectors.toList());
		CreateMenuEntry entry = inventoryAssistant.getLegacyMenuEntry(boxedIds, Arrays.asList(option), forceLeftClick);
		if (entry != null)
		{
			WidgetItem wi = inventoryAssistant.getWidgetItem(boxedIds);
			if (wi != null)
			{
				apiPlugin.doActionMsTime(entry, wi.getCanvasBounds(), delay);
			}
		}
	}

	public WidgetItem getWidgetItem(int id)
	{
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
		if (inventoryWidget != null)
		{
			Collection<WidgetItem> items = inventoryAssistant.getWidgetItems();
			for (WidgetItem item : items)
			{
				if (item.getId() == id)
				{
					return item;
				}
			}
		}
		return null;
	}

	public WidgetItem getWidgetItem(Collection<Integer> ids)
	{
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
		if (inventoryWidget != null)
		{
			Collection<WidgetItem> items = inventoryAssistant.getWidgetItems();
			for (WidgetItem item : items)
			{
				if (ids.contains(item.getId()))
				{
					return item;
				}
			}
		}
		return null;
	}
}
