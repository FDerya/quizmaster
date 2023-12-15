package model;
//Bianca Duijvesteijn, studentnummer 500940421

import java.util.ArrayList;
import java.util.List;

public class Group {
    final int MAX_AANTAL_STUDENTEN = 25;
    private String groupName;
    private int numberOfStudents;
    private User userName;
    private Course courseName;
    private List<Group> groups = new ArrayList<>();

    /*   public Group(String groupName, String courseName, int numberOfStudents, User userName) {
        this("default", "default", 0, User.getUserName);
    }*/

    public Group(String groupName,Course courseName , int numberOfStudents, User userName) {
        this.groupName = groupName;
        this.numberOfStudents = numberOfStudents;
        this.userName = getUserName();
        this.courseName = getCourseName();
    }

    @Override
    public String toString() {
        return "Group{" +
                "MAX_AANTAL_STUDENTEN=" + MAX_AANTAL_STUDENTEN +
                ", groupName='" + groupName + '\'' +
                ", numberOfStudents=" + numberOfStudents +
                ", userName=" + userName +
                ", courseName=" + courseName +
                ", groups=" + groups +
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
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
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

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
