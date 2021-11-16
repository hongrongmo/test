package InsThesClassCode;

/***
 * @author TELEBH
 * @Date: 01/17/2017
 * @Descriptrion: reading Inspec Thesaurus Classification Code XML file did not read "&" in HTML entities in the XML file
 * (i.e. &acute;) it was read as (acute) which is wrong. this java class below reads it right after converting (&) in the xml to it's 
 * equivalent HTML entity (&amp;) then it was read properly as follows
 * 
 * 
 /***** Input
  * <?xml version="1.0"?>
<company>
	<staff>
		<firstname>Lorentz and Poincare&amp;acute; invariance</firstname>
		<lastname>mook kim</lastname>
		<nickname>mkyong</nickname>
		<salary>100000</salary>
	</staff>
	<staff>
		<firstname>low</firstname>
		<lastname>yin fong</lastname>
		<nickname>fong fong</nickname>
		<salary>200000</salary>
	</staff>
</company>
*****/

 /***** Output
  *  First Name : Lorentz and Poincare&acute; invariance
  *  Last Name : mook kim
  *  Nick Name : mkyong
  *	 Salary : 100000
  *  First Name : low
  *  Last Name : yin fong
  *  Nick Name : fong fong
  *  Salary : 200000
  ********/


import java.io.File;
import java.io.IOException;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;


public class ReadXmlSpecialChar {

		public static void main(String[] args) {

		  SAXBuilder builder = new SAXBuilder();
		  File xmlFile = new File("file.xml");

		  try {

			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			List list = rootNode.getChildren("staff");

			for (int i = 0; i < list.size(); i++) {

			   Element node = (Element) list.get(i);

			   //System.out.println("First Name : " + node.getChildText("firstname"));
			   System.out.println("First Name : " + node.getChildTextNormalize("firstname"));
			   System.out.println("Last Name : " + node.getChildText("lastname"));
			   System.out.println("Nick Name : " + node.getChildText("nickname"));
			   System.out.println("Salary : " + node.getChildText("salary"));

			  
			}

		  } catch (IOException io) {
			System.out.println(io.getMessage());
		  } catch (JDOMException jdomex) {
			System.out.println(jdomex.getMessage());
		  }
		}
	
}
