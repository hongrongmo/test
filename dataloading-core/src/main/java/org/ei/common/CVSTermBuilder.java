package org.ei.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.oro.text.perl.Perl5Util;

public class CVSTermBuilder {

    public static String getCVField(String sVal) {

        if (sVal == null)
            return "";

        if (sVal.startsWith("*")) {

            if (sVal.endsWith("-A")) {
                sVal = "CVMA";
            } else if (sVal.endsWith("-P")) {
                sVal = "CVMP";
            } else if (sVal.endsWith("-N")) {
                sVal = "CVMN";
            } else {
                sVal = "CVM";
            }

        } else {

            if (sVal.endsWith("-A")) {
                sVal = "CVA";
            } else if (sVal.endsWith("-P")) {
                sVal = "CVP";
            } else if (sVal.endsWith("-N")) {
                sVal = "CVN";
            } else {
                sVal = "CV";
            }

        }

        return sVal;
    }

    public String removeBar(String term) {
        Perl5Util perl = new Perl5Util();
        if (term == null)
            return "";
        String termNoAst = perl.substitute("s/\\|/;/g", term);
        return termNoAst;
    }

    public String removeAst(String sVal) {

        return "";
    }

    public String getStandardTerms(String mh) {

        Perl5Util perl = new Perl5Util();

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/;/", mh.toUpperCase());

        StringBuffer termBuffer = new StringBuffer();

        for (Iterator<String> iter = lstTokens.iterator(); iter.hasNext();) {
            String term = ((String) iter.next()).trim();

            if (term.length() > 1 && !term.startsWith("*") && !term.endsWith("-A") && !term.endsWith("-P") && !term.endsWith("-N") && !term.equals("")) {
                termBuffer.append(term);

                if (iter.hasNext())
                    termBuffer.append(";");
            }

        }

        return termBuffer.toString();
    }

    public String expandMajorTerms(String mh) {

        Perl5Util perl = new Perl5Util();

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/;/", mh.toUpperCase());

        StringBuffer termBuffer = new StringBuffer();

        for (Iterator<String> iter = lstTokens.iterator(); iter.hasNext();) {

            String majorTerm = ((String) iter.next()).trim();

            // Major Role NAP
            if (majorTerm.endsWith("-*NAP")) {
                int index = 0;
                String nonMajorTerm = "";
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-*NAP");
                    nonMajorTerm = majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-*NAP");
                    majorTerm = majorTerm.substring(0, index);
                    nonMajorTerm = majorTerm.substring(1, index);
                }

                termBuffer.append(majorTerm).append("-N");
                termBuffer.append(";").append(nonMajorTerm).append("-A");
                termBuffer.append(";").append(nonMajorTerm).append("-P");

            } else if (majorTerm.endsWith("-*N*AP")) {

                int index = 0;
                String nonMajorTerm = "";
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-*N*AP");
                    nonMajorTerm = majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-*N*AP");
                    majorTerm = majorTerm.substring(0, index);
                    nonMajorTerm = majorTerm.substring(1, index);
                }

                termBuffer.append(majorTerm).append("-N");
                termBuffer.append(";").append(majorTerm).append("-A");
                termBuffer.append(";").append(nonMajorTerm).append("-P");

            } else if (majorTerm.endsWith("-*NA*P")) {

                int index = 0;
                String nonMajorTerm = "";
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-*NA*P");
                    nonMajorTerm = majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-*NA*P");
                    majorTerm = majorTerm.substring(0, index);
                    nonMajorTerm = majorTerm.substring(1, index);
                }

                termBuffer.append(majorTerm).append("-N");
                termBuffer.append(";").append(nonMajorTerm).append("-A");
                termBuffer.append(";").append(majorTerm).append("-P");

            } else if (majorTerm.endsWith("-*N*A*P")) {

                int index = 0;
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-*N*A*P");
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-*N*A*P");
                    majorTerm = majorTerm.substring(0, index);
                }

                termBuffer.append(majorTerm).append("-N");
                termBuffer.append(";").append(majorTerm).append("-A");
                termBuffer.append(";").append(majorTerm).append("-P");
            }
            // Non Major Role NAP
            else if (majorTerm.endsWith("-N*AP")) {

                int index = 0;
                String nonMajorTerm = "";
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-N*AP");
                    nonMajorTerm = majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-N*AP");
                    majorTerm = majorTerm.substring(0, index);
                    nonMajorTerm = majorTerm.substring(1, index);
                }

                termBuffer.append(nonMajorTerm).append("-N");
                termBuffer.append(";").append(majorTerm).append("-A");
                termBuffer.append(";").append(nonMajorTerm).append("-P");

            } else if (majorTerm.endsWith("-NA*P")) {

                int index = 0;
                String nonMajorTerm = "";
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-NA*P");
                    nonMajorTerm = majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-NA*P");
                    majorTerm = majorTerm.substring(0, index);
                    nonMajorTerm = majorTerm.substring(1, index);
                }

                termBuffer.append(nonMajorTerm).append("-N");
                termBuffer.append(";").append(nonMajorTerm).append("-A");
                termBuffer.append(";").append(majorTerm).append("-P");

            } else if (majorTerm.endsWith("-N*A*P")) {

                int index = 0;
                String nonMajorTerm = "";
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-N*A*P");
                    nonMajorTerm = majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-N*A*P");
                    majorTerm = majorTerm.substring(0, index);
                    nonMajorTerm = majorTerm.substring(1, index);
                }

                termBuffer.append(nonMajorTerm).append("-N");
                termBuffer.append(";").append(majorTerm).append("-A");
                termBuffer.append(";").append(majorTerm).append("-P");
            }
            // Major Role NA
            else if (majorTerm.endsWith("-*NA")) {

                int index = 0;
                String nonMajorTerm = "";
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-*NA");
                    nonMajorTerm = majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-*NA");
                    majorTerm = majorTerm.substring(0, index);
                    nonMajorTerm = majorTerm.substring(1, index);
                }

                termBuffer.append(majorTerm).append("-N");
                termBuffer.append(";").append(nonMajorTerm).append("-A");

            } else if (majorTerm.endsWith("-*N*A")) {

                int index = 0;
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-*N*A");
                    majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-*N*A");
                    majorTerm = majorTerm.substring(0, index);
                }

                termBuffer.append(majorTerm).append("-N");
                termBuffer.append(";").append(majorTerm).append("-A");

            }

            // Major Role AP
            else if (majorTerm.endsWith("-*AP")) {

                int index = 0;
                String nonMajorTerm = "";
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-*AP");
                    nonMajorTerm = majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-*AP");
                    majorTerm = majorTerm.substring(0, index);
                    nonMajorTerm = majorTerm.substring(1, index);
                }

                termBuffer.append(majorTerm).append("-A");
                termBuffer.append(";").append(nonMajorTerm).append("-P");

            } else if (majorTerm.endsWith("-*A*P")) {

                int index = 0;
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-*A*P");
                    majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-*A*P");
                    majorTerm = majorTerm.substring(0, index);

                }

                termBuffer.append(majorTerm).append("-A");
                termBuffer.append(";").append(majorTerm).append("-P");

            }

            // Non-Major Role NA
            else if (majorTerm.endsWith("-N*A")) {

                int index = 0;
                String nonMajorTerm = "";
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-N*A");
                    nonMajorTerm = majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-N*A");
                    majorTerm = majorTerm.substring(0, index);
                    nonMajorTerm = majorTerm.substring(1, index);
                }

                termBuffer.append(nonMajorTerm).append("-N");
                termBuffer.append(";").append(majorTerm).append("-A");

            }

            // Non-Major Role AP
            else if (majorTerm.endsWith("-A*P")) {

                int index = 0;
                String nonMajorTerm = "";
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-A*P");
                    nonMajorTerm = majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-A*P");
                    majorTerm = majorTerm.substring(0, index);
                    nonMajorTerm = majorTerm.substring(1, index);
                }

                termBuffer.append(nonMajorTerm).append("-A");
                termBuffer.append(";").append(majorTerm).append("-P");

            }

            // Major Role NP
            else if (majorTerm.endsWith("-*NP")) {

                int index = 0;
                String nonMajorTerm = "";
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-*NP");
                    nonMajorTerm = majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-*NP");
                    majorTerm = majorTerm.substring(0, index);
                    nonMajorTerm = majorTerm.substring(1, index);
                }

                termBuffer.append(majorTerm).append("-N");
                termBuffer.append(";").append(nonMajorTerm).append("-P");

            } else if (majorTerm.endsWith("-*N*P")) {

                int index = 0;
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-*N*P");
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-*N*P");
                    majorTerm = majorTerm.substring(0, index);

                }

                termBuffer.append(majorTerm).append("-N");
                termBuffer.append(";").append(majorTerm).append("-P");

            }
            // Non-Major Role NP
            else if (majorTerm.endsWith("-N*P")) {

                int index = 0;
                String nonMajorTerm = "";
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-N*P");
                    nonMajorTerm = majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-N*P");
                    majorTerm = majorTerm.substring(0, index);
                    nonMajorTerm = majorTerm.substring(1, index);
                }

                termBuffer.append(nonMajorTerm).append("-N");
                termBuffer.append(";").append(majorTerm).append("-P");

            }
            // Major (N)
            else if (majorTerm.endsWith("-*N")) {

                int index = 0;
                new StringBuffer();

                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-*N");
                    majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-*N");
                    majorTerm = majorTerm.substring(0, index);

                }

                termBuffer.append(majorTerm).append("-N");

            }
            // Major (A)
            else if (majorTerm.endsWith("-*A")) {

                int index = 0;
                new StringBuffer();

                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-*A");
                    majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-*A");
                    majorTerm = majorTerm.substring(0, index);

                }

                termBuffer.append(majorTerm).append("-A");

            }
            // Major (P)
            else if (majorTerm.endsWith("-*P")) {

                int index = 0;
                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", majorTerm)) {
                    index = majorTerm.indexOf("-*P");
                    majorTerm.substring(1, index);
                    majorTerm = majorTerm.substring(0, index);
                } else {

                    index = majorTerm.indexOf("-*P");
                    majorTerm = majorTerm.substring(0, index);

                }

                termBuffer.append(majorTerm).append("-P");

            } else {
                if (!majorTerm.equals("") && majorTerm.length() > 1)
                    termBuffer.append(majorTerm);
            }

            if (iter.hasNext() && majorTerm.length() > 1)
                termBuffer.append(";");

        }

        return termBuffer.toString();
    }

    public String expandNonMajorTerms(String cv) {

        Perl5Util perl = new Perl5Util();

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/;/", cv.toUpperCase());

        StringBuffer cvBuffer = new StringBuffer();

        for (Iterator<String> iter = lstTokens.iterator(); iter.hasNext();) {

            String cvTerm = ((String) iter.next()).trim();

            if (cvTerm.endsWith("-NA")) {

                int index = 0;

                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", cvTerm)) {
                    index = cvTerm.indexOf("-NA");
                    cvTerm = cvTerm.substring(0, index);
                } else {

                    index = cvTerm.indexOf("-NA");
                    cvTerm = cvTerm.substring(0, index);
                }

                cvBuffer.append(cvTerm).append("-N");
                cvBuffer.append(";").append(cvTerm).append("-A");

            } else if (cvTerm.endsWith("-NP")) {

                int index = 0;

                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", cvTerm)) {
                    index = cvTerm.indexOf("-NP");
                    cvTerm = cvTerm.substring(0, index);
                } else {

                    index = cvTerm.indexOf("-NP");
                    cvTerm = cvTerm.substring(0, index);
                }

                cvBuffer.append(cvTerm).append("-N");
                cvBuffer.append(";").append(cvTerm).append("-P");

            } else if (cvTerm.endsWith("-AP")) {

                int index = 0;

                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", cvTerm)) {
                    index = cvTerm.indexOf("-AP");
                    cvTerm = cvTerm.substring(0, index);
                } else {

                    index = cvTerm.indexOf("-AP");
                    cvTerm = cvTerm.substring(0, index);
                }

                cvBuffer.append(cvTerm).append("-A");
                cvBuffer.append(";").append(cvTerm).append("-P");

            } else if (cvTerm.endsWith("-NAP")) {

                int index = 0;

                if (perl.match("/[0-9]*-[0-9]*-[0-9]*/", cvTerm)) {
                    index = cvTerm.indexOf("-NAP");
                    cvTerm = cvTerm.substring(0, index);
                } else {

                    index = cvTerm.indexOf("-NAP");
                    cvTerm = cvTerm.substring(0, index);
                }

                cvBuffer.append(cvTerm).append("-N");
                cvBuffer.append(";").append(cvTerm).append("-A");
                cvBuffer.append(";").append(cvTerm).append("-P");
            } else {
                if (!cvTerm.equals("") && cvTerm.length() > 1)
                    cvBuffer.append(cvTerm);
            }

            if (iter.hasNext() && cvTerm.length() > 1)
                cvBuffer.append(";");
        }

        return cvBuffer.toString();
    }

    public String getMajorTerms(String mh) {

        Perl5Util perl = new Perl5Util();

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/;/", mh.toUpperCase());

        StringBuffer cvBuffer = new StringBuffer();

        for (Iterator<String> iter = lstTokens.iterator(); iter.hasNext();) {

            String cvTerm = ((String) iter.next()).trim();

            if (cvTerm.startsWith("*") && cvTerm.length() > 1) {

                cvBuffer.append(cvTerm);

                if (iter.hasNext())
                    cvBuffer.append(";");

            }
        }

        return cvBuffer.toString();

    }

    public String getNonMajorTerms(String cv) {

        Perl5Util perl = new Perl5Util();

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/;/", cv.toUpperCase());

        StringBuffer cvBuffer = new StringBuffer();

        for (Iterator<String> iter = lstTokens.iterator(); iter.hasNext();) {

            String cvTerm = ((String) iter.next()).trim();

            if (!cvTerm.startsWith("*") && cvTerm.length() > 1) {

                cvBuffer.append(cvTerm);

                if (iter.hasNext())
                    cvBuffer.append(";");
            }
        }

        return cvBuffer.toString();

    }

    public String getNoRoleTerms(String cv) {

        Perl5Util perl = new Perl5Util();

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/;/", cv.toUpperCase());

        StringBuffer cvBuffer = new StringBuffer();

        for (Iterator<String> iter = lstTokens.iterator(); iter.hasNext();) {

            String cvTerm = ((String) iter.next()).trim();

            if (cvTerm.endsWith("-N") && cvTerm.length() > 1) {

                cvBuffer.append(cvTerm);

                if (iter.hasNext())
                    cvBuffer.append(";");
            }
        }

        return cvBuffer.toString();

    }

    public String getReagentTerms(String cv) {

        Perl5Util perl = new Perl5Util();

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/;/", cv.toUpperCase());

        StringBuffer cvBuffer = new StringBuffer();

        for (Iterator<String> iter = lstTokens.iterator(); iter.hasNext();) {

            String cvTerm = ((String) iter.next()).trim();

            if (cvTerm.endsWith("-A") && cvTerm.length() > 1) {

                cvBuffer.append(cvTerm);

                if (iter.hasNext())
                    cvBuffer.append(";");
            }
        }

        return cvBuffer.toString();

    }

    public String getProductTerms(String cv) {

        Perl5Util perl = new Perl5Util();

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/;/", cv.toUpperCase());

        StringBuffer cvBuffer = new StringBuffer();

        for (Iterator<String> iter = lstTokens.iterator(); iter.hasNext();) {

            String cvTerm = ((String) iter.next()).trim();

            if (cvTerm.endsWith("-P") && cvTerm.length() > 1) {

                cvBuffer.append(cvTerm);

                if (iter.hasNext())
                    cvBuffer.append(";");
            }
        }

        return cvBuffer.toString();

    }

    public String getMajorNoRoleTerms(String cv) {

        Perl5Util perl = new Perl5Util();

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/;/", cv.toUpperCase());

        StringBuffer cvBuffer = new StringBuffer();

        for (Iterator<String> iter = lstTokens.iterator(); iter.hasNext();) {

            String cvTerm = ((String) iter.next()).trim();

            if (cvTerm.startsWith("*") && cvTerm.endsWith("-N") && cvTerm.length() > 1) {

                cvBuffer.append(cvTerm);

                if (iter.hasNext())
                    cvBuffer.append(";");
            }
        }

        return cvBuffer.toString();

    }

    public String getMajorReagentTerms(String cv) {

        Perl5Util perl = new Perl5Util();

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/;/", cv.toUpperCase());

        StringBuffer cvBuffer = new StringBuffer();

        for (Iterator<String> iter = lstTokens.iterator(); iter.hasNext();) {

            String cvTerm = ((String) iter.next()).trim();

            if (cvTerm.startsWith("*") && cvTerm.endsWith("-A") && cvTerm.length() > 1) {

                cvBuffer.append(cvTerm);

                if (iter.hasNext())
                    cvBuffer.append(";");
            }
        }

        return cvBuffer.toString();

    }

    public String getMajorProductTerms(String cv) {

        Perl5Util perl = new Perl5Util();

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/;/", cv.toUpperCase());

        StringBuffer cvBuffer = new StringBuffer();

        for (Iterator<String> iter = lstTokens.iterator(); iter.hasNext();) {

            String cvTerm = ((String) iter.next()).trim();

            if (cvTerm.startsWith("*") && cvTerm.endsWith("-P") && cvTerm.length() > 1) {

                cvBuffer.append(cvTerm);

                if (iter.hasNext())
                    cvBuffer.append(";");
            }
        }

        return cvBuffer.toString();

    }

    public String removeRoleTerms(String cv) {

        Perl5Util perl = new Perl5Util();

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/;/", cv.toUpperCase());

        StringBuffer cvBuffer = new StringBuffer();

        for (Iterator<String> iter = lstTokens.iterator(); iter.hasNext();) {

            String cvTerm = ((String) iter.next()).trim();

            if (!cvTerm.endsWith("-A") && !cvTerm.endsWith("-P") && !cvTerm.endsWith("-N") && cvTerm.length() > 1) {

                if (!cvTerm.equals("")) {
                    cvBuffer.append(cvTerm);

                    if (iter.hasNext())
                        cvBuffer.append(";");
                }
            }
        }
        return cvBuffer.toString();

    }

    public static String formatCT(String ct) {

        Perl5Util perl = new Perl5Util();

        if (ct == null)
            return "";

        String parsedCT = perl.substitute("s/\\#//g", ct);

        return parsedCT;

    }

    public static void main(String[] args) {

        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-a-*NAP"));
        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-b-*N*AP"));
        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-c-*NA*P"));
        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-d-*N*A*P"));

        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-e-N*AP"));
        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-f-NA*P"));
        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-g-N*A*P"));

        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-h-*AP"));
        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-i-*A*P"));
        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-j-A*P"));

        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-k-*NA"));
        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-l-*N*A"));
        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-m-N*A"));

        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-n-*NP"));
        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-o-*N*P"));
        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-p-N*P"));

        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-q-*N"));
        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-r-*A"));
        System.out.println(new CVSTermBuilder().expandMajorTerms("*term-s-*P"));

    }

}
