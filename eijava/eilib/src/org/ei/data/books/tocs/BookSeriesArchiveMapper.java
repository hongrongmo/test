package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BookSeriesArchiveMapper extends ArchiveMapper {
  protected static Log log = LogFactory.getLog(BookSeriesArchiveMapper.class);

  private static String DEFAULT_PATTERN  =  "EVB1181";

  private Pattern mappattern;

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

  public File[] getBookSeriesFileList(String dir) {
    FilenameFilter bookSeriesArchiveFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
			System.out.println("File= "+dir+" Name= "+name);
            return  mappattern.matcher(dir.getName()).find(); //name.startsWith("EVIBS") || name.startsWith("EVB0");
        }
    };
    return new File(dir).listFiles(bookSeriesArchiveFilter);
  }

  public BookSeriesArchiveMapper(String expression) {
    mappattern = Pattern.compile(expression);
    System.out.println("PATH-EXP= "+expression);
    setArchive_root(System.getProperty("map_archive_root", ReferexBaseProcessor.DEFAULT_ROOT));
    createmap();
  }

  public BookSeriesArchiveMapper() {
    this(BookSeriesArchiveMapper.DEFAULT_PATTERN);
  }

  public void createmap()
  {
	System.out.println("PATH 1= "+getArchive_root());
    File[] series = getBookSeriesFileList(getArchive_root()+"/EVB1181");
    if(series != null) {
      for(int seriesidx = 0; seriesidx < series.length; seriesidx++) {
          log.info((seriesidx + 1) + ". series: " + series[seriesidx]);
          //File[] files = getFileList(series[seriesidx].getAbsolutePath());
          //for (int i = 0; i < files.length; i++) {
              File[] issues = getFileList(series[seriesidx].getAbsolutePath());
              for (int j = 0; j < issues.length; j++) {
                log.info((j + 1) + ". issue: " + issues[j]);
                File xmlFile = new File(issues[j] + System.getProperty("file.separator") + "issue.xml");
                Issue anissue = IssueLoader.getIssue(xmlFile);

                if (anissue != null) {
                  String isbn = cleanIdentifier(anissue.getIsbn());
                  System.out.println("****ISBN= "+isbn);
                  if (isbn.length() == 10) {
                    isbn = convertToIsbn13(isbn);
                  }
                  addArchviePath(isbn,issues[j].getAbsolutePath()) ;
                }
                else
                {
					System.out.println("****** anissue is null");
				}
              } // for issues
          //} // for files
      } // for series
    }
    else
    {
      log.error("No books in " + getArchive_root() + " found matching " + mappattern.pattern());
    }
  }

}
