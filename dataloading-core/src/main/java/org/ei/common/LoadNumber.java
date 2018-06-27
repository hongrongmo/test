package org.ei.common;

import java.util.Calendar;
import java.util.GregorianCalendar;


public abstract class LoadNumber
{

	public abstract int increment();

	public abstract int decrement();

	public static int getCurrentWeekNumber()
	{
	   String weeknum;
	   Calendar calendar = new GregorianCalendar();
	   java.util.Date time = new java.util.Date();
	   calendar.setTime(time);
	   weeknum=Integer.toString(calendar.get(Calendar.YEAR)) + Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR));
	   return Integer.parseInt(weeknum);
   }

}
