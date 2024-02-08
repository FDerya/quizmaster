package controller;

import database.mysql.QuestionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.Question;
import model.User;
import view.Main;
import java.util.List;
import java.util.Optional;

public class ManageQuestionsController {

    QuestionDAO questionDAO;
    @FXML
    ListView<Question> questionList;
    @FXML
    Label warningLabel;
    @FXML
    Label questionCountLabel;
    @FXML
    Button idBeginscherm;

    public ManageQuestionsController() {
        this.questionDAO = new QuestionDAO(Main.getDBaccess());

    }
    // Method that initializes the controller; receives the current user's questions and creates a visual list if there are questions
    // sets the mode for selecting a single item, adds listeners to update the number of questions, and Cleans the selection after the elements are filled.
    public void setup() {
        User currentUser = User.getCurrentUser();

        if (currentUser != null) {
            int userId = currentUser.getIdUser();
            idBeginscherm.setText(Main.getMainScreenButtonText());

            List<Question> userQuestions = questionDAO.getQuestionsForUser(userId);

            if (!userQuestions.isEmpty()) {
                ObservableList<Question> questionObservableList = FXCollections.observableArrayList(userQuestions);
                questionList.setItems(questionObservableList);

                questionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

                create_HBOX();


                questionList.getSelectionModel().selectedItemProperty().addListener(
                        (observable, oldValue, newValue) -> updateQuestionCount(newValue)
                );
            }
            questionList.getSelectionModel().clearSelection();
        }

    }

    // Added a HBOX to create a column between quiz name and question
    private void create_HBOX() {
        questionList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    HBox hbox = new HBox(10);

                    Label quizLabel = new Label(item.getQuiz().getNameQuiz() + " :");
                    Label questionLabel = new Label(item.getQuestion());

                    hbox.getChildren().addAll(quizLabel, questionLabel);

                    setGraphic(hbox);
                }
            }
        });
    }

    // How many questions in a quiz
    void updateQuestionCount(Question selectedQuestion) {
        if (selectedQuestion != null) {
            int questionCount = questionDAO.getQuestionCountForQuiz(selectedQuestion.getQuiz());
            questionCountLabel.setText("Vragen in Quiz: " + questionCount);
        }
    }

    // terug naar menu
    public void doMenu(ActionEvent event) {
        Main.getSceneManager().showWelcomeScene();
    }

    // Creates a new question (Button)
    public void doCreateQuestion() {
        Main.getSceneManager().showCreateUpdateQuestionScene(null);
        warningLabel.setVisible(false);
    }

    // Updates the question
    public void doUpdateQuestion(ActionEvent event) {
        // Gets the selected question
        Question question = questionList.getSelectionModel().getSelectedItem();
        if (question == null) {
            warningLabel.setVisible(true);
            warningLabel.setText("Je moet eerst een vraag kiezen!");
        } else {
            Main.getSceneManager().showCreateUpdateQuestionScene(question);
            warningLabel.setVisible(false);
        }
    }

    // Deletes the question
    public void doDeleteQuestion(ActionEvent event) {
        // Gets the selected question
        Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            show_delete_confirmation(selectedQuestion);
        } else {
            warningLabel.setVisible(true);
            warningLabel.setText("Je moet eerst een vraag kiezen!");
        }

    }

    private void show_delete_confirmation(Question selectedQuestion) {
        // Show a confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bevestig Verwijderen");
        alert.setHeaderText("Weet je zeker dat je de vraag wilt verwijderen?");

        // Get the buttons
        ButtonType okButton = new ButtonType("Verwijderen", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeButton = new ButtonType("Annuleren", ButtonBar.ButtonData.CANCEL_CLOSE);

        // Set the buttons
        alert.getButtonTypes().setAll(okButton, closeButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User clicked OK, proceed with deletion
            questionDAO.deleteOne(selectedQuestion);
            questionList.getItems().remove(selectedQuestion);
            warningLabel.setVisible(false);
        }
    }

    public void doDashboard(ActionEvent event) {
        Main.getSceneManager().showCoordinatorDashboard();
    }
}


