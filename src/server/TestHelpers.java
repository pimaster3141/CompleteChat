package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * final static method to pause a thread (USED ONLY FOR TESTING)
 */
public final class TestHelpers
{	
	
	public static Socket serverSide;
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
	
	public static void assignServerSocket(final ServerSocket server)
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					serverSide = server.accept();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public static Socket getServerSocket()
	{
		return serverSide;
	}
}
