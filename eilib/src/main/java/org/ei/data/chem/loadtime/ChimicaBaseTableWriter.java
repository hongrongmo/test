package org.ei.data.chem.loadtime;

import java.io.FileWriter;import java.io.PrintWriter;import java.util.Hashtable;import java.util.Properties;import org.apache.oro.text.perl.Perl5Util;public class ChimicaBaseTableWriter
{

    private Perl5Util perl = new Perl5Util();
    private int recsPerFile = -1;
    private int curRecNum = 0;
    private String filename;
    private PrintWriter out;
    private String filepath;
    private int loadnumber;
    private int filenumber = 0;
    private boolean open = false;
    private static Properties props;
    private String[] baseTableFields = ChimicaBaseTableRecord.baseTableFields;
//  private ChimicaRecordFixer fixer = new ChimicaRecordFixer();

    {
        props = new Properties();
        props.setProperty("M_ID", "GU");
        props.setProperty("ID", "ID");

        props.setProperty("CHN", "EX");
        props.setProperty("HST", "HS");
        props.setProperty("TIE", "TI");
        props.setProperty("AUT","AU");
        props.setProperty("CNA", "CA");

        props.setProperty("ADR","AF");
        props.setProperty("JTA", "JT");

        props.setProperty("STI","ST");
        props.setProperty("PYR","YR");
        props.setProperty("VIP", "VI");

        props.setProperty("REF", "RF");

        props.setProperty("COD", "CN");
        props.setProperty("ISS", "SN");
        props.setProperty("CNS", "CS");

        props.setProperty("LNA", "LA");

        props.setProperty("LNS", "LA");

        props.setProperty("CTM", "CV");
        props.setProperty("CRD", "CR");
        props.setProperty("TRC", "NC");
        props.setProperty("STD", "DT");
        props.setProperty("ITD", "IT");

        props.setProperty("PII", "PI"); // Question Mark

        props.setProperty("SPD", "SP");

        props.setProperty("EML", "EA"); //Question Mark

        props.setProperty("MNF", "MF");

        props.setProperty("TNV", "TV");

        props.setProperty("MNV", "MV");

        props.setProperty("TNM", "TM"); // Question Mark

        props.setProperty("MSN", "MN");

        props.setProperty("TIF", "TI");
        props.setProperty("PUB", "PB");

        props.setProperty("EIPT", "ET");

        props.setProperty("CPX", "CP");

        props.setProperty("CFN", "CF");

        props.setProperty("CFL", "FL");
        props.setProperty("CFD", "FD");

        props.setProperty("CFA", "FA");

        props.setProperty("CCC", "CC");

        props.setProperty("ABS", "AB");
        props.setProperty("LOAD_NUMBER", "LN");
    }



    public ChimicaBaseTableWriter(int recsPerFile,
                                    String filename)
    {
        this.recsPerFile = recsPerFile;
        this.filename = filename;
    }




    public ChimicaBaseTableWriter(String filename)
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

                StringBuffer value = (StringBuffer)record.get(props.getProperty(bf));
                String valueS = null;
                if(value != null)
                {
                    valueS = value.toString();
                    if(bf.equals("CHN") || bf.equals("EX"))
                    {
                        valueS = perl.substitute("s/EIX//ig", valueS);
                    }

                    valueS = perl.substitute("s/\\t/     /g", valueS);

                }


                if(i > 0)
                {
                    recordBuf.append("  ");
                }


                if(valueS != null)
                {
                    recordBuf.append(valueS);
                }

            }

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