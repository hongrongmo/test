
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * @author: HT
 * @Date: 03/17/2018
 * @Description: Amazon Job Assessment. find the most occurence words in a text excluding common words (i.e. the, an,...) and replace punctuatuins with space
 * and ignore case sensitivity
 * 
 * OUTPUT: most common words and thier count is: cheese, 2
	most common words and thier count is: s, 2

YES, this was exactly same output as was in Amazon Assessment ["cheese", "s"]
 */
public class CommonWords {

	String literatureText = "Hello World";
	      
	 // METHOD SIGNATURE BEGINS, THIS METHOD IS REQUIRED
    List<String> retrieveMostFrequentlyUsedWords(String literatureText, 
                                                 List<String> wordsToExclude)
    {
        List<String> frequentWords = new ArrayList<String>();
        
        //replace punctutuation with space
        
        literatureText = literatureText.replaceAll("\\p{Punct}+"," ");		// replaces allpunctuations
        
        String literatureTextWords[] = literatureText.split("\\s+");
        
        // convert exlude list to lowercase
        wordsToExclude = wordsToExclude.stream(). // Convert collection to Stream
        		map(String::toLowerCase).// Convert each element to upper case
        		collect(Collectors.toList()); // Collect results to a new list

        for(int i=0;i<literatureTextWords.length;i++)
        {
            if(!(wordsToExclude.contains(literatureTextWords[i].toLowerCase())))
            {
            	frequentWords.add(literatureTextWords[i].toLowerCase());
            }
            
        }
        return frequentWords;
    }
    
    public static void main(String[] args)
    {
    	int max;
    	
        List<String> commonWords = new ArrayList<String>();
        String literatureText ="Jack and Jill went to the market to buy bread and cheese. Cheese is Jack's and Jill's favorite food";
        
        Hashtable<String, Integer> numbers
        = new Hashtable<String, Integer>();
        
        commonWords.add("and");
        commonWords.add("he");
        commonWords.add("the");
        commonWords.add("to");
        commonWords.add("is");
        commonWords.add("Jack");
        commonWords.add("Jill");
        
        List<String> CommonText = new CommonWords().retrieveMostFrequentlyUsedWords(literatureText,commonWords);
       
        
        for(int i=0;i<CommonText.size();i++)
        {
        	if(numbers.containsKey(CommonText.get(i)))
               {
        		numbers.put(CommonText.get(i), numbers.get(CommonText.get(i)) +1);
        		
               }
        	else
        	{
        		numbers.put(CommonText.get(i), 1);
        	}
        }
        
        
        // find most occurence word in hashtable
        
        max = Collections.max(numbers.values());
        for(String key: numbers.keySet())	
        {
        	if(numbers.get(key) == max)
        	{
        		 System.out.println("most common words and thier count is: " + key + ", " + max);
        	}
        }
       
        
    }
}
