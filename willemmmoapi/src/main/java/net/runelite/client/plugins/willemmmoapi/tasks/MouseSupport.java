package net.runelite.client.plugins.willemmmoapi.tasks;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.willemmmoapi.MouseType;
import net.runelite.client.plugins.willemmmoapi.WillemmmoApiConfig;
import static net.runelite.client.plugins.willemmmoapi.WillemmmoApiPlugin.sleep;

@Slf4j
@Singleton
public class MouseSupport
{
	@Inject
	private Client client;
	@Inject
	private ExecutorService executorService;
	@Inject
	private WillemmmoApiConfig config;
	@Inject
	private Calculations calculations;

	private void mouseEvent(int id, Point point)
	{
		MouseEvent e = new MouseEvent(
			client.getCanvas(),
			id,
			System.currentTimeMillis(),
			0,
			point.getX(),
			point.getY(),
			1,
			false,
			1
		);
		client.getCanvas().dispatchEvent(e);
	}

	public void click(Rectangle rectangle)
	{
		assert !client.isClientThread();

		Point point = getClickPoint(rectangle);
		click(point);
	}

	public void moveClick(Rectangle rectangle)
	{
		assert !client.isClientThread();

		Point point = getClickPoint(rectangle);
		moveClick(point);
	}

	public void moveClick(Point p)
	{
		assert !client.isClientThread();

		if (client.isStretchedEnabled())
		{
			final Dimension stretched = client.getStretchedDimensions();
			final Dimension real = client.getRealDimensions();
			final double width = (stretched.width / real.getWidth());
			final double height = (stretched.height / real.getHeight());
			final Point point = new Point((int) (p.getX() * width), (int) (p.getY() * height));
			mouseEvent(504, point);
			mouseEvent(505, point);
			mouseEvent(503, point);
			mouseEvent(501, point);
			mouseEvent(502, point);
			mouseEvent(500, point);
			return;
		}
		mouseEvent(504, p);
		mouseEvent(505, p);
		mouseEvent(503, p);
		mouseEvent(501, p);
		mouseEvent(502, p);
		mouseEvent(500, p);
	}

	public void handleMouseClick(Point point)
	{
		//assert !client.isClientThread();
		final int viewportHeight = client.getViewportHeight();
		final int viewportWidth = client.getViewportWidth();
		log.debug("Performing mouse click: {}", config.getMouse());
		Widget minimapWidget = client.getWidget(164, 20);
		if (minimapWidget != null && minimapWidget.getBounds().contains(point.getX(), point.getY()))
		{
			log.info("Avoiding minimap click");
			point = new Point(0, 0);
		}
		switch (config.getMouse())
		{
			case MOVE:
				if (point.getX() > viewportWidth || point.getY() > viewportHeight || point.getX() < 0 || point.getY() < 0)
				{
					point = new Point(client.getCenterX() + calculations.getRandomIntBetweenRange(-100, 100),
						client.getCenterY() + calculations.getRandomIntBetweenRange(-100, 100));
				}
				break;
			case ZERO_MOUSE:
				point = new Point(0, 0);
				break;
			case NO_MOVE:
				if (point.getX() > viewportWidth || point.getY() > viewportHeight || point.getX() < 0 || point.getY() < 0)
				{
					point = new Point(client.getCenterX() + calculations.getRandomIntBetweenRange(-100, 100),
						client.getCenterY() + calculations.getRandomIntBetweenRange(-100, 100));
					break;
				}
			case RECTANGLE:
				point = new Point(client.getCenterX() + calculations.getRandomIntBetweenRange(-100, 100),
					client.getCenterY() + calculations.getRandomIntBetweenRange(-100, 100));
				break;
		}
		log.debug("Clicking at Point: {}", point);
		if (!client.isClientThread())
		{
			if (config.getMouse().equals(MouseType.MOVE))
			{
				moveClick(point);
			}
			else
			{
				click(point);
			}
		}
		else
		{
			Point finalClickPoint = point;
			log.debug("Clicking on new thread");
			if (config.getMouse().equals(MouseType.MOVE))
			{
				executorService.submit(() -> moveClick(finalClickPoint));
			}
			else
			{
				executorService.submit(() -> click(finalClickPoint));
			}
		}
	}

	public void click(Point p)
	{
		assert !client.isClientThread();
		if (client.isStretchedEnabled())
		{
			final Dimension stretched = client.getStretchedDimensions();
			final Dimension real = client.getRealDimensions();
			final double width = (stretched.width / real.getWidth());
			final double height = (stretched.height / real.getHeight());
			final Point point = new Point((int) (p.getX() * width), (int) (p.getY() * height));
			mouseEvent(501, point);
			mouseEvent(502, point);
			mouseEvent(500, point);
			return;
		}
		mouseEvent(501, p);
		mouseEvent(502, p);
		mouseEvent(500, p);
	}

	public Point getClickPoint(Rectangle rect)
	{
		final int x = (int) (rect.getX() + calculations.getRandomIntBetweenRange((int) rect.getWidth() / 6 * -1, (int) rect.getWidth() / 6) + rect.getWidth() / 2);
		final int y = (int) (rect.getY() + calculations.getRandomIntBetweenRange((int) rect.getHeight() / 6 * -1, (int) rect.getHeight() / 6) + rect.getHeight() / 2);

		return new Point(x, y);
	}

	public void delayMouseClick(Point point, long delay)
	{
		executorService.submit(() ->
		{
			try
			{
				sleep(delay);
				handleMouseClick(point);
			}
			catch (RuntimeException e)
			{
				e.printStackTrace();
			}
		});
	}

	public void delayMouseClick(Rectangle rectangle, long delay)
	{
		Point point = getClickPoint(rectangle);
		delayMouseClick(point, delay);
	}

}
