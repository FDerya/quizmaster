package controller;

import database.mysql.DBAccess;
import database.mysql.QuizDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Quiz;
import view.Main;

public class CreateUpdateQuizController {
    @FXML
    ListView<Quiz> quizList;
    @FXML
    TextField waarschuwingsTextField;

    private final DBAccess dbAccess;

    public CreateUpdateQuizController(){this.dbAccess = Main.getDBaccess();}
    public void setup(Quiz quizOne) {
       Quiz printQuiz = quizOne;
       quizList.getItems().add(printQuiz);
    }
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }

    public void doCreateUpdateQuiz() {

    }
}