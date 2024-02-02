package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    // ToString Methode
    @Override
    public String toString() {
        // Controleer of lrocalDate overeenkomt met het verwachte formaat voordat je probeert te parsen
        if (localDate.matches("\\d{2}-\\d{2}-\\d{4}")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDateTime dateTime = LocalDateTime.parse(localDate, formatter);
            String formattedDate = dateTime.format(formatter);
            return "\nBehaald resultaat: " + score + "\n" +
                    "Voltooiingsdatum: " + formattedDate;
        } else {
            return "\nBehaald resultaat: " + score + "\n" +
                    "Voltooiingsdatum: " + localDate;
        }
    }



    // Getters en setters
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
