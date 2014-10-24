package org.ei.service.amazon.dynamodb;

public class DynamoDBTables {
	
	public static final String TABLE_RUNTIME_PROPERTIES = "runtimeproperties";
	
	
	public static final String COLUMN_RUNTIME_PROPERTIES_DEFAULT = "default";
	public static final String COLUMN_RUNTIME_PROPERTIES_LOCAL = "local";
	public static final String COLUMN_RUNTIME_PROPERTIES_KEY = "key";
	public static final String COLUMN_RUNTIME_PROPERTIES_CERT = "cert";
	public static final String COLUMN_RUNTIME_PROPERTIES_DEV = "dev";
    public static final String COLUMN_RUNTIME_PROPERTIES_RELEASE = "release";
    public static final String COLUMN_RUNTIME_PROPERTIES_PROD = "prod";
	
	
	public static final String TABLE_USERPREFS = "userprefs";
	
    public static final String COLUMN_EVPREFS_SORT = "SORT";
    public static final String COLUMN_EVPREFS_DL_OUTPUT = "DL_OUTPUT";
    public static final String COLUMN_EVPREFS_DL_FORMAT = "DL_FORMAT";
    public static final String COLUMN_EVPREFS_RESULTS_PER_PAGE = "RESULTS_PER_PAGE";
    public static final String COLUMN_EVPREFS_SHOW_PREV = "SHOW_PREVIEW";
	
}
