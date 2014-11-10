/*
 * Created on Apr 11, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.data;

import org.apache.oro.text.perl.Perl5Util;

/**
 * @author KFokuo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EltAafFormatter {
	
	public static String formatAffiliation(String aaf) {

		  Perl5Util perl = new Perl5Util();

		  if (aaf == null)
			  return "";

		  String result = perl.substitute("s/\\|[0-9]+\\:\\://g", aaf);

		  return result;
	  }
 

}
