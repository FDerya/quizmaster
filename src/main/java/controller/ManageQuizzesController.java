package controller;
// Tom van Beek, 500941521.

import database.mysql.QuestionDAO;
import database.mysql.QuizDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.*;
import view.Main;

import java.util.List;

public class ManageQuizzesController extends WarningAlertController {
    @FXML
    ListView<Quiz> quizList;
    @FXML
    Label countLabel;
    private final QuizDAO quizDAO;
    private final QuestionDAO questionDAO;

    public ManageQuizzesController() {
        this.quizDAO = new QuizDAO(Main.getDBaccess());
        this.questionDAO = new QuestionDAO(Main.getDBaccess());
    }

    // Quizlijst van coordinator (user) afdrukken in scherm
    public void setup() {
        User currentUser = User.getCurrentUser();
        List<Quiz> quizzen = quizDAO.getQuizzesFromUser(currentUser);
        countLabel.setText("Gemiddeld aantal vragen per quiz: "+ doCount(quizzen));
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
                    aantal.setText(" " + questionDAO.getQuestionCountForQuiz(item) + " ");
                }
                setGraphic(hBox);
            }
        });
        quizList.getSelectionModel().getSelectedItem();

    }

    public void doMenu(ActionEvent event) {
        Main.getSceneManager().showWelcomeScene();
    }

    // Nieuwe quiz maken met leeg scherm
    public void doCreateQuiz(ActionEvent event) {
        Main.getSceneManager().showCreateUpdateQuizScene(null);
    }

    // Quiz bewerken met vooraf ingevuld scherm
    public void doUpdateQuiz(ActionEvent event) {
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        if (quiz == null) {
            setEmptyChoice("quiz", true);
        } else {
            Main.getSceneManager().showCreateUpdateQuizScene(quiz);
        }
    }

    private double doCount(List<Quiz> quizzes) {
        int count = 0;
        int amount = 0;
        for (int i = 0; i < quizzes.size(); i++) {
            amount += quizzes.get(i).getAmountQuestions();
            count++;
        }
        double avg = (amount*10 / count)/10.0;
        return avg;
    }

    // Quiz verwijderen uit de database én de Listview
    public void doDeleteQuiz(ActionEvent event) {
        Quiz selectedQuiz = quizList.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            setEmptyChoice("quiz", true);
            return;
        }
        if (confirmDeletion(selectedQuiz.getNameQuiz(), "Quiz")) {
            quizDAO.deleteQuiz(selectedQuiz);
            quizList.getItems().remove(selectedQuiz);
        }
    }
}