package net.runelite.client.pluins.corpspec;

import com.google.inject.Provides;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.willemmmoapi.WillemmmoApiPlugin;
import net.runelite.client.plugins.willemmmoapi.tasks.ObjectApi;
import org.pf4j.Extension;

@Extension
@PluginDependency(WillemmmoApiPlugin.class)
@Slf4j
@PluginDescriptor(
	name = "Corp Spec",
	description = "Plugin to QOL killing Corp",
	tags = {"corp", "Willemmmo", "spec", "QOL", "overlay"},
	enabledByDefault = false
)
public class CorpSpecPlugin extends Plugin
{
	@Inject
	public Client client;
	@Inject
	private CorpSpecConfig config;
	@Inject
	private ConfigManager configManager;
	@Inject
	private WillemmmoApiPlugin apiPlugin;
	@Inject
	private ObjectApi objectApi;

	GameObject Pool_POH;

	@Provides
	CorpSpecConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(CorpSpecConfig.class);
	}

	@Override
	protected void startUp()
	{

	}

	@Override
	protected void shutDown()
	{

	}

	@Subscribe
	public void onClientTick(ClientTick event)//use this to do every 0.6 seconds
	{
		SearchPool();
		if (Pool_POH == null)
		{
			return;
		}
		log.info("Did find drink option. Printing ID: " + Pool_POH.getId());
	}

	@Subscribe
	public void onGameTick(GameTick event)//use this to do every MS
	{

	}

	@Nullable
	private void SearchPool()
	{
		Pool_POH = objectApi.findNearestGameObjectMenuWithinn(client.getLocalPlayer().getWorldLocation(), 10, "Drink");
	}
}
