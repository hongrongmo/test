
/*
 * @author: hteleb
 * @Date: 06/26/2018
 * @Description: Amazon Test find Greatest common Divider for array of "5" numbers
 * for array of 5 numbers
 */
public class AmazonGCD 
{
	public int GCD(int a, int b)
	{			
		if(a == 0)
			return b;
		return GCD(b%a,a);
	}
		
	public int findGCD(int []arr, int num)
	{
		int gcd = arr[0];
		for(int i=1;i<num;i++)
		{
			gcd = GCD(arr[i],gcd);
		}
		return gcd;
	}

public static void main(String[] args)
{
	int num=2;
	//int[] arr = {2,4,6,8,20};		//2
	//int[] arr = {2,7,9,10,20};      //2
	int [] arr = {24,54};

	AmazonGCD obj = new AmazonGCD();
	int gcd = obj.findGCD(arr,num);
	System.out.println(gcd);
}
}
