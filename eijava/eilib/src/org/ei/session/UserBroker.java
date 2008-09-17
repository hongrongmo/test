package org.ei.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.util.TimedObject;

public class UserBroker
{
	private static final String AUTH_POOL = "AuthPool";
	private TimedObject gatewayListTO;

	public String validateCustomerIP(String prodname,
								     String ipAddress)
		throws SessionException
	{
		Connection con = null;
		try
		{
			con = getConnection(AUTH_POOL);
		    String customerID = getCustomerIDByIp(ipAddress,
				                                  prodname,
				                                  con);
			if(customerID != null)
			{
				return customerID;
			}
			else
			{
				return "invalid_ip";
			}
		}
		catch(Exception e1)
		{
			throw new SessionException(e1);
		}
		finally
		{
			try
			{
				replaceConnection(con, AUTH_POOL);
			}
			catch(Exception e)
			{
				throw new SessionException(e);
			}
		}
	}

	public User getUser(String prodname,
			    		String username,
			    		String password,
			    		String ipAddress,
			    		String refURL,
			    		String entryToken)
		throws SessionException
	{

		User u = null;
		Connection con = null;

		try
		{
			long begin = System.currentTimeMillis();
			con = getConnection(AUTH_POOL);

			/*
			* First check the entry token.
			* Short circuit all other auth procedures if entry token is present
			*/

			if(entryToken != null)
			{
				System.out.println("Looking for entry token:"+entryToken);
				u = getUserByEntryToken(entryToken, con);
				u.setIpAddress(ipAddress);
				u.setUsername("_ET");
				return u;
			}

			/*
			* Next check for username and password.
			* Short circuit all other auth procedures if username and password is
			* present.
			*/

			if(username != null && password != null)
			{
				System.out.println("Looking for username and password");
				u = getUserByUsername(username,
									  password,
							          prodname,
									  con);
				if(u == null)
				{
					System.out.println("No customer found returning blank user");
					u = new User();
					u.setIpAddress(ipAddress);
					u.setUsername("_UN_FAIL");
					return u;
				}
				else
				{
					System.out.println("Found customer found returning user with customer info");
					u.setIpAddress(ipAddress);
					return u;
				}
			}

			/*
			* If entry entry token and username are not present the check refURL
			* Don't short circuit if not found.
			*/

			if(refURL != null)
			{
				System.out.println("Looking for rerURL:"+refURL);
				u = getUserByRefURL(refURL,
									prodname,
									con);
				if(u != null)
				{
					u.setIpAddress(ipAddress);
					u.setUsername("_REF");
					return u;
				}
			}

			/*
			* If we've got this far then check for IP address.
			*/

			u = getUserByIp(ipAddress,
							prodname,
							con);
			if(u != null)
			{
				u.setIpAddress(ipAddress);
				u.setUsername("_IP");
				return u;
			}


			/*
			* All forms of authentication failed so create a new User with no customer info
			*/

			u = new User();
			u.setIpAddress(ipAddress);
			u.setUsername("_FAIL");
		}
		catch(Exception e1)
		{
			throw new SessionException(e1);
		}
		finally
		{
			try
			{
				replaceConnection(con, AUTH_POOL);
			}
			catch(Exception e)
			{
				throw new SessionException(e);
			}
		}

		return u;

	}

	private User getUserByEntryToken(String entryToken,
									 Connection con)
		throws Exception
	{
		User user = new User();
		EntryTokenBroker eb = new EntryTokenBroker(con);
		EntryToken et = eb.startToken(entryToken);
		if(et != null)
		{
			user.setCustomerID("10");
			user.setContractID(Integer.toString(et.getCampaign()));
			user.setCartridge(parseCartridge(et.getCredentials(), et));
			user.setStartPage(Defaults.STARTPAGE);
			user.setDefaultDB(Defaults.DEFAULTDB);
			user.setEntryToken(et);
		}

		return user;
	}


	private User getUserByUsername(String username,
				       String password,
				       String prodname,
				       Connection con)
		throws SessionException
	{

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		User user = null;

		try
		{
			StringBuffer qBuf = new StringBuffer();
			qBuf.append("select * from user_pass_data u ");
			qBuf.append("where u.username = ? and u.password = ?  and u.prod_name = ?");
			pstmt = con.prepareStatement(qBuf.toString());
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setString(3, prodname);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				user = new User();
				user.setCustomerID(rs.getString("cust_id"));
				user.setContractID(rs.getString("contract_id"));

				if(statusOn(con,user.getContractID()))
				{
					user = populate(con, user, prodname);
					user.setUsername(username);
				}
				else
				{
					user = null;
				}

				break;
			}

		}
		catch(Exception e)
		{
			throw new SessionException(e);
		}
		finally
		{
			if(rs != null)
			{
				try{ rs.close(); }catch(Exception e1){ e1.printStackTrace(); }
			}

			if(pstmt != null)
			{
				try{ pstmt.close(); }catch(Exception e1){ e1.printStackTrace(); }
			}

		}

		return user;

	}


	private synchronized List getGatewayURLs(Connection con)
			throws Exception
	{

		ArrayList list = null;

		Statement stmt = null;
		ResultSet rs = null;

		if(this.gatewayListTO != null &&
		 !this.gatewayListTO.expired())
		{
			System.out.println("Got Ref URL from cache");
			return (List)gatewayListTO.getObject();
		}

		list = new ArrayList();

		try
		{
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from GATEWAY_DATA");
			while(rs.next())
			{
				GatewayData g = new GatewayData();
				g.setURL(rs.getString("URL"));
				g.setContractID(rs.getString("CONTRACT_ID"));
				g.setCustID(rs.getString("CUST_ID"));
				g.setProdName(rs.getString("PROD_NAME"));
				list.add(g);
			}

			TimedObject gto = new TimedObject();
			gto.setExpireIn(3600000);
			gto.setObject(list);
			gatewayListTO = gto;
		}
		finally
		{
			if(rs != null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return list;
	}


	private User getUserByRefURL(String refURL,
					     String prodname,
					     Connection con)
			throws SessionException
	{

		User user = null;

		try
		{

			List gatewayURLs = getGatewayURLs(con);
			for(int i=0; i<gatewayURLs.size(); i++)
			{
				GatewayData g = (GatewayData)gatewayURLs.get(i);

				if(g.matches(refURL, prodname))
				{
					user = new User();
					user.setCustomerID(g.getCustID());
					user.setContractID(g.getContractID());
					if(statusOn(con,user.getContractID()))
					{
						user = populate(con, user, prodname);
					}
					else
					{
						user = null;
					}

					break;
				}
			}
		}
		catch(Exception e)
		{
			throw new SessionException(e);
		}


		return user;
	}

	/**
	*	Lightweight method used to re-validate a customerID by IP address
	**/

	private String getCustomerIDByIp(String ipAddress,
							 	     String prodname,
							 	     Connection con)
		throws SessionException
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String customerID = null;
		try
		{
			StringBuffer ipBuffer = new StringBuffer();
			StringTokenizer st = new StringTokenizer(ipAddress, ".");
			while(st.hasMoreTokens())
			{
				String s = st.nextToken();
				if(s.length() == 1)
				{
					s = "00"+s;
				}
				else if(s.length() == 2)
				{
					s = "0"+s;
				}

				ipBuffer.append(s);
			}

			ipAddress = ipBuffer.toString();
			StringBuffer qBuf = new StringBuffer();

			qBuf.append("select * from ip_data i where ");
			qBuf.append("i.high_ip >= ? and i.low_ip <= ? and i.prod_name = ?");
			pstmt = con.prepareStatement(qBuf.toString());
			pstmt.setString(1, ipAddress);
			pstmt.setString(2, ipAddress);
			pstmt.setString(3, prodname);
			rs = pstmt.executeQuery();

			if(rs.next())
			{
				return rs.getString("cust_id");
			}
		}
		catch(Exception e)
		{
			throw new SessionException(e);
		}
		finally
		{
			if(rs != null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}

			if(pstmt != null)
			{
				try
				{
					pstmt.close();
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
		}

		return customerID;
	}

	private User getUserByIp(String ipAddress,
							 String prodname,
							 Connection con)
		throws SessionException
	{

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		User user = null;
		try
		{


			StringBuffer ipBuffer = new StringBuffer();
			StringTokenizer st = new StringTokenizer(ipAddress, ".");
			while(st.hasMoreTokens())
			{
				String s = st.nextToken();
				if(s.length() == 1)
				{
					s = "00"+s;
				}
				else if(s.length() == 2)
				{
					s = "0"+s;
				}

				ipBuffer.append(s);
			}


			ipAddress = ipBuffer.toString();
			StringBuffer qBuf = new StringBuffer();

			qBuf.append("select * from ip_data i where ");
			qBuf.append("i.high_ip >= ? and i.low_ip <= ? and i.prod_name = ?");
			pstmt = con.prepareStatement(qBuf.toString());
			pstmt.setString(1, ipAddress);
			pstmt.setString(2, ipAddress);
			pstmt.setString(3, prodname);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				user = new User();
				user.setCustomerID(rs.getString("cust_id"));
				user.setContractID(rs.getString("contract_id"));

				if(statusOn(con,user.getContractID()))
				{
					user = populate(con, user, prodname);
					break;
				}
				else
				{
					user = null;
				}
			}
		}
		catch(Exception e)
		{
			throw new SessionException(e);
		}
		finally
		{

			if(rs != null)
			{
				try{ rs.close(); }catch(Exception e1){ e1.printStackTrace(); }
			}

			if(pstmt != null)
			{
				try{ pstmt.close(); }catch(Exception e1){ e1.printStackTrace(); }
			}

		}

		return user;
	}


	private boolean statusOn(Connection con,
							 String contractID)
		throws Exception
	{
		boolean on = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = con.prepareStatement("select STATUS from CONTRACT_DATA where CONTRACT_ID = ?");
			pstmt.setString(1, contractID);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				String status = rs.getString("STATUS");
				if(status.equalsIgnoreCase("Y"))
				{
					on = true;
				}
			}
		}
		finally
		{
			if(rs != null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(pstmt != null)
			{
				try
				{
					pstmt.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return on;

	}

	private User populate(Connection con,
				      	  User user,
				      	  String prodname)
		throws Exception
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String carray[] = null;
		String startPage = null;
		String defaultDB = null;
		String refEmail = null;
		try
		{
			pstmt = con.prepareStatement("Select * from EV2_CUSTOMER_OPTION where CONTRACT_ID = ?");
			pstmt.setString(1, user.getContractID());
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				carray = parseCartridge(rs.getString("CARTRIDGE"));
				startPage = rs.getString("STARTPAGE");
				defaultDB = rs.getString("DEFAULTDB");
				refEmail = rs.getString("REFEMAIL");
				break;
			}


			if(carray == null)
			{
				user.setCartridge(Defaults.CARRAY);
				user.setStartPage(Defaults.STARTPAGE);
				user.setDefaultDB(Defaults.DEFAULTDB);
			}
			else
			{
				user.setCartridge(carray);
				if(startPage != null && startPage.length() > 0)
				{
					user.setStartPage(startPage);
				}
				if(defaultDB != null && defaultDB.length() > 0)
				{
					user.setDefaultDB(defaultDB);
				}
				if(refEmail != null && refEmail.length() > 0)
				{
					user.setRefEmail(refEmail);
				}
			}
		}
		finally
		{

			if(rs != null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(pstmt != null)
			{
				try
				{
					pstmt.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return user;
	}

	private String[] parseCartridge(String cartridge)
	{
		if(cartridge.charAt(cartridge.length() - 1) == ';')
		{
			cartridge = cartridge.substring(0, cartridge.length() - 1);
		}

		StringTokenizer tokens = new StringTokenizer(cartridge,";");
		String[] carray = new String[tokens.countTokens()];
		int x = 0;
		while(tokens.hasMoreTokens())
		{
			carray[x] = tokens.nextToken();
			++x;
		}

		return carray;
	}

	private String[] parseCartridge(String cartridge,
									EntryToken entryToken)
	{
		StringTokenizer tokens = new StringTokenizer(cartridge,";");
		String[] carray = new String[tokens.countTokens()+1];
		int x = 0;
		while(tokens.hasMoreTokens())
		{
			carray[x] = tokens.nextToken();
			++x;
		}

		StringBuffer buf = new StringBuffer();
		buf.append("PPD~");
		buf.append(Long.toString(entryToken.getStartTime()));
		buf.append("~");
		buf.append(Long.toString(entryToken.getSessionLength()));
		carray[x] = buf.toString();


		return carray;
	}



	private Connection getConnection(String authCon)
			throws Exception
	{
		ConnectionBroker broker = ConnectionBroker.getInstance();
		return broker.getConnection(authCon);
	}

	private void replaceConnection(Connection con, String authCon)
		throws Exception
	{
		ConnectionBroker broker = ConnectionBroker.getInstance();
		broker.replaceConnection(con, authCon);
	}

	class GatewayData
	{
		private String URL;
		private String prodName;
		private String contractID;
		private String custID;

		public void setURL(String URL)
		{
			this.URL = URL;
		}

		public String getURL()
		{
			return this.URL;
		}

		public void setProdName(String prodName)
		{
			this.prodName = prodName;
		}

		public String getProdName()
		{
			return this.prodName;
		}

		public String getContractID()
		{
			return this.contractID;
		}

		public void setContractID(String contractID)
		{
			this.contractID = contractID;
		}

		public String getCustID()
		{
			return this.custID;
		}

		public void setCustID(String custID)
		{
			this.custID = custID;
		}

		public boolean matches(String refURL, String pName)
		{
			boolean b = false;
			int i = URL.length();
			char c1 = URL.charAt(0);
			char c2 = URL.charAt(i-1);

			if(!prodName.equals(pName))
			{
				return false;
			}

			if(c1 == '%' && c2 == '%')
			{

				String s =  URL.substring(1, i-1);
				if(refURL.toLowerCase().indexOf(s.toLowerCase()) > -1)
				{
					b = true;
				}
				else
				{
					b = false;
				}

			}
			else if(c1 != '%' && c2 == '%')
			{

				String s = URL.substring(0, i-1);
				if(refURL.toLowerCase().indexOf(s.toLowerCase()) == 0)
				{
					b = true;
				}
				else
				{
					b = false;
				}
			}
			else if(c1 != '%' && c2 != '%')
			{
				if(URL.equalsIgnoreCase(refURL))
				{
					b = true;
				}
				else
				{
					b = false;
				}
			}

			return b;
		}
	}
}