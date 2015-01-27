package org.ei.stripes.customtags;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.validator.GenericValidator;
import org.ei.stripes.view.SearchResult;

public class SearchResultFormatTag extends SimpleTagSupport {
    public static final String CIT_FORMAT_SOURCE_LINE = "citationsourceline";
    public static final String ABS_FORMAT_SOURCE_LINE = "abstractsourceline";
    public static final String SEL_CIT_FORMAT_SOURCE_LINE = "selcitationsourceline";
    private boolean on;         // Flag for highlighting to be applied (abstract)
    private SearchResult result; // Required SearchResult object
    private String name; // Required name attr - identifies the tag to be
                         // written
    
    private boolean seenfirst = false;
    private boolean seenfirstsemicolon = false;

    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        JspWriter out = pageContext.getOut();

        if (CIT_FORMAT_SOURCE_LINE.equals(name)) {
            seenfirst = false;
            StringBuffer body = new StringBuffer();
            if (!result.isNosource()) {
                seenfirst = true;
                if (GenericValidator.isBlankOrNull(result.getSource())) {
                    body.append("<b>&nbsp;Source:</b> <span>No source available</span>");
                } else {
                    body.append("<b>&nbsp;Source:</b> <span><i>" + result.getSource() + "</i></span>");
                }
            }
            if (result.getDoc().getDbmask() != 131072) {
                if (!GenericValidator.isBlankOrNull(result.getBpn())) {
                    body.append("<span class=\"publisher\">" + result.getBpn() + "</span>");
                    if (!seenfirst)
                        seenfirst = true;
                }
                if (!GenericValidator.isBlankOrNull(result.getByr())) {
                    body.append("<span class=\"pubyear\">" + result.getByr() + "</span>");
                    if (!seenfirst)
                        seenfirst = true;
                }
            }
            if(!GenericValidator.isBlankOrNull(result.getPf())){
            	appendWithComma("("+result.getPf()+")", body);
            }
            
            appendWithComma(result.getSp(), body);
            appendWithCommaLabel("Sponsor",result.getRsp(),body, false);
            appendWithCommaLabel("Report", result.getRnlabel(), body, false);
            appendWithComma(result.getRn(), body);
            appendWithComma(result.getVo(), body);
            appendWithComma(result.getPages(), body);
            appendWithComma(result.getArn(), body);
            if (!GenericValidator.isBlankOrNull(result.getPage())) {
                appendWithComma("p " +result.getPage(), body);
            }
            if (!GenericValidator.isBlankOrNull(result.getPagespp())) {
                appendWithComma(result.getPagespp() + " pp", body);
            }
            appendWithComma(result.getSd(), body);
            appendWithCommaItalic(result.getMt(), body,false);
            appendWithComma(result.getVt(), body);
            if (GenericValidator.isBlankOrNull(result.getSd())) {
                appendWithComma(result.getYr(), body);
            }
            
            if(!GenericValidator.isBlankOrNull(result.getPd())){
            	appendWithComma("<b>Publication date: </b>"+result.getPd(), body);
            }

            appendWithComma(result.getNv(), body);
            appendWithComma(result.getPa(), body);

            //
            // Patent-based info next!
            //
            List<String> assigneelinks = result.getAssigneelinks();
            if (assigneelinks != null && assigneelinks.size() > 0) {
                StringBuffer output = new StringBuffer();
                for (int i=0; i<assigneelinks.size(); i++) {
                    if (i>0) output.append("&nbsp;-&nbsp;");
                    output.append(assigneelinks.get(i));
                }
                body.append("<span><b>&nbsp;Assignee: </b>" + output.toString() + "</span>");
            }
            List<String> patassigneelinks = result.getPatassigneelinks();
            if (patassigneelinks != null && patassigneelinks.size() > 0) {
                StringBuffer output = new StringBuffer();
                output.append("<b>&nbsp;Patent assignee:</b> <span>");
                for (int i=0; i<assigneelinks.size(); i++) {
                    if (i>0) output.append("&nbsp;-&nbsp;");
                    output.append(assigneelinks.get(i));
                }
                body.append("<span><b>&nbsp;Assignee: </b>" + output.toString() + "</span>");
            }
            if (!GenericValidator.isBlankOrNull(result.getPan())) body.append("<span> <b>Application number: </b>"+result.getPan()+"</span>");
            if (!GenericValidator.isBlankOrNull(result.getPap())) body.append("<span> <b>Patent number: </b>"+result.getPap()+"</span>");
            if (!GenericValidator.isBlankOrNull(result.getPinfo())) body.append("<span> <b>Patent information: </b>"+result.getPinfo()+"</span>");
            if (!GenericValidator.isBlankOrNull(result.getPm())) body.append("<span> <b>Publication Number: </b>"+result.getPm()+"</span>");
            if (!GenericValidator.isBlankOrNull(result.getPm1())) body.append("<span> <b>Publication Number: </b>"+result.getPm1()+"</span>");
            if (!GenericValidator.isBlankOrNull(result.getUpd())) body.append("<span> <b>Publication " + (result.getDoc().getDbmask() == 2048 ? "year":"date") + ": </b>"+result.getUpd()+"</span>");
            if (!GenericValidator.isBlankOrNull(result.getKd())) body.append("<span> <b>Kind: </b>"+result.getKd()+"</span>");
            if (!GenericValidator.isBlankOrNull(result.getPfd())) body.append("<span> <b>Filing date: </b>"+result.getPfd()+"</span>");
            if (!GenericValidator.isBlankOrNull(result.getPidd())) body.append("<span> <b>Patent issue date: </b>"+result.getPidd()+"</span>");
            if (!GenericValidator.isBlankOrNull(result.getPpd())) body.append("<span> <b>Publication date: </b>"+result.getPpd()+"</span>");
            if (!GenericValidator.isBlankOrNull(result.getCopa())) body.append("<span> <b>Country of application: </b>"+result.getCopa()+"</span>");
            if (!GenericValidator.isBlankOrNull(result.getLa())) body.append("<span> <b>Language: </b>"+result.getLa()+"</span>");
            if (!GenericValidator.isBlankOrNull(result.getNf())) body.append("<span> <b>Figures: </b>"+result.getNf()+"</span>");
            if (!GenericValidator.isBlankOrNull(result.getAv())) body.append("<span> <b>Availability: </b>"+result.getAv()+"</span>");

            out.write(body.toString());
        } else if (ABS_FORMAT_SOURCE_LINE.equals(name)) {
            seenfirst = false;
            seenfirstsemicolon = false;
            StringBuffer body = new StringBuffer();

            if (!result.isNosource()) {
                seenfirst = true;
                seenfirstsemicolon = true;
                String source = HighlightTag.getHighlighting(false, on, false, result.getSource());
                if (GenericValidator.isBlankOrNull(source)) {
                    body.append("<b>Source:</b> <span>No source available</span>");
                } else {
                    body.append("<b>Source:</b> <span><i>" + source + "</i></span>");
                }
            }
            
            append(";&nbsp;","Sponsor",result.getRsp(),body,true);
            append(";&nbsp;","Report",result.getRnlabel(),body,false);
            append("&nbsp;",null,result.getPn(),body,true);
            
            append(",&nbsp;",null,result.getVo(),body,false);
            if (GenericValidator.isBlankOrNull(result.getPagespp()) || GenericValidator.isBlankOrNull(result.getPage())) {
            	append(",&nbsp;",null,result.getPages(),body,false);
            }
            
            append(",&nbsp;",null,result.getPage(),body,false);
            append(",&nbsp;",null,result.getPagespp(),body,false);
            append(",&nbsp;art. no. ",null,result.getArn(),body,false);
            append(",&nbsp;",null,result.getRn(),body,true);
            append(",&nbsp;",null,result.getSd(),body,true);
            append(",&nbsp;",null,result.getPa(),body,true);
            appendWithItalic(",&nbsp;",null,result.getMt(),body,true);
            append(",&nbsp;",null,result.getVt(),body,true);
            
           //citedby
            //<PASM>
            //<EASM>
           
            append("<br/>&nbsp;","Patent information",result.getPinfo(),body,true);
            append("&nbsp;","Application information",result.getPapim(),body,true);
            
            if (!GenericValidator.isBlankOrNull(result.getPan())) {
            	if (!GenericValidator.isBlankOrNull(result.getPap()) && !GenericValidator.isBlankOrNull(result.getPpn())) {
            		body.append("<br/>");
            	}
            	append("&nbsp;","Application number",result.getPan(),body,true);
            }
            
            if (!GenericValidator.isBlankOrNull(result.getPm())) {
            	if (!GenericValidator.isBlankOrNull(result.getPap()) && !GenericValidator.isBlankOrNull(result.getPpn()) 
            			&& !GenericValidator.isBlankOrNull(result.getPan())) {
            		body.append("<br/>");
            	}
            	append("&nbsp;","Publication number",result.getPm(),body,true);
            }
            
            append("&nbsp;","Publication Number",result.getPm1(),body,true);
                        
            List<String> pim = result.getPim();   
            if (pim != null && pim.size() > 0) {
                StringBuffer output = new StringBuffer();
                for (int i=0; i<pim.size(); i++) {
                    if (i>0) output.append("&nbsp;-&nbsp;");
                    output.append(pim.get(i));
                }
                append("&nbsp;","Priority information",output.toString(),body,true);
            }
            
            if (!GenericValidator.isBlankOrNull(result.getUpd())) {
            	if(result.getLabels().get("UPD")!=null){
            		append("&nbsp;",result.getLabels().get("UPD"),result.getUpd(),body,false);
            	}else{
					append("&nbsp;","Publication date",result.getUpd(),body,false);
				}
		    }
            
            append("&nbsp;","Kind",result.getKd(),body,false);
            append("<br/>&nbsp;","Patent number",result.getPap(),body,true);
            append("<br/>&nbsp;","Filing date",result.getPfd(),body,false);
            append("&nbsp;","Publication date",result.getPpd(),body,false);
            append("&nbsp;","Patent issue date",result.getPidd(),body,false);
            append("&nbsp;","Country of application",result.getCopa(),body,false);
            if (GenericValidator.isBlankOrNull(result.getSd())){
            	append(",&nbsp;",null,result.getYr(),body,false);
            }
			
            append("&nbsp;","Publication date",result.getPd(),body,false);
            append("&nbsp;","Priority Number",result.getPi(),body,false);
			
			if (!GenericValidator.isBlankOrNull(result.getPpn())) {
				if (!GenericValidator.isBlankOrNull(result.getPap())) {
	        		body.append("<br/>");
	        	}
				append("&nbsp;","Priority Number",result.getPpn(),body,true);
			}
            //AIN
			append("<br/>&nbsp;","IPE Code",result.getIpc(),body,true);
			
			if (!GenericValidator.isBlankOrNull(result.getEcl())) {
				if (!GenericValidator.isBlankOrNull(result.getIpc())) {
	        		body.append("<br/>");
	        	}
				append("&nbsp;","ECLA Classes",result.getEcl(),body,true);
			}
			
			append(";&nbsp;","Language",result.getLa(),body,false);
			append(";&nbsp;&nbsp;","ISSN",result.getAbstractrecord().getIssn(),body,false);
			
			if (!GenericValidator.isBlankOrNull(result.getAbstractrecord().getEissn())){ 
				if (!GenericValidator.isBlankOrNull(result.getAbstractrecord().getIssn()) && 
						result.getAbstractrecord().getIssn().length()>0){
					append(",&nbsp;&nbsp;","E-ISSN",result.getAbstractrecord().getEissn(),body,false);
				}
				else{
					append(";&nbsp;&nbsp;","E-ISSN",result.getAbstractrecord().getEissn(),body,false);
				}
			}
			
			append(";&nbsp;&nbsp;",result.getLabels().get("BN"),result.getIsbn(),body,false);
			
			if (!GenericValidator.isBlankOrNull(result.getIsbn13())){ 
				if (!GenericValidator.isBlankOrNull(result.getIsbn()) && 
						result.getIsbn().length()>0){
					append(",&nbsp;&nbsp;","ISBN-13",result.getIsbn13(),body,false);
				}
				else{
					append(";&nbsp;&nbsp;","ISBN-13",result.getIsbn13(),body,false);
				}
			}
            
            append(";&nbsp;&nbsp;","DOI",result.getAbstractrecord().getDoi(),body,false);
            append(";&nbsp;&nbsp;",result.getLabels().get("ARTICLE_NUMBER"),result.getArticlenumber(),body,false);
            appendOriginal(",&nbsp;",null,result.getBpc()+"p",body,false,result.getBpc());
            append(",&nbsp",null,result.getByr(),body,false);
            append(";&nbsp","Publisher",result.getBpn(),body,false);
            append(";&nbsp","Conference",result.getCf(),body,true);
            append(",&nbsp",null,result.getMd(),body,false);
            append(",&nbsp",null,result.getMl(),body,false);
            append(";&nbsp","Sponsor",result.getSp(),body,true);
            //CLOC
            append(";&nbsp","Publisher",result.getIpn(),body,true);
            append(";&nbsp","Country of publication",result.getCpub(),body,true);
            append(";<br/>",null,result.getFttj(),body,false);
            
            appendWithSpan(";&nbsp;","Availability",result.getAv(),body,false);
            appendWithSpan(";&nbsp;","Scope",result.getSc(),body,false);
            
            out.write(body.toString());

        }else if (SEL_CIT_FORMAT_SOURCE_LINE.equals(name)) {
            seenfirst = false;
            StringBuffer body = new StringBuffer();
            if (!result.isNosource()) {
                seenfirst = true;
                if (GenericValidator.isBlankOrNull(result.getSource())) {
                    body.append("<b>&nbsp;Source:</b> <span>No source available</span>");
                } else {
                    body.append("<b>&nbsp;Source:</b> <span><i>" + result.getSource() + "</i></span>");
                }
            }
            
            if (result.getDoc().getDbmask() != 131072) {
            	appendOriginal("",null,result.getLabels().get("BN")+": "+result.getIsbn(),body,false,result.getIsbn());
            	
            }
            if (result.getDoc().getDbmask() == 131072) {
            	appendOriginal("",null,"ISBN-13: "+result.getIsbn13(),body,false,result.getIsbn13());
            }
    		
            append(",&nbsp",null,result.getBpn(),body,false);
            append(",&nbsp",null,result.getByr(),body,false);
            appendOriginal("&nbsp",null,"("+result.getPf()+")",body,false,result.getPf());    
            
            append("&nbsp;","Sponsor",result.getRsp(),body,true);
            append("&nbsp;","Report",result.getRnlabel(),body,false);
            append(",&nbsp;",null,result.getRn(),body,false);
            append(",&nbsp;",null,result.getVo(),body,false);
            append(",&nbsp;",null,result.getPages(),body,false);
            append(",&nbsp;art. no. ",null,result.getArn(),body,false);
            
            append(", p ",null,result.getPage(),body,false);
            appendOriginal(",&nbsp;",null,result.getPagespp()+" pp",body,false,result.getPagespp());
            
            append(",&nbsp;",null,result.getSd(),body,false);
            appendWithItalic(",&nbsp;",null,result.getMt(),body,false);
            append(",",null,result.getVt(),body,false);
         
            if (GenericValidator.isBlankOrNull(result.getSd())){
            	if (result.getDoc().getDbname().equalsIgnoreCase("NTIS")) {
            		if(!GenericValidator.isBlankOrNull(result.getPf()) 
            				|| !GenericValidator.isBlankOrNull(result.getRsp())
            				|| !GenericValidator.isBlankOrNull(result.getRnlabel())){
            			append("",null,result.getYr(),body,false);
            		}else{
            			append(",&nbsp;",null,result.getYr(),body,false);
            		}
            	}else{
            		append(",&nbsp;",null,result.getYr(),body,false);
            	}
            }
            
            append("&nbsp;","Publication date",result.getPd(),body,false);
            append(",&nbsp;",null,result.getNv(),body,false);
            append(",&nbsp;",null,result.getPa(),body,false);
            
            //
            // Patent-based info next!
            //
            List<String> assigneelinks = result.getAssigneelinks();
            if (assigneelinks != null && assigneelinks.size() > 0) {
                StringBuffer output = new StringBuffer();
                for (int i=0; i<assigneelinks.size(); i++) {
                    if (i>0) output.append("&nbsp;-&nbsp;");
                    output.append(assigneelinks.get(i));
                }
                append("&nbsp;","Assignee",output.toString(),body,false);
            }
            List<String> patassigneelinks = result.getPatassigneelinks();
            if (patassigneelinks != null && patassigneelinks.size() > 0) {
                StringBuffer output = new StringBuffer();
                for (int i=0; i<assigneelinks.size(); i++) {
                    if (i>0) output.append("&nbsp;-&nbsp;");
                    output.append(assigneelinks.get(i));
                }
                append("&nbsp;","Patent assignee",output.toString(),body,false);
            }
            append("&nbsp;","Application number",result.getPan(),body,false);
            append("&nbsp;","Patent number",result.getPap(),body,false);
            
            List<String> pim = result.getPim();   
            if (pim != null && pim.size() > 0) {
                StringBuffer output = new StringBuffer();
                for (int i=0; i<pim.size(); i++) {
                    if (i>0) output.append("&nbsp;-&nbsp;");
                    output.append(pim.get(i));
                }
                append("&nbsp;","Patent information",output.toString(),body,true);
            }
            
            append("&nbsp;","Patent information",result.getPinfo(),body,true);
            append("&nbsp;","Publication Number",result.getPm(),body,true);
            append("&nbsp;","Publication Number",result.getPm1(),body,true);
            
            if (!GenericValidator.isBlankOrNull(result.getUpd())) {
            	if(result.getDoc().getDbmask() != 2048){
            		append("&nbsp;","Publication date",result.getUpd(),body,false);
            	}else{
					append("&nbsp;","Publication year",result.getUpd(),body,false);
				}
		    }
            append("&nbsp;","Kind",result.getKd(),body,false);
            append("<br/>&nbsp;","Filing date",result.getPfd(),body,false);
            append("&nbsp;","Patent issue date",result.getPidd(),body,false);
            append("&nbsp;","Publication date",result.getPpd(),body,false);
            append("&nbsp;","Country of application",result.getCopa(),body,false);
            append("&nbsp;","Language",result.getLa(),body,false);
            append("&nbsp;","Figures",result.getNf(),body,false);
            append("&nbsp;","Availability",result.getAv(),body,false);
            
            List<String> dt = result.getDt();   
            if (dt != null && dt.size() > 0) {
                for (int i=0; i<dt.size(); i++) {
                    if (dt.get(i).equals("Article in Press")){
                        body.append("<span><br/><img src=\"/static/images/btn_aip.gif\" border=\"0\" style=\"vertical-align:bottom\" title=\"Articles not published yet, but available online\" alt=\"Articles not published yet, but available online\"/> Article in Press</span>");
                        break;
                    }
                    else if (dt.get(i).equals("In Process")){
                        body.append("<span><br/><img src=\"/static/images/btn_aip.gif\" border=\"0\" style=\"vertical-align:bottom\" title=\"Records still in the process of being indexed, but available online.\" alt=\"Records still in the process of being indexed, but available online.\"/> In Process</span>");
                        break;
                    }
                }
            }
        
            out.write(body.toString());
        }
    }

    /**
     * Convenience method to append comma
     * 
     * @param body
     * @param seenfirst
     * @param seenfirstsemicolon
     * @return
     */
    private boolean appendWithComma(String value, StringBuffer body) {
        if (!GenericValidator.isBlankOrNull(value)) {
            if (!seenfirst) {
                seenfirst = true;
            } else {
                body.append(", ");
            }
            if (!seenfirstsemicolon) {
            	seenfirstsemicolon = true;
            }
            body.append("<span>" + value + "</span>");
        }
        return seenfirst;
    }

    /**
     * Convenience method to prepend comma to value, italicized
     * @param value
     * @param body
     * @param seenfirst
     * @param seenfirstsemicolon
     * @param hitHighlighting
     * @return
     * @throws JspTagException 
     */
    private boolean appendWithCommaItalic(String value, StringBuffer body,boolean hitHighlighting) throws JspTagException {
        if (!GenericValidator.isBlankOrNull(value)) {
        	
        	if (!seenfirstsemicolon) {
            	seenfirstsemicolon = true;
            }
        	
        	if(hitHighlighting){
        		value=HighlightTag.getHighlighting(false, on, false, value);
        	}
        	
            return appendWithComma("<i>" + value + "</i>", body);
        }        
        return seenfirst;
    }
    
    
    /**
     * Convenience method to append Comma with Label
     * 
     * @param label
     * @param value
     * @param body
     * @param seenfirst
     * @param seenfirstsemicolon
     * @param hitHighlighting
     * @return
     */
    private boolean appendWithCommaLabel(String label,String value, StringBuffer body,boolean hitHighlighting) throws JspTagException {
        if (!GenericValidator.isBlankOrNull(value)) {
            if (!seenfirst) {
                seenfirst = true;
            } else {
                body.append(" ");
            }
            
            if (!seenfirstsemicolon) {
            	seenfirstsemicolon = true;
            }
            
            if(hitHighlighting){
        		value=HighlightTag.getHighlighting(false, on, false, value);
        	}
            
            body.append("<b>" + label + ":</b> "+value);
        }
        return seenfirst;
    }
    
  
    /**
     * Convenience method to append given string with Label and value validate  value
     * 
     * @param append
     * @param label
     * @param value
     * @param body
     * @param seenfirst
     * @param hitHighlighting
     * @return
     */
    private boolean append(String append,String label,String value, StringBuffer body,boolean hitHighlighting) throws JspTagException {
        if (!GenericValidator.isBlankOrNull(value)) {
            
        	 if(hitHighlighting){
         		value=HighlightTag.getHighlighting(false, on, false, value);
         	}
        	 
        	 if (!seenfirst) {
                 seenfirst = true;
             } else {
                 body.append(append);
             }
        	 
        	if(label!=null && !label.trim().equals("")){ 
        		body.append("<b>" + label + ":</b> "+value);
        	}else{
        		body.append(value);
        	}
            
            
        }
        return seenfirst;
    }
    
    /**
     * Convenience method to append given string with Label and value validate original value
     * 
     * @param append
     * @param label
     * @param value
     * @param body
     * @param seenfirst
     * @param hitHighlighting
     * @param originalvalue
     * @return
     */
    private boolean appendOriginal(String append,String label,String value, StringBuffer body,boolean hitHighlighting,String originalvalue) throws JspTagException {
        if (!GenericValidator.isBlankOrNull(originalvalue)) {
            
        	 if(hitHighlighting){
         		value=HighlightTag.getHighlighting(false, on, false, value);
         	}
        	 
        	 if (!seenfirst) {
                 seenfirst = true;
             } else {
                 body.append(append);
             }
        	 
        	if(label!=null && !label.trim().equals("")){ 
        		body.append("<b>" + label + ":</b> "+value);
        	}else{
        		body.append(value);
        	}
            
            
        }
        return seenfirst;
    }
    
    /**
     * Convenience method to append span to given string with Label and value validate  value
     * 
     * @param append
     * @param label
     * @param value
     * @param body
     * @param seenfirst
     * @param hitHighlighting
     * @return
     */
    private boolean appendWithSpan(String append,String label,String value, StringBuffer body,boolean hitHighlighting) throws JspTagException {
        if (!GenericValidator.isBlankOrNull(value)) {
            
        	 if(hitHighlighting){
         		value=HighlightTag.getHighlighting(false, on, false, value);
         	}
        	 
        	 if (!seenfirst) {
                 seenfirst = true;
             } else {
                 body.append(append);
             }
        	 
        	if(label!=null && !label.trim().equals("")){ 
        		body.append("<span> <b>" + label + ":</b> "+value+"</span>");
        	}else{
        		body.append("<span>" +value+"</span>");
        	}
            
            
        }
        return seenfirst;
    }
    
    /**
     * Convenience method to append italic to given string with Label and value validate  value
     * 
     * @param append
     * @param label
     * @param value
     * @param body
     * @param seenfirst
     * @param hitHighlighting
     * @return
     */
    private boolean appendWithItalic(String append,String label,String value, StringBuffer body,boolean hitHighlighting) throws JspTagException {
        if (!GenericValidator.isBlankOrNull(value)) {
            
        	 if(hitHighlighting){
         		value=HighlightTag.getHighlighting(false, on, false, value);
         	}
        	 
        	 if (!seenfirst) {
                 seenfirst = true;
             } else {
                 body.append(append);
             }
        	 
        	if(label!=null && !label.trim().equals("")){ 
        		body.append("<i> <b>" + label + ":</b> "+value+"</i>");
        	}else{
        		body.append("<i>" +value+"</i>");
        	}
            
            
        }
        return seenfirst;
    }
    
    //
    //
    // GETTERS / SETTERS
    //
    //

    public void setName(String name) {
        this.name = name;
    }

    public void setResult(SearchResult result) {
        this.result = result;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

}
