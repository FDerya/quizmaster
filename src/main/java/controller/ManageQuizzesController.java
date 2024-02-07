package controller;
// Tom van Beek, 500941521.

import database.mysql.DBAccess;
import database.mysql.QuestionDAO;
import database.mysql.QuizDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.*;
import view.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class ManageQuizzesController extends WarningAlertController {
    // Attributes
    @FXML
    ListView<Quiz> quizList;
    @FXML
    Button mainScreenButton;
    private final QuizDAO QUIZDAO;
    private final QuestionDAO QUESTIONDAO;
    private static File fileTXT = new File("src/main/java/database/saveQuizTXT.txt");


    public ManageQuizzesController() {
        this.QUIZDAO = new QuizDAO(Main.getDBaccess());
        this.QUESTIONDAO = new QuestionDAO(Main.getDBaccess());
    }

    // Setup Quizlist in screen for coordinator
    public void setup() {
        mainScreenButton.setText(Main.getMainScreenButtonText());
        User currentUser = User.getCurrentUser();
        List<Quiz> quizzen = QUIZDAO.getQuizzesFromUser(currentUser);
        quizList.getItems().addAll(quizzen);
        makeColumns();
        quizList.getSelectionModel().getSelectedItem();

    }

    private void makeColumns() {
        quizList.setCellFactory(param -> new ListCell<>() {
            @Override
            public void updateItem(Quiz item, boolean empty) {
                super.updateItem(item, empty);
                Label naam = new Label();
                naam.setPrefWidth(200.0);
                Label aantal = new Label();
                HBox hBox = new HBox(naam, aantal);
                if (!(item == null || empty)) {
                    naam.setText(item.getNameQuiz());
                    aantal.setText(" " + QUESTIONDAO.getQuestionCountForQuiz(item) + " ");
                }
                setGraphic(hBox);
            }
        });
    }

    public void doMenu(ActionEvent event) {
        Main.getSceneManager().showWelcomeScene();
    }

    // Go to an empty screen for a new quiz
    public void doCreateQuiz(ActionEvent event) {
        Main.getSceneManager().showCreateUpdateQuizScene(null);
    }

    // Change a quiz with a filled screen
    public void doUpdateQuiz(ActionEvent event) {
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        if (quiz == null) {
            setEmptyChoice("quiz", true);
        } else {
            Main.getSceneManager().showCreateUpdateQuizScene(quiz);
        }
    }

    // Delete a quiz from the database and quizList
    public void doDeleteQuiz(ActionEvent event) {
        Quiz selectedQuiz = quizList.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            setEmptyChoice("quiz", true);
            return;
        }
        if (confirmDeletion(selectedQuiz.getNameQuiz(), "Quiz")) {
            QUIZDAO.deleteQuiz(selectedQuiz);
            quizList.getItems().remove(selectedQuiz);
        }
    }
}