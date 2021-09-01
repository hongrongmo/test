package org.ei.util.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UploadToS3 
{
	public static void main(String args[]) throws Exception

	{
		
		 String dataFile = args[0];
		 BufferedReader in = null;
		 UploadToS3 u = new UploadToS3();
		 if(dataFile.toLowerCase().endsWith(".zip"))
         {
             System.out.println("IS ZIP FILE");
             ZipFile zipFile = new ZipFile(dataFile);
             Enumeration entries = zipFile.entries();
             while (entries.hasMoreElements())
             {
                 ZipEntry entry = (ZipEntry)entries.nextElement();
                 in = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF-8"));
               
                 u.writeRecs(in);
             }
         }
         else if(dataFile.toLowerCase().endsWith(".xml"))
         {
             System.out.println("IS XML FILE");
           
             File file = new File(dataFile);
             in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
           
             
             u.writeRecs(in);
           //for test only
             /*
             String line = "";
             while((line = in.readLine()) != null){
             	System.out.println(line);
             } 
             */                  
             //end of test
         }        
	}
	
	private void writeRecs(BufferedReader xmlReader) throws Exception
    {
		
    }
}