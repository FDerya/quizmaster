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
    private boolean isNew;

    // Constructors
    public Group(int idGroup, Course course, String groupName, int amountStudent, User teacher){
        this.idGroup = idGroup;
        this.course = course;
        this.groupName = groupName;
        this.amountStudent = amountStudent;
        this.teacher = teacher;
        this.isNew = false;
    }

    public Group() {
        super();
    }

    // toString methods
    @Override
    public String toString() {
        return groupName;
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

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getAmountStudent() {
        return amountStudent;
    }

    public void setAmountStudent(int amountStudent) {
        this.amountStudent = amountStudent;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}