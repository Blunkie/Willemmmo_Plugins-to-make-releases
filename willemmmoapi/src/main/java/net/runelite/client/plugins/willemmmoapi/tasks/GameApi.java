package net.runelite.client.plugins.willemmmoapi.tasks;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;

@Slf4j
@Singleton
public class GameApi
{
	@Inject
	Client client;
	@Inject
	public ExecutorService executorService;
	public static boolean waiting = false;
	public static boolean walking = false;
	public static boolean iterating = false;
	public static long gameTickDelay = 0;
	public static long millisDelay = 0;

	public Client client()
	{
		return client;
	}

	public void waitUntil(BooleanSupplier supplier)
	{
		log.info("Executing waituntil");
		waiting = true;

		if (client.isClientThread())
		{
			executorService.submit(() -> waitUntil(supplier));
			return;
		}
		if (!waitUntil(supplier, 100))
		{
			waiting = false;
			throw new IllegalStateException("took to long");
		}
		waiting = false;
	}

	public boolean waitUntil(BooleanSupplier supplier, int tickcount)
	{
		waiting = true;
		if (client.isClientThread())
		{
			boolean result = getFromExecutorThread(() -> waitUntil(supplier, tickcount));
			waiting = false;
			return result;
		}
		else
		{
			for (var i = 0; i < tickcount; i++)
			{
				if (supplier.getAsBoolean())
				{
					waiting = false;
					return true;
				}
				tick();
			}
			waiting = false;
			return false;
		}
	}

	public static boolean isBusy()
	{
		return waiting || walking || iterating;
	}

	public <T> T getFromExecutorThread(Supplier<T> supplier)
	{
		if (client.isClientThread())
		{
			CompletableFuture<T> future = new CompletableFuture<>();

			executorService.submit(() -> {
				future.complete(supplier.get());
			});
			return future.join();
		}
		else
		{
			return supplier.get();
		}
	}

	public void tick()
	{
		if (client.getGameState() == GameState.LOGIN_SCREEN || client.getGameState() == GameState.LOGIN_SCREEN_AUTHENTICATOR)
		{
			return;
		}

		waiting = true;
		gameTickDelay = ticks() + 1;

		if (client.isClientThread())
		{
			return;
		}

		long start = client().getTickCount();

		while (client.getTickCount() == start)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				waiting = false;
				throw new RuntimeException(e);
			}
		}
	}

	public long ticks()
	{
		return client.getTickCount();
	}
}
