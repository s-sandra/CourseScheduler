import java.util.Scanner;

/**
* This class stores attributes for a class in a schedule.
* @author Sandra Shtabnaya
*/

public class Course implements Comparable<Course>{
	private int startHour;
	private int startMinute;
	private int endHour;
	private int endMinute;
	private int credits;
	private String startTimeOfDay;
	private String endTimeOfDay;
	private String courseName; //stores the course department and number.
	private String courseTitle; //stores the name of the course
	private String location;
	private String conflictingCourse;
	private String day;
	private int differingTimes; //stores the amount of different meeting times.
	private Schedule schedule;


	/**
	 * Creates a new course from a schedule file.
	 * @param file the Scanner reading the .csv file.
	 * @param sch the Schedule the course belongs to.
	 * @throws IllegalFileException if the schedule file has improper formatting.
	 */
	public Course(Scanner file, Schedule sch) throws IllegalFileException{
		schedule = sch;
		String line = file.nextLine();
		differingTimes = countMeetingTimes(line);

		//if the given class has a single meeting time.
		if(differingTimes == 0){
			differingTimes = 1;
		}

		if((line.toUpperCase().contains("ONLINE") || line.toUpperCase().contains("TBA"))
				&& differingTimes != -2){
			file.close();
			throw new IllegalFileException("Check commas.");
		}
		else if(differingTimes < 0 && (!line.toUpperCase().contains("ONLINE")
				&& !line.toUpperCase().contains("TBA"))){
			file.close();
			throw new IllegalFileException("Check commas.");
		}

		parseCourse(file);
	}


	/**
	 * Directly constructs a new course.
	 * @param sHr the starting hour.
	 * @param sMin the starting minute.
	 * @param sTime the starting time of day.
	 * @param eHr the ending hour.
	 * @param eMin the ending minute.
	 * @param eTime the ending time of day.
	 */
	public Course(int sHr, int sMin, String sTime, int eHr, int eMin, String eTime){
		startTimeOfDay = sTime;
		startHour = sHr;
		startMinute = sMin;
		endTimeOfDay = eTime;
		endHour = eHr;
		endMinute = eMin;
	}


	/**
	 * This helper method calculate the amount of differing class times
	 * for a course by counting the number of commas separating dates and
	 * class times.
	 * @param line the line of the csv file containing the course information.
	 * @return the number of differing meeting times.
	 */
	private int countMeetingTimes(String line){
		int classTimesCount = 0;

		for(int i = 0; i < line.length(); i++){
			if(line.charAt(i) == ','){
				classTimesCount++;
			}
		}

		//subtracts the number of commas in a description with exactly one meeting time.
		return classTimesCount - 5;
	}


	/**
	 * This helper method parses a line from the schedule file and
	 * sets the appropriate values for the class.
	 * @param file the Scanner reading the csv file.
	 */
	private void parseCourse(Scanner file){

		file.useDelimiter(",");

		//takes in the first four strings representing the
		//course name, title, location and credits.
		courseName = file.next();
		courseTitle = file.next().trim();
		credits = Integer.valueOf(file.next().trim());

		parseLocations(file);

		//if the class is online, does not look for dates.
		if(location.toUpperCase().equals("ONLINE")){
			courseTitle += " ONLINE";
			return;
		}
		else if(location.toUpperCase().equals("TBA")){
			courseTitle += " TBA";
			return;
		}

		file.useDelimiter(",|-");
		parseTimes(file);

	}

	/**
	 * This helper method reads in the string
	 * containing a class' meeting times.
	 * @param file the Scanner reading the csv file.
	 */
	private void parseTimes(Scanner file){
		String meetingDays;
		String startTime;
		String endTime;

		//searches for more dates and times based on the
		//amount of differing class times.
		for(int i = 0; i < differingTimes; i++) {
			meetingDays = file.next().trim();
			startTime = file.next();
			endTime = file.next();

			//while the meetingDays string has more week days
			while (meetingDays.length() > 0) {

				//gets the first character in dates.
				setDay(chopMeetingDays(meetingDays));

				startHour = parseHour(startTime);
				startMinute = parseMinute(startTime);
				setStartTime(isMorning(startTime));

				endHour = parseHour(endTime);
				endMinute = parseMinute(endTime);
				setEndTime(isMorning(endTime));

				//adds the course to the schedule.
				schedule.addCourse(this);

				//if the meetingDays string still has another
				//week day
				if (meetingDays.length() > 1) {
					meetingDays = meetingDays.substring(1);
				} else {
					meetingDays = "";
				}
			}
		}
	}


	/**
	 * This helper method determines if a given
	 * time of day is in the morning.
	 * @param s AM or PM.
	 */
	private boolean isMorning(String s){
		s = s.toUpperCase();
		String timeOfDay = s;

		if(timeOfDay.contains("P")){
			return false;
		}
		return true;
	}



	/**
	 * This helper method takes in the meeting days of the
	 * course corresponding to a class time. Each character
	 * represents a single day of the week, with Thursday denoted
	 * as "R."
	 */
	private char chopMeetingDays(String dates){
		if(dates.length() > 0){
			return dates.charAt(0);
		}
		return ' ';
	}


	/**
	 * This helper method takes in a string containing
	 * either the class start time or end time. It parses the line
	 * to return the hour in integer form.
	 * @param s the String containing a time.
	 */
	private int parseHour(String s){
		Scanner parseHour = new Scanner(s);
		parseHour.useDelimiter(":");
		int hour = Integer.valueOf(parseHour.next().trim());
		parseHour.close();
		return hour;
	}


	/**
	 * This helper method takes in a string containing
	 * either the class start time or end time. It parses the line
	 * to return the amount of minutes from the beginning of the hour.
	 * @param s the String containing a time.
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
	 * This helper method reads in the string
	 * containing a class' location.
	 * @param file the Scanner reading the csv file.
	 */
	private void parseLocations(Scanner file){
		String hall = file.next();

		location = hall;

		while(file.hasNext()){
			location = hall + " " + file.next();
		}
	}

	
	public void setDay(char date){
		if(date == 'M'){
				day = "Monday";
		}
		else if(date == 'T'){
			day = "Tuesday";
		}
		else if(date == 'W'){
			day = "Wednesday";
		}
		else if(date == 'R'){
			day = "Thursday";
		}
		else if(date == 'F'){
			day = "Friday";
		}
		else if(date == 'S'){
			day = "Saturday";
		}
	}
	
	public void setStartTime(boolean isMorning){
		if(isMorning){
			startTimeOfDay = "AM";
		}
		else{
			startTimeOfDay = "PM";
		}
	}
	
	public void setEndTime(boolean isMorning){
		if(isMorning){
			endTimeOfDay = "AM";
		}
		else{
			endTimeOfDay = "PM";
		}
	}


	/**
	 * Determines whether the passed course conflicts with this course.
	 * @param course the course in question.
	 * @return whether a time conflict exists between the passed course
	 * and this course.
	 */
	public boolean conflictsWith(Course course) {
		boolean conflicting = false;

		//stores the details of the class we are attempting to add
		int addingStartHour = course.getStartHr();
		int addingEndHour = course.getEndHr();
		int addingStartMin = course.getStartMin();
		int addingEndMin = course.getEndMin();
		String addingStartTime = course.getStartTimeOfDay();
		String addingEndTime = course.getEndTimeOfDay();

		//if the current class ends at the same time of day
		//as the adding class starts.
		if (endTimeOfDay.equals(addingStartTime)) {

			//if the current class end hour is the same
			//as the adding class start hour
			if (endHour == addingStartHour) {

				//if the current class ends after or at the same
				//time as the adding class begins
				if (endMinute >= addingStartMin) {
					conflicting = true;
				}
			}

			//if the current class ends after the adding class begins
			else if (endHour != 12 && endHour > addingStartHour) {

				//if the adding class ends before the current class ends
				if (addingEndHour > endHour) {
					conflicting = true;
				}
			}
		}

		//if the current class starts at the same time of
		//day as the adding class
		if (startTimeOfDay.equals(addingEndTime)) {

			//if the current class start hour is the same
			//as the adding class end hour
			if (addingEndHour == startHour) {

				//if the adding class ends after or at the same
				//time as the added class begins
				if (addingEndMin >= startMinute) {
					conflicting = true;
				}
			}

			//if the current class starts before the adding class ends
			else if (startHour != 12 && startHour < addingEndHour) {

				//if the current class ends after the adding
				//class begins
				if (endHour > addingStartHour) {
					conflicting = true;
				}
			}
		}

		//if both classes end at the same time of day
		if (endTimeOfDay.equals(addingEndTime)) {

			//if both classes end at the same hour
			if (endHour == addingEndHour) {

				//if the current class ends after or at the
				//same time as the adding class ends
				if (endMinute >= addingEndMin) {
					conflicting = true;
				}
			}
		}

		//if both classes start at the same time of day
		if (startTimeOfDay.equals(addingStartTime)) {

			//if both classes start at the same hour
			if (startHour == addingStartHour) {

				//if the current class starts before or at
				//the same minute as the adding class
				if (addingStartMin >= startMinute) {
					conflicting = true;
				}
			}
		}

		//accounts for a starting hour of noon.
		if (startHour == 12 && endHour != 12) {

			//if the adding class starts before noon and ends in the middle
			//of the current class
			if (addingStartTime.equals("AM") && addingEndHour < endHour) {
				conflicting = true;
			}
		}

		if (addingStartHour == 12 && addingEndHour != 12) {

			//if the current class starts before noon and ends in the middle
			//of the adding class
			if (startTimeOfDay.equals("AM") && endHour < addingEndHour) {
				conflicting = true;
			}
		}

		//if there is a conflict, stores the name of the
		//class this course conflicts with.
		if (conflicting) {
			setConflict(course.getTitle());
		}

		return conflicting;
	}


	/**
	 * This method is used to store the name of the class it conflicts
	 * with, if there is a time conflict.
	 * @param name the name of the class this course conflicts with.
	 */
	public void setConflict(String name){
		conflictingCourse = name;
	}


	/**
	 * This method determines the length of the class in multiples of five.
	 * @return the amount of five minutes in the class length.
	 */
	public int length(){
		int minuteCount;
		int totalHrs;
		int totalMins;

		//if the interval starts and ends in the same time of day
		if(startTimeOfDay.equals(endTimeOfDay)){

			totalMins = (60 - startMinute) + endMinute;

			//if the class takes 24 hours
			if(startHour == endHour && totalMins == 60){
				totalHrs = 23;
			}

			//if the interval starts and ends at the same hour.
			else if(startHour == endHour){
				totalHrs = -1;
			}

			//if the interval starts at noon, finds the
			//distance between the end hour and noon.
			else if(startHour == 12){

				//subtracts one to account for incomplete
				//hours due to the start and end minutes
				totalHrs = endHour - 1;
			}

			//if the interval ends at noon, finds the
			//distance between the start hour and noon.
			else if(endHour == 12){
				totalHrs = startHour - 1;
			}

			//if the class lasts more than 24 hours
			else if(startHour > endHour){
				totalHrs = 23 + (startHour - endHour);
			}

			else{
				totalHrs = endHour - startHour - 1;
			}
		}

		//if the interval starts and ends in different
		//parts of the day
		else{

			//if the interval ends at noon, finds the
			//distance between the start hour from noon.
			if(endHour == 12){
				totalHrs = 12 - startHour - 1;
			}

			//finds how long the start hour is from noon,
			//and adds the end hour.
			else{
				totalHrs = (12 - startHour) + endHour - 1;
			}
			totalMins = (60 - startMinute) + endMinute;
		}

		minuteCount = totalHrs * 60 + totalMins;
		minuteCount /= 5;

		return minuteCount;
	}


	/**
	 * This method compares two courses based on start time.
	 * @param course the first course to compare this course with.
	 * @return a positive number if this starts later,
	 * a negative number if this starts earlier, and
	 * zero if both courses start at the same time.
	 */
	public int compareTo(Course course){
		int otherStartHr = course.getStartHr();
		int otherStartMin = course.getStartMin();
		String otherStartTimeOfDay = course.getStartTimeOfDay();

		//if both classes start in the same time of day
		if(otherStartTimeOfDay.equals(startTimeOfDay)){

			//if both classes start at the same hour,
			if(startHour == otherStartHr){

				//if both classes start at the same minute,
				if(startMinute == otherStartMin){
					return 0;
				}
				//if this class starts earlier,
				else if(startMinute < otherStartMin){
					return -1;
				}
				return 1;
			}

			//if the current class starts at noon
			if(startHour == 12 || startHour < otherStartHr){
				return -1;
			}
		}

		//if this class starts in the morning, and the other class starts in the afternoon,
		else if(startTimeOfDay.equals("AM") && otherStartTimeOfDay.equals("PM")){
			return -1;
		}

		return 1;
	}


	public int getCredits(){
		return credits;
	}
	
	public String getName(){
		return courseName;
	}
	
	public String getLocation(){
		return location;
	}
	
	public String getTitle(){
		return courseTitle;
	}
	
	public int getStartHr(){
		return startHour;
	}
	
	public int getStartMin(){
		return startMinute;
	}
	
	public int getEndHr(){
		return endHour;
	}
	
	public int getEndMin(){
		return endMinute;
	}
	
	public String getStartTimeOfDay(){
		return startTimeOfDay;
	}
	
	public String getEndTimeOfDay(){
		return endTimeOfDay;
	}
	
	public String getConflict(){
		return conflictingCourse;
	}
	
	public String getDay(){
		return day;
	}
	
	public String toString(){
		return courseName + " - " + courseTitle;
	}
}
