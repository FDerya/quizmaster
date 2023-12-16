package model;

public class Question {

/*
Fatma Tatar
 */
    private int idQuestion;
    private int idQuiz;
    private String questionText;
    private String answerRight;
    private String answerWrong1;
    private String answerWrong2;
    private String answerWrong3;
    private static final int AANTAL_ANTWOORDEN = 4;

    public Question(int idQuestion, int idQuiz, String questionText,
                    String answerRight, String answerWrong1, String answerWrong2, String answerWrong3) {
        this.idQuestion = idQuestion;
        this.idQuiz = idQuiz;
        this.questionText = questionText;
        this.answerRight = answerRight;
        this.answerWrong1 = answerWrong1;
        this.answerWrong2 = answerWrong2;
        this.answerWrong3 = answerWrong3;
    }


    public Question(int idQuiz, String questionText,
                    String answerRight, String answerWrong1, String answerWrong2, String answerWrong3) {
        this(0, idQuiz, questionText, answerRight, answerWrong1, answerWrong2, answerWrong3);
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
    public String getQuestionText() { return questionText;
    }
    public String getAnswerRight() {  return answerRight;
    }

    public String getAnswerWrong1() {return answerWrong1;
    }
    public String getAnswerWrong2() {return answerWrong2;
    }
    public String getAnswerWrong3() {return answerWrong3;


    }

}
