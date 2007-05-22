package org.ei.domain;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SearchForm {

    public static final int ENDYEAR = 2007;


    public static Map getDiscipline(int selecteddbMask) {

        Map disctype = new LinkedHashMap();

        // NOT 2 is DatabaseConfig.INS_MASK
        if(selecteddbMask != DatabaseConfig.INS_MASK)
        {
             disctype.put("NO-LIMIT", "Discipline type not available");
        }
        else
        {
            disctype.put("NO-LIMIT", "All disciplines");
        }
        // A for DatabaseConfig.INS_MASK
        if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
             disctype.put("A", "Physics");
        }
        //B
        if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
            disctype.put("B", "Electrical/Electronic engineering");
        }
        //C
        if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
            disctype.put("C", "Computers/Control engineering");
        }
        //D
        if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
            disctype.put("D", "Information technology");
        }
        //E
        if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
            disctype.put("E", "Manufacturing and production engineering");
        }

        return disctype;

    }

    public static Map getSection(int selecteddbMask) {

        Map sectiontype = new LinkedHashMap();

        // All fields
        // do not show all fields if Database is only PAG
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK)
        {
             sectiontype.put("NO-LIMIT", "All fields");
        }


        // KY
        // do not show KY as S/T/A if DB is only PAG
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK)
        {
             sectiontype.put("KY", "Subject/Title/Abstract");
        }
        else
        {
        	 sectiontype.put("KY", "Keyword");
        }

        // AB
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK)
        {
             sectiontype.put("AB", "Abstract");
        }

        //AU
        if((selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK)
        {
             sectiontype.put("AU", "Author");
        }
        else if(selecteddbMask == DatabaseConfig.UPA_MASK ||
                selecteddbMask == DatabaseConfig.EUP_MASK ||
                selecteddbMask == DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK )
        {
             sectiontype.put("AU", "Inventor");
        }
        else
        {
             sectiontype.put("AU", "Author/Inventor");
        }

        //AF ---  only cpx,ins,ntis
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK)
        {
             sectiontype.put("AF", "Author affiliation");
        }
        else if(selecteddbMask == DatabaseConfig.UPA_MASK ||
                selecteddbMask == DatabaseConfig.EUP_MASK ||
                selecteddbMask == DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK )
        {
             sectiontype.put("AF", "Assignee");
        }
        else if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK)
        {
             sectiontype.put("AF", "Author affiliation/Assignee");
        }

        if((selecteddbMask == DatabaseConfig.PAG_MASK))
        {
            sectiontype.put("BN", "ISBN");
        }

        // TI
        if(true)
        {
            sectiontype.put("TI", "Title");
        }
        // CL
        if(selecteddbMask == DatabaseConfig.CPX_MASK ||
           selecteddbMask == DatabaseConfig.CBF_MASK)
        {
             sectiontype.put("CL", "Ei Classification code");
        }
        else if(selecteddbMask == DatabaseConfig.INS_MASK ||
                selecteddbMask == DatabaseConfig.GEO_MASK)
        {
             sectiontype.put("CL", "Classification code");
        }

        //CN
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
           (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("CN", "CODEN");
        }
        //CF
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
           (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("CF","Conference information");
        }
        //CC
        if(selecteddbMask == DatabaseConfig.CPX_MASK)
        {
            sectiontype.put("CC","Conference code");
        }
        //SN
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("SN","ISSN");
        }
        //MH
        if(selecteddbMask == DatabaseConfig.CPX_MASK ||
           selecteddbMask == DatabaseConfig.CBF_MASK)
        {
            sectiontype.put("MH","Ei main heading");
        }
        //PN
        if((selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("PN","Publisher");
        }
        //ST
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("ST","Serial title");
        }

        // Inspec unique fields
        //PM
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
        	(selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CPX_MASK) != DatabaseConfig.CPX_MASK &&
		   (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
             sectiontype.put("PM","Patent number");
        }
        else if(selecteddbMask == DatabaseConfig.UPA_MASK ||
                selecteddbMask == DatabaseConfig.EUP_MASK ||
                selecteddbMask == DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK )
        {
             sectiontype.put("PM","Patent number");
        }

        //PA
        if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
            sectiontype.put("PA","Filing date");
        }
        //PI
        if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
            sectiontype.put("PI","Patent issue date");
        }
        //PU
        if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
            sectiontype.put("PU","Country of application");
        }
        //MI
        if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
            sectiontype.put("MI","Material Identity Number");
        }
        //CV
        if(selecteddbMask == DatabaseConfig.CPX_MASK ||
           selecteddbMask == DatabaseConfig.CBF_MASK)
        {
             sectiontype.put("CV", "Ei controlled term");
        }
        else if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
             sectiontype.put("CV","Inspec controlled term");
        }
        else if(selecteddbMask == DatabaseConfig.NTI_MASK)
        {
             sectiontype.put("CV","NTIS controlled term");
        }
        else if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
        		(selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
                (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK)
        {
            sectiontype.put("CV","Controlled term");
        }

        //DatabaseConfig.NTI_MASK unique fields
        //CT
        if(selecteddbMask == DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("CT","Contract number");
        }
        //CO
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK)
        {
            sectiontype.put("CO","Country of origin");
        }
        //AG
        if(selecteddbMask == DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("AG","Monitoring agency");
        }
        //PD
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
        	(selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
            (selecteddbMask & DatabaseConfig.CPX_MASK) != DatabaseConfig.CPX_MASK &&
            (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
            (selecteddbMask & DatabaseConfig.INS_MASK) != DatabaseConfig.INS_MASK &&
            (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
             sectiontype.put("PD","Publication date");
        }
        //AN
        if(selecteddbMask == DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("AN","NTIS accession number");
        }
        else if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
        		(selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
                (selecteddbMask & DatabaseConfig.CPX_MASK) != DatabaseConfig.CPX_MASK &&
				(selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
                (selecteddbMask & DatabaseConfig.INS_MASK) != DatabaseConfig.INS_MASK &&
                (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("PAM","Application number");
        }

        //RN
        if(selecteddbMask == DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("RN","Report number");
        }
        // Patent fields
        //PRN
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
        	(selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.CPX_MASK) != DatabaseConfig.CPX_MASK &&
		   (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.INS_MASK) != DatabaseConfig.INS_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
             sectiontype.put("PRN","Priority number");
        }
        //PID
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
        	(selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.CPX_MASK) != DatabaseConfig.CPX_MASK &&
		   (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.INS_MASK) != DatabaseConfig.INS_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
             sectiontype.put("PID","IPC code");
        }
        //PUC
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
        	(selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.CPX_MASK) != DatabaseConfig.CPX_MASK &&
		   (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.INS_MASK) != DatabaseConfig.INS_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
             sectiontype.put("PUC","US classification");
        }

        if(selecteddbMask == DatabaseConfig.PAG_MASK)
        {
            sectiontype.put("CV","Subject");
        }

        return sectiontype;
    }

    public static Map getTreatment(int selecteddbMask) {

        Map treattype = new LinkedHashMap();

        // NO-LIMIT
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
             treattype.put("NO-LIMIT", "All treatment types");
        }
        else
        {
             treattype.put("NO-LIMIT", "Treatment type not available");
        }
        //APP
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
             treattype.put("APP", "Applications");
        }
        //BIO
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
             treattype.put("BIO", "Biographical");
        }
        //ECO
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
             treattype.put("ECO", "Economic");
        }
        //EXP
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
             treattype.put("EXP", "Experimental");
        }
        //GEN
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
             treattype.put("GEN", "General review");
        }
        //Cpx fields
        //HIS
        if(selecteddbMask == DatabaseConfig.CPX_MASK)
        {
             treattype.put("HIS", "Historical");
        }
        //LIT
        if(selecteddbMask == DatabaseConfig.CPX_MASK)
        {
             treattype.put("LIT", "Literature review");
        }
        //MAN
        if(selecteddbMask == DatabaseConfig.CPX_MASK)
        {
             treattype.put("MAN", "Management aspects");
        }
        //NUM
        if(selecteddbMask == DatabaseConfig.CPX_MASK)
        {
             treattype.put("NUM", "Numerical");
        }
        //Inspec fields
        //NEW
        if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
             treattype.put("NEW", "New development");
        }
        if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
             treattype.put("PRA", "Practical");
        }
        if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
             treattype.put("PRO", "Product review");
        }
        //THR
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
           (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
             treattype.put("THR", "Theoretical");
        }

        return treattype;
    }

   public static Map getDoctype(int selecteddbMask) {

        Map doctype = new LinkedHashMap();

        // NO-LIMIT
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            doctype.put("NO-LIMIT", "All document types");
        }
        else if(selecteddbMask == DatabaseConfig.UPA_MASK ||
                selecteddbMask == DatabaseConfig.EUP_MASK ||
                selecteddbMask == DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK )
        {
              doctype.put("NO-LIMIT", "All document types");
              doctype.put("NO-LIMIT", "All patents");
        }
        else
        {
             doctype.put("NO-LIMIT", "Document type not available");
        }

        String loc = System.getProperty("loc");

        if (loc != null && loc.equals("china")) {
            if(selecteddbMask == DatabaseConfig.CPX_MASK)
            {
                doctype.put("CORE", "CORE");
            }
        }
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            doctype.put("JA", "Journal article");
        }
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            doctype.put("CA", "Conference article");
        }
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            doctype.put("CP", "Conference proceeding");
        }
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            doctype.put("MC", "Monograph chapter");
        }
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            doctype.put("MR", "Monograph review");
        }
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            doctype.put("RC", "Report chapter");
        }
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            doctype.put("RR", "Report review");
        }
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            doctype.put("DS", "Dissertation");
        }
        if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
            doctype.put("UP", "Unpublished paper");
        }
        if(selecteddbMask == DatabaseConfig.CPX_MASK )
        {
            doctype.put("PA", "Patents (before 1970)");
        }
        else if(selecteddbMask == DatabaseConfig.CBF_MASK )
        {
            doctype.put("PA", "Patents");
        }
        else if(selecteddbMask == DatabaseConfig.INS_MASK)
        {
            doctype.put("PA", "Patents (before 1977)");
        }

        if(selecteddbMask == DatabaseConfig.UPA_MASK)
        {
            doctype.put("UA", "US Applications");
            doctype.put("UG", "US Granted");
        }
        else if(selecteddbMask == DatabaseConfig.EUP_MASK)
        {
            doctype.put("EA", "European Applications");
            doctype.put("EG", "European Granted");
        }
        else if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
				(selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
                (selecteddbMask & DatabaseConfig.CPX_MASK) != DatabaseConfig.CPX_MASK &&
                (selecteddbMask & DatabaseConfig.INS_MASK) != DatabaseConfig.INS_MASK &&
           		(selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
                (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            doctype.put("UA", "US Applications");
            doctype.put("UG", "US Granted");
            doctype.put("EA", "European Applications");
            doctype.put("EG", "European Granted");
        }

        return doctype;
    }


	public static Map getLanguage(int selecteddbMask)
	{
		Map lang = new LinkedHashMap();
		if((selecteddbMask & DatabaseConfig.PAG_MASK) == DatabaseConfig.PAG_MASK)
		{
            lang.put("NO-LIMIT", "Language not available");
		}
		else
		{
            lang.put("NO-LIMIT", "All Languages");
            lang.put("English", "English");
            lang.put("Chinese", "Chinese");
            lang.put("French", "French");
            lang.put("German", "German");
            lang.put("Italian", "Italian");
            lang.put("Japanese", "Japanese");
            lang.put("Russian", "Russian");
            lang.put("Spanish", "Spanish");
		}

		return lang;
	}


   public static String getOption(String selectedWord, int selecteddbMask, String searchType) {

        Map options = new LinkedHashMap();
        StringBuffer outputString = new StringBuffer();

        if (searchType.equals("section")) {
            options = getSection(selecteddbMask);
        }
        else if (searchType.equals("doctype")) {
            options =  getDoctype(selecteddbMask);
        }
        else if (searchType.equals("treattype")) {
            options = getTreatment(selecteddbMask);
        }
        else if (searchType.equals("discipline")) {
            options = getDiscipline(selecteddbMask);
        }
        else if(searchType.equals("language"))
        {
			options = getLanguage(selecteddbMask);
		}
        Iterator optionKeys = options.keySet().iterator();
        while (optionKeys.hasNext()) {
            String name = (String) optionKeys.next();
            String value = (String) options.get(name);
            outputString.append("<option value=\"" + name + "\"");
            if (name.equals(selectedWord)) {
                outputString.append(" selected=\"true\"");
            }

            outputString.append(">" + value + "</option>");
        }

        return outputString.toString();

    }

    private static int calStartYear(int selectedDbMask, String sYear) {

        int dYear = 1973;
        try {

	        if (sYear != null && sYear.length() > 0) {
	            if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK)) {
	                int cpxStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("CST") + 3, sYear.indexOf("CST") + 7));
	                dYear = (dYear > cpxStartYear) ? cpxStartYear : dYear;
	            }
                if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.CBF_MASK) == DatabaseConfig.CBF_MASK)) {
                    int cpxStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("ZST") + 3, sYear.indexOf("ZST") + 7));
                    dYear = (dYear > cpxStartYear) ? cpxStartYear : dYear;
                }
                if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK)) {
	                int insStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("IST") + 3, sYear.indexOf("IST") + 7));
	                dYear = (dYear > insStartYear) ? insStartYear : dYear;
	            }
	            if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.NTI_MASK) == DatabaseConfig.NTI_MASK)) {
	                int ntiStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("NST") + 3, sYear.indexOf("NST") + 7));
	                dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
	            }
	            if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.UPA_MASK) == DatabaseConfig.UPA_MASK)) {
	                int upStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("UST") + 3, sYear.indexOf("UST") + 7));
	                dYear = (dYear > upStartYear) ? upStartYear : dYear;
	            }
	            if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.EUP_MASK) == DatabaseConfig.EUP_MASK)) {
	                int epStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("EST") + 3, sYear.indexOf("EST") + 7));
	                dYear = (dYear > epStartYear) ? epStartYear : dYear;
	            }
	            if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK)) {
	                int geStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("GST") + 3, sYear.indexOf("GST") + 7));
	                dYear = (dYear > geStartYear) ? geStartYear : dYear;
	            }
	            if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.PAG_MASK) == DatabaseConfig.PAG_MASK)) {
	                int paStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("PST") + 3, sYear.indexOf("PST") + 7));
	                dYear = (dYear > paStartYear) ? paStartYear : dYear;
	            }
	        }
		}
		catch(NumberFormatException e)
		{
			System.out.println("Problem with BackOffice \"Default Database:\" Settings! Selected DBMask contains values which user does not have permission for.");
		}
        return dYear;
    }
    
    private static int calEndYear(int selectedDbMask) 
    {
    	if(selectedDbMask == DatabaseConfig.CBF_MASK)
        {
    		return DatabaseConfig.CBF_ENDYEAR;        		
        }
        return SearchForm.ENDYEAR;
	        
    }

    public static String getYear(int selectedDbMask, 
    							String sYear, 
    							String strYear, 
    							String yearType) 
    {
        StringBuffer yearString = null;
        try 
        {
            yearString = new StringBuffer();
            
            int endYear = SearchForm.calEndYear(selectedDbMask);
            int sy = SearchForm.calStartYear(selectedDbMask, strYear);
            int dy = endYear;
            
            if (sYear.length() > 0) 
            {
                dy = SearchForm.calDisplayYear(selectedDbMask, sYear);
            }
           
            else if (yearType.equals("startYear")) 
            {
                dy = SearchForm.calDisplayYear(selectedDbMask, strYear);
            }
            else if (yearType.equals("endYear")) 
            {
                dy = endYear;
            }
            			
            for (int j = sy; j <= endYear; j++) 
			{
				yearString.append("<option value='" + j);

				if (j == dy) 
				{
						yearString.append("' selected='true' >" + j + "</option>");
				}
				else 
				{
						yearString.append("' >" + j + "</option>");
				}
			}	
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return yearString.toString();
    }

    private static int calDisplayYear(int selectedDbMask, String sYear) {
        // 2006 since displayed start year could be a very recent value
        // (i.e. An account could have 2000-2006 as their default range)
        // We set this as high as possible and then compare to
        // all possible values and take minimum
        int dYear = calEndYear(selectedDbMask);

        try {

	       	// same as above - not an else if
	        // choose the least of the three when picking selected start year
	        if (sYear.length() > 4) {
	            if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK)) {
	                int cpxStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("CSY") + 3, sYear.indexOf("CSY") + 7));
	                dYear = (dYear > cpxStartYear) ? cpxStartYear : dYear;
	            }

	            if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK)) {
	                int insStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("ISY") + 3, sYear.indexOf("ISY") + 7));
	                dYear = (dYear > insStartYear) ? insStartYear : dYear;
	            }
	            if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.NTI_MASK) == DatabaseConfig.NTI_MASK)) {
	                int ntiStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("NSY") + 3, sYear.indexOf("NSY") + 7));
	                dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
	            }
	            if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.UPA_MASK) == DatabaseConfig.UPA_MASK)) {
	                int usStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("USY") + 3, sYear.indexOf("USY") + 7));
	                dYear = (dYear > usStartYear) ? usStartYear : dYear;
	            }
	            if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.EUP_MASK) == DatabaseConfig.EUP_MASK)) {
	                int epStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("ESY") + 3, sYear.indexOf("ESY") + 7));
	                dYear = (dYear > epStartYear) ? epStartYear : dYear;
	            }
	            if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK)) {
	                int geStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("GSY") + 3, sYear.indexOf("GSY") + 7));
	                dYear = (dYear > geStartYear) ? geStartYear : dYear;
	            }
	            if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.PAG_MASK) == DatabaseConfig.PAG_MASK)) {
	                int paStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("PSY") + 3, sYear.indexOf("PSY") + 7));
	                dYear = (dYear > paStartYear) ? paStartYear : dYear;
	            }
                if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.CBF_MASK) == DatabaseConfig.CBF_MASK)) {
                    int cbfStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("ZSY") + 3, sYear.indexOf("ZSY") + 7));
                    dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
                }
	        }
	        else if (sYear != null && sYear.length() > 0) {
	            dYear = Integer.parseInt(sYear);
	        }
        }
		catch(NumberFormatException e)
		{
			System.out.println("Problem with BackOffice \"Default Database:\" Settings! Selected DBMask contains values which user does not have permission for.");
		}

        return dYear;
    }


    private static String getOpenLookupRow(String sessionID,int selecteddbMask, String field, String imgname, int idx)
    {
        StringBuffer outputString = new StringBuffer();
        outputString.append("<tr><td valign='top'>");
        outputString.append("<a CLASS='MedBlueLink' href='#' onclick='OpenLookup(\"").append(sessionID).append("\",\"").append(selecteddbMask).append("\",\"").append(field).append("\",\"lookuplink").append(idx).append("\");void(\"\")'>");
        outputString.append("<img name='lookuplink").append(idx).append("' src='/engresources/images/").append(imgname).append("' height='15' border='0'/></a></td></tr>");
        outputString.append(SearchForm.LOOKUP_SPACER_ROW);

        return outputString.toString();
    }

    private static final String LOOKUP_SPACER_ROW = "<tr><td height=\"4\"><img src=\"/engresources/images/s.gif\" height=\"4\"></td></tr>";


    public static String getQuickLookupLink(String sessionID,int selecteddbMask)
    {

        StringBuffer outputString = new StringBuffer(SearchForm.LOOKUP_SPACER_ROW);

        //AU
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK)
        {
			if((selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
			   (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK)
			{
				outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AUS","ath.gif",1));
			}
			else if(selecteddbMask == DatabaseConfig.UPA_MASK ||
					selecteddbMask == DatabaseConfig.EUP_MASK ||
					selecteddbMask == DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK )
			{
				outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AUS","inv.gif",1));
			}
			else
			{
				outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AUS","auinv.gif",1));
			}
		}
		else
		{
			outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AUS","checking.gif",1));
		}

        //AF ---  only cpx,ins,ntis
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK)
        {
			if((selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
			   (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK)
			{
				outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AF","af.gif",2));
			}
			else if(selecteddbMask == DatabaseConfig.UPA_MASK ||
					selecteddbMask == DatabaseConfig.EUP_MASK ||
					selecteddbMask == DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK )
			{
				outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AF","asg.gif",2));
			}
			else
			{
				outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AF","afas.gif",2));
			}
		}
		else
		{
			outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AF","checking.gif",2));
		}


        //CV
        if((selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK)
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"CVS","ct.gif",3));
        }
        else
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"CVS","checking.gif",3));
        }
        //ST
        if ((selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
            (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
            (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
            (selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK)
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"ST","st.gif",4));
        }
        else
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"ST","checking.gif",4));
        }
        //PB
        if((selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK)
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"PN","pb.gif",5));
        }
        else
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"PN","checking.gif",5));
        }

        return outputString.toString();
    }

    public static String getExpertLookupLink(String sessionID,int selecteddbMask) {

        StringBuffer outputString = new StringBuffer(SearchForm.LOOKUP_SPACER_ROW);

        //AU
        if(selecteddbMask != DatabaseConfig.PAG_MASK)
        {
			if((selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
			   (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK)
			{
				outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AUS","ath.gif",1));
			}
			else if(selecteddbMask == DatabaseConfig.UPA_MASK ||
					selecteddbMask == DatabaseConfig.EUP_MASK ||
					selecteddbMask == DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK )
			{
				outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AUS","inv.gif",1));
			}
			else
			{
				outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AUS","auinv.gif",1));
			}
		}
		else
		{
			outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AUS","checking.gif",1));
		}

        //AF ---  only cpx,ins,ntis
        if(selecteddbMask != DatabaseConfig.PAG_MASK)
        {
			if((selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
			   (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK)
			{
				outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AF","af.gif",2));
			}
			else if(selecteddbMask == DatabaseConfig.UPA_MASK ||
					selecteddbMask == DatabaseConfig.EUP_MASK ||
					selecteddbMask == DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK )
			{
				outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AF","asg.gif",2));
			}
			else
			{
				outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AF","afas.gif",2));
			}
		}
		else
		{
    		outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"AF","checking.gif",2));
		}

        //CLS
        if (((selecteddbMask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK) ||
            ((selecteddbMask & DatabaseConfig.CBF_MASK) == DatabaseConfig.CBF_MASK) ||
            ((selecteddbMask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) ||
            ((selecteddbMask & DatabaseConfig.NTI_MASK) == DatabaseConfig.NTI_MASK) ||
            ((selecteddbMask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK))
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"CVS","ct.gif",3));
        }
        else
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"CVS","checking.gif",3));
        }

        //LA
        if (((selecteddbMask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK) ||
            ((selecteddbMask & DatabaseConfig.CBF_MASK) == DatabaseConfig.CBF_MASK) ||
            ((selecteddbMask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) ||
            ((selecteddbMask & DatabaseConfig.NTI_MASK) == DatabaseConfig.NTI_MASK) ||
            ((selecteddbMask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK))
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"LA","lng.gif",4));
        }
        else
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"LA","checking.gif",4));
        }


        //ST
        if (((selecteddbMask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK) ||
            ((selecteddbMask & DatabaseConfig.CBF_MASK) == DatabaseConfig.CBF_MASK) ||
            ((selecteddbMask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) ||
            ((selecteddbMask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK))
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"ST","st.gif",5));
        }
        else
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"ST","checking.gif",5));
        }


        //DT
        if (((selecteddbMask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK) ||
        	((selecteddbMask & DatabaseConfig.CBF_MASK) == DatabaseConfig.CBF_MASK) ||
            ((selecteddbMask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) ||
            ((selecteddbMask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK))
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"DT","dt.gif",6));
        }
        else
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"DT","checking.gif",6));
        }

        //PN
        if (((selecteddbMask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK) ||
            ((selecteddbMask & DatabaseConfig.CBF_MASK) == DatabaseConfig.CBF_MASK) ||
            ((selecteddbMask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK))
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"PN","pb.gif",7));
        }
        else
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"PN","checking.gif",7));
        }
        //TR
        if (((selecteddbMask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK) ||
            ((selecteddbMask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK))
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"TR","tr.gif",8));
        }
        else
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"TR","checking.gif",8));
        }

        //DSC
        if ((selecteddbMask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK)
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"DI","dsc.gif",9));
        }
        else
        {
        	outputString.append(getOpenLookupRow(sessionID,selecteddbMask,"DI","checking.gif",9));
        }

        return outputString.toString();
    }

   public static String getLookupLink(String sessionID, int selecteddbMask, String lookupType) {

        if (lookupType.equalsIgnoreCase("expert"))
        {
        	return(getExpertLookupLink(sessionID,selecteddbMask));
        }
        else
        {
        	return (getQuickLookupLink(sessionID,selecteddbMask));
        }
    }

}