package controller;
// Tom van Beek, 500941521.

import database.mysql.QuizDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.*;
import view.Main;

import java.util.List;
import java.util.Optional;

public class ManageQuizzesController {
    @FXML
    ListView<Quiz> quizList;
    private final QuizDAO quizDAO;

    public ManageQuizzesController() {
        this.quizDAO = new QuizDAO(Main.getDBaccess());
    }

    // Quizlijst afdrukken in scherm
    public void setup() {
        User currentUser = User.getCurrentUser();
        List<Quiz> quizzen = quizDAO.getQuizzesFromUser(currentUser);
        quizList.getItems().addAll(quizzen);
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
                    aantal.setText(" " + item.getAmountQuestions() + " ");
                }
                setGraphic(hBox);
            }
        });
        quizList.getSelectionModel().getSelectedItem();
        quizList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldQuiz, newQuiz) ->
                        System.out.println("Geselecteerde quiz: " + observableValue + ", " + oldQuiz + ", " + newQuiz));

    }

    public void doMenu(ActionEvent event) {
        Main.getSceneManager().showWelcomeScene();
    }

    // Nieuwe quiz maken met leeg scherm
    public void doCreateQuiz(ActionEvent event) {
        Main.getSceneManager().showCreateUpdateQuizScene(null);}

    // Quiz bewerken met vooraf ingevuld scherm
    public void doUpdateQuiz(ActionEvent event) {
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        if (quiz == null) {
            showWarning();
        } else {
            Main.getSceneManager().showCreateUpdateQuizScene(quiz);
        }
    }

    // Waarschuwing in pop-up scherm weergeven
    private void showWarning() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Waarschuwing");
            alert.setHeaderText(null);
            alert.setContentText("Selecteer een quiz.");
            alert.showAndWait();
        });
    }

    //Waarschuwing in pop-up scherm weergeven voor verwijderen quiz
    private boolean confirmDeletion(Quiz selectedQuiz) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Verwijder quiz");
        alert.setHeaderText("Quiz `" + selectedQuiz.getNameQuiz() + "` wordt verwijderd.");
        alert.setContentText("Weet je het zeker?");
        ButtonType buttonTypeCancel = new ButtonType("Annuleer");
        ButtonType buttonTypeContinue = new ButtonType("Verwijder");
        alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeContinue);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeContinue;
    }

    // Quiz verwijderen uit de database én de Listview
    public void doDeleteQuiz(ActionEvent event) {
        Quiz selectedQuiz = quizList.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showWarning();
            return;
        }
        if (confirmDeletion(selectedQuiz)) {
            quizDAO.deleteQuiz(selectedQuiz);
            quizList.getItems().remove(selectedQuiz);
        }
    }
}