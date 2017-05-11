import java.io.FileNotFoundException;

/**
 * Creates and prints the schedule for the semester.
 * @author Sandra Shtabnaya
 */
public class SemesterScheduler {
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
}
