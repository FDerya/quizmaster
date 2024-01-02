//
// Dit model is gemaakt door Eline van Tunen, 500636756
//

package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Course {
// Attributes
    private int idCourse;
    private User coordinator;
    private String nameCourse;
    private String difficultyCourse;

// Contructors
    public Course(int idCourse, User coordinator, String nameCourse, String difficultyCourse){
        this.idCourse = idCourse;
        this.coordinator = coordinator;
        this.nameCourse = nameCourse;
        this.difficultyCourse = difficultyCourse;
    }
    public Course(int idCourse, String nameCourse, String difficulty) {
        this.idCourse = idCourse;
        this.nameCourse = nameCourse;
        this.difficultyCourse = difficulty;
    }

    public Course(User coordinator, String nameCourse, String difficultyCourse){
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
    public String getDifficultyCourse() {
        return difficultyCourse;
    }
    public void setDifficultyCourse(String difficultyCourse) {
        this.difficultyCourse = difficultyCourse;
    }
}

