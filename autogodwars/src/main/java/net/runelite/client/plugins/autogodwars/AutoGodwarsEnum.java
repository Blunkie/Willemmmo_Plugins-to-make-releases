package net.runelite.client.plugins.autogodwars;


import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.Prayer;

public class AutoGodwarsEnum
{
	public enum BandosRole
	{
		TANK,
		DPS_MAGE,
		DPS_RANGE
	}

	public enum BandosFocus
	{
		SERGEANT_STRONGSTACK(NPCContainer.BossMonsters.SERGEANT_STRONGSTACK.getNpcID()),
		SERGEANT_STEELWILL(NPCContainer.BossMonsters.SERGEANT_STEELWILL.getNpcID()),
		SERGEANT_GRIMSPIKE(NPCContainer.BossMonsters.SERGEANT_GRIMSPIKE.getNpcID()),
		GENERAL_GRAARDOR(NPCContainer.BossMonsters.GENERAL_GRAARDOR.getNpcID());

		private final int id;

		BandosFocus(int npcID)
		{
			this.id = npcID;
		}

		public final int getId()
		{
			return id;
		}
	}

	public enum Focus
	{
		SERGEANT_STRONGSTACK(NPCContainer.BossMonsters.SERGEANT_STRONGSTACK.getNpcID()),
		SERGEANT_STEELWILL(NPCContainer.BossMonsters.SERGEANT_STEELWILL.getNpcID()),
		SERGEANT_GRIMSPIKE(NPCContainer.BossMonsters.SERGEANT_GRIMSPIKE.getNpcID()),
		GENERAL_GRAARDOR(NPCContainer.BossMonsters.GENERAL_GRAARDOR.getNpcID());

		private final int id;

		Focus(int npcID)
		{
			this.id = npcID;
		}

		public int getId()
		{
			return id;
		}
	}

	public enum BoostingPrayers
	{
		PIETY(Prayer.PIETY, Prayer.PIETY.getVarbit()),
		RIGOUR(Prayer.RIGOUR, Prayer.RIGOUR.getVarbit()),
		AUGURY(Prayer.AUGURY, Prayer.AUGURY.getVarbit());
		private final Prayer prayer;
		private final int varbit;

		BoostingPrayers(Prayer prayer, int varbit)
		{
			this.prayer = prayer;
			this.varbit = varbit;
		}

		public Prayer getPrayer()
		{
			return prayer;
		}

		public int getVarbit()
		{
			return varbit;
		}
	}

	enum Food
	{
		MANTA_RAY(ItemID.MANTA_RAY, "Manta ray"),
		TUNA_POTATO(ItemID.TUNA_POTATO, "Tuna potato"),
		DARK_CRAB(ItemID.DARK_CRAB, "Dark crab"),
		ANGLERFISH(ItemID.ANGLERFISH, "Anglerfish"),
		SEA_TURTLE(ItemID.SEA_TURTLE, "Sea turtle"),
		MUSHROOM_POTATO(ItemID.MUSHROOM_POTATO, "Mushroom potato"),
		SHARK(ItemID.SHARK, "shark"),
		COOKED_KARAMBWAN(ItemID.COOKED_KARAMBWAN, "Cooked karambwan"),
		PEACH(ItemID.PEACH, "Peach"),
		MONK_FISH(ItemID.MONKFISH, "Monk fish"),
		SUMMER_PIE(ItemID.SUMMER_PIE, "Summer pie"),
		HALF_A_SUMMER_PIE(ItemID.HALF_A_SUMMER_PIE, "Half a summer pie");
		@Getter
		private final int id;
		@Getter
		private final String name;

		Food(int id, String name)
		{
			this.id = id;
			this.name = name;
		}
	}

	enum Brew
	{
		SARADOMIN_BREW(ItemID.SARADOMIN_BREW1, ItemID.SARADOMIN_BREW2, ItemID.SARADOMIN_BREW3, ItemID.SARADOMIN_BREW4),
		None(0, 0, 0, 0);

		@Getter
		private final int dose1, dose2, dose3, dose4;
		@Getter
		private final int[] ids;


		Brew(int dose1, int dose2, int dose3, int dose4, int... ids)
		{
			this.dose1 = dose1;
			this.dose2 = dose2;
			this.dose3 = dose3;
			this.dose4 = dose4;
			this.ids = ids;
		}
	}

	enum RestorePrayer
	{
		PRAYER_POTION(ItemID.PRAYER_POTION1, ItemID.PRAYER_POTION2, ItemID.PRAYER_POTION3, ItemID.PRAYER_POTION4, ItemID.PRAYER_POTION4, ItemID.PRAYER_POTION3, ItemID.PRAYER_POTION2, ItemID.PRAYER_POTION1),
		SUPER_RESTORE(ItemID.SUPER_RESTORE1, ItemID.SUPER_RESTORE2, ItemID.SUPER_RESTORE3, ItemID.SUPER_RESTORE4, ItemID.SUPER_RESTORE4, ItemID.SUPER_RESTORE3, ItemID.SUPER_RESTORE2, ItemID.SUPER_RESTORE1),
		SANFEW_SERUM(ItemID.SANFEW_SERUM1, ItemID.SANFEW_SERUM2, ItemID.SANFEW_SERUM3, ItemID.SANFEW_SERUM4);

		@Getter
		private final int dose1, dose2, dose3, dose4;

		@Getter
		private final int[] ids;

		RestorePrayer(int dose1, int dose2, int dose3, int dose4, int... ids)
		{
			this.dose1 = dose1;
			this.dose2 = dose2;
			this.dose3 = dose3;
			this.dose4 = dose4;
			this.ids = ids;
		}
	}

	enum Antivenom
	{
		ANTI_POISON(ItemID.ANTIPOISON1, ItemID.ANTIPOISON2, ItemID.ANTIPOISON3, ItemID.ANTIPOISON4),
		SUPER_ANTI_POISON(ItemID.SUPERANTIPOISON1, ItemID.SUPERANTIPOISON2, ItemID.SUPERANTIPOISON3, ItemID.SUPERANTIPOISON4),
		ANTIDOTE_PLUS(ItemID.ANTIDOTE1, ItemID.ANTIDOTE2, ItemID.ANTIDOTE3, ItemID.ANTIDOTE4),
		ANTIDOTE_PLUSPLUS(ItemID.ANTIDOTE1_5958, ItemID.ANTIDOTE2_5956, ItemID.ANTIDOTE3_5954, ItemID.ANTIDOTE4_5952),
		ANTI_VENOM(ItemID.ANTIVENOM1, ItemID.ANTIVENOM2, ItemID.ANTIVENOM3, ItemID.ANTIVENOM4),
		ANTI_VENOMPLUS(ItemID.ANTIVENOM1_12919, ItemID.ANTIVENOM2_12917, ItemID.ANTIVENOM3_12915, ItemID.ANTIVENOM4_12913),
		SANFEW_SERUM(ItemID.SANFEW_SERUM1, ItemID.SANFEW_SERUM2, ItemID.SANFEW_SERUM3, ItemID.SANFEW_SERUM4);
		@Getter
		private final int dose1, dose2, dose3, dose4;

		@Getter
		private final int[] ids;

		Antivenom(int dose1, int dose2, int dose3, int dose4, int... ids)
		{
			this.dose1 = dose1;
			this.dose2 = dose2;
			this.dose3 = dose3;
			this.dose4 = dose4;
			this.ids = ids;
		}
	}
}

