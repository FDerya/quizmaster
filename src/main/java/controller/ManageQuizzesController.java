package controller;

import database.mysql.QuizDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import model.*;
import view.Main;

import java.util.List;

public class ManageQuizzesController {
    @FXML
    ListView<Quiz> quizList;
    private final QuizDAO quizDAO;
    @FXML
    TextField waarschuwingsTextField;


    public ManageQuizzesController() {
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
                    aantal.setText("`" + item.getAmountQuestions() + "`");
                }
                setGraphic(hBox);
            }
        });
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