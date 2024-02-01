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
    @FXML
    ListView<Quiz> quizList;
    @FXML
    Label countLabel;
    private final QuizDAO quizDAO;
    private final QuestionDAO questionDAO;
    private static File fileTXT = new File("src/main/java/database/saveQuizTXT.txt");


    public ManageQuizzesController() {
        this.quizDAO = new QuizDAO(Main.getDBaccess());
        this.questionDAO = new QuestionDAO(Main.getDBaccess());
    }

    // Quizlijst van coordinator (user) afdrukken in scherm
    public void setup() {
        User currentUser = User.getCurrentUser();
        List<Quiz> quizzen = quizDAO.getQuizzesFromUser(currentUser);
        countLabel.setText("Gemiddeld aantal vragen per quiz: " + doCount(quizzen));
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
                    aantal.setText(" " + questionDAO.getQuestionCountForQuiz(item) + " ");
                }
                setGraphic(hBox);
            }
        });
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
        int count = quizzes.size();
        int amount = questionDAO.getQuestionCountForUser(User.getCurrentUser());
        double avg = 0;
        if (amount > 0) {
            avg = Math.round((amount * 10.0 / count)) / 10.0;
        } else {
            showEmpty("coordinator", "vragen gemaakt");
        }
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

    public void doWriteTXT(ActionEvent event) {
        User user = User.getCurrentUser();
        if (user.getRole().equals("Coördinator")) {
            List<Quiz> listQuiz = quizDAO.getQuizzesFromUser(user);
            saveQuizToTXT(Main.getDBaccess(), listQuiz, quizDAO, user);
        }else if(user.getRole().equals("Administrator")){
            //saveAllToTXT(Main.getDBaccess(), quizList);
        }
    }

    private void saveQuizToTXT(DBAccess dbAccess, List<Quiz> listQuiz, QuizDAO quizDAO, User user) {
        dbAccess.openConnection();
        if (!listQuiz.isEmpty()) {
            int amountQuiz = listQuiz.size();
            int amountQuestion = questionDAO.getQuestionCountForUser(user);
            double avgQuestion = (Math.round(amountQuestion * 10 / amountQuiz) / 10.0);
            try {
                PrintWriter printWriter = new PrintWriter(fileTXT);
                printWriter.printf("%-30s %-30s %-15s %-10s\n", "Cursus", "Quiznaam", "Level", "Aantal vragen per quiz");
                for (Quiz quiz : listQuiz) {
                    printWriter.printf("%-30s %-30s %-15s %-10d\n", quiz.getCourse(), quiz.getNameQuiz(), quiz.getLevel(), questionDAO.getQuestionCountForQuiz(quiz));
                }
                printWriter.println("\nEr zijn " + amountQuiz + " quizzen met " + amountQuestion + " vragen, gemiddelde = " + avgQuestion);
                printWriter.close();

            } catch (FileNotFoundException fileNotFoundException) {
                System.out.println("File not found: " + fileNotFoundException);
            }
        }
    }

}
