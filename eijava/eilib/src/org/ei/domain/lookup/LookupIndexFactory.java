package org.ei.domain.lookup;


/** This class <code> LookupIndexFactory </code> decides which lookup class to instatntinate
* @author $Author:   hmo  $
* @version $Revision:   1.1  $ $Date:   Jun 19 2007 14:24:12  $
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
		else if(searchfield!=null && searchfield.equals("PC"))
		{
			lis = new LookupPC(sessionID,
							   1000000);
		}
		else if(searchfield!=null && searchfield.equals("PID"))
		{
			lis = new LookupIPC(sessionID,
								1000000);
		}

    	return lis;
  }

}