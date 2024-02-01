package model;
// Tom van Beek, 500941521.

public class Quiz {
    //Attributen
    private int idQuiz;
    private Course course;
    private String nameQuiz;
    private String level;
    private int minimumAmountCorrectQuestions;

    //Constructors
    public Quiz(int idQuiz, Course course, String nameQuiz, String level, int minimumAmountCorrectQuestions){
        this.idQuiz = idQuiz;
        this.course = course;
        this.nameQuiz = nameQuiz;
        this.level = level;
        this.minimumAmountCorrectQuestions = minimumAmountCorrectQuestions;
    }
    public Quiz (Course course, String nameQuiz, String level, int minimumAmountCorrectQuestions){
        this(0,course, nameQuiz,level, minimumAmountCorrectQuestions);
    }
    public Quiz(Course course){
        this(0,course, "","",0);
    }

    public Quiz(){
        this(0,null,"","",0);
    }

    @Override
    public String toString() {
        return super.toString();
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

    public int getMinimumAmountCorrectQuestions() {
        return minimumAmountCorrectQuestions;
    }

    public String getLevel() {
        return level;
    }

    public void setMinimumAmountCorrectQuestions(int minimumAmountCorrectQuestions) {
        this.minimumAmountCorrectQuestions = minimumAmountCorrectQuestions;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getNameQuiz() {
        return nameQuiz;
    }
    public void setNameQuiz(String nameQuiz) {
        this.nameQuiz = nameQuiz;
    }
}


