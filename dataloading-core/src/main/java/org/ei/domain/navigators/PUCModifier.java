/*
 * Created on Aug 11, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import org.ei.common.upt.USPTOClassNormalizer;
import org.ei.domain.ClassNodeManager;
import org.ei.domain.ClassTitleDisplay;
import org.ei.common.upt.*;
/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PUCModifier extends PatentCodeModifier
{
    public String seekCode(ClassNodeManager cm) throws Exception
    {
        return ClassTitleDisplay.getDisplayTitle(cm.seekUS(USPTOClassNormalizer.normalize(this.getValue())));
    }

    public PUCModifier(int i, String slable, String svalue)
    {
        super(i, slable, svalue);
    }

}
