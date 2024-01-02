package controller;

import database.mysql.QuestionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.Question;


import java.util.List;

/*public class ManageQuestionsController {

    private final QuestionDAO questionDAO;
    @FXML
    ListView<Question> questionList;
    @FXML
    Button deleteQuestion;
    @FXML
    Label warningLabel;
    private Question question;


    public ManageQuestionsController() {
        this.questionDAO = new QuestionDAO(questionTestMain.getDBaccess());


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
        questionTestMain.getSceneManager().showWelcomeScene();
    }

    public void doCreateQuestion() {
        Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            questionTestMain.getSceneManager().showCreateUpdateQuestionScene(selectedQuestion);
        } else {
            warningLabel.setVisible(true);
            warningLabel.setText("Je moet eerst een vraag kiezen");
        }
    }


    public void doUpdateQuestion(ActionEvent event) {
        Question question = questionList.getSelectionModel().getSelectedItem();
        questionTestMain.getSceneManager().showCreateUpdateQuestionScene(question);

    }


    public void doDeleteQuestion(ActionEvent event) {
        Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            questionList.getItems().remove(selectedQuestion);

        } else {
            warningLabel.setVisible(true);
            warningLabel.setText("Je moet eerst een vraag kiezen");
        }
    }
}*/

