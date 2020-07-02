package org.ei.dataloading.geobase.loadtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UpdateGeobaseAffiliation
{
	public static String masterTableName = "GEO_MASTER";

	public static void main(String[] args) throws Exception
	{

		String inputName = null;
		String[] fileNameArray = null;
		UpdateGeobaseAffiliation updateAff = new UpdateGeobaseAffiliation();
		Connection con = null;
		PreparedStatement pstmt = null;
		String connectionURL = "jdbc:oracle:thin:@206.137.75.51:1521:EI";
		String driver = "oracle.jdbc.driver.OracleDriver";
		String username = "ap_ev_search";
		String password = "";
		try
		{
			if(args.length>0)
			{
				inputName = args[0];
			}
			else
			{
				System.out.println("please enter a filename");
				System.exit(1);
			}

			if(args.length>1)
			{
				masterTableName = args[1];
			}

			if(inputName.indexOf(";")>-1)
			{
				fileNameArray = inputName.split(";");
			}
			else
			{
				fileNameArray = new String[1];
				fileNameArray[0] = inputName;
			}

			con = updateAff.getConnection(connectionURL,
								driver,
								username,
								password);

			pstmt = con.prepareStatement("update geo_master set author_affiliation=? where accession_number=?");


			for(int i=0;i<fileNameArray.length;i++)
			{
				String fileName = fileNameArray[i];
				BufferedReader in = new BufferedReader(new FileReader(new File(fileNameArray[i])));
				String line = null;
				String[] result = null;
				while((line = in.readLine())!=null)
				{
					line = line.trim();
					if(line.length()>1)
					{
						updateAff.processLine(line,pstmt);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(con != null)
			{
				try
				{
					con.close();
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
		System.exit(1);
	}

	private void processLine(String line, PreparedStatement pstmt)
	{

		String accession_number = null;
		String aff = null;
		try
		{

			if(line!=null && line.indexOf("|")>-1)
			{
				int barIndex = line.indexOf("|");
				accession_number = line.substring(0,barIndex);
				aff = line.substring(barIndex);
				if((accession_number !=null && (accession_number.trim()).length()>0) &&
				   (aff !=null && (aff.trim()).length()>0))
				{
					pstmt.setString(1,aff.trim());
					pstmt.setString(2,accession_number.trim());
					pstmt.executeUpdate();
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("error: accession_number="+accession_number+" doi= "+aff+" exception: "+e.toString());;
		}

	}

	protected Connection getConnection(String connectionURL,
	                                         String driver,
	                                         String username,
	                                         String password)
	            throws Exception
	{

		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL,
										  username,
										  password);
		return con;
	}

}
