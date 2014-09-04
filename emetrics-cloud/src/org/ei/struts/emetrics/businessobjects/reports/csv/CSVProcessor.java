package org.ei.struts.emetrics.businessobjects.reports.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.emetrics.businessobjects.ranges.IRange;
import org.ei.struts.emetrics.businessobjects.reports.ResultProcessor;
import org.ei.struts.emetrics.businessobjects.reports.ResultsSet;
import org.ei.struts.emetrics.businessobjects.reports.Result;
import org.ei.struts.emetrics.businessobjects.reports.Report;
import org.ei.struts.emetrics.businessobjects.reports.DataRow;
import org.ei.struts.emetrics.labels.MonthYearDecorator;

public class CSVProcessor {

	private static final String SEPARATOR = "\\t";
	private Log log = LogFactory.getLog(CSVProcessor.class);
	private static CSVProcessor _instance = null;

	private CSVProcessor() {
	}

	public static CSVProcessor getInstance() {
		if (_instance == null) {
			_instance = new CSVProcessor();
		}
		return _instance;
	}

	public List executeQuery(ResultsSet resultsSet, Map pStmntValues,
			ResultProcessor processor) throws Exception {

		List results = new ArrayList();
		String filename = ((String) pStmntValues.get("path"))
				.concat((String) pStmntValues.get("filename"));
		try {
			results = handleQuery(resultsSet, pStmntValues, processor);
		} catch (IOException e) {
			String message = "Could not find the file for " + filename;
			log.error(message);
		} catch (Exception e) {
			String message = "Some other error occurred";
			log.error(message, e);
		}

		// And return its results
		return (results);
	}

	protected List handleQuery(ResultsSet resultsSet, Map pStmntValues,
			ResultProcessor processor) throws IOException {

		List results = new ArrayList();
		BufferedReader in = null;

		try {

			String filename = ((String) pStmntValues.get("path"))
					.concat((String) pStmntValues.get("filename"));
			File inputFile = new File(filename);
			IRange range = (IRange) pStmntValues.get("range");

			// Get the results from the file
			in = new BufferedReader(new FileReader(inputFile));

			log.debug("File " + filename
					+ " successfully opened. Last modified = "
					+ new Date(inputFile.lastModified()));

			Report report = resultsSet.getReport();
			report.setTimestamp(inputFile.lastModified());

			String[] headers = {};
			DynaBean myBean = new LazyDynaBean();

			// column headers
			String record = in.readLine();
			if (record != null) {
				headers = record.split(CSVProcessor.SEPARATOR);
			}

			if (report.getReportId() == 720) {
				report.setTableRows(new ArrayList());
				resultsSet = report.createResultsSet(report.getReportId() + 1);
				int resultIdbase = resultsSet.getResultsSetId() * 10;
				Result result = resultsSet.createResult(resultIdbase);
				result.setType("org.ei.struts.emetrics.labels.MonthYearDecorator");
				result.setName(headers[0]);
				result.setColumn(headers[0]);

				for (int h = 1; h < headers.length; h++) {
					result = resultsSet.createResult(resultIdbase + h);
					result.setType("org.ei.struts.emetrics.labels.BigDecimalDecorator");
					result.setName(headers[h]);
					result.setColumn(headers[h]);

					DataRow datarow = report.createDataRow(resultIdbase + h);
					datarow.setResultsSetId(resultsSet.getResultsSetId());
					datarow.setResultId(resultIdbase + h);
				}
			}

			if ((report.getReportId() == Constants.MARKETER_DATABASE_COMBO)
					|| (report.getReportId() == Constants.CUSTOMER_DATABASE_COMBO)) {
				report.setTableRows(new ArrayList());
				resultsSet = report.createResultsSet(report.getReportId() + 1);
				int resultIdbase = resultsSet.getResultsSetId() * 10;
				Result result = resultsSet.createResult(resultIdbase);
				result.setType("org.ei.struts.emetrics.labels.DatabaseMaskDecorator");
				result.setName(headers[0]);
				result.setColumn(headers[0]);

				MonthYearDecorator formatter = new MonthYearDecorator();
				for (int h = 1; h < headers.length; h++) {
					result = resultsSet.createResult(resultIdbase + h);
					result.setType("org.ei.struts.emetrics.labels.BigDecimalDecorator");
					result.setName(formatter.decorate(headers[h]));
					result.setColumn(headers[h]);

					DataRow datarow = report.createDataRow(resultIdbase + h);
					datarow.setResultsSetId(resultsSet.getResultsSetId());
					datarow.setResultId(resultIdbase + h);
				}
			}

			Calendar recordtime = Calendar.getInstance();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

			Date recorddate = null;

			DynaBean sumBean = new LazyDynaBean();
			boolean sumYTD = false;
			int thisYear = Calendar.getInstance().get(Calendar.YEAR);

			Collection resultcolumns = resultsSet.getResults();

			// read column values
			while ((record = in.readLine()) != null) {
				myBean = new LazyDynaBean();
				String[] fields = record.split(CSVProcessor.SEPARATOR);
				try {
					recorddate = formatter.parse(fields[0]);
					recordtime.setTime(recorddate);
				} catch (ParseException e) {
					recorddate = null;
				}

				if (recorddate == null) {

					try {
						formatter = new SimpleDateFormat("yyyyMM");
						recorddate = formatter.parse(fields[0]);
						recordtime.setTime(recorddate);
						recordtime.set(Calendar.DAY_OF_MONTH, 1);
					} catch (ParseException e) {
						recorddate = Calendar.getInstance().getTime();
					}
				} // if

				// check to see if record date is not in the range
				if ((range != null) && (!range.isWithinRange(recordtime))) {
					// skip record
					continue;
				}

				// check to see if we should sum these values for YTD
				if (report.getReportId() == Constants.COUNTER2_DATABASE_REPORT1_ID) {
					sumYTD = true;
				}

				Iterator iter = resultcolumns.iterator();
				while (iter.hasNext()) {
					Result rescol = (Result) iter.next();

					int f = 0;
					String columnHeader = "";
					for (; f < headers.length; f++) {
						if (headers[f].equalsIgnoreCase(rescol.getColumn())) {
							columnHeader = headers[f].toLowerCase();
							try {
								// myBean.set(columnHeader,
								// Long.valueOf(fields[f]));
								myBean.set(columnHeader, fields[f]);
							} catch (NumberFormatException e) {
								myBean.set(columnHeader, fields[f]);
								log.error("error setting column value", e);
							} catch (ArrayIndexOutOfBoundsException e) {
								myBean.set(columnHeader, Constants.EMPTY_STRING);
								log.error("error setting column value", e);
							} catch (Exception e) {
								log.error("error setting column value", e);
							}
							break;
						}
					} // for

					if ((f > 0) && (sumYTD)) {
						try {
							BigDecimal sumcol = new BigDecimal(0);
							if (sumBean.get(columnHeader) != null) {
								sumcol = (BigDecimal) sumBean.get(columnHeader);
							}
							sumcol = sumcol.add(new BigDecimal(fields[f]));
							sumBean.set(columnHeader, sumcol);
						} catch (ArrayIndexOutOfBoundsException e) {
							sumYTD = false;
						} catch (NumberFormatException nfe) {
							sumYTD = false;
						}
					} // if

				} // while

				results.add(myBean);
				sumYTD = false;
			} // while reading file

			if ((report.getReportId() != Constants.MARKETER_DATABASE_COMBO)
					&& (report.getReportId() != Constants.CUSTOMER_DATABASE_COMBO)) {
				Collections.reverse(results);
			}

			// check to see if at least one day of current calendar year is
			// within requested date range
			// or else do not show current calendar YTD number
			if ((report.getReportId() == Constants.COUNTER2_DATABASE_REPORT1_ID)) {

				// add YTD sum to results
				sumBean.set(headers[0].toLowerCase(), Constants.YTD);
				results.add(sumBean);

				// walk all columns
				Iterator iter = resultcolumns.iterator();
				while (iter.hasNext()) {
					Result rescol = (Result) iter.next();
					String colname = rescol.getColumn();

					try {
						// examine the sum column entry
						long colval = Long.parseLong(sumBean.get(colname)
								.toString());

						if (colval == 0) {
							// if it is zero, remove all the corresponding
							// column entries from the results
							Iterator itrRows = results.iterator();
							while (itrRows.hasNext()) {
								Map arowmap = ((LazyDynaBean) itrRows.next())
										.getMap();
								arowmap.remove(colname);
							}
						}
					} catch (NumberFormatException e) {
						log.error("error parsing "
								+ sumBean.get(colname).toString(), e);
					}
				} // while
			} // if

			// Process the results
			if (report.getReportId() == Constants.COUNTER2_DATABASE_REPORT1_ID) {
				results = processor.process(resultsSet, results);
			}

		} catch (IOException e) {

			log.error("Could not open the buffered reader for ", e);

			// And rethrow as our runtime exception
			throw e;
		} catch (Exception e) {

			log.error(e);

			// And rethrow as our runtime exception
		}

		finally {
			if (in != null) {
				in.close();
			}
		}

		// Return the results.
		return (results);
	} // end

}