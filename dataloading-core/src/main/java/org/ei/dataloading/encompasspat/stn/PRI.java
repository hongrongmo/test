/*
 * Created on Apr 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.dataloading.encompasspat.stn;
import java.util.*;
import org.apache.oro.text.perl.*;

/**
 * @author Tsolovye
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PRI {
	private static Perl5Util perl = new Perl5Util();
	private Hashtable ac = new Hashtable();
	private Hashtable ap = new Hashtable();
	private Hashtable ad = new Hashtable();
	private int priCounter = 0;
	public void setAC(StringBuffer aAc) {
		++priCounter;
		ac.put(Integer.toString(priCounter), aAc);
	}
	public void setAP(StringBuffer aAp) {
		ap.put(Integer.toString(priCounter), aAp);
	}
	public void setAD(StringBuffer aAd) {
		ad.put(Integer.toString(priCounter), aAd);
	}
	public StringBuffer getPRI() {
		StringBuffer pri = new StringBuffer();
		String priStr = null;
		String aAc = "";
		String aAp = "";
		String aAd = "";
		for (int i = 1; i <= priCounter; i++) {
			pri.append(replaceSpc(ac.get(Integer.toString(i)))).append(", ");
			pri.append(replaceSpc(ap.get(Integer.toString(i)))).append(", ");
			pri.append(replaceSpc(ad.get(Integer.toString(i)))).append("; ");
		}
		return pri;
	}

	public static String replaceSpc(Object str){
		String result = "";
		if (str == null){
			result= "";
		}else {
			result = str.toString().trim();
		}
		String singleSpc = " ";
		str = perl.substitute("s/["+singleSpc+"]+/"+singleSpc+"/g",result);
		return result;
	}

}
