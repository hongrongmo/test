import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

import com.google.common.io.FileBackedOutputStream;

/*
 * @author: Hanan
 * @Date: 12/15/2017
 * @Description: i have a very big text file "apr-all.txt" that contains 13M+ M_ID for author profiles
 * that is already indexed in ES. the file was generated using Pythin script "ES-scan.py"
 * but for unknown reason "\n" in Python caused when loaded the file to table it added "double quotes" 
 * that are invesible and could nnot remove it from oracle. though i changed it to be "\r\n" in Python
 * but still did not resolve the issue. VI the file replacing "\r" to "\n" still did not work
 * 
 * so here ia m trying to read the out file from Python that is "\r" delimited and
 * re-generate the file to have "\n" instead to be able to load to oracle table
 */
public class ReadTextFile 
{
	public static void main(String[] args)
	{
		Scanner scan = null;
		BufferedReader br = null;
		PrintWriter out = null;
		try
		{
			String currDir = System.getProperty("user.dir");
			System.out.println("Current Dir: " + currDir);
			String infileName = currDir + "/apr-all.txt";
			String line = null;
			
			String outfileName = currDir + "/apr-all-lines.txt";
			/*scan = new Scanner(new File(fileName));
			scan.useDelimiter("\n");
			
			
			
						
		while(scan.hasNext())
			{
				line = scan.nextLine();
				System.out.print(line);
			}*/
			
			FileReader fr = new FileReader(new File(infileName));
			br = new BufferedReader(fr);
			
			out = new PrintWriter(new File(outfileName));
			
			while((line = br.readLine()) !=null)
			{
				System.out.println(line);
				out.println(line);
			}
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("Exception reading from file, reason: " + ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(scan !=null)
				{
					scan.close();
				}
			}
			
			
			catch(Exception e)
			{
				System.out.println("Failed to close the scanner!!!!");
				e.printStackTrace();
			}
			
			try
			{
				if(br !=null)
				{
					br.close();
				}
			}
			
			catch(Exception e)
			{
				System.out.println("Failed to close the BufferedReader!!!!");
				e.printStackTrace();
			}
			try
			{
				if(out !=null)
				{
					out.close();
				}
			}
			catch(Exception e)
			{
				System.out.println("Failed to close the FileWriter!!!!");
				e.printStackTrace();
			}
		}
	}
	
}
