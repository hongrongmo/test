package org.ei.dataloading.bd.loadtime.nosql;

/**
 * 
 * @author TELEBH
 * @Date: 02/26/2021
 * @Description: Schemas' constants for XML documents 
 */
public class SchemaConstants {

	
	 public static final String BD_ENCODING="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; 
	 public static final String BD_STARTROOTELEMENT = "<bibdataset xsi:schemaLocation=\"http://www.elsevier.com/xml/ani/ani http://www.elsevier.com/xml/ani/compendex.xsd\" xmlns=\"http://www.elsevier.com/xml/ani/ani\" xmlns:aii=\"http://www.elsevier.com/xml/ani/internal\" xmlns:ce=\"http://www.elsevier.com/xml/ani/common\" xmlns:mml=\"http://www.w3.org/1998/Math/MathML\" xmlns:ait=\"http://www.elsevier.com/xml/ani/ait\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
	 public static final String BD_ENDROOTELEMENT   ="</bibdataset>";
	    
	
	 
	 public static final String INS_ENCODING = "<?xml version=\"1.0\" encoding=\"US-ASCII\"?>";
	 public static final String INS_SCHEMA = "<!DOCTYPE inspec SYSTEM \"inspec_xml.dtd\">";
	 public static final String INS_STARTROOTELEMENT = "<inspec>";
	 public static final String INS_ENDROOTELEMENT = "</inspec>";

	 
	 
}
