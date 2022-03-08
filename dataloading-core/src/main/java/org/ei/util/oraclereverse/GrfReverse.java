package org.ei.util.oraclereverse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.ei.common.Constants;
import org.ei.util.db.DbConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

/***
 * 
 * @author TELEBH
 * @Date: 03/04/2022
 * @Description: Reverse GRF by transforming data from Oracle format into original source file using same XML format as BD/CPX
 * 
 * In other words we need not exactly extract GRF data  in XML , but to extract GRF data from Oracle in the format of CPX XML format
 * 
 * and so this is an adhoc process only capture abstract and title from adhoc file provided by Matt and which is loaded to table hh_grf_ni_test_2022
 * and embed it as title and abstract of CPX XML CAR record , so will use BdReverseRecordBuilder.java to map BD with this temp table to get the data
 * 
 * Other way is to use XML Zip parser to read <item> and </item> tags in original XML file and the replace Title and Abstract with Title &Abstract from source file
 * 
 */
public class GrfReverse {

	List<String> fileNames;
	String url;
	String driver;
	String userName, passwd;
	
	public static void main(String[] args)
	{
		GrfReverse g = new GrfReverse();
		g.run(args);
	}
	public void run(String[] args)
	{
		if(args.length >1)
		{
			if(args[0] != null && !(args[0].isBlank()))
			{
				fileNames = Arrays.asList(args[0].split(";"));
				System.out.println("fileNames: " + fileNames);
			}
					
		}
		if(args.length >4)
		{
			if(args[1] != null && !(args[1].isBlank()))
			{
				url = args[1];
				System.out.println("ConnectionURL: " + url);
			}
			if(args[2] != null && !(args[2].isEmpty()))
			{
				driver = args[2];
				System.out.println("driver: " + args[2]);
			}
			if(args[3] != null && !(args[2].isBlank()))
			{
				userName = args[3];
				System.out.println("userName: " + userName);
			}
			if(args[4] != null && !(args[4].isBlank()))
			{
				passwd = args[4];
			}
		}
		else
		{
			System.out.println("Not enough parameters!");
			System.exit(1);
		}
		
		start();
	}
	
	public void start()
	{
		
		// pull the list of GRF Titles/Abstract
		Connection con = DbConnection.getConnection(url, driver, userName, passwd);
		List<List<String>> grfInfo = getGrfInfo(con);
		
		for(String fname: fileNames)
		{
			ReadInputFile(fname, grfInfo);
		}
		
	}
	
	public List<List<String>> getGrfInfo(Connection con)
	{
		List<List<String>> grf_info = new LinkedList<>();
		String idNum = null;
		try
		{
			PreparedStatement ps;
			ResultSet rs;
			String sql = "select id_number,title_of_analytic, abstract from hh_grf_ni_test_2022_with_cpxpui where rownum<6000";
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next())
			{
				List<String> info = new ArrayList<>();
				
				if(rs.getString("ID_NUMBER") != null)
					idNum = rs.getString("ID_NUMBER");
				if(rs.getString("TITLE_OF_ANALYTIC") != null)
				{
					String[] ti = rs.getString("TITLE_OF_ANALYTIC").split(Constants.IDDELIMITER);
					info.add(ti[1]);
				}
					
				if(rs.getString("ABSTRACT") != null)
					info.add(rs.getString("ABSTRACT"));
				
				grf_info.add(info);
			}
			
		}
		catch(SQLException e)
		{
			System.out.println("Sql Exception happened: " + e.getMessage());
			e.printStackTrace();
		}
		
			return grf_info;
		
		
	}
	public void ReadInputFile(String inFile, List<List<String>> grfInfo)
	{
		BufferedReader br = null;
		
		
		try
		{
			if(inFile.toLowerCase().endsWith(".zip"))
			{
				System.out.println("Input Is Zip file: " + inFile);
				ZipFile zipFile = new ZipFile(inFile);
				Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
				Iterator<? extends ZipEntry> itr = zipEntries.asIterator();
				while(itr.hasNext())
				{
					ZipEntry entry = itr.next();
					br = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF-8"));
						
					
					
				}
				
			}
			else if(inFile.toLowerCase().endsWith("xml"))
			{
				System.out.println("Input Is Xml file: " + inFile);
				 File file = new File(inFile);
	             br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				
				
			}
			
			// Read XML record and replace Title and Abstract with GRF Info
			writeRecs(br, grfInfo, inFile);
		}
		catch(IOException ex)
		{
			System.out.println("Issue reading inFile!!!!");
			System.out.println("Error: " + ex.getMessage());
			System.out.println("Exit");
			System.exit(1);
		}
		catch(IllegalStateException ex)
		{
			System.out.println("Issue reading Zip file entries!!!!");
			System.out.println("Exit");
			System.exit(1);
		}
		catch(Exception ex)
		{
			System.out.println("Issue reading Input File!!!!");
			System.exit(1);
		}
		
        finally
        {
            if(br != null)
            {
            	try {
					br.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
            }
        }
		
	}
	public void writeRecs(BufferedReader xmlReader, List<List<String>> grfInfo, String fName)
	{
		int recordCount = 0;
		boolean start = false;
		String line;

		
		StringBuilder sb = new StringBuilder();

		FileWriter outFile = null;
		List<File> filesList = new ArrayList<>();
		
		long startTime = System.currentTimeMillis();
		long midTime = 0, endTime = 0;
		
		String grf_title = "";
		String grf_ab = "";
		
	try
	{
		File file = new File(System.getProperty("user.dir") + File.separator + fName.substring(0, fName.indexOf(".")) + "_out.xml");
		outFile = new FileWriter(file);
		
			while((line = xmlReader.readLine()) != null)
			{
				// If identified beginning of file, next few lines are for the item record
				if(start)
				{
					if(line.indexOf("<titletext") > -1)
					{
						
						
						/** COMMENTED OUT FOR JSON FORMAT, UNCOMMENT IN PROD WHEN USING XML FORMAT
						sb.append(SchemaConstants.BD_ENCODING + "\n");
						sb.append(SchemaConstants.BD_STARTROOTELEMENT + "\n");
						**/
						line = line.substring(0, line.indexOf(">")+1) + grf_title + "</titletext>";
						sb.append(line + "\n");
					}
					else if(line.contains("<ce:para>"))
					{
						line = line.substring(0, line.indexOf(">")+1) + grf_ab + line.substring(line.indexOf("<"), line.length());
						sb.append(line + "\n");
					}
					
					else
						sb.append(line + "\n");
				}
				// Beginning of Record, read GRF title & abstract
				
				if(line.indexOf("<item>") > -1)
				{
					sb.append(line + "\n");
					start = true;
					if(grfInfo.size() >0)
					{
						grf_title = grfInfo.get(0).get(0);
						grf_ab = grfInfo.get(0).get(1);
						
						grfInfo.remove(0);
						
					}
					else
					{
						grf_title = "";
						grf_ab = "";
					}
					
				}
				
				
		
					
				// End of record, stop reading & retain M_ID for naming out file
				if(line.indexOf("</item>") > -1)
				{
					start = false;
					//sb.append(SchemaConstants.BD_ENDROOTELEMENT);			//COMMENTED OUT FOR JSON FORMAT, UNCOMMENT IN PROD WHEN USING XML FORMAT
					
					outFile.write(sb.toString());  
				
					recordCount ++;
		
					
					// Reset/clear out SB for next record
					sb.delete(0, sb.length());
					
					

					
				}
			}

			midTime = System.currentTimeMillis();
			System.out.println("Total time to parse inFile: " + (midTime - startTime)/1000 + " seconds");
			
		} 
			
		catch (IOException e) 
		{
			System.err.println("Problem reading XML FILE!!!!");
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
	
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			System.out.println("Total Record Count uploaded for file: " + fName + " : " + recordCount);
			endTime = System.currentTimeMillis();
			System.out.println("Total Time used: " + (endTime - midTime) /1000 + " seconds");
			
			//close fileWriter
			close(outFile);
			
		}
	}
	
	public void close(FileWriter outFile)
	{
		try
		{
			if(outFile != null)
			{
				outFile.flush();
				outFile.close();
			}
		}
		catch(Exception e)
		{
			System.out.println("Failed to close OutFile!!!");
			e.printStackTrace();
		}
	}
}
