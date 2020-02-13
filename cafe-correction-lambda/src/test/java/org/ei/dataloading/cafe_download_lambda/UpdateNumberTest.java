package org.ei.dataloading.cafe_download_lambda;

import java.util.Calendar;
import java.util.Date;

public class UpdateNumberTest {

	public static void main(String[] args) 
	
	{
		//Test New UpdateNumber
		String str = "loadnum=2020047";
		
		int origUpdateNum = (Integer.parseInt(str.substring(str.indexOf("=")+1,str.trim().length())));
		int tempUpdateNum = (Integer.parseInt(str.substring(str.indexOf("=")+1,str.trim().length() - 1)));
		System.out.println("STR: " + str);
		System.out.println("tempUpdate# " + tempUpdateNum);
		String substr = str.substring(str.length()-1,str.length());
		System.out.println("substr# " + substr);
		int originUpdateNumeDay = Integer.parseInt(str.substring(str.length()-1,str.length()));

		// check day of the week, if it is 7th then update weeknumber, otherwise increment the day
					String newUpdateNum;
					
					if(originUpdateNumeDay <7)
					{
						newUpdateNum = tempUpdateNum + Integer.toString((++originUpdateNumeDay));
					}
					else
					{
						// append 1 to new loadnum
						tempUpdateNum ++;
						newUpdateNum  = tempUpdateNum + "1"; 
					}
		System.out.println("Next UpdateNumber: " + newUpdateNum);

		//get Day of the week
		findDayOfTheWeek();
	}

	
	public static void findDayOfTheWeek()
	{
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		System.out.println("Day of the week: " + dayOfWeek);
		System.out.println("Week Of the Year: " + Calendar.WEEK_OF_YEAR);
		
		System.out.println("Current WeekNumber: ");
		
	}
}
