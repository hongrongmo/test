<<<<<<< HEAD
package org.ei.domain;

import java.util.List;


/** This Interface is implemented by all the basic DocBuilders
  *  like CPXChemDocBuilder,CNBNDocBuilder...
  *  It provides a method buildPage which takes list of DocIds
  *  and returns a List of EIDoc's
  *
  * @author Ravi Kumar Gullapalli
  */

public interface DocumentBuilder
{

	public List buildPage(List listOfDocID,
			      String dataFormat)
		throws DocumentBuilderException;

	public DocumentBuilder newInstance(Database database);

}
=======
package org.ei.domain;

import java.util.List;


/** This Interface is implemented by all the basic DocBuilders
  *  like CPXChemDocBuilder,CNBNDocBuilder...
  *  It provides a method buildPage which takes list of DocIds
  *  and returns a List of EIDoc's
  *
  * @author Ravi Kumar Gullapalli
  */

public interface DocumentBuilder
{

	public List buildPage(List listOfDocID, 
			      String dataFormat) 
		throws DocumentBuilderException;
	
	public DocumentBuilder newInstance(Database database);

}
>>>>>>> 15dca715b02cb03c5fa8fd5c16635f7e802f1eae
