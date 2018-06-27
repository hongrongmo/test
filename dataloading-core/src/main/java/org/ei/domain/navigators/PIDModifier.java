/*
 * Created on Aug 11, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import org.ei.common.upt.IPCClassNormalizer;
import org.ei.domain.ClassNodeManager;
import org.ei.domain.ClassTitleDisplay;
/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PIDModifier extends PatentCodeModifier
{

    public String seekCode(ClassNodeManager cm) throws Exception
    {
        return ClassTitleDisplay.getDisplayTitle(cm.seekIPC(IPCClassNormalizer.normalize(this.getValue())));
    }

    public PIDModifier(int i, String slable, String svalue)
    {
        super(i, slable.toUpperCase(), svalue.toUpperCase());
    }

}
