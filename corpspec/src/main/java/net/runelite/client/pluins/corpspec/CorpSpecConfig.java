package net.runelite.client.pluins.corpspec;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ConfigTitle;

@ConfigGroup("CorpSpec")
public interface CorpSpecConfig extends Config
{
	@ConfigTitle(
		keyName = "CorpSpec",
		name = "Corp Spec by Willemmmo",
		description = "Config of this plugin",
		position = 0
	)
	String CorpSpec = "Corp Spec by Willemmmo";

	@ConfigSection(
		keyName = "Title1",
		name = "Distance for objects",
		description = "Setup distances",
		position = 0
	)
	String Title1 = "Title 1";

	@ConfigItem(
		keyName = "distance1",
		name = "Distance to check",
		description = "Distance in POH",
		position = 0,
		section = Title1
	)
	default int distance1()
	{
		return 10;
	}
	@ConfigItem(
		keyName = "targetaccount",
		name = "Target for spectransfer",
		description = "Name of target player",
		position = 1,
		section = Title1
	)
	default String targetaccount()
	{
		return "Fill in name";
	}
}
