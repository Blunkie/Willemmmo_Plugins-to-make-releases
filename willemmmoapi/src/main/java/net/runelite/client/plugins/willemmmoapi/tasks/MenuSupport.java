package net.runelite.client.plugins.willemmmoapi.tasks;

import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;

@Slf4j
@Singleton
public class MenuSupport
{
	@Inject
	private Client client;

	public CreateMenuEntry entry;

	public boolean sendClick;

	public void createEntry(CreateMenuEntry menuEntry)
	{
		entry = menuEntry;
	}

	public void createEntry(CreateMenuEntry menuEntry, boolean send)
	{
		entry = menuEntry;
		sendClick = send;
	}

	public void setEntry(CreateMenuEntry menuEntry)
	{
		entry = menuEntry;
	}

	public void setEntry(CreateMenuEntry menuEntry, boolean send)
	{
		entry = menuEntry;
		sendClick = send;
	}
}
