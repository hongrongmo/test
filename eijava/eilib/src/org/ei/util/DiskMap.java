<<<<<<< HEAD
package org.ei.util;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.*;

public class DiskMap {

    private IndexReader reader;
    private IndexWriter writer;

    public static void main(String args[]) throws Exception {
        //		DiskMap writeMap = new DiskMap();
        //		writeMap.openWrite(args[0]);
        //		writeMap.put("test", "test1");
        //		writeMap.close();

        DiskMap readMap = new DiskMap();
        String codeString = args[1];
        readMap.openRead(args[0],false);
        String val = readMap.get(codeString);
        System.out.println(val);
        readMap.close();
    }

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

    public void openRead(String dir, boolean inMemory) throws Exception {

        if (!inMemory)
            this.reader = IndexReader.open(dir);
        else
            this.reader = IndexReader.open(new RAMDirectory(dir));
    }

    public void openWrite(String dir) throws Exception {
        this.writer = new IndexWriter(dir, new StandardAnalyzer(), true);
    }

    public void openWrite(String dir, boolean flag) throws Exception {
		       this.writer = new IndexWriter(dir, new StandardAnalyzer(), flag);
	}


    public void close() throws Exception {
        if (reader != null) {
            reader.close();
        }
        else {
            writer.close();
        }
    }

    public String get(String key) throws Exception {
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



    public void optimize() throws Exception {
        this.writer.optimize();
    }

    public void put(String key, String value) throws Exception {
        Field keyField = new Field("KEY", key, false, true, false);
        Field valueField = new Field("VALUE", value, true, false, false);
        Document doc = new Document();
        doc.add(keyField);
        doc.add(valueField);
        writer.addDocument(doc);
    }

}
=======
package org.ei.util;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.*;

public class DiskMap {

    private IndexReader reader;
    private IndexWriter writer;

    public static void main(String args[]) throws Exception {
        //		DiskMap writeMap = new DiskMap();
        //		writeMap.openWrite(args[0]);
        //		writeMap.put("test", "test1");
        //		writeMap.close();

        DiskMap readMap = new DiskMap();
        readMap.openRead(args[0],false);
        String val = readMap.get("US20010000014A1");
        System.out.println(val);
        readMap.close();
    }

    public void openRead(String dir, boolean inMemory) throws Exception {
    	
        if (!inMemory)
            this.reader = IndexReader.open(dir);
        else
            this.reader = IndexReader.open(new RAMDirectory(dir));
    }

    public void openWrite(String dir) throws Exception {
        this.writer = new IndexWriter(dir, new StandardAnalyzer(), true);
    }

    public void close() throws Exception {
        if (reader != null) {
            reader.close();
        }
        else {
            writer.close();
        }
    }

    public String get(String key) throws Exception {
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

    public void optimize() throws Exception {
        this.writer.optimize();
    }

    public void put(String key, String value) throws Exception {
        Field keyField = new Field("KEY", key, false, true, false);
        Field valueField = new Field("VALUE", value, true, false, false);
        Document doc = new Document();
        doc.add(keyField);
        doc.add(valueField);
        writer.addDocument(doc);
    }

}
>>>>>>> 15dca715b02cb03c5fa8fd5c16635f7e802f1eae
