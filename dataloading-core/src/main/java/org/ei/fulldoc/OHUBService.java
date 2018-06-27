package org.ei.fulldoc;

import org.ei.util.StringUtil;

public class OHUBService {

	public static String ohuburl = "http://linkinghub.elsevier.com/servlets/OHXmlRequestXml";
	public static String salt = "E78C4EAF-4064-41C5-9FC2-E1C73DE9FBF5";
	public static String saltVersion = "1";
	public static String partnerID = "14";
	private StringUtil sUtil = new StringUtil();

	private String withDash(String ISSN) {
		String newISSN = null;

		if (ISSN.indexOf("-") < 0) {
			String begin = ISSN.substring(0, 4);
			String end = ISSN.substring(4, ISSN.length());
			newISSN = begin + "-" + end;
		} else {
			newISSN = ISSN;
		}

		return newISSN;

	}

	private String withoutDash(String ISSN) {
		return sUtil.replace(ISSN, "-", "", StringUtil.REPLACE_FIRST,
				StringUtil.MATCH_CASE_INSENSITIVE);
	}

}
