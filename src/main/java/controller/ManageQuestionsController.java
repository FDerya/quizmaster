package controller;

import database.mysql.QuestionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Question;
import view.Main;
import java.util.List;
import java.util.Optional;

public class ManageQuestionsController {
    private final QuestionDAO questionDAO;
    @FXML
    ListView<Question> questionList;
    @FXML
    Label warningLabel;
    private Question question;


    public ManageQuestionsController() {
        this.questionDAO = new QuestionDAO(Main.getDBaccess());


    }

    public void setup() {
        List<Question> allQuestions = questionDAO.getAll();
        ObservableList<Question> questionObservableList =
                FXCollections.observableArrayList(allQuestions);
        questionList.setItems(questionObservableList);
        questionList.getSelectionModel().selectFirst();
    }

    // terug naar menu
    public void doMenu(ActionEvent event) {
        Main.getSceneManager().showWelcomeScene();
    }

    public void doCreateQuestion() {
        Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            Main.getSceneManager().showCreateUpdateQuestionScene(selectedQuestion);
            warningLabel.setVisible(false);
        } else {
            warningLabel.setVisible(true);
            warningLabel.setText("Je moet eerst een vraag kiezen");
        }
    }


    public void doUpdateQuestion(ActionEvent event) {
        Question question = questionList.getSelectionModel().getSelectedItem();
        Main.getSceneManager().showCreateUpdateQuestionScene(question);

    }


    public void doDeleteQuestion(ActionEvent event) {
        Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            questionList.getItems().remove(selectedQuestion);
            warningLabel.setVisible(false);

        } else {
            warningLabel.setVisible(true);
            warningLabel.setText("Je moet eerst een vraag kiezen");
        }
    }
}

