package server;

public final class Pause
{
	public static void pause(long milli, boolean testing)
	{
		if(testing)
			try
			{
				Thread.sleep(milli);
			}
			catch (InterruptedException ignore)
			{
			}
	}
}
