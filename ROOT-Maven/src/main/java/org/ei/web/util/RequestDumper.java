package org.ei.web.util;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;

public class RequestDumper {
    private static Logger log4j = Logger.getLogger(RequestDumper.class);

    public static void dump(HttpServletRequest request, String feature) {

        log4j.info("-------------- general info ---------------");
        log4j.info("context path: " + request.getContextPath());
        log4j.info("request URI: " + request.getRequestURI());
        log4j.info("query string: " + request.getQueryString());
        log4j.info("method: " + request.getMethod());

        if (!GenericValidator.isBlankOrNull(feature)) {
            if (feature.contains("headers")) {
                log4j.info("-------------- request headers ---------------");
                Enumeration<String> en = request.getHeaderNames();

                String key;
                String value;

                while (en.hasMoreElements()) {
                    key = (String) en.nextElement();
                    value = request.getHeader(key);

                    log4j.info("header: " + key + " = " + value);
                }
            } else if (feature.contains("params")) {
                log4j.info("-------------- request parameters ---------------");
                Enumeration<String> en = request.getParameterNames();

                String key;
                String[] value;

                while (en.hasMoreElements()) {
                    key = (String) en.nextElement();
                    value = request.getParameterValues(key);

                    for (int t = 0; t < value.length; t++) {
                        log4j.info("parameter: " + key + " = " + value[t]);
                    }
                }

            } else if (feature.contains("session")) {
                log4j.info("-------------- session attributes ---------------");
                HttpSession session = request.getSession(false);

                Enumeration<String> en = session.getAttributeNames();

                String key;
                Object value;

                while (en.hasMoreElements()) {
                    key = (String) en.nextElement();
                    value = session.getAttribute(key);

                    log4j.info("session: " + key + " = " + value.toString());
                }
            } else if (feature.contains("cookie")) {
                log4j.info("-------------- cookies ---------------");

                Cookie[] carray = request.getCookies();

                Cookie cookie;
                String name;
                String value;

                for (int t = 0; t < carray.length; t++) {
                    cookie = carray[t];

                    name = cookie.getName();
                    value = cookie.getValue();

                    log4j.info("cookie: " + name + " = " + value);
                }

            } else if (feature.contains("sysprops")) {
                log4j.info("-------------- System Properties ---------------");
                Properties props = System.getProperties();
                Enumeration<Object> en = props.keys();

                String key;
                String value;

                while (en.hasMoreElements()) {
                    key = (String) en.nextElement();
                    value = props.getProperty(key);

                    log4j.info(key + " = " + value);
                }
            } else {
                log4j.info("error: RequestDumper got feature = -" + feature + "-");
            }

        }

    }
}
