package org.ei.dataloading.cbnb.loadtime;

import java.util.*;
import java.io.*;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;

public class CBNBBaseTableWriter
{

    private Perl5Util perl = new Perl5Util();
    private int recsPerFile = -1;
    private int curRecNum = 0;
    private String filename;
    private String propsfilename;
    private PrintWriter out;
    private String filepath;
    private int loadnumber;
    private int filenumber = 0;
    private boolean open = false;
    private Properties props;
    private String[] baseTableFields = CBNBBaseTableRecord.baseTableFields;
    private CBNBRecordFixer fixer = new CBNBRecordFixer();

    {
        props = new Properties();
        props.setProperty("M_ID", "GU");
        props.setProperty("ABN", "ABN");
        props.setProperty("CDT", "CDT");
        props.setProperty("DOC", "DOC");
        props.setProperty("SCO", "SCO");
        props.setProperty("FJL", "FJL");
        props.setProperty("ISN", "ISN");
        props.setProperty("CDN", "CDN");
        props.setProperty("LAN", "LAN");
        props.setProperty("VOL", "VOL");
        props.setProperty("ISS", "ISS");
        props.setProperty("IBN", "IBN");
        props.setProperty("PBR", "PBR");
        props.setProperty("PAD", "PAD");
        props.setProperty("PAG", "PAG");
        props.setProperty("PBD", "PBD");
        props.setProperty("PBN", "PBN");
        props.setProperty("SRC", "SRC");
        props.setProperty("SCT", "SCT");
        props.setProperty("SCC", "SCC");
        props.setProperty("EBT", "EBT");
        props.setProperty("CIN", "CIN");
        props.setProperty("REG", "REG");
        props.setProperty("CYM", "CYM");
        props.setProperty("SIC", "SIC");
        props.setProperty("GIC", "GIC");
        props.setProperty("GID", "GID");
        props.setProperty("ATL", "ATL");
        props.setProperty("OTL", "OTL");
        props.setProperty("EDN", "EDN");
        props.setProperty("AVL", "AVL");
        props.setProperty("CIT", "CIT");
        props.setProperty("ABS", "S");
        props.setProperty("PYR", "PYR");
        props.setProperty("LOAD_NUMBER", "LN");

    }



    public CBNBBaseTableWriter(int recsPerFile, String filename)
    {
        this.recsPerFile = recsPerFile;
        this.filename = filename;
    }




    public CBNBBaseTableWriter(String filename)
    {
        this.filename = filename;

    }

    public void begin()
                    throws Exception
    {
                ++filenumber;

                out = new PrintWriter(new FileWriter(filename+"."+filenumber));
                open = true;
                curRecNum = 0;
    }


    public void writeRec(Hashtable record)
            throws Exception
    {


            StringBuffer recordBuf = new StringBuffer();
            for(int i=0; i<baseTableFields.length; ++i)
            {
                String bf = baseTableFields[i];

                if(record == null)
                {
                    System.out.println("Record was null");
                }

                StringBuffer value = (StringBuffer)record.get(props.getProperty(bf).trim());
                String valueS = null;
                if(value != null)
                {
                    valueS = value.toString();
                    valueS = perl.substitute("s/\\t/     /g", valueS);

                }


                if(i > 0)
                {
                    //recordBuf.append("  ");

                    //12/31/2014 add tab as in eijava not spaces
                    recordBuf.append("\t");

                }


                if(valueS != null)
                {
                    recordBuf.append(valueS);
                }

            }

                out.println(fixer.fixRecord(recordBuf.toString()));

            ++curRecNum;
    }


    public void end()
            throws Exception
    {
        if(open)
        {
            out.close();
            open = false;
        }
    }
}
