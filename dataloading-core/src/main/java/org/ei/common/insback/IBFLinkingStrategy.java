package org.ei.common.insback;

import org.ei.domain.EIDoc;
import org.ei.domain.ElementData;
import org.ei.domain.Keys;
import org.ei.fulldoc.LinkInfo;
import org.ei.fulldoc.LinkingStrategy;


public class IBFLinkingStrategy implements LinkingStrategy {

    public LinkInfo buildLink(EIDoc eid) throws Exception {
        LinkInfo linkInfo = new LinkInfo();
        linkInfo.url = "http://dx.doi.org/" + getDOI(eid);
        return linkInfo;
    }

    public String hasLink(EIDoc eid) {
        if (getDOI(eid) != null) {
            return HAS_LINK_YES;
        } else {
            return HAS_LINK_NO;
        }
    }

    private String getDOI(EIDoc eidoc) {
        String value = null;
        if (eidoc.getElementDataMap().containsKey(Keys.DOI)) {
            ElementData doi = (ElementData) eidoc.getElementDataMap().get(Keys.DOI);
            String[] edata = doi.getElementData();
            value = edata[0];
        }
        return value;
    }
}
