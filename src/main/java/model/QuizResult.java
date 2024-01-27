package model;

public class QuizResult {
    private Quiz quiz;
    private double score;

    public QuizResult(Quiz quiz, double score) {
        this.quiz = quiz;
        this.score = score;
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "quiz=" + quiz +
                ", score=" + score +
                '}';
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
