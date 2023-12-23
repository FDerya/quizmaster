package model;
// Bianca Duijvesteijn, studentnummer 500940421

public class Group {
    // Attributes
    final int MAX_AANTAL_STUDENTEN = 25;
    private int idGroup;
    private int idTeacher;
    private Course courseName;
    private String groupName;
    private int amountStudent;
    private User userName;

    // Constructors
    public Group(int idGroup, int idTeacher, Course courseName, String groupName, int amountStudent,
                 User userName) {
        this.idGroup = idGroup;
        this.idTeacher = idTeacher;
        this.courseName = courseName;
        this.groupName = groupName;
        this.amountStudent = amountStudent;
        this.userName = userName;
    }
    public Group(Course courseName, String groupName, int amountStudent, User userName) {
        this.courseName = courseName;
        this.groupName = groupName;
        this.amountStudent = amountStudent;
        this.userName = userName;
    }

    // toString-methode
    @Override
    public String toString() {
        return "Deze groep bestaat uit maximaal " + MAX_AANTAL_STUDENTEN + ". De groep heet " + groupName +
                ", er zitten " + amountStudent + " studenten in de groep. De coordinator is: " + userName +
                " en de groep volgt de cursus: " + courseName + '}';
    }


    // Getters en setters voor de attributen
    public int getMAX_AANTAL_STUDENTEN() {
        return MAX_AANTAL_STUDENTEN;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    public int getIdTeacher() {
        return idTeacher;
    }

    public void setIdTeacher(int idTeacher) {
        this.idTeacher = idTeacher;
    }

    public Course getCourseName() {
        return courseName;
    }

    public void setCourseName(Course courseName) {
        this.courseName = courseName;
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

    public User getUserName() {
        return userName;
    }

    public void setUserName(User userName) {
        this.userName = userName;
    }
}

