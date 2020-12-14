package org.ei.dataloading.deandashboard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

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
			inFileName = args[0];
		System.out.println("InputFile: " + inFileName);
		readCSVFile();
	}

	private void readCSVFile()
	{
		LinkedHashMap<Double, ScivalInstitutionRecord> institutionsList = new LinkedHashMap<>();
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
						instRec.setInstitution(cell.getStringCellValue());
					if(colIndex == 2)
					{
						if(cell.getNumericCellValue() != 0)
							instRec.setAffiliation_ID(cell.getNumericCellValue());
					}
					if(colIndex == 3)
					{
						if(!cell.getStringCellValue().isEmpty())
							instRec.setAffiliation_Name(cell.getStringCellValue());
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
				
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	class ScivalInstitutionRecord
	{
		private double institution_ID;
		private String institution;
		private double affiliation_ID;
		private String affiliation_Name;
		private String region;
		private String country;
		
		//Setters & Getters
		public double getInstitution_ID() {
			return institution_ID;
		}
		public void setInstitution_ID(double institution_ID) {
			this.institution_ID = institution_ID;
		}
		public String getInstitution() {
			return institution;
		}
		public void setInstitution(String institution) {
			this.institution = institution;
		}
		public double getAffiliation_ID() {
			return affiliation_ID;
		}
		public void setAffiliation_ID(double affiliation_ID) {
			this.affiliation_ID = affiliation_ID;
		}
		public String getAffiliation_Name() {
			return affiliation_Name;
		}
		public void setAffiliation_Name(String affiliation_Name) {
			this.affiliation_Name = affiliation_Name;
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
