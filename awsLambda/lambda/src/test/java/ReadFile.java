import java.io.File;
import java.util.Scanner;


public class ReadFile {

	public static void main(String[] args) {
		
		Scanner scan = null;
		try
		{
			String currDir = System.getProperty("user.dir");
			System.out.println("current dir: " + currDir);
			//String fileName= currDir + "/es/" + "apr_701600_1497539060899";
			String fileName= "C://NYC_dataload_split//EngineeringVillage//dataloading-core//es/" + "apr_701600_1497560470030";
			
			scan = new Scanner(new File(fileName));
			scan.useDelimiter("\\Z");
			
			String fileContent = scan.next();
			System.out.println("File Content: " + fileContent);
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
				System.out.println("Failed to close the scanner!!!!!");
				e.printStackTrace();
			}
		}

	}

}
