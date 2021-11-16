package vtw;

import java.io.File;
import java.io.FilenameFilter;

/***
 * 
 * @author TELEBH
 * @Date: 11/02/2016
 * @Description: to separete VTW "WO" Patnet from other US/EP Patents as Hongrong required to split them separatly, i have to 
 * filter downloaded vtw xml files into raw_dir and make WO in separate ZIP file
 */
public class FilterDirFiles {

	public static void main(String[] args) {
		
		String dirName = "C:\\ws\\awsLambda\\lambda\\zips\\filefilter";
		File fileDir = new File (dirName);
		//WO files only
		String[] xmlFiles = fileDir.list(new FilenameFilter() {
			
			//@Override
			public boolean accept(File fileDir, String name) {
				
				return name.toLowerCase().startsWith("wo");
			}
		});
		
		System.out.println("************");
		System.out.println("WO files");
		System.out.println("************");
		
		for(int i=0;i<xmlFiles.length;i++)
		{
			System.out.println(xmlFiles[i]);
		}
		
		//WO & US files only
		 xmlFiles = fileDir.list(new FilenameFilter() {
					
					//@Override
					public boolean accept(File fileDir, String name) {
						
						return (name.toLowerCase().startsWith("wo") || name.toLowerCase().startsWith("us"));
					}
				});
				
				System.out.println("************");
				System.out.println("WO & US files");
				System.out.println("************");
				
				
		for(int i=0;i<xmlFiles.length;i++)
		{
			System.out.println(xmlFiles[i]);
		}
		
		System.out.println("************");
		System.out.println("All files");
		System.out.println("************");
		
		// all files in the dir
		xmlFiles = fileDir.list();

		for(int i=0;i<xmlFiles.length;i++)
		{
			System.out.println(xmlFiles[i]);
		}
	}

}
