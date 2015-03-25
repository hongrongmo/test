
package org.ei.dataloading.inspec.loadtime;

import java.io.Serializable;
import java.util.Vector;

/**
 *  Description of the Class
 *
 *@author     dbaptist
 *@created    September 19, 2001
 */
public class MARC implements Serializable {

	private final static int LDRL = 24;
	//Leader length
	private final static int RSZL = 5;
	//Record length
	private final static int FSPL = 1;
	//Field separator length
	private final static int RSPL = 1;
	//Record separator length
	private final static int DIRL = 12;
	//Directory length
	private final static int TAGL = 3;
	private String leader;
	private int size;
	private Field fields[];
	private int currentField;


	//Tag length

	/**
	 *  Constructor for the MARC object
	 *
	 *@param  rec                       Description of Parameter
	 *@exception  InvalidMARCException  Description of Exception
	 *@since
	 */
	public MARC(String rec) throws InvalidMARCException {
		Vector dirs = new Vector(20, 10);
		currentField = 0;

		try {
			leader = rec.substring(0, LDRL);
			size = Integer.parseInt(leader.substring(0, RSZL));
			int offset;
			int fieldCount;
			int length;
			int mark = LDRL + FSPL + RSPL;
			String dir;
			boolean EOD = false;
			for (offset = LDRL, fieldCount = 0; EOD != true; offset += DIRL, fieldCount++) {
				dir = rec.substring(offset, offset + DIRL);
				dirs.insertElementAt(dir, fieldCount);
				mark += (DIRL + Integer.parseInt(dir.substring(TAGL, 7)));
				if (mark == size) {
					EOD = true;
				}
			}
			String content = rec.substring(++offset);
			String tag;
			String info;
			fields = new Field[fieldCount + 1];
			fields[0] = new Field("000", leader);
			for (int i = 0; i < fieldCount; i++) {
				tag = ((String) dirs.elementAt(i)).substring(0, TAGL);
				length = Integer.parseInt(((String) dirs.elementAt(i)).substring(TAGL, 7));
				offset = Integer.parseInt(((String) dirs.elementAt(i)).substring(7));
				info = content.substring(offset, offset + length);
				//System.out.println(tag + " " + length + " " + offset + " " + info);
				fields[i + 1] = new Field(tag, info);
			}
		} catch (Exception e) {
			throw new InvalidMARCException();
		} finally {
			dirs = null;
		}
	}


	/**
	 *  Gets the leader attribute of the MARC object
	 *
	 *@return    The leader value
	 *@since
	 */
	public String getLeader() {
		return (leader);
	}


	/**
	 *  Gets the size attribute of the MARC object
	 *
	 *@return    The size value
	 *@since
	 */
	public int getSize() {
		return (size);
	}


	/**
	 *  Gets the field attribute of the MARC object
	 *
	 *@param  tag                       Description of Parameter
	 *@return                           The field value
	 *@exception  NoSuchFieldException  Description of Exception
	 *@since
	 */
	public Field getField(String tag) throws NoSuchFieldException {
		for (int i = 0; i < fields.length; i++) {
			if (tag.compareTo(fields[i].getTag()) == 0) {
				return (fields[i]);
			}
		}
		throw new NoSuchFieldException();
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 *@since
	 */
	public int countFields() {
		return (fields.length);
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 *@since
	 */
	public boolean hasMoreFields() {
		if (currentField < fields.length) {
			return (true);
		} else {
			return (false);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@return                           Description of the Returned Value
	 *@exception  NoSuchFieldException  Description of Exception
	 *@since
	 */
	public Field nextField() throws NoSuchFieldException {
		if (currentField < fields.length) {
			return (fields[currentField++]);
		} else {
			throw new NoSuchFieldException();
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  pattern  Description of Parameter
	 *@return          Description of the Returned Value
	 *@since
	 */
	public boolean exist(String pattern) {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].matchTag(pattern)) {
				return true;
			}
		}
		return false;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  pattern    Description of Parameter
	 *@param  indicator  Description of Parameter
	 *@return            Description of the Returned Value
	 *@since
	 */
	public boolean exist(String pattern, int indicator) {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].matchTag(pattern)) {
				try {
					if (Integer.parseInt(fields[i].getTag()) < 9) {
						continue;
					} else {
						fields[i].getIndicator(indicator);
						return true;
					}
				} catch (NoSuchIndicatorException e) {
					continue;
				}
			}
		}
		return false;
	}


	/**
	 *  Description of the Method
	 *
	 *@since
	 */
	public void finalize() {
		for (int i = 0; i < fields.length; i++) {
			fields[i].finalize();
			fields[i] = null;
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  pattern  Description of Parameter
	 *@param  code     Description of Parameter
	 *@return          Description of the Returned Value
	 *@since
	 */
	public boolean exist(String pattern, char code) {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].matchTag(pattern)) {
				try {
					fields[i].getSubField(code);
					return true;
				} catch (NoSuchSubFieldException e) {
					continue;
				}
			}
		}
		return false;
	}
}


