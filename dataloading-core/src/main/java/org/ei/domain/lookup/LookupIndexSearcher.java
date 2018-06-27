package org.ei.domain.lookup;

import java.io.Writer;

import org.ei.domain.Database;
import org.ei.domain.LookupIndexException;

/** This Interface <code> LookupIndexSearcher </code> is implemented by Lookup classes
* like CpxAU, CpxCV etc.
* it provides method lookupIndexXML which is writing xml stream
* @author $Author:   johna  $
* @version $Revision:   1.0  $ $Date:   May 09 2007 15:35:58  $
*/

public interface LookupIndexSearcher{

  /** This method <code> LookupIndexXML </code> is writing xml stream
  * @param String searchword
  * @param int count
  * @param Writer out
  * @return void
  */
  public void lookupIndexXML (String lookupSearchID,
  							  String searchword,
  							  Database[] databases,
  							  int pageCount,
  							  Writer out)
  	throws LookupIndexException;
}