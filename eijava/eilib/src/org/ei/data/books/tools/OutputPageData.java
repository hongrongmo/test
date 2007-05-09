package org.ei.data.books.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* This class walks a directory and creates a SQL loader input file
 * output.out
 * 
 * The file is then fed into the following sqlldr command
 * sqlldr.exe AP_PRO1/ei3it@NEPTUNE direct=false data=output.out control=ReferexPages.sqlldr.ctl log=ReferexPages.sqlldr.log bad=ReferexPages.sqlldr.bad silent=FEEDBACK
 * 
 */ 
public class OutputPageData {

    protected static Log log = LogFactory.getLog(OutputPageData.class);

	public static void main(String[] args) throws IOException {

		log.debug("Starting...");

		if (args.length == 0) {
			(new OutputPageData()).crawlDirectory("V:\\BurstAndExtracted");
		}
		else {
			List dirs = java.util.Arrays.asList(args[0].split(System.getProperty("path.separator")));
            log.debug(dirs);
            Iterator itrdirs = dirs.iterator();
            while(itrdirs.hasNext()) {
                String dir = (String) itrdirs.next();
                (new OutputPageData()).crawlDirectory(dir);
            }
			
		}

        log.debug("...done.");

    }
	
	public OutputPageData() throws IOException {
	}
	
	public void processPDF(File pdfFile, Writer out) throws IOException {

		PDF_FileInfo referexbook = new PDF_FileInfo(pdfFile);

        Visitor visitor = null;
        visitor = new SqlLoaderVisitor(out);
        referexbook.accept(visitor);
//
        visitor = new ChapterStartEndVisitor();
        referexbook.accept(visitor);
//        
//        visitor = new LogInfoVisitor();
//        referexbook.accept(visitor);
//
//        visitor = new HtmlTocVisitor();
//        referexbook.accept(visitor);
        
//		boolean hasChapters = false;
//        int chapterLevel = -1;
//        Iterator itr = referexbook.createIterator();
//        while(itr.hasNext()) {
//            Bookmark mk = (Bookmark) itr.next();
//            hasChapters |= (mk.getTitle().toLowerCase().indexOf("chapter") >= 0);
//            if(hasChapters) {
//                chapterLevel = mk.getLevel();
//            }
//        }
//        log.info("Book isbn:" + referexbook.getIsbn() + " chapters: " + hasChapters + " level: " + chapterLevel);
//        if(!hasChapters) {
//          visitor = new LogInfoVisitor();
//          referexbook.accept(visitor);
//        }
	}

    private FilenameFilter pdfFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith("pdf");
        }
    };

    private FileFilter pdfDirFilter = new FileFilter() {
        public boolean accept(File dir) {
            return dir.isDirectory();
        }
    };

    private File[] getFileList(String dir) {
        File[] files = new File[]{};

        File allpdfs = new File(dir);
        if (allpdfs.isDirectory()) {
            files = allpdfs.listFiles(pdfDirFilter);
        }
        return files;
    }
    
    public void crawlDirectory(String dir) {
		
        Writer out = null;
		try {
			out = new PrintWriter(new FileOutputStream("output.out"));

            log.info("Top level PDF Directory: " + dir);

            File[] files = getFileList(dir);
            // all files in the directory are passed through
			for (int i = 0; i < files.length; i++) {
                log.debug("Found PDF file: " + files[i].getName());

                processPDF(files[i], out);
			} // for

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
	}
}