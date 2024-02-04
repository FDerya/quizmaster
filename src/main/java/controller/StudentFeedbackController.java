package controller;
// Defines a custom list cell for displaying quiz results in a JavaFX app. The createResultsListCell method
// sets up an HBox with labels for the quiz result, date/time, and score, using data from the given QuizResult
// object. It includes error handling for parsing the date/time string.

import javacouchdb.QuizResultCouchDBDAO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import model.Quiz;
import model.QuizResult;
import model.User;
import view.Main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentFeedbackController {
    @FXML
    private Label countLabel;
    @FXML
    private Label feedbackLabel;
    @FXML
    private ListView<QuizResult> feedbackList;
    private QuizResultCouchDBDAO quizResultCouchDBDAO;

    public void setQuizResultCouchDBDAO(QuizResultCouchDBDAO quizResultCouchDBDAO) {
        this.quizResultCouchDBDAO = quizResultCouchDBDAO;
    }

    // Sets up the controller, with the quiz, the user en the results
    public void setup(Quiz quiz, User user) {
        if (quizResultCouchDBDAO == null) {
            throw new IllegalStateException("quizResultCouchDBDAO is not initialized");
        }

        feedbackLabel.setText("Feedback voor quiz: " + quiz.getNameQuiz() + " door " + user.getFullName());
        feedbackList.setCellFactory(param -> new ResultsListCell());
        List<QuizResult> quizResults = quizResultCouchDBDAO.getQuizResults(quiz, user);
        feedbackList.getItems().addAll(quizResults);
        updateAttemptCountLabel(quizResults);
    }

    // Handles the action to return to the welcome scene
    @FXML
    private void doMenu() {
        try {
            Main.getSceneManager().showWelcomeScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Calculates and sets the number of attempts based on quiz results
    private void updateAttemptCountLabel(List<QuizResult> quizResults) {
        int totalAttempts = 0;
        for (QuizResult result : quizResults) {
            totalAttempts += result.getAttemptCount();
        }
        countLabel.setText("Aantal pogingen: " + totalAttempts);
    }


    private static class ResultsListCell extends ListCell<QuizResult> {
        @Override
        protected void updateItem(QuizResult quizResult, boolean empty) {
            super.updateItem(quizResult, empty);

            if (empty || quizResult == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox hBox = createResultsListCell(quizResult);
                setGraphic(hBox);
            }
        }

        // Creates an HBox with Labels for result and date, setting their texts
        private Label createResultLabel(String resultText) {
            Label result = new Label(resultText);
            result.setPrefWidth(100.0);
            result.setPadding(new Insets(0, 10, 0, 0));
            return result;
        }

        // Creates a Label for displaying the date and time with the specified text
        private Label createDateAndTimeLabel(String dateTimeText) {
            Label dateAndTime = new Label(dateTimeText);
            dateAndTime.setPrefWidth(150.0);
            dateAndTime.setPadding(new Insets(0, 10, 0, 0));
            return dateAndTime;
        }

        // Creates a Label for displaying the quiz result with the specified text
        private Label createScoreLabel(String scoreText) {
            Label scoreLabel = new Label(scoreText);
            scoreLabel.setPrefWidth(100.0);
            scoreLabel.setPadding(new Insets(0, 10, 0, 0));
            return scoreLabel;
        }

        // Creates an HBox containing Labels for the result, date, and score, setting their texts based on the provided
        private HBox createResultsListCell(QuizResult quizResult) {
            LocalDateTime dateTime = LocalDateTime.parse(quizResult.getLocalDateTime());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = dateTime.format(formatter);

            Label resultLabel = createResultLabel(quizResult.getResult());
            Label dateAndTimeLabel = createDateAndTimeLabel(formattedDateTime);
            Label scoreLabel = createScoreLabel(quizResult.getScore());

            HBox hBox = new HBox(resultLabel, dateAndTimeLabel, scoreLabel);
            return hBox;
        }
    }
}


