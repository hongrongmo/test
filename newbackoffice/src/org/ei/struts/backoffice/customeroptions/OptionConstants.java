package org.ei.struts.backoffice.customeroptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.LabelValueBean;
import org.ei.struts.backoffice.Constants;

public final class OptionConstants {

    private static Log log = LogFactory.getLog(OptionConstants.class);

    public static Collection getAllLitbulletins(String strProductID) {

        Collection bulletins = new ArrayList();

        // lit bulletins
        bulletins.add(new LabelValueBean(A_Label, A_Cartridge));
        bulletins.add(new LabelValueBean(CZL_Label, CZL_Cartridge));
        bulletins.add(new LabelValueBean(FRL_Label, FRL_Cartridge));
        bulletins.add(new LabelValueBean(HE_Label, HE_Cartridge));
        bulletins.add(new LabelValueBean(NG_Label, NG_Cartridge));
        bulletins.add(new LabelValueBean(OCL_Label, OCL_Cartridge));
        bulletins.add(new LabelValueBean(PRP_Label, PRP_Cartridge));
        bulletins.add(new LabelValueBean(PS_L_Label, PS_L_Cartridge));
        bulletins.add(new LabelValueBean(TS_Label, TS_Cartridge));
        bulletins.add(new LabelValueBean(TL_Label, TL_Cartridge));

        bulletins.add(new LabelValueBean(LIT_PDF_Label, LIT_PDF_Cartridge));
        bulletins.add(new LabelValueBean(LIT_HTM_Label, LIT_HTM_Cartridge));

        return bulletins;
    }

    public static Collection getAllPatbulletins(String strProductID) {

        Collection bulletins = new ArrayList();

        // pat bulletins
        bulletins.add(new LabelValueBean(CZP_Label, CZP_Cartridge));
        bulletins.add(new LabelValueBean(CP_Label, CP_Cartridge));
        bulletins.add(new LabelValueBean(ETS_Label, ETS_Cartridge));
        bulletins.add(new LabelValueBean(FRP_Label, FRP_Cartridge));
        bulletins.add(new LabelValueBean(OCP_Label, OCP_Cartridge));
        bulletins.add(new LabelValueBean(PP_Label, PP_Cartridge));
        bulletins.add(new LabelValueBean(PSP_Label, PSP_Cartridge));
        bulletins.add(new LabelValueBean(PS_P_Label, PS_P_Cartridge));
        bulletins.add(new LabelValueBean(POL_Label, POL_Cartridge));
        bulletins.add(new LabelValueBean(TP_Label, TP_Cartridge));

        bulletins.add(new LabelValueBean(PAT_PDF_Label, PAT_PDF_Cartridge));
        bulletins.add(new LabelValueBean(PAT_HTM_Label, PAT_HTM_Cartridge));

        return bulletins;
    }

    public static Collection getReferexOptions() {

        Collection cartridges = new ArrayList();

        cartridges.add(new LabelValueBean(ELE_Label, ELE_Cartridge));
        cartridges.add(new LabelValueBean(CHE_Label, CHE_Cartridge));
        cartridges.add(new LabelValueBean(MAT_Label, MAT_Cartridge));
        cartridges.add(new LabelValueBean(COM_Label, COM_Cartridge));
        cartridges.add(new LabelValueBean(CIV_Label, CIV_Cartridge));
        cartridges.add(new LabelValueBean(SEC_Label, SEC_Cartridge));

        cartridges.add(new LabelValueBean(BPE_Label, BPE_Cartridge));
        cartridges.add(new LabelValueBean(ELE1_Label, ELE1_Cartridge));
        cartridges.add(new LabelValueBean(CHE1_Label, CHE1_Cartridge));
        cartridges.add(new LabelValueBean(MAT1_Label, MAT1_Cartridge));
        cartridges.add(new LabelValueBean(ELE2_Label, ELE2_Cartridge));
        cartridges.add(new LabelValueBean(CHE2_Label, CHE2_Cartridge));
        cartridges.add(new LabelValueBean(MAT2_Label, MAT2_Cartridge));
        cartridges.add(new LabelValueBean(ELE3_Label, ELE3_Cartridge));
        cartridges.add(new LabelValueBean(CHE3_Label, CHE3_Cartridge));
        cartridges.add(new LabelValueBean(MAT3_Label, MAT3_Cartridge));
        cartridges.add(new LabelValueBean(COM3_Label, COM3_Cartridge));
        cartridges.add(new LabelValueBean(CIV3_Label, CIV3_Cartridge));
        cartridges.add(new LabelValueBean(SEC3_Label, SEC3_Cartridge));
        cartridges.add(new LabelValueBean(ELE4_Label, ELE4_Cartridge));
        cartridges.add(new LabelValueBean(CHE4_Label, CHE4_Cartridge));
        cartridges.add(new LabelValueBean(MAT4_Label, MAT4_Cartridge));
        cartridges.add(new LabelValueBean(COM4_Label, COM4_Cartridge));
        cartridges.add(new LabelValueBean(CIV4_Label, CIV4_Cartridge));
        cartridges.add(new LabelValueBean(SEC4_Label, SEC4_Cartridge));

        cartridges.add(new LabelValueBean(ELE5_Label, ELE5_Cartridge));
        cartridges.add(new LabelValueBean(CHE5_Label, CHE5_Cartridge));
        cartridges.add(new LabelValueBean(MAT5_Label, MAT5_Cartridge));
        cartridges.add(new LabelValueBean(CIV5_Label, CIV5_Cartridge));

        cartridges.add(new LabelValueBean(ELE6_Label, ELE6_Cartridge));
        cartridges.add(new LabelValueBean(CHE6_Label, CHE6_Cartridge));
        cartridges.add(new LabelValueBean(MAT6_Label, MAT6_Cartridge));
        cartridges.add(new LabelValueBean(COM6_Label, COM6_Cartridge));
        cartridges.add(new LabelValueBean(CIV6_Label, CIV6_Cartridge));
        cartridges.add(new LabelValueBean(SEC6_Label, SEC6_Cartridge));

        return cartridges;
    }


    public static String[] getSelectedDefaultDatabases(String strProductID) {

        // These are the checked default database cartridges
        Collection defaults = new ArrayList();
        if (strProductID.equals(Constants.EV2)) {
            defaults.add("1");
        }
        return (String[]) defaults.toArray(new String[] {});
    }

    public static String[] getDefaultDBOptions(String strProductID) {

        // These are the initially checked cartridges
        Collection defaults = new ArrayList();
        if (strProductID.equals(Constants.EV2)) {
            defaults.add(CPX_Cartridge);
            // defaults.add(WEB_Cartridge);
            defaults.add(UPO_Cartridge);
            defaults.add(CRC_Cartridge);
            defaults.add(ESN_Cartridge);
            defaults.add(SCI_Cartridge);
            defaults.add(THS_Cartridge);
        }
        return (String[]) defaults.toArray(new String[] {});
    }

    public static Collection getUserInterfaceOptions() {

        Collection cartridges = new ArrayList();

        // moved thesaurus to UI tab - did not seem to fit with databases
        // since it is more of a UI feature than a real 'database'
        cartridges.add(new LabelValueBean(THS_Label, THS_Cartridge));
        cartridges.add(new LabelValueBean(FUL_Label, FUL_Cartridge));
        cartridges.add(new LabelValueBean(CFU_Label, CFU_Cartridge));
        cartridges.add(new LabelValueBean(IBL_Label, IBL_Cartridge));

        cartridges.add(new LabelValueBean(LHL_Label, LHL_Cartridge));
        cartridges.add(new LabelValueBean(REF_Label, REF_Cartridge));
        // turn off lexis-nexis news cartridge
        cartridges.add(new LabelValueBean(LEX_Label, LEX_Cartridge));

        cartridges.add(new LabelValueBean(CCL_Label, CCL_Cartridge));

        cartridges.add(new LabelValueBean(LHC_Label, LHC_Cartridge));
        cartridges.add(new LabelValueBean(EZY_Label, EZY_Cartridge));
        cartridges.add(new LabelValueBean(EBX_Label, EBX_Cartridge));

        cartridges.add(new LabelValueBean(LGO_Label, LGO_Cartridge));
        cartridges.add(new LabelValueBean(RSS_Label, RSS_Cartridge));

        cartridges.add(new LabelValueBean(BLG_Label, BLG_Cartridge));

        cartridges.add(new LabelValueBean(GAR_Label, GAR_Cartridge));

        cartridges.add(new LabelValueBean(TAG_Label, TAG_Cartridge));

        return (cartridges);

    }
    public static Collection getMoreDBOptions(String strProductID) {

        Collection cartridges = new ArrayList();

        if (strProductID.equals(Constants.EV2)) {

            cartridges.add(new LabelValueBean(DSS_Label, DSS_Cartridge));
            cartridges.add(new LabelValueBean(ESN_Label, ESN_Cartridge));
            cartridges.add(new LabelValueBean(SCI_Label, SCI_Cartridge));
            cartridges.add(new LabelValueBean(EMS_Label, EMS_Cartridge));
            cartridges.add(new LabelValueBean(EEV_Label, EEV_Cartridge));
            cartridges.add(new LabelValueBean(OJP_Label, OJP_Cartridge));
            cartridges.add(new LabelValueBean(SPI_Label, SPI_Cartridge));
            cartridges.add(new LabelValueBean(SOL_Label, SOL_Cartridge));

            cartridges.add(new LabelValueBean(GSP_Label, GSP_Cartridge));

        }
        return (cartridges);
    }

    public static Collection getAllDBOptions(String strProductID) {

        Collection cartridges = new ArrayList();

        if (strProductID.equals(Constants.EV2)) {
            cartridges.add(new LabelValueBean(CPX_Label, CPX_Cartridge));
            cartridges.add(new LabelValueBean(C84_Label, C84_Cartridge));
            cartridges.add(new LabelValueBean(ZBF_Label, ZBF_Cartridge));
            cartridges.add(new LabelValueBean(INS_Label, INS_Cartridge));
            cartridges.add(new LabelValueBean(IBF_Label, IBF_Cartridge));
            cartridges.add(new LabelValueBean(IBS_Label, IBS_Cartridge));

            cartridges.add(new LabelValueBean(NTI_Label, NTI_Cartridge));
            //cartridges.add(new LabelValueBean(THS_Label, THS_Cartridge));

            // Referex is not a cartridge in itself
            // it is assigned by the selection of Collections
            // Adding this checkbox would probably cause more configuration problems
            //cartridges.add(new LabelValueBean(PAG_Label, PAG_Cartridge));

            cartridges.add(new LabelValueBean(UPA_Label, UPA_Cartridge));
            cartridges.add(new LabelValueBean(EUP_Label, EUP_Cartridge));

            // jam - added CBNB Cart to EV2 Options
            cartridges.add(new LabelValueBean(CBN_Label, CBN_Cartridge));
            cartridges.add(new LabelValueBean(GEO_Label, GEO_Cartridge));
            cartridges.add(new LabelValueBean(GRF_Label, GRF_Cartridge));
            cartridges.add(new LabelValueBean(PCH_Label, PCH_Cartridge));
            // jam - added CHM Cart to EV2 Options
            cartridges.add(new LabelValueBean(CHM_Label, CHM_Cartridge));
            // jam - added Encompass Carts to EV2 Options
            cartridges.add(new LabelValueBean(ELT_Label, ELT_Cartridge));
            cartridges.add(new LabelValueBean(EPT_Label, EPT_Cartridge));

            cartridges.add(new LabelValueBean(UPO_Label, UPO_Cartridge));
            cartridges.add(new LabelValueBean(CRC_Label, CRC_Cartridge));

            // Move more search sources/external DBs to separate method
        }
        return (cartridges);
    }

    public static Collection getDefaultSortByOptions(String strProductID) {

        Collection alloptions = new ArrayList();

        if (strProductID.equals(Constants.EV2)) {
            alloptions.add(new LabelValueBean("default", Constants.EMPTY_STRING));
            alloptions.add(new LabelValueBean("Relevance", "re"));
            alloptions.add(new LabelValueBean("Publication year", "yr"));
        }
        return (alloptions);
    }

    // The 'Value' in these entries is equal to the CID
    // which is used to build the default redirect location
    // for the application
    public static Collection getDefaultStartPageOptions(String strProductID) {

        Collection alloptions = new ArrayList();

        if (strProductID.equals(Constants.EV2)) {
            alloptions.add(new LabelValueBean("default", Constants.EMPTY_STRING));
            alloptions.add(new LabelValueBean("Easy Search", "easySearch"));
            alloptions.add(new LabelValueBean("Quick Search", "quickSearch"));
            alloptions.add(new LabelValueBean("Expert Search", "expertSearch"));
            alloptions.add(new LabelValueBean("Thesaurus", "thesHome"));
            alloptions.add(new LabelValueBean("eBook Search", "ebookSearch"));
            alloptions.add(new LabelValueBean("Help", "help"));
            alloptions.add(new LabelValueBean("Tags", "tagsLoginForm"));
        } else if (strProductID.equals(Constants.ENC2)) {
            alloptions.add(new LabelValueBean("default", Constants.EMPTY_STRING));
            alloptions.add(new LabelValueBean("Quick Search", "quickSearch"));
            alloptions.add(new LabelValueBean("Expert Search", "expertSearch"));
            alloptions.add(new LabelValueBean("Bulletins", "bulletins"));
            alloptions.add(new LabelValueBean("Help", "help"));
        }

        return (alloptions);
    }

    public static Collection getDefaultDatabaseOptions(String strProductID) {

        Collection alloptions = new ArrayList();

        if (strProductID.equals(Constants.ENC2)) {
            alloptions.add(new LabelValueBean("default", Constants.EMPTY_STRING));
            alloptions.add(new LabelValueBean("Compendex", "1"));
            alloptions.add(new LabelValueBean("Inspec", "2"));
            alloptions.add(new LabelValueBean("NTIS", "4"));
            alloptions.add(new LabelValueBean("EnCompassLIT", "1024"));
        }
        return (alloptions);
    }

    public static Collection getYearRange() {
        return getYearRange(1884, 0);
    }

    public static Collection getYearRange(int startyear) {
        return getYearRange(startyear, 0);
    }

    public static Collection getYearRange(int startyear, int endyear) {
        Collection colAllYears = new ArrayList();

        colAllYears.add(new LabelValueBean("default", Constants.EMPTY_STRING));

        if (endyear <= 0) {
            endyear = Calendar.getInstance().get(Calendar.YEAR);
        }

        for (int i = startyear; i < endyear; i++) {
            colAllYears.add(new LabelValueBean(String.valueOf(i), String.valueOf(i)));
        }

        return colAllYears;
    }

    public static Collection getYesNoOption() {

        Collection colAllStatus = new ArrayList();

        colAllStatus.add(new LabelValueBean("default", Constants.EMPTY_STRING));
        colAllStatus.add(new LabelValueBean("Yes", Constants.YES));
        colAllStatus.add(new LabelValueBean("No", Constants.NO));

        return (colAllStatus);
    }

    public static Collection getOnOffOption() {

        Collection colAllStatus = new ArrayList();

        colAllStatus.add(new LabelValueBean("default", Constants.EMPTY_STRING));
        colAllStatus.add(new LabelValueBean("On", Constants.ON));
        colAllStatus.add(new LabelValueBean("Off", Constants.OFF));

        return (colAllStatus);
    }

    // private method used for getting key value
    // from array of Cartridges
    public static String getKeyValueFromCartridges(String strkey, String[] carts) {
        String value = null;
        for (int j = 0; j < carts.length; j++) {
            String carvalue = (String) carts[j];
            if (carvalue.indexOf(strkey) >= 0) {
                value = carvalue.substring(carvalue.indexOf(strkey)+ strkey.length(), carvalue.length());
                log.debug(strkey + "-----------------" + value);
            }
        }
        return value;
    }

    // CV
    public static final String CHM_Label = "Chimica";

    public static final String CHM_Cartridge = "CHM";

    public static final String CBN_Label = "CBNB";

    public static final String CBN_Cartridge = "CBN";

    public static final String BSA_Label = "Beilstein Abstracts";

    public static final String BSA_Cartridge = "BSA";

    public static final String CCD_Label = "Combined Chemical Dictionary";

    public static final String CCD_Cartridge = "CCD";

    public static final String DCC_Label = "Dictionary of Commonly Cited Compounds";

    public static final String DCC_Cartridge = "DCC";

    public static final String HCP_Label = "The Handbook of Chemistry and Physics";

    public static final String HCP_Cartridge = "HCP";

    public static final String PPD_Label = "Polymers: A Property DataBase";

    public static final String PPD_Cartridge = "PPD";

    public static final String POC_Label = "Properties of Organic Compounds";

    public static final String POC_Cartridge = "POC";

    public static final String ENV_Label = "EnvironetBase";

    public static final String ENV_Cartridge = "ENV";

    public static final String USP_Label = "USPTO";

    public static final String USP_Cartridge = "UPO";

    public static final String ESP_Label = "Espacenet";

    public static final String ESP_Cartridge = "ESP";

    public static final String SCI_Label = "Scirus";

    public static final String SCI_Cartridge = "SCI";

    public static final String ACD_Label = "Available Chemical Database";

    public static final String ACD_Cartridge = "ACD";

    public static final String ODS_Label = "OHS Safety Data Sheets";

    public static final String ODS_Cartridge = "ODS";

    public static final String BRC_Label = "Brethericks Reactive Chemical Hazards Database(BH)";

    public static final String BRC_Cartridge = "BRC";

    public static final String CEB_Label = "Chemical Engineering & Biotechnology Abstract";

    public static final String CEB_Cartridge = "CEB";

    public static final String RCI_Label = "Reation Citation Index";

    public static final String RCI_Cartridge = "RCI";

    public static final String NSS_Label = "ACD/Labs NWR Spectra - SEARCH";

    public static final String NSS_Cartridge = "NSS";

    public static final String PPS_Label = "ACD/Labs  Physical Properties - SEARCH";

    public static final String PPS_Cartridge = "PPS";

    public static final String NSP_Label = "ACD/Labs NMR Spectra - PREDICT";

    public static final String NSP_Cartridge = "NSP";

    public static final String PPP_Label = "ACD/Labs Physical Properties - PREDICT";

    public static final String PPP_Cartridge = "PPP";

    // EV2
    public static final String CPX_Label = "Compendex";

    public static final String CPX_Cartridge = "CPX";

    // public static final String CQK_Label = "Compendex: last two weeks only";
    // public static final String CQK_Cartridge = "CQK";
    // public static final String WEB_Label = "Website Abstracts";
    // public static final String WEB_Cartridge = "WEB";
    public static final String UPO_Label = "US Patent Office";

    public static final String UPO_Cartridge = "UPO";

    public static final String CRC_Label = "CRC Press Handbooks";

    public static final String CRC_Cartridge = "CRC";

    public static final String SOL_Label = "Solusource";

    public static final String SOL_Cartridge = "SOL";

    // public static final String CIN_Label = "Compendex & Inspec";
    // public static final String CIN_Cartridge = "CIN";
    public static final String INS_Label = "Inspec";

    public static final String INS_Cartridge = "INS";

    public static final String DSS_Label = "Document Souls";

    public static final String DSS_Cartridge = "DSS";

    // public static final String ESN_Label = "Espacenet";
    // public static final String ESN_Cartridge = "ESN";
    // public static final String SCI_Label = "Scirus";
    // public static final String SCI_Cartridge = "SCI";
    public static final String EMS_Label = "KellySearch - (replacement for EMSAT)";

    public static final String EMS_Cartridge = "EMS";

    // public static final String EEV_Label = "Edinburgh Engineering Virtual
    // library";
    // public static final String EEV_Cartridge = "EEV";
    public static final String OJP_Label = "Online Journal publishing Service";

    public static final String OJP_Cartridge = "OJP";

    public static final String SPI_Label = "SPIEWeb";

    public static final String SPI_Cartridge = "SPI";

    // public static final String NTI_Label = "National Technical Information
    // Service";
    // public static final String NTI_Cartridge = "NTI";
    public static final String THS_Label = "Thesaurus";

    public static final String THS_Cartridge = "THS";

    public static final String C84_Label = "Compendex Backfile";

    public static final String C84_Cartridge = "C84";

    public static final String ZBF_Label = "Engineering Index backfile";
    public static final String ZBF_Cartridge = "ZBF";

    public static final String IBS_Label = "Inspec Archive standalone";
    public static final String IBS_Cartridge = "IBS";

    // public static final String GSP_Label = "GlobalSpec";
    // public static final String GSP_Cartridge = "GSP";

    public static final String FUL_Label = "FUL - Full-text linking OFF - Abstract and Detailed";

    public static final String FUL_Cartridge = "FUL";

    public static final String CFU_Label = "CFU - Full-text linking OFF - Citation";

    public static final String CFU_Cartridge = "CFU";

    public static final String IBL_Label = "IBL - SFX Image Based Linking ON";

    public static final String IBL_Cartridge = "IBL";


    public static final String LHL_Label = "DDS Service - Linda Hall Library";

    public static final String LHL_Cartridge = "LHL";

    public static final String REF_Label = "Reference Services OFF";

    public static final String REF_Cartridge = "REF";

    public static final String IBF_Label = "Inspec Backfile";

    public static final String IBF_Cartridge = "IBF";

    // jam - added new books & books perpetual

    public static final String PAG_Label = "Referex";
    public static final String PAG_Cartridge = "PAG";

    public static final String OBO_Label = "OBO - Books OFF";
    public static final String OBO_Cartridge = "OBO";

    public static final String ELE_Label = "ELE - Books (Base Electrical)";
    public static final String ELE_Cartridge = "ELE";
    public static final String CHE_Label = "CHE - Books (Base Chemistry)";
    public static final String CHE_Cartridge = "CHE";
    public static final String MAT_Label = "MAT - Books (Base Materials)";
    public static final String MAT_Cartridge = "MAT";
    public static final String COM_Label = "COM - Books (Base Computing)";
    public static final String COM_Cartridge = "COM";
    public static final String CIV_Label = "CIV - Books (Base Civil)";
    public static final String CIV_Cartridge = "CIV";
    public static final String SEC_Label = "SEC - Books (Base Security)";
    public static final String SEC_Cartridge = "SEC";


    public static final String BPE_Label = "Perpetual Books";
    public static final String BPE_Cartridge = "BPE";

    public static final String ELE1_Label = "ELE1 - Books (Supplelement Electrical) 2006 expansion";
    public static final String ELE1_Cartridge = "ELE1";
    public static final String CHE1_Label = "CHE1 - Books (Supplelement Chemistry) 2006 expansion";
    public static final String CHE1_Cartridge = "CHE1";
    public static final String MAT1_Label = "MAT1 - Books (Supplelement Materials) 2006 expansion";
    public static final String MAT1_Cartridge = "MAT1";
    public static final String ELE2_Label = "ELE2 - Books (Supplelement Electrical) 2007 expansion";
    public static final String ELE2_Cartridge = "ELE2";
    public static final String CHE2_Label = "CHE2 - Books (Supplelement Chemistry) 2007 expansion";
    public static final String CHE2_Cartridge = "CHE2";
    public static final String MAT2_Label = "MAT2 - Books (Supplelement Materials) 2007 expansion";
    public static final String MAT2_Cartridge = "MAT2";
    public static final String ELE3_Label = "ELE3 - Books (Supplelement Electrical) 2007 frontlist";
    public static final String ELE3_Cartridge = "ELE3";
    public static final String CHE3_Label = "CHE3 - Books (Supplelement Chemistry) 2007 frontlist";
    public static final String CHE3_Cartridge = "CHE3";
    public static final String MAT3_Label = "MAT3 - Books (Supplelement Materials) 2007 frontlist";
    public static final String MAT3_Cartridge = "MAT3";
    public static final String COM3_Label = "COM3 - Books (Supplelement Computing) 2007 frontlist";
    public static final String COM3_Cartridge = "COM3";
    public static final String CIV3_Label = "CIV3 - Books (Supplelement Civil) 2007 frontlist";
    public static final String CIV3_Cartridge = "CIV3";
    public static final String SEC3_Label = "SEC3 - Books (Supplelement Security) 2007 frontlist";
    public static final String SEC3_Cartridge = "SEC3";

    public static final String ELE4_Label = "ELE4 - Books (Supplelement Electrical) 2008 frontlist";
    public static final String ELE4_Cartridge = "ELE4";
    public static final String CHE4_Label = "CHE4 - Books (Supplelement Chemistry) 2008 frontlist";
    public static final String CHE4_Cartridge = "CHE4";
    public static final String MAT4_Label = "MAT4 - Books (Supplelement Materials) 2008 frontlist";
    public static final String MAT4_Cartridge = "MAT4";
    public static final String COM4_Label = "COM4 - Books (Supplelement Computing) 2008 frontlist";
    public static final String COM4_Cartridge = "COM4";
    public static final String CIV4_Label = "CIV4 - Books (Supplelement Civil) 2008 frontlist";
    public static final String CIV4_Cartridge = "CIV4";
    public static final String SEC4_Label = "SEC4 - Books (Supplelement Security) 2008 frontlist";
    public static final String SEC4_Cartridge = "SEC4";



    public static final String ELE5_Label = "ELE5 - Books (Supplelement Electrical) William Andrew backlist";
    public static final String ELE5_Cartridge = "ELE5";
    public static final String CHE5_Label = "CHE5 - Books (Supplelement Chemistry) William Andrew backlist";
    public static final String CHE5_Cartridge = "CHE5";
    public static final String MAT5_Label = "MAT5 - Books (Supplelement Materials) William Andrew backlist";
    public static final String MAT5_Cartridge = "MAT5";
    public static final String CIV5_Label = "CIV5 - Books (Supplelement Civil) William Andrew backlist";
    public static final String CIV5_Cartridge = "CIV5";

    public static final String ELE6_Label = "ELE6 - Books (Supplelement Electrical) 2009 frontlist";
    public static final String ELE6_Cartridge = "ELE6";
    public static final String CHE6_Label = "CHE6 - Books (Supplelement Chemistry) 2009 frontlist";
    public static final String CHE6_Cartridge = "CHE6";
    public static final String MAT6_Label = "MAT6 - Books (Supplelement Materials) 2009 frontlist";
    public static final String MAT6_Cartridge = "MAT6";
    public static final String COM6_Label = "COM6 - Books (Supplelement Computing) 2009 frontlist";
    public static final String COM6_Cartridge = "COM6";
    public static final String CIV6_Label = "CIV6 - Books (Supplelement Civil) 2009 frontlist";
    public static final String CIV6_Cartridge = "CIV6";
    public static final String SEC6_Label = "SEC6 - Books (Supplelement Security) 2009 frontlist";
    public static final String SEC6_Cartridge = "SEC6";


    // jam - added GEOBase Cart
    public static final String GEO_Label = "GeoBase";
    public static final String GEO_Cartridge = "GEO";

    // jam - added GeoRef Cart
    public static final String GRF_Label = "GeoRef";
    public static final String GRF_Cartridge = "GRF";


    // jam - added TAG OFF Cart
    public static final String TAG_Label = "Tags + Groups OFF";

    public static final String TAG_Cartridge = "TAG";

    // jam - added patents
    // Patents 'Parent'
    // public static final String UPT_Label = "Patents Database";
    // public static final String UPT_Cartridge = "UPT";
    // Patents 'Children'
    public static final String UPA_Label = "US Patents Database";

    public static final String UPA_Cartridge = "UPA";

    public static final String EUP_Label = "European Patents Database";

    public static final String EUP_Cartridge = "EUP";

    public static final String CCL_Label = "Email Alert cc: List";

    public static final String CCL_Cartridge = "CCL";

    // jam - added for Vishali's Local Holding Citation
    public static final String LHC_Label = "Local Holdings in Citation format";

    public static final String LHC_Cartridge = "LHC";

    // jam - added for Easy Search
    public static final String EZY_Label = "Easy Search OFF";

    public static final String EZY_Cartridge = "EZY";

    public static final String EBX_Label = "Easy Search Checkboxes ON";

    public static final String EBX_Cartridge = "EBX";

    // jam - added for custom Logo
    public static final String LGO_Label = "Customized institutional logo";

    public static final String LGO_Cartridge = "LGO";

    public static final String RSS_Label = "RSS OFF";

    public static final String RSS_Cartridge = "RSS";

    public static final String BLG_Label = "Blog This! OFF";

    public static final String BLG_Cartridge = "BLG";

    public static final String LEX_Label = "Lexis-Nexis News OFF";

    public static final String LEX_Cartridge = "LEX";

    public static final String GAR_Label = "Navigator graph and download";

    public static final String GAR_Cartridge = "GAR";

    // PV
    public static final String PCM_Label = "Legacy Paper Chem";

    public static final String PCM_Cartridge = "PCM";

    public static final String PCH_Label = "Paper Chem";

    public static final String PCH_Cartridge = "PCH";

    public static final String NTI_Label = "National Technical Information Service (NTIS)";

    public static final String NTI_Cartridge = "NTI";

    public static final String ESN_Label = "Espacenet";

    public static final String ESN_Cartridge = "ESN";

    // public static final String UPO_Label ="US Patent Office";
    // public static final String UPO_Cartridge = "UPO";
    public static final String GSP_Label = "GlobalSpec";

    public static final String GSP_Cartridge = "GSP";

    // public static final String SCI_Label ="Scirus";
    // public static final String SCI_Cartridge = "SCI";
    public static final String EEV_Label = "Edinburgh Engineering Virtual library";

    public static final String EEV_Cartridge = "EEV";

    public static final String PPW_Label = "PaperOnWeb";

    public static final String PPW_Cartridge = "PPW";

    // EnCompassWeb
    public static final String ELT_Label = "EnCompass Lit";

    public static final String ELT_Cartridge = "ELT";

    public static final String EPT_Label = "EnCompass Pat";

    public static final String EPT_Cartridge = "EPT";

    public static final String LIT_PDF_Label = "LIT Bulletins in PDF";

    public static final String LIT_PDF_Cartridge = "LIT_PDF";

    public static final String LIT_HTM_Label = "LIT Bulletins in HTML";

    public static final String LIT_HTM_Cartridge = "LIT_HTM";

    public static final String PAT_PDF_Label = "PAT Bulletins in PDF";

    public static final String PAT_PDF_Cartridge = "PAT_PDF";

    public static final String PAT_HTM_Label = "PAT Bulletins in HTML";

    public static final String PAT_HTM_Cartridge = "PAT_HTM";

    // EnCompassWeb Bulletins
    public static final String A_Label = "Automotive";

    public static final String A_Cartridge = "a";

    public static final String CZL_Label = "Catalysts & Zelolites/LIT";

    public static final String CZL_Cartridge = "czl";

    public static final String CZP_Label = "Catalysts & Zelolites/PAT";

    public static final String CZP_Cartridge = "czp";

    public static final String CP_Label = "Chemical products";

    public static final String CP_Cartridge = "cp";

    public static final String ETS_Label = "Environment, Transport & Storage";

    public static final String ETS_Cartridge = "ets";

    public static final String FRL_Label = "Fuel Reformulation/LIT";

    public static final String FRL_Cartridge = "frl";

    public static final String FRP_Label = "Fuel Reformulation/PAT";

    public static final String FRP_Cartridge = "frp";

    public static final String HE_Label = "Health & Environment";

    public static final String HE_Cartridge = "he";

    public static final String NG_Label = "Natural Gas";

    public static final String NG_Cartridge = "ng";

    public static final String OCL_Label = "Oilfield Chemicals/LIT";

    public static final String OCL_Cartridge = "ocl";

    public static final String OCP_Label = "Oilfield Chemicals/PAT";

    public static final String OCP_Cartridge = "ocp";

    public static final String PSP_Label = "Petroleum & Specialty Products";

    public static final String PSP_Cartridge = "psp";

    public static final String PP_Label = "Petroleum Processes";

    public static final String PP_Cartridge = "pp";

    public static final String PRP_Label = "Petroleum Refining & Petrochemicals";

    public static final String PRP_Cartridge = "prp";

    public static final String PS_L_Label = "Petroleum Substitutes/LIT";

    public static final String PS_L_Cartridge = "ps_l";

    public static final String PS_P_Label = "Petroleum Substitutes/PAT";

    public static final String PS_P_Cartridge = "ps_p";

    public static final String POL_Label = "Polymers";

    public static final String POL_Cartridge = "pol";

    public static final String TS_Label = "Transportation & Storage";

    public static final String TS_Cartridge = "ts";

    public static final String TL_Label = "Tribology/LIT";

    public static final String TL_Cartridge = "tl";

    public static final String TP_Label = "Tribology/PAT";

    public static final String TP_Cartridge = "tp";

}
