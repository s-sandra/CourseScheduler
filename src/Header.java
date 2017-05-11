/**
 * This class creates a header for each day of the week in the schedule.
 * @author Sandra Shtabnaya
 */
public class Header {
    private String weekDay;
    private String[] weekDays = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};

    /**
     * Constructs a new header.
     * @param day the number of the day of the week.
     */
    public Header(int day){
        weekDay = weekDays[day];
    }


    /**
     * Generates a header for a week day.
     * @param earliest the earliest class in the week day.
     * @param latest the latest class in the week day.
     * @return the header for the week day.
     */
    public String makeHeader(Course earliest, Course latest){
        String header = "";
        int earliestHr = earliest.getStartHr();
        int range = getScheduleRange(earliest, latest);

        //adjusts the table accordingly for uniform appearance.
        shiftTable(range, earliestHr);
        System.out.println("----" + weekDay + header);
        System.out.println(scale);

        return header;
    }
}
