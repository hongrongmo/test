package org.ei.dataloading.geobase.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.ei.dataloading.LoadLookup;
import org.ei.util.StringUtil;
import org.ei.xml.Entity;

public class ExtractStGeo
{
	public static final String AUDELIMITER = new String(new char[] {30});
	public static final String IDDELIMITER = new String(new char[] {29});
	public static final String GROUPDELIMITER = new String(new char[] {02});

	public void extract(int load_number_begin, int load_number_end, Connection con,String dbname)
		throws Exception
	{
		PrintWriter writerSt 		= null;
		PreparedStatement pstmt1 	= null;
		ResultSet rs1 				= null;

		long begin 					= System.currentTimeMillis();

		try
		{

			writerSt	= new PrintWriter(new FileWriter(dbname+"_st.lkp"));

			if(load_number_end == 0)
			{
				pstmt1	= con.prepareStatement(" select source_title from "+dbname+"_master where (source_title is not null) and load_number = "+load_number_begin);
				System.out.println("\n\nQuery: "+" select source_title from "+dbname+"_master where (source_title is not null) and load_number = "+load_number_begin);
			}
			else
			{
				pstmt1	= con.prepareStatement(" select source_title from "+dbname+"_master where (source_title is not null) and load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
				System.out.println("\n\nQuery: "+" select source_title from "+dbname+"_master where (source_title is not null) and load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
			}

			rs1		= pstmt1.executeQuery();

			String[] stArray = null;

			while(rs1.next())
			{
				String stString = rs1.getString("source_title");

				if(stString != null)
				{
					if(stString.indexOf(AUDELIMITER)>-1)
					{
						stArray = stString.split(AUDELIMITER);
					}
					else
					{
						stArray = new String[1];
						stArray[0] = stString;
					}

					for(int i=0;i<stArray.length;i++)
					{
						String singleStString = stArray[i];

						String display_name		= singleStString.trim().toUpperCase();
						display_name 			= Entity.prepareString(display_name);
						display_name			= LoadLookup.removeSpecialCharacter(display_name);
						writerSt.println(display_name+"\t"+dbname.toLowerCase()+"\t");

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

	private String getIndexedAuthor(String author)
	{
		String Iauthor 			= new String();
		StringUtil stringUtil 	= new StringUtil();

		//` ~ ! @ # $ % ^ & * ( ) - _ = + [ { ] } \ | ; : ' " , < . > / ?
		//~ ! @ # % ^ + = ` : | , . < > [ ] - '
		Iauthor = author.replace('~', ' ');
		Iauthor = Iauthor.replace('!', ' ');
		Iauthor = Iauthor.replace('@', ' ');
		Iauthor = Iauthor.replace('#', ' ');
		Iauthor = Iauthor.replace('$', ' ');
		Iauthor = Iauthor.replace('%', ' ');
		Iauthor = Iauthor.replace('^', ' ');
		Iauthor = Iauthor.replace('&', ' ');
		Iauthor = Iauthor.replace('*', ' ');
		Iauthor = Iauthor.replace('+', ' ');
		Iauthor = Iauthor.replace('`', ' ');
		Iauthor = Iauthor.replace(':', ' ');
		Iauthor = Iauthor.replace('|', ' ');
		Iauthor = Iauthor.replace('<', ' ');
		Iauthor = Iauthor.replace('>', ' ');
		Iauthor = Iauthor.replace('[', ' ');
		Iauthor = Iauthor.replace(']', ' ');
		Iauthor = Iauthor.replace('\'', ' ');

		Iauthor = stringUtil.replace(Iauthor,",", " ",1,4);

		Iauthor = Iauthor.replace('.', ' ');
		Iauthor = Iauthor.replace('-', ' ');

		Iauthor = Iauthor.trim();

		while(Iauthor.indexOf("  ") > -1)
		{
			Iauthor = stringUtil.replace(Iauthor,"  ", " ",1,4);
		}

		Iauthor = Iauthor.replace(' ', '9');

		return Iauthor;

	}

}

