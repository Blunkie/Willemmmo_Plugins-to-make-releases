package net.runelite.client.plugins.autogodwars;


import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.mixins.Inject;

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

	private int AnglerFish()
	{
		return 0;
	}

	enum Food
	{

		MANTA_RAY(ItemID.MANTA_RAY, "Manta ray", 22),
		TUNA_POTATO(ItemID.TUNA_POTATO, "Tuna potato", 22),
		DARK_CRAB(ItemID.DARK_CRAB, "Dark crab", 22),
		ANGLERFISH(ItemID.ANGLERFISH, "Anglerfish", 22),
		SEA_TURTLE(ItemID.SEA_TURTLE, "Sea turtle", 21),
		MUSHROOM_POTATO(ItemID.MUSHROOM_POTATO, "Mushroom potato", 20),
		SHARK(ItemID.SHARK, "Shark", 20),
		CURRY(ItemID.CURRY, "Curry", 19),
		UGTHANKI_KEBAB(ItemID.UGTHANKI_KEBAB, "Ugthanki kebab", 19),
		COOKED_KARAMBWAN(ItemID.COOKED_KARAMBWAN, "Cooked karambwan", 18),
		PEACH(ItemID.PEACH, "Peach", 8),
		MONK_FISH(ItemID.MONKFISH, "Monk fish", 16),
		SUMMER_PIE(ItemID.SUMMER_PIE, "Summer pie", 11),
		HALF_A_SUMMER_PIE(ItemID.HALF_A_SUMMER_PIE, "Half a summer pie", 11),
		PINEAPPLE_PIZZA(ItemID.PINEAPPLE_PIZZA, "1/2 pineapple pizza", 11),
		_12_PINEAPPLE_PIZZA(ItemID._12_PINEAPPLE_PIZZA, "Pineapple pizza", 11),
		ANCHOVY_PIZZA(ItemID.ANCHOVY_PIZZA, "Anchovy pizza", 9),
		_12_ANCHOVY_PIZZA(ItemID._12_ANCHOVY_PIZZA, "1/2 anchovy pizza", 9);
		@Getter
		private final int id;
		@Getter
		private final String name;
		@Getter
		private final double heals;
		@Inject
		private Client client;


		Food(int id, String name, double heals)
		{
			this.id = id;
			this.name = name;
			if (id == ItemID.ANGLERFISH)
			{
				int lvlHitpoints = client.getRealSkillLevel(Skill.HITPOINTS);
				int modifier = 13;
				if (lvlHitpoints >= 10 && lvlHitpoints <= 24)
				{
					modifier = 2;
				}
				if (lvlHitpoints >= 25 && lvlHitpoints <= 49)
				{
					modifier = 4;
				}
				if (lvlHitpoints >= 25 && lvlHitpoints <= 49)
				{
					modifier = 6;
				}
				if (lvlHitpoints >= 25 && lvlHitpoints <= 49)
				{
					modifier = 8;
				}
				this.heals = (lvlHitpoints * 0.1) + modifier;
			}
			else
			{
				this.heals = heals;
			}
		}
	}

	enum Brew
	{
		SARADOMIN_BREW(ItemID.SARADOMIN_BREW1, ItemID.SARADOMIN_BREW2, ItemID.SARADOMIN_BREW3, ItemID.SARADOMIN_BREW4),
		None(0, 0, 0, 0);

		@Getter
		private final int dose1, dose2, dose3, dose4;
		//@Getter
		private int[] ids;


		Brew(int dose4, int dose3, int dose2, int dose1, int... ids)
		{
			this.dose1 = dose1;
			this.dose2 = dose2;
			this.dose3 = dose3;
			this.dose4 = dose4;
			this.ids = ids;
		}

		public int[] getIds()
		{
			ids = new int[]{dose1, dose2, dose3, dose4};
			return ids;
		}
	}

	@Getter
	enum RestorePrayer
	{
		PRAYER_POTION(ItemID.PRAYER_POTION1, ItemID.PRAYER_POTION2, ItemID.PRAYER_POTION3, ItemID.PRAYER_POTION4),
		SUPER_RESTORE(ItemID.SUPER_RESTORE1, ItemID.SUPER_RESTORE2, ItemID.SUPER_RESTORE3, ItemID.SUPER_RESTORE4),
		SANFEW_SERUM(ItemID.SANFEW_SERUM1, ItemID.SANFEW_SERUM2, ItemID.SANFEW_SERUM3, ItemID.SANFEW_SERUM4);

		@Getter
		private final int dose1, dose2, dose3, dose4;

		private int[] ids;

		RestorePrayer(int dose1, int dose2, int dose3, int dose4, int... ids)
		{
			this.dose1 = dose1;
			this.dose2 = dose2;
			this.dose3 = dose3;
			this.dose4 = dose4;
			this.ids = ids;
		}

		public int[] getIds()
		{
			ids = new int[]{dose1, dose2, dose3, dose4};
			return ids;
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

		private int[] ids;

		Antivenom(int dose1, int dose2, int dose3, int dose4, int... ids)
		{
			this.dose1 = dose1;
			this.dose2 = dose2;
			this.dose3 = dose3;
			this.dose4 = dose4;
			this.ids = ids;
		}

		public int[] getIds()
		{
			ids = new int[]{dose1, dose2, dose3, dose4};
			return ids;
		}
	}

	@RequiredArgsConstructor
	@Getter
	public enum Stamina
	{
		STAMINA_1(ItemID.STAMINA_POTION1, 20),
		STAMINA_2(ItemID.STAMINA_POTION2, 20),
		STAMINA_3(ItemID.STAMINA_POTION3, 20),
		STAMINA_4(ItemID.STAMINA_POTION4, 20),
		STAMINA_MIX_1(ItemID.STAMINA_MIX1, 20),
		STAMINA_MIX_2(ItemID.STAMINA_MIX2, 99),
		PURPLE_SWEETS(ItemID.PURPLE_SWEETS_10476, 10);

		private static final ImmutableMap<Integer, Stamina> idmap;

		static
		{
			ImmutableMap.Builder<Integer, Stamina> builder = ImmutableMap.builder();

			for (Stamina stamina : values())
			{
				builder.put(stamina.itemID, stamina);
			}
			idmap = builder.build();
		}

		private final int itemID;
		private final int runEnergy;

		public static Stamina of(int itemID)
		{
			return idmap.get(itemID);
		}
	}
}

