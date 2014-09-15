package org.ei.stripes.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.session.UserPreferences;
import org.ei.session.UserSession;
import org.ei.stripes.EVActionBeanContext;
import org.ei.stripes.action.EVPathUrl;

/**
 * The Class CustomizedLogo.
 */
public class CustomizedLogo {

	/** The log4j. */
	private static Logger log4j = Logger.getLogger(CustomizedLogo.class);

	/** The customer image path. */
	private static String  customerImagePath = null;

    /** The imgsrc. */
    private String imgsrc;

	/** The customerurl. */
	private String customerurl;

	/**
	 * Gets the imgsrc.
	 *
	 * @return the imgsrc
	 */
	public String getImgsrc() {
		if (imgsrc == null) return null;
		else return imgsrc ;
	}

	/**
	 * Sets the imgsrc.
	 *
	 * @param imgsrc the new imgsrc
	 */
	public void setImgsrc(String imgsrc) {

		this.imgsrc = imgsrc;
	}

	/**
	 * Gets the customerurl.
	 *
	 * @return the customerurl
	 */
	public String getCustomerurl() {
		return customerurl;
	}

	/**
	 * Sets the customerurl.
	 *
	 * @param customerurl the new customerurl
	 */
	public void setCustomerurl(String customerurl) {
		this.customerurl = customerurl;
	}

	/**
	 * Builds the customized logo.
	 *
	 * @param context the context
	 * @return the customized logo
	 */
	public static CustomizedLogo build(EVActionBeanContext context) {
		CustomizedLogo logo = null;
		try {
			UserSession usersession = context.getUserSession();
			if (usersession == null || usersession.getUser() == null) return null;

			UserPreferences userprefs = context.getUserSession().getUser().getUserPreferences();
			if(userprefs == null || !userprefs.isClientCustomLogo()) return null;

			IEVWebUser evWebUser = usersession.getUser();
			if(evWebUser == null || evWebUser.getAccount() == null ) return null;

			String accountId = evWebUser.getAccount().getAccountId();

			if (!GenericValidator.isBlankOrNull(accountId)) {
					logo = new CustomizedLogo();
					if(customerImagePath == null){
						ApplicationProperties runtimeprops = EVProperties.getApplicationProperties();
						customerImagePath = runtimeprops.getProperty(ApplicationProperties.CUSTOMER_IMAGES_URL_PATH,"https://s3.amazonaws.com/ev-customer-images/");
					}
					logo.setImgsrc(customerImagePath+accountId +".gif");
					if(turkishAccountIds.contains(accountId)){
						logo.setCustomerurl("http://www.ulakbim.gov.tr/cabim/ekual/");
					}else{
						logo.setCustomerurl(EVPathUrl.EV_HOME.value());
					}
			} else return null;
		} catch (Exception e) {
			log4j.error("Unable to get the custom image.",e);
			return null;
		}
		return logo;
	}

	/** The turkish account ids. */
	private static List<String> turkishAccountIds = new ArrayList<String>();

	static {
		turkishAccountIds.add("38618");
		turkishAccountIds.add("264741");
		turkishAccountIds.add("40919");
		turkishAccountIds.add("263675");
		turkishAccountIds.add("263884");
		turkishAccountIds.add("263661");
		turkishAccountIds.add("31788");
		turkishAccountIds.add("50262");
		turkishAccountIds.add("41578");
		turkishAccountIds.add("264758");
		turkishAccountIds.add("52534");
		turkishAccountIds.add("49428");
		turkishAccountIds.add("40859");
		turkishAccountIds.add("264470");
		turkishAccountIds.add("264517");
		turkishAccountIds.add("38658");
		turkishAccountIds.add("67394");
	}

}
