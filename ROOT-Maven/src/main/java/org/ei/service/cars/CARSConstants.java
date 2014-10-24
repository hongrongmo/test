package org.ei.service.cars;



public class CARSConstants {
	public static final String CARS_GENERIC_FORWARD = "CARSGeneric";
	public static final String CARS_POPUP_FORWARD = "CARSPopup";
	public static final String CARS_ERROR_FORWARD = "CARSErrorPage";
	public static final String CARS_ONLYLOGO_FORWARD = "CARSOnlyLogo";
	public static final String CARS_SETTINGS_FORWARD = "CARSSettings";
	public static final String CARS_PATHCHOICE_FORWARD = "PATHCHOICE";
	public static final String CARS_INSTITUTION_FORWARD = "INSTITUTION";
	public static final String CARS_LOGINFULL_FORWARD="CARSLoginFull";
	public static final String GZIP = "gzip,deflate";
	
    public static String getLocalAuthenticateURI() {
        return 
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_BASE_URI) +
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.INITIAL_AUTHENTICATE_URI) +
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_URL_EXTN);
    }

    public static String getLocalLoginFullURI() {
        return 
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_BASE_URI) +
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.LOGIN_FULL_URI) +
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_URL_EXTN);
    }

    public static String getLocalProfileURI() {
        return 
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_BASE_URI) +
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.PROFILE_DISPLAY_URI) +
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_URL_EXTN);
    }
    
    public static String getLocalSettingsURI() {
        return 
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_BASE_URI) +
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.SETTINGS_URI) +
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_URL_EXTN);
    }
    
    /*
     * This url will be used for showing personalization activation required page when View Saved Searches, Alert and Folders links clicked
     * for Shibboleth anon user 
     */
    public static String getLocalActivatePersonalizationURI() {
        return 
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_BASE_URI) +
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.ACTIVATE_PERSONALIZATION_URI) +
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_URL_EXTN);
    }
    
}