package org.ei.controller;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ei.biz.personalization.IEVWebUser;
import org.ei.session.SessionID;
import org.ei.session.UserSession;
import org.ei.util.SpinLockException;
import org.ei.util.StringUtil;
import org.ei.xml.TransformerBroker;

public class OutputPrinter {
    private boolean appendSession = false;
    private HttpServletResponse response;
    private StringUtil stringUtil = new StringUtil();
    private List<String> comments;
    private byte linefeed = 10;
    private char[] openComment;
    private char[] closeComment;
    private String redirBase;
    private String mimeType;
    private Writer outWriter;
    private OutputStream outStream;
    private boolean cookieSet = false;
    private String cacheID;
    private String entryToken;

    public void setSessionCookie(String sessionID) {
        if (!cookieSet) {
            Cookie cookie = new Cookie("EISESSION", sessionID.toString());
            // A negative value means that the cookie is not stored persistently
            // and will be deleted when the Web browser exits. A zero value
            // causes the cookie to be deleted.
            // http://java.sun.com/j2ee/sdk_1.2.1/techdocs/api/javax/servlet/http/Cookie.html#setMaxAge(int)
            cookie.setMaxAge(-1);
            cookie.setPath("/");
            response.addCookie(cookie);
            cookieSet = true;

            if (entryToken != null) {
                Cookie dpCookie = new Cookie("DAYPASS", "true");
                dpCookie.setMaxAge(86400);
                dpCookie.setPath("/");
                response.addCookie(dpCookie);
            }
        }
    }

    public void setCacheID(String cacheID) {
        this.cacheID = cacheID;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public OutputPrinter(HttpServletResponse response, boolean appendSession, String redirBase) throws IOException {
        String o = "<!-- ";
        String c = " -->";
        this.redirBase = redirBase;
        this.openComment = o.toCharArray();
        this.closeComment = c.toCharArray();
        this.appendSession = appendSession;
        this.response = response;
    }

    public void setAppendSession(boolean b) {
        this.appendSession = b;
    }

    public void setEntryToken(String entryToken) {
        this.entryToken = entryToken;
    }

    public void setContentType(String mimeType) {
        response.setContentType(mimeType);
        this.mimeType = mimeType;
    }

    public void setContentDisposition(String strFilename) {
        response.setHeader("Content-Disposition", "attachment; filename=" + strFilename + "");
    }

    public void print(String styleSheetURL, InputStream xmlStream, UserSession session) throws Exception {
    	String sessionID = null;
    	TransformerBroker tBroker = TransformerBroker.getInstance();
        Transformer transformer = tBroker.getTransformer(styleSheetURL);
        // cacheID is only used with RSS transactions.  If it's null then we are
        // NOT processing RSS so it's OK to get the customer ID
    	if(this.cacheID == null){
    		sessionID = (session.getSessionID()).toString();
	        IEVWebUser user = session.getUser();
	        String customerID = user.getCustomerID();
	        setSessionCookie(sessionID);
	        transformer.setParameter("CUST-ID", customerID);
    	}

        if (this.mimeType.indexOf("text") == 0) {
            long begin = System.currentTimeMillis();

            outWriter = getOutputWriter(sessionID);
           // System.out.println(new Scanner(xmlStream).useDelimiter("\\A").next());
            transformer.transform(new StreamSource(new StreamChopper(xmlStream)), new StreamResult(outWriter));
            outWriter.flush();

            if (this.mimeType.indexOf("html") > -1 && comments != null) {
                long end = System.currentTimeMillis();
                long dif = end - begin;
                this.comments.add("Transform Time:" + Long.toString(dif));
                appendComments(outWriter);
            }
        } else {
            outStream = this.response.getOutputStream();
            transformer.transform(new StreamSource(new StreamChopper(xmlStream)), new StreamResult(outStream));
        }
    }

    /**
     * Print HTML output from response
     *
     * @param htmlStream
     * @param session
     * @throws Exception
     */
    public void print(InputStream htmlStream, UserSession session) throws Exception {
        String sessionID = (session.getSessionID()).toString();

        try {
            outWriter = getOutputWriter(sessionID);
            int _byte = 0;
            while ((_byte = htmlStream.read()) != -1) {
                outWriter.write(_byte);
            }
        } catch (Exception e) {
            close();
        }

    }

    public void close() throws Exception {
        if (outWriter != null) {
            try {
                outWriter.close();
            } catch (Exception e) {
                throw e;
            }
        }

        if (outStream != null) {
            try {
                outStream.close();
            } catch (Exception e) {
                throw e;
            }
        }
    }

    private void appendComments(Writer out) throws Exception {
        out.write(linefeed);
        for (int i = 0; i < comments.size(); ++i) {
            String comment = (String) comments.get(i);
            // System.out.println("outputing:"+ comment);

            out.write(openComment);
            out.write(comment);
            out.write(closeComment);
            out.write(linefeed);

        }
    }

    public void printFatalError() throws IOException {
        if (outWriter != null) {
            outWriter.write("The website is currently unavailable.");
        } else if (outStream != null) {
            ServletOutputStream s = (ServletOutputStream) outStream;
            s.print("The website is currently unavailable.");
        }
    }

    public void up() throws IOException {
        if (outWriter == null) {
            outWriter = this.response.getWriter();
        }

        outWriter.write("Up And Running");
    }

    /**
     * Returns a Stripes-friendly redirect string
     *
     * @param url
     * @param sessionID
     * @return
     */
    public String getModelRedirect(String url, SessionID sessionID) {
        setSessionCookie(sessionID.toString());
        if (url.indexOf("/") == 0) {
            url = "http://" + redirBase + url;
        }

        if (appendSession) {
            if (url.indexOf("?") == -1) {
                url = url + "?EISESSION=" + sessionID.toString();
            } else {

                url = stringUtil.replace(url, "?", "?EISESSION=" + sessionID.toString() + "&", StringUtil.REPLACE_FIRST, StringUtil.MATCH_CASE_INSENSITIVE);
            }
        }
        return url;
    }

    /**
     * Writes a redirect to the response
     *
     * @param url
     * @param sessionID
     * @throws IOException
     */
    public void printModelRedirect(String url, SessionID sessionID) throws IOException {

        setSessionCookie(sessionID.toString());
        if (url.indexOf("/") == 0) {
            url = "http://" + redirBase + url;
        }

        if (appendSession) {
            if (url.indexOf("?") == -1) {
                url = url + "?EISESSION=" + sessionID.toString();
            } else {

                url = stringUtil.replace(url, "?", "?EISESSION=" + sessionID.toString() + "&", StringUtil.REPLACE_FIRST, StringUtil.MATCH_CASE_INSENSITIVE);
            }
        }

        response.sendRedirect(url);
    }

    private Writer getOutputWriter(String sessionID) throws IOException, SpinLockException {
        Writer w = null;
        if (this.outWriter != null) {
            return this.outWriter;
        }

        if (this.cacheID != null) {
            w = new RssFileWriter(this.response.getWriter(), this.cacheID);
        } else {
            if (appendSession) {
                w = new SessionAppenderWriter(new SessionReplacerWriter(this.response.getWriter(), sessionID, this.mimeType.equals("text/html")), sessionID);
            } else {
                w = new SessionReplacerWriter(this.response.getWriter(), sessionID, this.mimeType.equals("text/html"));
            }
        }

        return w;
    }

    class StreamChopper extends FilterInputStream {
        private int START = 0;
        private int CLEAN = 1;
        private int OPEN_TAG = 2;
        private int EXPLANATION_POINT = 3;
        private int DASH_1 = 4;
        private int DASH_2 = 5;
        private int E = 6;
        private int N = 7;
        private int D = 8;
        private int DASH_3 = 9;
        private int DASH_4 = 10;
        private int DONE = 11;

        private int state = START;

        public StreamChopper(InputStream in) {
            super(in);
        }

        public int available() throws IOException {
            return in.available();
        }

        public void close() throws IOException {
            in.close();
        }

        public void mark(int readLimit) {
            in.mark(readLimit);
        }

        public void reset() throws IOException {
            in.reset();
        }

        public boolean markSupported() {
            return in.markSupported();
        }

        public int read() throws IOException {

            //
            if (state == DONE) {
                // System.out.println("Finished Reading Data");
                return -1;
            }

            int i = in.read();
            char c = (char) i;

            if (state == START) {
                // System.out.println("Reading the data !!!!!!!");
                if (c == '<') {
                    state = OPEN_TAG;
                } else {
                    state = CLEAN;
                }
            } else if (state == CLEAN) {

                if (c == '<') {
                    state = OPEN_TAG;
                }
            } else if (state == OPEN_TAG) {
                if (c == '!') {
                    state = EXPLANATION_POINT;
                } else {
                    state = CLEAN;
                }
            } else if (state == EXPLANATION_POINT) {

                if (c == '-') {
                    state = DASH_1;
                } else {
                    state = CLEAN;
                }

            } else if (state == DASH_1) {

                if (c == '-') {
                    state = DASH_2;
                } else {
                    state = CLEAN;
                }
            } else if (state == DASH_2) {

                if (c == 'E') {
                    state = E;
                } else {
                    state = CLEAN;
                }
            } else if (state == E) {

                if (c == 'N') {
                    state = N;
                } else {
                    state = CLEAN;
                }
            } else if (state == N) {

                if (c == 'D') {
                    state = D;
                } else {
                    state = CLEAN;
                }
            } else if (state == D) {

                if (c == '-') {
                    state = DASH_3;
                } else {
                    state = CLEAN;
                }

            } else if (state == DASH_3) {

                if (c == '-') {
                    state = DASH_4;
                } else {
                    state = CLEAN;
                }

            } else if (state == DASH_4) {

                if (c == '>') {

                    state = DONE;
                } else {
                    state = CLEAN;
                }
            }

            return i;
        }

        public int read(byte[] b) throws IOException {
            // System.out.println("Reading into byte array 1");
            int numBytes = 0;
            for (int x = 0; x < b.length; ++x) {
                int i = read();
                if (i == -1) {
                    if (numBytes == 0) {
                        numBytes = -1;
                    }
                    break;
                } else {
                    ++numBytes;
                    b[x] = (byte) i;
                }
            }

            return numBytes;

        }

        public int read(byte[] b, int off, int length) throws IOException {
            int numBytes = 0;
            // System.out.println("Reading into byte array 2");
            for (int x = 0; x < length; ++x) {
                int i = read();
                if (i == -1) {
                    if (numBytes == 0) {
                        numBytes = -1;
                    }

                    break;
                }

                // System.out.print((char)i);
                ++numBytes;
                b[off++] = (byte) i;

            }

            return numBytes;

        }

        public long skip(long n) throws IOException {
            return in.skip(n);
        }

    }

}
