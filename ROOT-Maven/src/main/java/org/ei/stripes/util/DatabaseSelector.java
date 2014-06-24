package org.ei.stripes.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.books.collections.ReferexCollection;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DatabaseConfigException;
import org.ei.domain.DriverConfig;
import org.ei.util.StringUtil;

/**
 * This class creates the appropriate database selection for a user.
 *
 * @author harovetm
 *
 */
public final class DatabaseSelector {
	private final static Logger log4j = Logger.getLogger(DatabaseSelector.class);

	// this class removes non-hosted databases (USPTO and CRC)
	// and backfiles from mask value
	public static int getScrubbedMask(int mask) {
		int exmasks[] = { DatabaseConfig.USPTO_MASK, DatabaseConfig.CRC_MASK,
				DatabaseConfig.C84_MASK, DatabaseConfig.IBF_MASK };

		for (int idx = 0; idx < exmasks.length; idx++) {
			if ((mask & exmasks[idx]) == exmasks[idx]) {
				mask -= exmasks[idx];
			}
		}

		return mask;
	}

	// list of all whole masks which can be displayed on the page
	protected static int[] dbMasks = new int[] { DatabaseConfig.CPX_MASK,
			DatabaseConfig.INS_MASK, DatabaseConfig.IBS_MASK,
			DatabaseConfig.NTI_MASK, DatabaseConfig.GEO_MASK,
			DatabaseConfig.EUP_MASK, DatabaseConfig.UPA_MASK,
			DatabaseConfig.PAG_MASK, DatabaseConfig.CBF_MASK,
			DatabaseConfig.CHM_MASK, DatabaseConfig.PCH_MASK,
			DatabaseConfig.ELT_MASK, DatabaseConfig.EPT_MASK,
			DatabaseConfig.CBN_MASK, DatabaseConfig.GRF_MASK, };
	static {
		Arrays.sort(dbMasks);
	}

	// is this mask a whole power of 2 which is in the sorted Array dbMasks
	protected static boolean isWholeDBMask(int amask) {
		return (Arrays.binarySearch(dbMasks, amask) >= 0);
	}

	/**
	 * Checkbox object for non-Referex sources
	 */
	public class DatabaseCheckbox implements Comparable<DatabaseCheckbox> {
		// private String type = "checkbox";
		// private String chkname = "database";
		// private String onclickevent = "change(\'database\')";

		private String chkid = null;
		private String chkvalue = null;
		private boolean checked = false;
		private String strlabel = null;
		private int sortValue = 0;
		private int umask = 0;

		public int compareTo(DatabaseCheckbox checkboxcompare) {
			DatabaseCheckbox d = checkboxcompare;
			if (d != null) {
				if (getSortValue() < d.getSortValue()) {
					return -1;
				} else if (getSortValue() > d.getSortValue()) {
					return 1;
				} else if (getSortValue() == d.getSortValue()) {
					return 0;
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		}

		public void setLabel(String label) {
			strlabel = label;
		}

		public String getLabel() {
			return strlabel;
		}

		public void setId(String id) {
			chkid = id;
		}

		public String getId() {
			return chkid;
		}

		public void setChecked(boolean ischecked) {
			checked = ischecked;
		}

		public boolean isChecked() {
			return checked;
		}

		public DatabaseCheckbox(int value) {
			chkvalue = String.valueOf(value);
		}

		public String toString() {
			StringBuffer dbcheckbox = new StringBuffer();
			dbcheckbox
					.append("<input style=\"vertical-align:middle\" type=\"checkbox\" name=\"database\" class=\"databasechkbox\"");
			dbcheckbox.append(" value=\"" + chkvalue + "\"");
			dbcheckbox.append(" id=\"" + chkid + "chkbx\"");
			dbcheckbox.append((checked ? " checked=\"checked\"" : ""));
			dbcheckbox.append(" />");

			dbcheckbox.append("<label class=\"SmBlackText\" for=\"").append(
					chkid).append("chkbx\">");
			dbcheckbox.append(strlabel);
			dbcheckbox.append("</label>");

			return dbcheckbox.toString();
		}

		public int getSortValue() {
			return sortValue;
		}

		public void setSortValue(int sortValue) {
			this.sortValue = sortValue;
		}

		public int getUmask() {
			return umask;
		}

		public void setUmask(int umask) {
			this.umask = umask;
		}
	}

	/**
	 * Returns a list of the available databases for output as checkboxes
	 *
	 * @return List of DatabaseCheckbox objects
	 * @throws DatabaseConfigException
	 */
	public List<DatabaseSelector.DatabaseCheckbox> getDatabaseCheckboxes(int userMask, int selectedMask) throws DatabaseConfigException {

		int sum = 0;
	    int umask = 0;
	    umask = getScrubbedMask(userMask);


        List lstinnerCheckboxes = new LinkedList();
        StringBuffer innercheckboxes = new StringBuffer();
        for (int x = 0; x < dbMasks.length; x++) {
          if ((umask & dbMasks[x]) == dbMasks[x]) {

            Database[] d = (DatabaseConfig.getInstance(DriverConfig.getDriverTable())).getDatabases(dbMasks[x]);

            DatabaseCheckbox dbcheck = new DatabaseCheckbox(dbMasks[x]);

            for (int y = 0; y < d.length; y++)
            {
              if (d[y] != null)
              {
                sum += d[y].getMask();
                if (!StringUtil.EMPTY_STRING.equals(innercheckboxes))
                {
                  dbcheck.setId(d[y].getID());
                  if ((selectedMask & dbMasks[x]) == dbMasks[x])
                  {
                    dbcheck.setChecked(true);
                  }
                }
                dbcheck.setSortValue(d[y].getSortValue());
                dbcheck.setLabel(d[y].getName());
                dbcheck.setUmask(umask);
                lstinnerCheckboxes.add(dbcheck);
              }
            } // for
          } // if
        } // for

        // sort checkboxes based on sort value
        Collections.sort(lstinnerCheckboxes);

        return lstinnerCheckboxes;
	}

	/**
	 * Checkbox object for non-Referex sources
	 */
	public class ReferexCheckbox {
		private String selcol;
		private ReferexCollection refcoll;

		public ReferexCheckbox(ReferexCollection refcoll) {
			this.refcoll = refcoll;
		}

		public String toString() {
			/*
			<input id="chkELE" type="checkbox" onclick="change('database')" value="ELE" name="col">
			<label class="SmBlackText" for="chkELE">Electronics &amp; Electrical</label>
			*/
			StringBuffer output = new StringBuffer();
			output.append("<input name=\"col\"");
			if ((selcol != null)
					&& (selcol.indexOf(refcoll.getAbbrev()) >= 0)) {
				output
						.append(" checked=\"checked\" style=\"vertical-align:middle;\" ");
			}
			output.append("type=\"checkbox\" id=\"chk");
			output.append(refcoll.getAbbrev());
			output.append("\" value=\"");
			output.append(refcoll.getAbbrev());
			output.append("\" onclick=\"change('");
			output.append(refcoll.getAbbrev());
			output.append("');\" />");
			output.append("<label for=\"chk");
			output.append(refcoll.getAbbrev()).append("\">");
			output.append(refcoll.getDisplayName());
			output.append("</label>");

			return output.toString();
		}

		//
		// GETTERS/SETTERS
		//
		public String getSelcol() {
			return selcol;
		}

		public void setSelcol(String selcol) {
			this.selcol = selcol;
		}

		public String getAbbrev() {
			return refcoll.getAbbrev();
		}

		public String getDisplayname() {
			return refcoll.getDisplayName();
		}

		public String getShortname() {
			return refcoll.getShortname();
		}
}

	/**
	 * Return list of checkboxes based on Referex entitlements
	 *
	 * @param creds
	 *            ???
	 * @param selectedColls
	 *            ???
	 * @return
	 */
	public List<ReferexCheckbox> getReferexCheckboxes(String creds,
			String selectedColls) {
		List<ReferexCheckbox> checkboxlist = new ArrayList<ReferexCheckbox>();

		// Must have creds to continue
		if (GenericValidator.isBlankOrNull(creds)) {
			log4j.warn("No credentials for referex checkboxes!");
			return checkboxlist;
		}
		String credentials[] = creds.split(";");
		Arrays.sort(credentials);

		// Must have db config for referex
		DatabaseConfig dbConfig = DatabaseConfig.getInstance();
		Database pagDatabase = dbConfig.getDatabase("pag");
		if (pagDatabase == null) {
			log4j.warn("No database configuration referex checkboxes!");
			return checkboxlist;
		}

		// get a list of the aviable referex collections
		// based on users credentials
		List<ReferexCollection> sfs = new ArrayList<ReferexCollection>();
		Map colls = pagDatabase.getDataDictionary().getClassCodes();
		Iterator itrColls = colls.keySet().iterator();
		while (itrColls.hasNext()) {
			String acoll = (String) itrColls.next();

			// loop through all the cartridges and see if any start with
			// a collection name and if it does add the collection to the list
			// added to remove dup cartridge
			if(!acoll.startsWith("TNF"))
			{
				for (int cartIndex = 0; cartIndex < credentials.length; cartIndex++) {
					if (credentials[cartIndex].toUpperCase().indexOf(
							acoll.toUpperCase()) == 0) {
						sfs.add(ReferexCollection.getCollection(acoll));
						break;
					}
				}
			}
			else if(acoll.length()>5)
			{

				for (int cartIndex = 0; cartIndex < credentials.length; cartIndex++) {
					if(credentials[cartIndex].toUpperCase().startsWith("TNF"))
					{
						if (credentials[cartIndex].toUpperCase().indexOf(
								acoll.toUpperCase()) == 0)
						{
							boolean flag = true;

							for(int j=0;j<credentials.length;j++)
							{
								if(credentials[j].toUpperCase().indexOf(acoll.toUpperCase().substring(3,6)) == 0)
								{
									flag = false;
									break;
								}
							}
							if(flag)
							{
								sfs.add(ReferexCollection.getCollection(acoll));
								break;
							}
						}
					}
				}
			}


		}
		Collections.sort(sfs);

		//
		// List should have at least entry
		//
		if (sfs.isEmpty()) {
			log4j.warn("No referex collections found in user credentials!");
			return null;
		}

		//
		// Build checkboxes
		//
		Iterator sfsItr = sfs.iterator();
		for (int colindex = 0; sfsItr.hasNext(); colindex++) {
			ReferexCollection value = (ReferexCollection) sfsItr.next();
			ReferexCheckbox checkbox = new ReferexCheckbox(value);
			if (selectedColls != null && selectedColls.indexOf(checkbox.getAbbrev().toUpperCase()) >= 0) {
				checkbox.setSelcol(checkbox.getAbbrev());
			}
			checkboxlist.add(checkbox);
		}

		// Return list!
		return checkboxlist;


	}

}
