/**
 *
 */
package org.ei.data;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;

/**
 * @author harovetm
 *
 */
public class DocTypeNormalizer {
    private static final Logger log4j = Logger.getLogger(DocTypeNormalizer.class);

    public static final String GENRE_ARTICLE = "article";
    public static final String GENRE_PROCEEDING = "proceeding";
    public static final String GENRE_CONFERENCE = "conference";
    public static final String GENRE_PATENT = "patent";
    public static final String GENRE_BOOKITEM = "bookitem";
    public static final String GENRE_REVIEW = "review";
    public static final String GENRE_DISSERTATION = "dissertation";
    public static final String GENRE_ARTICLEINPRESS = "articleinpress";

    public static final String REGEX_JOURNAL = ".*\\(JA\\).*";
    public static final String REGEX_CONFERENCE = ".*\\(CA\\).*";
    public static final String REGEX_PROCEEDING = ".*\\(CP\\).*";
    public static final String REGEX_PATENT = ".*\\(PA\\).*";
    public static final String REGEX_CHAPTER = ".*\\(MC\\)|\\(RC\\).*";
    public static final String REGEX_REVIEW = ".*\\(MR\\)|(RR\\).*";
    public static final String REGEX_DISSERTATION = ".*\\(DS\\).*";
    public static final String REGEX_AIP = ".*(Press Release|Article in Press|In Process).*";
    public static final String REGEX_BOOK = ".*Book.*";

    public static String normalize(String rawdt) {
        // If incoming doctype is empty, return article
        if (GenericValidator.isBlankOrNull(rawdt)) {
            log4j.info("Empty doctype, return '" + GENRE_ARTICLE + "'");
            return GENRE_ARTICLE;
        }

        if (rawdt.matches(REGEX_JOURNAL)) {
            log4j.info("Matched Journal regex, return '" + GENRE_ARTICLE + "'");
            return GENRE_ARTICLE;
        } else if (rawdt.matches(REGEX_BOOK)) {
            // Do NOT move this - Books can have a document type of "Book (CA)" so if this comes
            // after the Conference match then it will never be hit!
            log4j.info("Matched Patent regex, return '" + GENRE_BOOKITEM + "'");
            return GENRE_BOOKITEM;
        } else if (rawdt.matches(REGEX_CONFERENCE)) {
            log4j.info("Matched Conference regex, return '" + GENRE_CONFERENCE + "'");
            return GENRE_CONFERENCE;
        } else if (rawdt.matches(REGEX_PROCEEDING)) {
            log4j.info("Matched Proceeding regex, return '" + GENRE_PROCEEDING + "'");
            return GENRE_PROCEEDING;
        } else if (rawdt.matches(REGEX_PATENT)) {
            log4j.info("Matched Patent regex, return '" + GENRE_PATENT + "'");
            return GENRE_PATENT;
        } else if (rawdt.matches(REGEX_CHAPTER)) {
            log4j.info("Matched Patent regex, return '" + GENRE_BOOKITEM + "'");
            return GENRE_BOOKITEM;
        } else if (rawdt.matches(REGEX_REVIEW)) {
            log4j.info("Matched Patent regex, return '" + GENRE_REVIEW + "'");
            return GENRE_REVIEW;
        } else if (rawdt.matches(REGEX_DISSERTATION)) {
            log4j.info("Matched Patent regex, return '" + GENRE_DISSERTATION + "'");
            return GENRE_DISSERTATION;
        } else {
            log4j.info("No regex matches, return '" + GENRE_ARTICLE + "'");
            return GENRE_ARTICLE;
        }
    }
}
