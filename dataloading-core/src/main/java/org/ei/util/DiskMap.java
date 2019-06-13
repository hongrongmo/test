package org.ei.util;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.store.RAMDirectory;
import java.util.*;

public class DiskMap {
    private final static Logger log4j = Logger.getLogger(DiskMap.class);

    private IndexReader reader;
    private IndexWriter writer;

    public static void main(String args[]) throws Exception {
        // DiskMap writeMap = new DiskMap();
        // writeMap.openWrite(args[0]);
        // writeMap.put("test", "test1");
        // writeMap.close();

        DiskMap readMap = new DiskMap();
        //12/24/2014 from eijava
        String codeString = args[1];
        //
        readMap.openRead(args[0], false);
        //String val = readMap.get("US20010000014A1");
        //12/24/2014 from eijava
        String val = readMap.get(codeString);
        //
        System.out.println(val);
        readMap.close();
    }
    
    public HashMap getAllDocs() throws Exception
    {
    	String info = null;
    	String key = null;
    	HashMap hm = new HashMap(); 
    
    	//TermDocs td = reader.termDocs();
    	//TermEnum td = reader.terms();
    	
    	for (int i=0; i<reader.maxDoc(); i++) 
    	{
    	    if (reader.isDeleted(i))
    	        continue;

    	    org.apache.lucene.document.Document doc = reader.document(i);
    	    String docId = doc.get("docId");
    	    Field[] fields =doc.getFields("KEY");
    	    System.out.println("field size = "+fields.length);
    	    for (int j=0;j<fields.length;j++)
    	    {
    	    	System.out.println((String)fields[j].name());
    	    }
    	    // do something with docId here...
    	    //}
    	    //while (td.next()) {
            //int docIndex = td.doc();
            //Document doc = reader.document(docIndex);
            info = doc.get("VALUE");
            key = doc.get("KEY");
            hm.put(key,info);
            System.out.println(i+"\t"+docId+"\t"+key+"\t"+info+"\n");
        }
    	
    	return hm;
    }

    //12/24/2014 from eijava
    public Document document(int i) throws Exception
    {
        return this.reader.document(i);
    }

    public int maxDoc()
    {
        return this.reader.maxDoc();
    }

    public boolean isDeleted(int i)
    {
        return this.reader.isDeleted(i);
    }
    
    //
    
    public void openRead(String dir, boolean inMemory) throws IOException  {
        //log4j.info("dir" + dir);
        if (!inMemory) {
            this.reader = IndexReader.open(dir);
        } else {
            this.reader = IndexReader.open(new RAMDirectory(dir));
        }
        //log4j.info("this.reader.directory().toString()" + this.reader.directory().toString());
    }

    public void openWrite(String dir) throws IOException  {
        this.writer = new IndexWriter(dir, new StandardAnalyzer(), true);
    }

    // 12/31/2014 from eijava
    
    public void openWrite(String dir, boolean flag) throws Exception {
        this.writer = new IndexWriter(dir, new StandardAnalyzer(), flag);
}
    
    public void close() throws IOException  {
        if (reader != null) {
            reader.close();
        } else {
            writer.close();
        }
    }

    public String get(String key) throws IOException  {
        String info = null;
        Term t = new Term("KEY", key);
        TermDocs td = reader.termDocs(t);
        if (td.next()) {
            int docIndex = td.doc();
            Document doc = reader.document(docIndex);
            info = doc.get("VALUE");
        }

        return info;
    }

    public void optimize() throws IOException  {
        this.writer.optimize();
    }

    public void put(String key, String value) throws IOException  {
        Field keyField = new Field("KEY", key, false, true, false);
        Field valueField = new Field("VALUE", value, true, false, false);
        Document doc = new Document();
        doc.add(keyField);
        doc.add(valueField);
        writer.addDocument(doc);
    }

}
