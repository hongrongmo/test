package org.ei.domain;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SearchForm {

    public static final int ENDYEAR = 2008;


    public static Map getDiscipline(int selecteddbMask) {
        Map disctype = new LinkedHashMap();
        boolean isDiscipline = false;

        if(selecteddbMask == DatabaseConfig.INS_MASK  ||
                selecteddbMask == DatabaseConfig.IBS_MASK )
        {
            isDiscipline = true;
            disctype.put("NO-LIMIT", "All disciplines");
        }
        else
        {
            disctype.put("NO-LIMIT", "Discipline type not available");
        }

        if(isDiscipline)
        {
             disctype.put("A", "Physics");
             disctype.put("B", "Electrical/Electronic engineering");
             disctype.put("C", "Computers/Control engineering");
             disctype.put("D", "Information technology");
             disctype.put("E", "Manufacturing and production engineering");
        }

        return disctype;

    }

    public static Map getSection(int selecteddbMask) {

        Map sectiontype = new LinkedHashMap();

        // All fields
        // do not show all fields if Database is only PAG or CBF
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
        if((selecteddbMask & DatabaseConfig.CBN_MASK)!= DatabaseConfig.CBN_MASK)
        {
			if((selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
			   (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
			   (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK)
			{
				 sectiontype.put("AU", "Author");
			}
			else if(selecteddbMask == DatabaseConfig.UPA_MASK ||
					selecteddbMask == DatabaseConfig.EUP_MASK ||
					selecteddbMask == DatabaseConfig.EPT_MASK ||
					selecteddbMask == DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK ||
					selecteddbMask == DatabaseConfig.EUP_MASK + DatabaseConfig.EPT_MASK ||
					selecteddbMask == DatabaseConfig.EPT_MASK + DatabaseConfig.UPA_MASK ||
					selecteddbMask == DatabaseConfig.EPT_MASK + DatabaseConfig.UPA_MASK + DatabaseConfig.EUP_MASK)
			{
				 sectiontype.put("AU", "Inventor");
			}
			else
			{
				 sectiontype.put("AU", "Author/Inventor");
			}
		}

        //AF ---  only cpx,ins,ntis
        if((selecteddbMask & DatabaseConfig.CBN_MASK)!= DatabaseConfig.CBN_MASK &&
           (selecteddbMask & DatabaseConfig.PAG_MASK)!= DatabaseConfig.PAG_MASK &&
           (selecteddbMask & DatabaseConfig.IBS_MASK)!= DatabaseConfig.IBS_MASK)
        {

			if((selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
			   (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
			   (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK)
			{
				sectiontype.put("AF", "Author affiliation");
			}
			else if(selecteddbMask == DatabaseConfig.UPA_MASK ||
				    selecteddbMask == DatabaseConfig.EUP_MASK ||
				    selecteddbMask == DatabaseConfig.EPT_MASK ||
				    selecteddbMask == DatabaseConfig.EPT_MASK + DatabaseConfig.UPA_MASK ||
				    selecteddbMask == DatabaseConfig.EUP_MASK + DatabaseConfig.EPT_MASK ||
				    selecteddbMask == DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK ||
				    selecteddbMask == DatabaseConfig.EPT_MASK + DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK)
			{
				sectiontype.put("AF", "Assignee");
			}
			else
			{
				sectiontype.put("AF", "Author affiliation/Assignee");
			}
		}

        if((selecteddbMask == DatabaseConfig.PAG_MASK))
        {
            sectiontype.put("BN", "ISBN");
        }

        // TI
        if(selecteddbMask == DatabaseConfig.EPT_MASK)
        {
			sectiontype.put("TI", "Patent title");
		}
		else
        {
            sectiontype.put("TI", "Title");
        }

        // CL
        if(selecteddbMask == DatabaseConfig.CPX_MASK ||
           selecteddbMask == DatabaseConfig.CBF_MASK ||
           selecteddbMask == DatabaseConfig.CPX_MASK + DatabaseConfig.C84_MASK)
        {
             sectiontype.put("CL", "Ei Classification code");
        }
        else if(selecteddbMask == DatabaseConfig.INS_MASK ||
                selecteddbMask == DatabaseConfig.IBS_MASK ||
        		selecteddbMask == DatabaseConfig.GEO_MASK ||
        	    selecteddbMask == DatabaseConfig.INS_MASK + DatabaseConfig.IBF_MASK ||
        	    selecteddbMask == DatabaseConfig.INS_MASK + DatabaseConfig.GEO_MASK ||
        	    selecteddbMask == DatabaseConfig.INS_MASK + DatabaseConfig.IBF_MASK + DatabaseConfig.GEO_MASK)

        {
             sectiontype.put("CL", "Classification code");
        }


        //CN
        if((selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
		   (selecteddbMask & DatabaseConfig.CRC_MASK) != DatabaseConfig.CRC_MASK &&
		   (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
           (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
		   (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
		   (selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
           (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK &&
           (selecteddbMask & DatabaseConfig.REF_MASK) != DatabaseConfig.REF_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.UPT_MASK) != DatabaseConfig.UPT_MASK &&
           (selecteddbMask & DatabaseConfig.USPTO_MASK) != DatabaseConfig.USPTO_MASK &&
           (selecteddbMask & DatabaseConfig.IBS_MASK)!= DatabaseConfig.IBS_MASK)
        {
            sectiontype.put("CN", "CODEN");
        }

        //CF
        if((selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
		   (selecteddbMask & DatabaseConfig.CRC_MASK) != DatabaseConfig.CRC_MASK &&
		   (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
		   (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
		   (selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK &&
		   (selecteddbMask & DatabaseConfig.REF_MASK) != DatabaseConfig.REF_MASK &&
		   (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
		   (selecteddbMask & DatabaseConfig.UPT_MASK) != DatabaseConfig.UPT_MASK &&
           (selecteddbMask & DatabaseConfig.USPTO_MASK) != DatabaseConfig.USPTO_MASK)
        {
            sectiontype.put("CF","Conference information");
        }

        //CC
        if(selecteddbMask == DatabaseConfig.CPX_MASK ||
           selecteddbMask == DatabaseConfig.CPX_MASK + DatabaseConfig.C84_MASK)
        {
            sectiontype.put("CC","Conference code");
        }

        //SN
        if((selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
		   (selecteddbMask & DatabaseConfig.CRC_MASK) != DatabaseConfig.CRC_MASK &&
		   (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
		   (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
		   (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
		   (selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK &&
		   (selecteddbMask & DatabaseConfig.REF_MASK) != DatabaseConfig.REF_MASK &&
		   (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
		   (selecteddbMask & DatabaseConfig.UPT_MASK) != DatabaseConfig.UPT_MASK &&
           (selecteddbMask & DatabaseConfig.USPTO_MASK) != DatabaseConfig.USPTO_MASK &&
           (selecteddbMask & DatabaseConfig.IBS_MASK)!= DatabaseConfig.IBS_MASK)
        {
            sectiontype.put("SN","ISSN");
        }

        //MH
        if(selecteddbMask == DatabaseConfig.CPX_MASK ||
           selecteddbMask == DatabaseConfig.CBF_MASK ||
           selecteddbMask == DatabaseConfig.CPX_MASK + DatabaseConfig.C84_MASK)
        {
            sectiontype.put("MH","Ei main heading");
        }

        //PN
        if((selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
           (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
           (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK)
        {
            sectiontype.put("PN","Publisher");
        }

        //ST
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("ST","Serial title");
        }

        //PM
       if((selecteddbMask & DatabaseConfig.CPX_MASK) != DatabaseConfig.CPX_MASK &&
          (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
          (selecteddbMask & DatabaseConfig.C84_MASK) != DatabaseConfig.C84_MASK &&
		  (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
	   	  (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
	   	  (selecteddbMask & DatabaseConfig.CRC_MASK) != DatabaseConfig.CRC_MASK &&
	   	  (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
	   	  (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
	   	  (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
	   	  (selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
	   	  (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK &&
          (selecteddbMask & DatabaseConfig.USPTO_MASK) != DatabaseConfig.USPTO_MASK)
        {
             sectiontype.put("PM","Patent number");
        }

        //PA
        if(selecteddbMask == DatabaseConfig.INS_MASK ||
           selecteddbMask == DatabaseConfig.INS_MASK + DatabaseConfig.IBF_MASK)
        {
            sectiontype.put("PA","Filing date");
        }

        //PI
        if(selecteddbMask == DatabaseConfig.INS_MASK ||
           selecteddbMask == DatabaseConfig.INS_MASK + DatabaseConfig.IBF_MASK)
        {
            sectiontype.put("PI","Patent issue date");
        }

        //PU
        if(selecteddbMask == DatabaseConfig.INS_MASK ||
           selecteddbMask == DatabaseConfig.INS_MASK + DatabaseConfig.IBF_MASK)
        {
            sectiontype.put("PU","Country of application");
        }

        //MI
        if(selecteddbMask == DatabaseConfig.INS_MASK ||
           selecteddbMask == DatabaseConfig.INS_MASK + DatabaseConfig.IBF_MASK)
        {
            sectiontype.put("MI","Material Identity Number");
        }

        //CV
        if(selecteddbMask == DatabaseConfig.CPX_MASK ||
           selecteddbMask == DatabaseConfig.CBF_MASK ||
           selecteddbMask == DatabaseConfig.C84_MASK + DatabaseConfig.CPX_MASK)
        {
             sectiontype.put("CV", "Ei controlled term");
        }
        else if(selecteddbMask == DatabaseConfig.INS_MASK ||
                selecteddbMask == DatabaseConfig.IBS_MASK ||
        	    selecteddbMask == DatabaseConfig.INS_MASK + DatabaseConfig.IBF_MASK)
        {
             sectiontype.put("CV","Inspec controlled term");
        }
        else if(selecteddbMask == DatabaseConfig.NTI_MASK)
        {
             sectiontype.put("CV","NTIS controlled term");
        }
        else if(selecteddbMask == DatabaseConfig.PAG_MASK)
        {
			 sectiontype.put("CV","Subject");
		}
        else if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
        		(selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
                (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
                (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
                (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK)
        {
            sectiontype.put("CV","Controlled term");
        }

        //DatabaseConfig.NTI_MASK unique fields
        //CT
        if(selecteddbMask == DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("CT","Contract number");
        }

        //CO or PC
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
           (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
           (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK &&
           (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
           (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
           (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
           (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK)
        {
            sectiontype.put("CO","Country of origin");
        }

        //AG
        if(selecteddbMask == DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("AG","Monitoring agency");
        }

        //PD
        if(selecteddbMask == DatabaseConfig.UPA_MASK ||
           selecteddbMask == DatabaseConfig.EUP_MASK ||
           selecteddbMask == DatabaseConfig.UPA_MASK + DatabaseConfig.EUP_MASK)
        {
             sectiontype.put("PD","Publication date");
        }
        //else if(selecteddbMask == DatabaseConfig.PCH_MASK)
		//{
		//	sectiontype.put("PD","Patent info");
  		//}

        //AN
        if(selecteddbMask == DatabaseConfig.NTI_MASK)
        {
            sectiontype.put("AN","NTIS accession number");
        }

        //PAM
        if(selecteddbMask == DatabaseConfig.UPA_MASK ||
	       selecteddbMask == DatabaseConfig.EUP_MASK ||
           selecteddbMask == DatabaseConfig.UPA_MASK + DatabaseConfig.EUP_MASK)
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
        if(selecteddbMask == DatabaseConfig.UPA_MASK ||
	       selecteddbMask == DatabaseConfig.EUP_MASK ||
           selecteddbMask == DatabaseConfig.UPA_MASK + DatabaseConfig.EUP_MASK)
        {
             sectiontype.put("PRN","Priority number");
        }

        //PID
         if(selecteddbMask == DatabaseConfig.UPA_MASK ||
		    selecteddbMask == DatabaseConfig.EUP_MASK ||
            selecteddbMask == DatabaseConfig.UPA_MASK + DatabaseConfig.EUP_MASK ||
            selecteddbMask == DatabaseConfig.EPT_MASK ||
            selecteddbMask == DatabaseConfig.EPT_MASK + DatabaseConfig.UPA_MASK + DatabaseConfig.EUP_MASK ||
            selecteddbMask == DatabaseConfig.EPT_MASK + DatabaseConfig.UPA_MASK ||
            selecteddbMask == DatabaseConfig.EPT_MASK + DatabaseConfig.EUP_MASK )
        {
             sectiontype.put("PID","Int. patent classification");
        }

        //PUC
		if(selecteddbMask == DatabaseConfig.EUP_MASK)
		{
			sectiontype.put("PUC","ECLA code");
		}
		else if(selecteddbMask == DatabaseConfig.UPA_MASK)
		{
			sectiontype.put("PUC","US Classification");
		}

		//CR
  		if(selecteddbMask == DatabaseConfig.ELT_MASK ||
		   selecteddbMask == DatabaseConfig.EPT_MASK ||
		   selecteddbMask == DatabaseConfig.EPT_MASK + DatabaseConfig.ELT_MASK )
		{
			sectiontype.put("CR","CAS registry number");
  		}

		//PC
		if(selecteddbMask == DatabaseConfig.EPT_MASK)
		{
			sectiontype.put("PC","Patent country");
  		}

  		//IP
		if(selecteddbMask == DatabaseConfig.EPT_MASK)
		{
			sectiontype.put("PID","Int. patent classification");
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
           (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
		   (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
		   (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK &&
           (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK)
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
           (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
		   (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
		   (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK &&
           (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK)
        {
             treattype.put("APP", "Applications");
        }

        //BIO
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
		   (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
		   (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK &&
           (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK)
        {
             treattype.put("BIO", "Biographical");
        }

        //ECO
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
		   (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
		   (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK &&
           (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK)
        {
             treattype.put("ECO", "Economic");
        }

        //EXP
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
		   (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
		   (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK &&
           (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK)
        {
             treattype.put("EXP", "Experimental");
        }

        //GEN
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
		   (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
		   (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK &&
           (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK)
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
           (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
		   (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
		   (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK &&
           (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK)
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
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK)
        {
            doctype.put("NO-LIMIT", "All document types");
        }
        else if(selecteddbMask == DatabaseConfig.UPA_MASK ||
                selecteddbMask == DatabaseConfig.EUP_MASK ||
                selecteddbMask == DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK )
        {
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
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
           //(selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
		   (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK )
        {
            doctype.put("JA", "Journal article");
        }


        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
           (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
		   (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
           (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK)
        {
            doctype.put("CA", "Conference article");
        }
        else if (selecteddbMask == DatabaseConfig.EPT_MASK ||
        		 selecteddbMask == DatabaseConfig.ELT_MASK )
        {
			doctype.put("CA", "Conference");
		}

        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
           (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
		   (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
           (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
           (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK)
        {
            doctype.put("CP", "Conference proceeding");
        }

        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
           (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK)
        {
            doctype.put("MC", "Monograph chapter");
        }

        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
           (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
		   (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
           (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK)
        {
            doctype.put("MR", "Monograph review");
        }

		//RC
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
           (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK)
        {
            doctype.put("RC", "Report chapter");
        }

		//RR
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
           (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK)
        {
            doctype.put("RR", "Report review");
        }

		//DS
        if((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK &&
		   (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK &&
           (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK &&
           (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK &&
           (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
           (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
           (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
		   (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
		   (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
		   (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK)
        {
            doctype.put("DS", "Dissertation");
        }

        if(selecteddbMask == DatabaseConfig.INS_MASK ||
           selecteddbMask == DatabaseConfig.IBS_MASK)
        {
            doctype.put("UP", "Unpublished paper");
        }

        if(selecteddbMask == DatabaseConfig.CPX_MASK )
        {
            doctype.put("PA", "Patents (before 1970)");
        }
        else if(selecteddbMask == DatabaseConfig.CBF_MASK ||
        		selecteddbMask == DatabaseConfig.PCH_MASK ||
        		selecteddbMask == DatabaseConfig.IBS_MASK)
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
                (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK &&
           		(selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK &&
                (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK &&
                (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
				(selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK &&
				(selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK &&
				(selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
           		(selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK)
        {
            doctype.put("UA", "US Applications");
            doctype.put("UG", "US Granted");
            doctype.put("EA", "European Applications");
            doctype.put("EG", "European Granted");
        }

        if(selecteddbMask == DatabaseConfig.PCH_MASK)
        {
			doctype.put("(CA or CP)","Conferences");
			doctype.put("PA","Patents");
			doctype.put("MC or MR or RC or RR or DS or UP", "Other documents");
		}

		if(selecteddbMask == DatabaseConfig.CBN_MASK)
        {
			doctype.put("Journal","Journal article");
			doctype.put("Advertizement","Advertisement");
			doctype.put("Book","Book");
			doctype.put("Directory","Directory");
			doctype.put("Company","Company Report");
			doctype.put("Stockbroker","Stockbroker Report");
			doctype.put("Market","Market Research Report");
			doctype.put("Press","Press Release");
		}

		if(selecteddbMask == DatabaseConfig.ELT_MASK)
        {
			doctype.put("AB","Abstract");
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
            if((selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK &&
               (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK &&
               (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK)
            {
            	lang.put("Italian", "Italian");
			}
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
        while (optionKeys.hasNext())
        {
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
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK)) {
                  int insStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("IST") + 3, sYear.indexOf("IST") + 7));
                  dYear = (dYear > insStartYear) ? insStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.IBS_MASK) == DatabaseConfig.IBS_MASK)) {
                int insStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("FST") + 3, sYear.indexOf("FST") + 7));
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
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.USPTO_MASK) == DatabaseConfig.USPTO_MASK)) {
                  int upStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("SST") + 3, sYear.indexOf("SST") + 7));
                  dYear = (dYear > upStartYear) ? upStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.REF_MASK) == DatabaseConfig.REF_MASK)) {
                  int upStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("DST") + 3, sYear.indexOf("DST") + 7));
                  dYear = (dYear > upStartYear) ? upStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.UPT_MASK) == DatabaseConfig.UPT_MASK)) {
                  int upStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("TST") + 3, sYear.indexOf("TST") + 7));
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
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.PCH_MASK) == DatabaseConfig.PCH_MASK)) {
                  int paStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("AST") + 3, sYear.indexOf("AST") + 7));
                  dYear = (dYear > paStartYear) ? paStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.CHM_MASK) == DatabaseConfig.CHM_MASK)) {
                  int paStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("HST") + 3, sYear.indexOf("HST") + 7));
                  dYear = (dYear > paStartYear) ? paStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.CBN_MASK) == DatabaseConfig.CBN_MASK)) {
                  int paStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("BST") + 3, sYear.indexOf("BST") + 7));
                  dYear = (dYear > paStartYear) ? paStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.ELT_MASK) == DatabaseConfig.ELT_MASK)) {
                  int paStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("LST") + 3, sYear.indexOf("LST") + 7));
                  dYear = (dYear > paStartYear) ? paStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.EPT_MASK) == DatabaseConfig.EPT_MASK)) {
                  int paStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("MST") + 3, sYear.indexOf("MST") + 7));
                  dYear = (dYear > paStartYear) ? paStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.CRC_MASK) == DatabaseConfig.CRC_MASK)) {
                  int paStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("RST") + 3, sYear.indexOf("RST") + 7));
                  dYear = (dYear > paStartYear) ? paStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.CBF_MASK) == DatabaseConfig.CBF_MASK)) {
                  int paStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("YST") + 3, sYear.indexOf("YST") + 7));
                  dYear = (dYear > paStartYear) ? paStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.IBS_MASK) == DatabaseConfig.IBS_MASK)) {
                  int ibsStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("FST") + 3, sYear.indexOf("FST") + 7));
                  dYear = (dYear > ibsStartYear) ? ibsStartYear : dYear;
              }
          }
    }
		catch(NumberFormatException e)
		{
			System.out.println("Problem with BackOffice \"Default Database:\" Settings! Selected DBMask contains values which user does not have permission for.");
			e.printStackTrace();
		}
        return dYear;
    }

    private static int calEndYear(int selectedDbMask)
    {
      if(selectedDbMask == DatabaseConfig.CBF_MASK)
      {
        return DatabaseConfig.CBF_ENDYEAR;
      }
      if(selectedDbMask == DatabaseConfig.IBS_MASK)
      {
        return DatabaseConfig.IBS_ENDYEAR;
      }
      return SearchForm.ENDYEAR;
    }

    public static String getYear(int selectedDbMask,
    							String sYear,
    							String strYear,
    							String yearType)
    {

        //System.out.println("\n\nselectedDbMask "+selectedDbMask+" sYear "+sYear+" strYear "+strYear+" yearType "+yearType);
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
                int cbfStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("YSY") + 3, sYear.indexOf("YSY") + 7));
                dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.IBS_MASK) == DatabaseConfig.IBS_MASK)) {
                int ibsStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("FSY") + 3, sYear.indexOf("FSY") + 7));
                dYear = (dYear > ibsStartYear) ? ibsStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.PCH_MASK) == DatabaseConfig.PCH_MASK)) {
                int cbfStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("ASY") + 3, sYear.indexOf("ASY") + 7));
                dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.CHM_MASK) == DatabaseConfig.CHM_MASK)) {
                int cbfStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("HSY") + 3, sYear.indexOf("HSY") + 7));
                dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.CBN_MASK) == DatabaseConfig.CBN_MASK)) {
                int cbfStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("BSY") + 3, sYear.indexOf("BSY") + 7));
                dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.ELT_MASK) == DatabaseConfig.ELT_MASK)) {
                int cbfStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("LSY") + 3, sYear.indexOf("LSY") + 7));
                dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.EPT_MASK) == DatabaseConfig.EPT_MASK)) {
                int cbfStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("MSY") + 3, sYear.indexOf("MSY") + 7));
                dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.UPT_MASK) == DatabaseConfig.UPT_MASK)) {
                int cbfStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("TSY") + 3, sYear.indexOf("TSY") + 7));
                dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.CRC_MASK) == DatabaseConfig.CRC_MASK)) {
                int cbfStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("RSY") + 3, sYear.indexOf("RSY") + 7));
                dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
              }
              if (selectedDbMask != 0 && ((selectedDbMask & DatabaseConfig.REF_MASK) == DatabaseConfig.REF_MASK)) {
                int cbfStartYear = Integer.parseInt(sYear.substring(sYear.indexOf("DSY") + 3, sYear.indexOf("DSY") + 7));
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


}