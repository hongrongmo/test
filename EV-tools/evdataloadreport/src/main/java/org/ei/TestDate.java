package org.ei;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TestDate {

	public static void main(String[] args) {
		
		int current = 28;
		int target = 25;
		int diff = current - target;
		
		/*Date date = new Date();
	    Calendar c = Calendar.getInstance();
	    c.setTime(date);
	    int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
	    c.add(Calendar.DATE, -i - 6);  
	    Date start = c.getTime();
	    //c.add(Calendar.DATE, 4);   
	    Date end = c.getTime();
	    //System.out.println(start + " - " + end);  // show time as well
	    System.out.println(start.toString().substring(0, 11).trim() + " - " + end.toString().substring(0, 11).trim());
		*/

		Calendar cal = Calendar.getInstance();
		int i = cal.get(Calendar.DAY_OF_WEEK) - cal.getFirstDayOfWeek();
		System.out.println("DIFF " + i);
		
		cal.add(Calendar.DATE, -(diff*7)-1);
		
		Date start = cal.getTime();
		
		cal.add(Calendar.DATE,  4);
		
		Date end = cal.getTime();
		//System.out.println("Start Time: " + start.toString().substring(0, 11).trim());
		//System.out.println("End Time: " + end.toString().substring(0, 11).trim());

		//int week = cal.getActualMaximum(Calendar.WEEK_OF_YEAR);  //52
		Calendar cal2 = new GregorianCalendar();
		Date trial = new Date();
		cal2.setTime(trial);
		
		int yearweek = cal2.get(Calendar.WEEK_OF_YEAR) +1;
		System.out.println("Week of the year (Georgian): " + yearweek);
		
		
		// test week number as calendar 
		
		Calendar cal3 = null;
		
		//int week = 26;   // right one that match calendar
		// # of weeks in year is 53, but ev use 52 weeks only, so need to start two weeks earlier
		/*
		 * i.e loadnumber [201527] that match calendar date Jun 22 - Jun 26, 2015
		 * loadnumber [201526] Jun 15 - Jun 19, 2015
		 * loadnumber [201525] Jun 08 - Jun 12, 2015
		 */
		int week = 27;
		for(int k = 1; k<=52;k++)
		{
			cal3 = Calendar.getInstance();
			//System.out.println("Week of the year : " + cal3.get(Calendar.WEEK_OF_YEAR));
			int j = cal3.get(Calendar.DAY_OF_WEEK) - cal3.getFirstDayOfWeek();
			
			cal3.add(Calendar.DATE, -(week*7)-j+1);
		
	    	Date start3 = cal3.getTime();
	    	cal3.add(Calendar.DATE,  4);
	    	Date end3 = cal3.getTime();
	    	
	    	week--;
	    	
	    	System.out.println(start3.toString().substring(0, 11).trim() + " - " + end3.toString().substring(0, 11).trim());
	    	
	    	//System.out.println(start3.toString().trim() + " - " + end3.toString().trim());
	    	
	    	
		}
		
    	
    	
		
		
	    

	}

}
