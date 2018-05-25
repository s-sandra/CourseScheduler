import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
* This class stores attributes for a class in a schedule.
* @author Sandra Shtabnaya
*/
public class Course implements Comparable<Course>, Cloneable{
	private DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter(); // accepts am/pm time format.
	private LocalTime startTime;
	private LocalTime endTime;
	private int credits;
	private String courseName; //stores the course department and number.
	private String courseTitle; //stores the name of the course
	private ArrayList<String> locations = new ArrayList<>(); //stores the meeting places of the course, if more than one.
	private String location; //stores the meeting place of the course.
	private String conflictingCourse; //the name of the course it conflicts with.
	private String day;
	private int differingTimes; //stores the amount of different meeting times.
	private Schedule schedule; //the schedule the course belongs to.


	/**
	 * Creates a new course from a schedule file.
	 * @param file the Scanner reading the .csv file.
	 * @param sch the Schedule the course belongs to.
	 * @throws IllegalFileFormatException if the schedule file has improper formatting.
	 */
	public Course(Scanner file, Schedule sch) throws IllegalFileFormatException {
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
			throw new IllegalFileFormatException("Check commas.");
		}
		else if(differingTimes < 0 && (!line.toUpperCase().contains("ONLINE")
				&& !line.toUpperCase().contains("TBA"))){
			file.close();
			throw new IllegalFileFormatException("Check commas.");
		}

		parseCourse(line);
	}


	/**
	 * Directly constructs a new course.
	 * @param sTime the time the class starts, in h:mm AM/PM
	 * @param eTime the time the class ends, in h:mm AM/PM
	 */
	public Course(String sTime, String eTime){
		startTime = LocalTime.parse(sTime, dtf);
		endTime = LocalTime.parse(eTime, dtf);
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
	 * @param line the line containing the course information.
	 * @throws IllegalFileFormatException if the file contains invalid information.
	 */
	private void parseCourse(String line) throws IllegalFileFormatException{
		Scanner file = new Scanner(line);
		file.useDelimiter(",");

		//takes in the first four strings representing the
		//course name, title, location and credits.
		courseName = file.next();
		courseTitle = file.next().trim();
		credits = Integer.valueOf(file.next().trim());
		parseLocations(file.next().trim());

		//if the class is online, does not look for dates.
		if(locations.get(0).toUpperCase().equals("ONLINE")){
			courseTitle += " ONLINE";
			day = "ONLINE";
			schedule.addCourse(this);
			return;
		}
		else if(locations.get(0).toUpperCase().equals("TBA")){
			courseTitle += " TBA";
			day = "TBA";
			schedule.addCourse(this);
			return;
		}

		file.useDelimiter(",|-");
		parseTimes(file);

	}

	/**
	 * This helper method reads in the string
	 * containing a class' meeting times.
	 * @param file the Scanner reading the csv file.
	 * @throws IllegalFileFormatException if the file contains invalid information.
	 */
	private void parseTimes(Scanner file) throws IllegalFileFormatException{
		String meetingDays;
		String startTime;
		String endTime;

		//searches for more dates and times based on the
		//amount of differing class times.
		for(int i = 0; i < differingTimes; i++) {
			meetingDays = file.next().trim();
			startTime = file.next().trim().toUpperCase();
			endTime = file.next().trim().toUpperCase();

			//while the meetingDays string has more week days
			while (meetingDays.length() > 0) {

				//gets the first character in dates.
				setDay(chopMeetingDays(meetingDays));
				this.startTime = LocalTime.parse(startTime, dtf);
				this.endTime = LocalTime.parse(endTime, dtf);
				location = locations.get(0);

				//adds the course to the schedule.
				try{
					schedule.addCourse((Course) this.clone());
				}
				catch(CloneNotSupportedException e){}

				//if the meetingDays string still has another
				//week day
				if (meetingDays.length() > 1) {
					meetingDays = meetingDays.substring(1);
				} else {
					meetingDays = "";
				}
			}

			//if the course has more than one meeting place,
			if(locations.size() > 1){
				locations.remove(0);
			}
		}
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
	 * This helper method reads in the string
	 * containing a class' location.
	 * @param line the line containing the location information
	 */
	private void parseLocations(String line){
		Scanner scanLoc = new Scanner(line);
		String hall = scanLoc.next();

		//accounts for online courses, or TBA locations.
		if(scanLoc.hasNext()){
			locations.add(hall + " " + scanLoc.next());
		}
		else{
			locations.add(hall);
		}


		//if the course has more than one meeting place,
		while(scanLoc.hasNext()){
			locations.add(hall + " " + scanLoc.next());
		}
	}

	
	private void setDay(char date) throws IllegalFileFormatException{
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
		else{
			throw new IllegalFileFormatException("For " + courseName + ": " + date + " is not a valid weekday.");
		}
	}


	/**
	 * Determines whether the passed course conflicts with this course.
	 * @param course the course in question.
	 * @return whether a time conflict exists between the passed course
	 * and this course.
	 */
	boolean conflictsWith(Course course) {
		boolean conflicting = false;

		//stores the details of the class we are attempting to add
		LocalTime addingStartTime = course.getStartTime();
		LocalTime addingEndTime = course.getEndTime();
		LocalTime addedStartTime = this.getStartTime();
		LocalTime addedEndTime = this.getEndTime();


		//if adding class begins at the same time as added class ends, then conflicting.
		if(addingStartTime.equals(addedEndTime)) {
			conflicting = true;
		}
		//if adding class begins at the same time as added class begins, then conflicting.
		else if(addingStartTime.equals(addedStartTime)){
			conflicting = true;
		}
		//if adding class begins before added class ends, but after added class begins, then conflicting.
		else if(addingStartTime.isBefore(addedEndTime) && addingStartTime.isAfter(addedStartTime)){
			conflicting = true;
		}
		//if adding class ends at the same time as added class begins, then conflicting.
		else if(addingEndTime.equals(addedStartTime)){
			conflicting = true;
		}
		//if adding class ends before added class ends, but after added class begins.
		else if(addingEndTime.isBefore(addedEndTime) && addingEndTime.isAfter(addedStartTime)){
			conflicting = true;
		}

		//if there is a conflict, stores the name of the
		//this class in the conflicting course.
		if (conflicting) {
			course.setConflict(courseName);
		}

		return conflicting;
	}


	/**
	 * This method is used to store the name of the class the
	 * current class conflicts with, if there is a time conflict.
	 * @param name the name of the class this course conflicts with.
	 */
	private void setConflict(String name){
		conflictingCourse = name;
	}


	/**
	 * This method determines the length of the class in multiples of five.
	 * @return the amount of five minutes in the class length.
	 */
	int length(){
		return Math.toIntExact(startTime.until(endTime, ChronoUnit.MINUTES) / 5);
	}

	/**
	 * This method compares two courses based on start time.
	 * @param course the first course to compare this course with.
	 * @return a positive number if this starts later,
	 * a negative number if this starts earlier, and
	 * zero if both courses start at the same time.
	 */
	public int compareTo(Course course){
		return startTime.compareTo(course.getStartTime());
	}

	int getCredits(){
		return credits;
	}
	
	String getName(){
		return courseName;
	}
	
	String getLocation(){ return location; }
	
	String getTitle(){ return courseTitle; }
	
	LocalTime getStartTime(){ return startTime; }
	
	LocalTime getEndTime(){
		return endTime;
	}
	
	String getConflict(){ return conflictingCourse;}
	
	String getDay(){
		return day;
	}
	
	public String toString(){
		return courseName + " - " + courseTitle;
	}
}
