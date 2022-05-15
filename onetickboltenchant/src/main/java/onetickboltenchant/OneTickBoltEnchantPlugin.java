package onetickboltenchant;

import com.google.inject.Provides;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.Point;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import onetickboltenchant.ScriptCommand.ScriptCommand;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "One Tick Bolt Enchant",
	description = "Plugin to enchant bolts for you ",
	enabledByDefault = false,
	tags = {"One", "Tick", "Bolt", "Willemmmo"}
)
@Slf4j
public class OneTickBoltEnchantPlugin extends Plugin
{
	public Queue<ScriptCommand> commandList = new ConcurrentLinkedDeque<>();
	public Queue<MenuEntry> entryList = new ConcurrentLinkedQueue<>();
	public boolean runboltenchanting = false;
	public boolean pressspace = false;
	public Robot r;
	@Inject
	public ClientThread clientThread;
	@Inject
	private Client client;
	@Getter(AccessLevel.PACKAGE)
	@Inject
	private OneTickBoltEnchantConfig config;
	@Inject
	private ConfigManager configManager;
	@Inject
	private KeyManager keyManager;
	@Inject
	private OneTickBoltEnchantHotkeyListener hotkeyListener;

	// Provides our config
	@Provides
	OneTickBoltEnchantConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OneTickBoltEnchantConfig.class);
	}

	@Override
	protected void startUp()
	{
		keyManager.registerKeyListener(hotkeyListener);
	}

	@Override
	protected void shutDown()
	{
		runboltenchanting = false;
		keyManager.unregisterKeyListener(hotkeyListener);
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		processCommands();
	}

	@Subscribe
	public void onGameTick(GameTick event) //use this to preform every tick
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		if (runboltenchanting)
		{
			click();
		}

			/*try
			{
				OneTickBoltEnchantHotkeyListener.addTickCommand("enchantbolt", this);
				//processCommands();
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

		}*/

		//if (runboltenchanting)
		//{
		//	OneTickBoltEnchantHotkeyListener.addTickCommand("enchantbolt", this);
		//	processCommands();

		//}
	}

	private void processCommands()
	{
		while (commandList.peek() != null)
		{
			commandList.poll().execute(client, config, this, configManager);
		}
	}

	public void click()
	{
		Point pos = client.getMouseCanvasPosition();

		if (client.isStretchedEnabled())
		{
			final Dimension stretched = client.getStretchedDimensions();
			final Dimension real = client.getRealDimensions();
			final double width = (stretched.width / real.getWidth());
			final double height = (stretched.height / real.getHeight());
			final Point point = new Point((int) (pos.getX() * width), (int) (pos.getY() * height));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			return;
		}

		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
	}
}