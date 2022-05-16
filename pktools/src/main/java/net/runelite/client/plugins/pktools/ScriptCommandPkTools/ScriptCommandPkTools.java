package net.runelite.client.plugins.pktools.ScriptCommandPkTools;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.pktools.PkToolsConfig;
import net.runelite.client.plugins.pktools.PkToolsHotkeyListener;
import net.runelite.client.plugins.pktools.PkToolsPlugin;

public interface ScriptCommandPkTools
{
	void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager);
	//void executeApi(Client client, PrayerApi prayerApi, WillemmmoApiPlugin apiPlugin, ConfigManager configManager);

	default void clickPrayer(WidgetInfo widgetInfo, Client client, PkToolsPlugin plugin)
	{
		try
		{
			Widget prayer_widget = client.getWidget(widgetInfo);

			if (prayer_widget == null)
			{
				return;
			}

			if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
			{
				return;
			}
			click(client);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	default void EnterDoor(Client client, PkToolsPlugin plugin)
	{
		try
		{
			//LegacyMenuEntry menuEntry = new LegacyMenuEntry("Peek", "<col=ffff>Passage", 677, MenuAction.GAME_OBJECT_SECOND_OPTION, 59, 54, false);
			//plugin.entryList.add((MenuEntry) menuEntry);
			plugin.clientThread.invoke(() -> client.invokeMenuAction("Peek", "<col=ffff>Passage", 677, MenuAction.GAME_OBJECT_SECOND_OPTION.getId(), 59, 54));
			plugin.clientThread.invoke(() -> client.invokeMenuAction("Remove", "<col=ff9040>Games necklace(4)</col>", 1, MenuAction.CC_OP.getId(), -1, 25362449));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	//use this for spells that are one click cast
	default void castSpell(WidgetInfo widgetInfo, Client client, PkToolsPlugin plugin)
	{
		try
		{
			Widget spell_widget = client.getWidget(widgetInfo);

			if (spell_widget == null)
			{
				return;
			}
			LegacyMenuEntry menuEntry = new LegacyMenuEntry(spell_widget.getTargetVerb(), spell_widget.getName(), 1, MenuAction.CC_OP.getId(), spell_widget.getItemId(), spell_widget.getId(), false);
			plugin.entryList.add((MenuEntry) menuEntry);

			//plugin.entryList.add(new MenuEntry(spell_widget.getTargetVerb(), spell_widget.getName(), 1, MenuAction.CC_OP.getId(), spell_widget.getItemId(), spell_widget.getId(), false));
			click(client);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	//use this for spells that are cast on a player or item
	default void clickSpell(WidgetInfo widgetInfo, Client client, PkToolsPlugin plugin)
	{
		try
		{
			Widget spell_widget = client.getWidget(widgetInfo);

			if (spell_widget == null)
			{
				return;
			}
			LegacyMenuEntry menuEntry = new LegacyMenuEntry(spell_widget.getTargetVerb(), spell_widget.getName(), 0, MenuAction.WIDGET_TYPE_1.getId(), spell_widget.getItemId(), spell_widget.getId(), false);
			plugin.entryList.add((MenuEntry) menuEntry);
			//plugin.entryList.add(new MenuEntry(spell_widget.getTargetVerb(), spell_widget.getName(), 0, MenuAction.WIDGET_TYPE_1.getId(), spell_widget.getItemId(), spell_widget.getId(), false));
			click(client);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	default void click(Client client)
	{
		Point pos = client.getMouseCanvasPosition();

		if (client.isStretchedEnabled())
		{
			final Dimension stretched = client.getStretchedDimensions();
			final Dimension real = client.getRealDimensions();
			final double width = (stretched.width / real.getWidth());
			final double height = (stretched.height / real.getHeight());
			final Point point = new Point((int) (pos.getX() * width), (int) (pos.getY() * height));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			return;
		}

		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
	}


}

class RigourCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		if (client.getVar(Prayer.RIGOUR.getVarbit()) == 1)
		{
			return;
		}
		clickPrayer(WidgetInfo.PRAYER_RIGOUR, client, plugin);
	}
}

class EnterDoorCommand implements ScriptCommandPkTools
{
	@Override
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		EnterDoor(client, plugin);
	}
}

class AuguryCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		if (client.getVar(Prayer.AUGURY.getVarbit()) == 1)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_AUGURY, client, plugin);
	}
}

class PietyCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		if (client.getVar(Prayer.PIETY.getVarbit()) == 1)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_PIETY, client, plugin);
	}
}

class IncredibleReflexesCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		if (client.getVar(Prayer.INCREDIBLE_REFLEXES.getVarbit()) == 1)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_INCREDIBLE_REFLEXES, client, plugin);
	}
}

class UltimateStrengthCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		if (client.getVar(Prayer.ULTIMATE_STRENGTH.getVarbit()) == 1)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_ULTIMATE_STRENGTH, client, plugin);
	}
}

class SteelSkinCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		if (client.getVar(Prayer.STEEL_SKIN.getVarbit()) == 1)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_STEEL_SKIN, client, plugin);
	}
}

class EagleEyeCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		if (client.getVar(Prayer.EAGLE_EYE.getVarbit()) == 1)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_EAGLE_EYE, client, plugin);
	}
}

class MysticMightCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		if (client.getVar(Prayer.MYSTIC_MIGHT.getVarbit()) == 1)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_MYSTIC_MIGHT, client, plugin);
	}
}

class ProtectFromMagicCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		if (client.getVarbitValue(Prayer.PROTECT_FROM_MAGIC.getVarbit()) == 1)
		{
			return;
		}
		plugin.ActivatePrayer(Prayer.PROTECT_FROM_MAGIC);
		//clickPrayer(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC, client, plugin);
	}
}

class ProtectFromMissilesCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		if (client.getVarbitValue(Prayer.PROTECT_FROM_MISSILES.getVarbit()) == 1)
		{
			return;
		}
		plugin.ActivatePrayer(Prayer.PROTECT_FROM_MISSILES);
		//clickPrayer(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES, client, plugin);
	}
}

class ProtectFromMeleeCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		if (client.getVarbitValue(Prayer.PROTECT_FROM_MELEE.getVarbit()) == 1)
		{
			return;
		}
		plugin.ActivatePrayer(Prayer.PROTECT_FROM_MELEE);
		//clickPrayer(WidgetInfo.PRAYER_PROTECT_FROM_MELEE, client, plugin);
	}
}

class ProtectItemCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		if (client.getVarbitValue(Prayer.PROTECT_ITEM.getVarbit()) == 1)
		{
			return;
		}
		plugin.ActivatePrayer(Prayer.PROTECT_ITEM);
	}
}

class FreezeCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		try
		{
			int boosted_level = client.getBoostedSkillLevel(Skill.MAGIC);

			if (boosted_level >= 82 && boosted_level < 94)
			{
				clickSpell(WidgetInfo.SPELL_ICE_BLITZ, client, plugin);
			}
			else
			{
				clickSpell(WidgetInfo.SPELL_ICE_BARRAGE, client, plugin);
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class FireSurgeCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		try
		{
			int boosted_level = client.getBoostedSkillLevel(Skill.MAGIC);

			if (boosted_level >= 95)
			{
				clickSpell(WidgetInfo.SPELL_FIRE_SURGE, client, plugin);
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class VengeanceCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		try
		{
			if (client.getBoostedSkillLevel(Skill.MAGIC) < 94)
			{
				return;
			}

			castSpell(WidgetInfo.SPELL_VENGEANCE, client, plugin);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class TeleBlockCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		try
		{
			if (client.getBoostedSkillLevel(Skill.MAGIC) < 85)
			{
				return;
			}

			clickSpell(WidgetInfo.SPELL_TELE_BLOCK, client, plugin);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class EntangleCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		try
		{
			if (client.getBoostedSkillLevel(Skill.MAGIC) < 79)
			{
				return;
			}

			clickSpell(WidgetInfo.SPELL_ENTANGLE, client, plugin);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class SpecCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		try
		{
			boolean spec_enabled = (client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1);

			if (spec_enabled)
			{
				return;
			}
			LegacyMenuEntry menuEntry = new LegacyMenuEntry("Use <col=00ff00>Special Attack</col>", "", 1, MenuAction.CC_OP.getId(), -1, 38862884, false);
			plugin.entryList.add((MenuEntry) menuEntry);
			//plugin.entryList.add(new MenuEntry("Use <col=00ff00>Special Attack</col>", "", 1, MenuAction.CC_OP.getId(), -1, 38862884, false));
			click(client);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class GroupCommand implements ScriptCommandPkTools
{
	int groupNumber;

	GroupCommand(String groupNumberString)
	{
		try
		{
			this.groupNumber = Integer.parseInt(groupNumberString);
		}
		catch (Exception e)
		{
			//ignored
		}
	}

	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		try
		{
			Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

			if (inventory == null)
			{
				return;
			}

			for (WidgetItem item : inventory.getWidgetItems())
			{
				if (("Group " + groupNumber).equalsIgnoreCase(PkToolsHotkeyListener.getTag(configManager, item.getId())))
				{
					LegacyMenuEntry menuEntry = new LegacyMenuEntry("Wield", "<col=ff9040>" + item.getId(), item.getId(), MenuAction.ITEM_SECOND_OPTION.getId(), item.getIndex(), WidgetInfo.INVENTORY.getId(), false);
					plugin.entryList.add((MenuEntry) menuEntry);
					//plugin.entryList.add(new MenuEntry("Wield", "<col=ff9040>" + item.getId(), item.getId(), MenuAction.ITEM_SECOND_OPTION.getId(), item.getIndex(), WidgetInfo.INVENTORY.getId(), false));
				}
			}
			click(client);
		}
		catch (Throwable e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class ItemCommand implements ScriptCommandPkTools
{
	int itemId;

	ItemCommand(String itemIdString)
	{
		try
		{
			this.itemId = Integer.parseInt(itemIdString);
		}
		catch (Exception e)
		{
			//ignored
		}
	}

	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		try
		{
			Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

			if (inventory == null)
			{
				return;
			}

			for (WidgetItem item : inventory.getWidgetItems())
			{
				if (itemId == item.getId())
				{
					LegacyMenuEntry menuEntry = new LegacyMenuEntry("Wield", "<col=ff9040>" + item.getId(), item.getId(), MenuAction.ITEM_SECOND_OPTION.getId(), item.getIndex(), WidgetInfo.INVENTORY.getId(), false);
					plugin.entryList.add((MenuEntry) menuEntry);
					//plugin.entryList.add(new MenuEntry("Wield", "<col=ff9040>" + item.getId(), item.getId(), MenuAction.ITEM_SECOND_OPTION.getId(), item.getIndex(), WidgetInfo.INVENTORY.getId(), false));
					click(client);
					return;
				}
			}
		}
		catch (Throwable e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class ClickEnemyCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		try
		{
			if (plugin.lastEnemy != null)
			{
				//LegacyMenuEntry menuEntry = new LegacyMenuEntry(("", "", plugin.lastEnemy.getPlayerId(), client.getSpellSelected() ? MenuAction.SPELL_CAST_ON_PLAYER.getId() : MenuAction.PLAYER_SECOND_OPTION.getId(), 0, 0, false);
				//plugin.entryList.add(menuEntry);
				//plugin.entryList.add(new MenuEntry("", "", plugin.lastEnemy.getPlayerId(), client.getSpellSelected() ? MenuAction.SPELL_CAST_ON_PLAYER.getId() : MenuAction.PLAYER_SECOND_OPTION.getId(), 0, 0, false));
				click(client);
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class WaitCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		try
		{
			//Thread.sleep(config.clickDelay());
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class ExceptionCommand implements ScriptCommandPkTools
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, ConfigManager configManager)
	{
		System.out.println("Command could not be read.");
	}
}
