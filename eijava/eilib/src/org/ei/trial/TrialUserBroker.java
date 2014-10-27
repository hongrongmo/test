package org.ei.trial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.Collection;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;
import org.ei.util.GUID;

public class TrialUserBroker
{
	private static final String SESSION_POOL = DatabaseConfig.SESSION_POOL;
	public void addTrialUser(TrialUser tu)
			throws TrialException
	{
		System.out.println("Accessing trial.TrialUserBroker");
		Connection con = null;
		//PreparedStatement pstmt = null;
		CallableStatement proc = null;

		try
		{
			con = getConnection(SESSION_POOL);
			proc = (CallableStatement)con.prepareCall("{ call TrialUserBroker_addTrialUser(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			proc.setString(1,new GUID().toString());
			proc.setString(2,checkCharacter(tu.getFirstName()));
			proc.setString(3,checkCharacter(tu.getLastName()));
			proc.setString(4,checkCharacter(tu.getJobTitle()));
			proc.setString(5,checkCharacter(tu.getCompany()));
			proc.setString(6,checkCharacter(tu.getAddress1()));
			proc.setString(7,checkCharacter(tu.getAddress2()));
			proc.setString(8,checkCharacter(tu.getWebSite()));
			proc.setString(9,checkCharacter(tu.getCity()));
			proc.setString(10,checkCharacter(tu.getState()));
			proc.setString(11,checkCharacter(tu.getZip()));
			proc.setString(12,tu.getCountry());
			proc.setString(13,checkCharacter(tu.getPhone()));
			proc.setString(14,checkCharacter(tu.getEmail()));
			proc.setString(15,tu.getHowHear());
			proc.setString(16,checkCharacter(tu.getHowHearExplained()));
			proc.setString(17,tu.getProduct());
			proc.setString(18,tu.getByMail());
			proc.setString(19,tu.getByEmail());
			//proc.setString(20,tu.getReferringURL());
			proc.setString(20, checkCharacter(tu.getReferringURL()));
			proc.setString(21,tu.getTrialDate());

			proc.execute();


		}
		catch(Exception e)
		{
			System.out.println("exception "+e);
			throw new TrialException(e);
		}
		finally
		{
			if(proc != null)
			{
				try
				{
					proc.close();
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
			if(con != null)
			{
				try
				{
					replaceConnection(con, SESSION_POOL);
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}



	public Collection getTrialAccount(java.sql.Date startDate,java.sql.Date endDate) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection allTrialAccount = new ArrayList();

		try
		{
			conn = getConnection(SESSION_POOL);
			if(startDate==null || endDate==null)
			{
				pstmt = conn.prepareStatement("SELECT a.*,to_char(a.time_stamp,'yyyy-mm-dd,hh:mi:ss') time FROM TRIAL_USER a ORDER BY a.TIME_STAMP DESC");
			}
			else
			{
				pstmt = conn.prepareStatement("SELECT a.*,to_char(a.time_stamp,'yyyy-mm-dd,hh:mi:ss') time FROM TRIAL_USER a WHERE a.TIME_STAMP>=? and a.TIME_STAMP<=? ORDER BY a.TIME_STAMP DESC");
				pstmt.setDate(1, startDate);
				pstmt.setDate(2, endDate);
			}
			rs = pstmt.executeQuery();

			TrialUser aUser = null;
			while(rs.next())
			{
				aUser = new TrialUser();
				aUser.setIndexID(rs.getString("INDEX_ID"));
				aUser.setFirstName(rs.getString("FIRST_NAME"));
				aUser.setLastName(rs.getString("LAST_NAME"));
				aUser.setJobTitle(rs.getString("JOB_TITLE"));
				aUser.setCompany(rs.getString("COMPANY"));
				aUser.setAddress1(rs.getString("ADDRESS1"));
				aUser.setAddress2(rs.getString("ADDRESS2"));
				aUser.setWebSite(rs.getString("WEB_SITE"));
				aUser.setCity(rs.getString("CITY"));
				aUser.setState(rs.getString("STATE"));
				aUser.setZip(rs.getString("ZIP"));
				aUser.setCountry(rs.getString("COUNTRY"));
				aUser.setPhone(rs.getString("PHONE_NUMBER"));
				aUser.setEmail(rs.getString("EMAIL_ADDRESS"));
				aUser.setHowHear(rs.getString("HOW_HEAR"));
				aUser.setHowHearExplained(rs.getString("SOURCE"));
				aUser.setProduct(rs.getString("PRODUCT"));
				aUser.setByMail(rs.getString("BY_MAIL"));
				aUser.setByEmail(rs.getString("BY_EMAIL"));
				aUser.setReferringURL(rs.getString("REFERRING_URL")==null?"No URL":rs.getString("REFERRING_URL"));
				aUser.setTrialDate(rs.getString("TIME"));
				allTrialAccount.add(aUser);
			}
		}

		finally
		{
			if(rs !=null)
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
			if(conn != null)
			{
				try
				{
					replaceConnection(conn, SESSION_POOL);
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
		}

		return allTrialAccount;
	}

	private String checkCharacter(String inString) throws Exception
	{
		if((inString != null)&&(inString.length()>0))
		{
			for(int i=0;i<inString.length();i++)
			{
				char c = inString.charAt(i);
				//System.out.println("char "+c);
				if((c<32)||(c>127))
				{
					throw new Exception("Invalid Character");
				}
			}
		}
		return inString;
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
}
