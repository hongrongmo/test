/*
 * $Author:   johna  $
 * $Revision:   1.7  $
 * $Date:   Jul 08 2010 16:54:24  $
 *
*/


package org.ei.struts.emetrics.businessobjects.reports.csv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.io.OutputStream;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import org.apache.commons.beanutils.DynaBean;

import org.ei.struts.emetrics.customer.view.VendorView;
import org.ei.struts.emetrics.customer.view.UserView;

import org.ei.struts.emetrics.businessobjects.reports.Report;
import org.ei.struts.emetrics.businessobjects.reports.Result;
import org.ei.struts.emetrics.businessobjects.reports.ResultProcessor;
import org.ei.struts.emetrics.businessobjects.reports.ResultsSet;
import org.ei.struts.emetrics.businessobjects.reports.csv.CSVProcessor;
import org.ei.struts.emetrics.businessobjects.reports.csv.CSVReportResultProcessor;
import org.ei.struts.emetrics.utils.StringUtils;
import org.ei.struts.framework.exceptions.DatastoreException;

/**
 * <p>Concrete implementation of {@link ResultSet} for an in-Jdo
 * database backed by an XML data file.</p>
 */

public class CSVResultsSet  implements ResultsSet {


    // ----------------------------------------------------------- Constructors


    /**
     * <p>Construct a new ResultSet associated with the specified
     * {@link Report}.
     *
     * @param report The report with which we are associated
     * @param resultsSetId The resultsSet id for this resultset
     */
    public CSVResultsSet(Report report, int resultsSetId) {
        this.report       = report;
        this.resultsSetId = resultsSetId;
    }

    /**
     * <p>Construct a new ResultSet with no value ( required for ODMG)
     */
    public CSVResultsSet() {

    }


    // ----------------------------------------------------- Instance Variables

    /**
     * Logging output for this reports instance.
     */
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * The id for this results set.
     */
    private int resultsSetId;

    private List columnlist;
    /**
     * The {@link Report} with which we are associated.
     */
    private Report report;

    /**
     * The report Id for this result.
     */
    private int reportId;

    /**
     * The {@link Result}s for this ResultsSet, keyed by resultsSetId.
     */
    private Collection results = new ArrayList();

    /**
     * The id for this results set.
     */
    public int getResultsSetId() {
        return (this.resultsSetId);
    }

    public void setResultsSetId(int resultsSetId) {
        this.resultsSetId = resultsSetId;
    }


    /**
     * The Report owning this ResultsSet.
     */
    public Report getReport() {
        return (this.report);
    }

    public void setReport(Report report) {
        this.report = report;
    }

    /**
     * The username for this ResultsSet.
     */
    private String name = null;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * The querystring for this ResultsSet.
     */
    private String queryString = null;

    public String getQueryString() {
        return (this.queryString);
    }

    public void setQueryString(String queryString) {
		this.queryString = StringUtils.normaliseWhitespace(queryString);
    }

    /**
     * The prepared statement fields for querystring of this ResultsSet.
     */
    private String pstmst = null;

    public String getPstmst() {
        return (this.pstmst);
    }

    public void setPstmst(String pstmst) {
    	this.pstmst = pstmst;
    }


    public Collection getResults() {
        return (this.results);
    }

    public void setResults(Collection results) {
        this.results = results;
    }

    // --------------------------------------------------------- Public Methods


    /**
     * Create and return a new {@link ResultsSet} associated with this
     * Report, for the specified id name.
     *
     * @param id Id for which to create a resultsSet
     *
     * @exception IllegalArgumentException if the id is not unique
     *  for this report
     */
    public Result createResult(int resultId) {

		Result result = null;
		try
		{
			result = new CSVResult(this, resultId);
			results.add(result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (log.isInfoEnabled()) {
			//log.info("Creating Result: " + result.toString() );
		}
		return (result);

    }


    /**
     * Populate {@link populate} the results associated with the specified
     * resultsSet.  Start of SQL. If none is found, return <code>null</code>.
     *
     */
    private List records;

    public List populate (Map pStmntValues) throws DatastoreException
    {

    	records = null;

    	ResultProcessor reportProcessor = CSVReportResultProcessor.getInstance();

		try {

			CSVProcessor csvProcessor = CSVProcessor.getInstance();
			records = csvProcessor.executeQuery(this,
										pStmntValues,
									   reportProcessor);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return (records);

    }


    /**
     * Find and return the {@link ResultsSet} associated with the specified
     * resultsSetId.  If none is found, return <code>null</code>.
     *
     * @param resultsSetId ResultsSetId to look up
     */
    public Result findResult(int resultId) {

		Result result = null;
		Result item = null;
		try
		{
			// 5. now iterate over the resultsset to print find the one
			java.util.Iterator iter = results.iterator();
			while (iter.hasNext())
			{
				item = (Result) iter.next();
				if (item.getResultId() == resultId) {
					result = item; // found it
					break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return (result);


    }


    /**
     * Return a String representation of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("<resultsset resultsSetId=\"");
        sb.append(resultsSetId);
        sb.append("\"");
        if (name != null) {
            sb.append(" name=\"");
            sb.append(name);
            sb.append("\"");
        }
        if (reportId > -1) {
            sb.append(" reportId=\"");
            sb.append(report.getReportId());
            sb.append("\"");
        }
        sb.append(">");
        return (sb.toString());

    }

	public void setColumnList(List columns) {
		columnlist = columns;
	}

	public List getColumnList() {
		// TODO Auto-generated method stub
		return columnlist;
	}

	public void toXML(Writer out)
		    throws IOException {

        StringBuffer xml = new StringBuffer();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        xml.append("\n\r");
        xml.append("<!DOCTYPE reports PUBLIC \"-//ProjectCounter//DTD reports//EN\"");
        xml.append("\n\r");
        xml.append(" \"http://www.projectcounter.org/dtd/2004/reports.dtd\">");
        xml.append("\n\r");
        xml.append("<reports xmlns=\"http://www.projectcounter.org/ns/2004/reports\">");
        xml.append("<database_report id=\"").append(this.report.getReportId()).append("\" cop_version=\"2\" cop_report=\"1\" >");

        xml.append("<header>");
        xml.append("<title>").append(this.report.getTitle()).append("</title>");

        // ISO8601 fmt - needs to be UTC time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'-T'HH:mm:ss'Z'");
        String fmtDate = formatter.format(new Date(this.report.getTimestamp()));

        xml.append("<timestamp>").append(fmtDate).append("</timestamp>");
        xml.append(VendorView.toXML());
        xml.append(getUser().toXML());
        xml.append("<customer>").append("").append("</customer>");
        xml.append("</header>");

        xml.append("<database_data>");

            Iterator rows = records.iterator();

// 7/28/13 Zhun fixed the below line bug. It skipped the first row of records.
//            DynaBean mybean = (DynaBean) rows.next();
			DynaBean mybean = null;

            while(rows.hasNext())
            {
				mybean = (DynaBean) rows.next();
				xml.append("<database name=\"").append(mybean.get("database").toString()).append("\" timeout=\"20\">");

				List columns = this.getColumnList();
                Iterator cols = columns.iterator();

                Result measure = (Result) cols.next();
                Result dbcol = (Result) cols.next();

                while(cols.hasNext())
                {
                    Result res = (Result) cols.next();
                    xml.append("<" + ((String)mybean.get(measure.getColumn())).replaceAll(" ","_") + " start=\"").append(res.getName()).append("\" end=\"").append(res.getName()).append("\" >");
                    xml.append(mybean.get(res.getColumn()).toString());
                    xml.append("</" + ((String)mybean.get(measure.getColumn())).replaceAll(" ","_") + ">");
                }
                xml.append("</database>");

            }

        xml.append("</database_data>");
        xml.append("</database_report>");
        xml.append("</reports>");

        out.write(xml.toString());
        return ;
	}


	public void toCSV(Writer out)
	    throws IOException {

        StringBuffer csv = new StringBuffer();

        HSSFWorkbook wb = getWorkbook();
        HSSFSheet sheet = wb.getSheetAt((short) 0);

        for(int x = 0; x <= sheet.getLastRowNum(); x++)
        {

            HSSFRow arow = sheet.getRow((short)x);

            if(arow != null)
            {
                if(arow.getLastCellNum() < 0)
                {
                    // skip completely empty rows
                    continue;
                }
                for(int y = 0; y <= arow.getLastCellNum(); y++)
                {
                    if(y > 0)
                    {
                        csv.append(",");
                    }
                    HSSFCell acell = arow.getCell((short)y);

                    log.debug("\t\tcol: " + y);

                    if(acell != null)
                    {
                        if(acell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                        {
                             // get native double - create object, convert to long, output as string
                             csv.append(String.valueOf((new Double(acell.getNumericCellValue())).longValue()));
                        }
                        else if(acell.getCellType() == HSSFCell.CELL_TYPE_BLANK)
                        {
                            log.debug("Skipping BLANK Cell.");
                            continue;
                        }
                        else if(acell.getCellType() == HSSFCell.CELL_TYPE_STRING)
                        {
                            log.debug("String Cell.");
                            csv.append(acell.getStringCellValue());
                        }
                        else
                        {
                            log.info("UNKNOW Cell TYpe = " + acell.getCellType());
                            csv.append(acell.getStringCellValue());
                        }
                    }
                    else
                    {
                        log.debug("Skipping null Cell: " + x + "," + y);
                    }
                }
            }
            else
            {
                log.debug("Skipping null Row: " + x);
            }
            csv.append("\n");
        }

        out.write(csv.toString());

        return ;
	}

    public void toXLS(OutputStream out)
	    throws IOException {

        HSSFWorkbook wb = getWorkbook();

        // Write the output to the stram
        wb.write(out);

        return;
    }

    private HSSFWorkbook getWorkbook()
    {
        int rowindex = 0;

        System.setProperty("org.apache.poi.util.POILogger","org.apache.poi.util.CommonsLogger");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String fmtDate = formatter.format(new Date(this.report.getTimestamp()));

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(this.report.getDescription());

        // Create a row and put some cells in it. Rows are 0 based.
        HSSFRow row = sheet.createRow((short)rowindex++);
        HSSFCell cell = null;

        // Create a cell and put a value in it.
        cell = row.createCell((short)0);
        cell.setCellValue(this.report.getDescription());
        cell = row.createCell((short)1);
        cell.setCellValue(this.report.getTitle());

        sheet.setColumnWidth((short)0, (short) 6120); // width in "twips" or 1/20th of a point. (72 points = 1 inch)
        sheet.setColumnWidth((short)1, (short) 5120); // width in "twips" or 1/20th of a point. (72 points = 1 inch)
        sheet.setColumnWidth((short)2, (short) 5120); // width in "twips" or 1/20th of a point. (72 points = 1 inch)
        sheet.setColumnWidth((short)3, (short) 3000); // width in "twips" or 1/20th of a point. (72 points = 1 inch)

        row = sheet.createRow((short)rowindex++);
        row.createCell((short)0).setCellValue(getUser().getName());

        row = sheet.createRow((short)rowindex++);
        row.createCell((short)0).setCellValue("Date run:");

        row = sheet.createRow((short)rowindex++);
        row.createCell((short)0).setCellValue(fmtDate);

        HSSFFont fontBoldArial = wb.createFont();
        fontBoldArial.setFontName("Arial");
        fontBoldArial.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        HSSFCellStyle cellStyleTextHeader = wb.createCellStyle();
        cellStyleTextHeader.setFont(fontBoldArial);
        cellStyleTextHeader.setFillForegroundColor(HSSFColor.TURQUOISE.index);
        cellStyleTextHeader.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyleTextHeader.setBorderBottom(HSSFCellStyle.BORDER_THIN);

        HSSFCellStyle cellStyleDateHeader = wb.createCellStyle();
        cellStyleDateHeader.setDataFormat(HSSFDataFormat.getBuiltinFormat("mmm-yyyy"));
        cellStyleDateHeader.setFont(fontBoldArial);
        cellStyleDateHeader.setFillForegroundColor(HSSFColor.TURQUOISE.index);
        cellStyleDateHeader.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyleDateHeader.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyleDateHeader.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

        HSSFCellStyle cellStyleNum = wb.createCellStyle();
        cellStyleNum.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));

	    // headers
	    int cellindex = 0;
        row = sheet.createRow((short)rowindex++);
        row.setHeight((short) 720); // height in "twips" or 1/20th of a point. (72 points = 1 inch)

	    List columns = this.getColumnList();
        Iterator cols = columns.iterator();

        // headers loop
        while(cols.hasNext())
        {
            if((cellindex == 1) || (cellindex == 2))
            {
                cell = row.createCell((short)cellindex);
                cell.setCellValue(((cellindex == 1) ? "Publisher" : "Platform"));
                cell.setCellStyle(cellStyleTextHeader);

                cellindex++;
                continue;
            }

            Result res = (Result) cols.next();

            if((cellindex == 0) || (cellindex == 3))
            {
                cell = row.createCell((short)cellindex);
                cell.setCellValue("");
                cell.setCellStyle(cellStyleTextHeader);

                cellindex++;
                continue;
            }

            cell = row.createCell((short)cellindex++);
            cell.setCellValue(res.getName());
            cell.setCellStyle(cellStyleDateHeader);
        }

        // data
        Iterator rows = records.iterator();
        String lastDB = null;
        while(rows.hasNext())
        {
            row = sheet.createRow((short)rowindex++);

            String database = null;
            String measure = null;

            DynaBean mybean = (DynaBean) rows.next();
            if(lastDB != null && !lastDB.equals(mybean.get("database"))) {
               row = sheet.createRow((short)rowindex++);
            }

            cellindex = 0;
            columns = this.getColumnList();
            cols = columns.iterator();
            while(cols.hasNext())
            {
                if((cellindex == 2))
                {
                    cell = row.createCell((short)cellindex);
                    cell.setCellValue("Engineering Village");

                    cellindex++;
                    continue;
                }

                if(cellindex == 1)
                {
                    cell = row.createCell((short)cellindex);
                    if(database != null)
                    {
                        cell.setCellValue((String) publishers.get(database));
                        database = null;
                    }
                    cellindex++;
                    continue;
                }

                Result res = (Result) cols.next();

                if(cellindex == 0)
                {
                    cell = row.createCell((short)cellindex);
                    cell.setCellValue((String) mybean.get("database"));
                    database = (String) mybean.get("database");
                    lastDB = database;
                    measure =  (String) mybean.get(res.getColumn());
                    cellindex++;
                    continue;
                }

                if(cellindex == 3)
                {
                    cell = row.createCell((short)cellindex);
                    cell.setCellValue(measure);

                    cellindex++;
                    continue;
                }

                cell = row.createCell((short)cellindex++);
                int cellval = 0;
                try {
                    cellval = Integer.parseInt(mybean.get(res.getColumn()).toString());
                    cell.setCellValue(cellval);
                    cell.setCellStyle(cellStyleNum);

                }
                catch(NumberFormatException e)
                {
                    cell.setCellValue(mybean.get(res.getColumn()).toString());
                }
            }
            // add an empty row after every pair of rows
            //if((rowindex % 3) == 0)
            //{
                //row = sheet.createRow((short)rowindex++);
            //}

        }
        return wb;
    }

    private UserView user;
    public void setUser(UserView auser){ this.user = auser; }
    public UserView getUser(){ return user; }


    private static Map publishers = new HashMap();
    static
    {
        publishers.put("Compendex", "Elsevier");
        publishers.put("Inspec", "IEE");
        publishers.put("NTIS", "NTIS");
        publishers.put("EU Patents", "Univentio");
        publishers.put("US Patents", "Univentio");
        publishers.put("EnCompass Lit", "Elsevier");
        publishers.put("EnCompass Pat", "Elsevier");
        publishers.put("Chimica", "Elsevier");
        publishers.put("CBNB", "Elsevier");
        publishers.put("CRCEngnetBase", "CRC");
        publishers.put("USPTO", "USPTO");
        publishers.put("GEOBase", "Elsevier");
        publishers.put("GeoBase", "Elsevier");
        publishers.put("GeoRef", "Elsevier");
        publishers.put("PaperChem", "Elsevier");

    }
}
