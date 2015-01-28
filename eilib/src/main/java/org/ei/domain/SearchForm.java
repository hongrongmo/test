package org.ei.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.ei.gui.ListBoxOption;

public class SearchForm {
    private final static Logger log4j = Logger.getLogger(SearchForm.class);

	public static final int ENDYEAR = 2015;

    /**
     * This method creates a string that will contain the start year
     * for each database which the user subscribes to and also a
     * selected start year for each database. The start yeaar will be
     * the furthest back a search can go and the selected start year
     * will appear as the default in all search forms.
     *
     * i.e. CSY1960CST1884ISY1970IST1969NSY1980NST1899
     *
     * @return String which contains keys followed by year values for each database the user subscribes to
     */
    public static String getClientStartYears(String carstr, String[] cartridges)
    {

        DatabaseConfig dbconfig = DatabaseConfig.getInstance();

        // simulate backoffice selected start year settings
        // carstr = carstr + "CSY1960;ISY1970;NSY1980";

        StringBuffer buf = new StringBuffer();

        int mask = dbconfig.getScrubbedMask(cartridges);
        Database[] d = dbconfig.getDatabases(mask);
        for(int y = 0; y < d.length; y++)
        {
            if(d[y] != null)
            {
                //log.debug(" LOG ID ==>" + d[y].getID());
                //System.out.println("databaseID "+d[y].getID());
                String firstChar = d[y].getSingleCharName().toUpperCase();

                String skey = firstChar.concat("SY");
                int dbStartYear = dbconfig.getStartYear(cartridges, d[y].getMask());

                buf.append(skey);
                if(carstr.indexOf(skey) > -1)
                {
                    buf.append(carstr.substring(carstr.indexOf(skey)+3, carstr.indexOf(skey) + 7));
                }
                else
                {
                    buf.append(dbStartYear);
                }
                skey = firstChar.concat("ST");
                buf.append(skey).append(dbStartYear);
            }
        } // for

        //log.debug(" LOG startYear ==>" + buf.toString());
        return buf.toString();
    }


    /**
     * Get the option list for the value in searchType ("section", "doctype", etc).
     *
     * @param selectedWord
     * @param selecteddbMask
     * @param searchType
     * @return
     */
    public static List<ListBoxOption> getOptions(String selectedWord, int selecteddbMask, String searchType) {

        List<ListBoxOption> options = null;
        StringBuffer outputString = new StringBuffer();

        if (searchType.equals("section")) {
            options = getSection(selecteddbMask, selectedWord);
        } else if (searchType.equals("doctype")) {
            options = getDoctype(selecteddbMask, selectedWord);
        } else if (searchType.equals("treattype")) {
            options = getTreatment(selecteddbMask, selectedWord);
        } else if (searchType.equals("discipline")) {
            options = getDiscipline(selecteddbMask, selectedWord);
        } else if (searchType.equals("language")) {
            options = getLanguage(selecteddbMask, selectedWord);
        }
        return options;
    }

    /**
     * Get the option list for the value in searchType ("section", "doctype", etc).
     *
     * @param selectedWord
     * @param selecteddbMask
     * @param searchType
     * @return
     */
    public static String getOption(String selectedWord, int selecteddbMask,
            String searchType) {

        List<ListBoxOption> options = null;
        StringBuffer outputString = new StringBuffer();

        if (searchType.equals("section")) {
            options = getSection(selecteddbMask, selectedWord);
        } else if (searchType.equals("doctype")) {
            options = getDoctype(selecteddbMask, selectedWord);
        } else if (searchType.equals("treattype")) {
            options = getTreatment(selecteddbMask, selectedWord);
        } else if (searchType.equals("discipline")) {
            options = getDiscipline(selecteddbMask, selectedWord);
        } else if (searchType.equals("language")) {
            options = getLanguage(selecteddbMask, selectedWord);
        }
        for (ListBoxOption option : options) {
            outputString.append(option.render());
        }
        return outputString.toString();

    }

	/**
	 * Get the list of Discipline options
	 *
	 * @param selecteddbMask
	 * @return
	 */
	public static List<ListBoxOption> getDiscipline(int selecteddbMask, String selectedWord) {
		List<ListBoxOption> disctype = new Vector<ListBoxOption>();

		if (selecteddbMask == DatabaseConfig.INS_MASK
				|| selecteddbMask == DatabaseConfig.IBS_MASK) {
			disctype.add(new ListBoxOption(selectedWord, "NO-LIMIT", "All disciplines"));
			disctype.add(new ListBoxOption(selectedWord, "A", "Physics"));
			disctype.add(new ListBoxOption(selectedWord, "B", "Electrical/Electronic engineering"));
			disctype.add(new ListBoxOption(selectedWord, "C", "Computers/Control engineering"));
			disctype.add(new ListBoxOption(selectedWord, "D", "Information technology"));
			disctype.add(new ListBoxOption(selectedWord, "E", "Manufacturing and production engineering"));
		} else {
			disctype.add(new ListBoxOption(selectedWord, "NO-LIMIT", "Discipline type not available"));
		}

		return disctype;

	}

	/**
	 * Get the list of Section options
	 *
	 * @param selecteddbMask
	 * @return
	 */
	public static List<ListBoxOption> getSection(int selecteddbMask, String selectedWord) {

		List<ListBoxOption> sectiontype = new Vector<ListBoxOption>();

		// All fields
		// do not show all fields if Database is only PAG or CBF
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "NO-LIMIT", "All fields"));
		}

		// KY
		// do not show KY as S/T/A if DB is only PAG
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "KY", "Subject/Title/Abstract"));
		} else {
			sectiontype.add(new ListBoxOption(selectedWord, "KY", "Keyword"));
		}

		// AB
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "AB", "Abstract"));
		}

		// AU
		if ((selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK) {
			if ((selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
					&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
					&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK) {
				sectiontype.add(new ListBoxOption(selectedWord, "AU", "Author"));
			} else if (selecteddbMask == DatabaseConfig.UPA_MASK
					|| selecteddbMask == DatabaseConfig.EUP_MASK
					|| selecteddbMask == DatabaseConfig.EPT_MASK
					|| selecteddbMask == DatabaseConfig.EUP_MASK
							+ DatabaseConfig.UPA_MASK
					|| selecteddbMask == DatabaseConfig.EUP_MASK
							+ DatabaseConfig.EPT_MASK
					|| selecteddbMask == DatabaseConfig.EPT_MASK
							+ DatabaseConfig.UPA_MASK
					|| selecteddbMask == DatabaseConfig.EPT_MASK
							+ DatabaseConfig.UPA_MASK + DatabaseConfig.EUP_MASK) {
				sectiontype.add(new ListBoxOption(selectedWord, "AU", "Inventor"));
			} else {
				sectiontype.add(new ListBoxOption(selectedWord, "AU", "Author/Inventor"));
			}
		}

		// AF --- only cpx,ins,ntis
		if ((selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK) {
			if ((selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
					&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
					&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK) {
				sectiontype.add(new ListBoxOption(selectedWord, "AF", "Author affiliation"));
			} else if (selecteddbMask == DatabaseConfig.UPA_MASK
					|| selecteddbMask == DatabaseConfig.EUP_MASK
					|| selecteddbMask == DatabaseConfig.EPT_MASK
					|| selecteddbMask == DatabaseConfig.EPT_MASK
							+ DatabaseConfig.UPA_MASK
					|| selecteddbMask == DatabaseConfig.EUP_MASK
							+ DatabaseConfig.EPT_MASK
					|| selecteddbMask == DatabaseConfig.EUP_MASK
							+ DatabaseConfig.UPA_MASK
					|| selecteddbMask == DatabaseConfig.EPT_MASK
							+ DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK) {
				sectiontype.add(new ListBoxOption(selectedWord, "AF", "Assignee"));
			} else {
				sectiontype.add(new ListBoxOption(selectedWord, "AF", "Author affiliation/Assignee"));
			}
		}

		// BN - FOR BOOKS - Added later on for GeoRef so as not mess up ordering
		// for fields in Books
		if ((selecteddbMask == DatabaseConfig.PAG_MASK)) {
			sectiontype.add(new ListBoxOption(selectedWord, "BN", "ISBN"));
		}

		// TI
		if (selecteddbMask == DatabaseConfig.EPT_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "TI", "Patent title"));
		} else {
			sectiontype.add(new ListBoxOption(selectedWord, "TI", "Title"));
		}

		// CL
		if (selecteddbMask == DatabaseConfig.CPX_MASK
				|| selecteddbMask == DatabaseConfig.CBF_MASK
				|| selecteddbMask == DatabaseConfig.CPX_MASK
						+ DatabaseConfig.C84_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "CL", "Ei Classification code"));
		} else if (selecteddbMask == DatabaseConfig.INS_MASK
				|| selecteddbMask == DatabaseConfig.IBS_MASK
				|| selecteddbMask == DatabaseConfig.GEO_MASK
				|| selecteddbMask == DatabaseConfig.INS_MASK
						+ DatabaseConfig.IBF_MASK
				|| selecteddbMask == DatabaseConfig.INS_MASK
						+ DatabaseConfig.GEO_MASK
				|| selecteddbMask == DatabaseConfig.INS_MASK
						+ DatabaseConfig.IBF_MASK + DatabaseConfig.GEO_MASK)

		{
			sectiontype.add(new ListBoxOption(selectedWord, "CL", "Classification code"));
		}

		// CN
		if ((selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.CRC_MASK) != DatabaseConfig.CRC_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK
				&& (selecteddbMask & DatabaseConfig.REF_MASK) != DatabaseConfig.REF_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.UPT_MASK) != DatabaseConfig.UPT_MASK
				&& (selecteddbMask & DatabaseConfig.USPTO_MASK) != DatabaseConfig.USPTO_MASK
				&& (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "CN", "CODEN"));
		}

		// CF
		if ((selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.CRC_MASK) != DatabaseConfig.CRC_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK
				&& (selecteddbMask & DatabaseConfig.REF_MASK) != DatabaseConfig.REF_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.UPT_MASK) != DatabaseConfig.UPT_MASK
				&& (selecteddbMask & DatabaseConfig.GRF_MASK) != DatabaseConfig.GRF_MASK
				&& // jam - added GeoRef to exclusion list
				(selecteddbMask & DatabaseConfig.USPTO_MASK) != DatabaseConfig.USPTO_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "CF", "Conference information"));
		}

		// CC
		if (selecteddbMask == DatabaseConfig.CPX_MASK
				|| selecteddbMask == DatabaseConfig.CPX_MASK
						+ DatabaseConfig.C84_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "CC", "Conference code"));
		}

		// BN - For GeoRef
		if (selecteddbMask == DatabaseConfig.GRF_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "BN", "ISBN"));
		}

		// SN
		if ((selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.CRC_MASK) != DatabaseConfig.CRC_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK
				&& (selecteddbMask & DatabaseConfig.REF_MASK) != DatabaseConfig.REF_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.UPT_MASK) != DatabaseConfig.UPT_MASK
				&& (selecteddbMask & DatabaseConfig.USPTO_MASK) != DatabaseConfig.USPTO_MASK
				&& (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "SN", "ISSN"));
		}

		// MH
		if (selecteddbMask == DatabaseConfig.CPX_MASK
				|| selecteddbMask == DatabaseConfig.CBF_MASK
				|| selecteddbMask == DatabaseConfig.CPX_MASK
						+ DatabaseConfig.C84_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "MH", "Ei main heading"));
		}

		// PN
		if ((selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "PN", "Publisher"));
		}

		// ST
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "ST", "Source title"));
		}

		// PM
		if ((selecteddbMask & DatabaseConfig.CPX_MASK) != DatabaseConfig.CPX_MASK
				&& (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK
				&& (selecteddbMask & DatabaseConfig.C84_MASK) != DatabaseConfig.C84_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.CRC_MASK) != DatabaseConfig.CRC_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK
				&& (selecteddbMask & DatabaseConfig.GRF_MASK) != DatabaseConfig.GRF_MASK
				&& // jam - added GeoRef to exclusion list
				(selecteddbMask & DatabaseConfig.USPTO_MASK) != DatabaseConfig.USPTO_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "PM", "Patent number"));
		}

		// PA
		if (selecteddbMask == DatabaseConfig.INS_MASK
				|| selecteddbMask == DatabaseConfig.INS_MASK
						+ DatabaseConfig.IBF_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "PA", "Filing date"));
		}

		// PI
		if (selecteddbMask == DatabaseConfig.INS_MASK
				|| selecteddbMask == DatabaseConfig.INS_MASK
						+ DatabaseConfig.IBF_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "PI", "Patent issue date"));
		}

		// PU
		if (selecteddbMask == DatabaseConfig.INS_MASK
				|| selecteddbMask == DatabaseConfig.INS_MASK
						+ DatabaseConfig.IBF_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "PU", "Country of application"));
		}

		// MI
		if (selecteddbMask == DatabaseConfig.INS_MASK
				|| selecteddbMask == DatabaseConfig.INS_MASK
						+ DatabaseConfig.IBF_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "MI", "Material Identity Number"));
		}

		// CV
		if (selecteddbMask == DatabaseConfig.CPX_MASK
				|| selecteddbMask == DatabaseConfig.CBF_MASK
				|| selecteddbMask == DatabaseConfig.C84_MASK
						+ DatabaseConfig.CPX_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "CV", "Ei controlled term"));
		} else if (selecteddbMask == DatabaseConfig.INS_MASK
				|| selecteddbMask == DatabaseConfig.IBS_MASK
				|| selecteddbMask == DatabaseConfig.INS_MASK
						+ DatabaseConfig.IBF_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "CV", "Inspec controlled term"));
		} else if (selecteddbMask == DatabaseConfig.NTI_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "CV", "NTIS controlled term"));
		} else if (selecteddbMask == DatabaseConfig.PAG_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "CV", "Subject"));
		} else if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "CV", "Controlled term"));
		}

		// DatabaseConfig.NTI_MASK unique fields
		// CT
		if (selecteddbMask == DatabaseConfig.NTI_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "CT", "Contract number"));
		}

		// CO or PC
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "CO", "Country of origin"));
		}

		// AG
		if (selecteddbMask == DatabaseConfig.NTI_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "AG", "Monitoring agency"));
		}

		// PD
		if (selecteddbMask == DatabaseConfig.UPA_MASK
				|| selecteddbMask == DatabaseConfig.EUP_MASK
				|| selecteddbMask == DatabaseConfig.UPA_MASK
						+ DatabaseConfig.EUP_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "PD", "Publication date"));
		}
		// else if(selecteddbMask == DatabaseConfig.PCH_MASK)
		// {
		// sectiontype.add(new ListBoxOption(selectedWord, "PD","Patent info"));
		// }

		// AN
		if (selecteddbMask == DatabaseConfig.NTI_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "AN", "NTIS accession number"));
		}

		// PAM
		if (selecteddbMask == DatabaseConfig.UPA_MASK
				|| selecteddbMask == DatabaseConfig.EUP_MASK
				|| selecteddbMask == DatabaseConfig.UPA_MASK
						+ DatabaseConfig.EUP_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "PAM", "Application number"));
		}

		// RN
		if (selecteddbMask == DatabaseConfig.NTI_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "RN", "Report number"));
		}

		// Patent fields
		// PRN
		if (selecteddbMask == DatabaseConfig.UPA_MASK
				|| selecteddbMask == DatabaseConfig.EUP_MASK
				|| selecteddbMask == DatabaseConfig.UPA_MASK
						+ DatabaseConfig.EUP_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "PRN", "Priority number"));
		}

		// PID
		if (selecteddbMask == DatabaseConfig.UPA_MASK
				|| selecteddbMask == DatabaseConfig.EUP_MASK
				|| selecteddbMask == DatabaseConfig.INS_MASK
				|| selecteddbMask == DatabaseConfig.EPT_MASK
				|| selecteddbMask == DatabaseConfig.INS_MASK
						+ DatabaseConfig.EUP_MASK
				|| selecteddbMask == DatabaseConfig.INS_MASK
						+ DatabaseConfig.UPA_MASK
				|| selecteddbMask == DatabaseConfig.INS_MASK
						+ DatabaseConfig.EPT_MASK
				|| selecteddbMask == DatabaseConfig.UPA_MASK
						+ DatabaseConfig.EUP_MASK
				|| selecteddbMask == DatabaseConfig.EPT_MASK
						+ DatabaseConfig.UPA_MASK
				|| selecteddbMask == DatabaseConfig.EPT_MASK
						+ DatabaseConfig.EUP_MASK
				|| selecteddbMask == DatabaseConfig.UPA_MASK
						+ DatabaseConfig.EUP_MASK + DatabaseConfig.INS_MASK
				|| selecteddbMask == DatabaseConfig.EPT_MASK
						+ DatabaseConfig.UPA_MASK + DatabaseConfig.EUP_MASK
				|| selecteddbMask == DatabaseConfig.EPT_MASK
						+ DatabaseConfig.UPA_MASK + DatabaseConfig.INS_MASK
				|| selecteddbMask == DatabaseConfig.EPT_MASK
						+ DatabaseConfig.EUP_MASK + DatabaseConfig.INS_MASK
				|| selecteddbMask == DatabaseConfig.EPT_MASK
						+ DatabaseConfig.UPA_MASK + DatabaseConfig.EUP_MASK
						+ DatabaseConfig.INS_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "PID", "Int. patent classification"));
		}

		// PUC
		if (selecteddbMask == DatabaseConfig.EUP_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "PEC", "ECLA code"));
		} else if (selecteddbMask == DatabaseConfig.UPA_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "PUC", "US Classification"));
		}

		// CR
		if (selecteddbMask == DatabaseConfig.ELT_MASK
				|| selecteddbMask == DatabaseConfig.EPT_MASK
				|| selecteddbMask == DatabaseConfig.EPT_MASK
						+ DatabaseConfig.ELT_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "CR", "CAS registry number"));
		}

		// PC
		if (selecteddbMask == DatabaseConfig.EPT_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "PC", "Patent country"));
		}

		// IP
		if (selecteddbMask == DatabaseConfig.EPT_MASK) {
			sectiontype.add(new ListBoxOption(selectedWord, "PID", "Int. patent classification"));
		}

		return sectiontype;
	}

	/**
	 * Get the list of Discipline options
	 *
	 * @param selecteddbMask
	 * @return
	 */
	public static List<ListBoxOption> getTreatment(int selecteddbMask, String selectedWord) {

		List<ListBoxOption> treattype = new Vector<ListBoxOption>();

		// NO-LIMIT
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.GRF_MASK) != DatabaseConfig.GRF_MASK
				&& // jam - added GeoRef to exclusion list
				(selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "NO-LIMIT", "All treatment types"));
		} else {
			treattype.add(new ListBoxOption(selectedWord, "NO-LIMIT", "Treatment type not available"));
		}

		// APP
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.GRF_MASK) != DatabaseConfig.GRF_MASK
				&& // jam - added GeoRef to exclusion list
				(selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "APP", "Applications"));
		}

		// BIO
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.GRF_MASK) != DatabaseConfig.GRF_MASK
				&& // jam - added GeoRef to exclusion list
				(selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.INS_MASK) != DatabaseConfig.INS_MASK
				&& (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "BIO", "Biographical"));
		}

		// BIB
		if (selecteddbMask == DatabaseConfig.INS_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "BIB", "Bibliography"));
		}

		// ECO
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.GRF_MASK) != DatabaseConfig.GRF_MASK
				&& // jam - added GeoRef to exclusion list
				(selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "ECO", "Economic"));
		}

		// EXP
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.GRF_MASK) != DatabaseConfig.GRF_MASK
				&& // jam - added GeoRef to exclusion list
				(selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "EXP", "Experimental"));
		}

		// GEN
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.GRF_MASK) != DatabaseConfig.GRF_MASK
				&& // jam - added GeoRef to exclusion list
				(selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "GEN", "General review"));
		}

		// Cpx fields
		// HIS
		if (selecteddbMask == DatabaseConfig.CPX_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "HIS", "Historical"));
		}
		// LIT
		if (selecteddbMask == DatabaseConfig.CPX_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "LIT", "Literature review"));
		}
		// MAN
		if (selecteddbMask == DatabaseConfig.CPX_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "MAN", "Management aspects"));
		}
		// NUM
		if (selecteddbMask == DatabaseConfig.CPX_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "NUM", "Numerical"));
		}

		// Inspec fields
		// NEW
		if (selecteddbMask == DatabaseConfig.INS_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "NEW", "New development"));
		}
		if (selecteddbMask == DatabaseConfig.INS_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "PRA", "Practical"));
		}
		if (selecteddbMask == DatabaseConfig.INS_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "PRO", "Product review"));
		}
		// THR
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.GRF_MASK) != DatabaseConfig.GRF_MASK
				&& // jam - added GeoRef to exclusion list
				(selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK) {
			treattype.add(new ListBoxOption(selectedWord, "THR", "Theoretical"));
		}

		return treattype;
	}

	/**
	 * Get the list of Doctype options
	 *
	 * @param selecteddbMask
	 * @return
	 */
	public static List<ListBoxOption> getDoctype(int selecteddbMask, String selectedWord) {
		List<ListBoxOption> doctype = new Vector<ListBoxOption>();

		// NO-LIMIT
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "NO-LIMIT", "All document types"));
		} else if (selecteddbMask == DatabaseConfig.UPA_MASK
				|| selecteddbMask == DatabaseConfig.EUP_MASK
				|| selecteddbMask == DatabaseConfig.EUP_MASK
						+ DatabaseConfig.UPA_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "NO-LIMIT", "All patents"));
		} else {
			doctype.add(new ListBoxOption(selectedWord, "NO-LIMIT", "Document type not available"));
		}

		String loc = System.getProperty("loc");
		if (loc != null && loc.equals("china")) {
			if (selecteddbMask == DatabaseConfig.CPX_MASK) {
				doctype.add(new ListBoxOption(selectedWord, "CORE", "CORE"));
			}
		}

		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&&
				// (selecteddbMask & DatabaseConfig.ELT_MASK) !=
				// DatabaseConfig.ELT_MASK &&
				(selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "JA", "Journal article"));
		}

		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "CA", "Conference article"));
		} else if (selecteddbMask == DatabaseConfig.ELT_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "CA", "Conference"));
		}

		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "CP", "Conference proceeding"));
		}

		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "MC", "Monograph chapter"));
		}

		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "MR", "Monograph review"));
		}

		// RC
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "RC", "Report chapter"));
		}

		// RR
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "RR", "Report review"));
		}

		// DS
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.UPA_MASK) != DatabaseConfig.UPA_MASK
				&& (selecteddbMask & DatabaseConfig.EUP_MASK) != DatabaseConfig.EUP_MASK
				&& (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "DS", "Dissertation"));
		}

		if (selecteddbMask == DatabaseConfig.INS_MASK
				|| selecteddbMask == DatabaseConfig.IBS_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "UP", "Unpublished paper"));
		}

		if (selecteddbMask == DatabaseConfig.CPX_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "PA", "Patents (before 1970)"));
			doctype.add(new ListBoxOption(selectedWord, "IP", "Article in Press"));
		} else if (selecteddbMask == DatabaseConfig.CBF_MASK
				|| selecteddbMask == DatabaseConfig.PCH_MASK
				|| selecteddbMask == DatabaseConfig.IBS_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "PA", "Patents"));
		} else if (selecteddbMask == DatabaseConfig.INS_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "PA", "Patents (before 1977)"));
		}

		if (selecteddbMask == DatabaseConfig.UPA_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "UA", "US Applications"));
			doctype.add(new ListBoxOption(selectedWord, "UG", "US Granted"));
		} else if (selecteddbMask == DatabaseConfig.EUP_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "EA", "European Applications"));
			doctype.add(new ListBoxOption(selectedWord, "EG", "European Granted"));
		} else if ((selecteddbMask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK
				&& (selecteddbMask & DatabaseConfig.GEO_MASK) != DatabaseConfig.GEO_MASK
				&& (selecteddbMask & DatabaseConfig.GRF_MASK) != DatabaseConfig.GRF_MASK
				&& (selecteddbMask & DatabaseConfig.CPX_MASK) != DatabaseConfig.CPX_MASK
				&& (selecteddbMask & DatabaseConfig.INS_MASK) != DatabaseConfig.INS_MASK
				&& (selecteddbMask & DatabaseConfig.IBS_MASK) != DatabaseConfig.IBS_MASK
				&& (selecteddbMask & DatabaseConfig.CBF_MASK) != DatabaseConfig.CBF_MASK
				&& (selecteddbMask & DatabaseConfig.NTI_MASK) != DatabaseConfig.NTI_MASK
				&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
				&& (selecteddbMask & DatabaseConfig.CBN_MASK) != DatabaseConfig.CBN_MASK
				&& (selecteddbMask & DatabaseConfig.CHM_MASK) != DatabaseConfig.CHM_MASK
				&& (selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
				&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "UA", "US Applications"));
			doctype.add(new ListBoxOption(selectedWord, "UG", "US Granted"));
			doctype.add(new ListBoxOption(selectedWord, "EA", "European Applications"));
			doctype.add(new ListBoxOption(selectedWord, "EG", "European Granted"));
		}

		if (selecteddbMask == DatabaseConfig.PCH_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "(CA or CP)", "Conferences"));
			doctype.add(new ListBoxOption(selectedWord, "PA", "Patents"));
			doctype.add(new ListBoxOption(selectedWord, "MC or MR or RC or RR or DS or UP", "Other documents"));
		}

		if (selecteddbMask == DatabaseConfig.CBN_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "Journal", "Journal article"));
			doctype.add(new ListBoxOption(selectedWord, "Advertizement", "Advertisement"));
			doctype.add(new ListBoxOption(selectedWord, "Book", "Book"));
			doctype.add(new ListBoxOption(selectedWord, "Directory", "Directory"));
			doctype.add(new ListBoxOption(selectedWord, "Company", "Company Report"));
			doctype.add(new ListBoxOption(selectedWord, "Stockbroker", "Stockbroker Report"));
			doctype.add(new ListBoxOption(selectedWord, "Market", "Market Research Report"));
			doctype.add(new ListBoxOption(selectedWord, "Press", "Press Release"));
		}

		if (selecteddbMask == DatabaseConfig.ELT_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "AB", "Abstract"));
		}

		// jam - added MAP exclusively for GeoRef
		if (selecteddbMask == DatabaseConfig.GRF_MASK) {
			doctype.add(new ListBoxOption(selectedWord, "MP", "Map"));
            doctype.add(new ListBoxOption(selectedWord, "GI", "In Process"));
		}

		return doctype;
	} // getDoctype()

	/**
	 * Get the list of Language options
	 *
	 * @param selecteddbMask
	 * @return
	 */
	public static List<ListBoxOption> getLanguage(int selecteddbMask, String selectedWord) {
		List<ListBoxOption> lang = new Vector<ListBoxOption>();
		if ((selecteddbMask & DatabaseConfig.PAG_MASK) == DatabaseConfig.PAG_MASK) {
			lang.add(new ListBoxOption(selectedWord, "NO-LIMIT", "Language not available"));
		} else {
			lang.add(new ListBoxOption(selectedWord, "NO-LIMIT", "All Languages"));
			lang.add(new ListBoxOption(selectedWord, "English", "English"));
			lang.add(new ListBoxOption(selectedWord, "Chinese", "Chinese"));
			lang.add(new ListBoxOption(selectedWord, "French", "French"));
			lang.add(new ListBoxOption(selectedWord, "German", "German"));
			if ((selecteddbMask & DatabaseConfig.ELT_MASK) != DatabaseConfig.ELT_MASK
					&& (selecteddbMask & DatabaseConfig.EPT_MASK) != DatabaseConfig.EPT_MASK
					&& (selecteddbMask & DatabaseConfig.PCH_MASK) != DatabaseConfig.PCH_MASK) {
				lang.add(new ListBoxOption(selectedWord, "Italian", "Italian"));
			}
			lang.add(new ListBoxOption(selectedWord, "Japanese", "Japanese"));
			lang.add(new ListBoxOption(selectedWord, "Russian", "Russian"));
			lang.add(new ListBoxOption(selectedWord, "Spanish", "Spanish"));
		}
		return lang;
	}

	public static int calEndYear(int selectedDbMask) {
		if (selectedDbMask == DatabaseConfig.CBF_MASK) {
			return DatabaseConfig.CBF_ENDYEAR;
		}
		if (selectedDbMask == DatabaseConfig.IBS_MASK) {
			return DatabaseConfig.IBS_ENDYEAR;
		}
		return SearchForm.ENDYEAR;
	}

	public static List<ListBoxOption> getYears(int selectedDbMask, String sYear,
            String strYear, String yearType) {
	    List<ListBoxOption> years = new ArrayList<ListBoxOption>();
        try {
            int endYear = SearchForm.calEndYear(selectedDbMask);
            int sy = SearchForm.calStartYear(selectedDbMask, strYear);
            int dy = endYear;

            if (sYear.length() > 0) {
                dy = SearchForm.calDisplayYear(selectedDbMask, sYear);
            }

            else if (yearType.equals("startYear")) {
                dy = SearchForm.calDisplayYear(selectedDbMask, strYear);
            } else if (yearType.equals("endYear")) {
                dy = endYear;
            }

            for (int j = sy; j <= endYear; j++) {
                String yr = Integer.toString(j);
                ListBoxOption lbo = new ListBoxOption("", yr, yr);
                if (j == dy) {
                    lbo.setSelected(yr);
                }
                years.add(lbo);
            }
        } catch (Exception e) {
            log4j.error("Unable to retrieve year ListBox options", e);
        }
        return years;
	}

	public static String getYear(int selectedDbMask, String sYear,
			String strYear, String yearType) {

		// System.out.println("selectedDbMask "+selectedDbMask+" sYear "+sYear+" strYear "+strYear+" yearType "+yearType);
		StringBuffer yearString = null;
		try {
			yearString = new StringBuffer();

			int endYear = SearchForm.calEndYear(selectedDbMask);
			int sy = SearchForm.calStartYear(selectedDbMask, strYear);
			int dy = endYear;

			if (sYear.length() > 0) {
				dy = SearchForm.calDisplayYear(selectedDbMask, sYear);
			}

			else if (yearType.equals("startYear")) {
				dy = SearchForm.calDisplayYear(selectedDbMask, strYear);
			} else if (yearType.equals("endYear")) {
				dy = endYear;
			}

			for (int j = sy; j <= endYear; j++) {
				yearString.append("<option value='" + j);

				if (j == dy) {
					yearString.append("' selected='true' >" + j + "</option>");
				} else {
					yearString.append("' >" + j + "</option>");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return yearString.toString();
	}

    private static int calStartYear(int selectedDbMask, String sYear) {
        int dYear = 1973;
        try {
            if (sYear != null && sYear.length() > 0) {
                Iterator itrdbs = (DatabaseConfig.getInstance()).getDatabaseTable().values().iterator();
                while (itrdbs.hasNext()) {
                    Database db = (Database) itrdbs.next();
                    if (selectedDbMask != 0 && ((selectedDbMask & db.getMask()) == db.getMask()) &&
                        sYear.indexOf(db.getSingleCharName() + "ST") >= 0) {
                        int dbStartYear = Integer.parseInt(sYear.substring(sYear.indexOf(db.getSingleCharName() + "ST") + 3,
                            sYear.indexOf(db.getSingleCharName() + "ST") + 7));
                        dYear = (dYear > dbStartYear) ? dbStartYear : dYear;
                    }
                }
            }
        } catch (NumberFormatException e) {
            log4j.error("Problem with BackOffice \"Default Database:\" Settings! Selected DBMask (" + selectedDbMask
                + ") contains values which user does not have permission for.", e);
        }
        return dYear;
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
                Iterator itrdbs = (DatabaseConfig.getInstance()).getDatabaseTable().values().iterator();
                while (itrdbs.hasNext()) {
                    Database db = (Database) itrdbs.next();
                    if (selectedDbMask != 0 && ((selectedDbMask & db.getMask()) == db.getMask()) &&
                        sYear.indexOf(db.getSingleCharName() + "SY") >= 0) {
                        int dbStartYear = Integer.parseInt(sYear.substring(sYear.indexOf(db.getSingleCharName() + "SY") + 3,
                            sYear.indexOf(db.getSingleCharName() + "SY") + 7));
                        dYear = (dYear > dbStartYear) ? dbStartYear : dYear;
                    }
                }
            } else if (sYear != null && sYear.length() > 0) {
                dYear = Integer.parseInt(sYear);
            }
        } catch (NumberFormatException e) {
            log4j.error("Problem with BackOffice \"Default Database:\" Settings! Selected DBMask (" + selectedDbMask
                + ") contains values which user does not have permission for.", e);
        }
        return dYear;
    }

}