/*
 * Created on Jun 20, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.data.inspec.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ei.common.bd.BdAuthor;
import org.ei.common.bd.BdAuthors;
import org.ei.common.Constants;

/**
 * @author solovyevat
 *
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class InspecAuthors {
    private BdAuthors au;
    private static ArrayList<String> auElements = new ArrayList<String>();
    private static ArrayList<String> auElementsExpanded = new ArrayList<String>();

    static {
        auElements.add("indexedName");
        auElements.add("affidStr");
    }

    static {
        auElementsExpanded.add("indexedName");
        auElementsExpanded.add("affidStr");
        auElementsExpanded.add("foreNames");
        auElementsExpanded.add("initials");
        auElementsExpanded.add("suffix");
        auElementsExpanded.add("eAddress");
        auElementsExpanded.add("id");
    }

    public InspecAuthors(String dAus) {
        au = new BdAuthors(formatAuStr(dAus));
    }

    public InspecAuthors(String dAus, String format) {
        if (format.equals("ref")) {
            au = new BdAuthors(formatRefAuStr(dAus));
        } else {
            au = new BdAuthors(formatAuStr(dAus));
        }
    }

    public String getAffIdList(String affidStr) {
        StringBuffer result = null;
        if (affidStr != null && !affidStr.trim().equals("")) {
            if (affidStr.indexOf(" ") > 0) {
                List<String> l = Arrays.asList(affidStr.split(" ", -1));
                result = new StringBuffer();
                for (int i = 0; i < l.size(); i++) {
                    String str = (String) l.get(i);
                    if (str != null && !str.trim().equals("")) {
                        str = str.substring(str.lastIndexOf(".") + 1);
                        result.append(str);
                        if (i < (l.size() - 1)) {
                            result.append(", ");
                        }
                    }
                }
            } else {
                result = new StringBuffer();
                String str = affidStr;
                str = str.substring(str.lastIndexOf(".") + 1);
                result.append(str);
            }
        }
        if (result != null && result.length() > 0) {
            return result.toString();
        }
        return null;
    }

    public String formatRefAuStr(String dAus) {
        StringBuffer formatedData = new StringBuffer();

        String[] daus = dAus.split(Constants.GROUPDELIMITER);
        for (int i = 0; i < daus.length; i++) {
            if (daus[i] != null && daus[i].length() > 0) {
                String[] dau = daus[i].split("\\|", -1);
                // 0
                formatedData.append(Constants.IDDELIMITER);
                // 1
                formatedData.append(dau[0]);
                // System.out.println("id= "+dau[0]);
                formatedData.append(Constants.IDDELIMITER);
                // 2
                formatedData.append(Constants.IDDELIMITER);
                // 3
                formatedData.append(Constants.IDDELIMITER);
                // 4
                formatedData.append(Constants.IDDELIMITER);
                formatedData.append(dau[4]);
                // 5

                formatedData.append(Constants.IDDELIMITER);
                formatedData.append(dau[2]);
                // System.out.println("initials= "+dau[2]);
                // 6
                formatedData.append(Constants.IDDELIMITER);
                formatedData.append(dau[1]);
                // System.out.println("degree= "+dau[1]);
                // 7
                formatedData.append(Constants.IDDELIMITER);
                // 8
                formatedData.append(Constants.IDDELIMITER);
                formatedData.append(dau[4]);
                // System.out.println("suffix= "+dau[4]);
                // 9
                formatedData.append(Constants.IDDELIMITER);
                formatedData.append(dau[3]);
                // System.out.println("nametext= "+dau[3]);
                // 10
                formatedData.append(Constants.IDDELIMITER);
                // 11
                formatedData.append(Constants.IDDELIMITER);
                // 12
                formatedData.append(Constants.IDDELIMITER);
                formatedData.append(dau[1]);
                // 13
                formatedData.append(Constants.IDDELIMITER);
                formatedData.append(dau[5]);
                // System.out.println("email= "+dau[5]);
            }
            formatedData.append(Constants.AUDELIMITER);
        }

        return formatedData.toString();

    }

    public String formatAuStr(String dAus) {

        boolean isExpanded = false;
        StringBuffer formatedData = new StringBuffer();

        String[] daus = dAus.split(Constants.AUDELIMITER);
        for (int i = 0; i < daus.length; i++) {
            if (daus[i] != null && daus[i].length() > 0) {
                String[] dau = daus[i].split(Constants.IDDELIMITER, -1);

                if (dau.length > 1) {
                    isExpanded = true;
                }

                if (daus != null && daus.length > 0) {

                    if (!isExpanded) {   // 0
                        formatedData.append(Constants.IDDELIMITER);
                        // 1
                        formatedData.append(Constants.IDDELIMITER);
                        // 2
                        if (dau.length > 1 && dau[1] != null && dau[1].trim().length() > 0) {
                            String str = getAffIdList(dau[1]);
                            formatedData.append(str);
                        }
                        formatedData.append(Constants.IDDELIMITER);
                        if (dau.length > 0 && dau[0] != null) {
                            formatedData.append(dau[0]);
                        }
                        formatedData.append(Constants.IDDELIMITER);
                        formatedData.append(Constants.IDDELIMITER);
                        formatedData.append(Constants.IDDELIMITER);
                        formatedData.append(Constants.IDDELIMITER);
                        formatedData.append(Constants.IDDELIMITER);
                        formatedData.append(Constants.IDDELIMITER);
                        formatedData.append(Constants.IDDELIMITER);
                    } else {
                        // 0
                        formatedData.append(Constants.IDDELIMITER);
                        // 1
                        formatedData.append(Constants.IDDELIMITER);
                        // 2
                        if (dau.length > 1 && dau[1] != null && dau[1].trim().length() > 0) {
                            String str = getAffIdList(dau[1]);
                            formatedData.append(str);
                        }
                        formatedData.append(Constants.IDDELIMITER);
                        // 3
                        if (dau.length > 0 && dau[0] != null) {
                            formatedData.append(dau[0]);
                        }

                        formatedData.append(Constants.IDDELIMITER);
                        // 4
                        if (dau.length > 3 && dau[3] != null) {
                            formatedData.append(dau[3]); // 3
                        }
                        formatedData.append(Constants.IDDELIMITER);
                        // 5

                        formatedData.append(Constants.IDDELIMITER);
                        // 6
                        formatedData.append(Constants.IDDELIMITER);
                        // 7
                        formatedData.append(Constants.IDDELIMITER);
                        // 8
                        if (dau.length > 4 && dau[4] != null) {
                            formatedData.append(dau[4]);
                        }
                        formatedData.append(Constants.IDDELIMITER);
                        // 9
                        formatedData.append(Constants.IDDELIMITER);
                        // 10
                        formatedData.append(Constants.IDDELIMITER);
                        // 11
                        formatedData.append(Constants.IDDELIMITER);
                        // 12
                        formatedData.append(Constants.IDDELIMITER);
                        // 13
                        formatedData.append(Constants.IDDELIMITER);
                        if (dau.length > 5 && dau[5] != null) {
                            formatedData.append(dau[5]);
                        }
                    }
                }
            }
            formatedData.append(Constants.AUDELIMITER);
        }

        return formatedData.toString();

    }

    public String[] getSearchValue() {
        return au.getSearchValue();
    }

    public List<BdAuthor> getInspecAuthors() {
        return au.getAuthors();
    }

}
