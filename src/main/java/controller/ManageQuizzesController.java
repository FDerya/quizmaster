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
    private final QuizDAO QUIZDAO;
    private final QuestionDAO QUESTIONDAO;
    private static File fileTXT = new File("src/main/java/database/saveQuizTXT.txt");



    public ManageQuizzesController() {
        this.QUIZDAO = new QuizDAO(Main.getDBaccess());
        this.QUESTIONDAO = new QuestionDAO(Main.getDBaccess());
    }

    // Quizlijst van coordinator (user) afdrukken in scherm
    public void setup() {
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

    public void doMenu(ActionEvent event) {Main.getSceneManager().showWelcomeScene();}

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

    // Quiz verwijderen uit de database én de Listview
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

    private void saveQuizToTXT(DBAccess dbAccess, List<Quiz> listQuiz, User user) {
        dbAccess.openConnection();
        if (!listQuiz.isEmpty()) {
            int amountQuiz = listQuiz.size();
            int amountQuestion = QUESTIONDAO.getQuestionCountForUser(user);
            double avgQuestion = (Math.round(amountQuestion * 10 / amountQuiz) / 10.0);
            try {
                printWriterQuiz(listQuiz, amountQuiz, amountQuestion, avgQuestion);
                showSaveAlert();

            } catch (FileNotFoundException fileNotFoundException) {
                System.out.println("File not found: " + fileNotFoundException);
            }
        }
    }

    private void printWriterQuiz(List<Quiz> listQuiz, int amountQuiz, int amountQuestion, double avgQuestion) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(fileTXT);
        printWriter.printf("%-30s %-30s %-15s %-10s\n", "Cursus", "Quiznaam", "Level", "Aantal vragen per quiz");
        for (Quiz quiz : listQuiz) {
            printWriter.printf("%-30s %-30s %-15s %-10d\n", quiz.getCourse(), quiz.getNameQuiz(), quiz.getLevel(), QUESTIONDAO.getQuestionCountForQuiz(quiz));
        }
        printWriter.println("\nEr zijn " + amountQuiz + " quizzen met " + amountQuestion + " vragen, gemiddelde = " + avgQuestion);
        printWriter.close();
    }
private void showSaveAlert(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

}
    /*public void fileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().
                addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                        new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                        new FileChooser.ExtensionFilter("All Files", "*.*"));
        Window mainStage = null;
        File selectedFile = fileChooser.showSaveDialog(mainStage);
        if (selectedFile != null) {
            mainStage.display(selectedFile);
        }
    }*/
}
