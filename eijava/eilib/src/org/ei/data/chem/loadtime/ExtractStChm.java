package org.ei.data.chem.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;

public class ExtractStChm
{

    public void extract(int load_number_begin, int load_number_end, Connection con)
        throws Exception
    {
        PrintWriter writerSt    = null;
        Hashtable stHash        = new Hashtable();

        PreparedStatement pstmt1    = null;
        ResultSet rs1               = null;

        long begin      = System.currentTimeMillis();

        String source_title = null;
        String issn         = null;

        try
        {

            writerSt    = new PrintWriter(new FileWriter("chm_st.lkp"));

            if(load_number_end == 0)
            {
                pstmt1  = con.prepareStatement(" select sti,isn from chm_master where (sti is not null) and load_number = "+load_number_begin);
                System.out.println("\n\nQuery: "+" select sti,isn from chm_master where (sti is not null) and load_number = "+load_number_begin);
            }
            else
            {
                pstmt1  = con.prepareStatement(" select sti,isn from chm_master where (sti is not null) and load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
                System.out.println("\n\nQuery: "+" select sti,isn from chm_master where (sti is not null) and load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
            }

            rs1     = pstmt1.executeQuery();

            while(rs1.next())
            {
                source_title    = rs1.getString("sti");
                issn            = rs1.getString("isn");

                if(source_title != null)
                {
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
                source_title    = (String)itrTest.next();
                issn            = (String)stHash.get(source_title);

                writerSt.println(issn.trim()+"\t"+source_title+"\tchm");
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

