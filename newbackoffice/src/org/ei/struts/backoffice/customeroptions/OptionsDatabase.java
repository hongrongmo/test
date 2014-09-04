/*
 * Created on Jan 7, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.backoffice.customeroptions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.dds.DDS;
import org.ei.struts.backoffice.customeroptions.dds.DDSDatabase;
import org.ei.struts.backoffice.customeroptions.village.VillageDatabase;
import org.ei.struts.backoffice.customeroptions.village.cv.Cv;
import org.ei.struts.backoffice.customeroptions.village.enc2.Enc2;
import org.ei.struts.backoffice.customeroptions.village.ev2.Ev2;
import org.ei.struts.backoffice.customeroptions.village.pv2.Pv2;
/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OptionsDatabase {

	private static Log log = LogFactory.getLog(OptionsDatabase.class);


	public boolean saveOptions(Options opts) {

		boolean result = false;
		String productid = opts.getProduct();

		if(Constants.DDS.equalsIgnoreCase(productid)) {
			result = (new DDSDatabase()).saveDDS(opts);
		}
		else if(Constants.CV.equalsIgnoreCase(productid)) {
			result = (new VillageDatabase()).saveCv(opts);
		}
		else if(Constants.EV2.equalsIgnoreCase(productid)) {
			result = (new VillageDatabase()).saveEv2(opts);
		}
		else if(Constants.PV2.equalsIgnoreCase(productid)) {
			result = (new VillageDatabase()).savePv2(opts);
		}
		else if(Constants.ENC2.equalsIgnoreCase(productid)) {
			result = (new VillageDatabase()).saveEnc2(opts);
		}
		else {
			log.error("No Product id match found for " + productid);
		}
		return result;

	}

	public Options createOptions(String strProductID) {

		Options opts = null;

		if(Constants.EV2.equalsIgnoreCase(strProductID)) {
			Ev2 ev2 = new Ev2();
			ev2.setSelectedOptions(OptionConstants.getDefaultDBOptions(Constants.EV2));
		  ev2.setSelectedDefaultDatabases(OptionConstants.getSelectedDefaultDatabases(Constants.EV2));

			opts = ev2;
		}
		else if(Constants.CV.equalsIgnoreCase(strProductID)) {
			Cv cv = new Cv();
			cv.setSelectedOptions(
				OptionConstants.getDefaultDBOptions(Constants.CV));
			opts = cv;
		}
		else if(Constants.PV2.equalsIgnoreCase(strProductID)) {
			Pv2 pv = new Pv2();
			pv.setSelectedOptions(
				OptionConstants.getDefaultDBOptions(Constants.PV2));
			opts = pv;
		}
		else if(Constants.ENC2.equalsIgnoreCase(strProductID)) {
			Enc2 enc = new Enc2();
			enc.setSelectedOptions(OptionConstants.getDefaultDBOptions(Constants.ENC2));
			enc.setLitbulletins(new String[]{});
			enc.setPatbulletins(new String[]{});
			opts = enc;
		}
		else if(Constants.DDS.equalsIgnoreCase(strProductID)) {
			opts = new DDS();
		}

		return opts;

	}


	public Options getOptionsData(
		String contractid,
		String customerid,
		String productid) {

		Options options = null;

		if(Constants.EV2.equals(productid)) {
			options = (new VillageDatabase()).findEv2(contractid, customerid);
		}
		else if(Constants.CV.equals(productid)) {
			options = (new VillageDatabase()).findCv(contractid, customerid);
		}
		else if(Constants.PV2.equals(productid)) {
			options = (new VillageDatabase()).findPv2(contractid, customerid);
		}
		else if(Constants.ENC2.equals(productid)) {
			options = (new VillageDatabase()).findEnc2(contractid, customerid);
		}
		else if(Constants.DDS.equals(productid)) {
			options = (new DDSDatabase()).findDDS(contractid);
		}

		if(options != null){
			options.setContractID(contractid);
			options.setCustomerID(customerid);
		}

		return options;
	}

}
