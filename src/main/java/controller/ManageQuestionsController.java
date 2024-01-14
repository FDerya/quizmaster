package controller;

import database.mysql.CourseDAO;
import database.mysql.QuestionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Course;
import model.Question;
import model.User;
import view.Main;
import java.util.List;
import java.util.Optional;

public class ManageQuestionsController {
    private final QuestionDAO questionDAO;

    private final CourseDAO courseDAO;

    @FXML
    ListView<Question> questionList;
    @FXML
    Label warningLabel;
    private Question question;

    @FXML
    Label questionCountLabel;

    public ManageQuestionsController() {
        this.questionDAO = new QuestionDAO(Main.getDBaccess());
        this.courseDAO = new CourseDAO(Main.getDBaccess());
    }

    public void setup() {

        User currentUser = User.getCurrentUser();
        int userId = currentUser.getIdUser();
        List<Question> userQuestions = questionDAO.getQuestionsForUser(userId);
        ObservableList<Question> questionObservableList =
                FXCollections.observableArrayList(userQuestions);
        questionList.setItems(questionObservableList);
        questionList.getSelectionModel().selectFirst();

        questionList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateQuestionCount(newValue)
        );

        // Select the first item to trigger the listener
        questionList.getSelectionModel().selectFirst();
    }

    private void updateQuestionCount(Question selectedQuestion) {
        if (selectedQuestion != null) {
            int questionCount = questionDAO.getQuestionCountForQuiz(selectedQuestion.getQuiz());
            questionCountLabel.setText("Vragen in Quiz: " + questionCount);
        }
    }

    // terug naar menu
    public void doMenu(ActionEvent event) {
        Main.getSceneManager().showWelcomeScene();
    }

    public void doCreateQuestion() {
        Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            Main.getSceneManager().showCreateUpdateQuestionScene(null);
            warningLabel.setVisible(false);
        } else {
            warningLabel.setVisible(true);
            warningLabel.setText("Je moet eerst een vraag kiezen!");
        }
    }


    public void doUpdateQuestion(ActionEvent event) {
        Question question = questionList.getSelectionModel().getSelectedItem();
        Main.getSceneManager().showCreateUpdateQuestionScene(question);

    }


    public void doDeleteQuestion(ActionEvent event) {
        Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            // Show a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Bevestig Verwijderen");
            alert.setHeaderText("Weet je zeker dat je de vraag wilt verwijderen?");
            alert.setContentText("Vraag : " + selectedQuestion.getIdQuestion());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // User clicked OK, proceed with deletion
                questionDAO.deleteOne(selectedQuestion);
                questionList.getItems().remove(selectedQuestion);
                warningLabel.setVisible(false);
            }
        } else {
            warningLabel.setVisible(true);
            warningLabel.setText("Je moet eerst een vraag kiezen!");
        }
    }
}

