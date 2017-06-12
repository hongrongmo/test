package vtw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jms.JMSException;

import org.apache.log4j.Logger;





/**
 * @author TELEBH
 * @Date: 02/07/2017
 * @Description: Backup Messages in VTW_Patents-dev:  
 * read VTW Message, parse it, archive it in Oracle table for later process in case 
 * there is a large flow of messages to read for later process
 * 
 * NOTE 1:
 * 	Each SQS message should be processed once, Amazon SQS distributed nature may result in Message duplication, so the our application should handle this
 *  Each SQS message contains a unique message "@id", so need to set unique index on this field in oracle table to avoid duplication. 
 *  
 *  NOTE 2:
 *  this class has same logic as "ArchiveVTWPtaentRefeed.java except that calls "Asset API" for patent download instead of "Search API" 
 *  as per Bart request to use Asset API , old logic of Search API still valid, in case we need to use it for testing, can use class mentioned above
 *  
 *  NOTE3:
 *  this class is used for AssetAPI
 */

public class ArchiveVTWPatentAsset{

	// get the list of Patent-Ids; with their signedAssetURL if any;  to download 
	private static Map<String,String> patentIds = new LinkedHashMap<String,String>();   // only for testing
	
// only for testing
	public static void main(String [] args)
	{
		VTWAssetAPI api = new VTWAssetAPI("nullpointer", 1);
		patentIds.put("AU2010281317A1", "http://dev-ucs-content-store-eu-west.s3.amazonaws.com/content/pat%3AAU2010281317A1/MAIN/application/xml/02d84460608e91d8510f8725fe109b7a/AU2010281317A1.xml?AWSAccessKeyId=AKIAIKW4U6PKMIE3KSLQ&Expires=1471522524&Signature=u1zyiRsWNXf5DlgT5d7zR9iadlY%3D");
		api.downloadPatent(patentIds, "forward");
	}
}
