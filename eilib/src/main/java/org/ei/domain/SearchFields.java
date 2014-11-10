package org.ei.domain;

import java.util.ArrayList;

public class SearchFields {
    public static SearchField ALL = new SearchField("ALL", "All fields");
    public static SearchField AB = new SearchField("AB", "Abstract");
    public static SearchField AN = new SearchField("AN", "Accession number");
    public static SearchField AI = new SearchField("AI", "Astronomical indexing");
    public static SearchField AJ = new SearchField("AJ", "Derwent accession number");

    public static SearchField AU = new AuthorSearchField("AU", "Author");
    public static SearchField AF = new AffiliationSearchField("AF", "Author affiliation");
    public static SearchField AV = new SearchField("AV", "Availability");
    public static SearchField CI = new SearchField("CI", "Chemical indexing");
    public static SearchField CL = new SearchField("CL", "Classification code");
    public static SearchField OC = new SearchField("OC", "Original classification code");
    public static SearchField CN = new SearchField("CN", "CODEN");
    public static SearchField CC = new SearchField("CC", "Conference code");
    public static SearchField CF = new SearchField("CF", "Conference information");
    public static SearchField CE = new SearchField("CE", "Chemical Acronyms");
    public static SearchField CT = new SearchField("CT", "Contract number");
    public static SearchField CV = new SearchField("CV", "Controlled term");
    public static SearchField CP = new SearchField("CP", "Companies");
    public static SearchField CM = new SearchField("CM", "Chemicals");
    public static SearchField CVP = new SearchField("CVP", "Controlled term as a product");
    public static SearchField CVA = new SearchField("CVA", "Controlled term as a reagent");
    public static SearchField CVN = new SearchField("CVN", "Controlled term with no role");
    public static SearchField CVM = new SearchField("CVM", "Major term");
    public static SearchField CVMA = new SearchField("CVMA", "Major term as a reagent");
    public static SearchField CVMN = new SearchField("CVMN", "Major term with no role");
    public static SearchField CVMP = new SearchField("CVMP", "Major term as a product");
    public static SearchField DPID = new SearchField("DPID", "Derwent IPC");
    public static SearchField GD = new SearchField("GD", "Industrial Sectors");
    public static SearchField PU = new SearchField("PU", "Country of application");
    public static SearchField CO = new SearchField("CO", "Country of origin ");
    public static SearchField DI = new SearchField("DI", "Discipline");
    public static SearchField DT = new SearchField("DT", "Document type");
    public static SearchField PA = new SearchField("PA", "Patent application date");
    public static SearchField BN = new SearchField("BN", "ISBN");
    public static SearchField SN = new SearchField("SN", "ISSN");
    public static SearchField IC = new SearchField("IC", "SIC Codes");
    public static SearchField LA = new SearchField("LA", "Language");
    public static SearchField LT = new SearchField("LT", "Linked term");
    public static SearchField MH = new SearchField("MH", "Ei main heading");
    public static SearchField MI = new SearchField("MI", "Material identity number");
    public static SearchField YR = new SearchField("YR", "Publication year");

    public static SearchField AG = new SearchField("AG", "Monitoring agency");
    public static SearchField NT = new SearchField("NT", "Notes");
    public static SearchField NI = new SearchField("NI", "Numerical indexing");
    public static SearchField PD = new SearchField("PD", "Publication date");
    public static SearchField PI = new SearchField("PI", "Patent issue date");
    public static SearchField PID = new SearchField("PID", "Int.patent classification");
    public static SearchField PEC = new SearchField("PEC", "ECLA code");
    public static SearchField PAC = new SearchField("PAC", "Patent authority code");
    public static SearchField PAM = new SearchField("PAM", "Patent application number");
    public static SearchField PAN = new SearchField("PAN", "Patent attorney name");
    public static SearchField PCI = new SearchField("PCI", "Patent citation index");
    public static SearchField PCO = new SearchField("PCO", "Patent application country");
    public static SearchField PFD = new SearchField("PFD", "Patent filing date");
    public static SearchField PRN = new SearchField("PRN", "Patent priority information");
    public static SearchField PRD = new SearchField("PRD", "Priority date");
    public static SearchField PRC = new SearchField("PRC", "Priority country");

    public static SearchField PM = new SearchField("PM", "Patent number");
    public static SearchField PE = new SearchField("PE", "Patent examiner");
    public static SearchField SO = new SearchField("SO", "Source");

    public static SearchField PN = new SearchField("PN", "Publisher");
    public static SearchField PUC = new SearchField("PUC", "US classification");
    public static SearchField RN = new SearchField("RN", "Report number");
    public static SearchField ST = new SearchField("ST", "Source title");
    public static SearchField KY = new SearchField("KY", "Subject/Title/Abstract");
    public static SearchField TI = new SearchField("TI", "Title");
    public static SearchField TR = new SearchField("TR", "Treatment type");
    public static SearchField FL = new SearchField("FL", "Uncontrolled term");
    public static SearchField RGI = new SearchField("RGI", "Regional terms");
    public static SearchField PC = new SearchField("PC", "Patent Country");
    public static SearchField IP = new SearchField("IP", "Int.patent classification");
    public static SearchField CR = new SearchField("CR", "CAS registry number");
    public static SearchField RO = new SearchField("RO", "Role");

    public static SearchField VO = new SearchField("VO", "Volume");
    public static SearchField SU = new SearchField("SU", "Issue");

    // EnCompass fields

    public static final SearchField[] ALL_FIELDS = { AB, AF, AG, AI, AJ, ALL, AN, AU, AV, BN, CC, CE, CF, CI, CL, CM, CN, CO, CP, CR, CT, CV, CVA, CVM, CVMA,
        CVMN, CVMP, CVN, CVP, DI, DT, FL, GD, IC, IP, KY, LA, LT, MH, MI, NI, NT, OC, PA, PAC, PAM, PAN, PC, PCI, PCO, PD, PE, PEC, PFD, PI, PID, DPID, PM, PN,
        PRN, PRC, PRD, PU, PUC, RGI, RN, RO, SN, SO, ST, SU, TI, TR, VO, YR };

    /*
     * This method is used to an array of CustomeSearchFields based on the users full mask and the users scrubbed mask.
     */

    public static CustomSearchField[] unionSearchFields(int userMaskMax, int userScrubbedMask) {
        return unionFields(ALL_FIELDS, userMaskMax, userScrubbedMask);
    }

    private static CustomSearchField[] unionFields(SearchField[] allFields, int userMaskMax, int userScrubbedMask) {
        Database[] databases = (DatabaseConfig.getInstance()).getDatabases(userScrubbedMask);
        ArrayList<CustomSearchField> fieldList = new ArrayList<CustomSearchField>();
        for (int i = 0; i < allFields.length; i++) {

            SearchField sf = allFields[i];
            ArrayList<Database> databaseList = new ArrayList<Database>();
            for (int c = 0; c < databases.length; c++) {
                if (databases[c].hasField(sf, userMaskMax)) {
                    databaseList.add(databases[c]);
                }
            }

            if (databaseList.size() > 0) {
                fieldList.add(new CustomSearchField(((Database[]) databaseList.toArray(new Database[databaseList.size()])), sf));
            }
        }

        return (CustomSearchField[]) fieldList.toArray(new CustomSearchField[fieldList.size()]);
    }
}