package controller;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.QuizDAO;
import database.mysql.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import model.*;
import view.Main;

import java.util.List;

public class CreateUpdateQuizController {
    @FXML
    ListView<Quiz> quizList;
    @FXML
    TextField waarschuwingsTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    ListView<Course> courseList;

    @FXML
    private MenuButton taskMenuButton;
    @FXML
    private MenuButton taskMenuButton1;


    private final DBAccess dbAccess;

    public CreateUpdateQuizController() {
        this.dbAccess = Main.getDBaccess();
    }

    public void setup(Quiz quizOne) {
        QuizDAO quizDAO = new QuizDAO(dbAccess);
        MenuItem amount1 = new MenuItem("1");
        amount1.setOnAction(actionEvent -> quizOne.setAmountQuestions(1));
        MenuItem amount2 = new MenuItem("2");
        amount2.setOnAction(actionEvent -> quizOne.setAmountQuestions(2));
        MenuItem amount3 = new MenuItem("3");
        amount3.setOnAction(actionEvent -> quizOne.setAmountQuestions(3));
        MenuItem amount4 = new MenuItem("4");
        amount4.setOnAction(actionEvent -> quizOne.setAmountQuestions(4));
        MenuItem amount5 = new MenuItem("5");
        amount5.setOnAction(actionEvent -> quizOne.setAmountQuestions(5));
        MenuItem amount6 = new MenuItem("6");
        amount6.setOnAction(actionEvent -> quizOne.setAmountQuestions(6));
        MenuItem amount7 = new MenuItem("7");
        amount7.setOnAction(actionEvent -> quizOne.setAmountQuestions(7));

        taskMenuButton.getItems().add(amount1);
        taskMenuButton.getItems().add(amount2);
        taskMenuButton.getItems().add(amount3);
        taskMenuButton.getItems().add(amount4);
        taskMenuButton.getItems().add(amount5);
        taskMenuButton.getItems().add(amount6);
        taskMenuButton.getItems().add(amount7);

        MenuItem level1 = new MenuItem("Beginner");
        amount1.setOnAction(actionEvent -> quizOne.setLevel("Beginner"));
        MenuItem level2 = new MenuItem("Medium");
        amount2.setOnAction(actionEvent -> quizOne.setLevel("Medium"));
        MenuItem level3 = new MenuItem("Gevorderd");
        amount3.setOnAction(actionEvent -> quizOne.setLevel("Gevorderd"));
        taskMenuButton.getItems().add(level1);
        taskMenuButton.getItems().add(level2);
        taskMenuButton.getItems().add(level3);

        CourseDAO courseDAO = new CourseDAO(dbAccess, new UserDAO(dbAccess));
        List<Course> courses = courseDAO.getAll();
        courseList.getItems().addAll(courses.toString());
    }

    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }

    public void doCreateUpdateQuiz() {
        Quiz quiz = null;
        QuizDAO quizDAO = new QuizDAO(dbAccess);
        CourseDAO courseDAO = new CourseDAO(dbAccess, new UserDAO(dbAccess));
        Course course = courseList.getSelectionModel().getSelectedItem();
        String nameQuiz = nameTextField.getText();
        String level = taskMenuButton1.getText();
        int amountQuestions = quiz.getAmountQuestions();
        quiz = new Quiz(course, nameQuiz, level, amountQuestions);
        quizDAO.storeOne(quiz);
    }
}