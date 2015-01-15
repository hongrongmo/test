package org.ei.domain.navigators;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.ei.util.StringUtil;

public class BookModifier extends EiModifier {

	public static final String TYPE_BOOK = "BOOK";
	public static final String TYPE_SECT = "SECT";

	String title = null;
	String modtype = null;

	public BookModifier(int i, String slabel, String svalue, String title, String type) {
		super(i, slabel, svalue);
		setType(type);
		setTitle(title);
	}

    public String toXML()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("<MODIFIER COUNT=\"").append(this.getCount()).append("\">");

        if(!StringUtil.EMPTY_STRING.equals(this.getValue()))
        {
            sb.append("<VALUE><![CDATA[").append(this.getValue()).append("]]></VALUE>");
        }
		try {

			sb.append("<LABEL><![CDATA[").append(this.getLabel()).append(
					"]]></LABEL>").append("<TITLE TYPE=\"").append(
					getType()).append(
					"\"><![CDATA[").append((this.getTitle() != null) ? URLDecoder.decode(this.getTitle(),"utf-8") : "").append(
					"]]></TITLE>").append("</MODIFIER>");
		} catch (UnsupportedEncodingException e) {

		}
        return sb.toString();
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return modtype;
	}

	public void setType(String stype) {
		this.modtype = stype;
	}

}
