package org.ei.dataloading.encompasspat.stn;
/*
 * Created on Apr 15, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.*;
import org.ei.util.GUID;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;

public class LoadPAT {
	private static Perl5Util perl = new Perl5Util();
	private static boolean fix;
	private static boolean toc;
	private static int exitNumber;
	private int counter = 0;
	private int loadNumber;
	public static Hashtable ltht = new Hashtable();

	EncompassAtomicBaseTableWriter baseWriter;
	public LoadPAT(int loadNumber) {
		this.loadNumber = loadNumber;
	}

	public static void main(String args[]) throws Exception {
		//int loadN = Integer.parseInt(args[0]);
		//String infile = args[1];
		//
		System.out.println("Start");
		//C:\javaplatform2\CO\API\data
		//	String infile = "c:/Temp/1998.pat";
	//	String infile = args[1].substring(0, args[1].indexOf("."));
		File file = new File("c:/alit_data/pat/testClob/");
		String[] list = file.list();
		for (int i = 0; i < list.length; i++) {
			File f = new File("c:/alit_data/pat/testClob/" + list[i]);

			if (f.isFile()) {
				String ln = list[i].substring(0, list[i].indexOf("."));
				System.out.println(ln);
				Integer intloadN =
					Integer
						.valueOf(ln);
				System.out.println(intloadN);
				System.out.println("Start2 intloadN"+intloadN);
						int loadN = intloadN.intValue();
				String infile = "c:/alit_data/pat/testClob/"+list[i];
				//String infile = list[i];
				if (args.length == 3) {
					exitNumber = Integer.parseInt(args[2]);
				} else {
					exitNumber = 0;
				}
				EncompassAtomicBaseTableDriver c =
					new EncompassAtomicBaseTableDriver(loadN);
				System.out.println("Start3");
				c.writeBaseTableFile(infile);
				Enumeration en = ltht.keys();
				while (en.hasMoreElements()) {
					System.out.println("::" + (String) en.nextElement());
				}
				System.out.println("load number"+loadN +" completed");
			}
		}
		System.out.println("end");
	}

	public void writeBaseTableFile(String infile) throws Exception {
		BufferedReader in = null;
		try {
			baseWriter =
				new EncompassAtomicBaseTableWriter(
					30000000,
						infile
						+ "."
						+ loadNumber
						+ ".out");
			//"alit0328d.dat");
			in = new BufferedReader(new FileReader(infile));
			System.out.println("Start3 ");
			baseWriter.begin();
			System.out.println("Start4 ");
			writeRecs(in);
			System.out.println("Start5 ");
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
		while ((line = in.readLine()) != null) {
			state = null;
			prevFieldName = fieldName;
			fieldName = null;
			if (exitNumber != 0 && counter == exitNumber) {
				break;
			}
			String c = null;
			int i = 0;
			if (line.length() > 0) {
				c = line.substring(0, 4);
				i = 1;
			}

			//	if (c.equals("DN")) {
			//	state = "endRecord";
			//	} else
			if (line.substring(0, 2).trim().equals("")) {
				state = "continueField";
			} else if (isNewField(line)) {
				state = "beginField";
			}
			if ((state != null) && (state.equals("beginField"))) {
				if (line.length() < 2) {
					System.out.println(line);
				} else {
					fieldName = getNewField(line);
					if (fieldName.equals("DN")) {
						if (alt.record != null && alt.record.size() > 0) {
							baseWriter.writeRec(alt.getRecord());
						}
						alt = new ALitLoad();
						id = new StringBuffer("ept_");
						alt.addRecord(
							"M_ID",
							id.append(new GUID()).toString(),
							"");
						alt.addRecord("LN", Integer.toString(loadNumber), "");
						++counter;
					}
					if (fieldName != null) {
						fieldName = fieldName.toUpperCase();
						data =
							line.substring(
								fieldName.length() + 1,
								line.length());
					}
					alt.addRecord(fieldName, rmLeadSpc(data), fieldNameHolder);
					fieldNameHolder = fieldName;
				}
			}
			/*
			if ((state != null)&&(state.equals("endRecord"))) {
				id = new StringBuffer("emt_");
				if (alt.record != null && alt.record.size() > 0) {
					alt.addRecord("M_ID", id.append(new GUID()).toString(), "");
					alt.addRecord("LN", Integer.toString(loadNumber), "");
					baseWriter.writeRec(alt.getRecord());
					++counter;
				}
				alt = new ALitLoad();
			}
			*/
			if ((state != null) && (state.equals("continueField"))) {
				alt.addRecord("", (rmLeadSpc(line)), fieldNameHolder);
			}
		}
		if (alt.record != null && alt.record.size() > 0) {
			//	id = new StringBuffer("emt_");
			//	alt.addRecord("M_ID", id.append(new GUID()).toString(), "");
			//	alt.addRecord("LN", Integer.toString(loadNumber), "");
			baseWriter.writeRec(alt.getRecord());
		}

	}
	public static String rmLeadSpc(String tagVal) {
		String result = null;
		MatchResult mRes = null;
		int len = 0;
		int rmSpcNum = 0;
		try {
			if (perl.match("/^\\s*/", tagVal)) {
				len = tagVal.length();
				result = perl.substitute("s/^\\s*//", tagVal);
				if ((len - result.length()) > 4) {
					result =
						((len - result.length()) == 5)
							? ("").concat(result)
							: (" ").concat(result);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public boolean isNewField(String line) {
		boolean isNewField = false;
		String tag = null;
		if (perl.match("/^[\\w]*/", line.substring(0, 4))) {
			MatchResult result1 = perl.getMatch();
			tag = result1.group(0);
		}
		if (tag != null) {
			for (int i = 0;
				i < AtomicBaseTableRecord.baseTableFields.length;
				i++) {
				if (AtomicBaseTableRecord
					.baseTableFields[i]
					.equals(tag.trim().toUpperCase())) {
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
		if (perl.match("/^[\\w]*/", line.substring(0, 5))) {
			MatchResult result1 = perl.getMatch();
			tag = result1.group(0);
			tag = tag.trim().toUpperCase();
		}
		if (tag != null) {
			for (int i = 0;
				i < AtomicBaseTableRecord.baseTableFields.length;
				i++) {
				if (AtomicBaseTableRecord.baseTableFields[i].equals(tag)) {
					result = tag;
				}
			}
		}
		return result;
	}
}
