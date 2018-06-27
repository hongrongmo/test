package org.ei.util;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.io.*;
import java.util.*;
//import common.*;


public class PrintXMLTagNameBySAX extends DefaultHandler {
   // A stack to keep track of the tag names
   // that are currently open ( have called
   // "startElement", but not "endElement".)
   private Stack tagStack = new Stack();
   // Local list of item names...
   private Vector items = new Vector();
   // Customer name...
   private String customer;
   // Buffer for collecting data from
   // the "characters" SAX event.
   private CharArrayWriter contents = new CharArrayWriter();
   // Override methods of the DefaultHandler class
   // to gain notification of SAX Events.
   //
        // See org.xml.sax.ContentHandler for all available events.
   //
   public void startElement( String namespaceURI,
               String localName,
              String qName,
              Attributes attr ) throws SAXException {
      contents.reset();
      // push the tag name onto the tag stack...
      tagStack.push( localName );
      // display the current path that has been found...
      System.out.println( "path found: [" + getTagPath() + "]" );
   }
   public void endElement( String namespaceURI,
               String localName,
              String qName ) throws SAXException {
      if ( getTagPath().equals( "item" ) ) {
         customer = contents.toString().trim();
      }
      else if ( getTagPath().equals( "item" ) ) {
         items.addElement( contents.toString().trim() );
      }
      // clean up the stack...
      tagStack.pop();
   }
   public void characters( char[] ch, int start, int length )
                  throws SAXException {
      // accumulate the contents into a buffer.
      contents.write( ch, start, length );
   }
   // Build the path string from the current state
   // of the stack...
   //
   // Very inefficient, but we'll address that later...
   private String getTagPath( ){
      //  build the path string...
      String buffer = "";
      Enumeration e = tagStack.elements();
      while( e.hasMoreElements()){
               buffer  = buffer + "/" + (String) e.nextElement();
      }
      return buffer;
   }
   public Vector getItems() {
           return items;
   }
   public String getCustomerName() {
         return customer;
   }
   public static void main( String[] argv ){
      System.out.println( "PrintXMLTagName:" );
      try {
         // Create SAX 2 parser...
         XMLReader xr = XMLReaderFactory.createXMLReader();
         // Set the ContentHandler...
         PrintXMLTagNameBySAX ex1 = new PrintXMLTagNameBySAX();
         xr.setContentHandler( ex1 );
         System.out.println();
         System.out.println("Tag paths located:");
         // Parse the file...
         xr.parse( new InputSource(
               new FileReader( "03012106_0001.XML" )) );
         System.out.println();
         System.out.println("Names located:");
         // Display Customer
         System.out.println( "Customer Name: " + ex1.getCustomerName() );
         // Display all item names to stdout...
         System.out.println( "Order Items: " );
         String itemName;
         Vector items = ex1.getItems();
         Enumeration e = items.elements();
         while( e.hasMoreElements()){
                   itemName = (String) e.nextElement();
            System.out.println( itemName );
         }
      }catch ( Exception e )  {
         e.printStackTrace();
      }
   }
}
