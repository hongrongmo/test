package org.ei.data.inspec.loadtime;

import java.io.*;
import java.lang.*;
import java.sql.*;
import java.util.*;
import org.ei.xml.*;

public class ExtractStIbf
{

	public void extract(int load_number_begin, int load_number_end, Connection con)
		throws Exception
	{
		PrintWriter writerSt 	= null;
		Hashtable stHash 		= new Hashtable();

		PreparedStatement pstmt1 	= null;
		ResultSet rs1 				= null;

		long begin 		= System.currentTimeMillis();

   		String fjt			= null;
		String thlp			= null;
   		String source_title	= null;
  		String coden		= null;
  		String issn			= null;
  		String isbn			= null;

		try
		{
			writerSt	= new PrintWriter(new FileWriter("ibf_st.lkp"));

			if(load_number_end == 0)
			{
				pstmt1	= con.prepareStatement(" select fjt from ibf_master where (fjt is not null) and load_number = "+load_number_begin);
				System.out.println("\n\nQuery: "+" select fjt,sn from ibf_master where (fjt is not null) and load_number = "+load_number_begin);
			}
			else
			{
				pstmt1	= con.prepareStatement(" select fjt from ibf_master where (fjt is not null)  and load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
				System.out.println("\n\nQuery: "+" select fjt from ibf_master where (fjt is not null)  and load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
			}

			rs1		= pstmt1.executeQuery();

			while(rs1.next())
			{
  				fjt		= rs1.getString("fjt");
  				//thlp	= rs1.getString("thlp");
  				//issn	= rs1.getString("sn");

				if(fjt != null)
				{
					source_title	= fjt.trim();
				}
				//else if(thlp != null)
				//{
				//	source_title	= thlp.trim();
				//}

				if(source_title != null)
				{
					source_title = Entity.prepareString(source_title);
					source_title = source_title.trim().toUpperCase();

					if(!stHash.containsKey(source_title))
					{
						if(issn == null)
						{
							issn = "";
						}
						stHash.put(source_title, issn);
					}
				}
			}

			Iterator itrTest = stHash.keySet().iterator();

			for(int i = 0; itrTest.hasNext(); i++)
			{
				source_title	= (String)itrTest.next();
				issn			= (String)stHash.get(source_title);

				writerSt.println(issn.trim()+"\t"+source_title+"\tibf\t");
			}

			stHash.clear();

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
			if(writerSt != null)
			{
				try
				{
					writerSt.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}

}

