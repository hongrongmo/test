package org.ei.util.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ProcessFastExtractFiles {
	
	 public static void main(String args[])
		        throws Exception
	{
		 
		 ProcessFastExtractFiles pFiles = new ProcessFastExtractFiles();
		 List<String> allFiles = pFiles.getAllFiles("totalFastZipFiles");
		 int j=0;
		 for(int i=1;i<allFiles.size();i++)
		 {
			File directory=new File("Batch_/"+j);
			try
			{
				if(!directory.exists())
				{
					directory.mkdir();
				}
				
				String sfilename = allFiles.get(i);
				Runtime r = Runtime.getRuntime();

	            Process p = r.exec("cp "+sfilename+" "+directory);
	            if(i>10)
	            {
	            	break;
	            }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		 }
		 
	}
	 
	 
	public List getAllFiles(String directoryName)
	{
		List<String> allFiles = new ArrayList<String>();
	    File directory = new File(directoryName);
	    //get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList){
	        if (file.isFile()){
	            //System.out.println(file.getName());
	            allFiles.add(file.getName());
	        }
	    }
	    System.out.println("TOTAL file "+allFiles.size());
	    return allFiles;
	}
}