package jar;

/**
 * @author TELEBH
 * @Date: 01/03/2017
 * @Description: as per Frank request need to compare Title for Cafe & BD that match based on AN. as eventhough records match
 * on AN, but their title still different, so we need to find that difference
 * 
 * this class compare strings using Google "Diff, Match and Patch" API 
 *  
 */
import jar.diff_match_patch.Diff;

import java.util.LinkedList;


/**
 * 
 * @author TELEBH
 * @Date: 12/30/2016
 * @Description: Compare two strings to get the difference patterns/characters between
 *  using apach commons "StringUtils" class
 *  "difference" method More precisely, return the remainder of the second String, 
 *  starting from where it's different from the first.)
 */
import org.apache.commons.lang3.*;


public class StringComparison {

	public static final int INDEX_NOT_FOUND = -1;
	
	
	  
	  
	public static void main(String[] args) {
		

		/*String str1 = "0LAQUOLIVINGRAQUOCHAINRADICALPOLYMERISATIONYENG";
		String str2 = "0<<LIVING>>CHAINRADICALPOLYMERISATIONYRUSSIANG";*/
		
		// if str2 is < str1, it substring str2 till str2.length, which is empty
		// if str2 is > str1, it substring str2 till str1.length, which will contains the  difference
		
		String str1 = "Decrease of emissions required to stabilize atmospheric CO2 due to positive carbon cycle-climate feedbacks";
		String str2 = "Decrease of emissions required to stabilize atmospheric CO<inf>2</inf> due to positive carbon cycle-climate feedbacks";
		
		String result = StringUtils.difference(str1, str2);
		System.out.println("difference: " + result);
		
		// get indexOfDifference
		
		int indexOfDiff = StringUtils.indexOfDifference(new String[] {str1,str2});
		System.out.println("Index of difference: " + indexOfDiff);
		
		
		//common prefix
		diff_match_patch dmp = new diff_match_patch();
		int count = dmp.diff_commonPrefix("abc", "xyz");
		System.out.println(count);
		
		// diff prefix
		count = dmp.diff_commonPrefix("1234abcdef", "1234xyz");
		System.out.println(count);
		
		
		 diff_match_patch.Operation DELETE = diff_match_patch.Operation.DELETE;
		 diff_match_patch.Operation EQUAL = diff_match_patch.Operation.EQUAL;
		 diff_match_patch.Operation INSERT = diff_match_patch.Operation.INSERT;
		/*  
		LinkedList<Diff> diffs = diffList(new Diff(EQUAL, "a\n"), new Diff(DELETE, "<B>b</B>"), new Diff(INSERT, "c&d"));
	   String str = dmp.diff_prettyHtml(diffs);
	   System.out.println(str);
	 
	   
	   count = dmp.match_main("abcdef", "abcdef", 1000);
	   System.out.println(count);*/
		 
	/**
	 * compare two strings and get the difference
	 */
	   
	   /*String a = "Decrease of emissions required to stabilize atmospheric CO2 due to positive carbon cycle-climate feedbacks";
	   String b = "Decrease of emissions required to stabilize atmospheric CO<inf>2</inf> due to positive carbon cycle-climate feedbacks";*/
	    
	   String a = "&laquo;Living&raquo; chain radical polymerisationyeng";
	   String b = "<<Living>> chain radical polymerisationyRussian";
	    
	   
	   LinkedList<Diff> DiffList = dmp.diff_main(a, b, false);
	   for(int i=0;i<DiffList.size();i++)
	   {
		   System.out.println("diff #" + i + " : " + DiffList.get(i));
	   }
	   
		
	}
	
	 // Private function for quickly building lists of diffs.
	  private static LinkedList<Diff> diffList(Diff... diffs) {
	    LinkedList<Diff> myDiffList = new LinkedList<Diff>();
	    for (Diff myDiff : diffs) {
	      myDiffList.add(myDiff);
	    }
	    return myDiffList;
	  }
	  

}

