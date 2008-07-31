package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BookArchiveMapper extends ArchiveMapper {
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
  
  public BookArchiveMapper() {
    createmap();
  }

  public void createmap() 
  {
    File[] archvies = getBookArchvieDirectoryList(ARCHIVE_ROOT);
    for (int arch = 0; arch < archvies.length; arch++) {
        File[] files = getFileList(archvies[arch].getPath());
        for (int i = 0; i < files.length; i++) {
          addArchviePath(files[i].getName(),files[i].getAbsolutePath()) ;         
        } // for files
    } // for archives
  }
}
