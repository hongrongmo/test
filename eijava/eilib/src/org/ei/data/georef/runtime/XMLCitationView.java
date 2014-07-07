package org.ei.data.georef.runtime;

import org.ei.domain.Key;
import org.ei.domain.Keys;
import org.ei.domain.RIS;
import java.util.Arrays;
import java.util.List;
import org.ei.domain.Citation;

    public class XMLCitationView extends CitationView {

      //private List fields = Arrays.asList(new String[]{});
      //private Key[] keys = new Key[]{};

      public String getFormat() { return Citation.XMLCITATION_FORMAT; }
    }
