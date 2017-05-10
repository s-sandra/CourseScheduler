import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

/**
 * This class creates a visual representation of a schedule file.
 * @author Sandra Shtabnaya
 */
public class Schedule {
    private ArrayList<ArrayList<Course>> week = new ArrayList<ArrayList<Course>>(); //stores all the classes in a week.
    private ArrayList<Course> conflictingCourses = new ArrayList<Course>(); //stores all the conflicting classes in the file.
    private ArrayList<Course> invalidCourses = new ArrayList<Course>(); //stores all classes with invalid descriptions.
    private ArrayList<String> locations = new ArrayList<String>();
    private ArrayList<Course> classes = new ArrayList<Course>();
    private int credits = 0;


    /**
     * Constructs a new schedule.
     * @param file the .csv file containing the schedule information.
     * @throws FileNotFoundException if the .csv file cannot be found in the files directory.
     * @throws IllegalFileException if the .csv file has improper formatting.
     */
    public Schedule(String file) throws FileNotFoundException, IllegalFileException{
        for(int i = 0; i < 7; i++){
            week.add(new ArrayList<Course>());
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

            Course course = new Course(file);
            classes.add(course);
        }
    }
}
