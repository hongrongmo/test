package InsThesClassCode;

import java.io.File;
import java.io.IOException;

//**** used for XML parsing using Node
/*import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;*/



import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;


import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;

public class InsThesClassCodes {

	public static void main(String[] args) 
	{
		String inFile = null, outFile = null;

		if(args.length >1)
		{
			if(args[0] !=null)
			{
				inFile = args[0];
			}
			if(args[1] !=null)
			{
				outFile = args[1];
			}
		}


		try {
			File file =new File(inFile);
			SAXBuilder builder = new SAXBuilder();

			Document document = builder.build(file);
			Element rootNode = document.getRootElement();
			List childNodesList = rootNode.getChildren("rec");

			for(int i=0; i<childNodesList.size();i++)
			{
				Element node = (Element) childNodesList.get(i);

				System.out.println("Status: " +  node.getChildText("status"));
				System.out.println("classification code: " + node.getChildText("cc"));
				System.out.println("class level: " + node.getChildText("level"));
				System.out.println("Title: " +  node.getChildText("title"));
				System.out.println("Scope: " +  node.getChildText("scope"));
				System.out.println("see also cross ref: " + node.getChildText("sacr").replaceAll(";", ","));
				System.out.println("see cross ref: " + node.getChildText("scr"));


				if(node.getName().equalsIgnoreCase("hisg"))
				{
					List cNodeList = node.getChildren("hist");
					for(int j=0; j<cNodeList.size();j++)
					{

					}
				}
			}

			/*DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				  DocumentBuilder db = dbf.newDocumentBuilder();
				  Document doc = db.parse(file);
				  doc.getDocumentElement().normalize();
				  NodeList nodeLst = doc.getElementsByTagName("rec");
				  //System.out.println("Root element " + doc.getDocumentElement().getNodeName());

				  for(int i=0;i<nodeLst.getLength();i++)
				  {

					  Node node = nodeLst.item(i);
					  NodeList childNodes = node.getChildNodes();
					  	for(int j=0;j<childNodes.getLength();j++)
					  	{
					  		Node cNode = childNodes.item(j);
					  		if(cNode.getNodeType()==Node.ELEMENT_NODE)
					  		{
					  			String nodeName = cNode.getNodeName().trim();

					  			System.out.println("NodeName: " + nodeName);
					  			System.out.println("NodeValue: " + cNode.getTextContent());
					  		}
					  	}

				  }*/
		} /*catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 */
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	private String getChildNode(List childs)
	{
		StringBuffer childNodeText = new StringBuffer();
		for(int j=0; j<childs.size();j++)
		{
			Element cNode = (Element) childs.get(j);

			childNodeText.append(getMixedValue(cNode));  // original

			if(j<childs.size()-1){
				childNodeText.append(";");
			}


		}
		return childNodeText.toString();

	}


	private String getMixedValue(Element node)
	{
		StringBuffer mainTermStringBuffer = new StringBuffer();


		if(node.getChildren().size() >0)
		{
			List tNodeList = node.getChildren();

			for(int k=0;k<tNodeList.size();k++)
			{

				if(((Element)tNodeList.get(k)).getName().equalsIgnoreCase("ccg"))
				{
					String NodeName = ((Element)tNodeList.get(k)).getName();
					mainTermStringBuffer.append(": ");
					if(((Element)tNodeList.get(k)).getChildren().size() >0)
					{
						List cNodeList = ((Element)tNodeList.get(k)).getChildren();

						for(int j = 0 ; j<cNodeList.size();j++)
						{

							mainTermStringBuffer.append(((Element)cNodeList.get(j)).getChildText(((Element)cNodeList.get(j)).getName()));

							if(j<cNodeList.size()-1){
								mainTermStringBuffer.append(", ");
							}
						}
					}
				}

			}


		}
	

	return mainTermStringBuffer.toString().trim();
	}
}



