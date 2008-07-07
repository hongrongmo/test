package org.ei.data.books.tocs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReferexRunner {

  protected static Log log = LogFactory.getLog(ReferexRunner.class);

  private static List<String> isbnList = new ArrayList<String>();
  
  public static void main(String[] args) {
    try {
      BufferedReader rdr = new BufferedReader(new FileReader("v:\\2008frontlist.txt"));
      while (rdr.ready()) {
        String aline = rdr.readLine();
        isbnList.add(aline);
      }
      rdr.close();
    } catch (IOException ioe) {
      isbnList = new ArrayList<String>();
    }
    /* manually override isbnList here */
//    isbnList = new ArrayList<String>();
//    isbnList.add("9781597492669");

    if (isbnList.isEmpty()) {
      log.error("ISBN list is empty! Processing ALL encountered isbns.");
    }

    ReferexBaseProcessor processor;
    ArchiveMapper mapper;
    int processed = 0;
    
    try {
      processor = new BookTocTransform();
      processor.setIsbnList(isbnList);
      mapper = new BookArchiveMapper();
      processed = mapper.map(processor);
      log.info("Total processed = " + processed);

//      processor = new BookSeriesTocTransform();
//      processor.setIsbnList(isbnList);
//      mapper = new BookSeriesArchiveMapper();
//      mapper.map(processor);
//
      processor = new BookCreator();
      processor.setIsbnList(isbnList);
      mapper = new BookArchiveMapper();
      processed = mapper.map(processor);
      log.info("Total processed = " + processed);

//     
//      processor = new BookSeriesCreator();
//      processor.setIsbnList(isbnList);
//      mapper = new BookArchiveMapper();
//      mapper.map(processor);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

   
  }
}
