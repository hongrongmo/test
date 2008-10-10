package org.ei.logging;

import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedList;

import org.ei.connectionpool.ConnectionBroker;

public class CLFLogger
{
    private CLFLogConsumer consumer;
    private LinkedList logQueue = new LinkedList();
	private long lastChecked;


    public CLFLogger(String filename)
        throws Exception
    {
        consumer = new CLFLogConsumer(filename);
        consumer.start();
    }

    public synchronized void enqueue(CLFMessage message)
    {
        logQueue.add(message);
    }

    public synchronized long getLastChecked()
    {
        return this.lastChecked;
    }

    public synchronized void setLastChecked(long time)
	{
		this.lastChecked = time;
    }

    public synchronized CLFMessage[] dequeue()
    {
        CLFMessage[] messages = new CLFMessage[logQueue.size()];
        for(int i=0; i<messages.length; i++)
        {
			messages[i] = (CLFMessage)logQueue.get(i);
		}
		logQueue.clear();

		return messages;
    }

    public void shutdown()
    {
        this.consumer.shutdown();
    }


    class CLFLogConsumer extends Thread
    {
        private String logServiceURL;
        private boolean shutdown = false;
        private int rollEvery = 1000*60*60*24;
        private String filename;

        public void shutdown()
        {
            this.shutdown = true;
        }

        public CLFLogConsumer(String filename)
            throws IOException
        {
            this.filename = filename;
        }

        public void run()
        {
            while(!shutdown)
            {
				setLastChecked(System.currentTimeMillis());
				CLFMessage[] messages = dequeue();
                for(int x=0; x<messages.length;++x)
                {
                    ConnectionBroker broker = null;
                    Connection con = null;
                    CLFMessage message = messages[x];

                    try
                    {
                        broker = ConnectionBroker.getInstance();
                        con = broker.getConnection("village_log");
                        ALSSqlHandler als = new ALSSqlHandler();
                        als.writeMessage(message,con);
                    }
                    catch(Exception e1)
                    {
                        e1.printStackTrace();
                    }
                    finally
                    {
                        if(con != null)
                        {
                            try
                            {
                                broker.replaceConnection(con,"village_log");
                            }
                            catch(Exception e2)
                            {
                                e2.printStackTrace();
                            }
                        }
                    }

                    try
                    {
                        FileLogger flogger = FileLogger.getInstance(filename,
                                                                    rollEvery);
                        flogger.write(message.getCLFMessage());
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                try
                {
                    Thread.sleep(1500);
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }

            try
            {
                FileLogger flogger = FileLogger.getInstance(filename,
                                                        rollEvery);
                flogger.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
