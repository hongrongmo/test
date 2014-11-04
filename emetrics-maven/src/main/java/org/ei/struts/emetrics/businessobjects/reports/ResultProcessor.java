/*
 * $Author:   johna  $
 * $Revision:   1.1  $
 * $Date:   Oct 06 2005 15:47:10  $
 *
*/

package org.ei.struts.emetrics.businessobjects.reports;

import java.util.List;

/**
 * Representation of a result set processor.  Takes a result set and munges it into whatever
 * type of object or set of objects it should be.
 *
 * @author Ryan Daigle
 * @version 1.0
 * @since 12/2/2001
 */
public interface ResultProcessor {

	/**
	 * Takes a result set and munges it into whatever type of object or set of
	 * objects it should be.
	 *
	 * @param rs     The result set to traverse in forming these objects
	 *
	 * @return the object or set of objects that this result set represents
	 *
	 * @exception SQLException
	 *                   this exception that can result from mishandling of a result set.  However, the developer
	 *                   shouldn't have to worry about the overhead of handling these exceptions, so let
	 *                   it bubble up.
	 */
	public List process(ResultsSet resultset, List data);
}