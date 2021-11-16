package tutorials.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCollections2 {

	public static void main(String[] args) 
	{
		Map<Integer,String> myMap = new HashMap<Integer,String>();
		myMap.put(4, "Test1");
		myMap.put(3, "Banana");
		myMap.put(2, "Apple");
		myMap.put(1, "Grabe");
		
		
		
		String[] arr = new String[10];
		
		List<String> test2 = new LinkedList<String>();
		test2.add("test5");
		test2.add("test3");
		Collections.sort(test2);
		
		System.out.println(test2.toString());
		
		List<Map.Entry<Integer,String>> mylist = new LinkedList<Map.Entry<Integer,String>>(myMap.entrySet());
		Collections.sort(mylist,new Comparator<Map.Entry<Integer, String>>()
				
				{
					public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2)
					{
						return (int)o1.getValue().compareTo(o2.getValue());
					}
				}
				
				);
		
		System.out.println(myMap.toString());
		
		List<Integer> mycost = new ArrayList<Integer>();
		mycost.add(20);
		mycost.add(100);
		mycost.add(0);
		
		int mincost = Collections.min(mycost);
		System.out.println("Mimum cost" + mincost);
		
		
		
		if(Float.toString(25.5f).matches("^\\d+.\\d+"))
			System.out.println("Yes");
		else
			System.out.println("No");
		
		
		String auidcount = "AUID5,";
		String[] splittedAuid = auidcount.split(",");
		System.out.println("Array length for splitted String: " + splittedAuid.length);
		System.out.println(splittedAuid[0]);
		
	}

}
