package org.ei.logging;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * Main class that does logging to a database. It kicks off a thread and
 * continually checks a queue for new messages. On shutdown,
 * 
 * @author harovetm
 * 
 */
public class CLFLogger {
	private Logger clflogger = Logger.getLogger("CLFLogger");
	private Logger log4j = Logger.getLogger(CLFLogger.class);

	private static Context envcontext;
	private CLFLogConsumer consumer;
	private LinkedList<CLFMessage> logQueue = new LinkedList<CLFMessage>();
	private long lastChecked;

	public CLFLogger() throws Exception {
		consumer = new CLFLogConsumer();
		this.lastChecked = 0L;
		consumer.start();
	}

	public synchronized void enqueue(CLFMessage message) {
		logQueue.add(message);
	}

	public synchronized CLFMessage[] dequeue() {
		long time = System.currentTimeMillis();
		if (this.lastChecked == 0 || time - this.lastChecked > 600000) {
			Date date = new Date(time);
			log4j.info("Checking the log queue, SIZE:" + logQueue.size() + " DATE-TIME: " + date.toString() + " UNIX-TIME: " + Long.toString(time));
			this.lastChecked = time;
		}

		CLFMessage[] messages = new CLFMessage[logQueue.size()];
		for (int i = 0; i < messages.length; i++) {
			messages[i] = (CLFMessage) logQueue.get(i);
		}
		logQueue.clear();

		return messages;
	}

	public void shutdown() {
		this.consumer.shutdown();
		try {
			this.consumer.join();
		} catch (InterruptedException e) {
			// Ignore subsequent interrupts
		}
	}

	class CLFLogConsumer extends Thread {
		private boolean shutdown = false;

		public void shutdown() {
			log4j.info("Shutting down log thread...");
			this.shutdown = true;
		}

		public CLFLogConsumer() throws IOException {
			log4j.info("Creating log thread...");
		}

		public void run() {
			log4j.info("Starting log thread...");

			Connection con = null;
			DataSource ds = null;

			//
			// Get the datasource and connection to use for the loop below
			//
			try {
				Context initialcontext = new InitialContext();
				envcontext = (Context) initialcontext.lookup("java:comp/env");
				ds = (DataSource) envcontext.lookup("jdbc/village_log");
			} catch (Throwable t) {
				throw new RuntimeException("Unable to connect to jdbc/village_log!", t);
			}

			try {

				//
				// Start loop to write entries. Only stop when shutdown
				// flag is set (@see shutdown() method)
				//
				while (!shutdown) {
					CLFMessage[] messages = dequeue();
					for (int x = 0; x < messages.length; ++x) {
						CLFMessage message = messages[x];

						try {
							con = ds.getConnection();
							ALSSqlHandler als = new ALSSqlHandler();
							als.writeMessage(message, con);
						} catch (Exception e1) {
							log4j.error("Unable to write message to 'village_log'", e1);
						} finally {
							if (con != null) {
								try {
									con.close();
								} catch (Exception e2) {
									log4j.error("Unable to close connection for 'village_log'", e2);
								}
							}
						}

						try {
							// Write at WARN level to special logger
							clflogger.warn(message.getCLFMessage());
						} catch (Exception e) {
							log4j.error("Unable to write data to 'village_log'", e);
						}
					}

					try {
						Thread.sleep(1500);
					} catch (Exception e1) {
						log4j.error("Unable to sleep in log thread", e1);
					}
				}

			} catch (Throwable t) {
				log4j.error("Error has occurred during File Logging!", t);
			}
		}
	}
}
