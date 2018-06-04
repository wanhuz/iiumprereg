package BaseClass;

public class Course {
    String courseCode;
    String courseSec;

    public Course() {

    }

    public Course(String courseCode, String courseSec) {
        this.courseCode = courseCode;
        this.courseSec = courseSec;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseSec() {
        return courseSec;
    }

    public void setCourseSec(String courseSec) {
        this.courseSec = courseSec;
    }


}
