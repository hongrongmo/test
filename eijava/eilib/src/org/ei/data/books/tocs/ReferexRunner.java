package org.ei.data.books.tocs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReferexRunner {

  protected static Log log = LogFactory.getLog(ReferexRunner.class);

  private static List<String> isbnList = new ArrayList<String>();

  private static String OPT_VERIFY = "verify";
  private static String OPT_TOC = "toc";
  private static String OPT_WHOLE = "whole";
  private static String OPT_BURST = "burst";
  private static String OPT_CHAPTERS = "chapters";
  private static String OPT_STAMP = "stamp";
  private static String OPT_ISBN = "isbn";
  private static String OPT_HELP = "help";
  private static String OPT_FILES = "files";
  private static String OPT_CLOUD = "cloud";

  public static void main(String[] args) {

    // create Options object
    Options options = new Options();

    // add options
    options.addOption(OPT_VERIFY, false, "verify archives");
    options.addOption(OPT_TOC, false, "generate Table of Contents");
    options.addOption(OPT_WHOLE, false, "create whole PDF file");
    options.addOption(OPT_BURST, false, "burst whole PDF file");
    options.addOption(OPT_CHAPTERS, false, "copy chapters");
    options.addOption(OPT_STAMP, false, "stamp PDF with metadata");
    options.addOption(OPT_HELP, false, "print usage information");
    options.addOption(OPT_CLOUD, false, "create tag cloud");

    // add group of options which only one can be used at a time
    OptionGroup isbnOptions = new OptionGroup();
    isbnOptions.addOption(new Option(OPT_ISBN, true, "one ISBN value to run on"));
    isbnOptions.addOption(new Option(OPT_FILES, true, "path separated list of files to read ISBNs values from"));
    options.addOptionGroup(isbnOptions);

    CommandLineParser parser = new GnuParser();
    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e1) {
      // TODO Auto-generated catch block
      log.error(e1);
    }

    if(cmd.hasOption(OPT_HELP)) {
      // automatically generate the help statement
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("ReferexRunner", options );
      System.exit(0);
    }

    if(cmd.hasOption(OPT_ISBN)) {
      String opt_isbn = cmd.getOptionValue(OPT_ISBN);
      /* manually override isbnList here */
      isbnList = new ArrayList<String>();
      isbnList.add(opt_isbn);
    }
    else {
      try {
        if(cmd.hasOption(OPT_FILES)) {
          String opt_files = cmd.getOptionValue(OPT_FILES);
          if(opt_files != null) {
            List<String> fileList = Arrays.asList(opt_files.split(System.getProperty("path.separator"))); //new ArrayList<String>();

            Iterator<String> fileItr = fileList.iterator();
            while(fileItr.hasNext()) {
              String isbnlist = fileItr.next();
              try {
                log.info("reading ISBNS from " + isbnlist);
                BufferedReader rdr = new BufferedReader(new FileReader(isbnlist));
                while (rdr.ready()) {
                  String aline = rdr.readLine();
                  isbnList.add(aline);
                }
                rdr.close();
              } catch(FileNotFoundException fne) {
                log.error("Skipping missing file: " + isbnlist);
              }
            }
          }
        }
      } catch (IOException ioe) {
        isbnList = new ArrayList<String>();
      }
    }

    if (isbnList.isEmpty()) {
      log.error("ISBN list is empty! Processing ALL encountered isbns.");
    }
    else {
      log.info("Processing " + isbnList.size() + " isbns.");
    }

    ReferexBaseProcessor processor;
    int processed = 0;

    try {

      //processor = new BookCreator(new BookArchiveMapper("^EVB1181"));
      processor = new BookCreator(new BookArchiveMapper());

      if(log.isDebugEnabled()) {
        log.info(processor.showDataSource());
      }

      processor.setIsbnList(isbnList);
      processor.setCheckArchive(cmd.hasOption(OPT_VERIFY));
      processor.setCreateToc(cmd.hasOption(OPT_TOC));
      processor.setCreateCloud(cmd.hasOption(OPT_CLOUD));
      processor.setCreateWholePdf(cmd.hasOption(OPT_WHOLE));
      processor.setBurstWholePdf(cmd.hasOption(OPT_BURST));
      processor.setCopyChapters(cmd.hasOption(OPT_CHAPTERS));
      processor.setStampWholePdf(cmd.hasOption(OPT_STAMP));

      processed = processor.processall();
      log.info("Total processed = " + processed);


      processor = new BookSeriesCreator(new BookSeriesArchiveMapper("EVB1181"));
      processor.setIsbnList(isbnList);
      System.out.println("isbnList= "+isbnList.size());

      processor.setCreateToc(cmd.hasOption(OPT_TOC));
      processor.setCreateWholePdf(cmd.hasOption(OPT_WHOLE));
      processor.setStampWholePdf(cmd.hasOption(OPT_STAMP));
      processor.setBurstWholePdf(cmd.hasOption(OPT_BURST));
      processor.setCopyChapters(cmd.hasOption(OPT_CHAPTERS));

	   if(log.isDebugEnabled()) {
	          log.info(processor.showDataSource());
      }
      processed = processor.processall();
      log.info("processed = " + processed);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
