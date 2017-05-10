import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

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
        for(Course event : classes){
            report += event.toString() + "\n";
        }

        report += "Total Credits: " + credits;
        return report;
    }


    public String toString(){
        String display = "";
        display += getReport();
        return display;
    }
}
