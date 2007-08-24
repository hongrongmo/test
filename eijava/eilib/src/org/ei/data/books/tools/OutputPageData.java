package org.ei.data.books.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
    public final static String PATH_PREFIX = "E:" + System.getProperty("file.separator") + "WOBL";
    private Writer cout = null;
    private Writer out = null;
    private Writer uout = null;
    
	public static void main(String[] args) throws IOException {

		log.debug("Starting...");

		if (args.length == 0) {
			(new OutputPageData()).crawlDirectory(PATH_PREFIX + System.getProperty("file.separator") + "BurstAndExtracted");
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
	
//    private static final String[] badArray = { "contents", "index", "copyright", "title page", "half title page", "back cover", "table of contents", "limited disclaimer and warranty", "front matter", "frontmatter", "cover", "backmatter", "back matter" };
    private Pattern skips = Pattern.compile("^(bibliography|acknowledgements|body|contributors|limited disclaimer and warranty|table of contents|half title page|cover|back cover|title page|copyright|cover|preface|notation|contents|index|front(\\s?)matter|back(\\s?)matter|afterward|notation|colour plates|related titles|index of <>|about the cd|exam objective map|nomenclature|list of symbols|names list|topics list|praise for  |content|preface to|dedication|contents of volumes in this series|assignments|about the authors|indicies)");
    
    public void processPDF(File pdfFile, Writer out) throws IOException {

		PDF_FileInfo referexbook = new PDF_FileInfo(pdfFile);

        Visitor visitor = null;

        visitor = new ZeroBookmarkFixerVisitor();
        referexbook.accept(visitor);
    
        visitor = new ChapterStartEndVisitor();
        referexbook.accept(visitor);
    
        Iterator itr = referexbook.createIterator();
        // check to see if there are still zeroes in any of the bookmarks
        boolean hasZeroes = false;
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

        visitor = new ChapterMarkerVisitor();
        referexbook.accept(visitor);
        
        Map fixedIsbns = new Hashtable();
        fixedIsbns.put("9781931836562",new Integer(3));
        fixedIsbns.put("9780120121601",new Integer(2));
        fixedIsbns.put("9780750666565",new Integer(4));
        fixedIsbns.put("9781931836630",new Integer(3));
        fixedIsbns.put("9780444519993",new Integer(4));
        fixedIsbns.put("9780080449241",new Integer(4));
        fixedIsbns.put("9780750678865",new Integer(1));
        fixedIsbns.put("9780750647090",new Integer(2));
        fixedIsbns.put("9780444507464",new Integer(2));
        fixedIsbns.put("9780124605305",new Integer(3));
        fixedIsbns.put("9780750666947",new Integer(2)); // Part I, etc.
        fixedIsbns.put("9780750666947",new Integer(2));
        fixedIsbns.put("9780444519481",new Integer(2));
        fixedIsbns.put("9780750668217",new Integer(2));
        fixedIsbns.put("9780080447087",new Integer(2));
        fixedIsbns.put("9781932266009",new Integer(3));
        fixedIsbns.put("9780444513540",new Integer(3));
        
        
        // suggested by Joel during proofing
        fixedIsbns.put("9780444515575",new Integer(1));
        fixedIsbns.put("9780124605299",new Integer(1));
        fixedIsbns.put("9780750647915",new Integer(2));

        fixedIsbns.put("9780750658010",new Integer(1));
        fixedIsbns.put("9780750663953",new Integer(1));
        fixedIsbns.put("9780750658072",new Integer(1));
        fixedIsbns.put("9780750662710",new Integer(1));
                
        
        
        if(fixedIsbns.containsKey(referexbook.getIsbn13())) { 
            itr = referexbook.createIterator();
            int chapterLevel = ((Integer) fixedIsbns.get(referexbook.getIsbn13())).intValue();
            log.info(referexbook.getIsbn13() + " using pre-assigned bookmark levels " + chapterLevel);
            while (itr.hasNext()) {
                Bookmark mk = (Bookmark) itr.next();
                int level  = mk.getLevel();
                if(chapterLevel == level) {
                    Matcher m = skips.matcher(mk.getTitle().toLowerCase());
                    if(!m.find()) {
                        mk.setChapter(true);
                    }
                 }
            }
        } else {
            boolean hasMarkedChapters = false;
            itr = referexbook.createIterator();
            while (itr.hasNext()) {
                Bookmark mk = (Bookmark) itr.next();
                if(mk.isChapter() && !mk.getTitle().toLowerCase().replaceAll("\\s","").startsWith("appendix")) {
                    log.info(referexbook.getIsbn13() + " found Chapters for at level " + mk);
                    hasMarkedChapters = true;
                    break;   
                }
            }
            if(!hasMarkedChapters) {
                log.info(referexbook.getIsbn13() + " setting default Chapter Level to Level 1");
                itr = referexbook.createIterator();
                int defaultLevel = 1;
                while (itr.hasNext()) {
                    Bookmark mk = (Bookmark) itr.next();
                    int level  = mk.getLevel();
                    if(defaultLevel == level) {
                        Matcher m = skips.matcher(mk.getTitle().toLowerCase());
                        if(!m.find()) {
                            mk.setChapter(true);
                        }
                        else {
                            log.debug(referexbook.getIsbn13() + " skipping " + mk.getTitle().toLowerCase());
                        }
                    }
                }
            }
            
        }


//        visitor = new LogInfoVisitor();
//        referexbook.accept(visitor);
//       
//        visitor = new SqlLoaderVisitor(out);
//        referexbook.accept(visitor);
        
        visitor = new BookSQLUpdaterVisitor(uout);
        referexbook.accept(visitor);
        visitor = new ChapterListVisitor(cout);
        referexbook.accept(visitor);
//        visitor = new HtmlTocVisitor();
//        referexbook.accept(visitor);

    }

    private FilenameFilter pdfFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith("pdf");
        }
    };

    private FileFilter pdfDirFilter = new FileFilter() {
        public boolean accept(File dir) {
//            return dir.isDirectory() && 
//            (
//                dir.getName().startsWith("9780750658010") ||
//                dir.getName().startsWith("9780750663953")
//            );
//        
            return dir.isDirectory() && dir.getName().startsWith("9780340740767");
//            return dir.isDirectory();
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
			out = new PrintWriter(new FileOutputStream(PATH_PREFIX + System.getProperty("file.separator") + "boutput.out"));
            cout = new PrintWriter(new FileOutputStream(PATH_PREFIX + System.getProperty("file.separator") + "chapters.out"));
            uout = new PrintWriter(new FileOutputStream(PATH_PREFIX + System.getProperty("file.separator") + "updates.out"));
            cout.write("set PATH=c:\\Fast\\FastPDF\\pdftk;%PATH%\\r\\n");
            
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
            if(uout != null) {
                try {
                    uout.close();
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