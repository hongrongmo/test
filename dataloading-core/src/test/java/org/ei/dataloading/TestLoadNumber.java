package org.ei.dataloading;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.threeten.extra.YearWeek;

/**
 * 
 * @author TELEBH
 * @Date: 01/04/2020
 * @Description :Test how to find weeknumber/loadnumber in Java as Aaron asked today, COnclusion found out that 
 * localDate and plus 1 week (weekoffset) as in Python script for our dataloading EC2 works fine to calculate correct weeknumber
 */
public class TestLoadNumber {

	public static void main(String[] args)
	{
		//Calendar cal = new GregorianCalendar();
		//Date date = new Date();
		//cal.setTime(date);
		//System.out.println("WeekNumber of the year: " + cal.get(Calendar.WEEK_OF_YEAR));
		
		//cal.add(Calendar.MONTH, -14);
		//System.out.println("Last 2 weeks loadNumber: " + cal.get(Calendar.DATE) + " , " + cal.get(Calendar.WEEK_OF_YEAR));
		
		// Only my testing to find correct date using Java ISO to match our loadnumber Python script
		LocalDate ld = LocalDate.of(2020, 12, 21);
		System.out.println("IsoWeek of the year: " + ld.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
		
		
		
		Calendar cal = Calendar.getInstance();
		cal.set(2020, 11, 25);
		//cal.setTime(new Date());
		
		// Alerts Code for calculating weeknumber, as reported by Aaron, it is not working correctly when year have 53 weeks
		/* caused email alerts to have "0" results as reported by DGA In last 2 weeks*/
		cal.add(Calendar.DATE, cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY ? 7
				: (7 + Calendar.FRIDAY - cal.get(Calendar.DAY_OF_WEEK)) % 7);
		String weekOfYear = String.format("%d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.WEEK_OF_YEAR));
		
		System.out.println("Week Of year: " + weekOfYear);
		
		ZoneId zoneId = ZoneId.of ( "America/Montreal" );
		ZonedDateTime now = ZonedDateTime.now ( zoneId );
		
		int week = now.get ( IsoFields.WEEK_OF_WEEK_BASED_YEAR );
		int weekYear = now.get ( IsoFields.WEEK_BASED_YEAR );
		
		System.out.println(week + " " + weekYear);
		
		System.out.println("^^^^^^^^^^^^^^^");
		YearWeek yw = YearWeek.of( 2020 , week ) ;
		System.out.println("YearWeek CLass Output: " + yw);
		
		System.out.println("--------------");
		
		// Final Correct WeekNumber Aaron will use to find correct week/loadnumber as we discussed & verified today
		// as a solution for the issue Web tea will re-run alerts for Fri: 12/25/2020 [202053] & 01/01/2021 [202101]
		LocalDate ld2 = LocalDate.of(2021, 12, 31);
		ld2 = ld2.plusWeeks(1);
		String weekOfYear1 = String.format("%d%02d", ld2.get(IsoFields.WEEK_BASED_YEAR), ld2.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
		System.out.println(weekOfYear1);
		//ALerts run for last week in December will be for week/loadNumber: 202201
		
		
		
		
	}
}
