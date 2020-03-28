package org.ei.dataloading.cafe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.json.JsonObject;

import org.apache.log4j.Logger;


/**
 * 
 * @author telebh
 * @Date: 06/16/2017
 * @Description: instead of creating AU/AF ES doc on the fly and pass it over to ES for indexing, save AU/AF ES Doc to out file (ES bulk file), then 
 * use the files for physical index to ES using MultiThreads
 */
public class WriteEsDocToFile {

	private int recsPerbulk = 10;
	private int curRecNum = 1;

	String apprev_doc_type = AuthorCombinerMultiThreads.doc_type;
	int loadNumber = AuthorCombinerMultiThreads.loadNumber;
	
	static LinkedList<String> esFilesList = new LinkedList<String>();


	PrintWriter out;
	String esFileName;
	int seqNum = 1;
	Long epoch;
	File esDir;


	private StringBuffer bulkIndexContents = new StringBuffer();

	private final static Logger logger = Logger.getLogger(WriteEsDocToFile.class);


	public WriteEsDocToFile(int bulkSize) 
	{
		recsPerbulk = bulkSize;

		init();
	}

	public WriteEsDocToFile() 
	{

	}

	// initialize the ESIndexFile
	private void init()
	{
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

			// get epoch timestamp for ESFileName
			Date currDate = dateFormat.parse(dateFormat.format(new Date()));
			epoch = currDate.getTime();		 

			String currDir = System.getProperty("user.dir");

			String esDirName= currDir+"/es";
			esDir = new File (esDirName);
			if(!esDir.exists())
			{
				esDir.mkdir();
			}
			esFileName = esDir + "/" + apprev_doc_type + "_" + loadNumber + "_" + epoch + "_" + seqNum + ".json";

			out = new PrintWriter(new FileWriter(esFileName));
			
			esFilesList.add(esFileName);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (ParseException e) 
		{

			e.printStackTrace();
		}


	}




	// close ESindexFile 
	public void close()
	{
		try
		{
			if(out !=null)
			{
				out.close();
				out.flush();
			}
		}
		catch(Exception ex)
		{
			System.out.println("Failed to close ESIndex File!!!!");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}



	/*
	 * Added on Thursday 06/15/2017
	 * most of AU/AF records count to be indexed is very big (thousands/millions). so creating ESDoc and index to ES on Fly will take so long time 
	 * and very likely to break down at some point. so to makes ES faster for larger counts, write bulk ESDocs to a file, then index the file once
	 */

	//Bulk Index 
	public void createBulkIndexFile(String doc_type, String doc_id, JsonObject profile_content)
	{
		try {

			if(curRecNum > recsPerbulk)
			{
				close();
				seqNum ++;
				esFileName = esDir + "/" + apprev_doc_type + "_" + loadNumber + "_" + epoch + "_" + seqNum + ".json";
				out = new PrintWriter(new FileWriter(esFileName));
				esFilesList.add(esFileName);
				
				curRecNum = 1;
				
			} 
	

			bulkIndexContents.append("{ \"index\" : { \"_type\" : \""+ doc_type + "\", \"_id\" : \"" + doc_id + "\" } }");
			bulkIndexContents.append("\n");
			bulkIndexContents.append(profile_content.toString());
			bulkIndexContents.append("\n");

			out.print(bulkIndexContents);

			bulkIndexContents = new StringBuffer();
			curRecNum ++;
		}

		catch (IOException e) 
		{
			e.printStackTrace();
		}


	}
}
