package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BookArchiveMapper extends ArchiveMapper {
  protected static Log log = LogFactory.getLog(BookArchiveMapper.class);

  private static String DEFAULT_PATTERN  =  "^EV[FI]0";

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

  public File[] getBookArchvieDirectoryList(String dir) {
      FileFilter bookArchiveFilter = new FileFilter() {
          public boolean accept(File dir) {
            return dir.isDirectory() && mappattern.matcher(dir.getName()).find(); //(dir.getName().startsWith("EVF0") || dir.getName().startsWith("EVI0"));
          }
      };
      return new File(dir).listFiles(bookArchiveFilter);
  }

  public BookArchiveMapper(String expression) {
    mappattern = Pattern.compile(expression);
    setArchive_root(System.getProperty("map_archive_root", ReferexBaseProcessor.DEFAULT_ROOT));
    createmap();
  }

  public BookArchiveMapper() {
    this(BookArchiveMapper.DEFAULT_PATTERN);
  }

  public void createmap()
  {
    File[] archvies = getBookArchvieDirectoryList(getArchive_root());
    if(archvies != null) {
      for (int arch = 0; arch < archvies.length; arch++) {
        File[] files = getFileList(archvies[arch].getPath());
        for (int i = 0; i < files.length; i++) {
          addArchviePath(files[i].getName(),files[i].getAbsolutePath()) ;
        } // for files
      } // for archives
    }
    else
    {
      log.error("No books in " + getArchive_root() + " found matching " + mappattern.pattern());
    }
  }
}
