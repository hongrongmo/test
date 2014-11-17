package org.ei.query.base;

import java.util.Vector;

import org.ei.exception.EVBaseException;

public class FieldException extends EVBaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2966001359246338799L;

	public FieldException(Exception e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	public FieldException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public FieldException(int code, String message, Exception e) {
		super(code, message, e);
	}

	private Vector fields;

	public void setFields(Vector fields) {
		this.fields = fields;
	}

	public Vector getFields() {
		return this.fields;
	}

	public String getMessage() {
		StringBuffer buf = new StringBuffer();
		buf.append("<DISPLAY>Query Error, The following field(s) do not exist: ");
		for (int i = 0; i < fields.size(); ++i) {
			if (i > 0) {
				buf.append(", ");
			}

			buf.append((String) fields.get(i));
		}

		buf.append("</DISPLAY>");
		return buf.toString();
	}
}
