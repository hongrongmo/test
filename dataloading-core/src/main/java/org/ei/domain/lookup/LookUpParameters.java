package org.ei.domain.lookup;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.ei.domain.DatabaseConfig;

public class LookUpParameters {
    
    /**
     * Create the XML for lookup parameters
     * @param sessionID
     * @param database
     * @param searchtype
     * @return
     * @throws IOException
     */
    public static String lookupParametersToXML(String sessionID, String database, String searchtype) throws IOException {
        int selectedDbMask = Integer.parseInt(database);
        
        int CPX = DatabaseConfig.CPX_MASK;
        int INSPEC = DatabaseConfig.INS_MASK;
        int NTIS = DatabaseConfig.NTI_MASK;
        int USPTO = DatabaseConfig.USPTO_MASK;
        int CRC = DatabaseConfig.CRC_MASK;
        int C84 = 33; // NOT 32???
        int PCH = DatabaseConfig.PCH_MASK;
        int CHM = DatabaseConfig.CHM_MASK;
        int CBN = DatabaseConfig.CBN_MASK;
        int ELT = DatabaseConfig.ELT_MASK;
        int EPT = DatabaseConfig.EPT_MASK;
        int IBF = DatabaseConfig.IBF_MASK;
        int GEO = DatabaseConfig.GEO_MASK;
        int EU_PATENTS = DatabaseConfig.EUP_MASK;
        int US_PATENTS = DatabaseConfig.UPA_MASK;
        int US_EU_PATENTS = US_PATENTS + EU_PATENTS;
        int REF = DatabaseConfig.REF_MASK;
        int PAG = DatabaseConfig.PAG_MASK;
        int CBF = DatabaseConfig.CBF_MASK;
        int IBS = DatabaseConfig.IBS_MASK;
        int GRF = DatabaseConfig.GRF_MASK;
        
        Writer xmlout = new StringWriter();
        xmlout.write("<FIELDS>");
        
        // AU
        if (selectedDbMask != CBN)
        {
            if ((selectedDbMask & US_PATENTS) != US_PATENTS &&
                (selectedDbMask & EU_PATENTS) != EU_PATENTS)
            {
                xmlout.write("<FIELD SHORTNAME=\"AUS\" DISPLAYNAME=\"Author\" />");
            }
            else if (selectedDbMask == US_PATENTS ||
                selectedDbMask == EU_PATENTS ||
                selectedDbMask == EU_PATENTS + US_PATENTS)
            {
                xmlout.write("<FIELD SHORTNAME=\"AUS\" DISPLAYNAME=\"Inventor\" />");
            }
            else
            {
                xmlout.write("<FIELD SHORTNAME=\"AUS\" DISPLAYNAME=\"Author/Inventor\"/>");
            }
        }
        
        // IPC Code
        if ((selectedDbMask & INSPEC) == INSPEC)
        {
            xmlout.write("<FIELD SHORTNAME=\"PID\" DISPLAYNAME=\"IPC Code\"/>");
        }
        
        // AF
        if (selectedDbMask != CBN)
        {
            if ((selectedDbMask & PAG) != PAG &&
                (selectedDbMask & US_PATENTS) != US_PATENTS &&
                (selectedDbMask & EPT) != EPT &&
                (selectedDbMask & EU_PATENTS) != EU_PATENTS)
            {
                xmlout.write("<FIELD SHORTNAME=\"AF\" DISPLAYNAME=\"Author affiliation\"/>");
            }
            else if (selectedDbMask == US_PATENTS ||
                selectedDbMask == EU_PATENTS ||
                selectedDbMask == EPT ||
                selectedDbMask == EU_PATENTS + US_PATENTS)
            {
                xmlout.write("<FIELD SHORTNAME=\"AF\" DISPLAYNAME=\"Assignee\"/>");
            }
            else if ((selectedDbMask & PAG) != PAG)
            {
                xmlout.write("<FIELD SHORTNAME=\"AF\" DISPLAYNAME=\"Affiliation/Assignee\"/>");
            }
        }
        
        if (searchtype.equals("Quick"))
        {
            // CVS
            if (selectedDbMask == CPX ||
                selectedDbMask == CBF)
            {
                xmlout.write("<FIELD SHORTNAME=\"CVS\" DISPLAYNAME=\"Ei Controlled Term\"/>");
            }
            else if (selectedDbMask == INSPEC ||
                selectedDbMask == IBS)
            {
                xmlout.write("<FIELD SHORTNAME=\"CVS\" DISPLAYNAME=\"Inspec Controlled Term\"/>");
            }
            else if (selectedDbMask == NTIS)
            {
                xmlout.write("<FIELD SHORTNAME=\"CVS\" DISPLAYNAME=\"NTIS Controlled Term\"/>");
            }
            else if ((selectedDbMask & PAG) != PAG &&
                (selectedDbMask & US_PATENTS) != US_PATENTS &&
                (selectedDbMask & EU_PATENTS) != EU_PATENTS)
            {
                xmlout.write("<FIELD SHORTNAME=\"CVS\" DISPLAYNAME=\"Controlled Term\"/>");
            }
            
            // Patent Country
            if ((selectedDbMask & EPT) == EPT)
            {
                xmlout.write("<FIELD SHORTNAME=\"PC\" DISPLAYNAME=\"Country\"/>");
            }
            
            // ST
            if ((selectedDbMask & PAG) != PAG &&
                (selectedDbMask & US_PATENTS) != US_PATENTS &&
                (selectedDbMask & EU_PATENTS) != EU_PATENTS &&
                (selectedDbMask & EPT) != EPT &&
                (selectedDbMask & NTIS) != NTIS)
            {
                xmlout.write("<FIELD SHORTNAME=\"ST\" DISPLAYNAME=\"Source title\"/>");
            }
            
            // PB
            if ((selectedDbMask & GEO) != GEO &&
                (selectedDbMask & US_PATENTS) != US_PATENTS &&
                (selectedDbMask & EU_PATENTS) != EU_PATENTS &&
                (selectedDbMask & EPT) != EPT &&
                (selectedDbMask & NTIS) != NTIS &&
                (selectedDbMask & CBN) != CBN &&
                (selectedDbMask & CHM) != CHM)
            {
                xmlout.write("<FIELD SHORTNAME=\"PN\" DISPLAYNAME=\"Publisher\"/>");
            }
        }
        if (!searchtype.equals("Quick"))
        {
            
            // CLS
            if (((selectedDbMask & CPX) == CPX) ||
                ((selectedDbMask & CBF) == CBF) ||
                ((selectedDbMask & INSPEC) == INSPEC) ||
                ((selectedDbMask & IBS) == IBS) ||
                ((selectedDbMask & GEO) == GEO) ||
                ((selectedDbMask & GRF) == GRF) ||
                ((selectedDbMask & PCH) == PCH) ||
                ((selectedDbMask & CHM) == CHM) ||
                ((selectedDbMask & EPT) == EPT) ||
                ((selectedDbMask & ELT) == ELT) ||
                ((selectedDbMask & CBN) == CBN) ||
                ((selectedDbMask & NTIS) == NTIS))
            {
                xmlout.write("<FIELD SHORTNAME=\"CVS\" DISPLAYNAME=\"Controlled term\"/>");
            }
            
            // Patent Country
            
            if ((selectedDbMask & EPT) == EPT)
            {
                xmlout.write("<FIELD SHORTNAME=\"PC\" DISPLAYNAME=\"Country\"/>");
            }
            
            // LA
            if (((selectedDbMask & CPX) == CPX) ||
                ((selectedDbMask & CBF) == CBF) ||
                ((selectedDbMask & INSPEC) == INSPEC) ||
                ((selectedDbMask & IBS) == IBS) ||
                ((selectedDbMask & GEO) == GEO) ||
                ((selectedDbMask & GRF) == GRF) ||
                ((selectedDbMask & PCH) == PCH) ||
                ((selectedDbMask & CHM) == CHM) ||
                ((selectedDbMask & EPT) == EPT) ||
                ((selectedDbMask & ELT) == ELT) ||
                ((selectedDbMask & CBN) == CBN) ||
                ((selectedDbMask & NTIS) == NTIS))
            {
                xmlout.write("<FIELD SHORTNAME=\"LA\" DISPLAYNAME=\"Language\"/>");
            }
            
            // ST
            if (((selectedDbMask & CPX) == CPX) ||
                ((selectedDbMask & CBF) == CBF) ||
                ((selectedDbMask & GEO) == GEO) ||
                ((selectedDbMask & GRF) == GRF) ||
                ((selectedDbMask & PCH) == PCH) ||
                ((selectedDbMask & CHM) == CHM) ||
                ((selectedDbMask & ELT) == ELT) ||
                ((selectedDbMask & CBN) == CBN) ||
                ((selectedDbMask & INSPEC) == INSPEC) ||
                ((selectedDbMask & IBS) == IBS))
            {
                xmlout.write("<FIELD SHORTNAME=\"ST\" DISPLAYNAME=\"Source title\"/>");
            }
            
            // DT
            if (((selectedDbMask & CPX) == CPX) ||
                ((selectedDbMask & CBF) == CBF) ||
                ((selectedDbMask & GEO) == GEO) ||
                ((selectedDbMask & GRF) == GRF) ||
                ((selectedDbMask & PCH) == PCH) ||
                ((selectedDbMask & CHM) == CHM) ||
                ((selectedDbMask & ELT) == ELT) ||
                ((selectedDbMask & CBN) == CBN) ||
                ((selectedDbMask & INSPEC) == INSPEC) ||
                ((selectedDbMask & IBS) == IBS))
            {
                xmlout.write("<FIELD SHORTNAME=\"DT\" DISPLAYNAME=\"Document type\"/>");
            }
            
            // PN
            if (((selectedDbMask & CPX) == CPX) ||
                ((selectedDbMask & CBF) == CBF) ||
                ((selectedDbMask & GRF) == GRF) ||
                ((selectedDbMask & PCH) == PCH) ||
                ((selectedDbMask & ELT) == ELT) ||
                ((selectedDbMask & INSPEC) == INSPEC) ||
                ((selectedDbMask & IBS) == IBS))
            {
                xmlout.write("<FIELD SHORTNAME=\"PN\" DISPLAYNAME=\"Publisher\"/>");
            }
            
            // TR
            if (((selectedDbMask & CPX) == CPX) || ((selectedDbMask & INSPEC) == INSPEC) || ((selectedDbMask & IBS) == IBS))
            {
                xmlout.write("<FIELD SHORTNAME=\"TR\" DISPLAYNAME=\"Treatment type\"/>");
            }
            
            // DSC
            if (((selectedDbMask & INSPEC) == INSPEC) || ((selectedDbMask & IBS) == IBS))
            {
                xmlout.write("<FIELD SHORTNAME=\"DI\" DISPLAYNAME=\"Discipline\"/>");
            }
            
        }
        xmlout.write("</FIELDS>");
        
        return xmlout.toString();
    }
}
