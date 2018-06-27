package org.ei.dataloading.geobase.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import org.ei.dataloading.LoadLookup;
import org.ei.xml.Entity;

public class ExtractAuAffGeo
{
	public static final String AUDELIMITER = new String(new char[] {30});
	public static final String IDDELIMITER = new String(new char[] {29});
	public static final String GROUPDELIMITER = new String(new char[] {02});

	public void extract(int load_number_begin, int load_number_end, Connection con,String dbname)
		throws Exception
	{
		PrintWriter writerAuthorAff	= null;
		PreparedStatement pstmt1 	= null;
		ResultSet rs1 				= null;

		long begin 					= System.currentTimeMillis();

		try
		{

			writerAuthorAff	= new PrintWriter(new FileWriter(load_number_begin+dbname+"_ausAff.lkp"));

			if(load_number_end == 0)
			{
				pstmt1	= con.prepareStatement(" select author_affiliation from "+dbname+"_master where (author_affiliation is not null) and load_number = "+load_number_begin);
				System.out.println("\n\nQuery: "+" select author_affiliation from "+dbname+"_master where (author_affiliation is not null) and load_number = "+load_number_begin);
			}
			else
			{
				pstmt1	= con.prepareStatement(" select author_affiliation from "+dbname+"_master where (author_affiliation is not null) and load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
				System.out.println("\n\nQuery: "+" select author_affiliation from "+dbname+"_master where (author_affiliation is not null) and load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
			}

			rs1		= pstmt1.executeQuery();

			String[] authorAffGroupArray = null;
			String[] authorAffArray = null;

			while(rs1.next())
			{
				String authorAffiliation = rs1.getString("author_affiliation");

				if(authorAffiliation != null)
				{
					if(authorAffiliation.indexOf(GROUPDELIMITER)>-1)
					{
						authorAffGroupArray = authorAffiliation.split(GROUPDELIMITER);
					}
					else
					{
						authorAffGroupArray = new String[1];
						authorAffGroupArray[0] = authorAffiliation;
					}
					for(int i=0;i<authorAffGroupArray.length;i++)
					{
						String authorAffGroupString = authorAffGroupArray[i];
						if(authorAffGroupString.indexOf(IDDELIMITER)>-1)
						{
							authorAffGroupString = authorAffGroupString.substring(authorAffGroupString.indexOf(IDDELIMITER)+1);
						}
						StringTokenizer st1 = new StringTokenizer(authorAffGroupString,AUDELIMITER,false);
						int countTokens1 = st1.countTokens();

						if (countTokens1 > 0)
						{
							while (st1.hasMoreTokens())
							{
								String display_name		= st1.nextToken().trim().toUpperCase();
								display_name 			= Entity.prepareString(display_name);
								display_name			= LoadLookup.removeSpecialCharacter(display_name);
								writerAuthorAff.println(display_name+"\t"+dbname.toLowerCase()+"\t");
							}
						}
					}

				}
			}

		}
		finally
		{
			if(rs1 != null)
			{
				try
				{
					rs1.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			if(pstmt1 != null)
			{
				try
				{
					pstmt1.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			if(writerAuthorAff != null)
			{
				try
				{
					writerAuthorAff.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}
}

