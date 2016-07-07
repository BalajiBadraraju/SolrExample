package solrSearchExample;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class DateRangeSolrQueryUtil {
	
	boolean interval = false;
	private String betweenClause = "";
	private Date firstMondayDate = null;
	private Date lastFridayDate = null;
	private String dateRangeClause = "";
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private ArrayList<String> betweenClauses = new ArrayList<String>();
	
	private void getSelectedDatesBasedOnRange(String startDateString, String endDateString, String dow) throws ParseException {
		
		Date startDate = new SimpleDateFormat("MM/dd/yyyy").parse(startDateString);
		Date endDate = new SimpleDateFormat("MM/dd/yyyy").parse(endDateString);
		
		ArrayList<Date> dateList = new ArrayList<Date>();
		String [] splittedDOW = dow.split(",");
		
		Calendar startDateCalendar = Calendar.getInstance();
		Calendar endDateCalendar = Calendar.getInstance();
		Calendar dayOfWeekCalendar = Calendar.getInstance();
		
		startDateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		startDateCalendar.setTime(startDate);
		
		endDateCalendar.setTime(endDate);
		
		// all days selected
		if(dow.equals("Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday")) { 
			if(dateRangeClause != null && !dateRangeClause.equals("")){
				dateRangeClause = dateRangeClause + " OR DATE:[" +dateFormat.format(startDateCalendar.getTime()) +" TO " + dateFormat.format(endDateCalendar.getTime()) +"]";
			} else {
				dateRangeClause = "DATE:[" +dateFormat.format(startDateCalendar.getTime()) +" TO " + dateFormat.format(endDateCalendar.getTime()) +"]";
			}
			return;
		}
		
		endDateCalendar.add(Calendar.DATE, 1);
	
		while (startDateCalendar.getTime().before(endDateCalendar.getTime())) {
			dateList.add(startDateCalendar.getTime());
			startDateCalendar.add(Calendar.DATE, 1);
		}
		
		//System.out.println(dateList.size());
				
		// if mon-fri selected
		if(dow.equals("Monday,Tuesday,Wednesday,Thursday,Friday")) {
			interval = true;
		}
		
		for(String str : splittedDOW){
			if(str.equals("Monday")){
				filterDateRangeBasedOnDay(dayOfWeekCalendar,dateList, 2);
			} else if (str.equals("Tuesday")){
				filterDateRangeBasedOnDay(dayOfWeekCalendar,dateList, 3);
			} else if (str.equals("Wednesday")){
				filterDateRangeBasedOnDay(dayOfWeekCalendar,dateList, 4);
			} else if (str.equals("Thursday")){
				filterDateRangeBasedOnDay(dayOfWeekCalendar,dateList, 5);
			} else if (str.equals("Friday")){
				filterDateRangeBasedOnDay(dayOfWeekCalendar,dateList, 6);
			} else if (str.equals("Saturday")){
				filterDateRangeBasedOnDay(dayOfWeekCalendar,dateList, 7);
			} else if (str.equals("Sunday")){
				filterDateRangeBasedOnDay(dayOfWeekCalendar,dateList, 1);
			}
		}
		
		addMissedOutDatesInRange(dayOfWeekCalendar,dateList);
		
	}
	
	private void addMissedOutDatesInRange(Calendar dayOfWeekCalendar, ArrayList<Date> dateList) {
		if(interval) {
			for(Date misseddate : dateList){
				dayOfWeekCalendar.setTime(misseddate);
				dayOfWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
				if(lastFridayDate != null && dayOfWeekCalendar.getTime().after(lastFridayDate) && dayOfWeekCalendar.get(Calendar.DAY_OF_WEEK)!=7 && dayOfWeekCalendar.get(Calendar.DAY_OF_WEEK)!=1){
					betweenClause = " OR DATE:\""+ dateFormat.format(dayOfWeekCalendar.getTime()) + "\"";
					//System.out.println(betweenClause);
					dateRangeClause = dateRangeClause + betweenClause;
				}
				
				if(firstMondayDate != null && dayOfWeekCalendar.getTime().before(firstMondayDate) && dayOfWeekCalendar.get(Calendar.DAY_OF_WEEK)!=7 && dayOfWeekCalendar.get(Calendar.DAY_OF_WEEK)!=1){
					betweenClause = " OR DATE:\""+ dateFormat.format(dayOfWeekCalendar.getTime()) + "\"";
					//System.out.println(betweenClause);
					dateRangeClause = dateRangeClause + betweenClause;
				}
			}
		}
		
	}

	private void filterDateRangeBasedOnDay(Calendar dayOfWeekCalendar,
			ArrayList<Date> dateList, int i) {
		
		int index = 0;
	
		for(Date date : dateList){
			dayOfWeekCalendar.setTime(date);
			dayOfWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			if(dayOfWeekCalendar.get(Calendar.DAY_OF_WEEK) == i){
				
				if(!interval){
					//System.out.println(" OR " + dayOfWeekCalendar.getTime());
					if(dateRangeClause.equals("")){
						dateRangeClause = "DATE:\"" + dateFormat.format(dayOfWeekCalendar.getTime())+ "\"";
					} else {
						dateRangeClause = dateRangeClause + " OR DATE:\"" + dateFormat.format(dayOfWeekCalendar.getTime())+ "\"";
					}
				}
				
				
				if(interval && i==2){
					betweenClause =  dateFormat.format(dayOfWeekCalendar.getTime()).toString();
					betweenClauses.add(betweenClause);
					
					if(index ==0){
						firstMondayDate = dayOfWeekCalendar.getTime();
					}
					
				}
				
				if(interval && i==6){
					if(firstMondayDate.before(dayOfWeekCalendar.getTime())){
						betweenClause = "DATE:[" + betweenClauses.get(index).toString() +" TO " + dateFormat.format(dayOfWeekCalendar.getTime()) +"]";
						//System.out.println(betweenClause);
						if(dateRangeClause.equals("")){
							dateRangeClause = betweenClause;
						} else {
							dateRangeClause = dateRangeClause + " OR " + betweenClause;
						}
					} else {
						index --;
					}
					lastFridayDate =  dayOfWeekCalendar.getTime();
				
				}
				
				index++;
				
			}
			
		}

	}


	public String getDateRangeQueryString(String startDate, String endDate,
			String selectedDays) throws ParseException {
		betweenClause = "";
		betweenClauses.clear();
		getSelectedDatesBasedOnRange(startDate, endDate, selectedDays);
		System.out.println("dateRangeClause: " + dateRangeClause);
		return dateRangeClause;
	}
	
	public static String convertMilliSecStringToISODateFormat(String millis){
		long milliSeconds= Long.parseLong(millis);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		//System.out.println("Parsed date: " + dateFormat.format(calendar.getTime()));
		return "\""+ dateFormat.format(calendar.getTime()) + "\"";
	}
	
	public static String convertAfterMilliSecStringToISODateFormat(String millis, String nodeName){
		long milliSeconds= Long.parseLong(millis);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		//System.out.println("Parsed date: " + dateFormat.format(calendar.getTime()));
		return nodeName + ":["+ dateFormat.format(calendar.getTime()) + " TO *]";
	}
	
	public static String convertBeforeMilliSecStringToISODateFormat(String millis, String nodeName){
		long milliSeconds= Long.parseLong(millis);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		//System.out.println("Parsed date: " + dateFormat.format(calendar.getTime()));
		return nodeName + ":[* TO "+ dateFormat.format(calendar.getTime()) + "]";
	}

}
