package org.ei.data.encompasspat.stn;
/*
 * Created on Apr 15, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.io.*;
import java.util.*;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.data.CVSTermBuilder;
import java.sql.*;
import java.util.*;

public class EncompassAtomicBaseTableWriter {

	private Perl5Util perl = new Perl5Util();
	private int recsPerFile = -1;
	private int curRecNum = 0;
	private String filename;
	private PrintWriter out;
	private String filepath;
	private int loadnumber;
	private int filenumber = 0;
	private boolean open = false;
	private CVSTermBuilder termBuilder = new CVSTermBuilder();
	private String[] baseTableFields = AtomicBaseTableRecord.baseTableFields;
	//   AtomicRecordFormatter recordFormatter = new AtomicRecordFormatter();

	public EncompassAtomicBaseTableWriter(int recsPerFile, String filename) {
		this.recsPerFile = recsPerFile;
		this.filename = filename;
	}

	public void begin() throws Exception {
		++filenumber;
		out = new PrintWriter(new FileWriter(filename + "." + filenumber));
		open = true;
		curRecNum = 0;
		System.out.println("Startbegitn  ");
	}
	public void writeRec(Hashtable record) throws Exception {

		if (curRecNum >= recsPerFile) {
			if (open) {
				end();
			}
			begin();
		}
		for (int i = 0; i < baseTableFields.length; ++i) {
			String fieldName = baseTableFields[i];
			StringBuffer recordBuf = new StringBuffer();
			String sVal = "";
			if (record == null) {
				System.out.println("Record was null");
			}
			String sbValue = null;
			if (record.containsKey(fieldName)) {
				sbValue = (String) record.get(fieldName).toString();
			}

			if (sbValue != null) {
				sVal = sbValue.toString();
			}
			else {
				sVal = fixField(fieldName);
			}

			if (sVal != null) {
				recordBuf.append(sVal);

			}
			String result = recordBuf.toString();
			String sp = " ";
			if (perl.match("/\\s{2,}/i", result)) {
				result = perl.substitute("s/\\s{2,}/" + sp + "/ig", result);
			}
			result = result.trim().concat("\t");

			out.write(result);
		}

		if (record.containsKey("CT")) {
			StringBuffer sbValue = (StringBuffer) record.get("CT");

			if (sbValue != null && sbValue.length() > 0) {
				StringBuffer terms = addTerms(sbValue);
				out.write(terms.toString());
			}
		}
		out.write("\n");
	}
	private StringBuffer addTerms(StringBuffer cvs) {

		 CVSTermBuilder termBuilder = new CVSTermBuilder();

		 String cv = termBuilder.getNonMajorTerms(cvs.toString());
		 String mh = termBuilder.getMajorTerms(cvs.toString());
		 StringBuffer cvsBuffer = new StringBuffer();

		 String expandedMajorTerms = termBuilder.expandMajorTerms(mh);
		 String expandedMH = termBuilder.getMajorTerms(expandedMajorTerms);
		 String expandedCV1 = termBuilder.expandNonMajorTerms(cv);
		 String expandedCV2 = termBuilder.getNonMajorTerms(expandedMajorTerms);

		 if (expandedCV2.length() > 0)
			 cvsBuffer.append(expandedCV1).append(";").append(expandedCV2);
		 else
			 cvsBuffer.append(expandedCV1);

		 String parsedCV = cvsBuffer.toString();

		 String cvt = termBuilder.getStandardTerms(parsedCV);

		 String cvm = termBuilder.removeRoleTerms(expandedMH);

		 String cvn = termBuilder.getNoRoleTerms(parsedCV);
		 String cva = termBuilder.getReagentTerms(parsedCV);
		 String cvp = termBuilder.getProductTerms(parsedCV);
		 String cvmn = termBuilder.getMajorNoRoleTerms(expandedMH);
		 String cvma = termBuilder.getMajorReagentTerms(expandedMH);
		 String cvmp = termBuilder.getMajorProductTerms(expandedMH);

		 cvt = perl.substitute("s/'/''/g", cvt);
		 cvm = perl.substitute("s/'/''/g", cvm);
		 cvn = perl.substitute("s/'/''/g", cvn);
		 cva = perl.substitute("s/'/''/g", cva);
		 cvp = perl.substitute("s/'/''/g", cvp);
		 cvma = perl.substitute("s/'/''/g", cvma);
		 cvmn = perl.substitute("s/'/''/g", cvmn);
		 cvmp = perl.substitute("s/'/''/g", cvmp);

		 LinkedHashMap mp = new LinkedHashMap();

		 if (cvt.length() > 0)
			 mp.put("cvs", cvt);
		 else
			 mp.put("cvs", "");

		 if (cvm.length() > 0)
			 mp.put("cvm", cvm);
		 else
			 mp.put("cvm", "");

		 if (cvn.length() > 0)
			 mp.put("cvn", cvn);
		 else
			 mp.put("cvn", "");

		 if (cva.length() > 0)
			 mp.put("cva", cva);
		 else
			 mp.put("cva", "");

		 if (cvp.length() > 0)
			 mp.put("cvp", cvp);
		 else
			 mp.put("cvp", "");

		 if (cvma.length() > 0)
			 mp.put("cvma", cvma);
		 else
			 mp.put("cvma", "");

		 if (cvmn.length() > 0)
			 mp.put("cvmn", cvmn);
		 else
			 mp.put("cvmn", "");

		 if (cvmp.length() > 0)
			 mp.put("cvmp", cvmp);
		 else
			 mp.put("cvmp", "");

		 Iterator itr = mp.entrySet().iterator();

		 StringBuffer newTerms = new StringBuffer();

		 while (itr.hasNext()) {
			 Map.Entry entry = (Map.Entry) itr.next();
			 String val = (String) mp.get(entry.getKey());

			 newTerms.append(removeSemiColon(val));

			 if (itr.hasNext())
				 newTerms.append("\t");
		 }

		 return newTerms;

	 }
	public String removeSemiColon(String sVal) {

		   if (sVal.endsWith(";")) {
			   sVal = sVal.substring(0, sVal.length() - 1);
			   return sVal;
		   }
		   else {
			   return sVal;
		   }
	   }

	public void end() throws Exception {
		if (open) {
			out.close();
			open = false;
		}
	}

	private String fixField(String fieldName) throws IOException {

		if (fieldName.equalsIgnoreCase("AS")) {
			return "QQ";
		}
		else if (fieldName.equalsIgnoreCase("LTM")) {
			return "QQ";
		}
		else if (fieldName.equalsIgnoreCase("LT")) {
			return "QQ";
		}
		else {
			return "";
		}
	}

}