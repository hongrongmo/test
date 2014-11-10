package org.ei.controller.logging;

import java.util.LinkedList;
import java.util.Properties;

import org.ei.logging.LogClient;

public class Logger
{


    private LogConsumer consumer;
    private LinkedList logQueue = new LinkedList();

    public Logger(String logServiceURL)
        throws LogException
    {
        try
        {
            consumer = new LogConsumer(logServiceURL);
            consumer.start();
        }
        catch(Exception e)
        {
            throw new LogException(e);
        }
    }

    public synchronized void enqueue(LogEntry entry)
    {
        logQueue.addLast(entry);
    }

    public synchronized LogEntry dequeue()
    {
        return (LogEntry)logQueue.removeFirst();
    }

    public synchronized int queueSize()
    {
        return logQueue.size();
    }

    public void shutdown()
    {
        this.consumer.shutdown();
    }


    class LogConsumer extends Thread
    {
        private String logServiceURL;
        private boolean shutdown = false;

        public void shutdown()
        {
            this.shutdown = true;
        }

        public LogConsumer(String logServiceURL)
            throws LogException
        {
            this.logServiceURL = logServiceURL;
        }

        private int milliToSec(long mill)
        {
            double a = mill / 1000;
            return (int)a;
        }

        public void run()
        {

            while(!shutdown)
            {

                int size = queueSize();
                for(int x=0; x<size;++x)
                {
                    try
                    {
                        LogClient client = new LogClient(this.logServiceURL);

                        LogEntry entry = (LogEntry)dequeue();
                        Properties logProps = entry.getLogProperties();
                        if ( logProps != null) {
                            client.setappdata(logProps);
                        }
                        String qString = logProps.getProperty("QUERY_STRING");
                        if(qString != null)
                        {
                            client.setquery_string(qString);
                            client.setdb_name(logProps.getProperty("DB_NAME"));
                            client.sethits(Integer.parseInt(logProps.getProperty("HIT_COUNT")));
                            client.setnum_recs(Integer.parseInt(logProps.getProperty("NUM_RECS")));
                            client.setaction(logProps.getProperty("ACTION"));
                            client.setrec_range(1);
                        }


                        client.setdate(entry.getRequestTime());
                        client.setbegin_time(entry.getRequestTime());
                        client.setend_time(entry.getResponseTime());
                        client.setresponse_time(entry.getResponseTime()-entry.getRequestTime());
                        client.setsid(entry.getSessionID());
                        client.setHost(entry.getIPAddress());
                        client.setreferrer(entry.getRefURL());
                        client.setuser_agent(entry.getUserAgent());
                        client.setrid(entry.getRequestID());
                        client.seturi_stem(entry.getURLStem());
                        client.seturi_query(entry.getURLQuery());
                        if(!entry.getCustomerID().equals("-"))
                        {
                            client.setcust_id(Long.parseLong(entry.getCustomerID()));
                        }
                        if(entry.getTransNum() != null)
                        {
                            client.settid(entry.getTransNum().longValue());
                        }
                        client.setappid(entry.getAppName());
                        client.setusername(entry.getUsername());
                        client.sendit();

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


        }

    }


}
