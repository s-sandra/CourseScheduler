/**
 *
 * @author Sandra Shtabnaya
 */

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class CourseScheduler {
	private static String[] weekDays = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
	private static ArrayList<Course> classes; //keeps track of all the classes added.
	private static ArrayList<String> locations; //stores the meeting places of a course
	private static String minutes; //stores the time line for each class
	private static String scale; //stores the time scale for each week day
	private static String header; //stores the header for each week day
	private static final int LONGEST_NAME_LEN = 10; //stores the longest course name for aligning the class time lines. 
	
	public static void main(String args[]){
		
		try{
			Schedule schedule = new Schedule("files/" + args[0]);
			System.out.println(schedule);
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
			System.exit(22);
		}
		catch(IllegalFileException e){
			e.getMessage();
			System.exit(22);
		}
		
	}

	
	/**
	 *convertToHours - this function is called once the time between
	 *classes has been calculated. It converts that time from minutes
	 *to hours. 
	 */
	private static String convertToHours(int walkingTime){
		String time = "";
		int hours = 0;
		
		while(walkingTime >= 60){
			hours++;
			walkingTime -= 60;
		}
		
		if(hours > 0){
			if(hours > 1){
				time += hours + " hrs ";
			}
			else{
				time += hours + " hr ";
			}
		}
		
		if(walkingTime > 0){
			time += String.valueOf(walkingTime) + " mins";
		}
		
		return time;
		
	}
	
	/**
	 * shiftMinutes - this function adjusts the start of the time line according to the 
	 * longest class name. This allows all the tables to start at the same place. 
	 */
	private static void shiftMinutes(int currentName, int range, Course added, Course earliest){
		minutes = "";
		
		//adds white space between the class name and the start of its time line. 
		for(int i = currentName; i < LONGEST_NAME_LEN + 4; i++){
			minutes += " ";
		}
		
		//concatenates the time line to the variable minutes. 
		ArrayList<String> line = makeLine(added, earliest, range);
		for(int i = 0; i < line.size(); i++){
			minutes += line.get(i);
		}
	}
	
	/**
	 * getScheduleRange - this function determines the length of the day
	 * in five minute intervals. It will be used to adjust the scale of the 
	 * time line to remove unneeded hours. 
	 */
	private static int getScheduleRange(Course earliest, Course latest){
		int range = 0;
		int latestHr = latest.getEndHr();
		int latestMin = latest.getEndMin();
		int earliestHr = earliest.getStartHr();
		
		//if the earliest class starts in the morning and the latest class starts in the afternoon. 
		if(earliest.getStartTimeOfDay().equals("AM") && latest.getEndTimeOfDay().equals("PM")){
			
			//calculates how many minutes the earliest class starts from noon.
			range += (12 - earliestHr) * 60;
			
			//if the latest class starts at noon.
			if(latestHr == 12){
				
				//calculates how many minutes the latest class starts from noon.
				range += (12 - latestHr) * 60 + latestMin;
			}
			else{
				
				//if the latest class is not at noon, converts its 
				//start time to minutes.
				range += latestHr * 60 + latestMin;
			}
		}
		else{
			
			if(earliestHr == 12 && latestHr != 12){
				range = latestHr * 60 + latestMin;
			}
			
			//if the earliest and latest classes start at the same
			//part of the day, finds their difference, and converts to minutes.
			else{
				range = (latestHr - earliestHr) * 60 + latestMin;
			}
		}
		
		range /= 5;
		
		return range;
	}
	
	/**
	 * shiftHeaders - this function adjusts the length of the schedule header
	 * according to the length of the longest class name and the 
	 * range of the time line.
	 */
	private static void shiftHeaders(int range, int day){
		header = "";
		
		//increases the length of the header to span the gap 
		//between the course name and time line. 
		for(int i = 0; i < LONGEST_NAME_LEN + 4; i++){
			header += "-";
		}
		
		//increases the length of the header until it reaches the 
		//end of the time line, accounting for the extra lines
		//before the header title, and the length of the title. 
		for(int i = weekDays[day].length() + 4; i < range; i++){
			header += "-";
		}
		
		header += "---------MEETING TIMES--------";
		
		//if there is more than one class in the week day, adds an 
		//additional section to the header for the walking times 
		//between classes.
		if(week.get(day).size() > 1){
			header += "---------------WALKING TIMES-----------";
		}
	}
	
	/**
	 * shiftScale - this function adjusts the time line scale based on the 
	 * start of the first class and the end of the last class in the 
	 * week day. 
	 */
	private static void shiftScale(int range, int earliestHr){
		scale = "";
		
		//adds whitespace between the course name and the start 
		//of the time line.
		for(int i = 0; i < LONGEST_NAME_LEN + 4; i++){
			scale += " ";
		}
		
		int hour = earliestHr;
		int prevHour = earliestHr;
		
		//determines how many hours are in the time range. 
		range = (range / 12) + 1;
		
		//adds hours and whitespace to the scale. 
		for(int i = 1; i <= range; i++){
			
			//if the hour is past noon
			if(hour == 13){
				hour = 1;
			}
			scale += hour;
			
			//if the previous hour was two digits and 
			//the current hour is one digit, adds less
			//whitespace to keep the scale aligned with 
			//the time line.
			if(prevHour > 9 && hour < 10){
				scale += "           ";
			}
			
			//if the previous hour was one digit, and
			//the current hour is two digits
			else if(prevHour < 10 && hour > 9){
				scale += "          ";
			}
			
			//if both the previous and current hour
			//are two digits
			else if(prevHour > 9 && hour > 9){
				scale += "          ";
			}
			
			//if both the previous and current hour
			//are one digit
			else{
				scale += "           ";
			}
			prevHour = hour;
			hour++;
		}
	}
	
	/**
	 * shiftTable - this function adjusts the table based on the time range 
	 * spent in class and the longest class name. It shifts the headers and 
	 * the scale.
	 */
	private static void shiftTable(int range, int earliestHr, int day){
		shiftHeaders(range, day);
		shiftScale(range, earliestHr);
	}

	/**
	 * makeLine - this function constructs the time line for each class in the day. 
	 * Each "." represents 5 minutes, "[" the start time, "=" each five minutes in 
	 * class and "]" the end time. 
	 */
	private static ArrayList<String> makeLine(Course added, Course earliest, int range){
		ArrayList<String> line = new ArrayList<String>();
		int startMin = getFiveMinuteStart(added, earliest);
		int endMin = startMin + getFiveMinuteCount(added);
		
		for(int i = 0; i <= range; i++){
			
			//if the current 5 minute count 
			//is the start time
			if(i == startMin){
				line.add("[");
			}
			else if(i == endMin){
				line.add("]");
			}
			else if(i < startMin || i > endMin){
				line.add(".");
			}
			else{
				line.add("=");
			}
		}
		
		return line;
	}
	
	/**
	 * getFiveMinuteStart - this function converts the start time of a 
	 * class into the number of five minutes (represented as ".") it is 
	 * from the beginning of the schedule time line. 
	 */
	private static int getFiveMinuteStart(Course added, Course earliest){
		int start = 0;
		int addedStartHr = added.getStartHr();
		int addedStartMin = added.getStartMin();
		String addedStartTime = added.getStartTimeOfDay();
		int earliestStartHr = earliest.getStartHr();
		String earliestStartTime = earliest.getStartTimeOfDay();
		
		//if the current and earliest classes begin in the 
		//same time of day
		if(earliestStartTime.equals(addedStartTime)){
			
			if(earliestStartHr == addedStartHr){
				start = 0;
			}
			
			//if the earliest class begins at noon, convert the 
			//current class start time to five minutes
			else if(earliestStartHr == 12){
				start = addedStartHr * 12;
			}
			
			//find the difference between the earliest and 
			//current class times and convert to five minutes. 
			else{
				start = (addedStartHr - earliestStartHr) * 12;
			}
		}
		
		//if the current and earliest classes begin at different 
		//times of the day, find how many hours left until noon 
		//for the earliest class start hour and add it to the start 
		//hour of the latest class. 
		else{
			
			//if the added class starts at noon
			if(addedStartHr == 12){
				start = (12 - earliestStartHr) * 12;
			}
			else{
				start = ((12 - earliestStartHr) + addedStartHr) * 12;
			}
		}
		
		//adds the start minutes to the count
		start += (addedStartMin / 5);
		
		return start;
	}
	
	/**
	 * getFiveMinuteBreak - this function determines the amount of 
	 * time (in terms of five minutes) between two classes. 
	 */
	private static int getFiveMinuteBreak(int eHr, int eMin, boolean eTime, 
										  int sHr, int sMin, boolean sTime){
		Course course = new Course();
		course.setStartTime(sHr, sMin, sTime);
		course.setEndTime(eHr, eMin, eTime);
		return getFiveMinuteCount(course) * 5;
	}

}
