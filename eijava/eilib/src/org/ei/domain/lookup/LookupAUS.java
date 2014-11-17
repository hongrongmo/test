package org.ei.domain.lookup;

import org.ei.domain.Database;



/** This class <code> InspecAF </code> produces xml for Combined AU lookup for Inspec, NTIS, CPX
* @author $Author:   johna  $
* @version $Revision:   1.0  $ $Date:   May 09 2007 15:35:58  $
*/


public class LookupAUS
	extends CombinedBase
{

	public LookupAUS(String sessionID,
					 int indexesPerPage)
	{
		super(sessionID,
			  indexesPerPage,
			  true);
	}

	protected String getSQLHook(String searchword,
								Database[] databases)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT display_name lookup_name, dbase, 'QQ' lookup_value ");
	    sql.append("FROM CMB_AU_LOOKUP WHERE display_name LIKE '" + searchword+"%' ");
	    sql.append(" AND dbase in (");
		sql.append(getSqlIN(databases));
		sql.append(") ORDER BY lookup_name" );

		return sql.toString();
	}
}