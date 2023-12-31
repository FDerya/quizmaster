package controller;

import database.mysql.QuizDAO;
import database.mysql.DBAccess;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.*;
import view.Main;


import java.util.List;
import java.util.ArrayList;

public class ManageQuizzesController {
    @FXML
    ListView<Quiz> quizList;
    @FXML
    TextField waarschuwingsTextField;
    private final DBAccess dbAccess;


    public ManageQuizzesController() {
        this.dbAccess = Main.getDBaccess();
    }


    public void setup() {
        QuizDAO quizDAO = new QuizDAO(dbAccess);
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

    public void doCreateQuiz() {
        //     QuizDAO quizDAO = new QuizDAO();
        //     quizDAO.storeOne(new Quiz());

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
        QuizDAO quizDAO = new QuizDAO(dbAccess);
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        quizDAO.deleteQuiz(quiz);
    }
}