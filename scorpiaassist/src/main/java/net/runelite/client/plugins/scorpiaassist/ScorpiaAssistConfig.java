package net.runelite.client.plugins.scorpiaassist;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

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

	@ConfigItem(
		keyName = "safePlayers",
		name = "Safe Players",
		description = "Players to not logout from.",
		position = 1
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
