package org.ei.dataloading.encompasspat.stn;
/*
 * Created on Apr 15, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.io.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.zip.*;
import org.ei.util.GUID;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;
import java.util.*;

public class EncompassAtomicBaseTableDriver {
	private static Perl5Util perl = new Perl5Util();
	private static boolean fix;
	private static boolean toc;
	private static int exitNumber;
	private int counter = 0;
	private int loadNumber;
	public static Hashtable ltht = new Hashtable();
	public static final String STOPCHAR = ">>";
	public static final int STOPCHAR1 = 26;

	EncompassAtomicBaseTableWriter baseWriter;
	public EncompassAtomicBaseTableDriver(int loadNumber) {
		this.loadNumber = loadNumber;
	}

	public static void main(String args[]) throws Exception {
		int loadN = Integer.parseInt(args[0]);
		String infile = args[1];
		//int loadN = 1965;
		//	C:\javaplatform2\CO\API\data
		//	String infile = "c:/alit_data/pat/ftoload/1995formatFixed.txt";
		//String infile = "c:/alit_data/pat/testLoad/1965.txt";
		//\alit_data\pat\testLoad
		//\alit_data\pat\ftoload
		//C:\alit_data\pat\ftoload
		System.out.println("Start");
		System.out.println("loadN::" + loadN);
		if (args.length == 3) {
			exitNumber = Integer.parseInt(args[2]);
		} else {
			exitNumber = 0;
		}
		EncompassAtomicBaseTableDriver c = new EncompassAtomicBaseTableDriver(loadN);
		System.out.println("Start1");
		c.writeBaseTableFile(infile);
		System.out.println("End");
	}

	public void writeBaseTableFile(String infile) throws Exception {
		BufferedReader in = null;
		//infile = infile.substring(infile.indexOf("c:/alit_data/pat/"));
		System.out.println("infile:" + infile);
		try {
			baseWriter = new EncompassAtomicBaseTableWriter(30000000, infile + "." + loadNumber + ".out");
			//"alit0328d.dat");
			in = new BufferedReader(new FileReader(infile));
			baseWriter.begin();
			writeRecs(in);
			baseWriter.end();
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	private void writeRecs(BufferedReader in) throws Exception {
		ALitLoad alt = new ALitLoad();
		String state = null;
		String line = null;
		String fieldName = null;
		String fieldNameHolder = "";
		String data = null;
		StringBuffer id = null;
		String prevFieldName = null;
		int temptestcounter = 0;
		long systime = 0;
		long systimeend = 0;
		int counter = 1;
		int lineNumber=0;
		systime = System.currentTimeMillis();
		List records = new Vector();

		while ((line = in.readLine()) != null) {
		  try{
			  lineNumber++;
			state = null;
			prevFieldName = fieldName;
			fieldName = null;
			if (exitNumber != 0 && counter == exitNumber) {
				break;
			}
			String c = null;
			int i = 0;
			if ((line != null) && checkInvChar(line)) {
				break;
			}
			if ((line != null) && line.length()>1 && (line.substring(0, 2).equals(EncompassAtomicBaseTableDriver.STOPCHAR))) {
				break;
			}
			if ((line != null) && (!line.equals("CTA")) && line.length()>1 && (line.substring(0, 2).trim().equals(""))) {
				state = "continueField";
			} else if (isNewField(line)) {
				state = "beginField";
			}
			if ((state != null) && (state.equals("beginField"))) {
				counter++;

				if (line.length() < 2) {
					System.out.println("****ERROR***"+line);
				} else {
					// remove spaces at the end of str
					fieldName = getNewField(line);
					if (fieldName.equals("DN")) {
						if (alt.record != null && alt.record.size() > 0) {
							baseWriter.writeRec(alt.getRecord());

						}
						alt = new ALitLoad();
						id = new StringBuffer("ept_");
						alt.addRecord("M_ID", id.append(new GUID()).toString(), "");
						alt.addRecord("LOAD_NUMBER", Integer.toString(loadNumber), "");
						++counter;
					}

					if ((fieldName != null) && (fieldName.length() < line.length())) {
						fieldName = fieldName.toUpperCase();
						data = line.substring(fieldName.length() + 1, line.length());
					}
					alt.addRecord(fieldName, rmLeadSpc(data), fieldNameHolder);
					fieldNameHolder = fieldName;
				}
			}

			if ((state != null) && (state.equals("continueField"))) {
				alt.addRecord("", (rmLeadSpc(line)), fieldNameHolder);
			}
		   }catch(Exception e){
			   System.out.println("Number="+lineNumber+" LINE="+line);
			   e.printStackTrace();
		   }
		}
		if (alt.record != null && alt.record.size() > 0) {
			baseWriter.writeRec(alt.getRecord());

		}

	}
	public static String rmLeadSpc(String tagVal) {
		String result = null;
		MatchResult mRes = null;
		int len = 0;
		int rmSpcNum = 0;
		try {
			if (perl.match("/^\\s{5,}/i", tagVal)) {
				tagVal = tagVal.substring(5);
			}
			
			//remove tab characters
			if(tagVal.indexOf("\t")>-1){
				System.out.println("found tab");
				tagVal = tagVal.replaceAll("\t", " ");
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return tagVal;
	}

	public boolean isNewField(String line) {
		boolean isNewField = false;
		String tag = null;
		if ((line == null) || (line.equals("CTA"))) {
			return false;
		}
		if (line.length() > 4) {
			if (perl.match("/^[\\w]+/", line.substring(0, 4))) {
				MatchResult result1 = perl.getMatch();
				tag = result1.group(0);
			}
		} else {
			if (perl.match("/^[\\w]+/", line)) {
				MatchResult result1 = perl.getMatch();
				tag = result1.group(0);
				tag = tag.trim().toUpperCase();
			}
		}
		if (tag != null) {
			for (int i = 0; i < AtomicBaseTableRecord.baseTableFields.length; i++) {
				if (AtomicBaseTableRecord.baseTableFields[i].equals(tag.trim().toUpperCase())) {
					isNewField = true;
				}
			}
		}
		return isNewField;
	}

	public String getNewField(String line) {
		line = line.toUpperCase();
		String tag = null;
		String result = null;
		if (line.length() > 4) {
			if (perl.match("/^[\\w]+/", line.substring(0, 5))) {
				MatchResult result1 = perl.getMatch();
				tag = result1.group(0);
				tag = tag.trim().toUpperCase();
			}
		} else {
			if (perl.match("/^[\\w]+/", line)) {
				MatchResult result1 = perl.getMatch();
				tag = result1.group(0);
				tag = tag.trim().toUpperCase();
			}
		}

		if (tag != null) {
			for (int i = 0; i < AtomicBaseTableRecord.baseTableFields.length; i++) {
				if (AtomicBaseTableRecord.baseTableFields[i].equals(tag)) {
					result = tag;
				}
			}
		}
		return result;
	}

	public static boolean checkInvChar(String line) {

		StringBuffer buffer = new StringBuffer();
		char[] sChar = line.toCharArray();
		int i = 0;
		for (int j = 0; j < sChar.length; j++) {
			i = (int) sChar[j];
			if (i == EncompassAtomicBaseTableDriver.STOPCHAR1) {
				return true;
			}
		}

		return false;
	} /**
										 * @param string
										 * @param infile
										 */

}
