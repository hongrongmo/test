/*
 * Created on Nov 13, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.data.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import org.ei.domain.FastClient;


/**
 * @author solovyevat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FastSearchUnitTestYRCounts extends FastSearchUnitTest
{


    public FastSearchUnitTestYRCounts(String inFile, 
            				     String outFile)
    {
        	super(inFile,outFile);
              
    }
    
    public static void main (String [] args) throws Exception
    {
        String inFile = args[0];
        String outFile = args[1];
        FastSearchUnitTestYRCounts stest = new FastSearchUnitTestYRCounts(inFile, outFile);
        stest.runTest();
        
    }
    
    public void runTest() throws Exception
    {
        
        FileWriter out = new FileWriter(this.outputFile);
        BufferedReader in = new BufferedReader(new FileReader(this.inputFile));
        String line ;
        String [] args = new String[3];
        try
        {
            while((line=in.readLine()) != null)
            {
                args = line.split(";");
                if(args != null && args.length > 0)
                {
                    boolean testResult = FastClient.unitTestCounts(args);                
                    String strResult = String.valueOf(testResult);
                    out.write("\n");
                    out.write(strResult);
                    out.write("\t");
                    out.write(args[1]);
                    out.write("\t");
                    out.write("comments::"+args[2]);

                }
            }
            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(in != null)
            {
                try
                {
                    in.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            if(out != null)
            {
                try
                {
                    out.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }                
        }
    }        

}
