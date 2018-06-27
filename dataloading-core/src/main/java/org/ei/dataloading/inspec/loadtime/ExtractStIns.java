package org.ei.dataloading.inspec.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.ei.xml.Entity;

public class ExtractStIns
{

    public void extract(int load_number_begin, int load_number_end, Connection con)
        throws Exception
    {
        PrintWriter writerSt    = null;

        PreparedStatement pstmt1    = null;
        ResultSet rs1               = null;

        String pfjt         = null;
        String sfjt         = null;
        String psource_title    = null;
        String ssource_title    = null;
        String pbsource_title   = null;
        String source_title = null;
        String pubti        = null;

        try
        {
            writerSt    = new PrintWriter(new FileWriter("ins_st.lkp"));

            if(load_number_end == 0)
            {
                pstmt1  = con.prepareStatement(" select pfjt,sfjt,pubti from new_ins_master where  load_number = "+load_number_begin);
                System.out.println("\n\nQuery: "+" select pfjt,sfjt,pubti from new_ins_master where  load_number = "+load_number_begin);
            }
            else
            {
                pstmt1  = con.prepareStatement(" select pfjt,sfjt,pubti from new_ins_master where  load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
                System.out.println("\n\nQuery: "+" select pfjt,sfjt,pubti from new_ins_master where  load_number >= "+load_number_begin+" and load_number <= "+load_number_end);
            }

            rs1     = pstmt1.executeQuery();

            while(rs1.next())
            {
                pfjt        = rs1.getString("pfjt");
                sfjt        = rs1.getString("sfjt");
                pubti       = rs1.getString("pubti");

                if(pfjt != null)
                {
                    psource_title   = pfjt.trim();
                    source_title = Entity.prepareString(psource_title);
                    source_title = source_title.trim().toUpperCase();

                    writerSt.println("\t"+source_title+"\tins");

                }
                else if(pubti != null)
                {
                    pbsource_title  = pubti.trim();
                    source_title = Entity.prepareString(pbsource_title);
                    source_title = source_title.trim().toUpperCase();

                    writerSt.println("\t"+source_title+"\tins");

                }
                if(sfjt != null)
                {
                    ssource_title   = sfjt.trim();
                    source_title = Entity.prepareString(ssource_title);
                    source_title = source_title.trim().toUpperCase();

                    writerSt.println("\t"+source_title+"\tins");

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

}

