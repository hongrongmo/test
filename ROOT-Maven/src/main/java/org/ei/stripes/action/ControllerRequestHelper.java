package org.ei.stripes.action;

import java.util.List;

import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;

import org.apache.log4j.Logger;
import org.ei.domain.personalization.SearchHistory;
import org.ei.exception.SystemErrorCodes;
import org.ei.stripes.EVActionBeanContext;
import org.ei.stripes.action.search.SearchDisplayAction;

public class ControllerRequestHelper {
	private static final Logger log4j = Logger.getLogger(ControllerRequestHelper.class);

	public static final String DEFAULT_ERROR_FIELD = "errorcode";
	public static final String SEARCH_HISTORY_ERROR_FIELD = "searchHistoryError";

	/**
	 * Process an error code from an action bean
	 * @param action
	 */
	public static void processErrorCode(EVActionBean action) {
		processErrorCode(action.getErrorCode(), action);
	}
	
	/**
	 * Process a specific error code
	 * @param errorCode
	 * @param action
	 */
	public static void processErrorCode(int errorCode, EVActionBean action) {
		EVActionBeanContext context = action.getContext();
		ValidationErrors validationerrors = context.getValidationErrors();
		if (validationerrors == null) {
			log4j.warn("No ValidationErrors object!");
			return;
		}

		/**
		 * Translate errorcode to error string
		 */
		switch (errorCode) {
		case SystemErrorCodes.INIT: break;
		case SystemErrorCodes.EBOOK_REMOVED:
			validationerrors.add(DEFAULT_ERROR_FIELD, new LocalizableError("org.ei.stripes.action.search.SearchDisplayAction.ebookremoval"));
			break;
		case SystemErrorCodes.SEARCH_NOT_FOUND:
			validationerrors.add(DEFAULT_ERROR_FIELD, new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.SearchNotFound"));
			break;
		case SystemErrorCodes.SAVED_SEARCH_NOT_FOUND:
			validationerrors.add(DEFAULT_ERROR_FIELD, new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.SavedSearchNotFound"));
			break;
		case SystemErrorCodes.SEARCH_QUERY_COMPILATION_FAILED:
			validationerrors.add(DEFAULT_ERROR_FIELD, new LocalizableError("org.ei.stripes.action.search.BaseSearchAction.syntaxerror", context.getHelpUrl()));
			break;
		case SystemErrorCodes.SEARCH_HISTORY_ERROR:
			validationerrors.add(SEARCH_HISTORY_ERROR_FIELD, new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.seachHistError"));
			break;
		case SystemErrorCodes.COMBINE_HISTORY_UNIQUE_DATABASE_ERROR:
			validationerrors.add(SEARCH_HISTORY_ERROR_FIELD, new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.dbMismatchSearchHistoryError"));
			break;
		case SystemErrorCodes.SEARCH_HISTORY_NOIDEXISTS:
			int size = 1;
			if (action != null && action instanceof SearchDisplayAction) {
				List<SearchHistory> history = ((SearchDisplayAction) action).getSearchHistoryList();
				if (history != null) size = history.size();
			}
			validationerrors.add(SEARCH_HISTORY_ERROR_FIELD, 
					new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.HisIdNotExistsError", size));
			break;
		default:
			validationerrors.add(DEFAULT_ERROR_FIELD, new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.unknownerror"));

		}
	}

}
