package net.runelite.client.plugins.autogodwars;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

public interface AutoGodwarsConfig extends Config
{
	@ConfigSection(
		keyName = "bosses",
		position = 1,
		name = "Bosses",
		description = ""
	)
	String bosses = "Bosses";
	@ConfigItem(
		position = 0,
		keyName = "gwd",
		name = "God Wars Dungeon",
		description = "Show tick timers for GWD Bosses. This must be enabled before you zone in.",
		section = bosses
	)
	default boolean gwd()
	{
		return true;
	}
	@ConfigItem(
		position = 1,
		keyName = "ignoreNonAttacking",
		name = "Ignore Non-Attacking",
		description = "Ignore monsters that are not attacking you"
	)
	default boolean ignoreNonAttacking()
	{
		return false;
	}
}
