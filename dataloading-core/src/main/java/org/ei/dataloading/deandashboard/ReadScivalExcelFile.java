package org.ei.dataloading.deandashboard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @author TELEBH
 * @Date: 12/11/2020
 * @Description: We do receive CSV file from Scival with updated Institutions list, but it is not in 
 * format to be processed, so we have to convert to process-able format first
 */
public class ReadScivalExcelFile {
	
	String inFileName;
	LinkedHashMap<Integer, ScivalInstitutionRecord> institutionsList;
	
	public ReadScivalExcelFile()
	{
		institutionsList = new LinkedHashMap<>();
	}
	public static void main(String[] args)
	{
		System.out.println("START!!!");
		if(args.length >0)
		{
			ReadScivalExcelFile obj = new ReadScivalExcelFile();
			obj.run(args);
		}
	}
	public void run(String[]args)
	{
		if(args[0] != null)
			if(args.length>0)
			{
				inFileName = args[0];
				System.out.println("Input FileName: " + inFileName);
			}
			else
			{
				System.out.println("Not enough parameters!!!! Re-run with excelfile name as input");
				System.exit(1);
			}
			
		System.out.println("InputFile: " + inFileName);
		readCSVFile();
		writeCSVFile();
	}

	private void readCSVFile()
	{
		XSSFWorkbook workbook = null;
		try(FileInputStream instream = new FileInputStream(new File(inFileName)))
		{
			
			// Get the workbook instance for xlsx file
			workbook = new XSSFWorkbook(instream);
			
			//Get the first sheet by index
			XSSFSheet sheet = workbook.getSheetAt(0);
			
			//Get iterator on rows of the sheet
			Iterator<Row> rowsIterator = sheet.iterator();
			
			// INstitution Info
			ScivalInstitutionRecord instRec = null;
			
			while(rowsIterator.hasNext())
			{
				// Get the row with all columns
				Row row = rowsIterator.next();
				
				// skip first row as it is headers
				if(row.getRowNum() == 0)
					continue;
				// Institution Profile Record, if exist get the existing record
				
				
				// affiliationId & affiliationName info holders
				int affiliationID = 0;
				String affiliationName = "";
				
				// Iterate through columns in current row
				Iterator<Cell> cellsIterator = row.cellIterator();
				while(cellsIterator.hasNext())
				{
					Cell cell = cellsIterator.next();
					int colIndex = cell.getColumnIndex();
					if(colIndex == 0)
					{
						if(cell.getNumericCellValue() != 0)
						{
							int instId = (int)cell.getNumericCellValue();
							if(!(institutionsList.containsKey(instId)))
							{
								instRec = new ScivalInstitutionRecord();
								instRec.setInstitution_ID(instId);
							}
								
							else
							{
								instRec = institutionsList.get(instId);
								System.out.println("Institution ID previously exist");
							}
								
						}
						
							
					}
						
					if(colIndex == 1)
						instRec.setInstitutionName(cell.getStringCellValue());
					if(colIndex == 2)
					{
						if(cell.getNumericCellValue() != 0)
							affiliationID = (int)cell.getNumericCellValue();
					}
					if(colIndex == 3)
					{
						if(!cell.getStringCellValue().isEmpty())
							affiliationName = cell.getStringCellValue();
					}
					if(colIndex == 4)
					{
						if(!cell.getStringCellValue().isEmpty())
							instRec.setRegion(cell.getStringCellValue());
					}
					if(colIndex == 5)
					{
						if(!cell.getStringCellValue().isEmpty())
							instRec.setCountry(cell.getStringCellValue());
					}
				}
				// add affiliationId & affiliationName entry to affInfo Map
				if(affiliationID != 0)
				{
					instRec.setAffiliationInfo(affiliationID, affiliationName);
				}
				
				// add Institution Record to inst.list
				institutionsList.put(instRec.getInstitution_ID(), instRec);
			}
			
			
		} 
		catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(workbook != null)
					workbook.close();
			}
			catch(Exception e)
			{
				System.out.println("Failed to close workbook");
				e.printStackTrace();
			}
		}
	}
	
	public void writeCSVFile()
	{
		String scival_out = "scival_instAff_out.csv";
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(scival_out))))
		{
			if(institutionsList.size() >0)
			{
				for(Map.Entry<Integer, ScivalInstitutionRecord> entry: institutionsList.entrySet())
				{
					ScivalInstitutionRecord recordInfo = entry.getValue();
					//for each affiliation_ID add entry in out file
					recordInfo.getAffiliationInfo().forEach((key,value) -> {
						try {
							writer.write(entry.getKey() + "," + recordInfo.getInstitutionName() + "," + key + "," + value
									+ "," + recordInfo.getRegion() + "," + recordInfo.getCountry());
							writer.write("\n");
						} catch (IOException e) {
							
							e.printStackTrace();
						}
					});
					
				}
			}
		} catch (IOException e) {
			System.out.println("Exception writing to scival out file?!!!!");
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		
		
	}
	
	public void writeExcelFile()
	{
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("institution_profile");
		int rownum = 0;
		
		if(institutionsList.size() >0)
		{
			for(Map.Entry<Integer, ScivalInstitutionRecord> entry: institutionsList.entrySet())
			{
				int colIndx = 0;
				Row row = sheet.createRow(rownum++);
				ScivalInstitutionRecord recordInfo = entry.getValue();
				
				//for each affiliation_ID add entry in out file
				recordInfo.getAffiliationInfo().forEach((key,value) -> {
					try {
						row.createCell(colIndx).setCellValue(entry.getKey());
						row.createCell(colIndx+1).setCellValue(recordInfo.getInstitutionName());
						row.createCell(colIndx+2).setCellValue(key);
						row.createCell(colIndx+3).setCellValue(value);
						row.createCell(colIndx+4).setCellValue(recordInfo.getRegion());
						row.createCell(colIndx+5).setCellValue(recordInfo.getCountry());
						
					} 
					catch (Exception e) 
					{
						
						e.printStackTrace();
					}
				});
			}
		}
	}
	class ScivalInstitutionRecord
	{
		private int institution_ID;
		private String institutionName;
		private Map<Integer, String> affiliationInfo;
		private String region;
		private String country;
		
		public ScivalInstitutionRecord()
		{
			//affiliationInfo = Collections.synchronizedMap(new LinkedHashMap<Double, String>());
			affiliationInfo = new LinkedHashMap<>();
		}
		//Setters & Getters
		public int getInstitution_ID() {
			return institution_ID;
		}
		public void setInstitution_ID(int institutionID) {
			this.institution_ID = institutionID;
		}
		public String getInstitutionName() {
			return institutionName;
		}
		public void setInstitutionName(String institution) {
			this.institutionName = institution;
		}
		public Map<Integer,String> getAffiliationInfo() {
			return affiliationInfo;
		}
		public void setAffiliationInfo(int affiliationID, String affiliationName) {
			this.affiliationInfo.put(affiliationID, affiliationName);
		}
		
		public String getRegion() {
			return region;
		}
		public void setRegion(String region) {
			this.region = region;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		
	}
}
