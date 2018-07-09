import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 * 
 * @author TELEBH
 * @Date: 06/23/2018
 *  @Description: general class for testing diff java functions
 */
public class JavaDiffTests {

	public static void main(String[] args) 
	{
		ArrayList<String> stopWords = new ArrayList<String>();
		stopWords.add("The");
		stopWords.add("At");
		stopWords.add("And");
		stopWords.add("Or");
		
		// map to lowercase
		stopWords = (ArrayList<String>) stopWords.stream().map(String::toLowerCase)
				.collect(Collectors.toList());
		
		System.out.println(stopWords.toString());

	}

}
