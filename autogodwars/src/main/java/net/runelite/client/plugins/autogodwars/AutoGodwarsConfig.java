package net.runelite.client.plugins.autogodwars;

import java.awt.Color;
import java.awt.event.KeyEvent;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ConfigTitle;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup("AutoGodwarsConfig")
public interface AutoGodwarsConfig extends Config
{
	/**
	 * -------------------------------------------------------------TITLE DEFINE
	 */
	@ConfigTitle(
		keyName = "bosses",
		position = 0,
		name = "          Boss Setup",
		description = ""
	)
	String bosses = "          Boss Setup";
	@ConfigTitle(
		keyName = "generalSettings",
		position = 5,
		name = "          General Settings Plugin",
		description = ""
	)
	String generalSettings = "          General Settings Plugin";
	/**
	 * -------------------------------------------------------------SECTION DEFINE
	 */
	@ConfigSection(
		keyName = "arma",
		position = 1,
		name = "Arma",
		description = "",
		closedByDefault = true,
		section = bosses
	)
	String arma = "Arma";
	@ConfigSection(
		keyName = "bandos",
		position = 2,
		name = "Bandos",
		description = "",
		closedByDefault = true
	)
	String bandos = "Bandos";
	@ConfigSection(
		keyName = "sara",
		position = 3,
		name = "Sara",
		description = "",
		closedByDefault = true

	)
	String sara = "Sara";
	@ConfigSection(
		keyName = "zammy",
		position = 4,
		name = "Zammy",
		description = "",
		closedByDefault = true
	)
	String zammy = "Zammy";
	@ConfigSection(
		name = "Sleep Delays",
		description = "",
		position = 5,
		keyName = "sleepDelays",
		closedByDefault = true
	)
	String sleepDelays = "Sleep Delays";
	@ConfigSection(
		keyName = "prayers",
		position = 6,
		name = "Prayers",
		description = "",
		closedByDefault = true
	)
	String prayers = "Prayers";
	@ConfigSection(
		keyName = "autoEatFoodSection",
		position = 7,
		name = "Auto Eat Food",
		description = "",
		closedByDefault = true
	)
	String autoEatFoodSection = "Auto Eat Food";
	@ConfigSection(
		keyName = "autoDrinkPrayerSection",
		position = 8,
		name = "Auto Drink Prayer",
		description = "",
		closedByDefault = true
	)
	String autoDrinkPrayerSection = "Auto Drink Prayer";
	@ConfigSection(
		keyName = "consumables",
		position = 9,
		name = "Other Consumables",
		description = "",
		closedByDefault = true
	)
	String consumables = "Other Consumables";
	@ConfigSection(
		keyName = "magic",
		position = 10,
		name = "Magic",
		description = "",
		closedByDefault = true
	)
	String magic = "Magic";

	@ConfigSection(
		keyName = "misc",
		position = 11,
		name = "Misc Settings",
		description = "",
		closedByDefault = true
	)
	String misc = "Misc Settings";
	@ConfigSection(
		keyName = "debug",
		position = 12,
		name = "Debugging",
		description = "",
		closedByDefault = true
	)
	String debug = "Debugging";

	/**
	 * -------------------------------------------------------------ARMADYL SECTION
	 */
	@ConfigItem(
		keyName = "enableArma",
		name = "Enable Armadyl",
		description = "Enable using plugin on Armadyl",
		section = arma,
		position = 1
	)
	default boolean enableArma()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableAutoPrayArma",
		name = "Auto Pray Armadyl",
		description = "Do you want to Auto Pray in Armadyl room",
		section = arma,
		position = 2,
		hidden = true,
		unhide = "enableArma"
	)
	default boolean enableAutoPrayArma()
	{
		return true;
	}

	/**
	 * -------------------------------------------------------------BANDOS SECTION
	 */
	@ConfigItem(
		keyName = "enableBandos",
		name = "Enable Bandos",
		description = "Enable using plugin on Bandos",
		section = bandos,
		position = 1
	)
	default boolean enableBandos()
	{
		return false;
	}
	@ConfigItem(
		keyName = "enableAutoPrayBandos",
		name = "Auto Pray Bandos",
		description = "Do you want to Auto Pray in Bandos room",
		section = bandos,
		position = 2,
		hidden = true,
		unhide = "enableBandos"
	)
	default boolean enableAutoPrayBandos()
	{
		return true;
	}

	@ConfigItem(
		keyName = "prioritiseRange",
		name = "Prioritise Range",
		description = "Prioritise Range prayer on Bandos when not Tanking",
		section = bandos,
		position = 3,
		hidden = true,
		unhide = "enableBandos"
	)
	default boolean prioritiseRange()
	{
		return true;
	}

	@ConfigItem(
		keyName = "boostPrayerBandos",
		name = "Boost Prayer",
		description = "Choose boost prayer. Enable switch in General Prayer Tab",
		section = bandos,
		position = 4,
		hidden = true,
		unhide = "enableBandos"
	)
	default AutoGodwarsEnum.BoostingPrayers boostPrayerBandos()
	{
		return AutoGodwarsEnum.BoostingPrayers.PIETY;
	}

	@ConfigItem(
		keyName = "bandosRole",
		name = "Bandos Role",
		description = "Select what Role you like to do",
		section = bandos,
		position = 5,
		hidden = true,
		unhide = "enableBandos"
	)
	default AutoGodwarsEnum.BandosRole bandosRole()
	{
		return AutoGodwarsEnum.BandosRole.TANK;
	}

	@ConfigItem(
		keyName = "focusBandos",
		name = "Who to focus on",
		description = "What NPC we focus on",
		section = bandos,
		position = 6,
		hidden = true,
		unhide = "enableBandos"
	)
	default AutoGodwarsEnum.BandosFocus focusBandos()
	{
		return AutoGodwarsEnum.BandosFocus.GENERAL_GRAARDOR;
	}

	/**
	 * -------------------------------------------------------------SARADOMIN SECTION
	 */
	@ConfigItem(
		keyName = "enableSara",
		name = "Enable Saradomin",
		description = "Enable using plugin on Saradomin",
		section = sara,
		position = 1
	)
	default boolean enableSara()
	{
		return true;
	}
	@ConfigItem(
		keyName = "enableAutoPraySara",
		name = "Auto Pray Saradomin",
		description = "Do you want to Auto Pray in Saradomin room",
		section = sara,
		position = 2,
		hidden = true,
		unhide = "enableSara"
	)
	default boolean enableAutoPraySara()
	{
		return true;
	}

	/**
	 * -------------------------------------------------------------ZAMORAK SECTION
	 */
	@ConfigItem(
		keyName = "enableZammy",
		name = "Enable Zamorak",
		description = "Enable using plugin on Zamorak",
		section = zammy,
		position = 1
	)
	default boolean enableZammy()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableAutoPrayZammy",
		name = "Auto Pray Zamorak",
		description = "Do you want to Auto Pray in Zamorak room",
		section = zammy,
		position = 2,
		hidden = true,
		unhide = "enableZammy"
	)
	default boolean enableAutoPrayZammy()
	{
		return true;
	}
	@ConfigItem(
		keyName = "prioritiseMage",
		name = "Prioritise Mage",
		description = "Prioritise Mage prayer on Zamorak when not Tanking",
		section = zammy,
		position = 3,
		hidden = true,
		unhide = "enableZammy"
	)
	default boolean prioritiseMage()
	{
		return true;
	}

	/**
	 * -------------------------------------------------------------SLEEP DELAYS
	 */
	@ConfigItem(
		keyName = "sleepMin",
		name = "Sleep Min",
		description = "",
		position = 1,
		section = sleepDelays
	)
	@Range(
		min = 1,
		max = 550
	)
	default int sleepMin()
	{
		return 60;
	}

	@Range(
		min = 1,
		max = 550
	)
	@ConfigItem(
		keyName = "sleepMax",
		name = "Sleep Max",
		description = "",
		position = 2,
		section = sleepDelays
	)
	default int sleepMax()
	{
		return 200;
	}

	@Range(
		min = 1,
		max = 550
	)
	@ConfigItem(
		keyName = "sleepTarget",
		name = "Sleep Target",
		description = "",
		position = 3,
		section = sleepDelays
	)
	default int sleepTarget()
	{
		return 100;
	}

	@Range(
		min = 1,
		max = 100
	)
	@ConfigItem(
		keyName = "sleepDeviation",
		name = "Sleep Deviation",
		description = "",
		position = 4,
		section = sleepDelays
	)
	default int sleepDeviation()
	{
		return 10;
	}

	@ConfigItem(
		keyName = "sleepWeightedDistribution",
		name = "Sleep Weighted Distribution",
		description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
		position = 5,
		section = sleepDelays
	)
	default boolean sleepWeightedDistribution()
	{
		return false;
	}

	/**
	 * -------------------------------------------------------------PRAYER SECTION
	 */
	@ConfigItem(
		position = 1,
		keyName = "switchPrayerKey",
		name = "Enable / Disable Prayer",
		description = "Key to enable or disable autoprayer",
		section = prayers
	)
	default Keybind switchPrayerKey()
	{
		return new Keybind(KeyEvent.VK_DEAD_GRAVE, 0);
	}

	@ConfigItem(
		position = 2,
		keyName = "enableColor",
		name = "Enable Color",
		description = "Color to display enable message",
		section = prayers
	)
	default Color enableColor()
	{
		return Color.decode("#004300");
	}

	@ConfigItem(
		position = 3,
		keyName = "disableColor",
		name = "Disable Color",
		description = "Color to display disable message",
		section = prayers
	)
	default Color disableColor()
	{
		return Color.decode("#FF6A00");
	}

	@ConfigItem(
		position = 4,
		keyName = "ignoreNonAttacking",
		name = "Ignore Non-Attacking",
		description = "Ignore monsters that are not attacking you",
		section = prayers
	)
	default boolean ignoreNonAttacking()
	{
		return false;
	}

	/**
	 * -------------------------------------------------------------AUTO EAT FOOD SECTION
	 */
	@ConfigTitle(
		keyName = "eatTitle",
		name = "Auto Eat Settings",
		description = "",
		section = autoEatFoodSection,
		position = 0
	)
	String eatTitle = "Auto Eat Settings";
	@ConfigItem(
		keyName = "enableAutoEat",
		name = "Enable Auto Eat",
		description = "Do you want to Auto Eat?",
		section = autoEatFoodSection,
		position = 1
	)
	default boolean enableAutoEat()
	{
		return true;
	}

	@ConfigItem(
		keyName = "eatBelow",
		name = "Eat Below",
		description = "Below what HP you want to Auto Eat?",
		section = autoEatFoodSection,
		position = 2,
		hidden = true,
		unhide = "enableAutoEat"
	)
	@Range(
		min = 1,
		max = 99
	)
	default int eatBelow()
	{
		return 45;
	}

	@ConfigItem(
		keyName = "eatDeviation",
		name = "Eat Deviation",
		description = "% of health to variate at Eating Food",
		section = autoEatFoodSection,
		position = 3,
		hidden = true,
		unhide = "enableAutoEat"
	)
	@Range(
		min = 1,
		max = 20
	)
	default int eatDeviation()
	{
		return 5;
	}

	@ConfigItem(
		keyName = "useBrews",
		name = "Use Brews",
		description = "Do you want to use Brews?",
		section = autoEatFoodSection,
		position = 4,
		hidden = true,
		unhide = "enableAutoEat"
	)
	default boolean useBrews()
	{
		return true;
	}

	@ConfigItem(
		keyName = "restoreAfter",
		name = "Restore after Brew",
		description = "Do you want to restore after Brews?",
		section = autoEatFoodSection,
		position = 5,
		hidden = true,
		unhide = "enableAutoEat"
	)
	default boolean restoreAfter()
	{
		return true;
	}

	@ConfigItem(
		keyName = "prioritiseHardFood",
		name = "Prioritise Hard Food",
		description = "Enabling this Prioritises hard food over Brews",
		section = autoEatFoodSection,
		position = 6,
		hidden = true,
		unhide = "enableAutoEat"
	)
	default boolean prioritiseHardFood()
	{
		return true;
	}
	/**
	 * -------------------------------------------------------------AUTO DRINK PRAYER SECTION
	 */
	@ConfigTitle(
		keyName = "prayTitle",
		name = "Auto Drink Prayer Settings",
		description = "",
		section = autoDrinkPrayerSection,
		position = 0
	)
	String prayTitle = "Auto Drink Prayer Settings";

	@ConfigItem(
		keyName = "restorePrayer",
		name = "Restore Prayer",
		description = "Do you want to restore Prayer",
		position = 1,
		section = autoDrinkPrayerSection
	)
	default boolean restorePrayer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "restorePrayerBelow",
		name = "Restore Below",
		description = "Below what prayer points to restore",
		section = autoDrinkPrayerSection,
		position = 2,
		hidden = true,
		unhide = "restorePrayer"
	)
	@Range(
		min = 1,
		max = 99
	)
	default int restorePrayerBelow()
	{
		return 20;
	}

	@ConfigItem(
		keyName = "prayerDeviation",
		name = "Prayer Deviation",
		description = "% of prayer to variate at Prayer",
		section = autoDrinkPrayerSection,
		position = 3,
		hidden = true,
		unhide = "restorePrayer"
	)
	@Range(
		min = 1,
		max = 20
	)
	default int prayerDeviation()
	{
		return 5;
	}
	/**
	 * -------------------------------------------------------------CONSUMABLES SECTION
	 */
	@ConfigItem(
		keyName = "cureVenomPoison",
		name = "Cure Venom/Poison",
		description = "Do you want to auto cure Venom or Poison",
		section = consumables,
		position = 0
	)
	default boolean cureVenomPoison()
	{
		return true;
	}
	@ConfigItem(
		keyName = "poisonCheckPrayer",
		name = "Use Sanfew Potion",
		description = "When Poisoned and need prayer, this will Prioritise",
		section = consumables,
		position = 1,
		hidden = true,
		unhide = "cureVenomPoison"
	)
	default boolean poisonCheckPrayer()
	{
		return true;
	}

	/**
	 * -------------------------------------------------------------MISC SECTION
	 */
	@ConfigItem(
		keyName = "allowWorldHop",
		name = "Allow Worldhop",
		description = "Enable world hopping",
		position = 0,
		section = misc
	)
	default boolean allowWorldHop()
	{
		return true;
	}

	@ConfigItem(
		keyName = "nameToEnableWorldHop",
		name = "Name to WorldHop after",
		description = "Fill in the name that makes you hop worlds",
		section = misc,
		position = 1,
		hidden = true,
		unhide = "allowWorldHop"
	)
	default String nameToEnableWorldHop()
	{
		return "Fill in the Name";
	}

	/**
	 * -------------------------------------------------------------DEBUGGING SECTION
	 */
	@ConfigItem(
		position = 1,
		keyName = "enableDebug",
		name = "Enable Debug",
		description = "Enable debugging for errors",
		section = debug
	)
	default boolean debug()
	{
		return false;
	}
}
