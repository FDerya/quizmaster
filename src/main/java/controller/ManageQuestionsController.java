package controller;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.QuestionDAO;
import database.mysql.QuizDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.Question;
import model.Quiz;
import model.User;
import view.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

public class ManageQuestionsController {

    private final QuestionDAO questionDAO;
    private final CourseDAO courseDAO;

    @FXML
    ListView<Question> questionList;
    @FXML
    Label warningLabel;
    @FXML
    Label questionCountLabel;
    private Question question;

    private static File fileTXT = new File("src/main/java/database/saveQuestionTXT.txt");

    public ManageQuestionsController() {
        this.questionDAO = new QuestionDAO(Main.getDBaccess());
        this.courseDAO = new CourseDAO(Main.getDBaccess());
    }

    public void setup() {
        // Gets the current user
        User currentUser = User.getCurrentUser();
        int userId = currentUser.getIdUser();

        // Gets the current user's questions
        List<Question> userQuestions = questionDAO.getQuestionsForUser(userId);

        // Tracking changes when occuring
        if (!userQuestions.isEmpty()) {
            ObservableList<Question> questionObservableList = FXCollections.observableArrayList(userQuestions);
            questionList.setItems(questionObservableList);

            // Set the selection mode to SINGLE
            questionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            // Added a HBOX to create a column between quiz name and question
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
            // How many questions in a quiz
            questionList.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> updateQuestionCount(newValue)
            );
        }
        // Clear the selection after populating the items
        questionList.getSelectionModel().clearSelection();
    }

    // How many questions in a quiz
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

    // Creates a new question
    public void doCreateQuestion() {
        Main.getSceneManager().showCreateUpdateQuestionScene(null);
        warningLabel.setVisible(false);
    }

    public void doUpdateQuestion(ActionEvent event) {
        // Gets the selected question
        Question question = questionList.getSelectionModel().getSelectedItem();
        if (question == null) {
            warningLabel.setVisible(true);
            warningLabel.setText("Je moet eerst een vraag kiezen!");
            warningLabel.setStyle("-fx-text-fill: red;");
        } else {
            Main.getSceneManager().showCreateUpdateQuestionScene(question);
            warningLabel.setVisible(false);
        }
    }

    public void doDeleteQuestion(ActionEvent event) {
        // Gets the selected question
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
            warningLabel.setStyle("-fx-text-fill: red;");
        }

    }

    public void doWriteTXT(ActionEvent event) {
        User user = User.getCurrentUser();
        if (user.getRole().equals("Coördinator")) {
            List<Question> listQuestion = questionDAO.getQuestionsForUser(user.getIdUser());
            saveQuestionToTXT(Main.getDBaccess(), listQuestion, questionDAO, user);
        }else if(user.getRole().equals("Administrator")){

        }
    }
    private void saveQuestionToTXT(DBAccess dbAccess, List<Question> listQuestion, QuestionDAO questionDAO, User user) {
        dbAccess.openConnection();
        try {
            PrintWriter printWriter = new PrintWriter(fileTXT);
            printWriter.printf("%-30s %-30s %-15s %-15s %-15s %-15s\n",
                    "Quiz", "Question", "AnswerRight", "AnswerWrong1", "AnswerWrong2", "AnswerWrong3");

            for (Question question : listQuestion) {
                printWriter.printf("%-30s %-30s %-15s %-15s %-15s %-15s\n",
                        question.getQuiz().getNameQuiz(), question.getQuestion(),
                        question.getAnswerRight(), question.getAnswerWrong1(),
                        question.getAnswerWrong2(), question.getAnswerWrong3());
            }

            printWriter.close();
            System.out.println("Questions saved to " + fileTXT);

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }

    public void doDashboard(ActionEvent event) {
        Main.getSceneManager().showCoordinatorDashboard();
    }
}


