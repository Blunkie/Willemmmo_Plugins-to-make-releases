package net.runelite.client.plugins.autogodwars;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runelite.api.GameObject;

@Getter(AccessLevel.PACKAGE)
class GameObjectContainer
{
	@Getter(AccessLevel.PACKAGE)
	private final GameObject gameObject;
	@Setter
	private AltarName gameObjectName;
	@Getter
	private final int gameObjectID;

	GameObjectContainer(final GameObject gameObject)
	{
		this.gameObject = gameObject;
		this.gameObjectName = AltarName.UNKNOWN;
		this.gameObjectID = gameObject.getId();

		GodwarsAltars godwarsAltars = GodwarsAltars.of(gameObject.getId());
		if (godwarsAltars == null)
		{
			throw new IllegalStateException();
		}
		this.gameObjectName = godwarsAltars.altarName;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(gameObject);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		GameObjectContainer that = (GameObjectContainer) o;
		return Objects.equals(gameObject, that.gameObject);
	}

	@RequiredArgsConstructor
	enum GodwarsAltars
	{
		Arma_Altar(26365, AltarName.ARMADYL_ALTAR),
		/**
		 * ARMADYL ALTAR = 2821,5301
		 * SW = 2824,5296
		 * NW = 2824,5308
		 * NE = 2842,5308
		 * SE = 2842,5296
		 * DOOR ID = 26502(gameobject) 40419(Examine)
		 */
		Bandos_Altar(26366, AltarName.BANDOS_ALTAR),
		/**
		 * BANDOS ALTAR = 2869, 5370 center = +1
		 * SW = 2864,5351
		 * NW = 2864,5369
		 * NE = 2876,5369
		 * SE = 2876,5351
		 * DOOR ID = 26503(gameobject) 40419(Examine)
		 * General spawn tile = 2872,5358
		 * Grimspike spawn tile = 2868,5362
		 * Steelwill spawn tile = 2872, 5352
		 * Strongstack spawn tile = 2866,5358
		 */
		Sara_Altar(26364, AltarName.SARADOMIN_ALTAR),
		/**
		 * SARADOMIN ALTAR = 2885,5267
		 * SW = 2889,5258
		 * NW = 2889,5275
		 * NE = 2907,5275
		 * SE = 2907,5257
		 * DOOR ID = 26504(gameobject) 40419(Examine)
		 */
		Zammy_Altar(26363, AltarName.ZAMORAK_ALTAR);
		/**
		 * ZAMORAK ALTAR = 2937,5323
		 * SW = 2918,5318
		 * NW = 2918,5331
		 * NE = 2936,5331
		 * SE = 2936,5318
		 * DOOR ID = 26505(gameobject) 40419(Examine)
		 */
		private static final ImmutableMap<Integer, GodwarsAltars> idMap;

		static
		{
			ImmutableMap.Builder<Integer, GodwarsAltars> builder = ImmutableMap.builder();
			for (GodwarsAltars altars : values())
			{
				builder.put(altars.gameObjectID, altars);
			}
			idMap = builder.build();
		}

		private final int gameObjectID;
		private final AltarName altarName;

		static GodwarsAltars of(int gameobjectID)
		{
			return idMap.get(gameobjectID);
		}
	}

	@AllArgsConstructor
	@Getter
	public enum AltarName
	{
		ARMADYL_ALTAR("Armadyl altar"),
		BANDOS_ALTAR("Bandos altar"),
		SARADOMIN_ALTAR("Saradomin altar"),
		ZAMORAK_ALTAR("Zamorak altar"),
		UNKNOWN("Unknown");
		private final String name;
	}
}
