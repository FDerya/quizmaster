package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Course {
// Attributes
    private int idCourse;
    private User coordinator;
    private String nameCourse;
    private int difficultyCourse;
    public Course(int idCourse, User coordinator, String nameCourse, int difficultyCourse){
        this.idCourse = idCourse;
        this.coordinator = coordinator;
        this.nameCourse = nameCourse;
        this.difficultyCourse = difficultyCourse;
    }
// Contructors

    public Course(User coordinator, String nameCourse, int difficultyCourse){
        this(0, coordinator, nameCourse, difficultyCourse);
    }

// Getters & Setters
    public int getIdCourse() {
        return idCourse;
    }
    public void setIdCourse(int idCourse) {
        this.idCourse = idCourse;
    }
    public User getCoordinator() {
        return coordinator;
    }
    public void setCoordinator(User coordinator) {
        this.coordinator = coordinator;
    }
    public String getNameCourse() {
        return nameCourse;
    }
    public void setNameCourse(String nameCourse) {
        this.nameCourse = nameCourse;
    }
    public int getDifficultyCourse() {
        return difficultyCourse;
    }
    public void setDifficultyCourse(int difficultyCourse) {
        this.difficultyCourse = difficultyCourse;
    }
}

