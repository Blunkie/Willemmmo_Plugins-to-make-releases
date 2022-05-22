package net.runelite.client.plugins.willemmmoapi.tasks;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.queries.GameObjectQuery;
import org.apache.commons.lang3.ArrayUtils;

@Slf4j
@Singleton
public class ObjectApi
{
	@Inject
	private Client client;

	@Nullable
	public GameObject findNearestGameObject(int... ids)
	{
		assert client.isClientThread();
		if (client.getLocalPlayer() == null)
		{
			return null;
		}
		return new GameObjectQuery()
			.idEquals(ids)
			.result(client)
			.nearestTo(client.getLocalPlayer());
	}

	@Nullable
	public GameObject findNearestGameObjectMenuWithin(WorldPoint worldPoint, int dist, String menuAction)
	{
		assert client.isClientThread();
		if (client.getLocalPlayer() == null)
		{
			return null;
		}
		return new GameObjectQuery()
			.isWithinDistance(worldPoint, dist)
			.filter(w -> ArrayUtils.contains(client.getObjectDefinition(w.getId()).getActions(), menuAction))
			.result(client)
			.nearestTo(client.getLocalPlayer());
	}

	@Nullable
	public GameObject findNearestGameObjectName(WorldPoint worldPoint, int distance, String name)
	{
		assert client.isClientThread();
		if (client.getLocalPlayer() == null)
		{
			return null;
		}
		return new GameObjectQuery()
			.nameEquals(name)
			.result(client)
			.nearestTo(client.getLocalPlayer());
	}
}
