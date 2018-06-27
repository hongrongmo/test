
package org.ei.dataloading.inspec.loadtime;

import java.io.Serializable;

/**
 *  Description of the Class
 *
 *@author     dbaptist
 *@created    September 19, 2001
 */
public class SubField implements Serializable {
	char type;
	String value;


	/**
	 *  Constructor for the SubField object
	 *
	 *@param  tagValue                  Description of Parameter
	 *@param  data                      Description of Parameter
	 *@exception  InvalidMARCException  Description of Exception
	 *@since
	 */
	public SubField(int tagValue, String data) throws InvalidMARCException {
		if (tagValue >= 0 && tagValue <= 9) {
			type = '\u0000';
			//type = data.charAt(0);
			//value = data.substring(2);
			value = data.trim();
		} else {
			try {

				//type = data.charAt(0);
				//value = data.substring(2);
				value = data.trim();
				//System.out.println(tagValue + "==" + value);
				//System.out.println("value Tag=" + tagValue + " value subfield=" + value);
			} catch (Exception e) {
				throw new InvalidMARCException();
			}
		}
	}


	/**
	 *  Gets the type attribute of the SubField object
	 *
	 *@return    The type value
	 *@since
	 */
	public char getType() {
		return (type);
	}


	/**
	 *  Gets the value attribute of the SubField object
	 *
	 *@return    The value value
	 *@since
	 */
	public String getValue() {
		return (value);
	}
}


