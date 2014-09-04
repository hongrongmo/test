package org.ei.struts.emetrics.businessobjects.reports.csv;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.display.ColumnDecorator;
import org.apache.taglibs.display.Decorator;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.emetrics.businessobjects.reports.DataRow;
import org.ei.struts.emetrics.businessobjects.reports.Report;
import org.ei.struts.emetrics.businessobjects.reports.Result;
import org.ei.struts.emetrics.businessobjects.reports.ResultProcessor;
import org.ei.struts.emetrics.businessobjects.reports.ResultsSet;

/**
* Result processor that will form <CODE>User</CODE> objects from the given result
* set.
*
*/
public class CSVReportResultProcessor implements ResultProcessor {

  /**
  * The <code>Log</code> instance for this application.
  */
  private Log log = LogFactory.getLog("CSVReportResultProcessor");

  /**
  * The singleton instance of this class.
  */
  private static ResultProcessor thisProcessor;

  /**
  * Default constructor.  Private so no outside instantiation.
  */
  private CSVReportResultProcessor() {
    //Private so no outside instantiation
  }

  /**
  * Get an instance of this result processor.
  *
  * @return an instance of this result processor
  */
  public static ResultProcessor getInstance() {

    //Since this class is stateless, we can use a singleton
    if(thisProcessor == null) {
      thisProcessor = new CSVReportResultProcessor();
    }
    return thisProcessor;
  }

  /**
  * Takes a result set and munges it into whatever type of object or set of
  * objects it should be.
  *
  * @param rs     The result set to traverse in forming these objects
  *
  * @return the object or set of objects that this result set represents
  *
  * @exception SQLException
  *                   this exception that can result from mishandling of a result set.  However, the developer
  *                   shouldn't have to worry about the overhead of handling these exceptions, so let
  *                   it bubble up.
  */
  public List process(ResultsSet rs, List data)
  {
    Report report = rs.getReport();

    // report definition <table> element
    // set table row definitions for display taglib
    Collection tablerows = report.getTableRows();
    Collection columnlist = new ArrayList();

    // add column header for the default column
    // <dataheader... in the report definition <table> element
    int resultssetid = report.getDataHeader().getResultsSetId();
    int resultid = report.getDataHeader().getResultId();
    Result dataheader = report.getResultsSet().findResult(resultid);

    Decorator columnHeaderDecorator =  null;

    if(dataheader.getType() != null) {
      try {
        Class c = Class.forName(dataheader.getType());

        if (Class.forName("org.apache.taglibs.display.Decorator").isAssignableFrom(c)) {
          columnHeaderDecorator = (Decorator) c.newInstance();
        }
        } catch(Exception e){
          // handle all the different types of exceptions
          // by ignoring them and leaving decorator as null
        }
      }

      // Transponse - don't add this to collection of columns
      // we will only have to remove it before we transpose
      // dataheader column becomes the header row
      // columnlist.add(result);

      // add column header for each element in a row of the results
      // <datarow... in the report definition <table> element
      Iterator iter = tablerows.iterator();
      while (iter.hasNext()) {
        DataRow datarow = (DataRow) iter.next();
        resultssetid = datarow.getResultsSetId();
        resultid = datarow.getResultId();

        Result result = report.getResultsSet().findResult(resultid);

        // check data set to make sure values exists under this column
        // otherwise do not add to output
        DynaBean row = (DynaBean) (data.iterator()).next();
        if(row.get(result.getColumn()) != null) {
          columnlist.add(result);
        }
      }

      // set the raw data for rendering the data table
      //request.setAttribute(Constants.REPORT_DATA_KEY, data);
      //request.setAttribute(Constants.DISPLAYTABLE_KEY, columnlist);

      // transpose
      List transposed = new ArrayList();
      List newcollist = new ArrayList();

      // This is the 'pivot' square 'A1' - doesn't change
      // It will be a header and was also a header in the old table
      // use the value from the old dataheader
      // create a new column key, measure, for storing the old column names as the frst entry in each row
      Result newcolumn = new CSVResult();
      newcolumn.setType("org.ei.struts.emetrics.labels.StringLabelDecorator");
      newcolumn.setColumn(dataheader.getColumn());
      newcolumn.setName(dataheader.getName());
      newcollist.add(newcolumn);


      // peek ahead and create another column for any extra attributes defined
      // on the result - i.e. Database name
      Iterator icol = columnlist.iterator();
      if(icol.hasNext())
      {
        Result column  = (Result) icol.next();
        if(column.getDatabase() != null)
        {
          newcolumn = new CSVResult();
          newcolumn.setType("org.ei.struts.emetrics.labels.StringLabelDecorator");
          newcolumn.setColumn("database");
          newcolumn.setName("Database");
          newcollist.add(newcolumn);
        }
      }

      // transpose the original columns into the first entry in each row
      // under the property name 'measure'
      icol = columnlist.iterator();
      while(icol.hasNext())
      {
        Result column  = (Result) icol.next();
        DynaBean newrow = new LazyDynaBean();
        newrow.set(dataheader.getColumn(),column.getName());
        transposed.add(newrow);
      }

      // transpose the data
      Iterator irow = data.iterator();
      int rowindex = 0;
      while(irow.hasNext()) {
        DynaBean row = (DynaBean) irow.next();

        // transponse this row by taking each value
        // and storing it under the first row entry value
        // as the transposed column key
        icol = columnlist.iterator();
        int colindex = 0;
        while(icol.hasNext())
        {
          // This is lazy, any changes to newrow will be reflected in object which remains in the list
          DynaBean newrow = (DynaBean) transposed.get(colindex);
          Result column  = (Result) icol.next();
          // create the new row by putting the original column values
          // under the new column name
          newrow.set( (row.get(dataheader.getColumn())) + "-" + rowindex , row.get(column.getColumn()));

//          log.info(" " + dataheader.getColumn() + " Key " + row.get(dataheader.getColumn()) + "-" + rowindex);
          if(column.getDatabase() != null)
          {
            newrow.set("database", column.getDatabase());
          }
          colindex++;
        }

        // create the new column list using the value stored under the dataheader
        // column name in this, the original untransposed,  row
        newcolumn = new CSVResult();
        // This is the decorator used to format the values stored under this column key (or 'column')
        newcolumn.setType("org.ei.struts.emetrics.labels.BigDecimalDecorator");

        // This is the key the value is stored under in each row for this column
        newcolumn.setColumn(row.get(dataheader.getColumn()) + "-" + rowindex );

        // This is the column label - format this now
    //    log.info(" dataheader.getColumn() = " + dataheader.getColumn());
        if(row.get(dataheader.getColumn()) != null)
        {
          String newcolumnheader = row.get(dataheader.getColumn()).toString();
          if(columnHeaderDecorator != null) {
            newcolumnheader = ((ColumnDecorator) columnHeaderDecorator).decorate(row.get(dataheader.getColumn()));
          }

  //        log.info("newcolumnheader is " + newcolumnheader);

          newcolumn.setName(newcolumnheader);
          newcollist.add(newcolumn);
        }
        rowindex++;
      }
      rs.setColumnList(newcollist);
      return transposed;
    }

  }
