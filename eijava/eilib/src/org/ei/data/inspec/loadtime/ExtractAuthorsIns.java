package org.ei.data.inspec.loadtime;

import java.io.*;
import java.lang.*;
import java.sql.*;
import java.util.*;
import org.ei.xml.*;
import org.ei.data.*;
import org.ei.util.StringUtil;

public class ExtractAuthorsIns
{

	public void extract(int load_number_begin, int load_number_end, Connection con)
		throws Exception
	{
		PrintWriter writerAuthor 	= null;
		Hashtable ausHash 			= new Hashtable();
		String[] authors = new String[1];
		PreparedStatement pstmt1 	= null;
		ResultSet rs1 				= null;

		long begin 					= System.currentTimeMillis();

		//DataCleaner cleaner = new DataCleaner();

		try
		{

			writerAuthor	= new PrintWriter(new FileWriter("ins_aus.lkp"));

			if(load_number_end == 0)
			{
				pstmt1	= con.prepareStatement(" select aus,aus2 from new_ins_master where (aus is not null) and load_number = "+load_number_begin);
				System.out.println("\n\nQuery: "+" select aus,aus2 from new_ins_master where (aus is not null) and load_number = "+load_number_begin);
			}
			else
			{
				pstmt1	= con.prepareStatement(" select aus,aus2 from new_ins_master where (aus is not null) and load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
				System.out.println("\n\nQuery: "+" select aus,aus2 from new_ins_master where (aus is not null) and load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
			}

			rs1		= pstmt1.executeQuery();
			while(rs1.next())
			{

				if((rs1.getString("aus") != null) || (rs1.getString("aus2") != null))
				{
					StringBuffer aus = new StringBuffer();
					if(rs1.getString("aus") != null)
					{
						aus.append(rs1.getString("aus"));
					}
					if(rs1.getString("aus2") != null)
					{
						aus.append(rs1.getString("aus2"));
					}

					authors = prepareAuthor(aus.toString());
				}
				for (int i = 0; i < authors.length; ++i)
				{
					String author = authors[i];
					if (author != null)
					{
						author = Entity.prepareString(author).trim().toUpperCase();
						//author = cleaner.stripBadChars(author);
				        writerAuthor.println(author.trim()+"\tins");
					}
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();;
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
			if(writerAuthor != null)
			{
				try
				{
					writerAuthor.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}

    private String[] prepareAuthor(String aString)
        throws Exception
    {

		ArrayList list = new ArrayList();
 		StringTokenizer st = new StringTokenizer(aString, InspecXMLReader.AUDELIMITER);
        String s;

		while (st.hasMoreTokens())
        {
            s = st.nextToken().trim();
		 	if(s.length() > 0)
		 	{
				if(s.indexOf(InspecXMLReader.IDDELIMITER) > -1)
				{
					 int i = s.indexOf(InspecXMLReader.IDDELIMITER);
					  s = s.substring(0,i);
				}
				s = s.trim();
				list.add(s);
		    }

        }

        return (String[])list.toArray(new String[1]);

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

