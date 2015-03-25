package org.ei.thesaurus;

import java.util.Comparator;

public class ThesaurusTermComp implements Comparator<ThesaurusRecord> {
	public int compare(ThesaurusRecord tag1, ThesaurusRecord tag2) {
		int result = 0;
		try {
			String term1 = "";
			String term2 = "";
			if (tag1 != null && tag1.getRecID() != null) {
				term1 = tag1.getRecID().getMainTerm();
			}
			if (tag2 != null && tag2.getRecID() != null) {
				term2 = tag2.getRecID().getMainTerm();
			}
			result = term1.toLowerCase().compareTo(term2.toLowerCase());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}