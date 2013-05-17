package server;

/**
 * final static method to pause a thread (USED ONLY FOR TESTING)
 */
public final class Pause
{
	/*
	 * pauses a thread in order to try to improve concurrency hit rate
	 * @param
	 * 	long - time of pause in milli's
	 * 	boolean - to enable this pause or not
	 * 		if the program is in 'test mode'
	 */
	public static void pause(long milli, boolean testing)
	{
		if (testing)
			try
			{
				Thread.sleep(milli);
			}
			catch (InterruptedException ignore)
			{
			}
	}
}
