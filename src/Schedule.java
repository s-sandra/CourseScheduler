import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.io.File;
import java.util.Collections;

/**
 * This class stores attributes of a weekly schedule.
 * @author Sandra Shtabnaya
 */
public class Schedule {
    DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter(); // accepts am/pm time format.
    private ArrayList<ArrayList<Course>> week = new ArrayList<>(); //stores all the classes in a week.
    private ArrayList<Course> conflictingCourses = new ArrayList<>(); //stores all the conflicting classes in the file.
    private ArrayList<Course> invalidCourses = new ArrayList<>(); //stores all classes with invalid descriptions.
    private ArrayList<Course> classes = new ArrayList<>();
    private int credits = 0;


    /**
     * Constructs a new schedule.
     * @param file the .csv file containing the schedule information.
     * @throws FileNotFoundException if the .csv file cannot be found in the files directory.
     * @throws IllegalFileFormatException if the .csv file has improper formatting.
     */
    Schedule(String file) throws FileNotFoundException, IllegalFileFormatException {
        for(int i = 0; i < 7; i++){
            week.add(new ArrayList<>());
        }

        readSchedule(file);
    }


    /**
     * This helper method reads in the .csv file containing the schedule information.
     * @param fileName the name of the schedule file.
     * @throws FileNotFoundException if the .csv file cannot be found in the files directory.
     * @throws IllegalFileFormatException if the .csv file has improper formatting.
     */
    private void readSchedule(String fileName) throws FileNotFoundException, IllegalFileFormatException {
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

        //checks to see if the class times are valid
        if(!hasValidTime(course)){
            return;
        }

        if(isNew(course, classes)){
            credits += course.getCredits();
        }
        classes.add(course);
        String day = course.getDay();

        if(day.equals("ONLINE") || day.equals("TBA")){
            return;
        }
        else if(day.equals("Monday") && !isConflicting(course, 0)){
            week.get(0).add(course);
        }
        else if(day.equals("Tuesday") && !isConflicting(course, 1)){
            week.get(1).add(course);
        }
        else if(day.equals("Wednesday") && !isConflicting(course, 2)) {
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
            if(isNew(course, conflictingCourses)){
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
        for(Iterator<Course> i = classes.iterator(); i.hasNext();) {
            Course course = i.next();
            if(course.getName().equals(event.getName())){
                credits -= course.getCredits();
                i.remove();
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
        if(!isNew(event, conflictingCourses)){
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
        LocalTime lateClass = LocalTime.parse("11:00 PM", dtf);
        LocalTime earlyClass = LocalTime.parse("7:00 AM", dtf);
        LocalTime addingStartTime = event.getStartTime();

        if(event.getDay().equals("ONLINE") || event.getDay().equals("TBA")){
            return true;
        }

        if(event.length() >= 60){
            isValid = false;
        }
        else if((addingStartTime.isAfter(lateClass) || addingStartTime.equals(lateClass)) ||
                (addingStartTime.isBefore(earlyClass) || addingStartTime.equals(earlyClass))){
            isValid = false;
        }

        if(!isValid && isNew(event, invalidCourses)){
            invalidCourses.add(event);
        }

        return isValid;
    }


    /**
     * This helper method determines if a given course
     * is part of a given ArrayList of courses.
     * @param event the course in question.
     * @param arrayList the ArrayList to check.
     * @return whether the course has already been added to the ArrayList.
     */
    private boolean isNew(Course event, ArrayList<Course> arrayList){

        for(Course lecture : arrayList){
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
        report += "----SUMMARY---------------------------\n";
        for(Course event : classes){
            //checks to see if the class has already been entered in the report.
            if(report.contains(event.getTitle())){
                continue;
            }
            report += event.toString() + "\n";
        }
        report += "Total Credits: " + credits + "\n";

        //provides a summary of invalid classes due to a time conflict.
        if(conflictingCourses.size() > 0){
            report += "\n----TIME CONFLICTS---------------------";
            for(Course event: conflictingCourses){
                report += "\n" + event.getTitle() + " conflicts with " + event.getConflict() + " on " + event.getDay();
            }
            report += "\n";
        }

        if(invalidCourses.size() > 0){
            report += "\n----ERRORS-----------------------------";
            for(Course event: invalidCourses){
                report += "\n" + event.getTitle() + " cannot last from " + dtf.format(event.getStartTime())
                        + " to " + dtf.format(event.getEndTime()) + " ";
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
        SchedulePrinter printer = new SchedulePrinter();

        //goes through each day in the week.
        for(int i = 0; i < 6; i++){

            //prints out the day only if it contains classes.
            if(week.get(i).size() > 0){

                ArrayList<Course> courses = week.get(i);
                Collections.sort(courses);
                Course earliest = courses.get(0);
                Course latest = courses.get(courses.size() - 1);

                //adds the header to the weekday's schedule
                display += printer.makeHeader(i, courses.size(), earliest, latest);
                display += "\n";

                //goes through the sorted list of courses in the week day.
                for(int j = 0; j < courses.size(); j++){

                    //creates the timeline for the course.
                    Course event = courses.get(j);
                    display += printer.makeTimelineFor(event);

                    //finds the next course in the week's schedule, if applicable.
                    Course next = null;
                    if(j + 1 < courses.size()){
                        next = courses.get(j + 1);
                    }

                    //prints the walking times between this course and the next.
                    display += printer.getWalkingTimesFor(event, next);
                    display += "\n";
                }
            }

            //adds space between week days only if it is part of the schedule.
            if(!week.get(i).isEmpty()){
                display += "\n\n\n\n";
            }
        }
        display += getReport();
        return display;
    }
}
