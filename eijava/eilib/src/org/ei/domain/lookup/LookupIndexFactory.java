package org.ei.domain.lookup;


/** This class <code> LookupIndexFactory </code> decides which lookup class to instatntinate
* @author $Author:   johna  $
* @version $Revision:   1.0  $ $Date:   May 09 2007 15:35:58  $
*/

public class LookupIndexFactory
{

	private int indexesPerPage;
	private String sessionID;

	public LookupIndexFactory(String sessionID,
							  int indexesPerPage)
	{
		this.indexesPerPage = indexesPerPage;
		this.sessionID = sessionID;
	}


  	public LookupIndexSearcher getIndexSearcher (String searchfield)
  	{
    	LookupIndexSearcher lis = null;

		if(searchfield!=null && searchfield.trim().equals("AUS"))
		{
			lis = new LookupAUS(sessionID,
								indexesPerPage);
		}
		else if(searchfield!=null && searchfield.equals("CVS"))
		{
			lis = new LookupCVS(sessionID,
								indexesPerPage);
		}
		else if(searchfield!=null && searchfield.equals("AF"))
		{
			lis = new LookupAF(sessionID,
							   indexesPerPage);
		}
		else if(searchfield!=null && searchfield.equals("PN"))
		{
			lis = new LookupPN(sessionID,
							   indexesPerPage);
		}
		else if(searchfield!=null && searchfield.equals("ST"))
		{
			lis = new LookupST(sessionID,
							   indexesPerPage);
		}
		else if(searchfield!=null && searchfield.equals("DT"))
		{
			lis = new LookupDT(sessionID,
							   1000000);
		}
		else if(searchfield!=null && searchfield.equals("TR"))
		{
			lis = new LookupTR(sessionID,
							   1000000);
		}
		else if(searchfield!=null && searchfield.equals("DI"))
		{
			lis = new LookupDI(sessionID,
							   1000000);
		}
		else if(searchfield!=null && searchfield.equals("LA"))
		{
			lis = new LookupLA(sessionID,
							   1000000);
		}

    	return lis;
  }

}