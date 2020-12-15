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
	LinkedHashMap<Double, ScivalInstitutionRecord> institutionsList;
	
	public ReadScivalExcelFile()
	{
		institutionsList = new LinkedHashMap<>();
	}
	public static void main(String[] args)
	{
		if(args.length >1)
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
		
		try(FileInputStream instream = new FileInputStream(new File(inFileName)))
		{
			
			// Get the workbook instance for xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(instream);
			
			//Get the first sheet by index
			XSSFSheet sheet = workbook.getSheetAt(0);
			
			//Get iterator on rows of the sheet
			Iterator<Row> rowsIterator = sheet.iterator();
			
			while(rowsIterator.hasNext())
			{
				// Get the rwo with all columns
				Row row = rowsIterator.next();
				
				// Institution Profile Record, if exist get the existing record
				
				ScivalInstitutionRecord instRec = new ScivalInstitutionRecord();
				
				// affiliationId & affiliationName info holders
				double affiliationID = 0;
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
							if(!institutionsList.containsKey(cell.getNumericCellValue()))
							{
								instRec = new ScivalInstitutionRecord();
								instRec.setInstitution_ID(cell.getNumericCellValue());
							}
								
							else
							{
								instRec = institutionsList.get(cell.getNumericCellValue());
								System.out.println("Institution ID previously exist");
							}
								
						}
						
							
					}
						
					if(colIndex == 1)
						instRec.setInstitutionName(cell.getStringCellValue());
					if(colIndex == 2)
					{
						if(cell.getNumericCellValue() != 0)
							affiliationID = cell.getNumericCellValue();
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
				
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public void writeCSVFile()
	{
		String scival_out = "scival_instAff_out.csv";
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(scival_out))))
		{
			if(institutionsList.size() >0)
			{
				for(Map.Entry<Double, ScivalInstitutionRecord> entry: institutionsList.entrySet())
				{
					ScivalInstitutionRecord recordInfo = entry.getValue();
					//for each affiliation_ID add entry in out file
					recordInfo.getAffiliationInfo().forEach((key,value) -> {
						try {
							writer.write(entry.getKey() + "," + recordInfo.getInstitutionName() + "," + key + "," + value
									+ "," + recordInfo.getRegion() + "," + recordInfo.getCountry());
						} catch (IOException e) {
							
							e.printStackTrace();
						}
					});
					{
						
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Exception writing to scival out file?!!!!");
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		
		
	}
	class ScivalInstitutionRecord
	{
		private double institution_ID;
		private String institutionName;
		private Map<Double, String> affiliationInfo;
		private String region;
		private String country;
		
		public ScivalInstitutionRecord()
		{
			//affiliationInfo = Collections.synchronizedMap(new LinkedHashMap<Double, String>());
			affiliationInfo = new LinkedHashMap<>();
		}
		//Setters & Getters
		public double getInstitution_ID() {
			return institution_ID;
		}
		public void setInstitution_ID(double institution_ID) {
			this.institution_ID = institution_ID;
		}
		public String getInstitutionName() {
			return institutionName;
		}
		public void setInstitutionName(String institution) {
			this.institutionName = institution;
		}
		public Map<Double,String> getAffiliationInfo() {
			return affiliationInfo;
		}
		public void setAffiliationInfo(double affiliationID, String affiliationName) {
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
