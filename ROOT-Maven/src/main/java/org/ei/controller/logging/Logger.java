package org.ei.controller.logging;

import java.util.LinkedList;
import java.util.Properties;

import org.apache.commons.validator.GenericValidator;
import org.ei.logging.LogClient;

/**
 * This class handles application logging to the 'jdbc/village_log' JDBC
 * connection.  It should be initialized at application startup by calling the
 * init method with the appropriate connection string to the current logging
 * service.  E.g. 138.12.89.2:25332/logservice/servlet
 * 
 * @author Unknown!
 *
 */
public class Logger {

	// The URL to the log service
	private String logServiceURL;
	
	// Threaded class to consume log entries
	private LogConsumer consumer;
	
	// The main logging queue - clients fill this queue up and it's
	// emptied be the LogConsumer
	private LinkedList<LogEntry> logQueue = new LinkedList<LogEntry>();

	private static Logger instance;
	
	/**
	 * Private constructor
	 * @param logServiceURL
	 * @throws LogException 
	 */
	private Logger(String logServiceURL) throws LogException {
		this.logServiceURL = logServiceURL;
		this.consumer = new LogConsumer(logServiceURL);
	};
	
	/**
	 * Init method - should be called at application startup
	 * 
	 * @param logServiceURL
	 * @return new Logger
	 * @throws LogException
	 */
	public static synchronized Logger init(String logServiceURL) throws LogException {
		try {
			
			if (instance != null) {
				throw new LogException("Logger has already been initialized!");
			}
			
			if (GenericValidator.isBlankOrNull(logServiceURL)) {
				throw new LogException("'logservice' URL is empty!");
			}
			
			instance = new Logger(logServiceURL);
			instance.consumer.start();

			return instance;
		} catch (Exception e) {
			throw new LogException(e);
		}
	}

	/**
	 * Return the singleton instance.
	 * @return
	 */
	public static Logger getInstance() {
		return instance;
	}
	
	public synchronized void enqueue(LogEntry entry) {
		logQueue.addLast(entry);
	}

	public synchronized LogEntry dequeue() {
		return (LogEntry) logQueue.removeFirst();
	}

	public synchronized int queueSize() {
		return logQueue.size();
	}

	public void shutdown() {
		this.consumer.shutdown();
	}

	/**
	 * This class is kicks off a Thread to constantly write entries to 
	 * the log service. It checks a It sleeps for 1.5 seconds between checks.
	 *  
	 * @author Unknown!
	 *
	 */
	class LogConsumer extends Thread {
		private String logServiceURL;
		private boolean shutdown = false;

		public void shutdown() {
			this.shutdown = true;
		}

		public LogConsumer(String logServiceURL) throws LogException {
			this.logServiceURL = logServiceURL;
		}

		private int milliToSec(long mill) {
			double a = mill / 1000;
			return (int) a;
		}

		public void run() {

			while (!shutdown) {

				int size = queueSize();
				for (int x = 0; x < size; ++x) {
					try {
						LogClient client = new LogClient(this.logServiceURL);

						LogEntry entry = (LogEntry) dequeue();
						Properties logProps = entry.getLogProperties();
						if (logProps != null) {
							client.setappdata(logProps);
						}
						String qString = logProps.getProperty("QUERY_STRING");
						if (qString != null) {
							client.setquery_string(qString);
							client.setdb_name(logProps.getProperty("DB_NAME"));
							client.sethits(Integer.parseInt(logProps
									.getProperty("HIT_COUNT")));
							client.setnum_recs(Integer.parseInt(logProps
									.getProperty("NUM_RECS")));
							client.setaction(logProps.getProperty("ACTION"));
							client.setrec_range(1);
						}

						client.setdate(entry.getRequestTime());
						client.setbegin_time(entry.getRequestTime());
						client.setend_time(entry.getResponseTime());
						client.setresponse_time(entry.getResponseTime()
								- entry.getRequestTime());
						client.setsid(entry.getSessionID());
						client.setHost(entry.getIPAddress());
						client.setreferrer(entry.getRefURL());
						client.setuser_agent(entry.getUserAgent());
						client.setrid(entry.getRequestID());
						client.seturi_stem(entry.getURLStem());
						client.seturi_query(entry.getURLQuery());
						if (entry.getCustomerID() != null && !entry.getCustomerID().equals("-")) {
							client.setcust_id(entry.getCustomerID());
						}
						if (entry.getTransNum() != null) {
							client.settid(entry.getTransNum().longValue());
						}
						client.setappid(entry.getAppName());
						client.setusername(entry.getUsername());
						client.sendit();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				try {

					Thread.sleep(1500);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		}

	}

}
