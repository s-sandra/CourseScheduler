import java.util.ArrayList;

/**
 * This class creates the visual representation for a schedule.
 * @author Sandra Shtabnaya
 */
public class SchedulePrinter {
    private String weekDay;
    private String[] weekDays = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
    private int numClasses;
    private Course earliest;
    private int range; //stores the amount of time spent in class during the day.
    private String scale; //stores the time scale for each week day
    private String header; //stores the header for the week day timeline.
    private final int LONGEST_NAME_LEN = 10; //stores the longest course name for aligning the course time lines.

    /**
     * Constructs a SchedulePrinter.
     */
    public SchedulePrinter(){}


    /**
     * Generates a header for a week day.
     * @param day the number of the day of the week.
     * @param numClasses the amount of classes in the week day.
     * @param earliest the earliest class in the week day.
     * @param latest the latest class in the week day.
     * @return the header for the week day.
     */
    public String makeHeader(int day, int numClasses, Course earliest, Course latest){
        weekDay = weekDays[day];
        this.numClasses = numClasses;
        this.earliest = earliest;
        int earliestHr = earliest.getStartHr();
        range = getScheduleRange(earliest, latest);

        //adjusts the table accordingly for uniform appearance.
        header = "\n----" + weekDay;
        shiftTable(earliestHr);
        header += "\n" + scale;

        return header;
    }


    /**
     * This helper method determines the length of the day in five minute intervals.
     * @param earliest the earliest class in the day.
     * @param latest the latest class in the day.
     * @return the length of the week day in five minute intervals.
     */
    private int getScheduleRange(Course earliest, Course latest){
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
     * This helper method adjusts the table based on the time range
     * of the week day. It shifts the headers and the scale.
     */
    private void shiftTable(int earliestHr){
        shiftHeaders();
        shiftScale(earliestHr);
    }


    /**
     * This helper method adjusts the length of the schedule header
     * according to the length of the longest class name and the
     * range of the time line.
     */
    private void shiftHeaders(){

        //increases the length of the header to span the gap
        //between the course name and time line.
        for(int i = 0; i < LONGEST_NAME_LEN + 4; i++){
            header += "-";
        }

        //increases the length of the header until it reaches the
        //end of the time line, accounting for the extra lines
        //before the header title, and the length of the title.
        for(int i = weekDay.length() + 4; i < range; i++){
            header += "-";
        }

        header += "---------MEETING TIMES--------";

        //if there is more than one class in the week day, adds an
        //additional section to the header for the walking times
        //between classes.
        if(numClasses > 1){
            header += "---------------WALKING TIMES-----------";
        }
    }


    /**
     * This helper method adjusts the time line scale based on the
     * start of the first class and the end of the last class in the
     * week day.
     * @param earliestHr the start hour of the earliest class.
     */
    private void shiftScale(int earliestHr){
        int timeRange;
        scale = "";

        //adds whitespace between the course name and the start
        //of the time line.
        for(int i = 0; i < LONGEST_NAME_LEN + 4; i++){
            scale += " ";
        }

        int hour = earliestHr;
        int prevHour = earliestHr;

        //determines how many hours are in the time range.
        timeRange = (range / 12) + 1;

        //adds hours and whitespace to the scale.
        for(int i = 1; i <= timeRange; i++){

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
     * Generates the title for each timeline in a weekday.
     * @param event the course to generate a title for.
     * @return the header for the course's timeline.
     */
    private String makeTimelineHeaderFor(Course event){
        String timeline = event.getName();

        //adds white space between the class name and the start of its time line.
        for(int i = event.getName().length(); i < LONGEST_NAME_LEN + 4; i++){
            timeline += " ";
        }

        return timeline;
    }


    /**
     * Generates a timeline for a given course in the week.
     * @param event the course for which to create a timeline.
     * @return the timeline for the course, represented by dots and
     * followed by meeting times.
     */
    public String makeTimelineFor(Course event){
        String minutes = makeTimelineHeaderFor(event);
        minutes += makeLine(event);
        minutes += getMeetingTimes(event);
        return minutes;
    }


    /**
     * This helper method constructs the time line for each class in the day.
     * Each "." represents 5 minutes, "[" the start time, "=" each five minutes in
     * class and "]" the end time.
     * @param event the course for which to create a timeline.
     * @return the visual representation of the timeline.
     */
    private String makeLine(Course event){
        ArrayList<String> line = new ArrayList<>();
        int startMin = getFiveMinuteStart(event);
        int endMin = startMin + event.length();

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

        String timeline = "";

        for(String minute : line){
            timeline += minute;
        }

        return timeline;
    }


    /**
     * This helper method calculates which dot in the timeline represents
     * the start time of the given course.
     * @param added the course for which to find the start time.
     * @return how many minutes the course's start time is from the earliest
     * course's start time.
     */
    private int getFiveMinuteStart(Course added){
        int start;
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
            //current class times and converts to five minutes.
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
     * This helper method returns the meetings times of a given class.
     * @param event the class for which to generate meeting times.
     * @return a visual representation of the class' meeting times.
     */
    private String getMeetingTimes(Course event){
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

        return times;
    }


    /**
     * Returns the time between and location of this course and the next, if applicable.
     * @return the information under the walking times header.
     * @param event the course for which to generate walking times.
     * @param nextClass the course after the current course. If the current course is
     * the last, then next is null.
     * @return the walking times and destinations of the given course.
     */
    public String getWalkingTimesFor(Course event, Course nextClass){
        String walkingTimes = "";

        int sHrGap = event.getEndHr();
        int sMinGap = event.getEndMin();
        String sTimeGap = event.getStartTimeOfDay();

        if(nextClass != null){
            int eHrGap = nextClass.getStartHr();
            int eMinGap = nextClass.getStartMin();
            String eTimeGap = nextClass.getStartTimeOfDay();

            //determines the time until next class and prints out the walking times and destinations.
            int walkingTime = getFiveMinuteBreak(eHrGap, eMinGap, eTimeGap, sHrGap, sMinGap, sTimeGap);
            String breakTime = convertToHours(walkingTime);
            walkingTimes = "       " + breakTime + " to get from " + event.getLocation() +
                    " to " + nextClass.getLocation();
        }

        return walkingTimes;
    }


    /**
     * This helper method converts the time between classes
     * from minutes to hours.
     * @param walkingTime the time between two classes.
     * @return the amount of hours in the walking time.
     */
    private String convertToHours(int walkingTime){
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
     * This helper method determines the amount of
     * time (in terms of five minutes) between two classes.
     * @param sHr the starting hour.
     * @param sMin the starting minute.
     * @param sTime the starting time of day.
     * @param eHr the ending hour.
     * @param eMin the ending minute.
     * @param eTime the ending time of day.
     */
    private int getFiveMinuteBreak(int eHr, int eMin, String eTime, int sHr, int sMin, String sTime){
        Course course = new Course(sHr, sMin, sTime, eHr, eMin, eTime);
        return course.length() * 5;
    }
}
