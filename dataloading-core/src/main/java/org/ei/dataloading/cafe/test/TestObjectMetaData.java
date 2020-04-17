package org.ei.dataloading.cafe.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * @author TELEBH
 *
 */


public class TestObjectMetaData {

	static TestObjectMetaData objectFromS3;
	static String fileName = "";
	BufferedReader S3Filecontents = null;
	
	
	public TestObjectMetaData(){}
	
	public static void main(String [] args)
	{
		if(args !=null && args.length >0)
		{
			if(args[0] !=null)
			{
				fileName = args[0];
				
				System.out.println("FileName to check its MetaData is: " + fileName);
			}
		}
		
		objectFromS3 = new TestObjectMetaData();
		objectFromS3.checkDBCollection(fileName);
		
	}

public boolean checkDBCollection(String fileName) 
{
	boolean cpxCollection = false;
	String dbCollection="";
	String substr="";
	String singleLine = null;
	String itemInfoStart,itemInfo,cpxIDInfo,accessNumber,epoch = "";

	try
	{
		File file = new File(fileName);
		InputStream inputStream = new FileInputStream(file);


		// only parse "CPX" Records, skip any other dbcollection
		S3Filecontents = new BufferedReader(new InputStreamReader(inputStream));
		while ((singleLine = S3Filecontents.readLine()) !=null)
		{

			if(singleLine.contains("<item-info>"))
			{
				itemInfoStart = singleLine.substring(singleLine.indexOf("<item-info>") , singleLine.length());
				if(itemInfoStart.length() >0 && itemInfoStart.contains("</item-info>"))
				{
					itemInfo = itemInfoStart.substring(0,itemInfoStart.indexOf("</item-info>") +12);


					if(itemInfo.length() >0 && itemInfo.contains("<itemid idtype=\"CPX\">") && itemInfo.contains("<dbcollection>CPX</dbcollection>"))
					{
						cpxIDInfo = itemInfo.substring(itemInfo.indexOf("<itemid idtype=\"CPX\">"),itemInfo.length());
						accessNumber = cpxIDInfo.substring(cpxIDInfo.indexOf(">")+1,cpxIDInfo.indexOf("</itemid>"));


						cpxCollection = true;
						System.out.println("CPX file: " + fileName);
					}
					else if(itemInfo.length() >0 && itemInfo.contains("<dbcollection>"))
					{
						dbCollection = itemInfo.substring(itemInfo.indexOf("<dbcollection>"), itemInfo.indexOf("</item-info>"));
						System.out.println("CollectionType: " +  dbCollection);

						System.out.println("Skip this Key as it belongs to db collection: " +  dbCollection);
					}

					
				}

			}
			
			//get the Object's epoch Metadata
			if(singleLine.contains("<xocs:indexeddate epoch"))
			{
				substr = singleLine.substring(singleLine.indexOf("<xocs:indexeddate epoch="), singleLine.length());
				epoch = substr.substring(substr.indexOf("=")+1, substr.indexOf(">")).trim().replace("\"", "");
				System.out.println("epoch: " + epoch);
			}


		}
	}

catch(IOException ex)
{
	ex.printStackTrace();
}
	finally
	{
		if(S3Filecontents !=null)
		{
			try
			{
				S3Filecontents.close();
			}
			catch(IOException ex)
			{
				System.out.println("Exception to close BufferedReader for : " + ex.getMessage());
				ex.printStackTrace();
			}
			
		}
	}


return cpxCollection;
}
}


