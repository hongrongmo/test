package org.ei.domain;

import org.apache.commons.validator.GenericValidator;

public class SortOption extends SelectOption {
	public static String SORT_FIELD_RELEVANCE = "relevance";
	public static String SORT_FIELD_DATE = "yr";
	public static String SORT_FIELD_AUTHOR = "ausort";
	public static String SORT_FIELD_SOURCE = "stsort";
	public static String SORT_FIELD_PUBLISHER = "pnsort";
	public static String SORT_FIELD_PATENTCITEDBY = "pcb";

	public static String SORT_DIR_UP = "up";
	public static String SORT_DIR_DOWN = "dw";

	private String field;
	private String defaultdirection;
	private String direction;

	public String getLabel() {
		return getPrettyfield() + (GenericValidator.isBlankOrNull(getPrettydir()) ? "" : (" (" + getPrettydir() + ")"));
		/*
		if (!GenericValidator.isBlankOrNull(field)) {
			if (SORT_FIELD_RELEVANCE.equals(field)) {
				return "Relevance";
			}
			else if (SORT_FIELD_DATE.equals(field)) {
				return "Date" + (SORT_DIR_UP.equals(direction) ? " (Oldest)" : " (Newest)");
			}
			else if (SORT_FIELD_AUTHOR.equals(field)) {
				return "Author" + (SORT_DIR_UP.equals(direction) ? " (A-Z)" : " (Z-A)");
			}
			else if (SORT_FIELD_SOURCE.equals(field)) {
				return "Source" + (SORT_DIR_UP.equals(direction) ? " (A-Z)" : " (Z-A)");
			}
			else if (SORT_FIELD_PUBLISHER.equals(field)) {
				return "Publisher" + (SORT_DIR_UP.equals(direction) ? " (A-Z)" : " (Z-A)");
			}
		}
		return label;
		*/
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getDefaultdirection() {
		return defaultdirection;
	}
	public void setDefaultdirection(String defaultdirection) {
		this.defaultdirection = defaultdirection;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getPrettyfield() {
		if (!GenericValidator.isBlankOrNull(field)) {
			if (SORT_FIELD_RELEVANCE.equals(field)) {
				return "Relevance";
			}
			else if (SORT_FIELD_DATE.equals(field)) {
				return "Date";
			}
			else if (SORT_FIELD_AUTHOR.equals(field)) {
				return "Author";
			}
			else if (SORT_FIELD_SOURCE.equals(field)) {
				return "Source";
			}
			else if (SORT_FIELD_PUBLISHER.equals(field)) {
				return "Publisher";
			}
			else if (SORT_FIELD_PATENTCITEDBY.equals(field)) {
				return "Cited by Patents";
			}
		}
		return "";
	}

	public String getPrettydir() {
		if (!GenericValidator.isBlankOrNull(direction)) {
			if (SORT_DIR_DOWN.equals(direction)) {
				if (SORT_FIELD_DATE.equals(field)) {
					return "Newest";
				}
				else if (SORT_FIELD_PATENTCITEDBY.equals(field)) {
					return "Most";
				}
				else if (SORT_FIELD_AUTHOR.equals(field) || SORT_FIELD_SOURCE.equals(field) || SORT_FIELD_PUBLISHER.equals(field)) {
					return "Z-A";
				}
			} else {
				if (SORT_FIELD_DATE.equals(field)) {
					return "Oldest";
				}
				else if (SORT_FIELD_PATENTCITEDBY.equals(field)) {
					return "Least";
				}
				else if (SORT_FIELD_AUTHOR.equals(field) || SORT_FIELD_SOURCE.equals(field) || SORT_FIELD_PUBLISHER.equals(field)) {
					return "A-Z";
				}
			}
		}
		return "";
	}
}
