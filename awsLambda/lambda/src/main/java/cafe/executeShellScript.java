package cafe;

import java.io.IOException;

/***
 * 
 * @author TELEBH
 * @Date: 11/22/2016
 *  @Description: Execute Fast upload script from java for Uploading CAfE ANI deletion file to fast
 */
public class executeShellScript {

	public static void main(String[] args) {
		
		String zipFile = "/data/fast/ept/fast/batch_201641_0001/EIDATA/1479842048146_ept_add_201641-0001.zip";
		try 
		{
			Runtime.getRuntime().exec("./sendfastunits.sh " + zipFile);
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}

}
