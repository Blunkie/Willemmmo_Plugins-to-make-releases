package net.runelite.client.plugins.scorpiaassist;


import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.iutils.iUtils;
import net.runelite.client.plugins.iutils.scripts.ReflectBreakHandler;
import net.runelite.client.plugins.iutils.scripts.iScript;
import org.pf4j.Extension;

@Extension
@Slf4j
@PluginDependency(iUtils.class)
public class ScorpiaAssistPlugin extends iScript
{
	@Inject
	private ScorpiaAssistConfig config;

	@Inject
	private Client client;

	@Inject
	private iUtils utils;

	@Inject
	private ReflectBreakHandler breakHandler;


	@Override
	protected void loop()
	{
		game.tick();
	}

	@Override
	protected void onStart()
	{
		breakHandler.startPlugin(this);
	}

	@Override
	protected void onStop()
	{
		breakHandler.stopPlugin(this);
	}

	public ScorpiaAssistPlugin()
	{

	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (breakHandler.isBreakActive(this))
		{
			return;
		}
	}

	private boolean isInPOH()
	{
		return false;
	}
}
