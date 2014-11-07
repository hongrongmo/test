/*
 * Created on Apr 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.emetrics.labels;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.display.ColumnDecorator;
import org.ei.struts.emetrics.Constants;

/**
 * @author JMoschet
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DatabaseMaskDecorator extends ColumnDecorator {

    protected static Log log = LogFactory.getLog(DatabaseMaskDecorator.class);

    public String decorate(Object columnValue) {

        String decorated = Constants.EMPTY_STRING;

        if (columnValue == null) {
            return decorated;
        }

        try {
            decorated = getDisplayName(Integer.parseInt(columnValue.toString()));
        } catch (NumberFormatException e) {
            decorated = columnValue.toString();
        }

        return decorated;

    }

    private static Map<Integer, String> databaseNames = new HashMap<Integer, String>();
    {
        databaseNames.put(new Integer(1), "Compendex");
        databaseNames.put(new Integer(2), "Inspec");
        databaseNames.put(new Integer(4), "NTIS");
        databaseNames.put(new Integer(8), "USPTO");
        databaseNames.put(new Integer(16), "CRC ENGnetBASE");
        // databaseNames.put(new Integer(32),"C84");
        databaseNames.put(new Integer(64), "PaperChem");
        databaseNames.put(new Integer(128), "Chimica");
        databaseNames.put(new Integer(256), "Chemical Business NewsBase");
        databaseNames.put(new Integer(512), ""); // BEILSTEIN
        databaseNames.put(new Integer(1024), "EnCompassLIT");
        databaseNames.put(new Integer(2048), "EnCompassPAT");
        // databaseNames.put(new Integer(4096),"IBF");
        databaseNames.put(new Integer(8192), "GeoBase");
        databaseNames.put(new Integer(16384), "EU Patents");
        databaseNames.put(new Integer(32768), "US Patents");
        // databaseNames.put(new Integer(65536),"");
        databaseNames.put(new Integer(131072), "Referex");
        databaseNames.put(new Integer(262144), "Engineering Index Backfile Standalone");
        // public static final int UPT_MASK = 524288;
        databaseNames.put(new Integer(1048576), "Inspec Archive Standalone");
        databaseNames.put(new Integer(2097152), "GeoRef");
    }

    private String getDisplayName(int mask) {

        StringBuffer buf = new StringBuffer();
        try {
            Iterator<Integer> itrDbs = databaseNames.keySet().iterator();
            while (itrDbs.hasNext()) {
                Integer dbkey = ((Integer) itrDbs.next());
                int dbmask = dbkey.intValue();
                if ((dbmask & mask) == dbmask) {
                    if (buf.length() != 0) {
                        buf.append(", ");
                    }
                    buf.append((String) databaseNames.get(dbkey));
                }
            }
        } catch (Exception e) {
        }

        return buf.toString();
    }
}
