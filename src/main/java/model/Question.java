package model;

public class Question {

    private int idQuestion;
    private int idQuiz;
    private String question;
    private String answerGood;
    private String answerWrong1;
    private String answerWrong2;
    private String answerWrong3;
    private static final int AANTAL_ANTWOORDEN = 4;

    // Constructor
    public Question (int idQuestion, int idQuiz, String question,
                     String answerGood, String answerWrong1, String answerWrong2, String answerWrong3) {
        this.idQuestion = idQuestion;

        this.idQuiz = idQuiz;
        this.question = question;
        this.answerGood = answerGood;
        this.answerWrong1 = answerWrong1;
        this.answerWrong2 = answerWrong2;
        this.answerWrong3 = answerWrong3;
    }

    public Question (int idQuiz, String questionString,
                     String answerGood, String answerWrong1, String answerWrong2, String answerWrong3) {
        this(0, idQuiz, questionString, answerGood, answerWrong1, answerWrong2, answerWrong3);
    }

    // Methodes
    @Override
    public String toString() {
        StringBuilder resultString = new StringBuilder();
        resultString.append(question);
        return resultString.toString();
    }
    // Getters en Setters
    public void setIdQuiz(int idQuiz) {
        this.idQuiz = idQuiz;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public int getIdQuiz() {
        return idQuiz;
    }

    public String getQuestion() {
        return question;
    }

    public String getanswerGood() {
        return answerGood;
    }

    public String getAnswerB() {
        return answerWrong1;
    }

    public String getAnswerC() {
        return answerWrong2;
    }

    public String getAnswerD() {
        return answerWrong3;
    }
}


