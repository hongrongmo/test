
package org.ei.dataloading.paper.loadtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;


public class PaperChemTabFormatFixer
{

    private PaperChemRecordFixer recordFixer = new PaperChemRecordFixer();

    public static void main(String argv[])
        throws Exception
    {

        PaperChemTabFormatFixer fixer = new PaperChemTabFormatFixer();
        fixer.fixFiles();
    }


    public void fixFiles()
        throws Exception
    {
        File dir = new File(".");
        String[] filenames = dir.list();
        for(int i=0; i<filenames.length; ++i)
        {
            String filename = filenames[i];
            if(filename.indexOf(".bad") > -1)
            {
                doFile(filename);
            }
        }
    }

    private void doFile(String filename)
        throws Exception
    {
        BufferedReader in = null;
        PrintWriter out = null;
        boolean good = false;
        try
        {

            in = new BufferedReader(new FileReader(filename));
            out = new PrintWriter(new FileWriter(filename+".tmp"));
            String line = null;
            while((line = in.readLine()) != null)
            {
                out.println(recordFixer.fixRecord(line));
            }
            good = true;
        }
        finally
        {
            if(in != null)
            {
                in.close();
            }

            if(out != null)
            {
                out.close();
            }

            /*
            if(good)
            {
                File fd = new File(filename);
                fd.delete();
                File rn = new File(filename+".tmp");
                rn.renameTo(fd);
            }
            */
        }

    }
}
