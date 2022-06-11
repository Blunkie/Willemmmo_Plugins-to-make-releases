package net.runelite.client.plugins.scorpiaassist;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("ScorpiaAssistConfig")
public interface ScorpiaAssistConfig extends Config
{
	@ConfigItem(
		keyName = "startPlugin",
		name = "Start/Stop",
		description = "Start/Stops the plugin",
		position = 0,
		title = "StartPlugin"
	)
	default Button startPlugin()
	{
		return new Button();
	}

	@ConfigSection(
		name = "Sleep Delays",
		description = "",
		position = 1,
		keyName = "sleepDelays",
		closedByDefault = true

	)
	String sleepDelays = "Sleep Delays";

	@Range(
		min = 0,
		max = 160
	)
	@ConfigItem(
		keyName = "sleepMin",
		name = "Sleep Min",
		description = "",
		position = 0,
		section = sleepDelays
	)
	default int sleepMin()
	{
		return 60;
	}

	@Range(
		min = 0,
		max = 160
	)
	@ConfigItem(
		keyName = "sleepMax",
		name = "Sleep Max",
		description = "",
		position = 1,
		section = sleepDelays
	)
	default int sleepMax()
	{
		return 350;
	}

	@Range(
		min = 0,
		max = 160
	)
	@ConfigItem(
		keyName = "sleepTarget",
		name = "Sleep Target",
		description = "",
		position = 2,
		section = sleepDelays
	)
	default int sleepTarget()
	{
		return 100;
	}

	@Range(
		min = 0,
		max = 160
	)
	@ConfigItem(
		keyName = "sleepDeviation",
		name = "Sleep Deviation",
		description = "",
		position = 3,
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
		position = 4,
		section = sleepDelays
	)
	default boolean sleepWeightedDistribution()
	{
		return false;
	}

	@ConfigItem(
		keyName = "safePlayers",
		name = "Safe Players",
		description = "Players to not logout from.",
		position = 2
	)
	default String safePlayers()
	{
		return "player1,player2,player3";
	}

	@ConfigItem(keyName = "debug", name = "Debug Messages", description = "", position = 2)
	default boolean debug()
	{
		return false;
	}
}
