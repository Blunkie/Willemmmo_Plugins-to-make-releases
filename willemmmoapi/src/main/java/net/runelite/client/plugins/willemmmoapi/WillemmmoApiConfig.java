package net.runelite.client.plugins.willemmmoapi;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("WillemmmoApi")
public interface WillemmmoApiConfig extends Config
{
	@ConfigSection(
		keyName = "delayConfig",
		name = "Sleep Delay Configuration",
		description = "Configure how the game handles sleep delays",
		closedByDefault = true,
		position = 0
	)
	String delayConfig = "delayConfig";

	@ConfigItem(keyName = "invokes", name = "Use invokes*", description = "Increased *speculated* risk, use at your own risk.", position = 1)
	default boolean invokes()
	{
		return false;
	}

	@ConfigItem(
		keyName = "getMouse",
		name = "Mouse",
		description = "Choose a mouse movement style",
		position = 15
	)
	default MouseType getMouse()
	{
		return MouseType.NO_MOVE;
	}
}
