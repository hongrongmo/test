package org.ei.stripes.customtags;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.validator.GenericValidator;
import org.ei.domain.DatabaseConfig;
import org.ei.stripes.view.Affil;
import org.ei.stripes.view.Author;
import org.ei.stripes.view.SearchResult;

public class MendeleyCoinsFormatTag extends SimpleTagSupport {
    private SearchResult result; // Required SearchResult object

    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        JspWriter out = pageContext.getOut();

        // <span class="Z3988"
        // title="ctx_ver=Z39.88-2004&amp;
        // rfr_id=info%3Asid%2Fmendeley.com%2Fmendeley&amp;
        // rft_val_fmt=info%3Aofi%2Ffmt%3Akev%3Amtx%3Ajournal&amp;
        // rft.genre=article&amp;
        // rft.date=2009&amp;
        // rft.volume=35&amp;
        // rft.issue=6&amp;
        // rft.pages=726-728&amp;
        // rft.atitle=How+to+choose+a+good+scientific+problem.&amp;
        // rft.jtitle=Molecular+cell&amp;
        // rft.title=Molecular+cell&amp;
        // rft.aulast=Alon&amp;
        // rft.aufirst=Uri&amp;
        // rft_id=info%3Adoi%2F10.1016%2Fj.molcel.2009.09.013&amp;
        // rft_id=info%3Apmid%2F19782018&amp;
        // rft.issn=10972765&amp;
        // rft.isbn=0769520561"></span>
        StringBuffer tagoutput = new StringBuffer("<span class=\"Z3988\" title=\"ctx_ver=Z39.88-2004&amp;rfr_id="
            + URLEncoder.encode("info:sid/engineeringvillage.com:search", "utf-8"));
        // TODO change link to directly access abstract or detailed page.  Not possible right now!
        //tagoutput.append("&amp;rft_id="+URLEncoder.encode("http://" + pageContext.getRequest().getServerName() + "/home.url", "utf-8"));
        if (DatabaseConfig.PAG_MASK == result.getDoc().getDbmask()) {
            tagoutput.append("&amp;rft_val_fmt=" + URLEncoder.encode("info:ofi/fmt:kev:mtx:book", "utf-8"));
        } else {
            tagoutput.append("&amp;rft_val_fmt=" + URLEncoder.encode("info:ofi/fmt:kev:mtx:journal", "utf-8"));
        }
        String doctype = result.getDoctype();
        if (GenericValidator.isBlankOrNull(doctype)) {
            tagoutput.append("&amp;rft.genre=unknown");
        } else if (doctype.contains("Journal article")) {
            tagoutput.append("&amp;rft.genre=article");
        } else if (doctype.contains("Conference article")) {
            tagoutput.append("&amp;rft.genre=conference");
        } else if (doctype.contains("Conference proceeding")) {
            tagoutput.append("&amp;rft.genre=proceeding");
        } else if (doctype.contains("Unpublished paper") || doctype.contains("Article in Press") || doctype.contains("In Process")) {
            tagoutput.append("&amp;rft.genre=preprint");
        } else {
            tagoutput.append("&amp;rft.genre=unknown");
        }
        tagoutput.append("&amp;rft.date=" + removeHtmlTag(result.getYr()));
        tagoutput.append("&amp;rft.atitle=" + removeHtmlTag(result.getTitle()).replace(" ", "+").replaceAll("</?inf>", "").replaceAll("\"", "&quot;"));
        if (!GenericValidator.isBlankOrNull(result.getDoi())) {
            tagoutput.append("&amp;rft_id=" + URLEncoder.encode("info:doi/" + removeHtmlTag(result.getDoi()), "utf-8"));
        }
        if (!GenericValidator.isBlankOrNull(result.getAbstractrecord().getCoden())) {
            tagoutput.append("&amp;rft.coden=" + removeHtmlTag(result.getAbstractrecord().getCoden()));
        }
        if (!GenericValidator.isBlankOrNull(result.getSource())) {
            tagoutput.append("&amp;rft.jtitle=" + removeHtmlTag(result.getSource()).replace(" ", "+"));
            tagoutput.append("&amp;rft.title=" + removeHtmlTag(result.getSource()).replace(" ", "+"));
        }
        if (!GenericValidator.isBlankOrNull(result.getIs())) {
            tagoutput.append("&amp;rft.issue=" + removeHtmlTag(result.getIs()));
        }
        if (!GenericValidator.isBlankOrNull(result.getVo())) {
            tagoutput.append("&amp;rft.volume=" + removeHtmlTag(result.getVo()));
        }
        if (!GenericValidator.isBlankOrNull(result.getArticlenumber())) {
            tagoutput.append("&amp;rft.artnum=" + removeHtmlTag(result.getArticlenumber()));
        }
        if (!GenericValidator.isBlankOrNull(result.getPages())) {
            tagoutput.append("&amp;rft.pages=" + removeHtmlTag(result.getPages()));
        }
        if (!GenericValidator.isBlankOrNull(result.getAbstractrecord().getIssn())) {
            tagoutput.append("&amp;rft.issn=" + removeHtmlTag(result.getAbstractrecord().getIssn()));
        }
        if (!GenericValidator.isBlankOrNull(result.getIsbn13())) {
            tagoutput.append("&amp;rft.isbn=" + removeHtmlTag(result.getIsbn13()));
        } else if (!GenericValidator.isBlankOrNull(result.getIsbn())) {
            tagoutput.append("&amp;rft.isbn=" + removeHtmlTag(result.getIsbn()));
        }

        if (result.getAuthors() != null) {
            for (Author author : result.getAuthors()) {
                tagoutput.append("&amp;rft.au=" + removeHtmlTag(author.getName()));
            }
        }
        if (result.getAffils() != null) {
            for (Affil affil : result.getAffils()) {
                tagoutput.append("&amp;rft.aucorp=" + removeHtmlTag(affil.getName()));
            }
        }
        tagoutput.append("\"></span>");
        out.write(tagoutput.toString());
    }

    //
    //
    // GETTERS / SETTERS
    //
    //

    public void setResult(SearchResult result) {
        this.result = result;
    }

    
    private String removeHtmlTag(String htmlString){
    	
    	if(htmlString == null) return null;
    	
    	return StringEscapeUtils.escapeHtml(htmlString.replaceAll("\\<.*?>",""));
    }
}
