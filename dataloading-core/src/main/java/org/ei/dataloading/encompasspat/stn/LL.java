/*
 * Created on Apr 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.dataloading.encompasspat.stn;

import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * @author Tsolovye
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LL {
	private String lval = null;
	public void setL(String aL) {
		lval = aL;
	}
	public String getL() {
		StringBuffer lresult = new StringBuffer();
		if (lval != null) {
			StringTokenizer tok = new StringTokenizer(lval, ";", false);
			while (tok.hasMoreElements()) {
				String tokStr = tok.nextToken();
				lresult.append(getstrL(tokStr));
				if ((tokStr != null) && (!tokStr.trim().equals(""))) {
					lresult.append("; ");
				}
			}
		}

		return lresult.toString();
	}
	private String getstrL(String strL) {

		StringBuffer lresult = new StringBuffer();
		int counter = 0;
		Hashtable ht = new Hashtable();
		StringTokenizer tok = new StringTokenizer(strL, ",", false);
		while (tok.hasMoreElements()) {
			ht.put(Integer.toString(++counter), tok.nextToken());
		}
		String sec = (String) ht.get("2");
		if (sec != null) {
			lresult.append((String) ht.get("2")).append(" ");
		}

		String tr = (String) ht.get("3");
		if (tr != null) {
			lresult.append(tr);
		}
		String one = (String) ht.get("1");
		if (one != null) {
			lresult.append(" ").append(one);
		}

		return lresult.toString();
	}

}
