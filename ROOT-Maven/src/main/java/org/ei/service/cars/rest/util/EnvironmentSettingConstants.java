package org.ei.service.cars.rest.util;

/**
 * This class contains the environment setting constants that are defined for
 * an application.  Each property correlates to a configuration variable 
 * defined in the SMAPI configuration files.  This acts as an interface that
 * separates the name of the variable defined in SMAPI from the name the 
 * application code base uses to obtain the variable value from SMAPI.
 * @modelguid {1D574185-B443-4EEF-937F-7A7BE1D47AE0}
 */
public final class EnvironmentSettingConstants {
    /**
     * Private constructor since this class should not be instantiated.
     * @modelguid {AE05EBFB-E78D-4C61-8036-D12883BAFBAA}
     */
    EnvironmentSettingConstants() { }

    /** @modelguid {1A8AD910-7E7E-4990-B420-3FAA3D946BF3} */
    public static final String RETRIEVAL_CURRENT_BATCH_SIZE =
        "RETRIEVAL_CURRENT_BATCH_SIZE";
        
    /**This value indicates the maximum number of search web service proxies
        to allow in the pool.  0 means unlimited.  Changes to this value
        will not be picked up until the app has been restarted.
        If this value is set to something other than 0, then
        MAX_POOL_SEARCH_PROXY_WAIT_TIME should also be set to something
        other than 0.  
     * */
    public static final String MAX_POOL_SEARCH_PROXIES =
        "MAX_POOL_SEARCH_PROXIES";

    /** @modelguid {641BF68F-EDFE-49B4-AC76-D45EC7C8B937} */
    public static final String MAX_POOL_SEARCH_PROXY_WAIT_TIME = 
        "MAX_POOL_SEARCH_PROXY_WAIT_TIME";
        
    /** @modelguid {48F160FA-CAB0-44CD-B584-D758D3289180} */
    public static final String CSWS_METADATA_REFRESH_PERIOD =
        "CSWS_METADATA_REFRESH_PERIOD";

    // webservice header constants
    /**Constant for consumer of webservice 
     * @modelguid {F25F1E9D-4EB5-474F-9730-A0480EA9C081}
     */
    public static final String CONSUMER_APP = "CONSUMER_APP";
    
    /**Constant for client of webservice 
     * @modelguid {3B6893E4-6A79-46D4-914F-0A31DB78CDD1}
     */
    public static final String CONSUMER_CLIENT = "CONSUMER_CLIENT";
    
    /**Constant for log level of webservice 
     * @modelguid {FDEB67CD-A03F-4A68-8A2E-A8B858C3B3D1}
     */
    public static final String WEBSERVICE_LOG_LEVEL = "WEBSERVICE_LOG_LEVEL";
    
    /**Constant for webservice version 
     * @modelguid {726113F7-E8B4-46E5-BC70-1D39B9D8C178}
     */
    public static final String WEBSERVICE_VERSION = "WEBSERVICE_VERSION";
    
    /** Constant for wait time to retrieve a proxy
     * @modelguid {D374A232-8A33-4DB9-91C4-535F0EA125B5}
     */
    public static final String MAX_POOL_ABSRET_PRIMARY_PROXY_WAIT_TIME = 
                        "MAX_POOL_ABSRET_PRIMARY_PROXY_WAIT_TIME";

    /** 
     * Constant for wait time to retrieve a proxy
     */
    public static final String MAX_POOL_ABSRET_SECONDARY_PROXY_WAIT_TIME = 
                        "MAX_POOL_ABSRET_SECONDARY_PROXY_WAIT_TIME";

    /** Constant for wait time to retrieve a proxy (author profile) */
    public static final String MAX_POOL_AUTHOR_PROXY_WAIT_TIME = 
                            "MAX_POOL_AUTHOR_PROXY_WAIT_TIME";

    /** Constant for number of proxies to create for the pool */
    public static final String MAX_POOL_AUTHOR_PRIMARY_PROXIES = 
                        "MAX_POOL_AUTHOR_PRIMARY_PROXIES";
                
    /** Constant for number of proxies to create for the pool
     * @modelguid {C176DBE3-7540-4CAB-BDCB-4905A7D5E3F4}
     */
    public static final String MAX_POOL_ABSRET_PRIMARY_PROXIES = 
                        "MAX_POOL_ABSRET_PRIMARY_PROXIES";

    /** Constant for number of proxies to create for the pool
     * @modelguid {C176DBE3-7540-4CAB-BDCB-4905A7D5E3F4}
     */
    public static final String MAX_POOL_ABSRET_SECONDARY_PROXIES = 
                        "MAX_POOL_ABSRET_SECONDARY_PROXIES";
                                              
    /** 
     * Constant for number of proxies to create for the pool
     */
    public static final String MAX_POOL_AE_RUNTIME_WS_PROXIES =
                         "MAX_POOL_AE_RUNTIME_WS_PROXIES";
    
    /** 
     * Constant for wait time to retrieve a proxy
     */
    public static final String MAX_POOL_AE_RUNTIME_WS_PROXY_WAIT_TIME =
                        "MAX_POOL_AE_RUNTIME_WS_PROXY_WAIT_TIME";
                        
    /** Constant for number of proxies to create for the pool
     * @modelguid {71E29007-50AC-4431-A773-D796FCEAB702}
     */
    public static final String MAX_POOL_CSWS_PROXIES =
                         "MAX_POOL_CSWS_PROXIES";
    
    /** Constant for wait time to retrieve a proxy
     * @modelguid {AB3E5CBB-56D4-4D13-B655-4DC24E4E3B81}
     */
    public static final String MAX_POOL_CSWS_PROXY_WAIT_TIME =
                        "MAX_POOL_CSWS_PROXY_WAIT_TIME";

    /** Constant for number of proxies to create for the pool
     * @modelguid {D2720152-A477-48B0-ADCC-6942268D9559}
     */
    public static final String MAX_POOL_SSWS_PROXIES = 
                        "MAX_POOL_SSWS_PROXIES";

    /** Constant for wait time to retrieve a proxy
     * @modelguid {2889F034-0352-4BC7-A914-0F8D35FC42EE}
     */
    public static final String MAX_POOL_SSWS_PROXY_WAIT_TIME =
                        "MAX_POOL_SSWS_PROXY_WAIT_TIME";
    
    /** Constant for number of proxies to create for the pool
     * @modelguid {0AB88465-DA14-4B69-BA3C-5E2C5BCC3FCD}
     */
    public static final String MAX_POOL_ABSTRACTS_METADATA_WS_PROXIES =
                        "MAX_POOL_ABSTRACTS_METADATA_WS_PROXIES";


    /** Constant for wait time to retrieve a proxy
     * @modelguid {1413013D-81BC-48BC-B0D9-5EC922FEC09F}
     */
    public static final String MAX_POOL_ABSTRACTS_METADATA_WS_PROXY_WAIT_TIME = 
                        "MAX_POOL_ABSTRACTS_METADATA_WS_PROXY_WAIT_TIME";

    /** Constant for number of proxies to create for the pool
     * @modelguid {55C8F839-BE7F-404D-A70D-508128A892BF}
     */
    public static final String MAX_POOL_JOURNAL_METADATA_WS_PROXIES =
                        "MAX_POOL_JOURNAL_METADATA_WS_PROXIES";

    /** Constant for wait time to retrieve a proxy
     * @modelguid {9C3EE886-1940-4DFF-A1B2-AE750CABC110}
     */
    public static final String MAX_POOL_JOURNAL_METADATA_WS_PROXY_WAIT_TIME = 
                        "MAX_POOL_JOURNAL_METADATA_WS_PROXY_WAIT_TIME";
                        
    /** Constant for number of proxies to create for the pool
     * @modelguid {18DAABBA-EA4A-4855-9166-4C98A94776AD}
     */
    public static final String MAX_POOL_CTO_WS_PROXIES =
                        "MAX_POOL_CTO_WS_PROXIES";

    /** Constant for wait time to retrieve a proxy
     * @modelguid {473E8C8C-E563-4114-B9A7-6141F3709736}
     */
    public static final String MAX_POOL_CTO_WS_PROXY_WAIT_TIME = 
                        "MAX_POOL_CTO_WS_PROXY_WAIT_TIME";
    /** Constant for wait time to retrieve a proxy
     * @modelguid {C0332657-333C-4296-B7E3-A55E94ABA4D0}
     */
    public static final String MAX_POOL_REFWORKS_PROXY_WAIT_TIME = 
                        "MAX_POOL_REFWORKS_PROXY_WAIT_TIME";
    
    /** Constant for number of proxies to create for the pool
     * @modelguid {F88FB0D3-DEB0-4C29-A9AC-2B3C6B8D2D32}
     */
    public static final String MAX_POOL_REFWORKS_PROXIES = 
                        "MAX_POOL_REFWORKS_PROXIES";
    
    /** Constant for number of proxies to create for the pool
     * @modelguid {7F943DD3-68F1-4137-9127-05335C05723F}
     */
    public static final String MAX_POOL_ELNSEARCH_PROXIES = "MAX_POOL_ELNSEARCH_PROXIES";
    
    /** Constant for wait time to retrieve a proxy
     * @modelguid {AB56928A-F117-436B-B87F-5A731C6C42C4}
     */
    public static final String MAX_POOL_ELNSEARCH_PROXY_WAIT_TIME = "MAX_POOL_ELNSEARCH_PROXY_WAIT_TIME";

    /** Constant for number of proxies to create for the pool
     * @modelguid {7C7E8E17-73E4-434A-BE2F-203647BE570D}
     */
    public static final String MAX_POOL_RWMET_PROXIES = "MAX_POOL_RWMET_PROXIES";
    
    /** Constant for wait time to retrieve a proxy
     * @modelguid {34050F30-DDA3-44B3-98BD-4CF6D89325E5}
     */
    public static final String MAX_POOL_RWMET_PROXY_WAIT_TIME = "MAX_POOL_RWMET_PROXY_WAIT_TIME";
        
    /** This value represents the time out value for the fast webservice call.
     * @modelguid {58D865D9-295E-4CC6-9FE5-58F236EC46CC}
     */           
    public static final String MAX_FASTSRCH_WEBSERVICE_CALL_TIME_OUT_VALUE =
                            "MAX_FASTSRCH_WEBSERVICE_CALL_TIME_OUT_VALUE";

    /** This value represents the time out value for the absret webservice call. 
     * @modelguid {CBCA117B-0B46-4F79-84DE-1FFAEDF79615}
     */           
    public static final String MAX_ABSRET_WEBSERVICE_CALL_TIME_OUT_VALUE =
                            "MAX_ABSRET_WEBSERVICE_CALL_TIME_OUT_VALUE";
    /** This value represents the time out value for the absret webservice call. 
    * @modelguid {CBCA117B-0B46-4F79-84DE-1FFAEDF79615}
    */           
    public static final String MAX_AUTHOR_WEBSERVICE_CALL_TIME_OUT_VALUE =
                              "MAX_AUTHOR_WEBSERVICE_CALL_TIME_OUT_VALUE";         
                            
    /** This value represents the time out value for the absmet webservice call. 
     * @modelguid {33A91DA0-815E-4664-8CA3-0697F4D37470}
     */           
    public static final String MAX_ABSMET_WEBSERVICE_CALL_TIME_OUT_VALUE =
                            "MAX_ABSMET_WEBSERVICE_CALL_TIME_OUT_VALUE";
                            
    /** 
     * This value represents the time out value for the ae runtime webservice call. 
     */           
    public static final String MAX_AE_RUNTIME_WEBSERVICE_CALL_TIME_OUT_VALUE =
                            "MAX_AE_RUNTIME_WEBSERVICE_CALL_TIME_OUT_VALUE";                            
                            
    /** This value represents the time out value for the jnlmet webservice call. 
     * @modelguid {FFB619DE-2EE3-4A68-997C-6F8D3A2B2C85}
     */           
    public static final String MAX_JNLMET_WEBSERVICE_CALL_TIME_OUT_VALUE =
                            "MAX_JNLMET_WEBSERVICE_CALL_TIME_OUT_VALUE";
                                    
    /** This value represents the time out value for the rwmet webservice call. 
     * @modelguid {B6356441-2048-4BAF-B894-BBA2B8C81478}
     */           
    public static final String MAX_RWMET_WEBSERVICE_CALL_TIME_OUT_VALUE =
                            "MAX_RWMET_WEBSERVICE_CALL_TIME_OUT_VALUE";
                                    
    /** This value represents the time out value for the csas webservice call. 
     * @modelguid {13A2423C-215E-4A77-B75D-3EFDDF2752D3}
     */           
    public static final String MAX_CSAS_WEBSERVICE_CALL_TIME_OUT_VALUE =
                            "MAX_CSAS_WEBSERVICE_CALL_TIME_OUT_VALUE";
                                    
    /** This value represents the time out value for the ssws webservice call. 
     * @modelguid {B392FE38-A262-40F6-9BE3-51E6764D2206}
     */           
    public static final String MAX_SSWS_WEBSERVICE_CALL_TIME_OUT_VALUE =
                            "MAX_SSWS_WEBSERVICE_CALL_TIME_OUT_VALUE";
                                    
    /** This value represents the time out value for the cto webservice call. 
     * @modelguid {D621FEBE-1A9D-438F-806E-BFEB6ACE59A3}
     */           
    public static final String MAX_CTO_WEBSERVICE_CALL_TIME_OUT_VALUE =
                            "MAX_CTO_WEBSERVICE_CALL_TIME_OUT_VALUE";
                            
    /** This value represents the time out value for the elnsrch webservice call. 
     * @modelguid {00DC8DF0-C0A5-4DF5-B1D5-DBF72DFF71CF}
     */           
    public static final String MAX_ELNSRCH_WEBSERVICE_CALL_TIME_OUT_VALUE =
                            "MAX_ELNSRCH_WEBSERVICE_CALL_TIME_OUT_VALUE";
                           
    /** 
     * This value represents the time to sleep between each Absmet webservice requests 
     * when the cache time tokens are different. 
     */           
    public static final String ABSMET_WEBSERVICE_SLEEP_TIME_BETWEEN_REQUESTS =
                            "ABSMET_WEBSERVICE_SLEEP_TIME_BETWEEN_REQUESTS"; 

    /**
     *  This value represents the time to sleep between each Jnlmet webservice 
     *  requests when the cache time tokens are different.
     */           
    public static final String JNLMET_WEBSERVICE_SLEEP_TIME_BETWEEN_REQUESTS =
                            "JNLMET_WEBSERVICE_SLEEP_TIME_BETWEEN_REQUESTS"; 
                                                        
    /** 
     * This value represents the maximum allowed retries to be performed for 
     * successfully retrieveing Absmet response data.  
     */           
    public static final String MAX_ABSMET_WEBSERVICE_RETRIES =
                                        "MAX_ABSMET_WEBSERVICE_RETRIES"; 

    /** 
     * This value represents the maximum allowed retries to be performed for 
     * successfully retrieveing Jnlmet response data.  
     */           
    public static final String MAX_JNLMET_WEBSERVICE_RETRIES =
                                        "MAX_JNLMET_WEBSERVICE_RETRIES"; 
               
    /**
     * This value represents the item count value used in response payloads 
     * used by AbsMet, JnlMet, and XAbsMet for chunking large response payloads. 
     * The item count is used to indicate the maximum number of items that a 
     * client would like to receive from the
     * metadata service. Note that there is a server side maximum value that 
     * overrides the client-specified value in the request payload
     */           
    public static final String MAX_ABSMET_WEBSERVICE_ITEM_RETRIEVAL_COUNT =
                                        "MAX_ABSMET_WEBSERVICE_ITEM_RETRIEVAL_COUNT"; 
      
    /**
     * This value represents the item count value used in response payloads 
     * used by AbsMet, JnlMet, and XAbsMet for chunking large response payloads. 
     * The item count is used to indicate the maximum number of items that a 
     * client would like to receive from the
     * metadata service. Note that there is a server side maximum value that 
     * overrides the client-specified value in the request payload
     */           
    public static final String MAX_JNLMET_WEBSERVICE_ITEM_RETRIEVAL_COUNT =
                                        "MAX_JNLMET_WEBSERVICE_ITEM_RETRIEVAL_COUNT"; 
                                                 
    /**
     * 
     *   If this element is set to &apos;true&apos; then dummy records will be
     *   filtered out when building the Issue and Volume list
     *   in the Absmet response payload.  On the other hand, if this element is
     *   set to &apos;false&apos; then dummy records will be included when 
     *   building the issue and volume lists.  Note that dummy records
     *   are currently (as of Feb 15 2005) not searchable at Fast.
     */           
    public static final String SUPPRESS_DUMMY_RECORDS_FROM_ABSMET=
                                        "SUPPRESS_DUMMY_RECORDS_FROM_ABSMET"; 
    
    /**
     * If this element is set to &apos;true&apos;then Articles-In-Press (AIP) records 
     * will be filtered out when building the IssueInfoList and VolumeInfoList
     * in the response payload.  On the other hand, if this element is
     * set to &apos;false&apos; then AIP documents will be included when 
     * building the issue and volume lists.
     * Note: Revisit this setting once "Article In Press" feature function is 
     * implemented
     */           
    public static final String SUPPRESS_ARTICLES_IN_PRESS_FROM_ABSMET =
                                            "SUPPRESS_ARTICLES_IN_PRESS_FROM_ABSMET";

    /**
     * This defines the location and name of the XSL Cache Configuration file.
     */
    public static final String XSL_CACHE_CONFIG_FILE = "XSL_CACHE_CONFIG_FILE";                                                

    /** The key that the vendor ID is stored under as a SMAPI variable */
    public static final String REFWORKS_VENDOR_ID_KEY = "REFWORKS_VENDOR_ID";

    /**
     * This variable defines the path of the URL that will be used for the Refworks XML
     * calls.  The noOverride value will be set to true so that this value
     * can be changed during the RefWorks DEV phase.  This value is appended to the 
     * base server URL returned by the CSAS.
     */
    public static final String REFWORKS_WEBSERVICE_ENDPOINT = 
        "REFWORKS_WEBSERVICE_ENDPOINT";
        
    /**
     * This variable is the path of the URL that Scopus points to in order to get 
     * the proper RefWorks Icon to display on the page.  This path, along with
     * additional URL parameters defined in the code, will allow RefWorks
     * to check a reference and redirect to the proper image.
     * The noOverride value will be set to true to allow this value to
     * change with different environments.  This value is appended to the base
     * server URL returned by the CSAS.
     */
    public static final String REFWORKS_ICON_REDIRECT_URL =
        "REFWORKS_ICON_REDIRECT_URL";
        
    /**
     * This value is required by 
     * com.elsevier.edit.app.web.url.scopus.ScopusDocumentURL,
     * but is not used in the Scopus application 
     */
    public static final String SCOPUS_BASE_URL = "SCOPUS_BASE_URL";

    /**The name of the SMAPI variable for MAX_ACTIVE
     *
     */
    public static final String XMLREADER_POOL_MAX_ACTIVE = "XMLREADER_POOL_MAX_ACTIVE";

    /*
     * Spring Configuration Context Constants
     */    
    /**
     * location of the spring context file
     */
    public static final String SCOPUS_FACTORY_CONTEXT_LOCATION = 
       "SCOPUS_FACTORY_CONTEXT_LOCATION";

     
    /**
      * Spring runtime configuration name
      */    
     public static final String SELECT_RUNTIME_CONFIG = "SELECT_RUNTIME_CONFIG";
     
     
     /**
      * The daily task execution time interval for fence data cache 
      * forced build.
      */
     public static final String FENCE_DATA_CACHE_FORCED_REBUILD_TIME =
    	 								"FENCE_DATA_CACHE_FORCED_REBUILD_TIME";
     
     /**
      * System variable for enabling session affinity with CSAS.
      */
     public static final String ENABLE_SESSION_AFFINITY =
    	 								"ENABLE_SESSION_AFFINITY";
     
}

/*****************************************************************************

                               ELSEVIER
                             CONFIDENTIAL


   This document is the property of Elsevier, and its contents are
   proprietary to Elsevier.   Reproduction in any form by anyone of the
   materials contained  herein  without  the  permission  of Elsevier is
   prohibited.  Finders are  asked  to  return  this  document  to the
   following Elsevier location.

       Elsevier
       360 Park Avenue South,
       New York, NY 10010-1710

   Copyright (c) 2005 by Elsevier, A member of the Reed Elsevier plc
   group.

   All Rights Reserved.

*****************************************************************************/
