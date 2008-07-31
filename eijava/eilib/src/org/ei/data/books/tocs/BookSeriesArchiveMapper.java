package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BookSeriesArchiveMapper extends ArchiveMapper {
  protected static Log log = LogFactory.getLog(BookSeriesArchiveMapper.class);
 
  private String ARCHIVE_ROOT =  "V:\\EW\\";

  public static File[] getFileList(String dir) {
    FileFilter archiveDirFilter = new FileFilter() {
        public boolean accept(File dir) {
            return dir.isDirectory();
        }
    };
    File[] files = new File[]{};
    File allfiles = new File(dir);
    if (allfiles.isDirectory()) {
        files = allfiles.listFiles(archiveDirFilter);
    }
    return files;
  }
  
  public static File[] getBookSeriesFileList(String dir) {
    FilenameFilter bookSeriesArchiveFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.startsWith("EVIBS") || name.startsWith("EVFB");
        }
    };
    return new File(dir).listFiles(bookSeriesArchiveFilter);
  }
 
  public BookSeriesArchiveMapper() {
    createmap();
  }
  public void createmap() 
  {
    File[] series = getBookSeriesFileList(ARCHIVE_ROOT);
    for(int seriesidx = 0; seriesidx < series.length; seriesidx++) {
        //log.info((seriesidx + 1) + ". series: " + series[seriesidx]);
        File[] files = getFileList(series[seriesidx].getAbsolutePath());
        for (int i = 0; i < files.length; i++) {
            File[] issues = getFileList(files[i].getAbsolutePath());
            for (int j = 0; j < issues.length; j++) {
              //log.info((j + 1) + ". issue: " + issues[j]);
              File xmlFile = new File(issues[j] + System.getProperty("file.separator") + "issue.xml");
              Issue anissue = IssueLoader.getIssue(xmlFile);
              if (anissue != null) {
                String isbn = cleanIdentifier(anissue.getIsbn());
                if (isbn.length() == 10) {
                  isbn = convertToIsbn13(isbn);
                }
                addArchviePath(isbn,issues[j].getAbsolutePath()) ;
              }
            } // for issues
        } // for files
    } // for series
  }

}
