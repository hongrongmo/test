import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.text.WordUtils;


/*
 * @author: ht
 * @Date: 01/29/2018
 * @Description: for confirming the value of previous and next day for Frank to manually run usage.sh script 
 * to get the missing usage log file of 20180126 during the EC2 recreation using TIO recent AMI ami-ced2efb4
 * 
 * Input: 20180126
 * OUtput:
 			PreviousDay: 20180125
			NextDay: 20180127
 */
public class UsageDates {

	public static void main(String[] args) {

		String date="20180126";
		String YYYY = date.substring(0,4);
		int MM = Integer.parseInt(date.substring(4,6));
		MM--;
		String DD = date.substring(6);
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Integer.parseInt(YYYY),
				MM,
				Integer.parseInt(DD));

		String previousDay = getPreviousDay(cal);

		GregorianCalendar cal2 = new GregorianCalendar();
		cal2.set(Integer.parseInt(YYYY),
				 MM,
				Integer.parseInt(DD));

		String nextDay = getNextDay(cal2);
		
		System.out.println("PreviousDay: " + previousDay);
		System.out.println("NextDay: " + nextDay);
		

	}

	public static String getPreviousDay(Calendar cal)
	{
		cal.add(cal.DATE, -1);
		String year = Integer.toString(cal.get(Calendar.YEAR));
		String month = getMonthString(cal.get(Calendar.MONTH));
		String previousDate = getDateString(cal.get(cal.DATE));
		String previousDay = year + month + previousDate;

		return previousDay;
	}

	public static String getNextDay(Calendar cal)
	{
		cal.add(cal.DATE, 1);
		String year = Integer.toString(cal.get(Calendar.YEAR));
		String month = getMonthString(cal.get(Calendar.MONTH));
		String nextDate = getDateString(cal.get(Calendar.DATE));
		String nextDay = year + month + nextDate;

		return nextDay;

	}
	
	public static String getMonthString(int month)
	{
		month++;
		String m = Integer.toString(month);
		if(m.length() == 1)
		{
			m = "0" + m;
		}

		return m;
	}
	
	public static String getDateString(int date)
	{
		String d = Integer.toString(date);
		if(d.length() == 1)
		{
			d = "0" + d;
		}

		return d;
	}

	
	
}
