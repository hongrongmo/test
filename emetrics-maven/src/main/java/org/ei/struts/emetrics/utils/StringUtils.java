/*
 * $Author:   johna  $
 * $Revision:   1.4  $
 * $Date:   Oct 03 2005 14:37:14  $
 *
*/

package org.ei.struts.emetrics.utils;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.oro.text.perl.Perl5Util;
//import org.ei.struts.emetrics.businessobjects.reports.Chart;
//import org.ei.struts.emetrics.businessobjects.reports.ChartRow;
import org.ei.struts.emetrics.businessobjects.reports.DataRow;
import org.ei.struts.emetrics.businessobjects.reports.ResultsSet;


public final class StringUtils  {

	/** never to be instantiated */
	private StringUtils() {
	}

	/** normalise whitespace in a String (all whitespace is transformed
	 *to single spaces and the string is NOT trimmed
	 */
	public static String normaliseWhitespace(String str) {
		Perl5Util perl = new Perl5Util();
		
		// replace  [ \t\n\r\f] with [ ] 
		return perl.substitute("s#\\s# #g", str);
		
	}

	/**
	* Sorts a LIST that contains DataRow, ChartRow, ResultsSet or Chart into ascending order by NUMERICALLY
	* ids.
	*
	*@param v A List of DataRow, ChartRow,, ResultsSet or Chart elements
	*@return The List in NUMERICALLY sorted order
	*/
	public static List SortIt(List alist) {
		// Clone the input Vector and sort its
		// String elements into alphabetical order
		//Vector numeric = (Vector)v.clone();

		// An anonymous String comparator that performs
		// a comparison of two Strings. It relies on the
		// fact that the Strings have already been lexicographically
		// sorted --which is done above.
		Comparator c = new Comparator() {
			public int compare(Object o1, Object o2) {

				int i1 = 0;
				int i2 = 0;

				// Convert both objects to String
				if (o1 instanceof DataRow) {
					 i1 = ((DataRow)o1).getRowId();
					 i2 = ((DataRow)o2).getRowId();
				} else if (o1 instanceof ResultsSet) {
					 i1 = ((ResultsSet)o1).getResultsSetId();
					 i2 = ((ResultsSet)o2).getResultsSetId();
				}

				if( i1 == i2 ) return 0;
				else if( i1 > i2 ) return 1;
				else return -1;

			  }

		};

		// Now sort the numerical list
		Collections.sort(alist,c);

		return alist;
	}
}
