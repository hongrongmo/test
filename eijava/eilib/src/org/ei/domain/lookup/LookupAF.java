package org.ei.domain.lookup;

import org.ei.domain.Database;


public class LookupAF extends CombinedBase
{
	public LookupAF(String sessionID,
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
		sql.append("SELECT institute_name lookup_name, dbase, 'QQ' lookup_value ");
		sql.append("FROM CMB_AF_LOOKUP WHERE institute_name LIKE '" + searchword+"%' ");
		sql.append(" AND dbase in (");
		sql.append(getSqlIN(databases));
		sql.append(") ORDER BY lookup_name" );
      	return sql.toString();
	}
}