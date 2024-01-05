package controller;

import database.mysql.QuizDAO;
import database.mysql.DBAccess;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import model.*;
import view.Main;

import java.util.List;

public class ManageQuizzesController {
    private final QuizDAO quizDAO;
    @FXML
    ListView<Quiz> quizList;

    private final DBAccess dbAccess;
    @FXML
    TextField waarschuwingsTextField;


    public ManageQuizzesController() {
        this.dbAccess = Main.getDBaccess();
        this.quizDAO = new QuizDAO(Main.getDBaccess());
    }

    // Quizlijst afdrukken in scherm
    public void setup() {
        List<Quiz> quizzen = quizDAO.getAll();
        quizList.getItems().addAll(quizzen);
        quizList.getSelectionModel().getSelectedItem();
        quizList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldQuiz, newQuiz) ->
                        System.out.println("Geselecteerde quiz: " + observableValue + ", " + oldQuiz + ", " + newQuiz));

    }

    public void doMenu(ActionEvent event) {
        Main.getSceneManager().showWelcomeScene();
    }


    public void doCreateQuiz(ActionEvent event) {
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        if (quiz == (null)) {
            waarschuwingsTextField.setVisible(true);
            waarschuwingsTextField.setText("Je moet eerst een quiz kiezen");
        } else {
            Main.getSceneManager().showCreateUpdateQuizScene(quiz);

        }
    }

    public void doUpdateQuiz(ActionEvent event) {
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        if (quiz == null) {
            waarschuwingsTextField.setVisible(true);
            waarschuwingsTextField.setText("Je moet eerst een quiz kiezen");
        } else {
            Main.getSceneManager().showCreateUpdateQuizScene(quiz);
        }
    }

    public void doDeleteQuiz(ActionEvent event) {
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        if (quiz == null) {
            waarschuwingsTextField.setVisible(true);
            waarschuwingsTextField.setText("Je moet eerst een quiz kiezen");
        } else {
            quizDAO.deleteQuiz(quiz);
            quizList.getItems().remove(quiz);
        }
    }
}