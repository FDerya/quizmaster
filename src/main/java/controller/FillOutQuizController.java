package controller;

import database.mysql.QuestionDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import model.Question;
import model.Quiz;
import view.Main;

import java.util.ArrayList;
import java.util.List;

public class FillOutQuizController {
    QuestionDAO questionDAO;

    @FXML
    private Label titleLabel;
    @FXML
    private TextArea questionArea;
    @FXML
    private Button previousQuestionButton;
    @FXML
    private Button nextQuestionButton;

    public void setup(Quiz quiz) {
        questionDAO = new QuestionDAO(Main.getDBaccess());
        if (titleLabel.getText().equals("Vraag 1")) {
         previousQuestionButton.setVisible(false);
         questionArea.setText("test");
         List<Question> questionList = questionDAO.getQuestionsForQuiz(quiz);
        }
    }

    public void doRegisterA() {}

    public void doRegisterB() {}

    public void doRegisterC() {}

    public void doRegisterD() {}

    public void doNextQuestion() {}

    public void doPreviousQuestion() {}

    @FXML
    private void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }
}
