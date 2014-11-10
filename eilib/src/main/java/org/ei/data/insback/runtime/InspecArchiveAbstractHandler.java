package org.ei.data.insback.runtime;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;


public class InspecArchiveAbstractHandler extends DefaultHandler implements LexicalHandler {


    private int          entryNum   = 0;
    private StringBuffer htmlbuf    = new StringBuffer();

    private String       tframe;
    private String       tcolsep;
    private String       trowsep;

    // Defines the column defaults for the table
    private Col[]        cols;
    private int          colsIndex  = 0;
    private StringBuffer ttitle;
    private StringBuffer linkBuf;
    private StringBuffer linknum;
    private boolean      entity     = true;

    private int          state      = -1;

    private int          TITLE_NODE = 1;
    private int          THEAD      = 2;
    private int          LINK_NODE  = 3;
    private boolean      tables     = true;
    private boolean      skip       = false;
    private boolean      font       = false;
    private boolean      colgroup   = false;


    public InspecArchiveAbstractHandler(boolean tables) {
        this.tables = tables;
    }

    public String getHtmlBuff() {
        return this.htmlbuf.toString();
    }

    public void startElement(String uri, String local, String raw, Attributes attrs) {
        if (raw.equals("table")) {

            if (!tables) {
                skip = true;
            }
            this.ttitle = new StringBuffer();


            if (!skip) {
                this.tframe = attrs.getValue("frame");
                if (this.tframe == null) {
                    this.tframe = "none";
                }
                this.tcolsep = attrs.getValue("colsep");
                if (this.tcolsep == null) {
                    this.tcolsep = "no";
                }

                this.trowsep = attrs.getValue("rowsep");
                if (this.trowsep == null) {
                    this.trowsep = "no";
                }

            }
        } else if (raw.equals("title")) {
            this.state = TITLE_NODE;
        } else if (raw.equals("font")) {
            font = true;

            if (!skip && this.state != LINK_NODE) {
                htmlbuf.append("<font color=\"red\">");
            }
        } else if (raw.equals("tgroup")) {
            colgroup = false;
            if (!skip) {
                this.colsIndex = -1;
                this.cols = new Col[100];
                // Go ahead and open the table
                htmlbuf.append("<p/><table");
                if (!this.tframe.equalsIgnoreCase("none")) {
                    htmlbuf.append("  border=\"1\">");
                } else {
                    htmlbuf.append(" cellspacing=\"10\">");
                }
            }

        } else if (raw.equals("colspec")) {
            if (!skip) {
                Col col = new Col();
                String name = attrs.getValue("colname");
                System.out.println("colname:" + name);
                String width = attrs.getValue("colwidth");
                String align = attrs.getValue("align");
                col.name = name;
                col.width = width;
                col.align = align;
                colsIndex++;
                this.cols[colsIndex] = col;

            }
        } else if (raw.equals("thead")) {
            if (!skip) {
                if (!colgroup) {
                    addColGroup();
                }
                String valign = attrs.getValue("valign");
                htmlbuf.append("<thead");
                if (valign != null) {
                    htmlbuf.append(" valign=\"");
                    htmlbuf.append(valign);
                    htmlbuf.append("\"");
                }
                htmlbuf.append(">");
                this.state = THEAD;
            }
        } else if (raw.equals("tbody")) {
            if (!skip && !colgroup) {
                addColGroup();
            }
        } else if (raw.equals("row")) {
            if (!skip) {
                String valign = attrs.getValue("valign");
                htmlbuf.append("<tr");
                if (valign != null) {
                    htmlbuf.append(" valign=\"");
                    htmlbuf.append(valign);
                    htmlbuf.append("\"");
                }
                htmlbuf.append(">");
            }
        } else if (raw.equals("uidlink")) {

            font = false;

            if (!skip) {
                linknum = new StringBuffer();
                int l = htmlbuf.length();
                if (l > 0 && htmlbuf.charAt(l - 1) != ' ') {
                    htmlbuf.append(" ");
                }

                if (tables) {
                    htmlbuf
                        .append("<a CLASS=\"SpLink\" href=\"/search/results/expert.url?CID=expertSearchCitationFormat&database=2&startYear=1884&endYear=2005&yearselect=yearrange&searchWord1=");
                }
                state = LINK_NODE;

            }
        } else if (raw.equals("entry")) {
            if (!skip) {
                String morerows = attrs.getValue("morerows");
                String nameest = attrs.getValue("namest");
                String nameend = attrs.getValue("nameend");
                String align = attrs.getValue("align");
                String valign = attrs.getValue("valign");
                String colspan = getColspan(nameest, nameend);

                if (state == THEAD) {
                    htmlbuf.append("<th");
                } else {
                    htmlbuf.append("<td");
                }

                if (morerows != null) {
                    int m = Integer.parseInt(morerows);
                    m = m + 1;
                    htmlbuf.append(" rowspan=\"");
                    htmlbuf.append(Integer.toString(m));
                    htmlbuf.append("\"");
                }


                if (colspan != null) {
                    htmlbuf.append(" colspan=\"");
                    htmlbuf.append(colspan);
                    htmlbuf.append("\"");
                }

                if (valign != null) {
                    htmlbuf.append(" valign=\"");
                    htmlbuf.append(valign);
                    htmlbuf.append("\"");
                }

                /*
                 *
                 * if(align == null && entryNum <= colsIndex) { Col col = cols[entryNum]; align = col.align; }
                 */

                if (align != null) {
                    htmlbuf.append(" align=\"");
                    htmlbuf.append(align);
                    htmlbuf.append("\"");
                }


                htmlbuf.append("><span CLASS=\"MedBlackText\">");
                entryNum++;
            }
        } else if (raw.equalsIgnoreCase("I")) {
            if (!skip) {
                if (state == TITLE_NODE) {
                    ttitle.append("<i>");
                } else {
                    htmlbuf.append("<i>");
                }
            }
        } else if (raw.equalsIgnoreCase("sup")) {
            if (!skip) {
                if (state == TITLE_NODE) {
                    ttitle.append("<sup>");
                } else {
                    htmlbuf.append("<sup>");
                }
            }
        } else if (raw.equalsIgnoreCase("sub")) {
            if (!skip) {
                if (state == TITLE_NODE) {
                    ttitle.append("<sub>");
                } else {
                    htmlbuf.append("<sub>");
                }
            }
        } else if (raw.equalsIgnoreCase("b")) {
            if (!skip && this.state != LINK_NODE) {
                if (state == TITLE_NODE) {
                    ttitle.append("<b>");
                } else {
                    htmlbuf.append("<b>");
                }
            }
        }
    }

    private String getColspan(String colBegin, String colEnd) {
        if (colBegin == null || colEnd == null) { return null; }

        int beginIndex = -1;
        int endIndex = -1;
        for (int i = 0; i <= colsIndex; i++) {
            Col col = cols[i];
            if (col.name.equals(colBegin)) {
                beginIndex = i;
            }

            if (col.name.equals(colEnd)) {
                endIndex = i;
            }
        }

        int span = (endIndex - beginIndex) + 1;
        return Integer.toString(span);

    }

    class Col {
        public String name;
        public String align;
        public String width;

    }

    public void characters(char ch[], int start, int length) {
        String s = new String(ch, start, length);
        if (s.equals("&")) {
            s = "amp";
        } else if (s.equals("<")) {
            s = "lt";
        } else if (s.equals(">")) {
            s = "gt";
        }

        if (state == TITLE_NODE) {
            ttitle.append(s);
        } else if (state == LINK_NODE) {
            if (!skip) {
                linknum.append(s);
            }
        } else {
            if (!skip) {
                htmlbuf.append(s);
            }
        }
    }

    public void endElement(String uri, String local, String raw) {
        if (raw.equalsIgnoreCase("title")) {

            if (this.ttitle.length() > 0 && !skip) {

                htmlbuf.append("</p><b>");
                htmlbuf.append(this.ttitle.toString());
                htmlbuf.append("</b><br/>");
            } else if (this.ttitle.length() > 0 && skip) {
                htmlbuf.append(" [TABLE:");
                htmlbuf.append(this.ttitle.toString());
                htmlbuf.append("] ");
            }


            this.state = -1;
        } else if (raw.equalsIgnoreCase("table")) {

            if (skip) {
                if (this.ttitle.length() == 0) {
                    htmlbuf.append(" [TABLE] ");
                }

            }
            skip = false;
        } else if (raw.equals("tgroup")) {
            if (!skip) {
                htmlbuf.append("</table></p>");
            }
        } else if (raw.equals("thead")) {
            if (!skip) {
                htmlbuf.append("</thead>");
                this.state = -1;
            }
        } else if (raw.equals("row")) {
            if (!skip) {
                htmlbuf.append("</tr>");
                entryNum = 0;
            }
        } else if (raw.equals("entry")) {
            if (!skip) {
                if (state == THEAD) {
                    htmlbuf.append("</a></th>");
                } else {
                    htmlbuf.append("</a></td>");
                }
            }
        } else if (raw.equalsIgnoreCase("I")) {
            if (!skip) {

                if (state == TITLE_NODE) {
                    ttitle.append("</I>");
                } else {
                    htmlbuf.append("</I>");
                }
            }
        } else if (raw.equalsIgnoreCase("sup")) {
            if (!skip) {

                if (state == TITLE_NODE) {
                    ttitle.append("</sup>");
                } else {
                    htmlbuf.append("</sup>");
                }

            }
        } else if (raw.equalsIgnoreCase("sub")) {
            if (!skip) {
                if (state == TITLE_NODE) {
                    ttitle.append("</sub>");
                } else {
                    htmlbuf.append("</sub>");
                }
            }
        } else if (raw.equals("font")) {
            if (!skip && this.state != LINK_NODE) {
                htmlbuf.append("</font>");
            }
        } else if (raw.equalsIgnoreCase("b")) {
            if (!skip && this.state != LINK_NODE) {
                if (state == TITLE_NODE) {
                    ttitle.append("</b>");
                } else {
                    htmlbuf.append("</b>");
                }
            }
        } else if (raw.equals("uidlink")) {
            if (!skip) {
                String ln = linknum.toString();

                if (tables) {
                    htmlbuf.append(ln);
                    htmlbuf.append("+WN+AN\">");
                }

                if (font) {
                    htmlbuf.append("<b><font color=\"red\">");
                }

                htmlbuf.append(ln);
                if (font) {
                    htmlbuf.append("</font></b>");
                }

                if (tables) {
                    htmlbuf.append("</a>");
                }
                state = -1;
            }
            font = false;
        }

    }

    public void addColGroup() {
        htmlbuf.append("<colgroup>");
        for (int i = 0; i <= colsIndex; i++) {
            htmlbuf.append("<col");
            if (cols[i].align != null) {
                htmlbuf.append(" align=\"");
                htmlbuf.append(cols[i].align);
                htmlbuf.append("\"");
            }
            htmlbuf.append("/>");

        }

        htmlbuf.append("</colgroup>");
        colgroup = true;
    }

    public void skippedEntity(java.lang.String name) throws SAXException {
        System.out.println(name);
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
    }

    public void endCDATA() throws SAXException {
    }

    public void endDTD() throws SAXException {
    }

    public void endEntity(String name) throws SAXException {
        if (!skip) {

            if (state == TITLE_NODE) {
                ttitle.append(";");
            } else {
                htmlbuf.append(";");
            }
        }
    }

    public void startCDATA() throws SAXException {
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }

    public void startEntity(String name) throws SAXException {
        if (!skip) {
            if (state == TITLE_NODE) {
                ttitle.append("&");
            } else {
                htmlbuf.append("&");
            }
        }
    }
}