package org.ei.common.georef;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ei.domain.Contributors;
import org.ei.domain.DocID;
import org.ei.domain.EIDoc;
import org.ei.domain.ElementData;
import org.ei.domain.ElementDataMap;
import org.ei.domain.Key;
import org.ei.domain.Keys;
import org.ei.domain.RIS;
import org.ei.domain.XMLWrapper;

import org.ei.common.*;


public class RISView extends DetailedView {

      private static final Key[] ris_keys = new Key[]{Keys.RIS_TY, // DocType
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
                                      Keys.RIS_CY,  // City of publication
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

      public EIDoc buildDocument(ResultSet rset, DocID did) throws SQLException {
        EIDoc adoc = super.buildDocument(rset, did);
        ElementDataMap adoc_ht =  adoc.getElementDataMap();

        ElementDataMap ris_ht = new ElementDataMap();

        // Get EV system DOC_TYPE codes for indexing and append them to (or use in favor of ?) the GeoRef values
        String doctype = createColumnValueField("DOCUMENT_TYPE").getValue();
        String bibcode = createColumnValueField("BIBLIOGRAPHIC_LEVEL_CODE").getValue();
        String mappingcode = null;
        if((doctype == null) || (bibcode == null)) {
          mappingcode = "S".concat(Constants.AUDELIMITER).concat("A");
        }
        else {
          mappingcode = doctype.concat(Constants.AUDELIMITER).concat(bibcode);
        }

        if(mappingcode != null)
        {
          // DocumentTypeMappingDecorator takes <DOCTYPE>AUDELIMITER<BIBCODE> String as field argument
          mappingcode = new RisDocumentTypeMappingDecorator(new DocumentTypeMappingDecorator(mappingcode)).getValue();
          addDocumentValue(Keys.DOC_TYPE, mappingcode);
        }

        changeElementKey(ris_ht, adoc_ht, Keys.DOC_TYPE, Keys.RIS_TY);
        changeElementKey(ris_ht, adoc_ht, Keys.LANGUAGE, Keys.RIS_LA);
        changeElementKey(ris_ht, adoc_ht, Keys.COPYRIGHT_TEXT, Keys.RIS_N1);
        changeElementKey(ris_ht, adoc_ht, Keys.TITLE, Keys.RIS_TI);
        changeElementKey(ris_ht, adoc_ht, Keys.TITLE_TRANSLATION, Keys.RIS_T1);
        changeElementKey(ris_ht, adoc_ht, Keys.MONOGRAPH_TITLE, Keys.RIS_BT);
        if(mappingcode != null)
        {
          if(mappingcode.equals("JOUR"))
          {
            changeElementKey(ris_ht, adoc_ht, Keys.SERIAL_TITLE, Keys.RIS_JO);
          }
          else
          {
            changeElementKey(ris_ht, adoc_ht, Keys.SERIAL_TITLE, Keys.RIS_T3);
          }
        }
        Contributors contributors = (Contributors) adoc_ht.get(Keys.AUTHORS);
        if(contributors != null)
        {
          contributors.setFirstAffiliation(null);
        }

        changeElementKey(ris_ht, adoc_ht, Keys.AUTHORS, Keys.RIS_AUS);
        changeElementKey(ris_ht, adoc_ht, Keys.AUTHOR_AFFS, Keys.RIS_AD);
        changeElementKey(ris_ht, adoc_ht, Keys.EDITORS, Keys.RIS_EDS);
        changeElementKey(ris_ht, adoc_ht, Keys.VOLUME, Keys.RIS_VL);
        changeElementKey(ris_ht, adoc_ht, Keys.ISSUE, Keys.RIS_IS);
        changeElementKey(ris_ht, adoc_ht, Keys.PUBLICATION_YEAR, Keys.RIS_PY);
        changeElementKey(ris_ht, adoc_ht, Keys.ACCESSION_NUMBER, Keys.RIS_AN);

        String pages = getPages();
        if(pages != null)
        {
          Pattern patternPages = Pattern.compile("^(\\d+)\\D+(\\d+)$");
          Matcher pagematch = patternPages.matcher(pages);
          if(pagematch.find())
          {
            ris_ht.put(Keys.RIS_SP, new XMLWrapper(Keys.RIS_SP, pagematch.group(1)));
            ris_ht.put(Keys.RIS_EP, new XMLWrapper(Keys.RIS_EP, pagematch.group(2)));
          }
          else
          {
            ris_ht.put(Keys.RIS_SP, new XMLWrapper(Keys.RIS_SP, pages));
          }
        }

        changeElementKey(ris_ht, adoc_ht, Keys.ISSN, Keys.RIS_SN);
        changeElementKey(ris_ht, adoc_ht, Keys.ISBN, Keys.RIS_S1);
        //      Keys.RIS_MD, //
        //      Keys.RIS_CY, //
        changeElementKey(ris_ht, adoc_ht, Keys.PUBLISHER, Keys.RIS_PB);
        changeElementKey(ris_ht, adoc_ht, Keys.ABSTRACT, Keys.RIS_N2);
        //      Keys.RIS_KW, // Main Heading
        changeElementKey(ris_ht, adoc_ht, Keys.INDEX_TERM, Keys.RIS_CVS);
        changeElementKey(ris_ht, adoc_ht, Keys.UNCONTROLLED_TERMS, Keys.RIS_FLS);
        changeElementKey(ris_ht, adoc_ht, Keys.DOI, Keys.RIS_DO);

        //printElementDataXML(ris_ht);

        EIDoc risdoc = new EIDoc(did, ris_ht, getFormat());
        risdoc.setOutputKeys(RISView.ris_keys);

        return risdoc;
      }

    private void changeElementKey(ElementDataMap ris_ht, ElementDataMap adoc_ht, Key oldkey, Key newkey)
    {
      ElementData edata = (ElementData) adoc_ht.get(oldkey);
      if(edata != null)
      {
        edata.setKey(newkey);
        ris_ht.put(newkey, edata);
      }

      return;
    }

    private void printElementDataXML(ElementDataMap ht)
    {
        Collection keySet = ht.keySet();
        Iterator itrKeys = keySet.iterator();
        while(itrKeys.hasNext())
        {
          Key key = (Key) itrKeys.next();
          Writer w = new StringWriter();
          ElementData eldata = ((ElementData) ht.get(key));
          if(eldata != null)
          {
            try {
              eldata.toXML(w);
            }
            catch(IOException e)
            {
            }
            System.out.println(" key: " +  key.getKey() + " == " + w.toString());
          }
          else
          {
            System.out.println(" key: " +  key.getKey() + " NO DATA");
          }
        }
    }

}
