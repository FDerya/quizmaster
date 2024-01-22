package controller;

import database.mysql.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
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
    private Label waarschuwingsLabel;
    private final DBAccess dbAccess;

    private final QuestionDAO questionDAO;
    private final CourseDAO courseDAO;
    private final UserDAO userDAO;
    private final QuizDAO quizDAO;

    public CoordinatorDashboardController() {
        this.userDAO = new UserDAO(Main.getDBaccess());
        this.dbAccess = Main.getDBaccess();
        this.quizDAO = new QuizDAO(Main.getDBaccess());
        this.questionDAO = new QuestionDAO(Main.getDBaccess());
        this.courseDAO = new CourseDAO(Main.getDBaccess());
    }

    public void setup() {
        List<Course> course = courseDAO.getAll();
        courseList.getItems().addAll(course);
        courseList.getSelectionModel().getSelectedItem();
        courseList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldCourse, newCourse) ->
                displayQuizForCourse(newCourse));

        // Vragen laden voor de initieel geselecteerde test
        Quiz initialSelectedQuiz = quizList.getSelectionModel().getSelectedItem();
        if (initialSelectedQuiz != null) {
            displayQuestionsForQuiz(initialSelectedQuiz);
        }

        quizList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldQuiz, newQuiz) ->
                       displayQuestionsForQuiz(newQuiz));
    }

    private void displayQuizForCourse(Course initialSelectedCourse) {
        // Verwijder bestaande vragen
        questionList.getItems().clear();
        quizList.getItems().clear();

        // Vragen ophalen en tonen voor de geselecteerde test
        if (initialSelectedCourse != null) {
            List<Quiz> quiz = questionDAO.getQuizForCourse(initialSelectedCourse);
            quizList.getItems().addAll(quiz);
        }
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
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        if (quiz == null) {
            Main.getSceneManager().showCreateUpdateQuizScene(null);
        } else {
            Main.getSceneManager().showCreateUpdateQuizScene(quiz);
        }
    }

    public void doEditQuiz() {
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        if (quiz == null) {
            waarschuwingsLabel.setVisible(true);
            waarschuwingsLabel.setText("Je moet eerst een quiz kiezen");
        } else {
            Main.getSceneManager().showCreateUpdateQuizScene(quiz);
        }
    }

    public void doNewQuestion() {
        Question question = questionList.getSelectionModel().getSelectedItem();
        if (question == null) {
            waarschuwingsLabel.setText("Je moet eerst een question kiezen");
            waarschuwingsLabel.setVisible(true);

        } else {
            Main.getSceneManager().showCreateUpdateQuestionScene(question);
        }

    }

    public void doEditQuestion() {

        Question question = questionList.getSelectionModel().getSelectedItem();
        if (question == null) {
            waarschuwingsLabel.setText("Je moet eerst een question kiezen");
            waarschuwingsLabel.setVisible(true);

        } else {
            Main.getSceneManager().showCreateUpdateQuestionScene(question);
        }
    }


    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }


}
