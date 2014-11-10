/*
 * $Id$
 *
 * Copyright 2010 samaxes.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ei.stripes.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

/**
 * Utility class to sanitize input data.
 *
 * @author Samuel Santos
 * @version $Revision$
 */
public class SafeHtmlUtil {

    private static Pattern scriptOpenPattern = Pattern.compile("\\<script\\>", Pattern.CASE_INSENSITIVE);
    private static Pattern scriptClosedPattern = Pattern.compile("\\<\\/script\\>", Pattern.CASE_INSENSITIVE);

    /**
     * Sanitize user inputs.
     *
     * @param raw the input string to be sanitized
     * @return the sanitized string
     */
    public static String sanitize(String raw) {
        return (raw == null || raw.length() == 0) ? raw : HTMLEntityEncode(canonicalize(raw));
    }

    /**
     * Encode HTML entities.
     *
     * @param input the input string to be encoded
     * @return the encoded string
     */
    public static String HTMLEntityEncode(String input) {
        input = scriptOpenPattern.matcher(input).replaceAll("&lt;script&gt;");
        input = scriptClosedPattern.matcher(input).replaceAll("&lt;/script&gt;");
        return input;
    }

    /**
     * Simplify input to its simplest form to make encoding tricks more difficult.
     *
     * @param input the input string to be canonicalized
     * @return the normalized string
     */
    public static String canonicalize(String input) {
        return Normalizer.normalize(input, Form.NFD);
    }
}