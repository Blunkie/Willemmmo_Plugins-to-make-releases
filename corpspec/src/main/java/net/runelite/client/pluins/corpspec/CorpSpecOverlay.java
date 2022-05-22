package net.runelite.client.pluins.corpspec;

import com.openosrs.client.ui.overlay.components.table.TableAlignment;
import com.openosrs.client.ui.overlay.components.table.TableComponent;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;

@Slf4j
@Singleton
public class CorpSpecOverlay extends OverlayPanel
{
	private final Client client;
	private final CorpSpecPlugin plugin;
	private final CorpSpecConfig config;
	private String infoStatus = "Starting...";

	@Inject
	private CorpSpecOverlay(final Client client, final CorpSpecPlugin plugin, final CorpSpecConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.BOTTOM_LEFT);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		TableComponent tableComponent = new TableComponent();
		tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);
		if (plugin.state != null)
		{
			infoStatus = "you are FUCKED";
			if (!plugin.state.name().equals("STUCK"))
			{
				infoStatus = plugin.state.name();
			}
		}
		tableComponent.addRow("Status: ", infoStatus);
		panelComponent.getChildren().add(tableComponent);
		return super.render(graphics);
	}
}
