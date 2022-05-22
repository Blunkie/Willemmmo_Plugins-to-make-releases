package net.runelite.client.plugins.willemmmoapi.tasks;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.queries.NPCQuery;

@Slf4j
@Singleton
public class NPCApi
{
	@Inject
	Client client;

	@Nullable
	public NPC findClosestNPC(int... ids)
	{
		assert client.isClientThread();
		if (client.getLocalPlayer() == null)
		{
			return null;
		}
		return new NPCQuery()
			.idEquals(ids)
			.result(client)
			.nearestTo(client.getLocalPlayer());
	}

	@Nullable
	public NPC findClosestNPC(String... names)
	{
		assert client.isClientThread();
		if (client.getLocalPlayer() == null)
		{
			return null;
		}
		return new NPCQuery()
			.nameContains(names)
			.result(client)
			.nearestTo(client.getLocalPlayer());
	}
}
