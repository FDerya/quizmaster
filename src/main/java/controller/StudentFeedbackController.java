package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.Quiz;
import model.QuizResult;
import view.Main;

import java.util.List;

public class StudentFeedbackController {

    @FXML
    private Label feedbackLabel;
    @FXML
    private ListView<QuizResult> feedbackList;

    public void setup(/*List<QuizResult> quizResults*/ Quiz quiz) {
        // feedbackLabel.setText("Feedback voor quiz: " + quiz);
        //feedbackList.getItems().addAll(quizResults);
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

