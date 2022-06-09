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
		name = "Distances&Variables",
		description = "Setup distances for object Interaction",
		position = 0
	)
	String Title1 = "Title 1";

	@ConfigItem(
		keyName = "distance1",
		name = "Distance Objects",
		description = "Distances in POH",
		position = 0,
		section = Title1
	)
	default int distance1()
	{
		return 10;
	}

	@ConfigItem(
		keyName = "üseInstance",
		name = "Use Instance",
		description = "Do you want to use the instance?",
		position = 1,
		section = Title1
	)
	default boolean UseInstance()
	{
		return true;
	}

	@ConfigItem(
		keyName = "HpToTeleport",
		name = "HP forceTeleport",
		description = "Setup tresshold for auto teleport",
		position = 2,
		section = Title1
	)
	default int HpToTeleport()
	{
		return 40;
	}

	@ConfigSection(
		keyName = "Title2",
		name = "QOL Utility",
		description = "Setup Spec transfer/Heal Other",
		position = 0
	)
	String Title2 = "Title 2";

	@ConfigItem(
		keyName = "targetaccount",
		name = "Target for Spectransfer",
		description = "Name of target player",
		position = 0,
		section = Title2
	)
	default String targetaccount()
	{
		return "Fill in name";
	}

	@ConfigItem(
		keyName = "HealOther",
		name = "Preform Heal Other",
		description = "Do you want to Heal Other?",
		position = 1,
		section = Title2
	)
	default boolean HealOther()
	{
		return true;
	}

	@ConfigItem(
		keyName = "nospectransferweapon",
		name = "On what weapon dont spec transfer",
		description = "Don't spec transfer on this weapons",
		position = 2,
		section = Title2
	)
	default String nospectransferweapon()
	{
		return "11804,2223";
	}

	@ConfigItem(
		keyName = "spectransferweapons",
		name = "On what weapon send Spec Transfer",
		description = "Sending spec transfer with this item equipped",
		position = 3,
		section = Title2
	)
	default String spectransferweapons()
	{
		return "26374";
	}
}
