/**
 *
 */
package org.ei.dataloading.inspec.loadtime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


//import org.ei.data.inspec.loadtime.InspecClassificationTableDriver.TRecord;
import org.ei.thesaurus.cpx.*;
//import org.jsoup.Jsoup;
//import org.jsoup.parser.Parser;

/**
 * @author TELEBH
 * @Note: 08/19/2016 there were many compilation errors for "cNode" and "tNode" for some reason as i had added jar to pom.xml the started complianing about this issue.
 * so all the cNode and tNode have been casted to "Concept" to resolve this issue
 */
public class TestInspecClassificationTableDriver {
    private static String infile;
    private static String outfile;


    private static final String REC_TYPE = "010";
    private static final String STATUS = "status";
    private static final String CLASS_CODE = "cc";
    private static final String CLASS_LEVEL = "level";
    private static final String CLASS_TITLE = "title";
    private static final String SCOPE_NOTES = "scope";
    private static final String SEE_ALSO_CROSS_REF = "sacr";
    private static final String SEE_CROSS_REF = "scr";
    private static final String HISTORY_SCOPE_NOTES = "hisg";
    private static final String FUTURE_SCOPE_NOTES = "240";
    public static final String tab = "\t";


    int commaDelimiter = 0;
    int semicolonDelimiter = 0;
    static List<Concept> ClassRecords;

    public static void main(String args[])
        throws Exception
    {
        infile = args[0];
        outfile = args[1];
        TestInspecClassificationTableDriver driver = new TestInspecClassificationTableDriver(infile, outfile);
        File out = new File(outfile);

        List<Concept> records  = driver.createData(infile);
        driver.writeData(records,out);
    }

    public TestInspecClassificationTableDriver(String in,
                                      String out)
    {
        this.infile = in;
        this.outfile = out;
    }

    public void writeData(List records, File outfile) throws Exception
    {
        int i =1;
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
            Iterator lit = records.iterator();
            while(lit.hasNext())
            {
                Concept record = (Concept)lit.next();
                writer.write(checkString(record.getSingleClassCodes()));

                writer.write(checkString(record.getClassStatus()));

                writer.write(checkString(record.getClassLevel()));

                writer.write(checkString(record.getClassTitle()));

                writer.write(checkString(record.getClassScopeNotes()));

                writer.write(checkString(record.getClassSeeAlsoRef()));

                writer.write(checkString(record.getClassSeeRef()));

                writer.write(checkString(record.getClassHistoryScopeNotes()));

                writer.newLine();
                i++;
            }
            writer.close();
        }

        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }


    public List<Concept> createData(String infile)
        throws Exception
    {
       List<Concept> records = new ArrayList<Concept>();

        try
        {
            String line = null;
            int i = 0;

            //HH 01/09/2014
            File file =new File(this.infile);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("rec");


            for(int j =0; j<nodeLst.getLength();j++)
            {
                Concept record = new Concept();
                Node node = nodeLst.item(j);
                NodeList childNodes = node.getChildNodes();


                for(int k=0;k<childNodes.getLength();k++)
                {
                    Node cNode = childNodes.item(k);

                   if(cNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                        String nodeName = cNode.getNodeName().trim();

                        
                        //HH 01/12/2016 take off all casting to "(Concept)" away, it is not needed when move "JDK 1.8.0_45" to be at top in order&export of build path
                        if(nodeName.equalsIgnoreCase(STATUS))
                        {
                            //record.setClassStatus(((Concept) cNode).getTextContent().trim());  // when JDK order at the end in build path
                        	
                        	record.setClassStatus(((Concept) cNode).getTextContent().trim());
                        }


                        if(nodeName.equalsIgnoreCase(CLASS_CODE))
                        {
                            record.setSingleClassCodes(((Concept) cNode).getTextContent().trim());

                        }

                        if(nodeName.equalsIgnoreCase(CLASS_LEVEL))
                        {
                            record.setClassLevel(((Concept) cNode).getTextContent().trim());
                        }

                        if(nodeName.equalsIgnoreCase(CLASS_TITLE))
                        {
                        	
                            record.setClassTitle(((Concept) cNode).getTextContent().trim());
                        }

                        if(nodeName.equalsIgnoreCase(SCOPE_NOTES))
                        {
                            record.setClassScopeNotes(((Concept) cNode).getTextContent().trim());
                        }

                        if(nodeName.equalsIgnoreCase(SEE_ALSO_CROSS_REF))
                        {
                            record.setClassSeeAlsoRef(((Concept) cNode).getTextContent().trim().replaceAll(";", ","));

                        }


                        if(nodeName.equalsIgnoreCase(SEE_CROSS_REF))
                        {

                            record.setClassSeeRef(((Concept) cNode).getTextContent().trim());
                        }



                        if(nodeName.equalsIgnoreCase(HISTORY_SCOPE_NOTES))
                        {
                            semicolonDelimiter = 1;
                            record.setClassHistoryScopeNotes(getChildNode(cNode.getChildNodes()));
                        }

                        semicolonDelimiter = 0;


                    }

                }

                records.add(record);

            }

            System.out.println("Records size is : " + records.size());

        }
    catch(IOException ex)
    {
        ex.printStackTrace();
    }

        return records;
    }


    private String getChildNode(NodeList childNodes)
    {
        StringBuffer childNodeText = new StringBuffer();
        for(int j=0;j<childNodes.getLength();j++){
            Node cNode = childNodes.item(j);
            if(cNode.getNodeType()==Node.ELEMENT_NODE)
            {
                childNodeText.append(getMixedValue(cNode));  // original

                if(j<childNodes.getLength()-1){
                        childNodeText.append(";");
                }
            }

        }
        return childNodeText.toString();

    }

  private String checkString(String string){
        String outString="";
        if(string==null){
            outString = tab;
        }
        if(string!=null){
            if(string.endsWith(";")){
                String noTrailingSemi = string.substring(0, string.length()-1);
                outString = noTrailingSemi+tab;
                if(outString.indexOf("\'")>-1){
                    outString = outString.replaceAll("\'", "\"");
                    outString = "\'"+outString+"\'";
                }
                //outString = outString+tab;
            }
            else{
                outString = string+tab;
            }
        }
        return outString;
    }


    public String notNull(String s)
    {
        if(s != null)
        {
            return s;
        }

        return "";
    }




    private static String unicodeMap(String txt)
    {
        int len = txt.length();
        StringBuffer sb = new StringBuffer();
        char c;

        for (int i = 0; i < len; i++) {
            c = txt.charAt(i);
            switch (c) {
                case '\u00A1':
                    sb.append('\u0141');
                    break;
                case '\u00A2':
                    sb.append('\u00D8');
                    break;
                case '\u00A3':
                    sb.append('\u0110');
                    break;
                case '\u00A4':
                    sb.append('\u00DE');
                    break;
                case '\u00A5':
                    sb.append('\u00C6');
                    break;
                case '\u00A6':
                    sb.append('\u008C');
                    break;
                case '\u00A7':
                    sb.append('\u00B4');
                    break;
                case '\u00A8':
                    sb.append('\u00B7');
                    break;
                case '\u00A9':
                    sb.append('\u266D');
                    break;
                case '\u00AA':
                    sb.append('\u00AE');
                    break;
                case '\u00AB':
                    sb.append('\u00B1');
                    break;
                case '\u00AC':
                    sb.append('\u01A0');
                    break;
                case '\u00AD':
                    sb.append('\u01AF');
                    break;
                case '\u00AE':
                    sb.append('\u02BE');
                    break;
                case '\u00B0':
                    sb.append('\u02BF');
                    break;
                case '\u00B1':
                    sb.append('\u0142');
                    break;
                case '\u00B2':
                    sb.append('\u00F8');
                    break;
                case '\u00B3':
                    sb.append('\u0111');
                    break;
                case '\u00B4':
                    sb.append('\u00FE');
                    break;
                case '\u00B5':
                    sb.append('\u00E6');
                    break;
                case '\u00B6':
                    sb.append('\u009C');
                    break;
                case '\u00B7':
                    sb.append('\u02BA');
                    break;
                case '\u00B8':
                    sb.append('\u0131');
                    break;
                case '\u00B9':
                    sb.append('\u00A3');
                    break;
                case '\u00BA':
                    sb.append('\u00F0');
                    break;
                case '\u00BC':
                    sb.append('\u01A1');
                    break;
                case '\u00BD':
                    sb.append('\u01B0');
                    break;
                case '\u00C0':
                    sb.append('\u00B0');
                    break;
                case '\u00C1':
                    sb.append('\u2113');
                    break;
                case '\u00C2':
                    sb.append('\u2117');
                    break;
                case '\u00C3':
                    sb.append('\u00A9');
                    break;
                case '\u00C4':
                    sb.append('\u266F');
                    break;
                case '\u00C5':
                    sb.append('\u00BF');
                    break;
                case '\u00C6':
                    sb.append('\u00A1');
                    break;
                case '\u00E0':
                    sb.append('\u0309');
                    break;
                case '\u00E1':
                    sb.append('\u0300');
                    break;
                case '\u00E2':
                    sb.append('\u0301');
                    break;
                case '\u00E3':
                    sb.append('\u0302');
                    break;
                case '\u00E4':
                    sb.append('\u0303');
                    break;
                case '\u00E5':
                    sb.append('\u0304');
                    break;
                case '\u00E6':
                    sb.append('\u0306');
                    break;
                case '\u00E7':
                    sb.append('\u0307');
                    break;
                case '\u00E8':
                    sb.append('\u0308');
                    break;
                case '\u00E9':
                    sb.append('\u030C');
                    break;
                case '\u00EA':
                    sb.append('\u030A');
                    break;
                case '\u00EB':
                    sb.append('\uFE20');
                    break;
                case '\u00EC':
                    sb.append('\uFE21');
                    break;
                case '\u00ED':
                    sb.append('\u0315');
                    break;
                case '\u00EE':
                    sb.append('\u030B');
                    break;
                case '\u00EF':
                    sb.append('\u0310');
                    break;
                case '\u00F0':
                    sb.append('\u0327');
                    break;
                case '\u00F1':
                    sb.append('\u0328');
                    break;
                case '\u00F2':
                    sb.append('\u0323');
                    break;
                case '\u00F3':
                    sb.append('\u0324');
                    break;
                case '\u00F4':
                    sb.append('\u0325');
                    break;
                case '\u00F5':
                    sb.append('\u0333');
                    break;
                case '\u00F6':
                    sb.append('\u0332');
                    break;
                case '\u00F7':
                    sb.append('\u0326');
                    break;
                case '\u00F8':
                    sb.append('\u031C');
                    break;
                case '\u00F9':
                    sb.append('\u032E');
                    break;
                case '\u00FA':
                    sb.append('\uFE22');
                    break;
                case '\u00FB':
                    sb.append('\uFE23');
                    break;
                case '\u00FE':
                    sb.append('\u0313');
                    break;
                case '\u001E':
                    // Get rid of any end of field
                    //sb.append('');
                    break;
                case '\r':
                case '\n':
                    // Escape '
                    //sb.append('');
                    //sb.append('\'');
                    break;
                case '"':
                    // Escape '
                    sb.append('"');
                //sb.append('"');
                //break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }


    private String getMixedValue(Node inputNode)
    {
        StringBuffer mainTermStringBuffer = new StringBuffer();


        if(inputNode.hasChildNodes())
        {
            NodeList tNode = inputNode.getChildNodes();

            for(int k=0;k<tNode.getLength();k++)
            {

                if(tNode.item(k).getNodeType()==Node.ELEMENT_NODE && tNode.item(k).getNodeName().equalsIgnoreCase("ccg"))
                {
                    String NodeName = tNode.item(k).getNodeName();
                    mainTermStringBuffer.append(": ");
                    if(tNode.item(k).hasChildNodes())
                    {
                        NodeList cNode = tNode.item(k).getChildNodes();

                        for(int j = 0 ; j<cNode.getLength();j++)
                        {
                            if(cNode.item(j).getNodeType()==Node.ELEMENT_NODE)
                            {
                                mainTermStringBuffer.append(((Concept) cNode.item(j)).getTextContent().trim());

                                if(j<cNode.getLength()-1){
                                    mainTermStringBuffer.append(", ");
                            }
                            }
                        }
                    }
                    //mainTermStringBuffer.append(": "+tNode.item(k).getTextContent().trim());   // original of mine
                }

                else
                {
                 mainTermStringBuffer.append(((Concept) tNode.item(k)).getTextContent());  //H 03-07-2014 Original

                }

            }
        }

        return mainTermStringBuffer.toString().trim();
    }

}
