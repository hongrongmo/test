package org.ei.data.insback.runtime;

import java.io.ByteArrayInputStream;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class InspecArchiveAbstract {
    
    
    public static void main(String[] args) throws Exception {
        String ab = "<abs>A detailed account with some additions and corrections (tabulated below) of work already published [see Abstract <uidlink saiss=\"1931-11\">1931A03699</uidlink>]. Mono-carboxylic acids: &alpha;-palmitic acid has <i>a</i>, 9.41; <i>b</i>, 5.00; <i>c</i>, 45.9 <ent><![CDATA[&Aring;]]></ent>.; <ent><![CDATA[&beta;]]></ent>, 50<ent><![CDATA[&deg;]]></ent> 50' with 4 molecules in the unit cell, n<sub><ent><![CDATA[&alpha;]]></ent></sub>, 1.533; n<sub><ent><![CDATA[&beta;]]></ent></sub>, 1.506; <ent><![CDATA[&beta;]]></ent>-stearic acid has <i>a</i>, 5.68; <i>b</i>, 7.39; <i>c</i>, 50.7 <ent><![CDATA[&Aring;]]></ent>.; <ent><![CDATA[&beta;]]></ent>, 60<ent><![CDATA[&deg;]]></ent>; with 4 molecules in the unit cell, n<sub><ent><![CDATA[&alpha;]]></ent></sub>, 1.535; n<sub><ent><![CDATA[&gamma;]]></ent></sub>, 1.508. Di-carboxylic acids: <table frame=\"topbot\" colsep=\"yes\" rowsep=\"no\"><tgroup cols=\"9\"><colspec colnum=\"1\" colname=\"col1\"></colspec><colspec colnum=\"2\" colname=\"col2\"></colspec><colspec colnum=\"3\" colname=\"col3\"></colspec><colspec colnum=\"4\" colname=\"col4\"></colspec><colspec colnum=\"5\" colname=\"col5\"></colspec><colspec colnum=\"6\" colname=\"col6\"></colspec><colspec colnum=\"7\" colname=\"col7\"></colspec><colspec colnum=\"8\" colname=\"col8\"></colspec><colspec colnum=\"9\" colname=\"col9\"></colspec><thead><row valign=\"middle\"><entry morerows=\"1\">Acid</entry><entry morerows=\"1\">Crystal system</entry><entry morerows=\"1\">Transition temperature, <ent><![CDATA[&alpha;]]></ent><ent><![CDATA[&rarr;]]></ent><ent><![CDATA[&beta;]]></ent>*</entry><entry namest=\"col4\" nameend=\"col8\" rowsep=\"no\" align=\"center\">Unit cell</entry><entry rowsep=\"yes\" morerows=\"1\">Crystal group</entry></row><row rowsep=\"yes\" valign=\"middle\"><entry><i>a</i></entry><entry><i>b</i></entry><entry><i>c</i></entry><entry><ent><![CDATA[&beta;]]></ent></entry><entry>No of mols.</entry></row></thead><tbody><row><entry>Malonic-<ent><![CDATA[&alpha;]]></ent></entry><entry>rhombic</entry><entry>80<ent><![CDATA[&deg;]]></ent></entry><entry>8.70</entry><entry>11.53</entry><entry>17.05</entry><entry>90<ent><![CDATA[&deg;]]></ent></entry><entry>16</entry><entry>-</entry></row><row><entry>Malconic-<ent><![CDATA[&beta;]]></ent></entry><entry>triclinic</entry><entry>80<ent><![CDATA[&deg;]]></ent></entry><entry>-</entry><entry>-</entry><entry>-</entry><entry>-</entry><entry>2</entry><entry>-</entry></row><row><entry>Succinic-<ent><![CDATA[&alpha;]]></ent></entry><entry>monoclinic</entry><entry>137<ent><![CDATA[&deg;]]></ent></entry><entry>5.70</entry><entry>26.2</entry><entry>7.57</entry><entry>115<ent><![CDATA[&deg;]]></ent> 45'</entry><entry>8</entry><entry>C<sub>2h</sub><sup>3</sup></entry></row><row><entry>Succinic-<ent><![CDATA[&beta;]]></ent></entry><entry>monoclinic</entry><entry>137<ent><![CDATA[&deg;]]></ent></entry><entry>5.06</entry><entry>8.81</entry><entry>7.57</entry><entry>133<ent><![CDATA[&deg;]]></ent> 37'</entry><entry>2</entry><entry>C<sub>2h</sub><sup>2</sup></entry></row><row><entry>Glutaric-<ent><![CDATA[&alpha;]]></ent></entry><entry>monoclinic</entry><entry>74<ent><![CDATA[&deg;]]></ent></entry><entry>10.34</entry><entry>5.08</entry><entry>32.9<ent><![CDATA[&deg;]]></ent></entry><entry>129<ent><![CDATA[&deg;]]></ent></entry><entry>8</entry><entry>C<sub>2h</sub><sup>6</sup></entry></row><row><entry>Glutaric-<ent><![CDATA[&beta;]]></ent></entry><entry>monoclinic</entry><entry>74<ent><![CDATA[&deg;]]></ent></entry><entry>10.06</entry><entry>4.87</entry><entry>17.4</entry><entry>132<ent><![CDATA[&deg;]]></ent> 35'</entry><entry>4</entry><entry>C<sub>2h</sub><sup>6</sup></entry></row></tbody></tgroup></table></abs>";
        // String ab =
        // "<abs>The following revised and additional data for the intensities and recoil energies (in ekV) of the <ent><![CDATA[&alpha;]]></ent>-particle lines of Rd-Ac, Ac-X and An are given. The figures in brackets give the numbering of the lines previously adopted [see Abstract <uidlink saiss=\"1932-09\">1932A03312</uidlink>]: <table frame=\"none\" colsep=\"no\" rowsep=\"no\"><title>Radioactinium</title><tgroup cols=\"3\"><colspec colnum=\"1\" colname=\"col1\" align=\"left\"></colspec><colspec colnum=\"2\" colname=\"col2\" align=\"center\"></colspec><colspec colnum=\"3\" colname=\"col3\" align=\"right\"></colspec><thead><row><entry><i>Line</i></entry><entry align=\"left\"><i>Intensity</i></entry><entry align=\"left\"><i>Energy</i></entry></row></thead><tbody><row><entry>0 (1)</entry><entry>80</entry><entry>6159</entry></row><row><entry>1 (2)</entry><entry>15</entry><entry>6127</entry></row><row><entry>2 (3)</entry><entry>100</entry><entry>6097</entry></row><row><entry>3</entry><entry>15</entry><entry>6075</entry></row><row><entry>4</entry><entry>5</entry><entry>6030</entry></row><row><entry>5 (4)</entry><entry>10</entry><entry>5975</entry></row><row><entry>6</entry><entry>5</entry><entry>5921</entry></row><row><entry>7 (5)</entry><entry>80</entry><entry>5869</entry></row><row><entry>8</entry><entry>15</entry><entry>5847</entry></row><row><entry>9 (6)</entry><entry>60</entry><entry>5822</entry></row><row><entry>10 (?)</entry><entry>10</entry><entry>5776</entry></row></tbody></tgroup></table><table frame=\"none\" colsep=\"no\" rowsep=\"no\"><title>Actinium </title><tgroup cols=\"3\"><colspec colnum=\"1\" colname=\"col1\" align=\"left\"></colspec><colspec colnum=\"2\" colname=\"col2\" align=\"center\"></colspec><colspec colnum=\"3\" colname=\"col3\" align=\"right\"></colspec><thead><row><entry><i>Line</i></entry><entry align=\"left\"><i>Intensity</i></entry><entry align=\"left\"><i>Energy</i></entry></row></thead><tbody><row><entry>0</entry><entry>6</entry><entry>5823</entry></row><row><entry>1 (1)</entry><entry>4</entry><entry>5709</entry></row><row><entry>2 (2)</entry><entry>1</entry><entry>5634</entry></row></tbody></tgroup></table><table frame=\"none\" colsep=\"no\" rowsep=\"no\"><title>Actinon</title><tgroup cols=\"3\"><colspec colnum=\"1\" colname=\"col1\" align=\"left\"></colspec><colspec colnum=\"2\" colname=\"col2\" align=\"center\"></colspec><colspec colnum=\"3\" colname=\"col3\" align=\"right\"></colspec><thead><row><entry><i>Line</i></entry><entry align=\"left\"><i>Intensity</i></entry><entry align=\"left\"><i>Energy</i></entry></row></thead><tbody><row><entry>0</entry><entry>-</entry><entry>6953</entry></row><row><entry>1</entry><entry>-</entry><entry>6683</entry></row><row><entry>2</entry><entry>-</entry><entry>6556</entry></row></tbody></tgroup></table> The resultant energy differences for Rd-Ac and An show improved agreement with the energies of corresponding <ent><![CDATA[&gamma;]]></ent>- and <ent><![CDATA[&beta;]]></ent>-rays respectively.</abs>";
        
        
        System.out.println(InspecArchiveAbstract.getHTML(ab));
    }
    
    
    public static String getHTML(String ab) throws Exception {
        XMLReader reader = org.ei.xml.SAXParserFactory.getDefaultSAXParser();
        InspecArchiveAbstractHandler handler = new InspecArchiveAbstractHandler(true);
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        reader.parse(new InputSource(new ByteArrayInputStream(ab.getBytes())));
        return handler.getHtmlBuff();
    }
    
    public static String getPLAIN(String ab) throws Exception {
        XMLReader reader = org.ei.xml.SAXParserFactory.getDefaultSAXParser();
        InspecArchiveAbstractHandler handler = new InspecArchiveAbstractHandler(false);
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        reader.parse(new InputSource(new ByteArrayInputStream(ab.getBytes())));
        return handler.getHtmlBuff();
    }
    
    
}
