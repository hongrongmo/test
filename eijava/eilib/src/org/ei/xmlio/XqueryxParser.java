package org.ei.xmlio;


import java.io.ByteArrayInputStream;

import org.ei.xml.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XqueryxParser
{

    public static void main(String args[])
        throws Exception
    {
        String queryString = "<query><andQuery><orQuery><word path=\"ti\">hello*</word><word>world acid</word></orQuery><notQuery><word>goodbye</word><word>shift</word></notQuery></andQuery></query>";
        XqueryxParser xp = new XqueryxParser();
        Query query = (Query)xp.parse(queryString);
        XqueryxRewriter xr = new XqueryxRewriter();
        query.accept(xr);
        System.out.println(xr.getQuery());
    }


    public Query parse(String xmlString) throws Exception
    {
        DOMParser parser = new DOMParser();
        ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes());
        Document doc = parser.parse(in);
        NodeList queryNodes = doc.getChildNodes();
        Query q = new Query();
        walk(queryNodes.item(0),q);
        return q;
    }

    public void walk(Node node,
                     XqueryxNode parent) throws Exception
    {
        String nodeName = node.getNodeName();
        if(nodeName.equals("query"))
        {
            NodeList nodes = node.getChildNodes();
            for(int x=0; x<nodes.getLength(); ++x)
            {
                walk(nodes.item(x), parent);
            }
        }
        else if(nodeName.equals("orQuery"))
        {
           OrQuery o = new OrQuery();
           parent.addChild(o);
           NodeList nodes = node.getChildNodes();
           for(int x=0; x<nodes.getLength(); ++x)
           {
               walk(nodes.item(x), o);
           }

        }
        else if(nodeName.equals("andQuery"))
        {
            AndQuery a = new AndQuery();
            parent.addChild(a);
            NodeList nodes = node.getChildNodes();
            for(int x=0; x<nodes.getLength(); ++x)
            {
                walk(nodes.item(x), a);
            }
        }
        else if(nodeName.equals("notQuery"))
        {
            NotQuery n = new NotQuery();
            parent.addChild(n);
            NodeList nodes = node.getChildNodes();
            for(int x=0; x<nodes.getLength(); ++x)
            {
                walk(nodes.item(x), n);
            }
        }
        else if(nodeName.equals("word"))
        {
            Word w = new Word();
            parent.addChild(w);
            if(node.hasAttributes())
            {
                NamedNodeMap map = node.getAttributes();
                String field = map.getNamedItem("path").getNodeValue();
                w.setField(field);
            }

            w.setWord(node.getFirstChild().getNodeValue());
        }
        else
        {
            throw new Exception("Invalid element name. Valid elements are query, andQuery, orQuery, notQuery, word");
        }
    }

}