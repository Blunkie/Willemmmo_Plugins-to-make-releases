package net.runelite.client.plugins.autogodwars;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.time.Duration;
import java.time.Instant;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import javax.inject.Inject;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.input.KeyListener;

public class AutoGodwarsHotkeyListener extends MouseAdapter implements KeyListener
{
	@Inject
	private Client client;
	private Instant lastPress;
	@Inject
	private AutoGodwarsPlugin plugin;
	@Inject
	private AutoGodwarsConfig config;
	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	AutoGodwarsHotkeyListener(AutoGodwarsPlugin plugin, AutoGodwarsConfig config, Client client)
	{
		this.plugin = plugin;
		this.config = config;
		this.client = client;
	}

	@Override
	public void keyPressed(KeyEvent event)
	{
		if (client.getGameState() != GameState.LOGGED_IN || event == null)
		{
			return;
		}
		try
		{
			if (lastPress != null && Duration.between(lastPress, Instant.now()).getNano() > 1000)
			{
				lastPress = null;
			}
			if (lastPress != null)
			{
				return;
			}
			int key_code = event.getKeyCode();
			if (key_code == config.switchPrayerKey().getKeyCode() && !plugin.enableAutoPrayers)
			{
				plugin.enableAutoPrayers = true;
				sendGameMessage("Enabled AutoPrayers", config.enableColor());
				return;
			}
			if (key_code == config.switchPrayerKey().getKeyCode() && plugin.enableAutoPrayers)
			{
				plugin.enableAutoPrayers = false;
				sendGameMessage("Disabled AutoPrayers", config.disableColor());
			}
		}
		catch (Throwable ex)
		{
			System.out.print(ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	public void sendGameMessage(String message, Color color)
	{
		String chatmessage = new ChatMessageBuilder()
			.append(color, message)
			.build();
		chatMessageManager
			.queue(QueuedMessage.builder()
				.type(ChatMessageType.CONSOLE)
				.runeLiteFormattedMessage(chatmessage)
				.build());
	}
}
