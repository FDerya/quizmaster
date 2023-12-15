package model;
// Tom van Beek, 500941521.

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    //Attributen
    private int idQuiz;
    private Course course;
    private String nameQuiz;
    private int level;
    private int aantal;

    //Constructors
    public Quiz(int idQuiz, Course course, String nameQuiz, int level, int aantal){
        this.idQuiz = idQuiz;
        this.course = course;
        this.nameQuiz = nameQuiz;
        this.level = level;
        this.aantal = aantal;
    }
    public Quiz(Course course){
        this(0,course, "",0,0);
    }

    //Getters en setters
    public int getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(int idQuiz) {
        this.idQuiz = idQuiz;
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
