package org.ei.stripes.action.autoComplete;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.apache.wink.json4j.JSONArray;
import org.ei.biz.personalization.EVWebUser;
import org.ei.controller.MemcachedUtil;
import org.ei.session.UserSession;
import org.ei.web.cookie.CookieHandler;
import org.ei.web.cookie.EISessionCookie;

/**
 * Class to handle the AutoComplete suggestions. Stores any previously retrieved suggestions in MemCached.
 *
 */
public class AutoCompleteServlet extends HttpServlet {
    private static final long serialVersionUID = 3916922085353734811L;
    private Logger log4j = Logger.getLogger(AutoCompleteServlet.class);
    private static int CACHE_EXPIRE = 30 * 24 * 60 * 60;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Writer out = null;
        String termString = "{}";
        out = response.getWriter();

        try {
            if (isUser(request)) {
                String term = request.getParameter("term");

                if (StringUtils.isNotBlank(term)) {
                    response.setContentType("txt/json");
                    termString = JSONlist(term.toLowerCase());

                }
                out.write(termString);
            }
        } catch (Exception e) {
            log4j.warn("Exception has occurred - " + e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    System.out.println("Error:" + e.getMessage());
                }
            }
        }
    }

    /**
     * Check to see if this session is a valid EV User Session.
     *
     * @param request
     * @return
     */
    private boolean isUser(HttpServletRequest request) {
        // Get the EISESSION cookie
        EISessionCookie eisessioncookie = CookieHandler.getEISessionCookie(request);
        if (eisessioncookie == null || GenericValidator.isBlankOrNull(eisessioncookie.getSessionid())) {
            log4j.warn("No valid EISESSION value could be found!");
            return false;
        }

        // Get the UserSession from HTTP session
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        UserSession evSession = (UserSession) session.getAttribute(eisessioncookie.getSessionid());
        if (evSession == null) {
            return false;
        }

        EVWebUser evUser = (EVWebUser) evSession.getUser();
        if (evUser == null) {
            return false;
        }

        return evUser.isCustomer();

    }

    public String JSONlist(String term) throws AutoCompleteException {
        // suggestionMap = getContext().getUserSession().getSuggestionMap();

        if (StringUtils.isBlank(term)) {
            return "";
        } else {
            MemcachedUtil memcached = MemcachedUtil.getInstance();
            String cacheKey = term;
            String resultString = (String) memcached.get(cacheKey);

            if (StringUtils.isEmpty(resultString)) {
                // go get some suggestions
                resultString = "{}";
                AutoCompleteDAO adao = new AutoCompleteDAO();
                JSONArray results = adao.getJSONTerms(cacheKey, new ArrayList<String>(), 20);
                if (results.size() > 0) {
                    resultString = results.toString();
                    // AddToCacheThread act = new AddToCacheThread(cacheKey, resultString);
                    // act.run();
                    // Thread thread = new Thread(new AddToCacheThread(cacheKey, resultString));
                    // thread.run();
                    if (StringUtils.isNotBlank(resultString)) {
                        memcached.add(cacheKey, CACHE_EXPIRE, resultString);
                        log4j.info("Adding value to cache for: " + cacheKey);
                    }

                }

            } else {
                log4j.info("Found value : " + resultString + "object for : " + cacheKey);
            }
            return resultString;
        }

    }

}
