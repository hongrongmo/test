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

      public Key[] getKeys() {
        return super.getKeys();
      }

      public String getFormat() { return RIS.RIS_FORMAT; }

      public RISView()
      {
        this.ris_ht = new ElementDataMap();
      }

      private ElementDataMap ris_ht;
      private ElementDataMap getRisElementDataMap() {
        return this.ris_ht;
      }

      private ElementDataMap doc_ht;
      private void setDocumentElementDataMap(ElementDataMap ht) {
        this.doc_ht = ht;
      }
      private ElementDataMap getDocumentElementDataMap() {
        return this.doc_ht;
      }

      public EIDoc buildDocument(ResultSet rset, DocID did)
                throws Exception
      {
        EIDoc adoc = super.buildDocument(rset, did);

        // Get EV system DOC_TYPE codes for indexing and append them to (or use in favor of ?) the GeoRef values
        String mappingcode = createColumnValueField("DOCUMENT_TYPE").getValue().concat(GRFDocBuilder.AUDELIMITER).concat(createColumnValueField("BIBLIOGRAPHIC_LEVEL_CODE").getValue());
        if(mappingcode != null)
        {
          // DocumentTypeMappingDecorator takes <DOCTYPE>AUDELIMITER<BIBCODE> String as field argument
          mappingcode = new DocumentTypeMappingDecorator(mappingcode).getValue();
          addDocumentValue(Keys.DOC_TYPE, adoc.replaceTYwithRIScode(mappingcode));
        }

        ElementDataMap adoc_ht =  adoc.getElementDataMap();
        setDocumentElementDataMap(adoc_ht);

        changeElementKey(Keys.DOC_TYPE, Keys.RIS_TY);
        changeElementKey(Keys.LANGUAGE, Keys.RIS_LA);
        changeElementKey(Keys.COPYRIGHT_TEXT, Keys.RIS_N1);
        changeElementKey(Keys.TITLE, Keys.RIS_TI);
        changeElementKey(Keys.TITLE_TRANSLATION, Keys.RIS_T1);
        changeElementKey(Keys.MONOGRAPH_TITLE, Keys.RIS_BT);
        //      Keys.RIS_JO,  // Serial title
        //      Keys.RIS_T3,  //  Serial title if ! a journal
        changeElementKey(Keys.AUTHORS, Keys.RIS_AUS);
        changeElementKey(Keys.AUTHOR_AFFS, Keys.RIS_AD);
        changeElementKey(Keys.EDITORS, Keys.RIS_EDS);
        changeElementKey(Keys.VOLUME, Keys.RIS_VL);
        changeElementKey(Keys.ISSUE, Keys.RIS_IS);
        changeElementKey(Keys.PUBLICATION_YEAR, Keys.RIS_PY);

        changeElementKey(Keys.ACCESSION_NUMBER, Keys.RIS_AN);
        //      Keys.RIS_SP, // Start Page
        //      Keys.RIS_EP, // End Pg
        changeElementKey(Keys.ISSN, Keys.RIS_SN);
        changeElementKey(Keys.ISBN, Keys.RIS_S1);
        //      Keys.RIS_MD, // Meeting Date?
        //      Keys.RIS_CY, // Conference ?
        //      Keys.RIS_PB, // Publisher
        changeElementKey(Keys.ABSTRACT, Keys.RIS_N2);
        //      Keys.RIS_KW, // Main Heading
        changeElementKey(Keys.INDEX_TERM, Keys.RIS_CVS);
        changeElementKey(Keys.UNCONTROLLED_TERMS, Keys.RIS_FLS);
        changeElementKey(Keys.DOI, Keys.RIS_DO);

        Collection keySet = ris_ht.keySet();
        Iterator itrKeys = keySet.iterator();
        while(itrKeys.hasNext())
        {
          Key key = (Key) itrKeys.next();
          Writer w = new StringWriter();
          ElementData eldata = ((ElementData) ris_ht.get(key));
          if(eldata != null)
          {
            eldata.toXML(w);
            System.out.println(" key: " +  key.getKey() + " == " + w.toString());
          }
          else
          {
            System.out.println(" key: " +  key.getKey() + " NO DATA");
          }
        }


        EIDoc risdoc = new EIDoc(did, ris_ht, getFormat());
        risdoc.setOutputKeys(this.keys);

        return risdoc;
      }

    private void changeElementKey(Key oldkey, Key newkey)
    {
      ElementData edata = (ElementData) getDocumentElementDataMap().get(oldkey);
      if(edata != null)
      {
        edata.setKey(newkey);
        ris_ht.put(newkey, edata);
      }

      return;
    }

}
