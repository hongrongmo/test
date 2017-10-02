package vtw.threads;

/**
 * 
 * @author TELEBH
 * @Date: 01/30/2017
 * @Description: main class that implememnts Runnable Interface
 * 1. As a first step, implement a run() method provided by a Runnable interface
 * 2. instantiate a Thread object, using start() method
 * 3. start thread by calling start() method, which executes a call to run( ) method
 */
public class ThreadRunnable implements Runnable{

	
	Thread th;
	String threadName;
	
	public ThreadRunnable(String thread_name) 
	{
		threadName = thread_name;
		System.out.println("Creating Thread: " + threadName);
	}
	
	// main logic of thread/ function to do
	public void run()
	{
		try
		{
			for(int i=0;i<5;i++)
			{
				System.out.println("Iteration #: " + i);
				
				Thread.sleep(1000);  // sleep for 1 second
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	// start point for running the thread (calles run () method)
	
	public void start()
	{
		System.out.println("starting thread: " + threadName);
		if(th ==null)
		{
			th = new Thread(this,threadName);
			th.start();
		}
	}
	

	public static class ThreadTest
	{
		public static void main(String[] args)
		{
			ThreadRunnable thr1 = new ThreadRunnable("Thread1");
			thr1.start();
			
			ThreadRunnable thr2 = new ThreadRunnable("Thread2");
			thr2.start();
		}
	}
}
