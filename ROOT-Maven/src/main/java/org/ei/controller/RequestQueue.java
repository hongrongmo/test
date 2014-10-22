package org.ei.controller;

import java.util.LinkedList;

public class RequestQueue
{

	private LinkedList elements = new LinkedList();
	private boolean closed = false;

           
	public class Closed extends RuntimeException
	{ 
		private Closed()          
		{ 
			super("Tried to access closed Blocking_queue");
        	}
	}

         
  	public synchronized final void enqueue(Object newElement)
                                                throws RequestQueue.Closed
	{ 
		if(closed)
		{
                	throw new Closed();
		}

		elements.addLast(newElement);
		notify();
	}
          
        
	public synchronized final Object dequeue() 
		throws InterruptedException, 
		       RequestQueue.Closed
  	{ 
  		while(elements.size() <= 0)
  		{ 
			wait();
  			if(closed)
			{
  				throw new Closed();
			}
  		}

		return elements.removeFirst();
	}
        

  	public synchronized final boolean isEmpty()
  	{ 
		return elements.size() > 0;
  	}
  
  	public synchronized void close()
  	{ 
		closed = true;
  		notifyAll();
	}

}
