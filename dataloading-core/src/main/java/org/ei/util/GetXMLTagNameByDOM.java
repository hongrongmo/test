package org.ei.util;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class GetXMLTagNameByDOM{


static Set nameSet = new HashSet();
static public void main(String[] arg){
	try {

	String xmlFile = arg[0];
	File file = new File(xmlFile);
	if(file.exists()){
		 // Create a factory
		 DocumentBuilderFactory factory =
			DocumentBuilderFactory.newInstance();
		 // Use the factory to create a builder
		 DocumentBuilder builder = factory.newDocumentBuilder();
		 Document doc = builder.parse(xmlFile);
		 // Get a list of all elements in the document
		 NodeList list = doc.getElementsByTagName("item");
		 getSubNode(null,list);
	}
	else{
		System.out.print("File not found!");
	}


	}
	catch (Exception e) {
	  e.printStackTrace();
	 System.exit(1);
	}
}

private static void getSubNode(String ParentNodeName, NodeList nodeList)
{
	 for (int i=0; i<nodeList.getLength(); i++) {
		// Get element
		Node element = (Node)nodeList.item(i);
		String nodeName = element.getNodeName();
		if(ParentNodeName!=null)
		{
			nodeName=ParentNodeName+"_"+nodeName;
		}

		if((!element.getNodeName().equals("#text"))&&!nameSet.contains(nodeName))
		{
			 nameSet.add(nodeName);
			 System.out.println(nodeName);
			 NamedNodeMap attrMap = element.getAttributes();
			 if(attrMap!=null){
				for (int j = 0; j < attrMap.getLength(); ++j)
				{
					 Node attr = attrMap.item(j);
					 System.out.println(nodeName+"."+attr.getNodeName());
				}
			}
		}

		if(!(element.getNodeType()==(Node.TEXT_NODE))){
			NodeList childNode = element.getChildNodes();
			getSubNode(nodeName,childNode);
		}

	}
}

	/*
static public void main(String[] arg){
	 try {

		 String xmlFile = arg[0];
		 File file = new File(xmlFile);
		 if(file.exists()){
			 // Create a factory
			 DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			 // Use the factory to create a builder
			 DocumentBuilder builder = factory.newDocumentBuilder();
			 Document doc = builder.parse(xmlFile);
			 // Get a list of all elements in the document
			 NodeList list = doc.getElementsByTagName("*");
			 System.out.println("XML Elements: ");
			 Set nameSet = new HashSet();
			 for (int i=0; i<list.getLength(); i++) {
				 // Get element
				 Element element = (Element)list.item(i);
				 String nodeName = element.getNodeName();


				 if(!nameSet.contains(nodeName))
				 {
					 nameSet.add(nodeName);
					 System.out.println(nodeName);
					 NamedNodeMap attrMap = element.getAttributes();

					 for (int j = 0; j < attrMap.getLength(); ++j)
					 {
						 Node attr = attrMap.item(j);
						 System.out.println(nodeName+"."+attr.getNodeName());
				 	}
				 }

			 }
		 }
		 else{
		 	System.out.print("File not found!");
		 }


	 }
	 catch (Exception e) {
		  e.printStackTrace();
		 System.exit(1);
	 }
   }
  */
}
