package org.ei.dataloading.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.ei.dataloading.RemoveDuplicates;

/**
 * 
 * @author TELEBH
 * @Date: 07/16/2020
 * @Description: BD reported that we missing some of CPX records delivered in recent files (i.e. files “cpx_eei_new_4241_276_1.xml & cpx_eei_new_4241_276_2.xml)
 * File contains so many records that failed to be processed, in order to identify the Record identifiers (i.e. Accessnumber, PUI) It is time consuming to do it manually
 * and that what brought this class 
 * 
 * 
 * 
 */
public class ReadBDProcessedLog {
	
	Map<String, String[]> recordIdentifierMap;
	FileWriter out;
	
	public ReadBDProcessedLog()
	{}
	
	public void IdentifyRecordIdentifier(String fileName)
	{
		recordIdentifierMap = new HashMap<>();
		
		File dir = new File("missed_files");
		// only list files
		File[] fileList = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".txt") && pathname.isFile();
			}
		});
		if(fileList.length >0)
		{
			for(File child: fileList)
			{
				System.out.println("Reading file: " + child.getName());
				File file = new File("missed_files" + File.separator + child.getName());
				try(BufferedReader reader = new BufferedReader(new FileReader(file)))
				{
					out = new FileWriter("missed_files" + File.separator + file.getName() + "_ids.csv");
					
					//out.write("doi" + "," + "PUI" + "," + "CAR-ID" + "," + "AccessionNumber" + "\n");
					out.write("PUI" + "," + "CAR-ID" + "," + "AccessionNumber" + "\n");
					String line = null;
					while((line = reader.readLine()) !=null)
					{
						if(line.contains("<itemidlist>") && line.contains("</itemidlist>"))
						{
							String recIds = line.substring(line.indexOf("<itemidlist>"), line.lastIndexOf("</itemidlist>"));
							String[] ids = recIds.trim().replaceAll("><", ">\n<").split("\n");
							if(ids.length >= 3)
							{
								//String[] rec = new String[4];
								for(String id: ids)
								{
									/*
									 * if(id.contains("ce:doi")) { out.write(id.substring(id.indexOf("<ce:doi>") +
									 * "<ce:doi>".length(), id.indexOf("</ce:doi>")) + ","); }
									 */
									if(id.contains("idtype=\"PUI\""))
									{
										out.write(id.substring(id.indexOf("<itemid idtype=\"PUI\">") + "<itemid idtype=\"PUI\">".length(), id.indexOf("</itemid>")) + ",");
									}
									
									if(id.contains("<itemid idtype=\"CAR-ID\">"))
									{
										out.write(id.substring(id.indexOf("<itemid idtype=\"CAR-ID\">") + "<itemid idtype=\"CAR-ID\">".length(), id.indexOf("</itemid>")) + ",");
									}
									if(id.contains("<itemid idtype=\"CPX\">"))
									{
										out.write(id.substring(id.indexOf("<itemid idtype=\"CPX\">") + "<itemid idtype=\"CPX\">".length(), id.indexOf("</itemid>")) + ",");
									}
								}
								out.write("\n");
							}
							
							
						}
						//uniqueList.put(line, uniqueList.getOrDefault(line, 0)+1);
					}
					
					// list out Only Unique IDS
					
					//String uniqueMIDS = uniqueList.entrySet().stream().filter(entry -> entry.getValue() ==1).map(entry -> entry.getKey()).collect(Collectors.joining("\n"));
					//+System.out.println("Unique Ids: " + uniqueMIDS);
					
				} catch (FileNotFoundException e) {
					System.out.println("File " + file.getName() + " Not Exist Exception!!!!");
					e.printStackTrace();
				} catch(NullPointerException ex)
				{
					System.out.println("HashMap null pointer exception!!!!");
					System.out.println(ex.getMessage());
					ex.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if(out != null)
						{
							out.flush();
							out.close();
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	public static void main(String[] args)
	{
		String fileName = "";
		
		if(args.length >0)
		{
			if(args[0] != null && !(args[0].isEmpty()))
				fileName = args[0];
		}
		else
		{
			System.out.println("No fileName provided!!! Re-run with filename containing Failed/missed BD records");
			System.exit(1);
		}
		
		ReadBDProcessedLog obj = new ReadBDProcessedLog();
		obj.IdentifyRecordIdentifier(fileName);

	}
	

}
