package model;

import java.time.LocalDate;

public class QuizResult {
    private String quiz;
    private String score;
    private String localDate;
    private String user;

    public QuizResult(String quiz, String score, String localDate, String user) {
        this.quiz = quiz;
        this.score = score;
        this.localDate = localDate;
        this.user = user;
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "quiz=" + quiz +
                ", score=" + score +
                '}';
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getLocalDate() {
        return localDate;
    }

    public void setLocalDate(String localDate) {
        this.localDate = localDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
