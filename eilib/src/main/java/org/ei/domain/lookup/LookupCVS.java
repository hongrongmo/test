package org.ei.domain.lookup;

import org.ei.domain.Database;

public class LookupCVS extends CombinedBase
{

	public LookupCVS(String sessionID,
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
		sql.append("SELECT index_term lookup_name, dbase, 'QQ' lookup_value ");
		sql.append("FROM CMB_CV_LOOKUP WHERE index_term LIKE '" + searchword+"%' ");
		sql.append(" AND dbase in (");
		sql.append(getSqlIN(databases));
		sql.append(") ORDER BY lookup_name" );
		return sql.toString();
	}
}