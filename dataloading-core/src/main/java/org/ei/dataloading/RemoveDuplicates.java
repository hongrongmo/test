package org.ei.dataloading;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 
 * @author TELEBH
 * @Date: 05/07/2020
 * @Description: During Fast to ES migration , QA requires to pull M_ID list from both ES and Fast for certain query to identify missing/extra records in one out of the others
 * 
 */
public class RemoveDuplicates {

	public RemoveDuplicates() {}
	public void FilterDuplicates(String fileName)
	{
		File file = new File(fileName);
		try(BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			Map<String, Integer> uniqueList = new Hashtable<String, Integer>();
			String line = null;
			while((line = reader.readLine()) !=null)
			{
				uniqueList.put(line, uniqueList.getOrDefault(line, 0)+1);
			}
			
			// list out Only Unique IDS
			
			String uniqueMIDS = uniqueList.entrySet().stream().filter(entry -> entry.getValue() ==1).map(entry -> entry.getKey()).collect(Collectors.joining(","));
			System.out.println("Unique Ids: " + uniqueMIDS);
			
		} catch (FileNotFoundException e) {
			System.out.println("File " + fileName + " Not Exist Exception!!!!");
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
			System.out.println("No fileName provided!!! Re-run with filename containing duplicate M_IDS");
			System.exit(1);
		}
		
		RemoveDuplicates obj = new RemoveDuplicates();
		obj.FilterDuplicates(fileName);

	}
	
}
