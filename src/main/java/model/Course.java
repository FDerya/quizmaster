//
// Dit model is gemaakt door Eline van Tunen, 500636756
//

package model;

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

    public Course(User coordinator, String nameCourse, String difficultyCourse){
        this(0, coordinator, nameCourse, difficultyCourse);
    }


// Methods
    @Override
    public String toString(){
        return String.format("%s", getNameCourse());
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

