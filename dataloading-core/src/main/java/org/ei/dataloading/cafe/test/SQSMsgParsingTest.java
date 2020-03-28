package org.ei.dataloading.cafe.test;

import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;

/**
 * 
 * @author TELEBH
 *
 */
public class SQSMsgParsingTest {

	private Perl5Util perl = new Perl5Util();
	HashMap<String,String> messageFieldKeys = new HashMap<String,String>();
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//case1:
		//String sqsMessage="{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"0022264446\", \"modification\" : \"CONTENT\", \"issn\" : \"07339399\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1454090150929\", \"action\" : \"u\", \"pui\" : \"446001092\", \"xocs-timestamp\" : \"2016-01-29T12:03:04.671893Z\", \"sort-year\" : \"1985\", \"eid\" : \"2-s2.0-0022264446\", \"doi\" : \"10.1061/(ASCE)0733-9399(1985)111:10(1277)\", \"load-unit-id\" : \"swd_uC43700434966.dat\", \"version\" : \"2016-01-29T06:47:50.000050+00:00\" } ] }";
		
		//case2:
		//String sqsMessage = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"35048835910\", \"modification\" : \"CONTENT\", \"issn\" : \"15409295\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1455815067342\", \"action\" : \"u\", \"pui\" : \"47557320\", \"xocs-timestamp\" : \"2016-02-18T16:20:40.040043Z\", \"sort-year\" : \"2007\", \"eid\" : \"2-s2.0-35048835910\", \"doi\" : \"10.1890/1540-9295(2007)5[415:UWUSPE]2.0.CO;2\", \"load-unit-id\" : \"swd_uC43700438160.dat\", \"version\" : \"2016-02-18T13:51:32.000032+00:00\" } ] }";
		
		//case3:
		String sqsMessage = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84929905832\", \"document-type\" : \"core\", \"epoch\" : \"1459849513121\", \"prefix\" : \"2-s2.0-\", \"action\" : \"d\", \"xocs-timestamp\" : \"n/a\", \"eid\" : \"2-s2.0-84929905832\", \"load-unit-id\" : \"swd_dC43700445283.dat\", \"version\" : \"n/a\" } ] }";
		
		SQSMsgParsingTest msgParsingTest= new SQSMsgParsingTest(); 
		//msgParsingTest.getFieldsKeys(sqsMessage);  //old way using stringtokenizer for subtoken
		msgParsingTest.getFieldsKeysWithSubString(sqsMessage);  //new way using substr
		msgParsingTest.DisplaySQSFields();
	}
	
	
	public void getFieldsKeys(String fieldKeys)
	{
		String key = "";
		String value = "";
		String key_Value = "";
		StringTokenizer subTokens = null;
		
		StringTokenizer token = new StringTokenizer(fieldKeys, ",");
		while(token.hasMoreTokens())
		{
			if(token !=null)
			{
				key_Value = formateString(token.nextToken());
				subTokens = new StringTokenizer(key_Value, ":");
				
				while(subTokens.hasMoreTokens())
				{
					key = formateString(subTokens.nextToken());
					
					if(key !=null)
					{
						if(key.equalsIgnoreCase("entries"))
						{
							key = formateString(subTokens.nextToken(":"));
						}
						if(key.equalsIgnoreCase("version") || key.equalsIgnoreCase("xocs-timestamp") || key.equalsIgnoreCase("doi"))
						{
							value = formateString(subTokens.nextToken(";"));
							value = value.substring(value.indexOf(":")+1, value.length()).trim();
						}
						else
						{
							value = formateString(subTokens.nextToken(":")).trim();
						}


						if(! (key.equalsIgnoreCase("entries")))
						{
							messageFieldKeys.put(key, value);
						}
					}
					
				}  //inner while
				
			}  //if
		}  //outer while
		
		
	}
	
	
	public void getFieldsKeysWithSubString(String fieldKeys)
	{
		String key = "";
		String value = "";
		String key_Value = "";
		String substr = "";
		
		StringTokenizer token = new StringTokenizer(fieldKeys, ",");
		while(token.hasMoreTokens())
		{
			if(token !=null)
			{
				key_Value = formateString(token.nextToken());
				if(key_Value !=null)
				{
						if(key_Value.contains("entries"))
						{
							substr = key_Value.substring(key_Value.indexOf(":")+1,key_Value.length()).trim();
							key = formateString(substr.substring(0, substr.indexOf(":")));
							value = formateString(substr.substring(substr.indexOf(":")+1, substr.length()).trim());
						}
						
						else
						{
							key = formateString(key_Value.substring(0, key_Value.indexOf(":")));
							value = formateString(key_Value.substring(key_Value.indexOf(":") +1, key_Value.length())).trim();
						}

							messageFieldKeys.put(key, value);
						
					
				}  //inner while
				
			}  //if
		}  //outer while
		
		
	}
	
	
	public String formateString(String str)
	{
		if(str !=null)
		{
			if(str.contains("{"))
			{
				str = perl.substitute("s/{//g", str);
			}
			if(str.contains("}"))
			{
				str = perl.substitute("s/}//g", str);
			}
			if(str.contains("\""))
			{
				str = perl.substitute("s/\"//g", str);
			}
			if(str.contains("["))
			{
				str = perl.substitute("s/\\[//g", str);
			}
			if(str.contains("]"))
			{
				str = perl.substitute("s/\\]//g", str);
			}
			/*str = str.replace("{", "");
		str = str.replace("}", "");
		str = str.replace("\"\"", "");*/
		}
		return str.trim();
	}
	
	
	public void DisplaySQSFields()
	{
		//printout all fields of SQS Message
		
		if(messageFieldKeys.size() >0)
		{
			for(String Key:messageFieldKeys.keySet())
			{
				System.out.println(Key+" : " + messageFieldKeys.get(Key));
			}
		}
	
	}
}
