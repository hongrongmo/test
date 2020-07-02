package org.ei.util.kafka;

import org.json.JSONObject;
import org.json.XML;
//import org.json.simple.JSONObject;
//import org.json.simple.XML;
 
public class XMLToJSON {
	public static void main(String...s){
		String xml_data = "<student><name>Neeraj Mishra</name><age>22</age></student>";
 
		//converting xml to json
		JSONObject obj = XML.toJSONObject(xml_data);
		
		System.out.println(obj.toString());
	}
}