package org.ei.data.books.tools;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class Validator {

	public static void main(String[] args) {
		
		String strFilename = "1158609229460_updat_rowset_10000_book-1.xml";
		//System.out.println(Charset.defaultCharset());
		//System.setProperty("org.xml.sax.driver","org.apache.xerces.parsers.SAXParser");
//
//		try {
//			System.out.println("FileInputStream");
//			FileInputStream  fin = new FileInputStream(strFilename);
//			int i = 0; 
//			long x = 0;
//			for(;((i = fin.read()) != -1);x++) {
//				System.out.println(i);
//			}
//			System.out.println("read ==> " + x);
//			fin.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		try {
//			System.out.println("InputStreamReader");
//			FileInputStream  fin = new FileInputStream(strFilename);
//			InputStreamReader is = new InputStreamReader(fin,"ISO-8859-1");
//			int i = 0;
//			long x = 0;
//			for(;((i = is.read()) != -1);x++) {
//				System.out.println(i);
//			}
//			System.out.println("read ==> " + x);
//			is.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		try {
//			XMLReader validator = new org.apache.xerces.parsers.SAXParser();
//			validator.setFeature("http://xml.org/sax/features/validation",true);
//			FileInputStream  fin = new FileInputStream(strFilename);
//			validator.parse(new InputSource(fin));
//		} catch (SAXNotRecognizedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (SAXNotSupportedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		SAXParser saxParser;
		try {
			InputStreamReader is = new InputStreamReader(
					new FileInputStream(
							strFilename));
			
			System.out.println("InputStreamReader encoding is " + is.getEncoding());
			saxParser = factory.newSAXParser();
			XMLReader aparser = saxParser.getXMLReader();
			aparser.setErrorHandler(new MyErrorHandler());
			aparser.parse(new InputSource(is));
		} catch (ParserConfigurationException e) {
			System.out.println(e.toString());
		} catch (SAXException e) {
			System.out.println(e.toString());
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		System.out.println(System.currentTimeMillis());
	}

	private static class MyErrorHandler implements ErrorHandler {
		public void warning(SAXParseException e) throws SAXException {
			System.out.println("Warning: ");
			printInfo(e);
		}

		public void error(SAXParseException e) throws SAXException {
			System.out.println("Error: ");
			printInfo(e);
		}

		public void fatalError(SAXParseException e) throws SAXException {
			System.out.println("Fatal error: ");
			printInfo(e);
		}

		private void printInfo(SAXParseException e) {
			System.out.println("   Public ID: " + e.getPublicId());
			System.out.println("   System ID: " + e.getSystemId());
			System.out.println("   Line number: " + e.getLineNumber());
			System.out.println("   Column number: " + e.getColumnNumber());
			System.out.println("   Message: " + e.getMessage());
		}
	}

}
