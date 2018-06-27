package org.ei.dataloading.upt.loadtime;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.oro.text.perl.Perl5Util;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The purpose of the class is to handle all SAX events and disptach the request to the appropriate handler.
 */
public class SaxBaseHandler extends DefaultHandler implements ErrorHandler {
    private Vector<String> vTags = null;
    private static Class[] clNoArgs = null;
    private static Class[] clAttrList = null;
    private static Class[] clString = null;
    Object oHandler = null;
    private Perl5Util perl = new Perl5Util();
    Hashtable<String, Method> htMethods = new Hashtable<String, Method>();

    /**********************************************
     *
     * STATIC INITIALIZERS
     *
     *
     ***********************************************/

    static {
        clNoArgs = new Class[] {};
        clAttrList = new Class[] { org.xml.sax.Attributes.class };
        clString = new Class[] { java.lang.String.class };
    }

    /**********************************************
     *
     * ABSTRACT METHODS
     *
     ***********************************************/

    /**********************************************
     *
     * CONSTRUCTORS
     *
     *
     ***********************************************/

    public SaxBaseHandler() {
    }

    /**
     * This constructor adds a hanlder to the Vector of SAX handlers.
     */
    public SaxBaseHandler(Object oHandler) {
        super();
        this.oHandler = oHandler;
        vTags = new Vector<String>();
    }

    /**********************************************
     *
     * STANDARD METHODS
     *
     *
     ***********************************************/

    /**
     * This call back method gets executed when the root node is parsed.
     */
    public void startDocument() throws org.xml.sax.SAXException {
        String sStartMethodName = "startDocument";

        Method mStartMethod = mFindMethod(oHandler, sStartMethodName, clNoArgs);

        if (mStartMethod != null) {
            try {
                mStartMethod.invoke(oHandler, new Object[] {});
            } catch (InvocationTargetException ex) {
                System.out.println("No start document found for " + sStartMethodName);
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
        }

    }

    public void startElement(String uri, String name, String qName, Attributes alAttrs) {

        String sTag = qName;
        int i;
        String sStartMethodName = "start" + sTag.toUpperCase();

        pushTag(sTag);

        Method mStartMethod = mFindMethod(oHandler, sStartMethodName, clAttrList);

        if (mStartMethod == null) {
            mStartMethod = mFindMethod(oHandler, sStartMethodName, clNoArgs);
        }

        if (mStartMethod != null) {
            try {
                Class[] caMethodArgs = mStartMethod.getParameterTypes();
                if (caMethodArgs.length == 0) {
                    mStartMethod.invoke(oHandler, new Object[] {});
                } else {
                    mStartMethod.invoke(oHandler, new Object[] { alAttrs });
                }
            } catch (InvocationTargetException ex) {
                System.out.println("No start element found for " + sStartMethodName);
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
        }
    }

    public void characters(char[] caChars, int iStart, int iEnd) {

        String sCurrentTag = sCurrentTag();
        String sMethodName = "";

        if (sCurrentTag != null && !sCurrentTag.trim().equals("")) {
            int i;
            sMethodName = "textOf" + sCurrentTag.toUpperCase();

            sMethodName = perl.substitute("s/-//g", sMethodName);
            String sArg = null;

            Method mMethod = mFindMethod(oHandler, sMethodName, clString);

            if (mMethod != null) {
                try {

                    sArg = new String(caChars, iStart, iEnd);

                    if (sArg != null && !sArg.equals("")) {
                        // sArg = sArg.trim();
                        mMethod.invoke(oHandler, new Object[] { sArg });
                    }
                } catch (InvocationTargetException ex) {
                    System.out.println("No characters found for " + sMethodName);
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    System.out.println(ex);
                }
            }

        }
    }

    public void endElement(String uri, String name, String qName) {
        int i;
        String sTag = qName;

        String sEndMethodName = "end" + sTag.toUpperCase();

        Method mEndMethod = mFindMethod(oHandler, sEndMethodName, clNoArgs);

        if (mEndMethod != null) {
            try {
                mEndMethod.invoke(oHandler, new Object[] {});
            } catch (InvocationTargetException ex) {
                System.out.println("No end element found for " + sEndMethodName);
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
        }

        popTag();
    }

    /**
     * This call back method gets executed when the closing tag for the root node is encountered.
     */
    public void endDocument() throws org.xml.sax.SAXException {

        String sEndMethodName = "endDocument";

        Method mEndMethod = mFindMethod(oHandler, sEndMethodName, clNoArgs);

        if (mEndMethod != null) {
            try {
                mEndMethod.invoke(oHandler, new Object[] {});
            } catch (InvocationTargetException ex) {
                System.out.println("No end document found for " + sEndMethodName);
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
        }

    }

    /**
     * Pops the XML element name from the stack.
     */
    private void popTag() {
        vTags.removeElementAt(vTags.size() - 1);
    }

    /**
     * Pushes the element name on the stack
     */
    private void pushTag(String sTag) {
        vTags.addElement(sTag);
    }

    /**
     * Gets the current XML element name from the stack.
     */
    private String sCurrentTag() {
        int iIndex = vTags.size() - 1;
        if (iIndex >= 0) {
            return (String) (vTags.elementAt(vTags.size() - 1));
        } else {
            return null;
        }
    }

    /**
     * This method invokes a method in the handler class.
     *
     * @param oHandler
     *            - The object handler.
     * @param sMethodName
     *            - The method name.
     * @param clArgs
     *            - The method arguments.
     */
    private Method mFindMethod(Object oHandler, String sMethodName, Class[] clArgs) {

        Method m = null;
        Class<? extends Object> classOfHandler = oHandler.getClass();

        try {

            if (htMethods.containsKey(sMethodName)) {
                m = (Method) htMethods.get(sMethodName);
            } else {
                m = classOfHandler.getMethod(sMethodName, clArgs);
                htMethods.put(sMethodName, m);
            }

        } catch (NoSuchMethodException ex) {
            // Ignore
        }
        return m;
    }

    class XmlFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            if (name.endsWith(".xml"))
                return true;
            else
                return false;
        }
    }

    public static void main(String args[]) throws Exception {

    }

    //
    // ErrorHandler methods
    //

    /** Warning. */
    public void warning(SAXParseException ex) {
        System.err.println("[Warning] " + getLocationString(ex) + ": " + ex.getMessage());
    }

    /** Error. */
    public void error(SAXParseException ex) {
        System.err.println("[Error] " + getLocationString(ex) + ": " + ex.getMessage());
    }

    /** Fatal error. */
    public void fatalError(SAXParseException ex) {
        System.err.println("[Fatal Error] " + getLocationString(ex) + ": " + ex.getMessage());

    }

    //
    // Private methods
    //

    /** Returns a string of the location. */
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer();

        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());

        return str.toString();

    }

}
