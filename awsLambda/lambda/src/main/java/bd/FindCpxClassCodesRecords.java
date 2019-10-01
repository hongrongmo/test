package bd;

/*
 * @author: hteleb
 * @Date: 07/15/2019
 * @Description: sosme CPX classification codes has no text description shows in EV detailed page, by comparing with 
 * hard-coded class codes/descrption in EV APP, it shows some CC in BD_Master not even exist in hard-coded
 * reported to Mattew Mcgarva, and he confirmed some of the CC in CPX abstracts incorrect
 * in order to proceed further i had:
 * 	a. pulled list of all CC in BD_master
 * 	b. pulled list of all CC hard-coded in EV
 * 	c. compared the 2 lists and got the ones in a that not in b as invalid codes as per Matt's confirmation
 * 	d. find all CPX records contains any of these invalid CC
 */
public class FindCpxClassCodesRecords 
{

	public static void main(String[] args)
	{
		if(args.length >0)
		{
			System.out.println("Test");
		}
		// since there is already a class that fetches all classification codes from bd_mastr, simply refrence this class
		
		CpxClassificationCodesBDMaster bd_master_class_codes = new CpxClassificationCodesBDMaster();
		
	}
}
