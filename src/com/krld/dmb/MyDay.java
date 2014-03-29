package com.krld.dmb;
import java.util.*;

public class MyDay
{

	public Calendar day;

	public static Calendar now;

	public static Calendar startDate;

	public static Calendar endDate;
	MyDay(Calendar c){
		day = (Calendar) c.clone();
	}

	public boolean isCompleted()
	{
		// TODO: Implement this method
		return day.before(now);
	}
}
