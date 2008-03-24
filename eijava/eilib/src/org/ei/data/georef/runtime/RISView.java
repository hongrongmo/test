package org.ei.data.georef.runtime;

import org.ei.domain.Key;
import org.ei.domain.Keys;
import org.ei.domain.RIS;
import java.sql.*;
import java.io.*;

import org.ei.domain.EIDoc;
import org.ei.domain.DocID;
import org.ei.domain.ElementDataMap;
import org.ei.domain.ElementData;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;


    public class RISView extends DetailedView {

        private Key[] keys = new Key[]{Keys.RIS_TY, // DocType
                                        Keys.RIS_LA, // Language
                                        Keys.RIS_N1, // Copyright
                                        Keys.RIS_TI, // Title
                                        Keys.RIS_T1, // Tranlated title
                                        Keys.RIS_BT, // Monograph title
                                        Keys.RIS_JO,  // Serial title
                                        Keys.RIS_T3,  //  Serial title if ! a journal
                                        Keys.RIS_AUS, // Authors
                                        Keys.RIS_AD, // Aff
                                        Keys.RIS_EDS, // Editors
                                        Keys.RIS_VL, // Volume
                                        Keys.RIS_IS, // Issue
                                        Keys.RIS_PY,  // Year
                                        Keys.RIS_AN, // ACCESSION_NUMBER
                                        Keys.RIS_SP,  // Start Pg
                                        Keys.RIS_EP,  // End Pg
                                        Keys.RIS_SN, // ISSN
                                        Keys.RIS_S1, // ISBN
                                        Keys.RIS_MD, // Meeting Date?
                                        Keys.RIS_CY,  // Conference ?
                                        Keys.RIS_PB,  // Publisher
                                        Keys.RIS_N2, // Abstract
                                        Keys.RIS_KW, // Main Heading
                                        Keys.RIS_CVS, // CV
                                        Keys.RIS_FLS, // FL
                                        Keys.RIS_DO, // DOI
                                        Keys.BIB_TY };

      public String getFormat() { return RIS.RIS_FORMAT; }

      public EIDoc buildDocument(ResultSet rset, DocID did)
                throws Exception
      {
        EIDoc adoc = super.buildDocument(rset, did);
        ElementDataMap adoc_ht =  adoc.getElementDataMap();

        /*Collection keySet = adoc_ht.keySet();
        Iterator itrKeys = keySet.iterator();
        while(itrKeys.hasNext())
        {
          Key key = (Key) itrKeys.next();
          Writer w = new StringWriter();
          ((ElementData) adoc_ht.get(key)).toXML(w);
        }*/

        ElementDataMap ris_ht =  new ElementDataMap();
        ris_ht.put(Keys.RIS_TY, ((ElementData) adoc_ht.get(Keys.DOC_TYPE)));
        ris_ht.put(Keys.RIS_LA, ((ElementData) adoc_ht.get(Keys.LANGUAGE)));
        ris_ht.put(Keys.RIS_N1, ((ElementData) adoc_ht.get(Keys.COPYRIGHT_TEXT)));
        ris_ht.put(Keys.RIS_TI, ((ElementData) adoc_ht.get(Keys.TITLE)));
        ris_ht.put(Keys.RIS_T1, ((ElementData) adoc_ht.get(Keys.TITLE_TRANSLATION)));
        ris_ht.put(Keys.RIS_BT, ((ElementData) adoc_ht.get(Keys.MONOGRAPH_TITLE)));
//      Keys.RIS_JO,  // Serial title
//      Keys.RIS_T3,  //  Serial title if ! a journal
        ris_ht.put(Keys.RIS_AUS, ((ElementData) adoc_ht.get(Keys.AUTHORS)));
        ris_ht.put(Keys.RIS_AD, ((ElementData) adoc_ht.get(Keys.AUTHOR_AFFS)));
        ris_ht.put(Keys.RIS_EDS, ((ElementData) adoc_ht.get(Keys.EDITORS)));
        ris_ht.put(Keys.RIS_VL, ((ElementData) adoc_ht.get(Keys.VOLUME)));
        ris_ht.put(Keys.RIS_IS, ((ElementData) adoc_ht.get(Keys.ISSUE)));
        ris_ht.put(Keys.RIS_PY, ((ElementData) adoc_ht.get(Keys.PUBLICATION_YEAR)));

        ris_ht.put(Keys.RIS_AN, ((ElementData) adoc_ht.get(Keys.ACCESSION_NUMBER)));
//      Keys.RIS_SP, // Start Page
//      Keys.RIS_EP, // End Pg
        ris_ht.put(Keys.RIS_SN, ((ElementData) adoc_ht.get(Keys.ISSN)));
        ris_ht.put(Keys.RIS_S1, ((ElementData) adoc_ht.get(Keys.ISBN)));
//      Keys.RIS_MD, // Meeting Date?
//      Keys.RIS_CY, // Conference ?
//      Keys.RIS_PB, // Publisher
        ris_ht.put(Keys.RIS_N2, ((ElementData) adoc_ht.get(Keys.ABSTRACT)));
//      Keys.RIS_KW, // Main Heading
        ris_ht.put(Keys.RIS_CVS, ((ElementData) adoc_ht.get(Keys.INDEX_TERM)));
        ris_ht.put(Keys.RIS_FLS, ((ElementData) adoc_ht.get(Keys.UNCONTROLLED_TERMS)));
        ris_ht.put(Keys.RIS_DO, ((ElementData) adoc_ht.get(Keys.DOI)));

        Collection keySet = ris_ht.keySet();
        Iterator itrKeys = keySet.iterator();
        while(itrKeys.hasNext())
        {
          Key key = (Key) itrKeys.next();
          Writer w = new StringWriter();
          //((ElementData) adoc_ht.get(key)).toXML(w);
          System.out.println(" key: " +  key.getKey());
        }


        EIDoc risdoc = new EIDoc(did, ris_ht, getFormat());
        risdoc.setOutputKeys(getKeys());

        return risdoc;
      }
}
