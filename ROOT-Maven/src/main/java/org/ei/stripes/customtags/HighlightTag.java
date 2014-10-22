package org.ei.stripes.customtags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.ei.data.insback.runtime.InspecArchiveAbstract;
import org.ei.query.base.HitHighlightFinisher;

public class HighlightTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7843802892658429417L;

	private String value;
	private boolean on=true;
	private boolean remove;
	private boolean v2;
	
	private PageContext pagecontext;
	@Override
	public void setPageContext(PageContext pageContext) {
		pagecontext = pageContext;
	}

	@Override
	public int doStartTag() throws JspException {
		if (value == null) {
			throw new JspTagException("Value is required!");
		}
		
		JspWriter out = pagecontext.getOut();
		try {
			out.write(getHighlighting(remove, on, v2, value));
		} catch (IOException e) {
			throw new JspTagException("Unable to add markup: ", e);
		} catch (Exception e) {
			throw new JspTagException("Unable to add markup: ", e);
		}
		return SKIP_BODY;
	}

	/**
	 * Return the highlighted text
	 * @param remove
	 * @param on
	 * @param value
	 * @return
	 * @throws JspTagException 
	 */
	public static String getHighlighting(boolean remove, boolean on, boolean v2, String value) throws JspTagException {
        if (value == null) {
            throw new JspTagException("Value is required!");
        }
        
        try {
            if (remove) {
                return HitHighlightFinisher.removeMarkup(value);
            } else {
                if (!on) {
                    if (v2) {
                        return InspecArchiveAbstract.getHTML(HitHighlightFinisher.removeMarkupWithTagCheckTagging(value));
                    } else {
                        return HitHighlightFinisher.removeMarkupWithTag(value);
                    }
                } else {
                    if (v2) {
                        return(InspecArchiveAbstract.getHTML(HitHighlightFinisher.addMarkupCheckTagging(value)));
                    } else {
                        return(HitHighlightFinisher.addMarkup(value));
                    }
                }
            }
        } catch (IOException e) {
            throw new JspTagException("Unable to add markup: ", e);
        } catch (Exception e) {
            throw new JspTagException("Unable to add markup: ", e);
        }
	}

	@Override
	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	public void setV2(boolean v2) {
		this.v2 = v2;
	}

}
