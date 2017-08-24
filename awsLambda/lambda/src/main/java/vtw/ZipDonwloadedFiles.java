package vtw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 
 * @author TELEBH
 * @Date: 03/22/2017
 * @Description: Test Zip VTW downloaded files as there are errors when unzip files in linux as follows
 * 
 * 	/*** Errors when unzip -tq filename.zip (unzip -tq 916.zip):
 * 
 * 	file #90:  bad zipfile offset (local header sig):  1770977
	file #91:  bad zipfile offset (local header sig):  1793733
	file #92:  bad zipfile offset (local header sig):  1805234
  	error:  invalid compressed data to inflate US8557727B2.xml
  	error:  invalid compressed data to inflate US6533271B1.xml
  	
  	EP2585772A2.xml:  mismatching "local" filename (US20130154891A1.xml),
         continuing with "central" filename version
  	error:  invalid compressed data to inflate EP2585772A2.xml

 */
public class ZipDonwloadedFiles {

	static String downloadDir;
	static String zipfileName;
	public static void main(String[] args) throws Exception 
	{
		
		if(args.length >2)
		{
			downloadDir = args[0];
			zipfileName = args[1];
		}
		else
		{
			System.out.println("not enough parameters!");
			System.exit(1);
		}
		System.out.println("Starting zip files in downloadDir:  " + downloadDir);
		zipDownloads(201713,downloadDir,zipfileName);
	}
	
	public static void zipDownloads(int loadnumber, String downloadDirName, String zipfileName) throws Exception
	{
		int zipFileID = 1;
		int curRecNum = 0;
		int sequence = Integer.parseInt(zipfileName);

		System.out.println("Zip downloaded files");

		

		String currDir = System.getProperty("user.dir");
		File zipsDir = new File(currDir+"/zips");
		if(!(zipsDir.exists()))
		{
			zipsDir.mkdir();
		}

		zipsDir = new File(zipsDir + "/vtw");
		if(!(zipsDir.exists()))
		{
			zipsDir.mkdir();
		}

		//zipsDir = new File(zipsDir+"/" +loadnumber);  // for organizing downloades based on loadnumber
		zipsDir = new File(zipsDir+"/tmp");
		if(!(zipsDir.exists()))
		{
			zipsDir.mkdir();
		}


		File downDir = new File(currDir + "/raw_data/"+downloadDirName);

		String[] xmlFiles = downDir.list();  
		File[] xmlFilesToDelete = downDir.listFiles();
		byte[] buf = new byte[1024];


		// create zip files if any files were downloaded, otherwise no zip file should be created
		if(xmlFiles.length >0)
		{	
			String zipFileName = zipsDir + "/" + sequence + ".zip";
			ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileName));

			for(int i=0; i<xmlFiles.length; i++)
			{
				// limit each single zip file to hold recsPerZipfile, otherwise split to multiple zip files
				if(curRecNum >= 2000)
				{
					curRecNum = 0;
					outZip.close();

					zipFileID++;
					sequence++;

					zipFileName = zipsDir + "/" + sequence + ".zip";
					outZip = new ZipOutputStream(new FileOutputStream(zipFileName));	
				}
				FileInputStream in = new FileInputStream(downDir + "/" + xmlFiles[i]);
				outZip.putNextEntry(new ZipEntry(xmlFiles[i]));

				int length;
				while((length = in.read(buf)) >0)
				{
					outZip.write(buf,0,length);
				}
				outZip.closeEntry();
				in.close();
				xmlFilesToDelete[i].delete();  // delete original xml file to save space

				++curRecNum;
			}

			outZip.close();
			downDir.delete();
		}


	}




}
