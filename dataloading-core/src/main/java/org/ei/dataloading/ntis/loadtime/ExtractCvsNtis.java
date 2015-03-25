package org.ei.dataloading.ntis.loadtime;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

public class ExtractCvsNtis
{

  public void extract(int load_number_begin, int load_number_end, Connection con)
    throws Exception
  {
    PrintWriter writerCvs   = null;
    PrintWriter writerCvs_unbalance   = null;
    PreparedStatement pstmt1  = null;
    ResultSet rs1         = null;
    int idx1 = 1;
    StringTokenizer st1 = null;
    int countTokens1 = 0 ;
    String cvs = null;
    String index_term = null;
    int counter = 1;

    try
    {
      writerCvs = new PrintWriter(new FileWriter("ntis_cvs.lkp"));
      //writerCvs_unbalance = new PrintWriter(new FileWriter("ntis_cvs_unbalance.lkp"));

      if(load_number_begin == 0)
      {
        pstmt1  = con.prepareStatement(" select des from ntis_master where des is not null");
      }
      else if(load_number_end == 0)
      {
        pstmt1  = con.prepareStatement(" select des from ntis_master where des is not null and load_number = ?");
        pstmt1.setInt(idx1++,load_number_begin);
      }
      else
      {
        pstmt1  = con.prepareStatement(" select des from ntis_master where des is not null and load_number >= ? and load_number <= ?");
        pstmt1.setInt(idx1++,load_number_begin);
        pstmt1.setInt(idx1++,load_number_end);
      }

      rs1   = pstmt1.executeQuery();

      while(rs1.next())
      {
        cvs = rs1.getString("des").substring(12).trim()+",";
        if(cvs != null )

        {

          st1 = new StringTokenizer(cvs,",",false);
          countTokens1  = st1.countTokens();


            while (st1.hasMoreTokens())
            {
              index_term   = st1.nextToken().trim();
              index_term = index_term.replace('*', ' ').trim();
              index_term = index_term.replace('.', ' ').trim();

              DescriptorStream ds = new DescriptorStream(new ByteArrayInputStream(index_term.getBytes()));
        	  boolean testBalancedParan = ds.getBalancedParen();

        	 /*
        	 	Test for unmatched parens and throw them away. This will discard the weird old
        	 	matched paired descriptors that we don't want in the index. See the NTIS raw data
        	 	spec for more information.

        	 */

        	 if(testToken(index_term) && testBalancedParan){
              	writerCvs.println(index_term.toUpperCase()+"	nti");
		  	  }else{
				 //writerCvs_unbalance.println(index_term.toUpperCase()+"	nti");
			  }

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
      if(writerCvs != null)
      {
        try
        {
          writerCvs.close();
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
      }
    }

  }

/** This method <code> testToken </code> validates data input for the flat file
* @param String token
* @return boolean false if data is invalid
*/
  public boolean testToken(String token){
    String s = null;
    boolean flag = true;
    int countClose = 0;
    int countOpen = 0;
    int counter =0;
    int i = 0;
    char test;

    s = token;
    countClose = s.indexOf(")");
    countOpen = s.indexOf("(");
    // validate against closing braket in the string placed before open braket
    if (countClose > 0){
      if (countClose < countOpen){
        System.out.println("returning false from testToken");
        return false;
      }
    }
    // validate against unbalanced brakets
 //   DescriptorStream ds = new DescriptorStream(new ByteArrayInputStream(s.getBytes()));
 //   boolean testparen = true;
 //   if (!(testparen = ds.getBalancedParen())){
 //     System.out.println(" UNBALANCED paren");
 //   }
 //   flag = testparen;
    if ((countClose != 0) || (countOpen !=0)){
//System.out.println(" token" + token);
//      st1 = new StringTokenizer(token,"(",false);
//System.out.println(" token " + token );
//      st2 = new StringTokenizer(token,")",false);
//      openparen  = st1.countTokens();
 //     closeparen  = st2.countTokens();

      int j = s.length();
      while (i < j) {
        test = s.charAt(i++);
        if (test == '('){
          ++counter;
        } else if (test == ')'){
          --counter;
        }
      }
      if (counter != 0){
        flag = false;
      }
    }
    return flag;
  }

}



