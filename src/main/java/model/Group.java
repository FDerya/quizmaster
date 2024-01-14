package model;
// Bianca Duijvesteijn, studentnummer 500940421
// Manages groups in the UI, including creating, updating, and deleting.
// Shows the selected group with course information and the number of similar groups.

public class Group {
    private int idGroup;
    private Course course;
    private String groupName;
    private int amountStudent;
    private User teacher;

    // Constructors
    public Group(int idGroup, Course course, String groupName, int amountStudent, User teacher) {
        this.idGroup = idGroup;
        this.course = course;
        this.groupName = groupName;
        this.amountStudent = amountStudent;
        this.teacher = teacher;
    }

    public Group(Course course, String groupName, int amountStudent, User teacher) {
        this.course = course;
        this.groupName = groupName;
        this.amountStudent = amountStudent;
        this.teacher = teacher;
    }

    // toString-methode
    @Override
    public String toString() {
        return String.valueOf(course);
    }

    //Getters en setters
    public int getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getAmountStudent() {
        return amountStudent;
    }

    public User getTeacher() {
        return teacher;
    }

}


