/*
 * $Author:   johna  $
 * $Revision:   1.30  $
 * $Date:   Oct 05 2007 16:14:50  $
 *
*/

package org.ei.struts.emetrics;

/**
 * Manifest constants for the emetrics.
 *
 * @author David Baptiste
 */

public final class Constants {

    public static final int COUNTER2_DATABASE_REPORT1_ID = 820;
    public static final int DEPARTMENT_REPORT_ID = 720;
    public static final int CUSTOMER_DATABASE_COMBO = 2700;
    public static final int MARKETER_DATABASE_COMBO = 1700;

    public static final int DEFAULT_RANGE_SIZE = 6;

    public static final String USAGE_PROD_ID = "9999";

    public static final String CHART_ID_KEY = "chartId";
    public static final String LAST_REPORT_ID_KEY = "hashid";

    public static final String CUSTOM = "custom";
    public static final String RANGE = "range";
    public static final String SINGLEDAY = "singleday";

    public static final String DESCENDING = "DESC";
    public static final String ASCENDING = "ASC";

    public static final String TOTALPAGES = "pages";
    public static final String TOTALSESSIONS = "totalsessions";
    public static final String TOTALSEARCHES = "totalsearches";
    public static final String CUSTOMER_NAME = "customername";

    public static final String Y = "Y";
    public static final String N = "N";
    public static final String YES = "YES";
    public static final String NO = "NO";
    public static final String ALL = "All";

    public static final String USER_INTERNAL = "marketer";
    public static final String USER_EXTERNAL_CUSTOMER = "customer";
    public static final String USER_EXTERNAL_CONSORTIUM = "consortium";

    public static final String DEFAULT_CONTEXT = "java:comp/env";
//    public static final String EMETRICS_DBCP_POOL = "jdbc/EmetricsPool";
    public static final String EMETRICS_AUTH_DBCP_POOL = "jdbc/AuthPool";

    public static final String ACTION_LIST = "list";
    public static final String ACTION_DESCRIBE = "describe";
    public static final String ACTION_DISPLAY_REPORT = "display";

    public static final String ACTION_SWITCH = "switch";
    public static final String ACTION_RETURN = "return";
    public static final String SUCCESS_KEY = "success";
    public static final String EXPORT_KEY = "export";
    public static final String DESCRIPTIONS_KEY = "descriptions";

    public static final String SYSTEM_FAILURE_KEY = "SystemFailure";
    public static final String SESSION_TIME_OUT_KEY = "SessionTimeOut";
    public static final String FAILURE_KEY = "Failure";
    public static final String USER_CONTAINER_KEY = "UserContainer";
    public static final String SWITCH_VIEW_KEY = "OLDVIEW";

    public static final String PIVOT_COLUMN_KEY = "measure";

    public static final String LAST_MOD = "lastmod";
    public static final String RAW_RESULTSSET = "resultsset";
    public static final String APPLICATION_CONTAINER_KEY = "ApplicationContainer";
    public static final String SIGNON_KEY = "Login";
    public static final String LOGIN_TOKEN_KEY = "LoginTokenKey";
    public static final String IMAGE_RESOURCE_KEY = "IMAGE_RESOURCE_KEY";
    public static final String ID_KEY = "id";
    public static final String REQUESTPAGE = "requestpage";
    public static final String SERVICE_FACTORY_KEY =  "org.ei.struts.backoffice.service.IBackOfficeServiceFactory";
    public static final String SERVICE_INTERFACE_KEY = "org.ei.struts.backoffice.service.IBackOfficeService";
    public static final String SERVLET_STARTTIME_KEY = "starttime";
    public static final String YTD = "99991231";
    public static final String YTD_LABEL = "YTD Total";
    /**
     * The package name for this application.
     */
    public static final String Package = "org.ei.struts.emetrics";

    public static final String EMPTY_STRING = "";

    /**
     * The application scope attribute under which our reports
     * are stored.
     */
    public static final String REPORTS_KEY = "reports";

    /**
     * The request scope bean id for report data for the
     * currently selected report.
     */
    public static final String REPORT_DATA_KEY = "report_data";
    public static final String DISPLAYTABLE_KEY = "displaytable";

    public static final String REPORT_PLUGIN = "report_plugin";
    public static final String DATA_STATUS = "data_status";
    public static final String USER_KEY = "user";
    public static final String ROLES_KEY = "roles";
    public static final String FACTORY_SELECTOR_KEY = "RoleIn";
    public static final String CMD = "cmd";
    public static final String EI_REPORT_ID = "REPID";
    public static final String EI_REPORT_FORMAT_ID = "REPFMT";
    public static final String CUSTOMER_VIEW = "CUSTOMERVIEW";
    public static final String CUSTOMER_ID = "CID";
    public static final String CUSTOMERS = "EMETREICS_CUSTOMERS";
    public static final String CHART_FACTORY_KEY = "CHART_FACTORY";
    public static final String CHART = "CHART";
    public static final String IMAGE_MAP = "IMAGE_MAP";
    public static final String MONTHS = "MONTHS";
    public static final String LASTUPDATE = "LASTUPDATE";
    public static final String MONTH = "month";

}
