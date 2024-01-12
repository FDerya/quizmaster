package controller;
// Tom van Beek, 500941521.

import database.mysql.CourseDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import model.*;
import database.mysql.DBAccess;
import database.mysql.QuizDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import view.Main;

import java.util.ArrayList;
import java.util.List;

public class CreateUpdateQuizController {
    private final DBAccess dbAccess;
    CourseDAO courseDAO = new CourseDAO(Main.getDBaccess());
    @FXML
    private TextField nameTextField;
    @FXML
    TextField amountTextField;
    @FXML
    private Label titelLabel;
    @FXML
    private Label courseLabel;
    @FXML
    private Label courseText;
    @FXML
    ComboBox<String> levelsListComboBox;
    @FXML
    ComboBox<Course> coursesListComboBox;

    ObservableList<String> levelsList = FXCollections.observableArrayList("Beginner", "Medium", "Gevorderd");
    List<Course> courssList = courseDAO.getAll();
    ObservableList<Course> coursesList = FXCollections.observableArrayList(courssList);


    public CreateUpdateQuizController() {
        this.dbAccess = Main.getDBaccess();
    }

    public void setup(Quiz quizOne) {
        levelsListComboBox.setItems(levelsList);
        coursesListComboBox.setItems(coursesList);
        titelLabel.setText("Maak nieuwe Quiz");
        if (quizOne != null) {
            titelLabel.setText("Wijzig Quiz");
            courseLabel.setText(quizOne.getCourse().getNameCourse());
            nameTextField.setText(quizOne.getNameQuiz());
            levelsListComboBox.setValue(quizOne.getLevel());
            amountTextField.setText(String.valueOf(quizOne.getAmountQuestions()));
            amountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    amountTextField.setText(oldValue);
                }
            });
        } else {
            courseText.setText("Naam van de cursus");
            courseText.setVisible(true);
            coursesListComboBox.setVisible(true);
        }
    }

    public void doMenuBack(ActionEvent event) {
        Main.getSceneManager().showManageQuizScene();
    }

    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }

    public void doCreateUpdateQuiz(ActionEvent event) {
        QuizDAO quizDAO = new QuizDAO(dbAccess);
        courseDAO = new CourseDAO(dbAccess);
        Course course = coursesListComboBox.getSelectionModel().getSelectedItem();
        String nameQuiz = nameTextField.getText();
        String level = levelsListComboBox.getValue();
        String amount = amountTextField.getText();
        int amountQuestions = Integer.parseInt(amount);
        Quiz quiz = new Quiz(course, nameQuiz, level, amountQuestions);
        quizDAO.storeOne(quiz);
    }
}