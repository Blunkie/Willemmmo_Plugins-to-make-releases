package onetickboltenchant.ScriptCommand;

public class ScriptCommandFactory
{
	public static ScriptCommand builder(final String scriptCommand)
	{
		switch (scriptCommand.toLowerCase())
		{
			case "enchantbolt":
				return new EnchantBoltCommand();
			case "openinventory":
				return new OpenInventoryTabCommand();
			case "openmage":
				return new OpenMageTabCommand();
			default:
				return new ExceptionCommand();

		}
	}
}
