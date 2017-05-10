import java.util.Scanner;

/**
*
* @author Sandra Shtabnaya
*/

public class Course {
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

	/**
	 * Creates a new course from a schedule file.
	 * @param file the Scanner reading the .csv file.
	 * @throws IllegalFileException if the schedule file has improper formatting.
	 */
	public Course(Scanner file) throws IllegalFileException{
		String line = file.nextLine();
		int totalDifferingClassTimes = countMeetingTimes(line, ',') - 5;

		//if the given class in the line has a single meeting time.
		if(totalDifferingClassTimes == 0){
			parseCourse(line, 1);
		}

		if((line.toUpperCase().contains("ONLINE") || line.toUpperCase().contains("TBA"))
				&& totalDifferingClassTimes != -2){
			file.close();
			throw new IllegalFileException("Check commas.");
		}
		else if(totalDifferingClassTimes < 0 && (!line.toUpperCase().contains("ONLINE")
				&& !line.toUpperCase().contains("TBA"))){
			file.close();
			throw new IllegalFileException("Check commas.");
		}

		//if the given class in the line has several meeting times.
		parseCourse(line, totalDifferingClassTimes);
	}

	public void setCredits(int cred){
		credits = cred;
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
	
	public void setStartTime(int hour, int min, boolean isMorning){
		startHour = hour;
		startMinute = min;
		
		if(isMorning){
			startTimeOfDay = "AM";
		}
		else{
			startTimeOfDay = "PM";
		}
	}
	
	public void setEndTime(int hour, int min, boolean isMorning){
		endHour = hour;
		endMinute = min;
		
		if(isMorning){
			endTimeOfDay = "AM";
		}
		else{
			endTimeOfDay = "PM";
		}
	}
	
	public void setName(String name){
		courseName = name;
	}
	
	public void setTitle(String title){
		courseTitle = title;
	}
	
	public void setLoc(String place){
		location = place;
	}
	
	/**
	 * getConflict - this method is used to 
	 * store the name of the class it conflicts
	 * with, if there is a time conflict.
	 */
	public void setConflict(String name){
		conflictingCourse = name;
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
