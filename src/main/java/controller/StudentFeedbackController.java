package controller;
// Bianca Duijvesteijn, studentnummer 500940421
// Manages the display of quiz feedback for a specific user on a particular quiz

import javacouchdb.QuizResultCouchDBDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.Quiz;
import model.QuizResult;
import model.User;
import view.Main;

import java.util.List;

public class StudentFeedbackController {
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
        feedbackLabel.setText("Quiz Feedback voor " + quiz.getNameQuiz() + " door " + user.getFullName());

        List<QuizResult> quizResults = quizResultCouchDBDAO.getQuizResults(quiz, user);


        feedbackList.getItems().addAll(quizResults);
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
}

