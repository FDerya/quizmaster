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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CreateUpdateQuizController {
    private final DBAccess dbAccess;

    private int idQuiz;
    CourseDAO courseDAO = new CourseDAO(Main.getDBaccess());
    private QuizDAO quizDAO = new QuizDAO(Main.getDBaccess());
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
    Label warningLabel;

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
            idQuiz = quizOne.getIdQuiz();
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
        Quiz quiz = createQuiz();
        String updateQuizAlert = "Quiz gewijzigd";
        String newQuizAlert = "Quiz toegevoegd";
        if (quiz != null) {
            if (titelLabel.getText().equals("Maak nieuwe Quiz")) {
                quizDAO.storeOne(quiz);
                showAlert(newQuizAlert);
            } else {
                quiz.setIdQuiz(idQuiz);
                quizDAO.updateOne(quiz);
                showAlert(updateQuizAlert);
            }
        }
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Main.getSceneManager().showManageQuizScene();
        }
    }

    public Quiz createQuiz() {
        String error = "Je hebt niet alle velden ingevuld.\nAlle velden zijn verplicht.";
        String courseName = courseLabel.getText();
        Course course = courseDAO.getOneByName(courseName);
        if (courseName.equals("")) {
            course = coursesListComboBox.getSelectionModel().getSelectedItem();
        }
        String nameQuiz = nameTextField.getText();
        String level = levelsListComboBox.getValue();
        String amount = amountTextField.getText();
        if (course == null || Objects.equals(nameQuiz, "") || level == null || amount.equals("")) {
            warningLabel.setText(error);
            warningLabel.setVisible(true);
            return null;
        } else {
            int amountQuestions = Integer.parseInt(amount);
            Quiz quiz = new Quiz(course, nameQuiz, level, amountQuestions);
            return quiz;
        }
    }
}