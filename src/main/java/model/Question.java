package model;

public class Question {

    // Attributes
    private int idQuestion;
    private int idQuiz;
    private String question;
    private String answerGood;
    private String answerWrong1;
    private String answerWrong2;
    private String answerWrong3;
    private static final int AANTAL_ANTWOORDEN = 4;

    public Question(int idQuestion, int idQuiz, String question,
                    String answerGood, String answerWrong1, String answerWrong2, String answerWrong3) {
        this.idQuestion = idQuestion;
        this.idQuiz = idQuiz;
        this.question = question;
        this.answerGood = answerGood;
        this.answerWrong1 = answerWrong1;
        this.answerWrong2 = answerWrong2;
        this.answerWrong3 = answerWrong3;
    }

    // Contructors
    public Question(int idQuiz, String question,
                    String answerGood, String answerWrong1, String answerWrong2, String answerWrong3) {
        this(0, idQuiz, question, answerGood, answerWrong1, answerWrong2, answerWrong3);
    }

    // Getters and Setters
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
    public String getQuestion() { return question;
    }
    public String getAnswerGood() {  return answerGood;
    }

    public String getAnswerWrong1() {return answerWrong1;
    }
    public String getAnswerWrong2() {return answerWrong2;
    }
    public String getAnswerWrong3() {return answerWrong3;


    }

}
