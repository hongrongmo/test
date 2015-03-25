package org.ei.dataloading.inspec.loadtime;


import java.io.Serializable;
import java.util.StringTokenizer;



/**
 *  Description of the Class
 *
 *@author     dbaptist
 *@created    September 19, 2001
 */
public class Field implements Serializable {
	private final static String SFSEP = "\u001F";
	// Sub-Field Separator

	private String tag;
	private char indicator[];
	private SubField subfields[];
	private int currentSubField;


	/**
	 *  Constructor for the Field object
	 *
	 *@param  tag                       Description of Parameter
	 *@param  data                      Description of Parameter
	 *@exception  InvalidMARCException  Description of Exception
	 *@since
	 */
	public Field(String tag, String data) throws InvalidMARCException {
		this.tag = tag;
		indicator = new char[2];
		currentSubField = 0;
		//System.out.println(tag + "==" + data);
		try {
			int tagValue = Integer.parseInt(tag);
			if (tagValue < 0) {
				throw new InvalidMARCException();
			} else if (tagValue >= 0 && tagValue <= 9) {
				//indicator[0] = ' ';
				//indicator[1] = ' ';
				indicator[0] = data.charAt(0);
				subfields = new SubField[1];
				//subfields[0] = new SubField(tagValue, data.substring(2));
				subfields[0] = new SubField(tagValue, data.substring(2));
			} else {
				indicator[0] = data.charAt(0);
				//indicator[1] = data.charAt(1);
				StringTokenizer tokenizer = new StringTokenizer(data.substring(2), SFSEP);
				subfields = new SubField[tokenizer.countTokens()];
				for (int i = 0; tokenizer.hasMoreTokens(); i++) {
					String temp = tokenizer.nextToken();
					subfields[i] = new SubField(tagValue, temp);
					//System.out.println(subfields[i].getValue() + " Field " + temp);
				}
				tokenizer = null;
			}
		} catch (Exception e) {
			throw new InvalidMARCException();
		}
	}


	/**
	 *  Gets the tag attribute of the Field object
	 *
	 *@return    The tag value
	 *@since
	 */
	public String getTag() {
		return (tag);
	}


	/**
	 *  Gets the indicator attribute of the Field object
	 *
	 *@param  type                          Description of Parameter
	 *@return                               The indicator value
	 *@exception  NoSuchIndicatorException  Description of Exception
	 *@since
	 */
	public char getIndicator(int type) throws NoSuchIndicatorException {
		if (type == 1 || type == 2) {
			return (indicator[type - 1]);
		} else {
			throw new NoSuchIndicatorException();
		}
	}


	/**
	 *  Gets the subField attribute of the Field object
	 *
	 *@param  code                         Description of Parameter
	 *@return                              The subField value
	 *@exception  NoSuchSubFieldException  Description of Exception
	 *@since
	 */
	public SubField getSubField(char code) throws NoSuchSubFieldException {
		for (int i = 0; i < subfields.length; i++) {
			if (code == subfields[i].getType()) {
				return (subfields[i]);
			}
		}
		throw new NoSuchSubFieldException();
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 *@since
	 */
	public int countSubFields() {
		return (subfields.length);
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 *@since
	 */
	public boolean hasMoreSubFields() {
		if (currentSubField < subfields.length) {
			return (true);
		} else {
			return (false);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@return                              Description of the Returned Value
	 *@exception  NoSuchSubFieldException  Description of Exception
	 *@since
	 */
	public SubField nextSubField() throws NoSuchSubFieldException {
		if (currentSubField < subfields.length) {
			return (subfields[currentSubField++]);
		} else {
			throw new NoSuchSubFieldException();
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@since
	 */
	public void finalize() {
		for (int i = 0; i < subfields.length; i++) {
			subfields[i] = null;
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  pattern  Description of Parameter
	 *@return          Description of the Returned Value
	 *@since
	 */
	public boolean matchTag(String pattern) {
		if (pattern.length() == 3) {
			for (int i = 0; i < 3; i++) {
				char c1 = pattern.charAt(i);
				char c2 = tag.charAt(i);
				if (c1 == c2) {
					continue;
				} else if (c1 == '*') {
					continue;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}
}


