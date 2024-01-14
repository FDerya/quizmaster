package model;

public class Question {


    private int idQuestion;
    private Quiz quiz;
    private String question;
    private String answerRight;
    private String answerWrong1;
    private String answerWrong2;
    private String answerWrong3;

    public Question(int idQuestion, Quiz  quiz, String question,
                    String answerRight, String answerWrong1, String answerWrong2, String answerWrong3) {
        this.idQuestion = idQuestion;
        this.quiz = quiz;
        this.question = question;
        this.answerRight = answerRight;
        this.answerWrong1 = answerWrong1;
        this.answerWrong2 = answerWrong2;
        this.answerWrong3 = answerWrong3;
    }

    public Question(Quiz quiz, String question, String answerRight, String answerWrong1, String answerWrong2, String answerWrong3) {
        this(0, quiz, question, answerRight, answerWrong1, answerWrong2, answerWrong3);
    }

    @Override
    public String toString() {
        return "Question: " + idQuestion + ", Quiz: " + quiz.getIdQuiz() + ", Text: " + question;
    }

    // Getters and Setters
    public int getIdQuestion() {
        return idQuestion;
    }

    public Quiz getQuiz() {
        return quiz;
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

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswerRight(String answerRight) {
        this.answerRight = answerRight;
    }

    public void setAnswerWrong1(String answerWrong1) {
        this.answerWrong1 = answerWrong1;
    }

    public void setAnswerWrong2(String answerWrong2) {
        this.answerWrong2 = answerWrong2;
    }

    public void setAnswerWrong3(String answerWrong3) {
        this.answerWrong3 = answerWrong3;
    }
}
