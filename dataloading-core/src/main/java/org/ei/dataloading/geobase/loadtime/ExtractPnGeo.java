package org.ei.dataloading.geobase.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.ei.xml.Entity;
import org.ei.dataloading.LoadLookup;


public class ExtractPnGeo
{
	public void extract(int load_number_begin, int load_number_end, Connection con,String dbname)
		throws Exception
	{
		PrintWriter writerPn 		= null;
		PreparedStatement pstmt1 	= null;
		ResultSet rs1 				= null;

		try
		{

			writerPn	= new PrintWriter(new FileWriter(dbname+"_Pn.lkp"));

			if(load_number_end == 0)
			{
				pstmt1	= con.prepareStatement(" select publisher_name from "+dbname+"_master where (publisher_name is not null) and load_number = "+load_number_begin);
				System.out.println("\n\nQuery: "+" select publisher_name from "+dbname+"_master where (publisher_name is not null) and load_number = "+load_number_begin);
			}
			else
			{
				pstmt1	= con.prepareStatement(" select publisher_name from "+dbname+"_master where (publisher_name is not null) and load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
				System.out.println("\n\nQuery: "+" select publisher_name from "+dbname+"_master where (publisher_name is not null) and load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
			}

			rs1		= pstmt1.executeQuery();

			while(rs1.next())
			{
				String pnString = rs1.getString("publisher_name");
				if(pnString != null)
				{
					pnString 			= Entity.prepareString(pnString);

					pnString			= LoadLookup.removeSpecialCharacter(pnString);

					writerPn.println(pnString+"\t"+dbname.toLowerCase()+"\t");
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
			if(writerPn != null)
			{
				try
				{
					writerPn.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}

}

