package model;

public class Question {

    /*
    Fatma Tatar
     */
    private int idQuestion;
    private Quiz idQuiz;
    private String question;
    private String answerRight;
    private String answerWrong1;
    private String answerWrong2;
    private String answerWrong3;
    private static final int AANTAL_ANTWOORDEN = 4;

    public Question(int idQuestion, Quiz  idQuiz, String question,
                    String answerRight, String answerWrong1, String answerWrong2, String answerWrong3) {
        this.idQuestion = idQuestion;
        this.idQuiz = idQuiz;
        this.question = question;
        this.answerRight = answerRight;
        this.answerWrong1 = answerWrong1;
        this.answerWrong2 = answerWrong2;
        this.answerWrong3 = answerWrong3;
    }

    // Getters and Setters
    public Quiz getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(Quiz idQuiz) {
        this.idQuiz = idQuiz;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswerRight() {
        return answerRight;
    }

    public String getAnswerWrong1() {
        return answerWrong1;
    }

    public String getAnswerWrong2() {
        return answerWrong2;
    }

    public String getAnswerWrong3() {
        return answerWrong3;
    }

    }
