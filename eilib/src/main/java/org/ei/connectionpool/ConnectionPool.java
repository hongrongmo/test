package org.ei.connectionpool;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;

import com.javaexchange.dbConnectionBroker.DbConnectionBroker;

@Deprecated
public class ConnectionPool
{
	private DbConnectionBroker broker;
	private String url;
	private String driver;
	private String username;
	private String password;
	private int minConnections;
	private int maxConnections;
	private double refreshInterval;
	private String logFile;


	public String getLogFile()
	{
		return this.logFile;
	}

	public void setLogFile(String logFile)
	{
		this.logFile = logFile;
	}

	public double getRefreshInterval()
	{
		return this.refreshInterval;
	}

	public void setRefreshInterval(double refreshInterval)
	{
		this.refreshInterval = refreshInterval;
	}


	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getUrl()
	{
		return this.url;
	}

	public void setDriver(String driver)
	{
		this.driver = driver;
	}

	public String getDriver()
	{
		return this.driver;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getUsername()
	{
		return this.username;
	}

	public String getPassword()
	{
		return this.password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public int getMinConnections()
	{
		return this.minConnections;
	}

	public void setMinConnections(int minConnections)
	{
		this.minConnections = minConnections;
	}

	public int getMaxConnections()
	{
		return this.maxConnections;
	}

	public void setMaxConnections(int maxConnections)
	{
		this.maxConnections = maxConnections;
	}


	public void initialize()
		throws ConnectionPoolException
	{
		try
		{
			broker = new DbConnectionBroker(this.driver,
							this.url,
							this.username,
							this.password,
							this.minConnections,
							this.maxConnections,
							this.logFile,
							this.refreshInterval);
		}
		catch(Exception e)
		{
			System.out.println("Pool:"+ e.getMessage());
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try
			{
				PrintStream out = new PrintStream(bout);
				e.printStackTrace(out);
			}
			catch(Exception e2)
			{
				System.out.println("Error in printing stack trace");
			}

			Exception e1 = new Exception(e.getMessage() + ":::::" + new String(bout.toByteArray()));
			throw new ConnectionPoolException(e1);
		}

	}

	public Connection getConnection()
		throws NoConnectionAvailableException
	{
		Connection c = broker.getConnection();
		if(c == null)
		{
			throw new NoConnectionAvailableException("Out of connections!");
		}
		return c;

	}


	public void closeConnections()
		throws ConnectionPoolException
	{
		try
		{
			broker.destroy(100);
		}
		catch(Exception e)
		{
			throw new ConnectionPoolException(e);
		}
	}


	public void replaceConnection(Connection c)
		throws ConnectionPoolException
	{
		try
		{
			c.setAutoCommit(true);
			broker.freeConnection(c);
		}
		catch(Exception e)
		{
			throw new ConnectionPoolException(e);
		}
	}

}
















