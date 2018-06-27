package jar;
import java.io.File;

public class RenameFile {

	public static void main(String[] args) {
	      
	      File f = null;
	      File f1 = null;
	      boolean bool = false;
	      
	      try{      
	         // create new File objects
	    	  String dir = System.getProperty("user.dir");
	         f = new File(dir + "/test.txt");
	         f1 = new File(dir + "/testABC.txt");
	         
	         // rename file
	         bool = f.renameTo(f1);
	         
	         // print
	         System.out.print("File renamed? "+bool + " FileName: " +  f.getName());
	         
	      }catch(Exception e){
	         // if any error occurs
	         e.printStackTrace();
	      }
	   }
}

