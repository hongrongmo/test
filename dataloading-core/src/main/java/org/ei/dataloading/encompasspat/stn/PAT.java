/*
 * Created on Apr 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.dataloading.encompasspat.stn;
import java.util.Hashtable;
import org.apache.oro.text.perl.*;
/**
 * @author Tsolovye
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PAT {
	private static Perl5Util perl = new Perl5Util();
	private Hashtable pc = new Hashtable();
	private Hashtable pn = new Hashtable();
	private Hashtable pd = new Hashtable();
	private int patCounter = 0;
	public void setPC(StringBuffer aPc) {
		++patCounter;
		pc.put(Integer.toString(patCounter), aPc);
	}
	public void setPN(StringBuffer aPn) {
		pn.put(Integer.toString(patCounter), aPn);
	}
	public void setPD(StringBuffer aPd) {
		pd.put(Integer.toString(patCounter), aPd);
	}

	public StringBuffer getPAT() {
		StringBuffer pat = new StringBuffer();
		for (int i = 1; i <= patCounter; i++) {
			pat.append(replaceSpc(pc.get(Integer.toString(i)))).append(", ");
			pat.append(replaceSpc(pn.get(Integer.toString(i)))).append(", ");
			pat.append(replaceSpc(pd.get(Integer.toString(i)))).append("; ");
		}
		return pat;
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
