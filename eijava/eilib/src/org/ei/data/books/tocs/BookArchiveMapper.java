package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BookArchiveMapper implements ArchiveMapper {
  protected static Log log = LogFactory.getLog(BookArchiveMapper.class);

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
  
  public static File[] getBookArchvieDirectoryList(String dir) {
      FileFilter bookArchiveFilter = new FileFilter() {
          public boolean accept(File dir) {
              return dir.isDirectory() && (dir.getName().startsWith("EVF0") || dir.getName().startsWith("EVI0"));
          }
      };
      return new File(dir).listFiles(bookArchiveFilter);
  }

  public int map(ArchiveProcessor processor) throws IOException
  {
    int count = 0;
    File[] archvies = getBookArchvieDirectoryList(ARCHIVE_ROOT);
    for (int arch = 0; arch < archvies.length; arch++) {
        log.info((arch + 1) + ". archive: " + archvies[arch].getName());
        File[] files = getFileList(archvies[arch].getPath());
        for (int i = 0; i < files.length; i++) {
        
          if(processor.process(files[i].getCanonicalPath()))
          {
            count++;
          }
        } // for files
    } // for archives
    return count;
  }
}
