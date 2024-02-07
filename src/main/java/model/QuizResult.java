package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    // ToString for QuizResults
    @Override
    public String toString() {
        if (localDateTime.matches("\\d{2}-\\d{2}-\\d{4}T\\d{2}:\\d{2}:\\d{2}.\\d+")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(localDateTime, formatter);
            String formattedDate = dateTime.format(formatter);
            return "\nBehaald resultaat: " + score + "\n" +
                    "Voltooiingsdatum: " + formattedDate;
        } else {
            return "\nBehaald resultaat: " + score + "\n" +
                    "Voltooiingsdatum: " + localDateTime;
        }
    }

    // Formats the date and time to the correct format
    public String formatLocalDateTime(String localDateTimeString) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(localDateTimeString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            return dateTime.format(formatter);
        } catch (DateTimeParseException e) {
            return localDateTimeString;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLocalDateTime() {
        return formatLocalDateTime(localDateTime);
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
