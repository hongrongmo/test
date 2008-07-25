package org.ei.data.books.tocs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReferexRunner {

  protected static Log log = LogFactory.getLog(ReferexRunner.class);

  private static List<String> isbnList = new ArrayList<String>();
  
  public static void main(String[] args) {
    try {
      List<String> fileList = new ArrayList<String>();
      fileList.add( "v:\\2007waiting.txt");
      fileList.add( "v:\\2008frontlist.txt");
      fileList.add( "v:\\threeupdates.txt");
      Iterator<String> fileItr = fileList.iterator();
      while(fileItr.hasNext()) {
        String isbnlist = fileItr.next();
        BufferedReader rdr = new BufferedReader(new FileReader(isbnlist));
        while (rdr.ready()) {
          String aline = rdr.readLine();
          isbnList.add(aline);
        }
        rdr.close();
      }
    } catch (IOException ioe) {
      isbnList = new ArrayList<String>();
    }
    /* manually override isbnList here */
    isbnList = new ArrayList<String>();
    isbnList.add("9780123744906");
    isbnList.add("9780750685399");
    isbnList.add("9780750687034");
    isbnList.add("9780123741110");
    isbnList.add("9780123708694");
    isbnList.add("9780123744005");
    isbnList.add("9780750685191");
    isbnList.add("9781597491174");
    isbnList.add("9781597492409");
    isbnList.add("9780123725783");
    
    if (isbnList.isEmpty()) {
      log.error("ISBN list is empty! Processing ALL encountered isbns.");
    }
    else {
      log.info("Processing " + isbnList.size() + " isbns.");
    }

    ArchiveMapper mapper;
    int processed = 0;
    
    try {
      mapper = new BookArchiveMapper();

      BookCreator bookProcessor = new BookCreator();
      bookProcessor.setIsbnList(isbnList);

      bookProcessor.setCheckArchive(true);
//      bookProcessor.setCreateToc(true);
//      bookProcessor.setCreateWholePdf(true);
//      bookProcessor.setBurstWholePdf(true);
//      bookProcessor.setCopyChapters(true);
      
      processed = mapper.map(bookProcessor);
      log.info("Total processed = " + processed);


//      ReferexBaseProcessor processor;
//      processor = new BookSeriesTocTransform();
//      processor.setIsbnList(isbnList);
//      mapper = new BookSeriesArchiveMapper();
//      processed = mapper.map(processor);
//      log.info("Total processed = " + processed);
//
//      processor = new BookSeriesCreator();
//      processor.setIsbnList(isbnList);
//      mapper = new BookSeriesArchiveMapper();
//      processed = mapper.map(processor);
//      log.info("Total processed = " + processed);

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

   
  }
}
