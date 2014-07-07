package org.ei.data.books.tocs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.ei.tags.AlphaComp;
import org.ei.tags.SizeComp;
import org.ei.tags.Tag;

public class BookCreator extends ReferexBaseProcessor  {

  private static final File xsltFile = new File("xsl\\BookTOC.xsl");
  private ArchiveMapper mapper;

  public BookCreator() {
    mapper = new BookArchiveMapper();
  }

  public BookCreator(ArchiveMapper archivemapper) {
    mapper = archivemapper;
  }


  public boolean process(String isbn) throws IOException {
    boolean result = false;
    String xmlpath = mapper.getArchviePath(isbn);
    File xmlFile = new File(xmlpath + FILE_SEP + "main.xml");
    HashMap bookInfo = getBookInfo(isbn);
    String bookpdf = getWhole_pdfs() + isbn + ".pdf";

    if(isCheckArchive() || isCreateToc() || isCreateWholePdf() || isCopyChapters())
    {
      if (xmlFile.exists()) {
        log.info(isbn + ": " + xmlFile);

        List<IncludeItem> includeitems = IssueLoader.getIssue(xmlFile).getIncludeItems();
        Iterator<IncludeItem> piis = includeitems.iterator();

        if(isCheckArchive()) {
          checkBookArchives(xmlFile, isbn);
        }

        if(isCreateToc()) {
          createToc(xmlFile, isbn);
        }
        if(isCreateWholePdf()) {
          pdfprocessor.createPDF(xmlFile, bookpdf, piis);
        }

        if(isCopyChapters())
        {
          // reset iterator
          piis = includeitems.iterator();
          File destinationfolder = new File(getBurst_extract() + isbn);
          if(!destinationfolder.exists()) {
            destinationfolder.mkdir();
          }
          while (piis.hasNext()) {
            IncludeItem pii = piis.next();
            String foldername = pii.getPii().replaceAll("\\p{Punct}", "");

            try {
              pdfprocessor.copyChapter(xmlFile, isbn, foldername, getBurst_extract());
              String chapterpdf = getBurst_extract() + isbn + FILE_SEP + foldername + ".pdf";
              String stampedchapterpdf = getBurst_extract() + isbn + FILE_SEP + foldername + "_stamped.pdf";
              log.info(chapterpdf);
              try {
                pdfprocessor.stampPDF(new File(chapterpdf),
                    new File(stampedchapterpdf),
                    bookInfo);

                if(new File(chapterpdf).delete())
                {
                  new File(stampedchapterpdf).renameTo(new File(chapterpdf));
                }
              }
              catch(Exception e){
                new File(stampedchapterpdf).delete();
              }
            }
            catch(IOException e)
            {
              log.error(e);
            }
          }
        }
        result = true;
      }
      else {
        log.error("Missing file isbn: " + xmlFile);
      }
    }

    if(isBurstWholePdf()) {
      File bookpdf_file = new File(bookpdf);
      if(bookpdf_file.exists()) {
        File burstdir = new File(getPages() + FILE_SEP + isbn);
        if(!burstdir.exists()) {
          burstdir.mkdir();
        }
        if(burstdir.isDirectory())
        {
          pdfprocessor.burstPDF(new File(bookpdf), getPages() + FILE_SEP + isbn);
        }
      }
      else {
        log.error("Cannot find file to burst: " + bookpdf_file);
      }

    }

    if(isStampWholePdf())
    {
      File bookpdf_file = new File(bookpdf);
      if(bookpdf_file.exists()) {
        String stampedbookpdf = getWhole_pdfs() + FILE_SEP + isbn + "_stamped.pdf";
        pdfprocessor.stampPDF(new File(bookpdf),
            new File(stampedbookpdf),
            bookInfo);

        if(new File(bookpdf).delete())
        {
          new File(stampedbookpdf).renameTo(new File(bookpdf));
          result = true;
        }
      }
      else {
        log.error("Cannot find file to stamp: " + bookpdf_file);
      }
    }
    if(isCreateCloud()) {
      createCloud(isbn);
    }
    return result;
  }

  private boolean createCloud(String isbn) {
    boolean result = false;

    int BUCKETS = 3;
    int TAGS_MAX = 40;

    try {
      File burst = new File(getBurst_extract() + isbn);
      if(!burst.exists()) {
        burst.mkdir();
      }
      if(burst.isDirectory())
      {

        Map<String, Integer> cloud_data = getCloudData(isbn);
        if(cloud_data.size() == 0) {
          log.error("No Keyword data available.");
          return false;
        }
        Iterator<String> itr_keywords = cloud_data.keySet().iterator();
        Tag[] all_tags = new Tag[cloud_data.size()];
        int idx = 0;
        while(itr_keywords.hasNext())
        {
          String keyword = itr_keywords.next();
          Tag booktag = new org.ei.tags.Tag();
          booktag.setTag(keyword);
          booktag.setCount(cloud_data.get(keyword).intValue());
          all_tags[idx++] = booktag;
          //log.info(keyword + " == " + cloud_data.get(keyword).intValue());
        }
        Arrays.sort(all_tags, new SizeComp());

        double max = all_tags[0].getCount();
        double min = all_tags[all_tags.length-1].getCount();
        int loop_max = all_tags.length;
        if(all_tags.length > TAGS_MAX)
        {
          min = all_tags[TAGS_MAX].getCount();
          loop_max = TAGS_MAX;
          Tag[] truncated_tags = new Tag[TAGS_MAX];
          for(int x = 0; x < TAGS_MAX; x++)
          {
            truncated_tags[x] = all_tags[x];
          }
          all_tags = truncated_tags;
          //log.info(" new length " + all_tags.length);
        }
        double buckets = BUCKETS;
        double intervals[] = new double[(int) buckets];
        double dif = max - min;
        double bucketsize = 0.0;
        if(dif < buckets)
        {
          bucketsize = max+1;
        }
        else
        {
          bucketsize = dif/buckets;
        }

        for(int i=0; i<buckets; i++)
        {
          double intervalMax = (min + (bucketsize*(i+1)));
          intervals[i] = intervalMax;
        }

        for(int i=0; i < loop_max; i++)
        {
          Writer sw = new StringWriter();
          Tag tag = all_tags[i];
          int count = tag.getCount();
          sw.write("<a href=\"/controller/servlet/Controller?CID=quickSearchCitationFormat");
          sw.write("&yearselect=yearrange");
          sw.write("&database=131072");
          sw.write("&searchWord1=" + tag.getTag());
          sw.write("&searchWord2=" + isbn);
          sw.write("&boolean1=AND");
          sw.write("&section1=KY");
          sw.write("&section2=BN\"");
          sw.write(" title=\"");
          sw.write(String.valueOf(tag.getCount()));
          if(tag.getCount() == 1)
            sw.write(" occurance\"");
          else
            sw.write(" occurances\"");

          for(int j=0;j<buckets; j++)
          {
            if(count <= intervals[j])
            {
              sw.write(" class=\"");
              sw.write("booktag");
              sw.write(Integer.toString(j+1));
              sw.write("\">");
              sw.write(tag.getTagName());
              break;
            }
          }
          sw.write("</a>");
          sw.close();
          tag.setTagID(sw.toString());
        }

        String cloud_filename = getBurst_extract() + isbn + FILE_SEP + isbn + "_cloud.html";
        log.info(cloud_filename);
        BufferedWriter cloud_file = new BufferedWriter(new FileWriter(cloud_filename));

        cloud_file.write("<div id=\"bookcloud\">");
        cloud_file.newLine();

        Arrays.sort(all_tags, new AlphaComp());

        for(idx = 0; idx < loop_max; idx++) {
          cloud_file.write(all_tags[idx].getTagID());
          cloud_file.write(" ");
          cloud_file.newLine();

        }

        cloud_file.write("</div>");
        cloud_file.newLine();

        cloud_file.close();

      }
    } catch(IOException e) {
        log.error(e);
    }

    return result;
  }

  private boolean createToc(File xmlFile, String isbn) {
    boolean result = false;

    Issue iss = IssueLoader.getIssue(xmlFile);
    if(iss != null) {
      try {
        File burst = new File(getBurst_extract() + isbn);
        if(!burst.exists()) {
          burst.mkdir();
        }
        if(burst.isDirectory())
        {
          Result tresult = new StreamResult(new FileOutputStream(getBurst_extract() + isbn + FILE_SEP + isbn + "_toc.html"));
          log.info(getBurst_extract() + isbn + FILE_SEP + isbn + "_toc.html");
          if(toctransformer.runTransform(xmlFile,xsltFile,tresult)) {
            result = true;
          }
        }
      } catch(IOException e) {
          log.error(e);
      }
    }
      return result;
  }

  private boolean checkBookArchives(File xmlFile, String isbn) {
    boolean flagged = false;

    List<IncludeItem> includeitems = IssueLoader.getIssue(xmlFile).getIncludeItems();
    Iterator<IncludeItem> piis = includeitems.iterator();
    int mainxmlpagetotal = 0;
    int pdfpagetotal = 0;
    while(piis.hasNext()) {
      IncludeItem item = piis.next();
      List<PageRange> pageRanges = item.getPageRanges();
      Iterator<PageRange> ranges = pageRanges.iterator();
      int xmlpages = 0;
      while(ranges.hasNext())
      {
        xmlpages += ranges.next().length();
        mainxmlpagetotal += xmlpages;
      }
      int pdfpages = 0;
      try {
        pdfpages = pdfprocessor.countPages(xmlFile, item);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      pdfpagetotal += pdfpages;
      if(pdfpages != xmlpages) {
        log.error(isbn + " " + item.getPii() + " main.xml length: " + xmlpages + " Chapter main.pdf pages " + pdfpages);
        flagged = true;
      }
    }
    if (flagged) {
      log.error(isbn + " " + " main.xml total: " + mainxmlpagetotal + ". Chapter main.pdf totals: " + pdfpagetotal);
    }

    log.info(isbn + ": passes checks successfully.");
    return flagged;
  }

//  private boolean createSQLLDR(File xmlFile, String isbn) {
//    boolean flagged = false;
//
//    List<IncludeItem> includeitems = IssueLoader.getIssue(xmlFile).getIncludeItems();
//    Iterator<IncludeItem> piis = includeitems.iterator();
//    int pageindex = 1;
//
//    NumberFormat df = new DecimalFormat("0000");
//    df.setMinimumIntegerDigits(4);
//
//    while(piis.hasNext()) {
//      IncludeItem item = piis.next();
//
//      List<PageRange> pageRanges = item.getPageRanges();
//      Iterator<PageRange> ranges = pageRanges.iterator();
//      int chapterstart = pageindex;
//      while(ranges.hasNext())
//      {
//        PageRange range = ranges.next();
//        List pageseq = range.sequence();
//        Iterator pages = pageseq.iterator();
//        while(pages.hasNext())
//        {
//          String page_label = (String) pages.next();
//          String pagefilename = df.format(pageindex);
//          log.info("pag_" + isbn + "_" + pageindex + "," + item.getPii() + "," + pageindex + "," + page_label + "," + item.getTitle() + "," + chapterstart + ",/data/books/EW/BurstAndExtracted/" + isbn + "/pg_" + pagefilename + ".txt");
//          pageindex++;
//        }
//      }
//    }
//
//    return flagged;
//  }
}