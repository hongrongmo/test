package org.ei;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;

import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
 
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Side;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


public class Export {

	 
	    /** The resulting PDF file. */
	    public static final String RESULT
	        = "first_table.pdf";
	 
	    /**
	     * Main method.
	     * @param    args    no arguments needed
	     * @throws DocumentException 
	     * @throws IOException
	     */
	    public static void main(String[] args)
	        throws IOException, DocumentException {
	    	ArrayList<Map<String,String>> table = new ArrayList<Map<String,String>>();
	    	Map<String,Integer> total = new HashMap<String,Integer>();
	    	String weekdate = "";
	    	
	        new Export().createPdf(RESULT,table,total,weekdate);
	    }
	 
	    /**
	     * Creates a PDF with information about the movies
	     * @param    filename the name of the PDF file that will be created.
	     * @throws    DocumentException 
	     * @throws    IOException
	     */
	    public void createPdf(String filename, ArrayList<Map<String,String>> table, Map<String,Integer> totals, String week)
	        throws IOException, DocumentException {
	    	
	    	// step 1
	        Document document = new Document();
	        // step 2
	        /*File file = new File("C:/ws/EV-tools/evdataloadreport/Report.pdf");*/
	        File file = new File(filename);
	        file.createNewFile();
	        
	        FileOutputStream fout = new FileOutputStream(file);
	        PdfWriter.getInstance(document, fout);
	        /*PdfWriter.getInstance(document, new FileOutputStream(filename));*/
	        // step 3
	        document.open();
	        // step 4
	        document.add(createFirstTable(table,totals,week));
	        // step 5
	        document.close();
	    }
	 
	    /**
	     * Creates our first table
	     * @return our first table
	     */
	    public static PdfPTable createFirstTable(ArrayList<Map<String,String>> report, Map<String,Integer> totals, String week) {
	    	// a table with three columns
	        PdfPTable table = new PdfPTable(8);
	       
	        table.setWidthPercentage(100);
	        table.setSpacingBefore(0f);
	        table.setSpacingAfter(0f);
	        
	        // Font
	        Font font = new Font(FontFamily.COURIER, 11f, Font.NORMAL);
	        Font content = new Font(FontFamily.COURIER, 8.0f, Font.NORMAL);
	        Font total = new Font(FontFamily.HELVETICA, 8.0f, Font.BOLD);
	        
	        // the cell object
	        PdfPCell cell;
	        PdfPCell cell1;
	        
	        float[] columnWidths = new float[] {40f, 40f, 40f, 100f , 40f , 40f , 40f , 100f};
            try {
				table.setWidths(columnWidths);
			} catch (DocumentException e) {
				
				e.printStackTrace();
			}
	        if(report !=null && report.size() >0)
	        {
	        	// we add a cell with colspan 4, first row for LoadNumber
	  	      
		        cell = new PdfPCell(new Phrase("LoadNumber: " + report.get(0).get("LOADNUMBER"),font));
		        cell.setColspan(4);
		        cell.setBackgroundColor(new BaseColor(Color.LIGHT_GRAY));
		        cell.setUseBorderPadding(true);
		        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		        cell.setBorder(Rectangle.NO_BORDER);
		        
		        table.addCell(cell);
		        
		     // we add a cell with colspan 4, first row for WeekDate
		        
		        cell1 = new PdfPCell(new Phrase("Week: " + week,font));
		        cell1.setColspan(8);
		        cell1.setBackgroundColor(new BaseColor(Color.LIGHT_GRAY));
		        cell1.setUseBorderPadding(true);
		        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		        cell1.setBorder(Rectangle.NO_BORDER);
		        
		        table.addCell(cell1);
		        
		        
		        //add table headers
		        
		        
		        table.addCell(new Phrase("Dataset",content));
		        table.addCell(new Phrase("Operation",content));
		        table.addCell(new Phrase("Week",content));
		        table.addCell(new Phrase("Source FName",content));
		        table.addCell(new Phrase("Source FCount",content));
		        table.addCell(new Phrase("Loaded Count",content));
		        table.addCell(new Phrase("Rejected Count",content));
		        table.addCell(new Phrase("Reason",content));
		        
		        
		        
	        	for(Map<String,String> item :report)
		        {
		        	table.addCell(new Phrase(item.get("DATASET"),content));
		        	table.addCell(new Phrase(item.get("OPERATION"),content));
		        	table.addCell(new Phrase(item.get("LOADNUMBER"),content));
		        	table.addCell(new Phrase(item.get("SOURCEFILENAME"),content));
		        	table.addCell(new Phrase(item.get("SOURCEFILECOUNT"),content));
		        	table.addCell(new Phrase(item.get("MASTERTABLECOUNT"),content));
		        	table.addCell(new Phrase(item.get("SRC_MASTER_DIFF"),content));
		        	table.addCell(new Phrase(item.get("ERRORMESSAGE"),content));
		        }
	        	
	        	
	        	PdfPCell cell2;
	 	        cell2 = new PdfPCell(new Phrase("Total",total));
	 	        cell2.setColspan(4);
	 	        cell2.setUseBorderPadding(true);
	 	        cell2.setBackgroundColor(new BaseColor(Color.lightGray));
	 	        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
	 	        
	 	        table.addCell(cell2);

	 	        // set totals 
	 	        
	 	       
	 	        	table.addCell(new Phrase(totals.get("TOTALSRCCOUT").toString(),total));
	 	        	table.addCell(new Phrase(totals.get("TOTALLOADEDCOUNT").toString(),total));
	 	        	table.addCell(new Phrase(totals.get("TOTALREJECTEDCOUNT").toString(),total));
	 	        	
	 	        	
	 	        	table.addCell(new Phrase("",total));

	        }
  
	        return table;
	    }
	   
}
	

