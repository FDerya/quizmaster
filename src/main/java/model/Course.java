package model;

public class Course {
// Attributes
    private int idCourse;
    private User coordinator;
    private String nameCourse;
    private int difficultyCourse;

// Contructors
    public Course(int idCourse, User coordinator, String nameCourse, int difficultyCourse){
        this.idCourse = idCourse;
        this.coordinator = coordinator;
        this.nameCourse = nameCourse;
        this.difficultyCourse = difficultyCourse;
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
