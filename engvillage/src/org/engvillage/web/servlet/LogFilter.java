package org.engvillage.web.servlet;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;

public class LogFilter implements Filter {

    private static Logger log4j = Logger.getLogger(LogFilter.class);
    private static Logger responselogger = Logger.getLogger("ResponseLogger");

    public static String NEWLINE = "\n";

    @Override
    public void destroy() {
        log4j.warn("LogFilter destroyed.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String printresponse = ApplicationProperties.getInstance().getProperty("engvillage.print.response");
        String sessionid = null;
        String uri = "";

        StopWatch st = new StopWatch();
        st.start();

        try {
            ServletResponse newResponse = response;

            if (request instanceof HttpServletRequest) {
                newResponse = new CharResponseWrapper((HttpServletResponse) response);
                sessionid = ((HttpServletRequest)request).getHeader("SESSION.SK");
                uri = ((HttpServletRequest)request).getRequestURI();
            }

            chain.doFilter(request, newResponse);

            if (newResponse instanceof CharResponseWrapper) {
                String text = newResponse.toString();
                if (text != null) {
                    if (Boolean.parseBoolean(printresponse) && !(uri.contains("/engvillage/models/world/healthcheck.jsp"))) {
                        responselogger.info("********************* Printing response from engvillage " + (sessionid == null ? "" : "for session ID: " + sessionid + " ") + "*******************************");
                        responselogger.info(text);
                        responselogger.info("********************* End printing response from engvillage ********************************");
                    }
                    response.getWriter().write(text);
                }
            }

        } finally {
            st.stop();
            log4j.warn("----------  /engvillage application request: " + NEWLINE
                + prettyPrintRequest((HttpServletRequest) request, (HttpServletResponse) response, NEWLINE) + NEWLINE
                + "    +    TIME:  " + st.toString());
            if (log4j.isInfoEnabled()) {
                RequestDumper.dump((HttpServletRequest) request, "params,headers");
            }
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        log4j.warn("LogFilter initialized.");
    }

    class CharResponseWrapper extends HttpServletResponseWrapper {

        protected CharArrayWriter charWriter;
        protected PrintWriter writer;
        protected boolean getOutputStreamCalled;
        protected boolean getWriterCalled;

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
            charWriter = new CharArrayWriter();
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (getWriterCalled) {
                throw new IllegalStateException("getWriter already called");
            }

            getOutputStreamCalled = true;
            return super.getOutputStream();
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (writer != null) {
                return writer;
            }
            if (getOutputStreamCalled) {
                throw new IllegalStateException("getOutputStream already called");
            }
            getWriterCalled = true;
            writer = new PrintWriter(charWriter);
            return writer;
        }

        public String toString() {
            String s = null;

            if (writer != null) {
                s = charWriter.toString();
            }
            return s;

        }
    }

    private static String prettyPrintRequest(HttpServletRequest request, HttpServletResponse response, String newline) {

        // Add request URI
        StringBuffer sb = new StringBuffer("    +    URI:        " + request.getRequestURI());
        if (!GenericValidator.isBlankOrNull(request.getQueryString())) {
            sb.append("?" + request.getQueryString());
        } else {
            Enumeration<String> en = request.getParameterNames();

            String key;
            String[] value;
            if (en.hasMoreElements()) {
                sb.append("?");
            }
            while (en.hasMoreElements()) {
                key = (String) en.nextElement();
                value = request.getParameterValues(key);
                sb.append(key + "=");
                for (int t = 0; t < value.length; t++) {
                    if (t > 0)
                        sb.append(",");
                    sb.append(value[t]);
                }
                if (en.hasMoreElements())
                    sb.append("&");
            }

        }
        sb.append(newline);

        // Add IP, Session ID and User-Agent
        String ip = request.getHeader("x-forwarded-for");
        if (GenericValidator.isBlankOrNull(ip)) {
            ip = request.getRemoteAddr();
            log4j.info("Retrieved IP from request.getRemoteaddr(): " + ip);
        } else {
            log4j.info("Retrieved IP from x-forwarded-for: " + ip);
        }
        sb.append("    +    IP:         " + ip + newline);
        // Get session ID from request
        HttpSession session = request.getSession(false);
        if (session != null) {
            sb.append("    +    SessionID:  " + session.getId());
        } else {
            sb.append("    +    SessionID:  Empty!");
        }

        return sb.toString();
    }

}
