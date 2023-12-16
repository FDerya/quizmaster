package model;
//Bianca Duijvesteijn, studentnummer 500940421

public class Group {
    //Attributen
    final int MAX_AANTAL_STUDENTEN = 25;
    private int idGroup;
    private int idTeacher;
    private Course courseName;
    private String groupName;
    private int amountStudent;
    private User userName;


    //All args constructor


    public Group(int idGroup, int idTeacher, Course courseName, String groupName, int amountStudent,
                 User userName) {
        this.idGroup = idGroup;
        this.idTeacher = idTeacher;
        this.courseName = courseName;
        this.groupName = groupName;
        this.amountStudent = amountStudent;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Group{" +
                "MAX_AANTAL_STUDENTEN=" + MAX_AANTAL_STUDENTEN +
                ", groupName='" + groupName + '\'' +
                ", numberOfStudents=" + amountStudent +
                ", userName=" + userName +
                ", courseName=" + courseName +
                '}';
    }

    //Getters en Setters
    public int getMAX_AANTAL_STUDENTEN() {
        return MAX_AANTAL_STUDENTEN;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getNumberOfStudents() {
        return amountStudent;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.amountStudent = numberOfStudents;
    }

    public User getUserName() {
        return userName;
    }

    public void setUserName(User userName) {
        this.userName = userName;
    }

    public Course getCourseName() {
        return courseName;
    }
    public void setCourseName(Course courseName) {
        this.courseName = courseName;
    }
}
