import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.util.Collections;

/**
 * This class creates a visual representation of a schedule file.
 * @author Sandra Shtabnaya
 */
public class Schedule {
    private ArrayList<ArrayList<Course>> week = new ArrayList<>(); //stores all the classes in a week.
    private ArrayList<Course> conflictingCourses = new ArrayList<>(); //stores all the conflicting classes in the file.
    private ArrayList<Course> invalidCourses = new ArrayList<>(); //stores all classes with invalid descriptions.
    private ArrayList<Course> classes = new ArrayList<>();
    private int credits = 0;


    /**
     * Constructs a new schedule.
     * @param file the .csv file containing the schedule information.
     * @throws FileNotFoundException if the .csv file cannot be found in the files directory.
     * @throws IllegalFileException if the .csv file has improper formatting.
     */
    Schedule(String file) throws FileNotFoundException, IllegalFileException{
        for(int i = 0; i < 7; i++){
            week.add(new ArrayList<>());
        }

        readSchedule(file);
    }


    /**
     * This helper method reads in the .csv file containing the schedule information.
     * @param fileName the name of the schedule file.
     * @throws FileNotFoundException if the .csv file cannot be found in the files directory.
     * @throws IllegalFileException if the .csv file has improper formatting.
     */
    private void readSchedule(String fileName) throws FileNotFoundException, IllegalFileException{
        Scanner file = new Scanner(new File(fileName));

        while(file.hasNextLine()) {
            new Course(file, this);
        }
        file.close();
    }


    /**
     * This method adds a non-conflicting course to the schedule.
     * It also updates the credit count.
     * @param course the course to add.
     */
    void addCourse(Course course){
        for(Course event : classes){
            if(event.getTitle().equals(course.getTitle())){
                return;
            }
        }

        //checks to see if the class times are valid
        if(!hasValidTime(course)){
            return;
        }

        credits += course.getCredits();
        classes.add(course);
        String day = course.getDay();


        if(day.equals("Monday") && !isConflicting(course, 0)){
            week.get(0).add(course);
        }
        else if(day.equals("Tuesday") && !isConflicting(course, 1)){
            week.get(1).add(course);
        }
        else if(day.equals("Wednesday") && !isConflicting(course, 2)){
            week.get(2).add(course);
        }
        else if(day.equals("Thursday") && !isConflicting(course, 3)){
            week.get(3).add(course);
        }
        else if(day.equals("Friday") && !isConflicting(course, 4)){
            week.get(4).add(course);
        }
        else if(day.equals("Saturday") && !isConflicting(course, 5)){
            week.get(5).add(course);
        }

        //adds the course to the list of conflicting classes
        //if it has not been rejected before and removes
        //the class from other weekdays.
        else{
            if(isNewConflict(course)){
                conflictingCourses.add(course);
            }
            removeConflictingCourses(course);
        }
    }


    /**
     * This helper method removes any conflicting courses that were previously
     * added to the schedule.
     */
    private void removeConflictingCourses(Course event){
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


    /**
     * This helper method determines if a given course conflicts with any courses
     * in the given day.
     * @param event the course in question.
     * @param day the day of the week in question.
     * @return whether the given course conflicts with an existing course in the given day.
     */
    private boolean isConflicting(Course event, int day){
        ArrayList<Course> weekDay = week.get(day);

        //determines if the class is not already in our list of
        //conflicting classes
        if(!isNewConflict(event)){
            return true;
        }

        //goes through each class in the weekday
        for(Course lecture: weekDay){
            if(lecture.conflictsWith(event)){
                return true;
            }
        }

        return false;
    }


    /**
     * This helper method determines if the given class times are valid.
     * @param event the course in question.
     * @return if the class doesn't last for more than five hours or
     * doesn't start after 11 PM and before 7 AM.
     */
    private boolean hasValidTime(Course event){
        boolean isValid = true;

        if(event.getLength() >= 60){
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

        if(!isValid && isNewConflict(event)){
            invalidCourses.add(event);
        }

        return isValid;
    }


    /**
     * This helper method determines if a rejected
     * course has already been added to the list of invalid courses.
     * @param event the course in question.
     * @return whether the course has been rejected before.
     */
    private boolean isNewConflict(Course event){

        for(Course lecture : invalidCourses){
            if(lecture.getName().equals(event.getName())){
                return false;
            }
        }
        return true;
    }


    /**
     * This helper method generates a report to append to the
     * end of the schedule. It makes a list of all the added classes
     * and the total number of credits.
     */
    private String getReport(){
        String report = "";

        //provides a summary of the classes added and the total credits.
        System.out.println("----SUMMARY---------------------------");
        for(Course event : classes){
            report += event.toString() + "\n";
        }
        report += "Total Credits: " + credits + "\n";

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

        return report;
    }


    /**
     * Returns the visual representation of the schedule.
     * @return the time line for each day, including the time between classes,
     * locations, meeting times of classes, and schedule summary.
     */
    public String toString(){
        String display = "\n";

        //goes through each day in the week.
        for(int i = 0; i < 6; i++){

            //prints out the day only if it contains classes.
            if(week.get(i).size() > 0){

                ArrayList<Course> courses = week.get(i);
                Collections.sort(courses);
                Course earliest = courses.get(0);
                Course latest = courses.get(courses.size() - 1);

                Header header = new Header(i);
                display += header.makeHeader(earliest, latest);

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
        display += getReport();
        return display;
    }
}
