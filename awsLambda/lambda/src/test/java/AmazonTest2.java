import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * @author: hteleb
 * @date: 06/26/2018
 * @Description: Amazon XOR adjacent 8 cells "i^j" , when 2 adjacent cells 
 * both active/inactive (00,11) next day will be active, last 2 cells are always inactive
 * 
 * (true when 2 are diff, false when are the same)
 */
public class AmazonTest2{

	List<Integer> competeCells(List<Integer> arr, int days)
	{
		List<Integer> result = new ArrayList<Integer>();
		int[] tomorrow = new int[8];
		int listSize = arr.size();
		
		tomorrow[listSize-2] = 0;
		tomorrow[listSize-1] = 0;
		
		
		for(int i=0;i<days;i++)
		{
			for(int j=0; j<arr.size()-2;j++)
			{
				if((arr.get(j)^arr.get(j+1)) ==0)
				{
					tomorrow[j] = 1;
				}
				else
					tomorrow[j] = arr.get(j);
				//System.out.println(arr.get(j)^arr.get(j+1));
			}
		}
		
		for(int i=0;i<tomorrow.length;i++)
		{
			result.add(tomorrow[i]);
		}
		return result;
	}
	public static void main(String[] args) {

		
		int days = 2;
		List<Integer> arr = new ArrayList<Integer>();
		arr.add(1);
		arr.add(0);
		arr.add(0);
		arr.add(1);
		arr.add(1);
		arr.add(0);
		arr.add(1);
		arr.add(0);

		AmazonTest2 obj = new AmazonTest2();
		List result = obj.competeCells(arr,days);
		for(int i=0;i<result.size();i++)
		{
			System.out.println(result.get(i));
		}
		
		List<String> list = new ArrayList<String>();
		list.add("111 abd cd");
		list.add("000 aa bb");
		list.add("000 22 33");
		Collections.reverse(list);
		System.out.println(list);
		
		System.out.println(list.get(0).substring(list.get(0).indexOf(" ")+1,list.get(0).length()));
		
		
	}
 

}
	
