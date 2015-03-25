package org.ei.dataloading.ntis.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;

public class ExtractAuAffNtis
{

	private Perl5Util perl = new Perl5Util();

  public void extract(int load_number_begin, int load_number_end, Connection con)
    throws Exception
  {
    PrintWriter writerAuAff = null;
    PreparedStatement pstmt1  = null;
    ResultSet rs1         = null;
    String authorAff = null;
    String institute_name = null;
    String source = null;
    String display_name = null;
    int idx1 = 1;

    try
    {
      writerAuAff = new PrintWriter(new FileWriter("ntis_af.lkp"));
      if(load_number_begin == 0)
      {
        pstmt1  = con.prepareStatement("select so from ntis_master where so is not null");
      }
      else if(load_number_end == 0)
      {
        pstmt1  = con.prepareStatement("select so from ntis_master where so is not null and load_number = ?");
        pstmt1.setInt(idx1++,load_number_begin);
      }
      else
      {
        pstmt1  = con.prepareStatement("select so from ntis_master where so is not null and load_number >= ? and load_number <= ?");
        pstmt1.setInt(idx1++,load_number_begin);
        pstmt1.setInt(idx1++,load_number_end);
      }
      rs1   = pstmt1.executeQuery();
      while(rs1.next())
      {
        	authorAff  = rs1.getString("so").trim();
        	if(authorAff != null)
        	{
		    	// if there are performers
		    	if(perl.match("#\\*\\*#", authorAff)) {
		    		source = perl.substitute("s#\\*\\*#;#g", authorAff);
		    	}
		    	else
		    	{
		    		source = authorAff;
		    	}
		    	// if there are sponsors
		    	if(perl.match("#\\*#",source))
		    	{
		      		int intStarIndex = source.indexOf("*");
		    		institute_name = source.substring(0, intStarIndex);
		    	}
		    	else
		    	{
		    		institute_name = source;
		    	}

            	institute_name = perl.substitute("s#\\.# #g", institute_name);
            	institute_name = perl.substitute("s#,# #g", institute_name);
            	institute_name = perl.substitute("s#\\s+# #g", institute_name);
            	StringTokenizer st1 = new StringTokenizer(institute_name,";",false);
				while (st1.hasMoreTokens())
				{
		  			display_name   = st1.nextToken();
            		writerAuAff.println(display_name.toUpperCase().trim() +"	nti");
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
      if(writerAuAff != null)
      {
        try
        {
          writerAuAff.close();
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
      }
    }

  }

}


