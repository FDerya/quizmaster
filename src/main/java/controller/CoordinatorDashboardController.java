package controller;

import database.mysql.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.*;
import view.Main;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorDashboardController {

    @FXML
    private ListView<Course> courseList;
    @FXML
    private ListView<Quiz> quizList;
    @FXML
    private ListView<Question> questionList;
    @FXML
    private TextField waarschuwingsTextField;
    private final DBAccess dbAccess;

    private final QuestionDAO questionDAO;

    public CoordinatorDashboardController() {
        this.userDAO = new UserDAO(Main.getDBaccess());
        this.dbAccess = Main.getDBaccess();
        this.quizDAO = new QuizDAO(Main.getDBaccess());
        this.questionDAO = new QuestionDAO(Main.getDBaccess());
    }


    private final UserDAO userDAO;
    private final QuizDAO quizDAO;


    public void setup() {
        courseList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldCourse, newCourse) ->
                        System.out.println("Geselecteerde cursus: " + observableValue + ", " + oldCourse + ", " + newCourse));

        List<Quiz> quizzen = quizDAO.getAll();
        quizList.getItems().addAll(quizzen);
        quizList.getSelectionModel().getSelectedItem();
        quizList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldQuiz, newQuiz) -> {
                    System.out.println("Geselecteerde quiz: " + observableValue + ", " + oldQuiz + ", " + newQuiz);
                    displayQuestionsForQuiz(newQuiz);
                });



        // Vragen laden voor de initieel geselecteerde test
        Quiz initialSelectedQuiz = quizList.getSelectionModel().getSelectedItem();
        if (initialSelectedQuiz != null) {
            displayQuestionsForQuiz(initialSelectedQuiz);
        }

        List<Question> question = questionDAO.getAll();
        questionList.getItems().addAll(question);
        questionList.getSelectionModel().getSelectedItem();
        questionList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldQuestion, newQuestion) ->
                        System.out.println("Geselecteerde question: " + observableValue + ", " + oldQuestion + ", " + newQuestion));
    }

    private void displayQuestionsForQuiz(Quiz quiz) {
        // Verwijder bestaande vragen
        questionList.getItems().clear();

        // Vragen ophalen en tonen voor de geselecteerde test
        if (quiz != null) {
            List<Question> questions = questionDAO.getQuestionsForQuiz(quiz);
            questionList.getItems().addAll(questions);
        }
    }

    public void doNewQuiz() {
        Quiz quiz = null;
        Main.getSceneManager().showCreateUpdateQuizScene(quiz);
    }

    public void doEditQuiz() {
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        if (quiz == null) {
            waarschuwingsTextField.setVisible(true);
            waarschuwingsTextField.setText("Je moet eerst een quiz kiezen");
        } else {
            Main.getSceneManager().showCreateUpdateQuizScene(quiz);
        }
    }

    public void doNewQuestion() {
        Question question = questionList.getSelectionModel().getSelectedItem();
        if (question == null) {
            waarschuwingsTextField.setText("Je moet eerst een question kiezen");
            waarschuwingsTextField.setVisible(true);

        } else {
            Main.getSceneManager().showCreateUpdateQuestionScene(question);
        }

    }

    public void doEditQuestion() {

        Question question = questionList.getSelectionModel().getSelectedItem();
        if (question == null) {
            waarschuwingsTextField.setText("Je moet eerst een question kiezen");
            waarschuwingsTextField.setVisible(true);

        } else {
            Main.getSceneManager().showCreateUpdateQuestionScene(question);
        }
    }


    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }


}
