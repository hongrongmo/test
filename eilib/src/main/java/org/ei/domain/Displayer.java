package org.ei.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ei.books.collections.ReferexCollection;
import org.ei.util.StringUtil;

public class Displayer {

    // this class removes non-hosted databases (USPTO and CRC)
    // and backfiles from mask value
    public static int getScrubbedMask(int mask) {
        int exmasks[] = { DatabaseConfig.USPTO_MASK, DatabaseConfig.CRC_MASK, DatabaseConfig.C84_MASK, DatabaseConfig.IBF_MASK };

        for (int idx = 0; idx < exmasks.length; idx++) {
            if ((mask & exmasks[idx]) == exmasks[idx]) {
                mask -= exmasks[idx];
            }
        }

        return mask;
    }

    // list of all whole masks which can be displayed on the page
    private static int[] dbMasks = new int[] { DatabaseConfig.CPX_MASK, DatabaseConfig.INS_MASK, DatabaseConfig.IBS_MASK, DatabaseConfig.NTI_MASK,
        DatabaseConfig.GEO_MASK, DatabaseConfig.EUP_MASK, DatabaseConfig.UPA_MASK, DatabaseConfig.PAG_MASK, DatabaseConfig.CBF_MASK, DatabaseConfig.CHM_MASK,
        DatabaseConfig.PCH_MASK, DatabaseConfig.ELT_MASK, DatabaseConfig.EPT_MASK, DatabaseConfig.CBN_MASK, DatabaseConfig.GRF_MASK, };
    static {
        Arrays.sort(dbMasks);
    }

    // is this mask a whole power of 2 which is in the sorted Array dbMasks
    private static boolean isWholeDBMask(int amask) {
        return (Arrays.binarySearch(dbMasks, amask) >= 0);
    }

    public static String referexCheckBoxes(String creds, String selectedColls) {
        StringBuffer innercheckboxes = new StringBuffer("");
        StringBuffer alldbcheckbox = new StringBuffer("");
        StringBuffer formhtml = new StringBuffer("");
        boolean hasCheckboxes = true;
        if (creds != null) {

            String credentials[] = creds.split(";");
            Arrays.sort(credentials);
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            Database pagDatabase = dbConfig.getDatabase("pag");
            if (pagDatabase != null) {

                // get a list of the aviable referex collections
                // based on users credentials
                List<ReferexCollection> sfs = new ArrayList<ReferexCollection>();
                Map<String, ?> colls = pagDatabase.getDataDictionary().getClassCodes();
                Iterator<String> itrColls = colls.keySet().iterator();
                while (itrColls.hasNext()) {
                    String acoll = (String) itrColls.next();
                    // loop through all the cartridges and see if any start with
                    // a collection name and if it does add the collection to the
                    // list
                    for (int cartIndex = 0; cartIndex < credentials.length; cartIndex++) {
                        if (credentials[cartIndex].toUpperCase().indexOf(acoll.toUpperCase()) == 0) {
                            sfs.add(ReferexCollection.getCollection(acoll));
                            break;
                        }
                    }
                }

                if (!sfs.isEmpty()) {
                    String inputType = "checkbox";
                    String labelClass = "SmBlackText";

                    // if only one collection is available, then do no use
                    // checkboxes, only show collection name in bold text
                    if (sfs.size() > 1) {
                        alldbcheckbox.append("<input name=\"allcol\"");
                        if ((selectedColls == null) || selectedColls.equals("")) {
                            alldbcheckbox.append(" checked=\"checked\" ");
                        }
                        alldbcheckbox.append(" style=\"vertical-align:middle;\" type=\"checkbox\" id=\"chkAll\" value=\"ALL\"  onClick=\"change(\'all\');\"/>");
                        alldbcheckbox.append("<label class=\"SmBlackText\" for=\"chkAll\">");
                        alldbcheckbox.append("All Referex Collections");
                        alldbcheckbox.append("</label>");
                        if (hasCheckboxes) {
                            alldbcheckbox.append("&#160;").append(HelpIcon.getHelpLink());
                        }
                        alldbcheckbox.append("<br/>");
                    } else {
                        hasCheckboxes = false;
                        inputType = "hidden";
                        labelClass = "MedBlackText";
                    }
                    Collections.sort(sfs);
                    Iterator<ReferexCollection> sfsItr = sfs.iterator();

                    // we will use a 1-row table just broken into cells which will contain
                    // the collections broken into threes with the labels aligned to the top of the cell
                    // using 1 row we don't have to pad the last cell
                    final int BREAKEVERY = 3;
                    int numcols = (sfs.size() % BREAKEVERY) + 1;
                    innercheckboxes.append("<table width=\"100%\" border=\"0\">");
                    for (int colindex = 0; sfsItr.hasNext(); colindex++) {
                        ReferexCollection value = (ReferexCollection) sfsItr.next();
                        if ((colindex % BREAKEVERY) == 0) {
                            innercheckboxes.append("<tr>");
                        }

                        // pad out last cell if it does not align to a multiple of the number we break on
                        if (!sfsItr.hasNext() && (BREAKEVERY - (colindex % BREAKEVERY) != 1)) {
                            innercheckboxes.append("<td valign=\"top\" colspan=\"" + (BREAKEVERY - (colindex % BREAKEVERY)) + "\">");
                        } else {
                            innercheckboxes.append("<td valign=\"top\">");
                        }
                        innercheckboxes.append("<input name=\"col\"");
                        if ((selectedColls != null) && (selectedColls.indexOf(value.getAbbrev()) >= 0)) {
                            innercheckboxes.append(" checked=\"checked\" style=\"vertical-align:middle;\" ");
                        }
                        innercheckboxes.append("type=\"").append(inputType).append("\" id=\"chk").append(value.getAbbrev()).append("\" value=\"")
                            .append(value.getAbbrev()).append("\" onClick=\"change(\'database\')\"/>");
                        innercheckboxes.append("<label class=\"").append(labelClass).append("\" for=\"chk").append(value.getAbbrev()).append("\">");
                        if (!hasCheckboxes) {
                            innercheckboxes.append("<B>");
                        }
                        innercheckboxes.append(value.getDisplayName());
                        if (!hasCheckboxes) {
                            innercheckboxes.append("</B>");
                        }
                        innercheckboxes.append("</label>");
                        innercheckboxes.append("</td>");

                        if ((colindex % BREAKEVERY) == (BREAKEVERY - 1)) {
                            innercheckboxes.append("</tr>");
                        }
                    }
                    innercheckboxes.append("</tr></table>");
                }
            }
        }
        innercheckboxes.append("</td>");

        // Start outer table which will contain label, spacing cells and
        // single cell
        // which will contain checkboxes
        formhtml.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

        // top row has single spacer cell followed by double span cell with
        // bold "CHOOSE COLLECTION " label
        if (hasCheckboxes) {
            formhtml.append("<tr>");
            formhtml.append("<td valign=\"top\" width=\"4\" bgcolor=\"#C3C8D1\">");
            formhtml.append("<img src=\"/static/images/s.gif\" width=\"4\"/></td>");
            formhtml.append("<td valign=\"top\" colspan=\"4\" bgcolor=\"#C3C8D1\">");
            formhtml.append("<a CLASS=\"SmBlueTableText\"><b>CHOOSE COLLECTION</b></a>");
            formhtml.append("</td>");
            formhtml.append("</tr>");
        }

        formhtml.append("<tr>");
        formhtml.append("<td valign=\"top\" width=\"4\" bgcolor=\"#C3C8D1\">");
        formhtml.append("<img src=\"/static/images/s.gif\" width=\"4\"/></td>");
        formhtml.append("<td valign=\"top\" width=\"15\" bgcolor=\"#C3C8D1\">");
        formhtml.append("<img src=\"/static/images/s.gif\" width=\"15\"/></td>");
        formhtml.append("<td valign=\"top\" bgcolor=\"#C3C8D1\">");

        // append the 'All' checkbox
        formhtml.append(alldbcheckbox);
        // append the inner checkboxes
        formhtml.append(innercheckboxes);
        // finish the surrounding table
        formhtml.append("</td><td valign=\"top\" >");
        formhtml.append("&#160;</td></tr>");
        formhtml.append("</table>");

        return formhtml.toString();
    }

    public static String toHTML(int userMask, int selectedMask, String searchType) {

        int sum = 0;

        StringBuffer formhtml = new StringBuffer();
        StringBuffer innercheckboxes = new StringBuffer();
        StringBuffer alldbcheckbox = new StringBuffer();

        // if selectedMask is USPTO or CRC
        // Then 'Easy Search' tab was selected from Remote DB
        // set the database value to all dbs in the user mask
        if ((selectedMask == DatabaseConfig.USPTO_MASK) || (selectedMask == DatabaseConfig.CRC_MASK)) {
            selectedMask = userMask;
        }

        int umask = 0;
        umask = getScrubbedMask(userMask);

        if (searchType.equalsIgnoreCase(Query.TYPE_EASY)) {
            // if this is one database
            if (isWholeDBMask(umask)) {
                // NO CHECKBOXES - Hide Single Database value - no label or
                // checkboxes
                formhtml.append("<input type=\"hidden\" name=\"database\" value=\"").append(umask).append("\"/>");
            } else {
                // No outer table for Easy search checkboxes
                // start checkbox for All dbs - we will complete outside the
                // loop
                alldbcheckbox.append("<input type=\"checkbox\" name=\"alldb\"");

                Displayer disp = new Displayer();

                List<DatabaseCheckbox> lstinnerCheckboxes = new LinkedList<DatabaseCheckbox>();

                for (int x = 0; x < dbMasks.length; x++) {
                    if ((umask & dbMasks[x]) == dbMasks[x]) {
                        Database[] d = (DatabaseConfig.getInstance()).getDatabases(dbMasks[x]);

                        DatabaseCheckbox dbcheck = disp.new DatabaseCheckbox(dbMasks[x]);

                        for (int y = 0; y < d.length; y++) {
                            if (d[y] != null) {
                                sum += d[y].getMask();
                                if (!StringUtil.EMPTY_STRING.equals(innercheckboxes)) {
                                    dbcheck.setId(d[y].getID());
                                    if ((selectedMask & dbMasks[x]) == dbMasks[x]) {
                                        dbcheck.setChecked(true);
                                    }
                                }

                                dbcheck.setSortValue(d[y].getSortValue());
                                dbcheck.setLabel(d[y].getName());
                                lstinnerCheckboxes.add(dbcheck);
                            }

                        } // for
                    } // if

                } // for

                // sort checkboxes based on sort value
                Collections.sort(lstinnerCheckboxes);
                Iterator<DatabaseCheckbox> itrChk = lstinnerCheckboxes.iterator();
                int easycount = 1;
                int spliteasyevery = 6;
                while (itrChk.hasNext()) {
                    DatabaseCheckbox dbcheck = (DatabaseCheckbox) itrChk.next();
                    innercheckboxes.append(dbcheck.toString());
                    innercheckboxes.append("&nbsp;&nbsp;");
                    if ((easycount % spliteasyevery) == 0) {
                        innercheckboxes.append("<br/>");
                    }
                    easycount = easycount + 1;
                }

                // finish the 'All' checkbox, label, and spacers
                alldbcheckbox.append(" style=\"vertical-align:middle;\" value=\"").append(sum)
                    .append("\" id=\"allchkbx\" onClick=\"change(\'alldb\');\"/><label class=\"SmBlackText\" for=\"allchkbx\">All</label></a> &nbsp;&nbsp; ");
                // append the 'All' checkbox
                formhtml.append(alldbcheckbox);
                // append the inner checkboxes
                formhtml.append(innercheckboxes);
            }
        } else {
            // if this is whole power of 2
            if (isWholeDBMask(umask)) {
                // NO CHECKBOXES - Display Single Database as a Label
                String dbName = DatabaseDisplayHelper.getDisplayName(umask);
                formhtml.append("<input type=\"hidden\" name=\"database\" value=\"").append(umask).append("\"/>");
                formhtml.append("<img src=\"/static/images/s.gif\" width=\"4\"/>");
                formhtml.append("<a CLASS=\"MedBlackText\"><b>").append(dbName).append("</b></a>");
            } else {
                // Start outer table which will contain label, spacing cells and
                // single cell
                // which will contain checkboxes
                formhtml.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

                // top row has single spacer cell followed by double span cell
                // with bold "SELECT DATABASE" label
                formhtml.append("<tr>");
                formhtml.append("<td valign=\"top\" width=\"4\" bgcolor=\"#C3C8D1\">");
                formhtml.append("<img src=\"/static/images/s.gif\" width=\"4\"/></td>");
                formhtml.append("<td valign=\"top\" colspan=\"4\" bgcolor=\"#C3C8D1\">");
                formhtml.append("<a CLASS=\"SmBlueTableText\"><b>SELECT DATABASE</b></a>");
                formhtml.append("</td>");
                formhtml.append("</tr>");

                // bottom row has two spacer cells followed by single span cell
                // which will hold checkboxes
                formhtml.append("<tr>");
                formhtml.append("<td valign=\"top\" width=\"4\" bgcolor=\"#C3C8D1\">");
                formhtml.append("<img src=\"/static/images/s.gif\" width=\"4\"/></td>");
                formhtml.append("<td valign=\"top\" width=\"15\" bgcolor=\"#C3C8D1\">");
                formhtml.append("<img src=\"/static/images/s.gif\" width=\"15\"/></td>");
                formhtml.append("<td valign=\"top\" bgcolor=\"#C3C8D1\">");

                // start the inner table
                formhtml.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"5\"><tr>");

                Displayer disp = new Displayer();

                List<DatabaseCheckbox> lstinnerCheckboxes = new LinkedList<DatabaseCheckbox>();
                for (int x = 0; x < dbMasks.length; x++) {
                    if ((umask & dbMasks[x]) == dbMasks[x]) {

                        Database[] d = (DatabaseConfig.getInstance()).getDatabases(dbMasks[x]);

                        DatabaseCheckbox dbcheck = disp.new DatabaseCheckbox(dbMasks[x]);

                        for (int y = 0; y < d.length; y++) {
                            if (d[y] != null) {
                                sum += d[y].getMask();
                                if (!StringUtil.EMPTY_STRING.equals(innercheckboxes)) {
                                    dbcheck.setId(d[y].getID());
                                    if ((selectedMask & dbMasks[x]) == dbMasks[x]) {
                                        dbcheck.setChecked(true);
                                    }
                                }
                                dbcheck.setSortValue(d[y].getSortValue());
                                dbcheck.setLabel(d[y].getName());
                                lstinnerCheckboxes.add(dbcheck);
                            }
                        } // for
                    } // if
                } // for

                // maximum/default is 5 on one row
                int splitevery = 5;
                if (lstinnerCheckboxes.size() > 5) {
                    splitevery = 4;
                }
                if ((lstinnerCheckboxes.size() % 4) == 0) {
                    splitevery = 4;
                } else {
                    if ((lstinnerCheckboxes.size() % 3) == 0) {
                        splitevery = 3;
                    }
                }

                // Add 'All' which occupies the first cell on every row
                splitevery = splitevery + 1;

                // start count at one - this includes the 'All' db
                // checkbox which spans every row
                int displayedCheckboxes = 1;
                int rowcount = 1;
                // sort checkboxes based on sort value
                Collections.sort(lstinnerCheckboxes);
                Iterator<DatabaseCheckbox> itrChk = lstinnerCheckboxes.iterator();
                while (itrChk.hasNext()) {
                    DatabaseCheckbox dbcheck = (DatabaseCheckbox) itrChk.next();

                    // increment count of boxes
                    displayedCheckboxes++;

                    if ((displayedCheckboxes % splitevery) == 1) {
                        innercheckboxes.append("<tr>");
                        // update rowcount and compensate for the row spanning 'All' checkbox
                        // at the begining of every new row
                        rowcount++;
                        displayedCheckboxes++;
                    }
                    innercheckboxes.append("<td valign=\"top\">");
                    innercheckboxes.append(dbcheck.toString());

                    // append the help link and image to the last checkbox
                    if (!itrChk.hasNext()) {
                        innercheckboxes.append("&nbsp;").append(HelpIcon.getHelpLink());
                    }
                    innercheckboxes.append("</td>");

                    // end the row every 'n' checkboxes
                    if ((displayedCheckboxes % splitevery) == 0) {
                        innercheckboxes.append("</tr>");
                    }
                }

                // after loop fill in empty space(s)
                if ((displayedCheckboxes % splitevery) != 0) {
                    innercheckboxes.append("<td colspan=\"").append(splitevery - (displayedCheckboxes % splitevery)).append("\">&#160;</td>");
                } else {
                    innercheckboxes.append("</tr>");
                }

                // set the rowspan of the first checkbox to be the entire height of the table
                alldbcheckbox.append("<td valign=\"top\" rowspan=\"").append(rowcount).append("\">");
                alldbcheckbox.append("<input type=\"checkbox\" name=\"alldb\"");
                // finish the 'All' checkbox
                alldbcheckbox.append(" style=\"vertical-align:middle;\" id=\"allchkbx\" value=\"").append(sum)
                    .append("\" onClick=\"change(\'alldb\');\"/><label class=\"SmBlackText\" for=\"allchkbx\">All</label></td>");
                // append the 'All' checkbox
                formhtml.append(alldbcheckbox);
                // append the inner checkboxes
                formhtml.append(innercheckboxes);
                // end the inner table
                formhtml.append("</table>");

                formhtml.append("</td>");
                formhtml.append("</tr>");
                formhtml.append("</table>");
            }
        }

        return formhtml.toString();
    }

    // innercheckboxes.append("<input type=\"checkbox\" name=\"database\" value=\"")
    // .append(dbMasks[x]).append("\" onClick=\"change(\'database\')\";");

    private class DatabaseCheckbox implements Comparable<Object> {
        // private String type = "checkbox";
        // private String chkname = "database";
        // private String onclickevent = "change(\'database\')";

        private String chkid = null;
        private String chkvalue = null;
        private boolean checked = false;
        private String strlabel = null;
        private int sortValue = 0;

        public int compareTo(Object obj) {
            DatabaseCheckbox d = (DatabaseCheckbox) obj;
            if (d != null) {
                if (getSortValue() < d.getSortValue()) {
                    return -1;
                } else if (getSortValue() > d.getSortValue()) {
                    return 1;
                } else if (getSortValue() == d.getSortValue()) {
                    return 0;
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        }

        public void setLabel(String label) {
            strlabel = label;
        }

        public void setId(String id) {
            chkid = id;
        }

        public void setChecked(boolean ischecked) {
            checked = ischecked;
        }

        public DatabaseCheckbox(int value) {
            chkvalue = String.valueOf(value);
        }

        public String toString() {
            StringBuffer dbcheckbox = new StringBuffer();
            dbcheckbox.append("<input style=\"vertical-align:middle\" type=\"checkbox\" name=\"database\" onClick=\"change(\'database\');\"");
            dbcheckbox.append(" value=\"" + chkvalue + "\"");
            dbcheckbox.append(" id=\"" + chkid + "chkbx\"");
            dbcheckbox.append((checked ? " checked=\"checked\"" : ""));
            dbcheckbox.append(" />");

            dbcheckbox.append("<label class=\"SmBlackText\" for=\"").append(chkid).append("chkbx\">");
            dbcheckbox.append(strlabel);
            dbcheckbox.append("</label>");

            return dbcheckbox.toString();
        }

        public int getSortValue() {
            return sortValue;
        }

        public void setSortValue(int sortValue) {
            this.sortValue = sortValue;
        }
    }
}