package org.ei.stripes.action.tagsgroups;

import java.io.StringReader;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.stripes.action.EVActionBean;
import org.ei.tags.Scope;
import org.ei.tags.TagBroker;

@UrlBinding("/tagsgroups/{$event}.url")
public class TagsGroupsAjaxAction extends EVActionBean {
	private final static Logger log4j = Logger
			.getLogger(TagsGroupsAjaxAction.class);

	// Scope of the tag request. Can contain the following values:
	// 1 - Public tags (default)
	// 2 - Private tags
	// 3 - Group tags: will be followed by a ':' and then the Group ID
	// 4 - Institution tags
	private String scope = Integer.toString(Scope.SCOPE_PUBLIC);

	// Input from text field
	private String searchgrouptags;

	/**
	 * Tags and groups autocomplete for tag search
	 * 
	 * @return Resolution
	 * @throws Exception
	 */
	@HandlesEvent("autocomplete")
	@DontValidate
	public Resolution autocomplete() throws Exception {
		log4j.info("Building autocomplete for '" + searchgrouptags + "', scope=" + scope);

		String groupid = null;
		int iscope = Scope.SCOPE_PUBLIC;
		String customerId = context.getUserSession().getUser().getCustomerID()
				.trim();
		String userid = context.getUserid();

		// Get the scope if present.
		if (scope.indexOf(":") > -1) {
			String[] scopeParts = scope.split(":");
			iscope = Integer.parseInt(scopeParts[0]);
			groupid = scopeParts[1];
		} else {
			iscope = Integer.parseInt(scope);
		}
		
		//
		// Use the TagBroker to get a ';' separated list of tags
		// matching the user's input
		//
		TagBroker tagBroker = new TagBroker();
		String tagresult = null;
		if (searchgrouptags != null) {
			if (groupid == null) {
				tagresult = tagBroker.autocompleteTags(searchgrouptags);
			} else {
				tagresult = tagBroker.autocompleteTags(searchgrouptags, iscope, groupid,
						customerId, userid);
			}
		}

		// Build the JSON return string
		tagresult = buildAutocompleteJSON(tagresult);
		return new StreamingResolution("text", new StringReader(tagresult));
	}

	/**
	 * Build a JSON-formatted string for consumption by the jQuery autocomplete feature.
	 * Format should be "[{"id":"&lt;id&gt;","label","&lt;label&gt;","value":"&lt;value&gt;"
	 * @param tagresult
	 * @return
	 */
	private String buildAutocompleteJSON(String tagresult) {
		if (GenericValidator.isBlankOrNull(tagresult)) {
			return "[]";
		}
		StringBuffer json = new StringBuffer();
		String[] tags = tagresult.split(TagBroker.DELIM);
		json.append("[");
		for (int i=0; i<tags.length; i++) {
			if (i > 0) json.append(",");
			json.append("\"" + tags[i] + "\"");
		}
		json.append("]");
		return json.toString();
	}
	

	//
	//
	// GETTERS/SETTERS
	//
	//


	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getSearchgrouptags() {
		return searchgrouptags;
	}

	public void setSearchgrouptags(String searchgrouptags) {
		this.searchgrouptags = searchgrouptags;
	}


}
