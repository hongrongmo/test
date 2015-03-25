/*
 * Created on Aug 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import java.util.Iterator;

import org.ei.util.StringUtil;

/**
 * @author JMoschet
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YearNavigator extends EiNavigator {
    // protected static Log log = LogFactory.getLog(YearNavigator.class);

    private String DISPLAYNAME = "Year";

    public YearNavigator() {
        super(EiNavigator.YR);
        setDisplayname(DISPLAYNAME);
    }

    // ([;1997] OR [1999;1999] OR [2000;]) WN YR
    public String getQueryString() {
        // log.info(" OVERRIDDEN getQueryString");
        StringBuffer sb = new StringBuffer();
        sb.append("(");

        Iterator itrmods = (this.getModifiers()).iterator();
        while (itrmods.hasNext()) {
            EiModifier modifier = (EiModifier) itrmods.next();
            String svalue = (String) modifier.getValue();
            svalue = svalue.replaceAll(";", "-");
            svalue = svalue.replaceAll("\\[", StringUtil.EMPTY_STRING);
            svalue = svalue.replaceAll("\\]", StringUtil.EMPTY_STRING);
            sb.append(svalue);
            if (itrmods.hasNext()) {
                sb.append(" OR ");
            }
        }
        sb.append(") WN ");
        sb.append((super.getFieldname()).toUpperCase());

        return sb.toString();

    }

}
