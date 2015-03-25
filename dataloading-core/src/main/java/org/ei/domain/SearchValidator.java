package org.ei.domain;

import java.util.Map;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;
import org.ei.exception.SystemErrorCodes;
import org.ei.query.base.FastQueryWriter;
import org.ei.query.limiter.ReferexLimiter;
import org.ei.util.GUID;

public class SearchValidator {
    private final static Logger log4j = Logger.getLogger(SearchValidator.class);

    public enum SearchValidatorStatus {
        SUCCESS, SYNTAX_ERROR, FIELD_ERROR, PARSE_ERROR, UNKNOWN_ERROR;
    }

    private Query queryObject;

    public Query getQueryObject() {
        return this.queryObject;
    }

    /**
     * Validate a search form
     *
     * @param searchtype
     * @param searchform
     * @return
     */
    public SearchValidatorStatus validate(SearchValidatorRequest validatorrequest) {
        log4j.info("Validating search form for searchtype: " + validatorrequest.getSearchtype());

        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        String[] credentials = validatorrequest.getCartridge();
        String searchtype = validatorrequest.getSearchtype();
        int databasemask = validatorrequest.getDatabasemask();

        try {
            ISearchForm searchform = validatorrequest.getSearchform();
            this.queryObject = new Query(databaseConfig, credentials);
            this.queryObject.setDataBase(databasemask);
            if ((searchtype != null) && searchtype.equals(Query.TYPE_BOOK)) {
                if (searchform.getAllcol() == null) {
                    if (searchform.getCol() != null) {
                        this.queryObject.setReferexCollections(new ReferexLimiter(searchform.getCol()));
                    }
                }
            }
            this.queryObject.setSearchType(searchtype);
            this.queryObject.setSessionID(validatorrequest.getSessionid());
            this.queryObject.setUserID(validatorrequest.getUserid());

            // Use setSearchPhrase to copy POST elements (searchWord1, boolean1,
            // etc)
            // into Query object
            this.queryObject.setSearchPhrase(searchform);

            // Set the various limiters
            this.queryObject.setDocumentType(searchform.getDoctype());
            this.queryObject.setTreatmentType(searchform.getTreatmentType());
            this.queryObject.setDisciplineType(searchform.getDisciplinetype());
            this.queryObject.setLanguage(searchform.getLanguage());

            // If start/end year parms are empty, get the years based on the
            // DB mask from the link
            String startYear = searchform.getStartYear();
            String endYear = searchform.getEndYear();
            if (GenericValidator.isBlankOrNull(startYear) || GenericValidator.isBlankOrNull(endYear)) {
                Map<String, String> startEndYears = databaseConfig.getStartEndYears(credentials, databasemask);
                if (GenericValidator.isBlankOrNull(startYear)) {
                    startYear = (String) startEndYears.get(DatabaseConfig.STARTYEAR);
                }
                if (GenericValidator.isBlankOrNull(endYear)) {
                    endYear = (String) startEndYears.get(DatabaseConfig.ENDYEAR);
                }
            }
            this.queryObject.setStartYear(startYear);
            this.queryObject.setEndYear(endYear);

            String yearselect = searchform.getYearselect();
            String updatesno = searchform.getUpdatesNo();
            if (!yearselect.equals("yearrange")) {
                this.queryObject.setLastUpdatesCount(updatesno);
            }

            // Set autostemming. Reverse logic - checked means "off", unchecked
            // means "on". :)
            String autostem = searchform.getAutostem();
            if ("true".equalsIgnoreCase(autostem)) {
                this.queryObject.setAutoStemming("off");
            } else {
                this.queryObject.setAutoStemming("on");
            }

            // Set sorting
            String sort = searchform.getSort();
            String sortdir = searchform.getSortdir();
            this.queryObject.setSortOption(new Sort(sort, sortdir));

            // Add the FastQueryWriter object
            this.queryObject.setSearchQueryWriter(new FastQueryWriter());

            //
            // VALIDATE!
            //
            
            return validate(this.queryObject);
            
        } catch (Throwable t) {
            log4j.error("Unable to compile Query object!", t);
            return SearchValidatorStatus.UNKNOWN_ERROR;
        }

    }

    /**
     * Validate a RERUN search - requires the previous search ID
     *
     * @param rerunid
     * @param searchtype
     * @param databasemask
     * @param ussession
     * @param searchform
     * @return
     */
    public SearchValidatorStatus validate(String rerunid, SearchValidatorRequest validatorrequest) {
        try {
            //
            // Retrieve the query object by the rerun ID
            //
            this.queryObject = Searches.getSearch(rerunid);

            this.queryObject.setDatabaseConfig(DatabaseConfig.getInstance());
            this.queryObject.setCredentials(validatorrequest.getCartridge());

            String searchID = new GUID().toString();
            this.queryObject.setID(searchID);
            this.queryObject.setDataBase(validatorrequest.getDatabasemask());
            this.queryObject.clearDupSet();
            this.queryObject.setDeDup(false);

            return SearchValidatorStatus.SUCCESS;
        } catch (Throwable t) {
            log4j.error("Unable to retrieve RERUN Query object!", t);
            return SearchValidatorStatus.UNKNOWN_ERROR;
        }

    }

    /**
     * Validate a RERUN search - requires the previous search ID
     *
     * @param rerunid
     * @param searchtype
     * @param databasemask
     * @param ussession
     * @param searchform
     * @return
     */
    public SearchValidatorStatus validate(Query queryObject) {
        this.queryObject = queryObject;
        // ********************************************************************
        // Attempt to compile query!! If it fails, redirect to quick search
        // form with error message...
        // ********************************************************************
        try {

            this.queryObject.compile();

            // If we get here (no Exceptions) then the search passed
            // validation.
            // Create the search ID and save the search!
            this.queryObject.setID((new GUID()).toString());
            return SearchValidatorStatus.SUCCESS;

        } catch (InfrastructureException ie) {
            log4j.error("Unable to compile Query object!", ie);
            if (ie.getErrorCode() == SystemErrorCodes.PARSE_ERROR) {
                return SearchValidatorStatus.PARSE_ERROR;
            }
            return SearchValidatorStatus.UNKNOWN_ERROR;
        } catch (SearchException se) {
            log4j.error("Unable to compile Query object!", se);
            if (se.getErrorCode() == SystemErrorCodes.INVALID_FIELD) {
                return SearchValidatorStatus.FIELD_ERROR;
            } else if (se.getErrorCode() == SystemErrorCodes.PARSE_ERROR) {
                return SearchValidatorStatus.PARSE_ERROR;
            }
            return SearchValidatorStatus.SYNTAX_ERROR;
        } catch (Throwable t) {
            log4j.error("Unable to compile Query object!", t);
            return SearchValidatorStatus.UNKNOWN_ERROR;
        }

    }
}
