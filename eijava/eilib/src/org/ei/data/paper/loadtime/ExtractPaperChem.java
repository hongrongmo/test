package org.ei.data.paper.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Clob;
import org.ei.util.*;
import org.ei.data.bd.loadtime.*;

public class ExtractPaperChem
{
	public static void main(String[] args) throws Exception
	{
		String[] m_ids = new String[]{"pch_B9CB8C083E7510C6E03408002081DCA4","pch_B9CB8C03873410C6E03408002081DCA4","pch_B9CB8C08184610C6E03408002081DCA4","pch_B9CB8C07B53010C6E03408002081DCA4","pch_B9CB8C0806B010C6E03408002081DCA4","pch_B9CB8C0806B110C6E03408002081DCA4"};
		Connection con = getDbCoonection("jdbc:oracle:thin:@neptune.elsevier.com:1521:EI", "AP_PRO1", "ei3it", "oracle.jdbc.driver.OracleDriver");
		ExtractPaperChem epc = new ExtractPaperChem();
		epc.extract(m_ids,con);
	}

    public void extract(String[] m_ids, Connection con)
        throws Exception
    {
        PrintWriter writerPub   = null;

        PreparedStatement pstmt1    = null;
        ResultSet rs1               = null;

        long begin          = System.currentTimeMillis();

        try
        {
			String midsList = "(";
			for(int i = 0; i < m_ids.length; i++)
			{
				midsList += "'" + m_ids[i] + "'";
				if(i != m_ids.length-1)
					midsList += ",";
			}

			midsList += ")";

            writerPub   = new PrintWriter(new FileWriter("paperchem_extract.sql"));

			String sqlQuery = " select * from paper_master where m_id in "+midsList;
            pstmt1  = con.prepareStatement(sqlQuery);
            System.out.println("\n\nQuery: "+sqlQuery);


            rs1     = pstmt1.executeQuery();

            while(rs1.next())
            {
				writeColumn(rs1, "m_id", writerPub);
				writeColumn(rs1, "ex", writerPub);
				writeColumn(rs1, "ab", writerPub);
				writeColumn(rs1, "au", writerPub);
				writeColumn(rs1, "media", writerPub);
				writeColumn(rs1, "csess", writerPub);
				writeColumn(rs1, "patno", writerPub);
				writeColumn(rs1, "pling", writerPub);
				writeColumn(rs1, "appln", writerPub);
				writeColumn(rs1, "prior_num", writerPub);
				writeColumn(rs1, "dt", writerPub);
				writeColumn(rs1, "assig", writerPub);
				writeColumn(rs1, "pcode", writerPub);
				writeColumn(rs1, "claim", writerPub);
				writeColumn(rs1, "sourc", writerPub);
				writeColumn(rs1, "nofig", writerPub);
				writeColumn(rs1, "notab", writerPub);
				writeColumn(rs1, "sub_index", writerPub);
				writeColumn(rs1, "specn", writerPub);
				writeColumn(rs1, "suppl", writerPub);
                writerPub.println();
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
            if(writerPub != null)
            {
                try
                {
                    writerPub.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }


	public void writeColumn(ResultSet rs1, String columnName, PrintWriter writerPub) throws Exception
	{
		String column = null;
		if(columnName.equals("ab"))
		{
			Clob clob = rs1.getClob("AB");
			if(clob != null)
			{
				column = StringUtil.getStringFromClob(clob);
			}
		}
		else
		{
			column   = rs1.getString(columnName);
			if(columnName.equals("au"))
			{
				column = formatAuthor(column);
			}
		}

		if(column != null)
		{
			writerPub.print(column + "\t");
		}
		else
			writerPub.print("\t");

	}

	public String formatAuthor(String column)
	{
		String lastName = null;
		String fullname = null;
		String givenName = null;
		String[] columnArray = null;
		StringBuffer nameBuffer = new StringBuffer();
		if(column != null)
		{
			if(column.indexOf(";")>-1)
			{
				columnArray = column.split(";",-1);
			}
			else
			{
				columnArray = new String[1];
				columnArray[0] = column;
			}

			for(int i=0;i<columnArray.length;i++)
			{
				fullname = columnArray[i];
				if(fullname.indexOf(",")>-1)
				{
					lastName  = fullname.substring(0,fullname.indexOf(",")-1);
					givenName = fullname.substring(fullname.indexOf(",")+1);
				}
				nameBuffer.append(BdParser.IDDELIMITER);//sec
				nameBuffer.append(i);
				nameBuffer.append(BdParser.IDDELIMITER);//auid
				nameBuffer.append("1");
				nameBuffer.append(BdParser.IDDELIMITER);//afid
				nameBuffer.append(BdParser.IDDELIMITER);//indexname
				nameBuffer.append(BdParser.IDDELIMITER);//initials
				nameBuffer.append(lastName);
				nameBuffer.append(BdParser.IDDELIMITER);//Surname
				nameBuffer.append(BdParser.IDDELIMITER);//Degrees
				nameBuffer.append(givenName);
				nameBuffer.append(BdParser.IDDELIMITER);//givenName
				nameBuffer.append(BdParser.IDDELIMITER);//Suffix
				nameBuffer.append(BdParser.IDDELIMITER);//Nametext
				nameBuffer.append(BdParser.IDDELIMITER);//PrefnameInitials
				nameBuffer.append(BdParser.IDDELIMITER);//PrefnameIndexedname
				nameBuffer.append(BdParser.IDDELIMITER);//PrefnameDegrees
				nameBuffer.append(BdParser.IDDELIMITER);//PrefnameSurname
				nameBuffer.append(BdParser.IDDELIMITER);//PrefnameGivenname
				nameBuffer.append(BdParser.IDDELIMITER);//Eaddress
				nameBuffer.append(BdParser.AUDELIMITER);
			}

		}
		return nameBuffer.toString();
	}


  public static Connection getDbCoonection(String url,String username,String password, String driver)
    throws Exception
  {
    Class.forName(driver).newInstance();
    Connection con  = DriverManager.getConnection(url, username, password);
    return con;
  }

}

