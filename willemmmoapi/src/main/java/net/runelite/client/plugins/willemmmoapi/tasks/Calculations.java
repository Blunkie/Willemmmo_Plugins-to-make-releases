package net.runelite.client.plugins.willemmmoapi.tasks;

import java.util.concurrent.ThreadLocalRandom;

public class Calculations
{
	public int getRandomIntBetweenRange(int min, int max)
	{
		//return (int) ((Math.random() * ((max - min) + 1)) + min); //This does not allow return of negative values
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
}
