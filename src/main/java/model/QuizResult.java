package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuizResult {
    private String quiz;
    private String score;
    private String result;
    private String localDateTime;
    private String user;

    public QuizResult(String quiz, String score, String result, String localDateTime, String user) {
        this.quiz = quiz;
        this.score = score;
        this.result = result;
        this.localDateTime = localDateTime;
        this.user = user;
    }

    // ToString Methode
    @Override
    public String toString() {
        // Controleer of localDate overeenkomt met het verwachte formaat voordat je probeert te parsen
        if (localDateTime.matches("\\d{2}-\\d{2}-\\d{4}")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDateTime dateTime = LocalDateTime.parse(localDateTime, formatter);
            String formattedDate = dateTime.format(formatter);
            return "\nBehaald resultaat: " + score + "\n" +
                    "Voltooiingsdatum: " + formattedDate;
        } else {
            return "\nBehaald resultaat: " + score + "\n" +
                    "Voltooiingsdatum: " + localDateTime;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
