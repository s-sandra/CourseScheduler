/**
 *
 * @author Sandra Shtabnaya
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class CourseScheduler {
	private static ArrayList<ArrayList<Course>> week; //stores every class for each day of the week.
	private static ArrayList<Course> conflictingCourses; //stores classes which conflict with added classes.
	private static ArrayList<Course> invalidCourses; //stores classes which has invalid meeting times.
	private static String[] weekDays = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
	private static ArrayList<Course> classes; //keeps track of all the classes added. 
	private static int credits;
	private static ArrayList<String> locations; //stores the meeting places of a course
	private static String minutes; //stores the time line for each class
	private static String scale; //stores the time scale for each week day
	private static String header; //stores the header for each week day
	private static final int LONGEST_NAME_LEN = 10; //stores the longest course name for aligning the class time lines. 
	
	public static void main(String args[]){
		
		try{
			readSchedule("files/" + args[0]);
			printSchedule();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(22);
		}
		
	}
	
	/**
	 * makeSchedule - this function is called when making a new schedule. 
	 * It initializes the ArrayLists containing information about the
	 * week and the classes. 
	 */
	public static void makeSchedule(){
		week = new ArrayList<ArrayList<Course>>();
		conflictingCourses = new ArrayList<Course>();
		invalidCourses = new ArrayList<Course>();
		locations = new ArrayList<String>();
		classes = new ArrayList<Course>();
		credits = 0;
		
		for(int i = 0; i < 7; i++){
			week.add(new ArrayList<Course>());
		}
		
	}
	
	/**
	 * readSchedule - this function reads in the given prospective classes
	 * to add to the schedule. 
	 */
	public static void readSchedule(String fileName) throws Exception{
		Scanner scan = new Scanner(new File(fileName));
		
		makeSchedule();
		
		while(scan.hasNextLine()){
			String line = scan.nextLine();
			int totalDifferingClassTimes = countMatches(line, ',') - 5;
			
			//if the given class in the line has a uniform meeting time.
			if(totalDifferingClassTimes == 0){
				parseCourse(line, 1);
			}
			
			if((line.toUpperCase().contains("ONLINE") || line.toUpperCase().contains("TBA"))
					&& totalDifferingClassTimes != -2){
				scan.close();
				throw new Exception("FILE FORMATTING ERROR: Check commas");
			}
			else if(totalDifferingClassTimes < 0 && (!line.toUpperCase().contains("ONLINE") 
					&& !line.toUpperCase().contains("TBA"))){
				scan.close();
				throw new Exception("FILE FORMATTING ERROR: Check commas");
			}
			
			//if the given class in the line has an irregular meeting time. 
			parseCourse(line, totalDifferingClassTimes);
		}
		
		scan.close();
	}
	
	/**
	 * printReport - this function prints out a report at the 
	 * end of the schedule. It makes a list of all the added classes
	 * and the total number of credits.
	 */
	
	public static void printReport(ArrayList<Course> courses){
		for(Course event : courses){
			System.out.println(event);
		}
		
		System.out.println("Total Credits: " + credits);
	}

	/**
	 * printSchedule - this function creates the visual representation 
	 * of the schedule. It prints out the time line for each day, 
	 * including the time between classes, locations, conflicting courses
	 * and total number of credits.
	 */
	public static void printSchedule(){
		System.out.println();
		
		//goes through each day in the week. 
		for(int i = 0; i < weekDays.length; i++){
			
			//prints out the day only if it contains classes. 
			if(week.get(i).size() > 0){
				
				ArrayList<Course> courses = sort(i);
				Course earliest = courses.get(0);
				Course latest = courses.get(courses.size() - 1);
				
				int earliestHr = earliest.getStartHr();
				int range = getScheduleRange(earliest, latest);
				
				//adjusts the table accordingly for uniform appearance.
				shiftTable(range, earliestHr, i);
				System.out.println("----" + weekDays[i] + header);
				System.out.println(scale);
			
				//goes through the sorted list of courses in the week day.
				for(int j = 0; j < courses.size(); j++){
					
					//prints the course name to the screen and adjusts the start of the 
					//time line for uniform appearance.
					Course event = courses.get(j);
					System.out.print(event.getName());
					shiftMinutes(event.getName().length(), range, event, earliest);
					System.out.print(minutes);
					
						
					int sHr = event.getStartHr();
					int sMin = event.getStartMin();
					String sTime = event.getStartTimeOfDay();
						
					int eHr = event.getEndHr();
					int eMin = event.getEndMin();
					String eTime = event.getEndTimeOfDay();
						
					//prints out the start and end times of the class.
					String times = "       ";
					
					//if the starting minute is one digit
					if(sMin < 10){
						times += sHr + ":0" + sMin + " " + sTime + " - ";
					}
					else{
						times += sHr + ":" + sMin + " " + sTime + " - ";;
					}
					
					//if the ending minute is one digit.
					if(eMin < 10){
						times += eHr + ":0" + eMin + " " + eTime;
					}
					else{
						times += eHr + ":" + eMin + " " + eTime;;
					}
					
					System.out.print(times);
						
					if(j != courses.size() - 1){
						Course nextClass = courses.get(j + 1);
						
						int sHrGap = event.getEndHr();
						int sMinGap = event.getEndMin();
						boolean sTimeGap = isMorning(event.getEndTimeOfDay());
						
						int eHrGap = nextClass.getStartHr();
						int eMinGap = nextClass.getStartMin();
						boolean eTimeGap = isMorning(nextClass.getStartTimeOfDay());
						
						//determines the time until next class and prints out the walking times and destinations.
						int walkingTime = getFiveMinuteBreak(eHrGap, eMinGap, eTimeGap, sHrGap, sMinGap, sTimeGap);
						String breakTime = convertToHours(walkingTime);
						System.out.print("       " + breakTime + " to get from " + event.getLocation() + 
								" to " + nextClass.getLocation());
						
					}
					
					System.out.print("\n");
				}
			}
			
			//adds space between week days only if it is not the end of the week. 
			if(i != weekDays.length && !week.get(i).isEmpty()){
				System.out.print("\n\n\n\n");
			}
		}
		
		
		//provides a summary of the classes added and the total credits. 
		System.out.println("----SUMMARY---------------------------");
		printReport(classes);
		
		//provides a summary of invalid classes due to a time conflict. 
		if(conflictingCourses.size() > 0){
			System.out.println("\n----TIME CONFLICTS---------------------");
			for(Course event: conflictingCourses){
				System.out.println(event.getTitle() + " conflicts with " + event.getConflict() + " on " + event.getDay());
			}
		}
		
		if(invalidCourses.size() > 0){
			System.out.println("\n----ERRORS-----------------------------");
			for(Course event: invalidCourses){
				System.out.println(event.getTitle() + " cannot last from " + event.getStartHr() + " " 
									+ event.getStartTimeOfDay() + " to " + event.getEndHr() + " " 
									+ event.getEndTimeOfDay());
			}
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
	 * sort - this function sorts the classes in a given 
	 * day from earliest to latest start time. 
	 */
	private static ArrayList<Course> sort(int day){
		ArrayList<Course> sorted = new ArrayList<Course>();
		ArrayList<Course> daySchedule = week.get(day);
		
		//creates a course with the latest possible start time (10 PM)
		Course earliest = new Course();
		earliest.setStartTime(10, 0, false);
		
		//while all the classes have not been sorted
		while(sorted.size() != daySchedule.size()){
			
			//goes through each class in the day
			for(int i = 0; i < daySchedule.size(); i++){
				Course course = daySchedule.get(i);
				
				//if the class has already been sorted,
				//skip to the next one.
				if(sorted.contains(course)){
					continue;
				}
				
				//if the classes being compared start in the same time of day
				if(course.getStartTimeOfDay().equals(earliest.getStartTimeOfDay())){
					
					//if the current class starts at noon
					if(course.getStartHr() == 12){
						earliest = course;
					}
					
					//if the current class starts earlier than the current earliest class
					else if(earliest.getStartHr() != 12 && course.getStartHr() < earliest.getStartHr()){
						earliest = course;
					}
				}
				
				//if the class is in the morning and the current earliest class is in the afternoon
				if(course.getStartTimeOfDay().equals("AM") && earliest.getStartTimeOfDay().equals("PM")){
					earliest = course;
				}
			}
			sorted.add(earliest);
			
			//resets the value of the earliest class
			earliest = new Course();
			earliest.setStartTime(10, 0, false);
		}
		
		return sorted;
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
	
	/**
	 * getFiveMinuteCount - this function determines the amount of 
	 * time in a given interval (in terms of five minutes). 
	 */
	private static int getFiveMinuteCount(Course event){
		int minuteCount = 0;
		int totalHrs = 0;
		int totalMins = 0;
		
		int startHr = event.getStartHr();
		int startMin = event.getStartMin();
		String startTimeOfDay = event.getStartTimeOfDay();
		
		int endHr = event.getEndHr();
		int endMin = event.getEndMin();
		String endTimeOfDay = event.getEndTimeOfDay();
		
		//if the interval starts and ends in the same time of day
		if(startTimeOfDay.equals(endTimeOfDay)){
			
			totalMins = (60 - startMin) + endMin;
			
			//if the class takes 24 hours
			if(startHr == endHr && totalMins == 60){
				totalHrs = 23;
			}
			
			//if the interval starts and ends at the same hour.
			else if(startHr == endHr){
				totalHrs = -1;
			}
			
			//if the interval starts at noon, finds the
			//distance between the end hour and noon.
			else if(startHr == 12){
				
				//subtracts one to account for incomplete 
				//hours due to the start and end minutes
				totalHrs = endHr - 1;
			}
			
			//if the interval ends at noon, finds the
			//distance between the start hour and noon.
			else if(endHr == 12){
				totalHrs = startHr - 1;
			}
			
			//if the class lasts more than 24 hours
			else if(startHr > endHr){
				totalHrs = 23 + (startHr - endHr);
			}
			
			else{
				totalHrs = endHr - startHr - 1;
			}
		}
		
		//if the interval starts and ends in different
		//parts of the day
		else{
			
			//if the interval ends at noon, finds the
			//distance between the start hour from noon.
			if(endHr == 12){
				totalHrs = 12 - startHr - 1;
			}
			
			//finds how long the start hour is from noon,
			//and adds the end hour. 
			else{
				totalHrs = (12 - startHr) + endHr - 1;
			}
			totalMins = (60 - startMin) + endMin;
		}
		
		minuteCount = totalHrs * 60 + totalMins;
		minuteCount /= 5;
		
		return minuteCount;
	}
	
	/**
	 * countMatches - this functions takes in a line from 
	 * the file and determines how many times a character occurs.
	 * It is used to calculate the amount of differing class times
	 * by counting the number of commas separating dates and class
	 * times.
	 */
	private static int countMatches(String s, char charNeeded){
		int matches = 0;
		
		for(int i = 0; i < s.length(); i++){
			if(s.charAt(i) == charNeeded){
				matches++;
			}
		}
		return matches;
	}
	
	/**
	 * parseCourse - this function is used to parse the 
	 * appropriate values from the file and create course objects
	 * for each class discovered.
	 */
	private static void parseCourse(String line, int differingTimes){
		String courseName;
		String courseTitle;
		int startHr;
		int startMin;
		int endHr;
		int endMin;
		int credits;
		boolean startTimeOfDay;
		boolean endTimeOfDay;
		String location;
		String meetingDays;
		String startTime;
		String endTime;
		char date;
		
		Scanner parseCourse = new Scanner(line);
		parseCourse.useDelimiter(",");
		
		//takes in the first four strings representing the 
		//course name, title, location and credits.
		courseName = parseCourse.next();
		courseTitle = parseCourse.next().trim();
		credits = Integer.valueOf(parseCourse.next().trim());
		
		parseLocations(parseCourse.next());
		location = locations.get(0);
		locations.remove(0);
		
		//if the class is online, does not look for dates,
		//but adds the course to the list courses. 
		if(location.toUpperCase().equals("ONLINE")){
			differingTimes = 0;
			Course course = new Course();
			course.setName(courseName);
			course.setTitle(courseTitle + " ONLINE");
			course.setCredits(credits);
			addCourse(course);
		}
		else if(location.toUpperCase().equals("TBA")){
			differingTimes = 0;
			Course course = new Course();
			course.setName(courseName);
			course.setTitle(courseTitle + " TBA");
			course.setCredits(credits);
			addCourse(course);
		}
		
		parseCourse.useDelimiter(",|-");
		
		//searches for more dates and times based on the 
		//amount of differing class times passed into the 
		//function.
		for(int i = 0; i < differingTimes; i++){
			meetingDays = parseCourse.next().trim();
			startTime = parseCourse.next();
			endTime = parseCourse.next();
			
			//while the meetingDays string has more week days
			while(meetingDays.length() > 0){
			
				//gets the first character in dates.	
				date = chopMeetingDays(meetingDays);
				
				startHr = parseHour(startTime);
				startMin = parseMinute(startTime);
				startTimeOfDay = isMorning(startTime);
				
				endHr = parseHour(endTime);
				endMin = parseMinute(endTime);
				endTimeOfDay = isMorning(endTime);
				
				//creates a new course object and adds it to the week.
				addCourse(courseName, courseTitle, location, date, credits, 
						  startHr, startMin, startTimeOfDay,
						  endHr, endMin, endTimeOfDay);
				
				//if the meetingDays string still has another
				//week day
				if(meetingDays.length() > 1){
					meetingDays = meetingDays.substring(1);
				}
				else{
					meetingDays = "";
				}
			}
			
			//if there are different location for the class,
			//changes the current location. 
			if(locations.size() > 0){
				location = locations.get(0);
				locations.remove(0);
			}
		}
		parseCourse.close();
	}
	
	/**
	 * parseLocations - this function reads in the 
	 * line containing a class' location and adds each
	 * differing place to the ArrayList locations.
	 */
	private static void parseLocations(String s){
		Scanner parseLoc = new Scanner(s);
		String hall = parseLoc.next();
		
		if(hall.toUpperCase().equals("ONLINE") ||
		   hall.toUpperCase().equals("TBA")){
			locations.add(hall);
		}
		
		while(parseLoc.hasNext()){
			locations.add(hall + " " + parseLoc.next());
		}
		
		parseLoc.close();
	}
	
	/**
	 * chopMeetingDays - this function takes in the meeting days of the
	 * course corresponding to a class time. Each character
	 * represents a single day of the week, with Thursday denoted
	 * as "R." 
	 */
	private static char chopMeetingDays(String dates){
		if(dates.length() > 0){
			return dates.charAt(0);
		}
		return ' ';
	}
	
	/**
	 * parseHour - this function takes in a string containing 
	 * either the class start time or end time. It parses the line 
	 * to return the hour in integer form. 
	 */
	private static int parseHour(String s){
		Scanner parseHour = new Scanner(s);
		parseHour.useDelimiter(":");
		int hour = Integer.valueOf(parseHour.next().trim());
		parseHour.close();
		return hour;
	}
	
	/**
	 * parseMinute - this function takes in a string containing 
	 * either the class start time or end time. It parses the line 
	 * to return the amount of minutes from the beginning of the
	 * hour. 
	 */
	private static int parseMinute(String s){
		s = s.toUpperCase();
		Scanner parseMin = new Scanner(s);
		parseMin.useDelimiter(":|AM|PM");
		parseMin.next();
		int minute = Integer.valueOf(parseMin.next().trim());
		parseMin.close();
		return minute;
	}
	
	/**
	 * isMorning - this function determines if a given 
	 * time of day (AM or PM) is in the morning. 
	 */
	private static boolean isMorning(String s){
		s = s.toUpperCase();
		String timeOfDay = s;
		
		if(timeOfDay.contains("P")){
			return false;
		}
		return true;
	}
	
	/**
	 * addCourse - this function adds a non-conflicting
	 * course to the ArrayList classes containing the name of valid
	 * courses. It also updates the credit count. 
	 */
	private static void addCourse(Course course){
		for(Course event : classes){
			if(event.getTitle().equals(course.getTitle())){
				return;
			}
		}
		credits += course.getCredits();
		classes.add(course);
	}
	
	/**
	 * addCourse - this function adds a course to the given weekday in the ArrayList week
	 * if the class does not conflict with already added classes.
	 */
	private static void addCourse(String name, String title, String loc, char day, int credits,
						   int sHr, int sMin, boolean sTime, 
						   int eHr, int eMin, boolean eTime){
		
		Course event = new Course();
		event.setName(name);
		event.setTitle(title);
		event.setLoc(loc);
		event.setStartTime(sHr, sMin, sTime);
		event.setEndTime(eHr, eMin, eTime);
		event.setCredits(credits);
		event.setDay(day);
		
		//checks to see if the class times are valid
		if(!hasValidTime(event)){
			return;
		}
		
		//if the course is on Monday
		if(day == 'M' && !isConflicting(event, 0)){
			week.get(0).add(event);
			addCourse(event);
		}
		//Tuesday
		else if(day == 'T' && !isConflicting(event, 1)){
			week.get(1).add(event);
			addCourse(event);
		}
		//Wednesday
		else if(day == 'W' && !isConflicting(event, 2)){
			week.get(2).add(event);
			addCourse(event);
		}
		//Thursday
		else if(day == 'R' && !isConflicting(event, 3)){
			week.get(3).add(event);
			addCourse(event);
		}
		//Friday
		else if(day == 'F' && !isConflicting(event, 4)){
			week.get(4).add(event);
			addCourse(event);
		}
		//Saturday
		else if(day == 'S' && !isConflicting(event, 5)){
			week.get(5).add(event);
			addCourse(event);
		}
		
		//adds the course to the list of conflicting classes
		//if it has not been rejected before and removes
		//the class from other weekdays.
		else{
			if(isNewConflict(event, conflictingCourses)){
				conflictingCourses.add(event);
			}
			removeConflictingCourses(event);
		}
	}
	
	/**
	 * hasValidTime - this function determines if the 
	 * given class times are valid, meaning that the class
	 * doesn't last for more than five hours or doesn't start
	 * after 11 PM and before 7 AM. 
	 */
	private static boolean hasValidTime(Course event){
		boolean isValid = true;
		
		if(getFiveMinuteCount(event) >= 60){
			isValid = false;
		}
		else if(event.getStartTimeOfDay().equals("PM") && event.getStartHr() < 12
				&& event.getStartHr() > 10){
			isValid = false;
		}
		else if(event.getStartTimeOfDay().equals("AM") && (event.getStartHr() == 12 ||
				event.getStartHr() < 7)){
			isValid = false;
		}
		
		if(!isValid && isNewConflict(event, invalidCourses)){
			invalidCourses.add(event);
		}
		
		return isValid;
	}
	
	/**
	 * isNewConflict - this function determines if a rejected
	 * course is not currently in the given ArrayList.
	 */
	private static boolean isNewConflict(Course event, ArrayList<Course> courses){
		
		for(Course lecture : courses){
			if(lecture.getName().equals(event.getName())){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * isConflicting - this function determines if a given
	 * course conflicts with any courses in the given day.
	 */
	private static boolean isConflicting(Course event, int day){
		ArrayList<Course> weekDay = week.get(day);
		boolean conflicting = false;
		
		//determines if the class is not 
		//already in our list of conflicting 
		//classes
		if(!isNewConflict(event, conflictingCourses)){
			return true;
		}
		
		//goes through each class in the weekday
		for(Course lecture: weekDay){
			
			//stores the details of an already added class
			int addedStartHour = lecture.getStartHr();
			int addedEndHour = lecture.getEndHr();
			int addedStartMin = lecture.getStartMin();
			int addedEndMin = lecture.getEndMin();
			String addedStartTime = lecture.getStartTimeOfDay();
			String addedEndTime = lecture.getEndTimeOfDay();
			
			//stores the details of the class we are attempting to add
			int addingStartHour = event.getStartHr();
			int addingEndHour = event.getEndHr();
			int addingStartMin = event.getStartMin();
			int addingEndMin = event.getEndMin();
			String addingStartTime = event.getStartTimeOfDay();
			String addingEndTime = event.getEndTimeOfDay();
			
			//if the current class ends at the same time of day
			//as the adding class starts.
			if(addedEndTime.equals(addingStartTime)){
				
				//if the current class end hour is the same
				//as the adding class start hour
				if(addedEndHour == addingStartHour){
					
					//if the current class ends after or at the same
					//time as the adding class begins
					if(addedEndMin >= addingStartMin){
						conflicting = true;
					}
				}
				
				//if the current class ends after the adding class begins
				else if(addedEndHour != 12 && addedEndHour > addingStartHour){
					
					//if the adding class ends before the current class ends
					if(addingEndHour > addedEndHour){
						conflicting = true;
					}
				}
			}
			
			//if the current class starts at the same time of 
			//day as the adding class
			if(addedStartTime.equals(addingEndTime)){
				
				//if the current class start hour is the same
				//as the adding class end hour
				if(addingEndHour == addedStartHour){
					
					//if the adding class ends after or at the same
					//time as the added class begins
					if(addingEndMin >= addedStartMin){
						conflicting = true;
					}
				}
				
				//if the current class starts before the adding class ends
				else if(addedStartHour != 12 && addedStartHour < addingEndHour){
					
					//if the current class ends after the adding
					//class begins
					if(addedEndHour > addingStartHour){
						conflicting = true;
					}
				}
			}
			
			//if both classes end at the same time of day
			if(addedEndTime.equals(addingEndTime)){
				
				//if both classes end at the same hour
				if(addedEndHour == addingEndHour){
					
					//if the current class ends after or at the 
					//same time as the adding class ends
					if(addedEndMin >= addingEndMin){
						conflicting = true;
					}
				}
			}
			
			//if both classes start at the same time of day
			if(addedStartTime.equals(addingStartTime)){
				
				//if both classes start at the same hour
				if(addedStartHour == addingStartHour){
					
					//if the current class starts before or at 
					//the same minute as the adding class
					if(addingStartMin >= addedStartMin){
						conflicting = true;
					}
				}
			}
			
			//accounts for a starting hour of noon. 
			if(addedStartHour == 12 && addedEndHour != 12){
				
				//if the adding class starts before noon and ends in the middle
				//of the current class
				if(addingStartTime.equals("AM") && addingEndHour < addedEndHour){
					conflicting = true;
				}
			}
			
			if(addingStartHour == 12 && addingEndHour != 12){
				
				//if the current class starts before noon and ends in the middle
				//of the adding class
				if(addedStartTime.equals("AM") && addedEndHour < addingEndHour){
					conflicting = true;
				}
			}
			
			//if there is a conflict, stores the name of the
			//class the course conflicts with
			if(conflicting){
				event.setConflict(lecture.getTitle());
				return conflicting;
			}
		}
		
		return conflicting;
	}
	
	/**
	 * removeConflictingCourses - this function removes any
	 * conflicting courses that were previously added to the 
	 * week.  
	 */
	private static void removeConflictingCourses(Course event){
		for(int i = 0; i < classes.size(); i++){
			int daySize = classes.size();
			
			for(int j = 0; j < daySize; j++){
				if(classes.get(j).getName().equals(event.getName())){
					classes.remove(j);
					daySize--;
				}
			}
		}
	}
}
