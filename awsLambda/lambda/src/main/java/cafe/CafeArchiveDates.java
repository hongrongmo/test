package cafe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.text.DateFormatter;

/*
 * @Date: 04/03/2018
 * @Author: telebh
 * @Description: reduce cafe downloading files by accumulating archive_dates instead of daiy bases download
 * reason is cafe keep sending very frequent updates for same records i.e. send update for 
 * record 123 today, then another one tomorrow and again in 2 days so we download and process the 
 * record 3 times, if we accumulate these 3 times and download the file only once
 * and so process the record only once. since cafe s3 bucket will be having latest version anyway
 * 
 * NOTE: but need to take into consideration, these cases that Cafe changing PUI/Record inside same Key/EID
 * this case already happened/still happening so if  we accumulate few days that may 
 * cause we will be missing certain PUI????? need to discuss further with NYC team
 * and investigate further cases this happened with Cafe team
 */
public class CafeArchiveDates {

	public static void main(String[] args) 
	{
		String archive_date = args[0];
		ArrayList<String> dates_list= new ArrayList<String>();
				
		//and TO_CHAR(archive_date, 'MON-DD-RR')='" + archive_date + "')";
		
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("MMM-dd-yy");
		String formated_date = format.format(now).toUpperCase();  // make sure to make the month in upper case, otherwise wouldnot work in query
		System.out.println("Current Date: " +  formated_date);
		
		
		try
		{
			
			//Start Date
			Date start_date = format.parse(archive_date);
			Calendar calStart = new GregorianCalendar();
			calStart.setTime(start_date);
			
			
			// END date
			
			final Calendar calEnd = Calendar.getInstance();
			calEnd.add(Calendar.DATE, -1);
			System.out.println("ENd date: " + calEnd.get(Calendar.DATE));
			
			
			/*Date endDate = new Date();
			Calendar calEnd = new GregorianCalendar();
			calEnd.setTime(endDate);*/
			
			
			
			while(calStart.before(calEnd))
			{
				formated_date = format.format(calStart.getTime());
				dates_list.add(formated_date.toUpperCase());
				calStart.add(Calendar.DATE, 1);  //increment cal by one day
			}
			
			System.out.println("Dates from start Date till current day");
			for(int i=0;i<dates_list.size();i++)
			{
				System.out.println(dates_list.get(i));
			}
		}
		catch(ParseException ex)
		{
			System.out.println("Invalid date format, re-try with format MON-DD-YY");
			System.exit(1);
		}
		
		
		

	}

}
