# CourseScheduler

## Description
My program, CourseScheduler, serves to assistant users during the college course selection process, similar to the scheduler tool provided 
in Banner during online registration. Since students do not have access to this tool until their appointed registration times, picking 
classes and calculating time conflicts can become a real hassle. My CourseScheduler program has all the features of the Banner tool, 
including its own useful modifications. It reads the file containing the student’s desired classes and outputs to the console a timeline 
of courses for each applicable day of the week. The y-axis contains the hours of the day, and the x-axis the name of the course. The 
timeline is split into five minute intervals denoted by a dot. It provides a visual representation of each class’ position in the day 
by using brackets and equal signs. The scheduler also provides the student with the amount of time between classes, the meeting times of
classes and the locations of the current and next class. This allows the user to plan accordingly, helping determine if getting to the 
next lecture on time is even possible. At the end of the schedule, the program outputs the names, titles and total credits of all the 
added courses, and a list of any rejected classes and the courses which they conflicted with. My CourseScheduler program also utilizes 
the Course class, which stores the information of each course in the week. It makes a separate course object for each meeting day.   

## Instructions
To run the program, you must create a csv file containing your desired classes, and place its name as the command line argument. 
Each class will have its own line in the file. This information is intended to be pulled from the UMW course catalogue, with similar 
formatting. The file format is as follows:

<blockquote>Name, Title, Credits, Hall & Room Number(s), Meeting Day(s), Meeting Time</blockquote>

If there are irregular meeting days, add the following to the end of the line, repeating for each differing meeting day and time:<br>
<blockquote>, Additional Meeting Day(s), Additional Meeting Time</blockquote>

If the course is online, follow the below format:<br> 
<blockquote>Name, Title, Credits, ONLINE</blockquote>

If the course location is yet to be announced, follow the following format:<br>
<blockquote>Name, Title, Credits, TBA</blockquote>

## Examples
<blockquote>CPSC 220, Computer Programming, 4, HCC 329, M, 2:00 PM – 4:00 PM, WF, 2:00 PM – 3:00 PM<br>
PSYC 100, General Psychology, 3, MONR 116, MWF, 11:00am – 11:50am</blockquote>

### Course Name
This is the department abbreviation and number of the course (CPSC 220).

### Course Title
This is the name of the course (Computer Programming and Problem Solving).

### Hall and Room Number
This is the location (it may be abbreviated) and room number of the class (HCC 329). 

### Credits
The number of credit hours the class is worth (4).

### Meeting Days
The days of the week corresponding with its following meeting times. It will be a string containing the first letters of each 
weekday, with Thursday denoted as “R”. 

### Meeting Times
The start and end times of the course, with AM/am or PM/pm. They must be separated with a dash (2:00 PM – 3:00 PM).
