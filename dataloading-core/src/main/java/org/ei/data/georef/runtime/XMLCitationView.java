package org.ei.data.georef.runtime;

import org.ei.domain.Citation;
import org.ei.common.georef.CitationView;

    public class XMLCitationView extends CitationView {

      //private List fields = Arrays.asList(new String[]{});
      //private Key[] keys = new Key[]{};

      public String getFormat() { return Citation.XMLCITATION_FORMAT; }
    }
