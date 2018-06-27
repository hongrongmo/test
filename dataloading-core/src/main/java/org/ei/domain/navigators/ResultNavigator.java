/*
 * Created on Jul 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;

/**
 * @author JMoschet
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ResultNavigator {
    public static final String REMOVE_MODIFIER = "QQ_REMOVE_MODIFIER_QQ";

    private List<EiNavigator> fastnavigators;

    private int m_compmask = 0;

    // TMH - adding state of navigators (open and sort order)

    public ResultNavigator(Hashtable<String, List<String[]>> navs, Database[] databases) {
        this.fastnavigators = getFastNavigators(navs, databases);

        // sets m_mixed, m_patents, m_other
        this.getComposition();

        // since we are coming from fast - edit members/change titles/etc,
        this.adjustComposition();
    }

    public ResultNavigator(String navigatorsstring) {
        if (navigatorsstring != null) {
            this.fastnavigators = getNavs(navigatorsstring);
            // set the members of the class which will be used to determine
            // the output order
            this.getComposition();

            // since we are NOT coming from fast but from the cache
            // we do not need to cleanup, etc.
        }
    }

    public int getNavigatorsCount() {
        return (fastnavigators != null) ? fastnavigators.size() : 0;
    }

    public int getCompositionMask() {
        return m_compmask;
    }

    private void getComposition() {
        EiNavigator dbnavigator = this.getNavigatorByName(EiNavigator.DB);

        if (dbnavigator != null) {
            List<?> mods = dbnavigator.getModifiers();
            // set the fast navigators collection to point to these navigators
            // so getNavigatorByName will work
            // we will set fastnavigators = navigators again to the return value of this method

            if (mods != null) {
                DatabaseConfig dbConfig = DatabaseConfig.getInstance();
                Iterator<?> itrmods = mods.iterator();
                while (itrmods.hasNext()) {
                    EiModifier amod = (EiModifier) itrmods.next();
                    // make sure db modifier has a value for looking up db
                    if (amod.getValue() != null) {
                        Database adb = dbConfig.getDatabase(amod.getValue());
                        if (adb == null) {
                            // if we failed - try trimming and lowercasing the code
                            if (amod.getValue().length() >= 3) {
                                adb = dbConfig.getDatabase(amod.getValue().substring(0, 3).toLowerCase());
                            }
                        }
                        // if db lookup is not null
                        if (adb != null) {
                            m_compmask += adb.getMask();
                        }
                    }
                }
            }
        }
    }

    private void adjustComposition() {

        // System.out.println(" adjustComposition w/ mask = " + getCompositionMask());
        boolean m_books = false;
        boolean m_cbnb = false;
        boolean m_chimica = false;
        boolean m_paperchem = false;
        boolean m_encompasslit = false;
        boolean m_encompasspat = false;
        boolean m_ntis = false;
        boolean m_geobase = false;
        boolean m_georef = false;
        boolean m_inspec = false;
        boolean m_inspecarchive = false;
        boolean m_compendex = false;
        boolean m_uspatents = false;
        boolean m_eupatents = false;

        EiNavigator dbnavigator = this.getNavigatorByName(EiNavigator.DB);

        if (dbnavigator != null) {
            List<?> mods = dbnavigator.getModifiers();

            m_compendex = mods.contains(EiModifier.MOD_CPX) || mods.contains(EiModifier.MOD_CBF);
            m_inspec = mods.contains(EiModifier.MOD_INS);
            m_inspecarchive = mods.contains(EiModifier.MOD_IBS);
            m_ntis = mods.contains(EiModifier.MOD_NTI);
            m_geobase = mods.contains(EiModifier.MOD_GEO);
            m_georef = mods.contains(EiModifier.MOD_GRF);
            m_encompasspat = mods.contains(EiModifier.MOD_EPT);
            m_encompasslit = mods.contains(EiModifier.MOD_ELT);
            m_cbnb = mods.contains(EiModifier.MOD_CBN);
            m_chimica = mods.contains(EiModifier.MOD_CHM);
            m_paperchem = mods.contains(EiModifier.MOD_PCH);
            m_books = mods.contains(EiModifier.MOD_PAG);
            m_eupatents = mods.contains(EiModifier.MOD_EUP);
            m_uspatents = mods.contains(EiModifier.MOD_UPA);
        }

        boolean m_booksOnly = (getCompositionMask() == DatabaseConfig.PAG_MASK);
        boolean m_cbnbOnly = (getCompositionMask() == DatabaseConfig.CBN_MASK);
        boolean m_encompasspatOnly = (getCompositionMask() == DatabaseConfig.EPT_MASK);
        boolean m_euppatentsOnly = (getCompositionMask() == DatabaseConfig.EUP_MASK);
        boolean m_uspatentsOnly = (getCompositionMask() == DatabaseConfig.UPA_MASK);

        // AU
        EiNavigator anav = getNavigatorByName(EiNavigator.AU);
        if (anav != null) {
            // if(mask == upa || mask == eup || mask == ept || mask == upa + eup || mask == upa + ept || mask == eup + ept || mask == upa + eup + ept)
            if ((m_uspatents || m_eupatents || m_encompasspat)
                && !(m_compendex || m_inspec || m_ntis || m_geobase || m_georef || m_encompasslit || m_cbnb || m_chimica || m_paperchem || m_books)) {
                anav.setDisplayname("Inventor");
            }
            // else if((mask :: cpx || mask :: cbf || mask :: ins || mask :: nti || mask :: geo || mask :: chm || mask :: pag || mask :: pch || mask :: elt) &&
            // (mask !: cbn || mask !: upa || mask !: eup || mask !: ept))
            else if ((m_compendex || m_inspec || m_inspecarchive || m_ntis || m_geobase || m_georef || m_encompasslit || m_paperchem || m_chimica || m_books)
                && !m_cbnb && !(m_uspatents || m_eupatents || m_encompasspat)) {
                anav.setDisplayname("Author");
            }
            // Author/Inventor if database combination doesn't contain CBNB AND does contain A&I database(s) AND also a patent database
            // else if((mask :: cpx || mask :: cbf || mask :: ins || mask :: nti || mask :: geo || mask :: chm || mask :: pag || mask :: pch || mask :: elt) &&
            // (mask !: cbn) && (mask :: upa || mask :: eup || mask :: ept))
            else if ((m_compendex || m_inspec || m_ntis || m_inspecarchive || m_geobase || m_georef || m_encompasslit || m_paperchem || m_chimica || m_books)
                && !m_cbnb && (m_uspatents || m_eupatents || m_encompasspat)) {
                anav.setDisplayname("Author/Inventor");
            } else {
                fastnavigators.remove(anav);
            }

            anav.getModifiers().remove(new EiModifier(0, "Anonymous", "Anonymous"));
            anav.getModifiers().remove(new EiModifier(0, "Anon", "Anon"));

        }
        // AF
        anav = getNavigatorByName(EiNavigator.AF);
        if (anav != null) {
            if (m_cbnbOnly) {
                fastnavigators.remove(anav);

                EiNavigator afnav = copyNavigator(anav, new EiNavigator(EiNavigator.GD));

                fastnavigators.add(afnav);
            }
            // if(mask == upa || mask == eup || mask == ept || mask == upa + eup || mask == upa + ept || mask == eup + ept || mask == upa + eup + ept)
            else if ((m_uspatents || m_eupatents || m_encompasspat)
                && !(m_books || m_cbnb || m_compendex || m_inspec || m_ntis || m_geobase || m_georef || m_encompasslit || m_paperchem || m_chimica)) {
                anav.setDisplayname("Assignee");
            }
            // else if((mask :: cpx || mask :: cbf || mask :: ins || mask :: nti || mask :: geo || mask :: chm || mask :: pag || mask :: pch || mask :: elt) &&
            // (mask !: cbn || mask !: upa || mask !: eup || mask !: ept))
            else if ((m_compendex || m_inspec || m_ntis || m_geobase || m_georef || m_encompasslit || m_paperchem || m_chimica || m_books) && !m_cbnb
                && !(m_inspecarchive || m_uspatents || m_eupatents || m_encompasspat)) {
                anav.setDisplayname("Author affiliation");
            }
            // Author/Inventor if database combination doesn't contain CBNB AND does contain A&I database(s) AND also a patent database
            // else if((mask :: cpx || mask :: cbf || mask :: ins || mask :: nti || mask :: geo || mask :: chm || mask :: pag || mask :: pch || mask :: elt) &&
            // (mask !: cbn) && (mask :: upa || mask :: eup || mask :: ept))
            else if ((m_compendex || m_inspec || m_ntis || m_geobase || m_georef || m_encompasslit || m_paperchem || m_chimica || m_books) && !m_cbnb
                && (m_inspecarchive || m_uspatents || m_eupatents || m_encompasspat)) {
                anav.setDisplayname("Author affiliation/Assignee");
            } else {
                fastnavigators.remove(anav);
            }
        }
        // CV
        anav = getNavigatorByName(EiNavigator.CV);
        if (anav != null) {
            anav.getModifiers().remove(new EiModifier(0, "S300", "S300"));
            anav.getModifiers().remove(new EiModifier(0, "WOBL", "WOBL"));
            anav.getModifiers().remove(new EiModifier(0, "406", "406"));

            // if((mask :: cpx || mask :: cbf || mask :: ins || mask :: nti || mask :: geo || mask :: cbn || mask :: chm || mask :: pch || mask :: elt || mask
            // :: ept) && (mask !: upa || mask !: eup || mask !: pag))
            if ((m_compendex || m_inspec || m_ntis || m_geobase || m_georef)
                && !(m_cbnb || m_chimica || m_paperchem || m_encompasslit || m_encompasspat || m_uspatents || m_eupatents || m_books)) {
                anav.setDisplayname("Controlled vocabulary");
            } else if ((m_compendex || m_inspec || m_inspecarchive || m_ntis || m_geobase || m_georef || m_cbnb || m_chimica || m_paperchem || m_encompasslit || m_encompasspat)
                && !(m_uspatents || m_eupatents || m_books)) {
                anav.setDisplayname("Controlled terms");
            } else {
                fastnavigators.remove(anav);
            }
        }
        // ST
        anav = getNavigatorByName(EiNavigator.ST);
        if (anav != null) {
            if (m_booksOnly) {
                // check for bookrecords and pagerecords doctypes in results
                EiNavigator dtnav = getNavigatorByName(EiNavigator.DT);
                List<?> dtmods = dtnav.getModifiers();
                boolean bookrecords = dtmods.contains(EiModifier.DT_BOOK);
                boolean pagerecords = dtmods.contains(EiModifier.DT_PAGE);
                // if only book records remove ST (book title) nav
                if ((bookrecords) && (!pagerecords)) {
                    fastnavigators.remove(anav);
                } else {
                    // Virtual navigator
                    // ST (Book Title, ST)
                    // change Serial title to Book title for when results are books only
                    fastnavigators.remove(anav);
                    anav = BookNavigator.createBookNavigator(anav);
                    fastnavigators.add(anav);
                }
            }
            // if((mask :: cpx || mask :: cbf || mask :: ins || mask :: nti || mask :: geo || mask :: cbn || mask :: chm || mask :: pch || mask :: elt) && (mask
            // !: upa || mask !: eup || mask !: pag || mask !: ept))
            else if ((m_compendex || m_inspec || m_inspecarchive || m_ntis || m_geobase || m_georef || m_cbnb || m_chimica || m_paperchem || m_encompasslit)
                && !(m_uspatents || m_eupatents || m_books || m_encompasspat)) {
                anav.setDisplayname("Source title");
            } else {
                fastnavigators.remove(anav);
            }
        }
        // PN
        anav = getNavigatorByName(EiNavigator.PN);
        if (anav != null) {
            // Publisher
            // if((mask :: cpx || mask :: cbf || mask :: ins || mask :: cbn || mask :: chm || mask :: pag || mask :: pch || mask :: elt) && (mask !: nti || mask
            // !: geo || mask !: upa || mask !: eup || mask !: ept))
            if ((m_compendex || m_inspec || m_inspecarchive || m_ntis || m_georef || m_cbnb || m_chimica || m_books || m_paperchem || m_encompasslit)
                && !(m_ntis || m_geobase || m_uspatents || m_eupatents || m_encompasspat)) {
                anav.setDisplayname("Publisher");
            } else {
                fastnavigators.remove(anav);
            }
        }
        // LA
        anav = getNavigatorByName(EiNavigator.LA);
        if (anav != null) {
            // Language
            // if((mask :: cpx || mask :: cbf || mask :: ins || mask :: nti || mask :: geo || mask :: cbn || mask :: chm || mask :: pch || mask :: elt || mask
            // :: ept) && (mask !: upa || mask !: eup || mask !: pag))
            if ((m_compendex || m_inspec || m_inspecarchive || m_ntis || m_geobase || m_georef || m_cbnb || m_chimica || m_paperchem || m_encompasslit || m_encompasspat)
                && !(m_uspatents || m_eupatents || m_books)) {
                anav.setDisplayname("Language");
            } else {
                fastnavigators.remove(anav);
            }
        }
        // YR - Do nothing - default as is
        /*
         * anav = getNavigatorByName(EiNavigator.YR); if(anav != null) { // Year //if((mask :: cpx || mask :: cbf || mask :: ins || mask :: nti || mask :: geo
         * || mask :: cbn || mask :: upa || mask :: eup || mask :: chm || mask :: pch || mask :: elt) && (mask !: pag || mask !: ept)) if((m_compendex ||
         * m_inspec || m_ntis || m_geobase || m_cbnb || m_uspatents || m_eupatents || m_chimica || m_paperchem || m_encompasslit) && !(m_books ||
         * m_encompasspat)) { anav.setDisplayname("Year"); } else { fastnavigators.remove(anav); } }
         */

        // CL
        anav = getNavigatorByName(EiNavigator.CL);
        if (anav != null) {
            // removed logic for mapping IC to CL for CBNB (moved to PK)
            // if (mask = pag)
            if (m_booksOnly) {
                // if books and nothing else
                // Uses CleanCLFacet
                // CL (Book Collection, CL)
                cleanCLFacet();
                anav.setDisplayname("Book Collection");
            } else if (m_cbnbOnly) {
                anav.setDisplayname("Industrial sector code");
            }
            // else if((mask :: cpx || mask :: cbf || mask :: ins || mask :: nti || mask :: geo || mask :: elt) && (mask !: cbn || mask !: upa || mask !: eup ||
            // mask !: pag || mask !: chm || mask !: pch || mask !: ept))
            else if ((m_compendex || m_inspec || m_inspecarchive || m_ntis || m_geobase || m_encompasslit)
                && !(m_cbnb || m_uspatents || m_eupatents || m_books || m_chimica || m_paperchem || m_encompasspat || m_georef)) {
                anav.setDisplayname("Classification code");
            } else {
                fastnavigators.remove(anav);
            }
        }
        // DT
        anav = getNavigatorByName(EiNavigator.DT);
        if (anav != null) {
            // Document type
            if (m_booksOnly) {
                // get DT nav and always remove it if books only
                fastnavigators.remove(anav);
                anav = null;
            }
            // There is some document type data for EnCompassPAT, but it is only present for US patents. Mary doesn't think DT navigatior should be displayed
            // for EPT.
            // if((mask :: cpx || mask :: cbf || mask :: ins || mask :: nti || mask :: geo || mask :: cbn || mask :: upa || mask :: eup || mask :: chm || mask
            // :: pch || mask :: elt) && (mask !: pag || mask !: ept))
            else if ((m_compendex || m_inspec || m_inspecarchive || m_ntis || m_geobase || m_georef || m_cbnb || m_uspatents || m_eupatents || m_chimica
                || m_paperchem || m_encompasslit)
                && !(m_books || m_encompasspat)) {
                anav.setDisplayname("Document type");
            } else {
                fastnavigators.remove(anav);
                anav = null;
            }

            if (anav != null) {
                // Document type -- editing values from navigator
                if ((m_uspatents || m_eupatents)
                    && !(m_compendex || m_inspec || m_ntis || m_geobase || m_georef || m_encompasslit || m_encompasspat || m_paperchem || m_chimica || m_cbnb || m_books)) {
                    // Remove the document type "Patent" from DT.
                    anav.setDisplayname("Patent type");
                    anav.getModifiers().remove(EiModifier.PATENT);
                } else if ((m_uspatents || m_eupatents)
                    && (m_compendex || m_inspec || m_ntis || m_geobase || m_encompasslit || m_encompasspat || m_paperchem || m_chimica || m_cbnb || m_books))
                // else if(mask :: upa || mask :: eup)
                {
                    // Remove the following document types from DT:
                    // US Grants
                    // US Applications
                    // European Grants
                    // European Applications
                    anav.getModifiers().remove(EiModifier.US_GRANTS);
                    anav.getModifiers().remove(EiModifier.US_APPLICATIONS);
                    anav.getModifiers().remove(EiModifier.EU_GRANTS);
                    anav.getModifiers().remove(EiModifier.EU_APPLICATIONS);
                }
            }
        }
        // FL
        anav = getNavigatorByName(EiNavigator.FL);
        if (anav != null) {
            // Uncontrolled Terms and Keyword
            if (m_booksOnly) {
                // Virtual navigator
                // FL (Keyword, FL)
                if (anav != null) {
                    anav.setDisplayname("Keyword");
                    anav.setFieldname("ky");
                    anav.setName("kynav");
                }
            }
            // No longer used for Qualifier for EncLit and EncPat - now uses PUC as Role
            // else if((mask :: chm || mask :: pch) && (mask !: cpx || mask !: cbf || mask !: ins || mask !: nti || mask !: geo || mask !: cbn || mask !: upa ||
            // mask !: eup || mask !: pag || mask !: elt || mask !: ept))
            else if ((m_chimica || m_paperchem || m_inspecarchive)
                && !(m_compendex || m_inspec || m_ntis || m_geobase || m_georef || m_cbnb || m_uspatents || m_eupatents || m_books || m_encompasslit || m_encompasspat)) {
                // FL (Uncontrolled Terms, FL)
                anav.setDisplayname("Uncontrolled terms");
            } else {
                fastnavigators.remove(anav);
            }
        }
        // CO
        anav = getNavigatorByName(EiNavigator.CO);
        if (anav != null) {
            // Country and Book Chapter
            // if(mask == pag)
            if (m_booksOnly) {
                // Virtual navigator
                // CO (Book Chapter, CO)
                // change Country to Book chapter for when results are books only
                fastnavigators.remove(anav);
                anav = BookNavigator.createBookNavigator(anav);
                fastnavigators.add(anav);
            }
            // else if((mask :: cpx || mask :: cbf || mask :: ins || mask :: nti || mask :: geo || mask :: cbn || mask :: upa || mask :: eup || mask :: elt ||
            // mask :: pch) & (mask !: pag || mask !: ept))
            else if ((m_compendex || m_inspec || m_ntis || m_geobase || m_georef || m_cbnb || m_chimica || m_uspatents || m_eupatents || m_encompasslit || m_paperchem)
                && !(m_inspecarchive || m_books || m_encompasspat)) {
                anav.setDisplayname("Country");
            } else {
                fastnavigators.remove(anav);
            }
        }

        // PID
        anav = getNavigatorByName(EiNavigator.PID);
        if (anav != null) {

            // IPC Code and Companies
            if (m_cbnbOnly) {
                // PID (Companies, COM->PID)
                // PEC (Chemicals, CIN->PEC)
                fastnavigators.remove(anav);

                EiNavigator pidnav = copyNavigator(anav, new EiNavigator(EiNavigator.CP));

                fastnavigators.add(pidnav);
            }
            // IPC data from the PK navigator should display if only EnCompassPAT is searched.
            // else if(mask == upa || mask == eup || mask == upa + eup || mask == upa + ept || mask == eup + ept || mask == upa + eup + ept)
            else if ((m_uspatents || m_eupatents || m_encompasspat || m_inspec)
                && !(m_compendex || m_ntis || m_georef || m_geobase || m_cbnb || m_books || m_encompasslit)) {
                // PID (IPC Code, PID)
                anav.setDisplayname("IPC code");
            } else if ((m_georef || m_geobase)
                && !(m_compendex || m_inspec || m_ntis || m_uspatents || m_eupatents || m_cbnb || m_books || m_encompasslit || m_encompasspat)) {
                fastnavigators.remove(anav);

                EiNavigator pidnav = copyNavigator(anav, new EiNavigator(EiNavigator.GEO));

                fastnavigators.add(pidnav);
            } else {
                fastnavigators.remove(anav);
            }
        }

        // PEC
        anav = getNavigatorByName(EiNavigator.PEC);
        if (anav != null) {
            // ECLA Code and Chemicals
            // if(mask == cbn)
            if (m_cbnbOnly) {
                // PEC (Chemicals, CIN->PEC)
                fastnavigators.remove(anav);

                EiNavigator pecnav = copyNavigator(anav, new EiNavigator(EiNavigator.CM));

                fastnavigators.add(pecnav);
            }
            // else if(mask == elt || mask == ept || mask == elt + ept)
            else if ((m_encompasslit || m_encompasspat)
                && !(m_compendex || m_inspec || m_inspecarchive || m_ntis || m_geobase || m_georef || m_cbnb || m_chimica || m_paperchem || m_uspatents
                    || m_eupatents || m_books)) {
                fastnavigators.remove(anav);

                EiNavigator pecnav = copyNavigator(anav, new EiNavigator(EiNavigator.CVM));

                fastnavigators.add(pecnav);
            }
            // else if((mask :: eup) && (mask !: cpx || mask !: cbf || mask !: ins || mask !: nti || mask !: geo || mask !: upa || mask !: chm || mask !: pag ||
            // mask !: pch || mask !: elt || mask !: ept))
            else if (m_euppatentsOnly) {
                // PEC (ECLA, PEC)
                anav.setDisplayname("ECLA");
            } else {
                fastnavigators.remove(anav);
            }
        }

        // PUC
        anav = getNavigatorByName(EiNavigator.PUC);
        if (anav != null) {
            // US Classification
            // if((mask :: upa) && (mask !: cpx || mask !: cbf || mask !: ins || mask !: nti || mask !: geo || mask !: cbn || mask !: eup || mask !: chm || mask
            // !: pag || mask !: pch || mask !: elt || mask !: ept))
            if (m_uspatentsOnly) {
                // PUC (US Classification, PUC)
                anav.setDisplayname("US classification");
            }
            // else if(mask == elt || mask == ept || mask == elt + ept)
            else if ((m_encompasslit || m_encompasspat)
                && !(m_compendex || m_inspec || m_inspecarchive || m_ntis || m_geobase || m_georef || m_cbnb || m_chimica || m_paperchem || m_uspatents
                    || m_eupatents || m_books)) {
                fastnavigators.remove(anav);

                EiNavigator pecnav = copyNavigator(anav, new RONavigator());

                fastnavigators.add(pecnav);
            } else {
                fastnavigators.remove(anav);
            }
        }

        // PK
        anav = getNavigatorByName(EiNavigator.PK);
        if (anav != null) {
            // Classification Code - NOTE: chimica and paperchem do not have classification codes.
            // if(mask == cbn)
            if (m_cbnbOnly) {
                fastnavigators.remove(anav);

                // if cbnb and nothing else
                // CL (Industrial sector, GIC->CL)
                EiNavigator clnav = copyNavigator(anav, new EiNavigator(EiNavigator.IC));

                fastnavigators.add(clnav);
            }
        }

        // PAC
        anav = getNavigatorByName(EiNavigator.PAC);
        if (anav != null) {
            // Authority Code
            // This navigator is returned for UPA and EUP, but they aren't displayed on the current site.
            // if(mask == ept)
            if (m_encompasspatOnly) {
                // PAC (Authority Code, PAC)
                fastnavigators.remove(anav);

                EiNavigator pacnav = copyNavigator(anav);
                pacnav.setDisplayname("Authority code");

                fastnavigators.add(pacnav);
            } else {
                fastnavigators.remove(anav);
            }
        }

    }

    public void removeRefinements(Refinements xrefs) {
        Iterator<?> itrRefinements = xrefs.iterator();
        while (itrRefinements.hasNext()) {
            EiNavigator refNav = (EiNavigator) itrRefinements.next();
            EiNavigator resultnav = getNavigatorByName(refNav.getName());

            if (resultnav == null) {
                resultnav = EiNavigator.createNavigator(refNav.getName());
            }
            // log.info(" removing refinement from Nav. Field [" + refNav.getName() + "]");

            Iterator<?> itrRefMods = refNav.getModifiers().iterator();

            // log.info(refNav.getName() + " has size of = " + refNav.getModifiers().size());

            while (itrRefMods.hasNext()) {
                EiModifier refmod = (EiModifier) itrRefMods.next();

                // calls List.remove(Object) which will use EiModifier compare() method
                // to find matches in modifier list

                // if we find a match List.remove(Object) will pull the first occurance from the list
                // previously this method 1) used List.indexOf(Object) to check for its existance
                // if found 2) retrieved the object using a List.get(int), then
                // 3) removed the object using List.remove(Object)
                // This consolidates those three calls into one
                resultnav.getModifiers().remove(refmod);

            } // while itrRefMods.hasNext()

            // jam - This was originally inside inner loop
            // if results navigator has 0 modifiers remaining
            // then remove navigator from results
            if (resultnav.getModifiers().size() == 0) {
                // log.info(" REMOVING NAVIGATOR ==> " + resultnav);
                fastnavigators.remove(resultnav);
            }
        } // while itrRefinements.hasNext()

    }

    public List<Database> getDeDupableDBList() {
        ArrayList<Database> dedupableDb = new ArrayList<Database>(EiModifier.DEDUPABLE_MODS.length);
        EiNavigator dbnav = getNavigatorByName(EiNavigator.DB);
        if (dbnav != null) {
            List<?> mods = dbnav.getModifiers();

            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            if ((mods != null) && (mods.size() >= 2)) {
                for (int i = 0; i < EiModifier.DEDUPABLE_MODS.length; i++) {
                    if (mods.contains(EiModifier.DEDUPABLE_MODS[i])) {
                        int modIdx = mods.indexOf(EiModifier.DEDUPABLE_MODS[i]);
                        Database adb = dbConfig.getDatabase(((EiModifier) mods.get(modIdx)).getValue());
                        dedupableDb.add(adb);
                    }
                }
            }
        }
        return dedupableDb;
    }

    public boolean isDeDupable() {
        return ((getDeDupableDBList()).size() >= 2);
    }

    /**
     * Returns a {@link EiNavigator} object that can then be written out to XML for on-screen display or to string format for caching The name argument is the
     * name, or id, of the navigator.
     * <p>
     * This method will return null if a navigator with the matching id cannot be found
     * 
     * @param name
     *            the id of the navigator
     * @return the EiNavigator with the specified id
     * @see EiNavigator
     */
    public EiNavigator getNavigatorByName(String navid) {
        // returning a 'null' object if navigastor does not exist
        EiNavigator navigator = null;
        if (fastnavigators != null) {
            Iterator<EiNavigator> itrnavs = this.fastnavigators.iterator();
            while (itrnavs.hasNext()) {
                EiNavigator anav = (EiNavigator) itrnavs.next();
                if (anav != null) {
                    String strnavid = (String) anav.getName();
                    if (strnavid.equals(navid)) {
                        navigator = anav;
                        break;
                    }
                }
            }
        }
        return navigator;
    }

    public String toString() {

        StringBuffer sb = new StringBuffer();
        Iterator<EiNavigator> itrnavs = fastnavigators.iterator();
        while (itrnavs.hasNext()) {
            EiNavigator navigator = (EiNavigator) itrnavs.next();
            if (navigator != null) {
                sb.append(navigator.toString());
            }

            sb.append(EiNavigator.NAVS_DELIM);
        }
        // log.info(sb.toString());

        return sb.toString();
    }

    public String toXML(ResultsState resultsstate) {
        Map<?, ?> mapstate = new Hashtable<Object, Object>();

        if (resultsstate != null) {
            mapstate = resultsstate.getStateMap();
        }

        StringBuffer sb = new StringBuffer();
        try {
            sb.append("<NAVIGATORS>");
            sb.append("<COMPMASK>");
            sb.append(m_compmask);
            sb.append("</COMPMASK>");

            // drive order off of navigator names list
            Iterator<String> itrnavs = adjustNavigatorOrder().iterator();

            while (itrnavs.hasNext()) {
                String navname = (String) itrnavs.next();

                EiNavigator navigator = getNavigatorByName(navname);
                if (navigator != null) {
                    // log.debug(" " + navigator.getModifiers().size());
                    if (navigator.getModifiers().size() == 0) {
                        continue;
                    }

                    if (EiNavigator.DB.equals(navname)) {
                        // jam - Here we SKIP the DB navigator if it only contains one DB
                        if (navigator.getModifiers().size() == 1) {
                            // log.info(" REMOVING Single Modifier DB NAVIGATOR ");
                            continue;
                        }
                    }

                    String navfield = (String) navigator.getFieldname();

                    int modifiercount = ResultsState.DEFAULT_STATE_COUNT;
                    if (mapstate.containsKey(navfield)) {
                        modifiercount = ((Integer) mapstate.get(navfield)).intValue();
                    }
                    sb.append(navigator.toXML(modifiercount));
                }
            }

            sb.append("</NAVIGATORS>");
            // No more pagers for Navigators - show all available Navigators
            // sb.append(ResultNavigator.getPagers(getNavigators().size(), navigatorcount));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    // Create a JSON String from the navigators
    public JSONArray toJSON(ResultsState resultsstate) {
        Map<?, ?> mapstate = new Hashtable<Object, Object>();
        JSONArray objList = new JSONArray();
        if (resultsstate != null) {
            mapstate = resultsstate.getStateMap();
        }

        StringBuffer sb = new StringBuffer();
        try {
            // drive order off of navigator names list
            Iterator<String> itrnavs = adjustNavigatorOrder().iterator();

            while (itrnavs.hasNext()) {
                String navname = (String) itrnavs.next();

                EiNavigator navigator = getNavigatorByName(navname);
                if (navigator != null) {
                    if (navigator.getModifiers().size() == 0) {
                        continue;
                    }

                    if (EiNavigator.DB.equals(navname)) {
                        // jam - Here we SKIP the DB navigator if it only contains one DB
                        if (navigator.getModifiers().size() == 1) {
                            // log.info(" REMOVING Single Modifier DB NAVIGATOR ");
                            continue;
                        }
                    }

                    String navfield = (String) navigator.getFieldname();
                    int modifiercount = ResultsState.DEFAULT_STATE_COUNT;
                    if (mapstate.containsKey(navfield)) {
                        modifiercount = ((Integer) mapstate.get(navfield)).intValue();
                    }
                    JSONObject jsonObj = navigator.toJSON(modifiercount);
                    objList.put(jsonObj);
                }
            }

            // No more pagers for Navigators - show all available Navigators
            // sb.append(ResultNavigator.getPagers(getNavigators().size(), navigatorcount));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return objList;
    }

    // Create a JSON String from the navigators but only return the field you want
    public JSONArray toJSON(ResultsState resultsstate, String field) {
        Map<?, ?> mapstate = new Hashtable<Object, Object>();
        JSONArray objList = new JSONArray();
        if (resultsstate != null) {
            mapstate = resultsstate.getStateMap();
        }

        StringBuffer sb = new StringBuffer();
        try {
            // drive order off of navigator names list
            Iterator<String> itrnavs = adjustNavigatorOrder().iterator();

            while (itrnavs.hasNext()) {
                String navname = (String) itrnavs.next();

                EiNavigator navigator = getNavigatorByName(navname);
                if (navigator != null) {
                    if (navigator.getModifiers().size() == 0) {
                        continue;
                    }

                    if (EiNavigator.DB.equals(navname)) {
                        // jam - Here we SKIP the DB navigator if it only contains one DB
                        if (navigator.getModifiers().size() == 1) {
                            // log.info(" REMOVING Single Modifier DB NAVIGATOR ");
                            continue;
                        }
                    }

                    String navfield = (String) navigator.getFieldname();
                    if (field.equalsIgnoreCase(navfield)) {

                        // only get the field we want.
                        int modifiercount = ResultsState.DEFAULT_STATE_COUNT;
                        if (mapstate.containsKey(navfield)) {
                            modifiercount = ((Integer) mapstate.get(navfield)).intValue();
                        }
                        JSONObject jsonObj = navigator.toJSON(modifiercount);
                        objList.put(jsonObj);

                    }
                }
            }

            // No more pagers for Navigators - show all available Navigators
            // sb.append(ResultNavigator.getPagers(getNavigators().size(), navigatorcount));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return objList;
    }

    private List<EiNavigator> getFastNavigators(Hashtable<String, List<String[]>> navs, Database[] databases) {
        List<EiNavigator> navigators = new ArrayList<EiNavigator>();
        List<EiModifier> modifiers = new ArrayList<EiModifier>();

        EiModifier modifier;
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
        ConvertModifierTitle convertModTitle = new ConvertModifierTitle();

        if ((navs != null) && !navs.isEmpty()) {
            Iterator<String> itrNavs = navs.keySet().iterator();

            EiNavigator dbnavigator = null;
            while (itrNavs.hasNext()) {
                String navigatorname = (String) itrNavs.next();

                List<String[]> mods = (List<String[]>) navs.get(navigatorname);
                if ((mods != null) && EiNavigator.DB.equalsIgnoreCase(navigatorname)) {
                    dbnavigator = EiNavigator.createNavigator(navigatorname);

                    modifiers = new ArrayList<EiModifier>();

                    // loop through all databases in query
                    // possibly missing parent databases in subcats count
                    // so we have to add it in with value of 0
                    // so upgrade will not be skipped
                    Iterator<String[]> itrmod;
                    for (int i = 0; i < databases.length; i++) {
                        Database backfile = databases[i].getBackfile();
                        if (backfile != null) {
                            boolean foundBackfile = false, foundParent = false;
                            itrmod = mods.iterator();
                            while (itrmod.hasNext()) {
                                String[] mod = (String[]) itrmod.next();
                                if (databases[i].getIndexName().equals(mod[0])) {
                                    foundParent = true;
                                }
                                if (backfile.getIndexName().equals(mod[0])) {
                                    foundBackfile = true;
                                }
                            }
                            itrmod = null;
                            if (!foundParent && foundBackfile) {
                                // log.debug(" added missing DB navigator " + databases[i].getIndexName());

                                mods.add(new String[] { databases[i].getIndexName(), "0" });
                            }
                        }

                    }

                    Collections.sort(mods, new DBModifierComparator());

                    itrmod = mods.iterator();
                    while (itrmod.hasNext()) {
                        String[] mod = (String[]) itrmod.next();
                        String dbid = mod[0];

                        if (dbid != null) {
                            dbid = dbid.substring(0, 3);
                        }

                        Database adb = dbConfig.getDatabase(dbid);

                        if (adb == null) {
                            // log.error(" SKIPPED NULL DB ==> " + dbid);
                            continue;
                        }
                        if (adb.isBackfile()) {
                            // log.debug(" SKIPPED BACKFILE DB ==> " + dbid);
                            continue;
                        }
                        int dbcount = Integer.parseInt(mod[1]);

                        // log.debug(" DB ==> " + dbid + "==" + dbcount);

                        Database backfile = adb.getBackfile();
                        if (backfile != null) {
                            Iterator<String[]> itrback = mods.iterator();
                            while (itrback.hasNext()) {
                                String[] back = (String[]) itrback.next();
                                if (back[0].equals(backfile.getID())) {
                                    dbcount += Integer.parseInt(back[1]);
                                    // log.debug(" adding backfile ==> " + back[0] + "==" + back[1]);
                                    break;
                                }
                            }
                            // log.debug(" DB ==> " + dbid + "==" + dbcount);
                        }

                        modifier = dbnavigator.createModifier(dbcount, adb.getName(), adb.getIndexName());
                        modifiers.add(modifier);
                    }
                    dbnavigator.setModifiers(modifiers);
                    navigators.add(dbnavigator);
                } else if (mods != null) {
                    EiNavigator navigator = EiNavigator.createNavigator(navigatorname);
                    modifiers = new ArrayList<EiModifier>();
                    for (int i = 0; i < mods.size(); i++) {
                        String[] mod = (String[]) mods.get(i);
                        int modcount = 0;
                        try {
                            modcount = (Integer.parseInt(mod[1]));
                        } catch (NumberFormatException nfe) {
                            modcount = 0;
                        }

                        String value = convertModTitle.getValue(mod[0], navigatorname);
                        String modvalue = value;
                        String modlabel = value;
                        String title = convertModTitle.getTitle(mod[0], navigatorname, dbConfig);

                        if (title != null) {
                            if (REMOVE_MODIFIER.equals(title)) {
                                // skip modifiers returned from getTitle with title REMOVE_MODIFIER
                                continue;
                            }
                            modlabel = title;
                        } else {
                            modlabel = mod[0];
                        }
                        modifier = navigator.createModifier(modcount, modlabel, modvalue);

                        modifiers.add(modifier);

                    }
                    // System.out.println(navigatorname + " ==> " + modifiers);
                    navigator.setModifiers(modifiers);
                    navigators.add(navigator);
                } else {
                    // System.out.println(" skipped NULL Fast INavigator ==> " + navigatorname);
                }
            } // whilem

        } // if (navs != null)

        return navigators;
    }

    // Parsing Routines !!!
    public static List<EiNavigator> getNavs(String navigatorsstring) {
        List<EiNavigator> navigators = new ArrayList<EiNavigator>();

        // create from string
        if (navigatorsstring != null) {
            String[] navs = navigatorsstring.split(EiNavigator.NAVS_DELIM);
            for (int i = 0; i < navs.length; i++) {
                navigators.add(EiNavigator.parseNavigator(navs[i]));
            }
        }
        return navigators;
    }

    public static String getPagers(int totalsize, int currentcount) {
        StringBuffer sb = new StringBuffer();
        sb.append("<NAVPAGER FIELD=\"PAGE\">");
        if (currentcount > ResultsState.DEFAULT_STATE_COUNT) {
            sb.append("<LESS COUNT=\"").append(currentcount / ResultsState.DEFAULT_FACTOR).append("\"/>");
        }
        if (totalsize > currentcount) {
            sb.append("<MORE COUNT=\"").append(currentcount * ResultsState.DEFAULT_FACTOR).append("\"/>");
        }
        sb.append("</NAVPAGER>");
        return sb.toString();

    }

    private class DBModifierComparator implements Comparator<Object> {
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();

        public int compare(Object o1, Object o2) {
            String o1s = ((String[]) o1)[0];
            String o2s = ((String[]) o2)[0];
            if (o1s != null) {
                o1s = o1s.substring(0, 3);
            }
            Database o1db = dbConfig.getDatabase(o1s);

            if (o2s != null) {
                o2s = o2s.substring(0, 3);
            }
            Database o2db = dbConfig.getDatabase(o2s);

            return o1db.compareTo(o2db);
        }

        public boolean equals(Object obj) {
            return (compare(this, obj) == 0);
        }

		@Override
		public Comparator<Object> reversed() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator<Object> thenComparing(Comparator<? super Object> other) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <U> Comparator<Object> thenComparing(
				Function<? super Object, ? extends U> keyExtractor,
				Comparator<? super U> keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <U extends Comparable<? super U>> Comparator<Object> thenComparing(
				Function<? super Object, ? extends U> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator<Object> thenComparingInt(
				ToIntFunction<? super Object> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator<Object> thenComparingLong(
				ToLongFunction<? super Object> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator<Object> thenComparingDouble(
				ToDoubleFunction<? super Object> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
			// TODO Auto-generated method stub
			return null;
		}
		public <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> nullsFirst(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> nullsLast(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T, U> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor,
				Comparator<? super U> keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T, U extends Comparable<? super U>> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> comparingInt(
				ToIntFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> comparingLong(
				ToLongFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> comparingDouble(
				ToDoubleFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
    }

    private List<String> adjustNavigatorOrder() {
        // get default order
        List<String> neworder = EiNavigator.getNavigatorNames();

        if ((getCompositionMask() == DatabaseConfig.EUP_MASK) || (getCompositionMask() == DatabaseConfig.UPA_MASK)
            || (getCompositionMask() == (DatabaseConfig.UPA_MASK + DatabaseConfig.EUP_MASK))) {
            neworder = new ArrayList<String>();
            neworder.add(EiNavigator.DB);
            neworder.add(EiNavigator.DT);
            neworder.add(EiNavigator.AU);
            neworder.add(EiNavigator.AF);
            neworder.add(EiNavigator.PUC);
            neworder.add(EiNavigator.PEC);
            neworder.add(EiNavigator.PID);
            neworder.add(EiNavigator.CO);
            neworder.add(EiNavigator.CV);
            neworder.add(EiNavigator.CL);
            neworder.add(EiNavigator.YR);
            neworder.add(EiNavigator.LA);
        }
        if (getCompositionMask() == DatabaseConfig.EPT_MASK) {
            neworder = new ArrayList<String>();
            neworder.add(EiNavigator.AU);
            neworder.add(EiNavigator.AF);
            neworder.add(EiNavigator.CV);
            neworder.add(EiNavigator.CVM);
            neworder.add(EiNavigator.PID);
            neworder.add(EiNavigator.PAC);
            neworder.add(EiNavigator.RO);
            neworder.add(EiNavigator.LA);
            neworder.add(EiNavigator.YR);
        }
        if (getCompositionMask() == DatabaseConfig.PAG_MASK) {
            neworder = new ArrayList<String>();
            neworder.add(EiNavigator.CL);
            neworder.add(EiNavigator.BKT);
            neworder.add(EiNavigator.BKS);
            neworder.add(EiNavigator.KY);
            neworder.add(EiNavigator.AU);
            neworder.add(EiNavigator.PN);
        }
        if (getCompositionMask() == DatabaseConfig.CBN_MASK) {
            neworder = new ArrayList<String>();
            neworder.add(EiNavigator.CP);
            neworder.add(EiNavigator.CM);
            neworder.add(EiNavigator.CV);
            neworder.add(EiNavigator.IC);
            neworder.add(EiNavigator.GD);
            neworder.add(EiNavigator.YR);
            neworder.add(EiNavigator.CO);
            neworder.add(EiNavigator.LA);
            neworder.add(EiNavigator.ST);
            neworder.add(EiNavigator.PN);
            neworder.add(EiNavigator.DT);
        }
        if ((getCompositionMask() == DatabaseConfig.PCH_MASK) || (getCompositionMask() == DatabaseConfig.CHM_MASK)
            || (getCompositionMask() == DatabaseConfig.PCH_MASK + DatabaseConfig.CHM_MASK)) {
            // For PaperChem and Chimica, move the uncontrolled terms navigator so that it is directly underneath controlled vocabulary.
            neworder = new ArrayList<String>();
            neworder.add(EiNavigator.DB);
            neworder.add(EiNavigator.AU);
            neworder.add(EiNavigator.AF);
            neworder.add(EiNavigator.CV);
            neworder.add(EiNavigator.FL);
            neworder.add(EiNavigator.CL);
            neworder.add(EiNavigator.CO);
            neworder.add(EiNavigator.DT);
            neworder.add(EiNavigator.LA);
            neworder.add(EiNavigator.YR);
            neworder.add(EiNavigator.ST);
            neworder.add(EiNavigator.PN);
        }
        return neworder;
    }

    private void cleanCLFacet() {
        EiNavigator anav = getNavigatorByName(EiNavigator.CL);
        if (anav != null) {
            anav = BookNavigator.cleanCLNavigator(anav);
            addNavigator(anav);
        }
    }

    private void addNavigator(EiNavigator nav) {
        fastnavigators.add(nav);
    }

    private void adjustNavigatorName(String navigatorName, String displayName) {
        EiNavigator nav = getNavigatorByName(navigatorName);
        if (nav != null) {
            nav.setDisplayname(displayName);
        }
    }

    private void adjustNavigatorProperties(String name, EiNavigator nav) {
        EiNavigator facet = getNavigatorByName(name);
        if (facet != null) {
            facet.setDisplayname(nav.getDisplayname());
            facet.setFieldname(nav.getFieldname());
            facet.setName(nav.getName());
        }
    }

    private void removeNavigator(String navigatorName) {
        EiNavigator nav = getNavigatorByName(navigatorName);
        if (nav != null) {
            fastnavigators.remove(nav);
        }
    }

    private void removeModifier(String navigatorName, EiModifier modifier) {
        EiNavigator navigator = getNavigatorByName(navigatorName);
        if (navigator != null) {
            navigator.getModifiers().remove(modifier);
        }
    }

    /*
     * This method takes a navighator and created a generic copy of it by not using the EiNavigator.createNvavigator() static "factory" method of the base type
     * EiNavigator. This is useful for removing Navigator specific behaviour that exists for Patent navigators that are reused for other non patent codes and do
     * not need translation or lookup for mouse overs, etc.
     */
    /* Currently it is used for refashioning navigators for CBNB */
    private EiNavigator copyNavigator(EiNavigator anav) {
        return copyNavigator(anav, new EiNavigator(anav.getName()));
    }

    private EiNavigator copyNavigator(EiNavigator anav, EiNavigator newnav) {
        List<EiModifier> copiedmods = new ArrayList<EiModifier>();
        Iterator<EiModifier> itrMods = anav.getModifiers().iterator();
        while (itrMods.hasNext()) {
            EiModifier amod = (EiModifier) itrMods.next();
            // skip any empty modifiers
            if ((amod != null) && (amod.getLabel().length() != 0)) {
                EiModifier newmod = null;
                newmod = newnav.createModifier(amod.getCount(), amod.getLabel(), amod.getValue());
                if (newmod != null) {
                    copiedmods.add(newmod);
                }
            }
        }

        newnav.setModifiers(copiedmods);

        return newnav;
    }
}