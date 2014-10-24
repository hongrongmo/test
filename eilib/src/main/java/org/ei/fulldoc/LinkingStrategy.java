/*
 * Created on Nov 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.fulldoc;

import org.ei.domain.EIDoc;
import org.ei.fulldoc.LinkInfo;


public interface LinkingStrategy
{
    public static final long PATENT_LINK_EXPIRES = 600000;

	public static final String HAS_LINK_NO = "N";
	public static final String HAS_LINK_YES = "Y";
	public static final String HAS_LINK_ALWAYS = "A";

    public String hasLink (EIDoc eidoc);
    public LinkInfo buildLink(EIDoc  doc) throws Exception;

}
