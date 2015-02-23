package org.ei.exception;

public class SystemErrorCodes {

	//
	// Unmapped error codes
	//
	public static final int INIT = 0;
	public static final int UNKNOWN = -1;
	public static final int ERROR_XML_CONVERSION_FAILED = -2;
	public static final int CANNOT_PROCESS_XML_ERROR = -3;

	//
	// Search error codes
	//
	public static final int UNKNOWN_SEARCH_ERROR = 1000;
	public static final int SEARCH_QUERY_COMPILATION_FAILED = 1010;
	public static final int SEARCH_QUERY_EXECUTION_FAILED = 1011;
	public static final int INVALID_FIELD = 1012;
	public static final int STRING_TOKENIZER_FAILED_FOR_INPUT_PARMS = 1013;
	public static final int LOAD_NUMBER_FETCH_FROM_DATABASE_FAILED = 1014;
    public static final int COMBINE_HISTORY_UNIQUE_DATABASE_ERROR = 1015;
    public static final int SEARCH_NOT_FOUND = 1016;
    public static final int SAVED_SEARCH_NOT_FOUND = 1017;
    public static final int SEARCH_HISTORY_ERROR = 1018;
    public static final int SEARCH_HISTORY_NOIDEXISTS = 1019;

	public static final int BASE_URL_EMPTY = 1020;
	public static final int RESULT_VIEW_EMPTY = 1021;
	public static final int SEARCH_TYPE_IS_NULL = 1022;
	public static final int NO_DATABASE_SELECTED_IN_REQUEST = 1023;
	public static final int LAST_FOUR_UPDATED_NUMBER_EMPTY = 1020;
	public static final int QUERY_STRING_EMPTY = 1025;
	public static final int QUERY_ENCODING_FAILED = 1026;
    public static final int SESSION_UPDATE_FOR_RECORD_PAGE_FAILED = 1027;
    public static final int NO_DATABASE_ENTITLEMENT = 1027;

	public static final int DOCUMENT_BUILDER_FAILED = 1040;
	public static final int PAGE_ENTRY_BUILDER_FAILED = 1041;
	public static final int INVALID_ARGUMENT_SET_ERROR = 1042;
	public static final int SEARCH_CONTROL_ALREADY_ACTIVE = 1043;

	public static final int FAST_HTTP_CONNECTION_FAILED = 1050;
	public static final int FAST_OUTPUT_STREAM_FETCH_FAILED = 1051;
	public static final int FAST_OUTPUT_STREAM_CLOSING_FAILED = 1052;
	public static final int FAST_HTTP_CONNECTION_RELEASE_FAILED = 1053;
	public static final int PAGE_FETCH_FROM_CACHE_FAILED = 1060;
	public static final int NAVIGATOR_FETCH_FROM_CACHE_FAILED = 1061;
	public static final int NAVIGATOR_DATA_CACHING_UPDATE_FAILED = 1062;
	public static final int PAGE_DATA_CACHING_UPDATE_FAILED = 1063;
	public static final int FAST_DATA_FETCH_FOR_CACHE_FAILED = 1064;
	public static final int NAVIGATOR_STATE_PARSER_CONFIGURATION_FAILED = 1070;
	public static final int NAVIGATOR_STATE_PARSING_FAILED = 1071;
	public static final int NAVIGATOR_STATE_IO_FAILED = 1072;
	public static final int NAVIGATOR_STATE_XPATH_EVALUATION_FAILED = 1073;
	public static final int EBOOK_REMOVED = 1074;

	//
	// Session error codes
	//
	public static final int UNKNOWN_SESSION_ERROR = 2000;
	public static final int SESSION_RETRIEVE_ERROR = 2001;
	public static final int SESSION_CREATE_ERROR = 2002;
	public static final int SESSION_UPDATE_ERROR = 2003;
	public static final int TEXTZONE_INVALID_VALUE_ERROR = 2004;
	public static final int LOCAL_HOLDING_ERROR = 2005;
	public static final int ENTRY_TOKEN_ERROR = 2006;
	public static final int CARTRIDGE_ERROR = 2010;

	//
	// Auth System error codes
	//
	public static final int UNKNOWN_AUTH_SYSTEM_ERROR = 3000;
	public static final int USER_ENTITLEMENT_FETCH_EXCETPTION = 3010;

	//
	// Infrastructure error codes
	//
	public static final int UNKNOWN_INFRASTRUCTURE_ERROR = 5000;
	public static final int CONTENT_CONFIG_PARSE_ERROR = 5001;
	public static final int BASKET_ERROR = 5002;
	public static final int PAGE_CACHE_ERROR = 5003;
	public static final int HISTORY_ERROR = 5004;
	public static final int PARSE_ERROR = 5005;
	public static final int SAVED_SEARCH_ERROR = 5006;
	public static final int SAVED_RECORDS_ERROR = 5006;
	public static final int SESSION_SEARCH_ERROR = 5007;
	public static final int NAVIGATOR_STATE_ERROR = 5009;
	public static final int PERSONAL_PROFILE_ERROR = 5010;
    public static final int PERSONAL_PROFILE_MERGE_ERROR = 5011;
    public static final int RUNTIME_PROPERTIES_ERROR = 5012;
    public static final int CLASSNODEMANAGER_ERROR = 5013;
    public static final int PID_DESCRIPTION_ERROR = 5014;
    public static final int USER_PREFERENCE_ERROR = 5015;
	public static final int PATENT_REF_URL_CREATION_FAILED = 5108;

	public static final int THES_PROCESSING_ERROR = 5200;
	public static final int THES_PATH_ERROR = 5201;

	public static final int RSS_FEED_ERROR = 5300;

	//
	// Service error codes
	//
	public static final int UNKNOWN_SERVICE_ERROR = 6000;
	public static final int AWS_S3_BAD_SIGNED_URL = 6001;
	public static final int AWS_S3_CANNOT_GET_DOCVIEW_BUCKET = 6002;
	public static final int AWS_S3_CANNOT_GET_BULLETIN_BUCKET = 6003;
	public static final int AWS_S3_CANNOT_GET_CUSTOMERIMAGE_BUCKET = 6004;
	public static final int AWS_S3_CANNOT_GET_REFEREX_BUCKET = 6005;
	public static final int REFEREX_ISBN_NOT_MAPPED = 6006;
    public static final int AWS_S3_CANNOT_GET_CUSTOMERIMAGES_LIST = 6007;
    public static final int AWS_SES_FAILURE = 6008;

	public static final int DATA_SERVICE_CONNECTION_ERROR = 6110;
	public static final int DATA_SERVICE_BAD_URL = 6111;
	public static final int PAT_PDF_ERROR = 6112;

	public static final int CS_ENTITLEMENTS_FETCH_ERROR = 6200;
	public static final int CS_USER_FENCE_FETCH_ERROR = 6201;
	public static final int CS_DEFAULT_FENCE_FETCH_ERROR = 6202;
	public static final int CS_USER_TEXTZONE_FETCH_ERROR = 6203;
	public static final int CS_DEFAULT_TEXTZONE_FETCH_ERROR = 6204;
	public static final int CSWS_NO_PROXY = 6203;
	public static final int FSWS_NO_PROXY = 6205;
	public static final int FSWS_SEARCH_FETCH_ERROR = 6206;
	public static final int XABS_MD_NO_PROXY = 6207;
	public static final int XABS_MD_FETCH_ERROR = 6208;

	public static final int CARS_RESPONSE_PROCESSING_ERROR = 6300;
	public static final int CARS_RESPONSE_PARSING_ERROR = 6301;
	public static final int XPATH_COMPILE_ERROR = 6302;
	public static final int XSL_TRANSFORM_ERROR = 6303;
	public static final int CARS_TEMPLATE_ERROR = 6304;

	public static final int REST_RESOURCE_ERROR = 6400;

}
