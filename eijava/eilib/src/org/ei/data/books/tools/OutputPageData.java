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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private Writer cout = null;
    private Writer out = null;
    
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

        log.info("...done.");

    }
	
	public OutputPageData() throws IOException {
	}
	
	public void processPDF(File pdfFile, Writer out) throws IOException {

		PDF_FileInfo referexbook = new PDF_FileInfo(pdfFile);

        Visitor visitor = null;

        visitor = new ZeroBookmarkFixerVisitor();
        referexbook.accept(visitor);
    
        visitor = new ChapterStartEndVisitor();
        referexbook.accept(visitor);
    
        // check to see if there are still zeroes in any of the bookmarks
        boolean hasZeroes = false;
        Iterator itr = referexbook.createIterator();
        while(itr.hasNext()) {
            Bookmark mk = (Bookmark) itr.next();
            if((mk.getPage() == 0)) {
                hasZeroes = true;
                log.error(" ZERO BOOKMARK: " + referexbook.getIsbn() + " -- " + mk.toString());
            }
        }
        if(hasZeroes) {
            log.info("=====================================================");
        } 

        Pattern p = Pattern.compile("^\\d(\\.)?[^\\d]");
        Pattern p2 = Pattern.compile("^\\w(\\.)");

        boolean hasChapters = false;
        int chapterLevel = 1;
        itr = referexbook.createIterator();
        while (itr.hasNext()) {
            Bookmark mk = (Bookmark) itr.next();
            String mktitle = mk.getTitle().toLowerCase();
//            log.info(mktitle);
            if((mktitle.indexOf("frontmatter") >= 0) || (mktitle.indexOf("front matter") >= 0)) {
                // skip over frontmatter bookmarks to get to body
                int level  = mk.getLevel();
                while(itr.hasNext()) {
                    Bookmark submk = (Bookmark) itr.next();
                    if(submk.getLevel() > level) {
                        continue;
                    }
                    break;
                }
            }
            hasChapters |= ((mktitle.indexOf("chapter") >= 0));
            if (hasChapters) {
                chapterLevel = mk.getLevel();
                break;
            }
            Matcher m = null;
            m = p.matcher(mktitle);
            hasChapters |= m.find();
            if (hasChapters) {
                chapterLevel = mk.getLevel();
                break;
            }
            m = p2.matcher(mktitle);
            hasChapters |= m.find();
            if (hasChapters) {
                chapterLevel = mk.getLevel();
                break;
            }
        }
        referexbook.setChapterBookmarkLevel(chapterLevel);

//        visitor = new LogInfoVisitor();
//        referexbook.accept(visitor);
        
//        visitor = new SqlLoaderVisitor(out);
//        referexbook.accept(visitor);
        
//      visitor = new ChapterListVisitor(cout);
//      referexbook.accept(visitor);

    
        visitor = new HtmlTocVisitor();
        referexbook.accept(visitor);

    }

    private FilenameFilter pdfFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith("pdf");
        }
    };

    private FileFilter pdfDirFilter = new FileFilter() {
        public boolean accept(File dir) {
//            return dir.isDirectory() && dir.getName().startsWith("012387582x");
            return dir.isDirectory() && !dir.getName().startsWith("978");
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
		
		try {
			out = new PrintWriter(new FileOutputStream("c:\\output.out"));
            cout = new PrintWriter(new FileOutputStream("c:\\chapters.out"));

            log.debug("Top level PDF Directory: " + dir);

            File[] files = getFileList(dir);
            if((files == null) || (files.length == 0)) {
                log.error("No files found to process.");
            }
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
            if(cout != null) {
                try {
                    cout.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
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