package org.ei.dataloading.encompasspat.stn;
import java.util.*;
import org.apache.oro.text.regex.*;
import org.apache.oro.text.perl.*;

/**
 * @author Tsolovye
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ALitLoad {
	public static Perl5Util perl = new Perl5Util();
	MatchResult mRes = null;
	public String curTagHolder = null;
	public StringBuffer tmpTagValPart = null;
	public static Hashtable htdelim = new Hashtable();
	public static Hashtable htconcat = new Hashtable();
	public Hashtable record = new Hashtable();
	public LL l = new LL();
	public PAT pat = new PAT();
	public PRI pri = new PRI();

	public Hashtable getRecord() {
		String ll = l.getL();
		StringBuffer pt = pat.getPAT();
		StringBuffer pi = pri.getPRI();
		if (ll != null) {
			ll = removeSpc(ll.toString());
			this.record.put("LL", new StringBuffer(ll));
		}
		if (pt != null) {
			this.record.put("PAT", pt);
		}
		if (pi != null) {
			this.record.put("PRI", pi);
		}
		addCas(this);
		return this.record;
	}

	public void addRecord(
		String fieldName,
		String fieldVal,
		String fieldNameHolder) {
		StringBuffer buffieldVal = null;
		if ((fieldName != null)
			&& (ALitStat.htconcat.containsKey(fieldName))) {
			concatTag(
				fieldName,
				fieldVal,
				(String) ALitStat.htconcat.get(fieldName),
				this);
			buffieldVal = this.tmpTagValPart;
		} else {
			buffieldVal = new StringBuffer(fieldVal);
		}
		if ((fieldName == null) || (fieldName.trim().equals(""))) {
			fieldName = fieldNameHolder;
		}
		if (fieldName.equals("ATM")) {
			if (this.record.containsKey("ATM1")) {
				fieldName = "ATM1";
			} else {
				fieldName = splitField(fieldName, this);
			}
		}
		ALitLoad.tagFactory(fieldName, buffieldVal, (ALitLoad) this);
	}

	public static void tagFactory(
		String tmpTag,
		StringBuffer tmpTagVal,
		ALitLoad lt) {
		String result = null;
		StringBuffer resultBuf = new StringBuffer();
		StringBuffer returnTagVal = null;
		String dspc = "  ";
		String spc = " ";
		lt.tmpTagValPart = tmpTagVal;
		if (((tmpTag == null) || (tmpTag.equals("")))
			&& ((lt.curTagHolder != null) || (!lt.curTagHolder.equals("")))) {
			tmpTag = lt.curTagHolder;
		}
		if (tmpTag.equals("LL")) {
			returnTagVal = new StringBuffer("");
			lt.l.setL(tmpTagVal.toString());
		} else if (tmpTag.equals("PC")) {
			returnTagVal = tmpTagVal;
			lt.pat.setPC(replaceDelim(tmpTagVal.toString(), ";", ""));
		} else if (tmpTag.equals("PN")) {
			returnTagVal = tmpTagVal;
			lt.pat.setPN(replaceDelim(tmpTagVal.toString(), ";", ""));
		} else if (tmpTag.equals("PD")) {
			returnTagVal = tmpTagVal;
			lt.pat.setPD(replaceDelim(tmpTagVal.toString(), ";", ""));
		} else if (tmpTag.equals("AC")) {
			returnTagVal = tmpTagVal;
			lt.pri.setAC(replaceDelim(tmpTagVal.toString(), ";", ""));
		} else if (tmpTag.equals("AD")) {
			returnTagVal = tmpTagVal;
			lt.pri.setAD(replaceDelim(tmpTagVal.toString(), ";", ""));
		} else if (tmpTag.equals("AP")) {
			returnTagVal = tmpTagVal;
			lt.pri.setAP(replaceDelim(tmpTagVal.toString(), ";", ""));
		} else if (tmpTag.equals("AB")) {
			returnTagVal = lt.tmpTagValPart;
		} else {
			returnTagVal = tmpTagVal;
		}

		if (returnTagVal != null) {
			returnTagVal = replaceSpecElements(returnTagVal.toString());
		}
		if (ALitStat.htdelim.containsKey(tmpTag)) {
			returnTagVal =
				replaceDelim(
					returnTagVal.toString(),
					"^",
					(String) ALitStat.htdelim.get(tmpTag.trim()));
		}
		if (ALitStat.htdollardelim.containsKey(tmpTag)) {
			returnTagVal =
				replaceDelim(
					returnTagVal.toString(),
					"$",
					(String) ALitStat.htdollardelim.get(tmpTag.trim()));
		}
		result = perl.substitute("s/\f//g", returnTagVal.toString());
		result = perl.substitute("s/\n//g", result);
		result = perl.substitute("s/\r//g", result);
		result = perl.substitute("s/\t//g", result);
		result = perl.substitute("s/\b//g", result);
		result = perl.substitute("s/\0x09//g", result);
		result = perl.substitute("s/" + dspc + "/" + spc + "/g", result);
		if (lt.record.containsKey(tmpTag.toUpperCase())) {
			StringBuffer tmp =
				(StringBuffer) lt.record.get(tmpTag.toUpperCase());
			tmp.append(result);
		} else {
			lt.record.put(tmpTag.toUpperCase(), new StringBuffer(result));
		}

	}

	public static String splitField(String tmpTag, ALitLoad lt) {
		String testTag = tmpTag;
		if (lt.record.containsKey(tmpTag)) {
			StringBuffer tagVal = (StringBuffer) lt.record.get(tmpTag);
			if ((tagVal != null) && (tagVal.length() > 3500)) {
				if (tmpTag.equals("ATM"))
					testTag = "ATM1";
			}
		}
		return testTag;
	}

	public static String removeSpc(String field) {
		String str = "";
		String dspc = "  ";
		String spc = " ";
		if (field != null) {
			str = perl.substitute("s/" + dspc + "/" + spc + "/g", field.trim());
		}
		return str;
	}

	public static StringBuffer replaceSpecElements(String tagVal) {
		Enumeration en = ALitStat.htReplTags.keys();
		MatchResult mRes = null;
		String key = null;
		String replacement = null;
		String result = null;
		result = tagVal;
		if (result != null) {
			while (en.hasMoreElements()) {
				key = (String) en.nextElement();
				replacement = (String) ALitStat.htReplTags.get(key);
				result =
					perl.substitute(
						"s/" + key + "/" + replacement + "/g",
						result);
			}
		}
		return new StringBuffer(result);
	}

	public static void concatTag(
		String tmpTag,
		String tmpTagVal,
		String delim,
		ALitLoad lt) {
		if (lt.record.containsKey(tmpTag)) {
			lt.tmpTagValPart = new StringBuffer("");
			lt.tmpTagValPart.append(delim).append(tmpTagVal);
		} else {
			lt.tmpTagValPart = new StringBuffer("");
			lt.tmpTagValPart.append(tmpTagVal);
		}
	}

	public static StringBuffer replaceDelim(
		String tmpTagVal,
		String currdelim,
		String repldlelim) {
		String replTmpTagVal = tmpTagVal;
		MatchResult mRes = null;
		if (replTmpTagVal != null && !replTmpTagVal.equals("")) {
			if (perl.match("/\\" + currdelim + "/", replTmpTagVal)) {
				replTmpTagVal =
					perl.substitute(
						"s/\\" + currdelim + "/\\" + repldlelim + "/g",
						replTmpTagVal);
			}
		}
		return new StringBuffer(replTmpTagVal);
	}

	public static void addCas(ALitLoad lt) {
		String tmpTag = null;
		Enumeration en = lt.record.keys();
		MatchResult mRes = null;
		String trimDash = null;
		String tmp = null;
		String cas = null;
		Hashtable htCas = new Hashtable();
		while (en.hasMoreElements()) {
			tmpTag = (String) en.nextElement();
			if (tmpTag.equals("CT") || tmpTag.equals("CT1")) {
				cas = lt.record.get(tmpTag).toString();
				if ((cas != null) && (!cas.equals("\t"))) {
					while (perl.match("/(#{1})([\\d-]*)/", cas)) {
						mRes = perl.getMatch();
						if (mRes.group(2) != null) {
							trimDash = mRes.group(2).toString();
							if ((trimDash.length() > 0)
								&& (trimDash.indexOf("-") > 0)) {
								tmp = trimDash.substring(trimDash.length() - 1);
								if (tmp.equals("-")) {
									trimDash =
										trimDash.substring(
											0,
											trimDash.length() - 1);
								}
							}
						}
						htCas.put(trimDash, trimDash);
						cas = cas.substring(perl.endOffset(0));
						if (cas == "") {
							break;
						}
					}
				}
			}
		}
		Enumeration enumRes = htCas.keys();
		StringBuffer res = new StringBuffer();
		while (enumRes.hasMoreElements()) {
			res.append((String) enumRes.nextElement()).append("; ");
		}
		if (res != null) {
			lt.record.put("CRN", res.toString());
		}
	}

}
