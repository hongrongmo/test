/*
 * Created on Oct 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;

/**
 * @author KFokuo
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EltAusFormatter {

    public static String formatAffiliation(String aaf) {

        Perl5Util perl = new Perl5Util();

        if (aaf == null)
            return "";

        String result = perl.substitute("s/::/;/g", aaf);

        char[] arrAaf = result.toCharArray();
        StringBuffer buffAaf = new StringBuffer();
        StringBuffer newAaf = new StringBuffer();

        for (int i = 0; i < arrAaf.length; i++) {

            Character c = new Character(arrAaf[i]);

            if (c.charValue() != '|' && !Character.isDigit(arrAaf[i]))
                buffAaf.append(arrAaf[i]);
        }

        StringTokenizer aus = new StringTokenizer(buffAaf.toString(), ";", false);

        while (aus.hasMoreTokens()) {
            String sVal = aus.nextToken().trim().toUpperCase();
            newAaf.append(sVal);

            if (aus.hasMoreElements())
                newAaf.append(";");
        }

        return newAaf.toString();
    }

    class FieldSorter {
        int order = 0;
        String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setOrdInd(int order) {
            this.order = order;
        }

        public int getOrdInd() {
            return order;
        }
    }

    class AuthorComparator implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            FieldSorter a1 = (FieldSorter) o1;
            FieldSorter a2 = (FieldSorter) o2;

            if (a1.getOrdInd() > a2.getOrdInd())
                return 1;
            else if (a1.getOrdInd() < a2.getOrdInd())
                return -1;
            else
                return 0;

        }
    }

    public String formatAuthors(String sAuthors) {

        Perl5Util perl = new Perl5Util();

        if (sAuthors == null)
            return "";

        List<FieldSorter> lstObjAuthors = new ArrayList<FieldSorter>();

        StringTokenizer tokens = new StringTokenizer(sAuthors, "|", false);

        while (tokens.hasMoreTokens()) {
            List<String> lstIndexes = new ArrayList<String>();
            String author = tokens.nextToken();

            if (author != null && !author.equals("")) {

                perl.split(lstIndexes, "/::/", author);
                String index = notNull((String) lstIndexes.get(0));
                index = perl.substitute("s/\\|//g", index);
                String authors = notNull((String) lstIndexes.get(1));
                FieldSorter objAuthors = new FieldSorter();
                objAuthors.setOrdInd(Integer.parseInt(index));
                objAuthors.setName(authors);
                lstObjAuthors.add(objAuthors);

            }
            lstIndexes.clear();
        }

        Collections.sort(lstObjAuthors, new AuthorComparator());

        StringBuffer sbAuthors = new StringBuffer();

        for (Iterator<FieldSorter> iter = lstObjAuthors.iterator(); iter.hasNext();) {
            FieldSorter author = (FieldSorter) iter.next();
            sbAuthors.append(author.getName());

            if (iter.hasNext())
                sbAuthors.append(";");

        }

        StringBuffer authors = new StringBuffer();
        StringBuffer newAuthors = new StringBuffer();

        char[] arrAaf = sbAuthors.toString().toCharArray();

        for (int i = 0; i < arrAaf.length; i++) {

            Character c = new Character(arrAaf[i]);

            if (c.charValue() != '|' && c.charValue() != ':' && !Character.isDigit(arrAaf[i]))
                authors.append(arrAaf[i]);
        }

        StringTokenizer aus = new StringTokenizer(authors.toString(), ";", false);

        while (aus.hasMoreTokens()) {

            String aut = aus.nextToken().trim();

            StringTokenizer authorNames = new StringTokenizer(aut, ",", false);

            while (authorNames.hasMoreTokens()) {

                String authorName = authorNames.nextToken().trim();

                if (!authorName.trim().equals(""))
                    newAuthors.append(authorName);
                else {

                    if (newAuthors.toString().endsWith(",")) {
                        String sVal = newAuthors.substring(0, newAuthors.length() - 1);

                        newAuthors.setLength(0);
                        newAuthors.append(sVal);
                    }

                }
                if (authorNames.hasMoreTokens() && !authorName.equals("")) {
                    newAuthors.append(", ");
                }

            }
            if (aus.hasMoreTokens())
                newAuthors.append(";");

        }

        return newAuthors.toString();
    }

    /**
     * @return
     */
    private static String notNull(String sVal) {

        if (sVal == null)
            sVal = "";

        return sVal;
    }
}
