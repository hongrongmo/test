package org.ei.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ei.session.UserPreferences;
import org.ei.stripes.EVActionBeanContext;
import org.ei.util.StringUtil;

public class MoreSearchSources {

    private static Map<String, String> dataBaseNames = new Hashtable<String, String>();

    @SuppressWarnings("rawtypes")
    protected HashMap moresources = new HashMap();
    protected List<String> moresourceslinks = null;

    static {

        // dataBaseNames.put("CPX", "Compendex"); // - - - -BIBLIOGRAPHIC DATABASES- - - -
        // dataBaseNames.put("INS", "Inspec");
        // dataBaseNames.put("NTI", "NTIS");
        dataBaseNames.put("GEO", "GEO");
        // dataBaseNames.put("PAG", "Referex|Referex"); // - - - -eBOOK- - - -
        dataBaseNames.put("SPI", "SPIEWeb");
        dataBaseNames.put("OJP", "OJPS");
        dataBaseNames.put("CRC", "16|CRC ENGnetBASE");           // - - - -HANDBOOKS- - - -
        dataBaseNames.put("ESPACENET", "Esp@cenet|Espacenet");  	     // - - - -PATENTS- - - -
        dataBaseNames.put("UPO", "8|USPTO");
        dataBaseNames.put("GLOBAL_SPEC", "GSP|GlobalSpec"); 	         // - - - -SPECIFICATIONS- - - -
        // dataBaseNames.put("IHS", "IHS|IHS Standards"); // - - - -STANDARDS- - - -
        // dataBaseNames.put("CSS", "Techstreet Standards|Techstreet Standards");
        dataBaseNames.put("SCIRUS", "Scirus|Scirus");  	         // - - - -WEB SEARCH- - - -
        // dataBaseNames.put("EEV", "EEVL|Intute");
        // EMSAT cartridge is back in use but it point to KellySearch it now points to ReedLink
        // dataBaseNames.put("EMS", "ReedLink|ReedLink");
        dataBaseNames.put("LEXIS-NEXIS_NEWS", "LexisNexis|LexisNexis News");  // - - - -LEXISNEXIS NEWS- - - -
        dataBaseNames.put("PCH", "PaperChem"); 	                 // - - - -PAPER VILLAGE- - - -
        dataBaseNames.put("CHM", "Chimica");  	                 // - - - -Chemica VILLAGE- - - -
        dataBaseNames.put("CBN", "CBNB");  	                     // - - - - CBNB - - - -//
        dataBaseNames.put("ELT", "EncompassLit");  	             // - - - - Encompass - - - - //
        dataBaseNames.put("EPT", "EncompassPat");

    }

    // - - - -BIBLIOGRAPHIC DATABASES- - - -
    private int[] dbMasks = { org.ei.domain.DatabaseConfig.CBF_MASK, org.ei.domain.DatabaseConfig.IBS_MASK };

    private String[] links = { "PAG", "CRC", "EMS", "GLOBAL_SPEC", "IHS", "UPO", "ESPACENET", "SCIRUS", "EEV", "LEXIS-NEXIS_NEWS" };

    /**
     * This method loads more search resources
     *
     * @param
     * @return Links-- Referex, CRC ENGnetBASE, ReedLink, GlobalSpec, IHS Standards, USPTO, esp@cenet, Scirus, EEVL, LexisNexis News //The order here will
     *         affect the order in the "More Search Sources" box on the site! Local Databases come first // The order of these "More Search Sources" is
     *         determined here as they are added to the output buffer. // BOOK SEARCH-- "PAG","CRC","EMS","GSP" // PATENTS-- "UPO","ESN","SCI","EEV" //
     *         LEXISNEXIS NEWS-- "LEX" // Right now it is always hardcoded for all userslater on, if it is added in the user cartridge, it can be retrieved by
     *         following code // LEX USer Cartridge LEX is a 'negative' cartridge. So if the does NOT have the cartridge, include LexisNexis News in the output.
     */
    public List<String> getMoreSearchSources(String[] cars, String sessionid, EVActionBeanContext context) {

        moresourceslinks = new ArrayList<String>();
        // String[] cars = context.getUserSession().getUser().getCartridge();
        UserPreferences userprefs = context.getUserSession().getUser().getUserPreferences();
        List<String> lstCarts = Arrays.asList(cars);
        int userMask = (DatabaseConfig.getInstance()).getMask(cars);

        if (lstCarts.size() > 0) {
            List<String> moresearchnames = new ArrayList<String>(dbMasks.length + links.length);
            for (int x = 0; x < dbMasks.length; x++) {
                if ((userMask & dbMasks[x]) == dbMasks[x]) {
                    String[] dbMasksNameValues = new String[3];
                    Database[] d = (DatabaseConfig.getInstance()).getDatabases(dbMasks[x]);
                    int sum = 0;
                    String name = StringUtil.EMPTY_STRING;
                    for (int y = 0; y < d.length; y++) {
                        if (d[y] != null) {
                            sum += d[y].getMask();
                            if (!StringUtil.EMPTY_STRING.equals(name)) {
                                name = name.concat(" &amp; ");
                            }
                            name = name.concat(d[y].getName());
                        }
                    } // for

                    dbMasksNameValues[0] = String.valueOf(sum);
                    dbMasksNameValues[1] = name;

                    if (dbMasksNameValues[0] != null && !dbMasksNameValues[0].equals("null") && dbMasksNameValues[1] != null
                        && !dbMasksNameValues[1].equals("null")) {

                        dbMasksNameValues[2] = getAltTag(dbMasksNameValues[1], dbMasksNameValues[0]);
                        moresearchnames.add(dbMasksNameValues[1]);
                        addMoresources(dbMasksNameValues);
                    }
                } // if
            } // for

            for (int listsize = 0; listsize < links.length; listsize++) {
                String[] dbMoreSources = new String[3];
                String[] dbNameValues = null;

                if (userprefs.getMoreSearchSources(links[listsize]) && dataBaseNames.get(links[listsize]) != null) {

                    dbNameValues = ((String) dataBaseNames.get(links[listsize])).split("\\|");

                } else if (links[listsize].equals("IHS") && (dataBaseNames.get(links[listsize]) != null)) {

                    dbNameValues = ((String) dataBaseNames.get(links[listsize])).split("\\|");

                } else if (!userprefs.getMoreSearchSources(links[listsize]) && ((lstCarts.contains(links[listsize])))
                    && dataBaseNames.get(links[listsize]) != null) {

                    dbNameValues = ((String) dataBaseNames.get(links[listsize])).split("\\|");
                }

                if (dbNameValues != null) {

                    if (dbNameValues[0] != null && !dbNameValues[0].equals("null") && dbNameValues[1] != null && !dbNameValues[1].equals("null")) {

                        dbMoreSources[0] = dbNameValues[0];
                        dbMoreSources[1] = dbNameValues[1];
                        dbMoreSources[2] = getAltTag(dbMoreSources[1], dbMoreSources[0]);
                        moresearchnames.add(dbMoreSources[1]);
                        addMoresources(dbMoreSources);
                    }
                }
            }

            Collections.sort(moresearchnames);

            for (int nameslist = 0; nameslist < moresearchnames.size(); nameslist++) {
                moresourceslinks.add(toString((String[]) moresources.get(moresearchnames.get(nameslist)), sessionid));
            }
        }

        return moresourceslinks;
    }

    // Alt Tags for more search sources
    private String getAltTag(String name, String value) {

        String altTag = "";

        if (value.equals("16")) {
            altTag = "CRCnetBASE - search over 8000 books that span 40 disciplines";
        } else if (value.equals("Esp@cenet")) {
            altTag = "Espacenet - access to 70 million patents worldwide";
        } else if (value.equals("GSP")) {
            altTag = "GlobalSpec - an information resource for the engineering community";
        } else if (value.equals("IHS")) {
            altTag = "IHS Standards - Search the world's largest collection of technical standards";
        } else if (value.equals("LexisNexis")) {
            altTag = "Access the latest news from LexisNexis";
        } else if (value.equals("Scirus")) {
            altTag = "Scirus - the most comprehensive scientific research tool on the web";
        } else if (value.equals("8")) {
            altTag = "United States Patent and Trademark Office - Patent Full-Text Databases";
        } else {
            altTag = name;
        }

        return altTag;
    }

    public Map<String, String> getDataBaseNames() {
        return dataBaseNames;
    }

    public void setDataBaseNames(Map<String, String> dataBaseNames) {
        MoreSearchSources.dataBaseNames = dataBaseNames;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addMoresources(String[] dbmoreresources) {
        if (this.moresources == null)
            this.moresources = new HashMap();
        this.moresources.put(dbmoreresources[1], dbmoreresources);
    }

    /** returns toString */
    public String toString(String[] moresources, String sessionid) {
        StringBuffer moresourceslink = new StringBuffer();
        moresourceslink.append(" <a target=\"_blank\" title=\"" + moresources[2] + "\"");
        moresourceslink.append(" href=\"#\" onclick=\"javascript:openRemoteDb('" + moresources[0] + "','" + sessionid + "','');return false;\">");
        moresourceslink.append("<b>" + moresources[1] + "</b></a>");

        return moresourceslink.toString();
    }

}