package model;
// Tom van Beek, 500941521.

public class Quiz {
    //Attributen
    private int idQuiz;
    private Course course;
    private String nameQuiz;
    private String level;
    private int amountQuestions;

    //Constructors
    public Quiz(int idQuiz, Course course, String nameQuiz, String level, int amountQuestions){
        this.idQuiz = idQuiz;
        this.course = course;
        this.nameQuiz = nameQuiz;
        this.level = level;
        this.amountQuestions = amountQuestions;
    }
    public Quiz (Course course, String nameQuiz, String level, int amountQuestions){
        this(0,course, nameQuiz,level,amountQuestions);
    }
    public Quiz(Course course){
        this(0,course, "","",0);
    }

    public Quiz(){
        this(0,null,"","",0);
    }
    @Override
    public String toString() {
        StringBuilder resultString = new StringBuilder();
        resultString.append(nameQuiz).append("\t\t\t") ;
        resultString.append(amountQuestions);
        return resultString.toString();
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

    public int getAmountQuestions() {
        return amountQuestions;
    }

    public String getLevel() {
        return level;
    }

    public void setAmountQuestions(int amountQuestions) {
        this.amountQuestions = amountQuestions;
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


