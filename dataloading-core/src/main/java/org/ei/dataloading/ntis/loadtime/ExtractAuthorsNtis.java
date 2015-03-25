package org.ei.dataloading.ntis.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import org.ei.common.ntis.*;


public class ExtractAuthorsNtis
{

  public void extract(int load_number_begin, int load_number_end, Connection con)
    throws Exception
  {
    PrintWriter writerAuthor  = null;
    PreparedStatement pstmt1  = null;
    ResultSet rs1         = null;
   // long begin          = System.currentTimeMillis();
    int idx = 1;
    StringTokenizer st1 = null;
    int countTokens1 = 0;
    String aus1 = null;
    String aus2 = null;
    String aus3 = null;
    String aus4 = null;
    String aus5 = null;
    String aus6 = null;
    String aus = null;
    String display_name = null;
    int counter = 1;
    try
    {
      writerAuthor  = new PrintWriter(new FileWriter("ntis_aus.lkp"));
      if(load_number_begin == 0)
      {
        pstmt1  = con.prepareStatement(" select pa1,pa2,pa3,pa4,pa5,hn from ntis_master where pa1 is not null");
        System.out.println("counteR"+counter);
      }
      else if(load_number_end == 0)
      {
        pstmt1  = con.prepareStatement(" select pa1,pa2,pa3,pa4,pa5,hn from ntis_master where pa1 is not null and load_number = ?");
        pstmt1.setInt(idx++,load_number_begin);
      }
      else
      {
        pstmt1  = con.prepareStatement(" select pa1,pa2,pa3,pa4,pa5,hn from ntis_master where pa1 is not null and load_number >= ? and load_number <= ?");
        pstmt1.setInt(idx++,load_number_begin);
        pstmt1.setInt(idx++,load_number_end);
      }
      rs1   = pstmt1.executeQuery();
      while(rs1.next())
      {
        aus1 = rs1.getString("pa1");
        aus2 = rs1.getString("pa2");
        aus3 = rs1.getString("pa3");
        aus4 = rs1.getString("pa4");
        aus5 = rs1.getString("pa5");
        aus6 = rs1.getString("hn");
        aus = NTISAuthor.formatAuthors(aus1,aus2,aus3,aus4,aus5,aus6);
        if(aus != null)
        {
          st1 = new StringTokenizer(aus,";",false);
          countTokens1 = st1.countTokens();
		  while (st1.hasMoreTokens())
		  {
		    display_name   = st1.nextToken().trim().toUpperCase();

			  writerAuthor.println(display_name.replace('*',' ').trim()+"\tnti");
		  }
        }
      }

    }
    catch(SQLException e)
    {
      e.printStackTrace();
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

}


