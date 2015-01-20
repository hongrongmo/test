package org.ei.data.test;

import org.apache.oro.text.perl.Perl5Util;

public class BdData {
    String mid;
    String citationTitle;
    String volume;
    String issue;
    String issn;
    String page;
    String publicationYear;
    String updateFlag;
    private String[] numberPatterns = { "/[1-9][0-9]*/" };
    private Perl5Util perl = new Perl5Util();

    public void setMid(String mid) {
        this.mid = mid;
    }

    public void setCitationTitle(String citationTitle) {
        this.citationTitle = citationTitle;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setUpdateFlag(String updateFlag) {
        this.updateFlag = updateFlag;
    }

    public String getMid() {
        return this.mid;
    }

    public String getCitationTitle() {
        return this.citationTitle;
    }

    public String getVolume() {
        return this.volume;
    }

    public String getIssue() {
        return this.issue;
    }

    public String getIssn() {
        return this.issn;
    }

    public String getPage() {
        return this.page;
    }

    public String getPublicationYear() {
        return this.publicationYear;
    }

    public String getUpdateFlag() {
        return this.updateFlag;
    }

    public void changeUpdateFlag(BdData o) {
        String cpxString = null;
        String bdString = null;
        StringBuffer updateFlageBuffer = new StringBuffer();
        if (o.getIssn() != null && this.issn != null) {
            cpxString = o.getIssn().replaceAll("-", "");
            bdString = this.issn.replaceAll("-", "");
            // System.out.println("cpxString "+cpxString+" bdString "+bdString);
            if (cpxString.equals(bdString)) {
                updateFlageBuffer.append("1");
            } else {
                updateFlageBuffer.append("0");
            }
        } else if (o.getIssn() == null && this.issn == null) {
            updateFlageBuffer.append("1");
        } else {
            updateFlageBuffer.append("0");
        }

        if (o.getVolume() != null && this.volume != null) {
            cpxString = o.getVolume();
            bdString = getFirstNumberGroup(this.volume);
            // System.out.println("cpxString "+cpxString+" bdString "+bdString);
            if (cpxString != null && bdString != null && cpxString.indexOf(bdString) > -1) {
                updateFlageBuffer.append("1");
            } else {
                updateFlageBuffer.append("0");
            }
        } else if (o.getVolume() == null && this.volume == null) {
            updateFlageBuffer.append("1");
        } else {
            updateFlageBuffer.append("0");
        }

        if (o.getIssue() != null && this.issue != null) {
            cpxString = o.getIssue();
            bdString = getFirstNumberGroup(this.issue);
            // System.out.println("cpxString "+cpxString+" bdString "+bdString);
            if (cpxString != null && bdString != null && cpxString.indexOf(bdString) > -1) {
                updateFlageBuffer.append("1");
            } else {
                updateFlageBuffer.append("0");
            }
        } else if (o.getIssue() == null && this.issue == null) {
            updateFlageBuffer.append("1");
        } else {
            updateFlageBuffer.append("0");
        }

        if (o.getPage() != null && this.page != null) {
            cpxString = o.getPage();
            bdString = getFirstNumberGroup(this.page);
            // System.out.println("cpxString "+cpxString+" bdString "+bdString);
            if (cpxString != null && bdString != null && cpxString.indexOf(bdString) > -1) {
                updateFlageBuffer.append("1");
            } else {
                updateFlageBuffer.append("0");
            }
        } else if (o.getPage() == null && this.page == null) {
            updateFlageBuffer.append("1");
        } else {
            updateFlageBuffer.append("0");
        }

        if (o.getPublicationYear() != null && this.publicationYear != null) {
            cpxString = o.getPublicationYear();
            bdString = getFirstNumberGroup(this.publicationYear);
            // System.out.println("cpxString "+cpxString+" bdString "+bdString);
            if (cpxString != null && bdString != null && cpxString.indexOf(bdString) > -1) {
                updateFlageBuffer.append("1");
            } else {
                updateFlageBuffer.append("0");
            }
        } else if (o.getPublicationYear() == null && this.publicationYear == null) {
            updateFlageBuffer.append("1");
        } else {
            updateFlageBuffer.append("0");
        }
        // System.out.println("update flag "+updateFlageBuffer.toString());
        setUpdateFlag(updateFlageBuffer.toString());
    }

    protected String getFirstNumberGroup(String inputString) {
        if (inputString != null) {
            if (perl.match("/(\\d+)/", inputString)) {
                // System.out.println("RAW "+inputString+" MATCH "+(String) (perl.group(0).toString()));
                return (String) (perl.group(0).toString());
            }
        }

        return null;
    }

    public boolean check(BdData o) {
        String cpxTitle = o.getCitationTitle();
        String bdTitle = null;
        if (this.citationTitle.length() > cpxTitle.length()) {
            bdTitle = this.citationTitle;
        } else {
            bdTitle = cpxTitle;
            cpxTitle = this.citationTitle;
        }
        if (bdTitle.indexOf(cpxTitle) > -1) {
            /*
             * if(this.issn != null && o.getIssn() != null) { String cpxIssn = o.getIssn().replaceAll("-",""); String bdIssn = this.issn.replaceAll("-","");
             * if(cpxIssn.equals(bdIssn)) { return true; } } else if(this.issn == null && o.getIssn() ==null) {
             */
            return true;
            // }

        }
        return false;
    }

}