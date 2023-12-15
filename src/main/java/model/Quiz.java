package model;
// Tom van Beek
import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private Course course;
    private int level;
    private int aantal;

    public Quiz(Course course, int level, int aantal){
        this.course = course;
        this.level = level;
        this.aantal = aantal;
    }
    public Quiz(Course course){
        this(course,0,0);
    }

    public Course getCourse() {
        return course;
    }

    public int getAantal() {
        return aantal;
    }

    public int getLevel() {
        return level;
    }

    public void setAantal(int aantal) {
        this.aantal = aantal;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
