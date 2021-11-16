package cafe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.ei.dataloading.awss3.AmazonS3Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;


/**
 * 
 * @author TELEBH
 * @Date: 05/26/2021
 * @Description: For OA Re-delivery and process for new OA values (text) instead of numeric (0,1,2) identified "60936" Cafe ANI records
 * still with value "1" and "2" so need to download these ANIs from Cafe URL provided by Paul Mosteret i.e. https://sccontent.elsevier.com/cafe/?id=84994750595&type=ANI
 * 
 * where numeric value is ANI's EID
 * 
 */
public class DownloadCafeFileFromURL {

	String value="12";
	AmazonS3 s3Client = null;
	public static void main(String[] args)
	{
		DownloadCafeFileFromURL obj = new DownloadCafeFileFromURL();
		
		try 
		{
			
			//obj.run();
			//obj.testHTTPCon("85076743924");
			
			// connect to AmazonS3
			obj.s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			obj.run();
			//obj.downloadFileFromS3("85076743924");  		// for individual file/S3 key testing
						
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * @Description: Loop through EID list in input txt file and for each EID download file from S3 bucket to local dir
	 */
	public void run()
	{
		String inFile = "remaining_oa.txt";

	
		try(BufferedReader input = new BufferedReader(new FileReader(new File(inFile)))) {

			//Download Start Time
			long startTime = System.currentTimeMillis();
			
	        while(input.readLine() != null){
	        	String key = input.readLine();
	        	System.out.println("Start to Download ANI EID: " + key);
	        	//testHTTPCon(key);
	        	downloadFileFromS3(key);				// for multiple files 
	        }
	        
	        //DOwnload End Time
	    	long finishTime = System.currentTimeMillis();
			System.out.println("Time to runQuery: " + (finishTime - startTime));
	    	
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{

			e.printStackTrace();
		}
		
	
				
	}
	public void testHTTPCon(String key) throws MalformedURLException
	{
		try {
			URL url = new URL("https://sccontent.elsevier.com/cafe?id=" + key + "&type=ANI");
			HttpURLConnection con;

			con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST");
			con.setDoOutput(true);
			DataOutputStream dos = new DataOutputStream(con.getOutputStream());
			//dos.writeChars("");
	
			int responseCode = con.getResponseCode();
			
			
			System.out.println("responseCode: " + responseCode);
			// Only read response if connection was successful
			if(responseCode == HttpURLConnection.HTTP_OK)
			{
				BufferedWriter out = new BufferedWriter(new FileWriter(new File(System.getProperty("user.dir") + File.separator
						 + "cafe" + File.separator + key + ".xml")));
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line;
				StringBuilder response = new StringBuilder();
				while((line = in.readLine()) != null)
				{
					response.append(line);
				}
				out.write(response.toString().replaceAll("><", ">\n<"));
				//close output stream
				out.close();
				
				// close stream
				in.close();
				
				//close con dos
				dos.close();
				
			
				
			
			}
			else
			{
				System.out.println("Key: " + key + " Not exist!!!!");
				
			}
			
			dos.flush();
			dos.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	
	public void downloadFileFromS3(String key)
	{
		 BufferedReader breader = null;
		 PrintWriter out = null;
		try
		{
		S3Object s3object = s3Client.getObject(new GetObjectRequest ("sccontent-ani-xml-prod", key));
		InputStream s3objectData = s3object.getObjectContent();
		//parseS3File(objectData);  // for single file testing
		
		if(s3objectData !=null)
		{
					
		
		 
		 //HH added 05/11/2021 to support new dl structure
		 StringBuilder sb = new StringBuilder();
	
			breader = new BufferedReader(new InputStreamReader(s3objectData));
			breader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(breader.readLine().replaceAll("><", ">\n<").getBytes())));
		
		File file = new File("cafe"+"/"+key+".xml");
		if (!file.exists()) 
		{
			System.out.println("Downloaded: "+file.getName());
			
		}
		else
		{
			System.out.println("file:" +  file.getName() + "already exist");
		}
		String line = null;
		out = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(),true)));
		while ((line = breader.readLine()) !=null)
		{
			//out.println(line.toString().replaceAll("><", ">\n<"));
			out.println(line);
		}
		
		}
		}
		catch (IOException e) {

			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			try
			{
				if(breader !=null)
				{
					breader.close();
					
					
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
			try
			{
				if(out !=null)
				{
					out.flush();
					out.close();
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		}
	

}
