/*
 * @Author: hteleb
 * @date: 06/17/2019
 * Description: as per Amazon calculates Greatest common Factor/ Highest common factor GCD/HCF. 
 * using the logic i understand , but it is not working it need to flip num with GCD as per "AmazonGCD"
 * so ignore about this way
 */
public class TestAmazonGCD2 
{
	
	public int GCD (int num, int gcd)
	{
		if(gcd == 0)
			return gcd;
		return GCD(gcd%num,num);
	}
	
	public int findGCD(int arr[], int arrSize)
	{
		int gcd = arr[0];
		for(int i=1;i<arrSize;i++)
		{
			i = GCD(arr[i], gcd);
		}
		return gcd;
	}
	
	public static void main(String[] args)
	{
		int num = 5;
		int arr[] = {2,7,9,10,20};
		
		TestAmazonGCD2 obj = new TestAmazonGCD2();
		int gcd = obj.findGCD(arr, num);
		System.out.println("GCD: " + gcd);
	}
}
