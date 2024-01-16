package controller;
// Tom van Beek, 500941521.

import database.mysql.CourseDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
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
    Label warningLabelNoCourse;
    @FXML
    Label warningLabelNoLevel;
    @FXML
    Label warningLabelNoFields;
    @FXML
    Label nameQuizLabel;
    @FXML
    Label amountQuestionsLabel;
    @FXML
    Label levelQuizLabel;

    @FXML
    ComboBox<String> levelsListComboBox;
    @FXML
    ComboBox<Course> coursesListComboBox;

    ObservableList<String> levelsList = FXCollections.observableArrayList("Beginner", "Medium", "Gevorderd");
    List<Course> courssList = quizDAO.getCoursesFromUser(User.getCurrentUser());
    ObservableList<Course> coursesList = FXCollections.observableArrayList(courssList);


    public CreateUpdateQuizController() {
        this.dbAccess = Main.getDBaccess();
    }

    //Quiz tonen om te wijzigen of leeg scherm voor het maken van een quiz
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

    // Quiz opslaan, met melding nieuwe of gewijzigde quiz
    public void doCreateUpdateQuiz(ActionEvent event) throws InterruptedException {
        Quiz quiz = createQuiz();
        String updateQuizAlert = "Quiz gewijzigd";
        String newQuizAlert = "Nieuwe quiz toegevoegd";
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
        boolean correctInput;
        String courseName = courseLabel.getText();
        Course course = courseDAO.getOneByName(courseName);
        if (courseName.equals("")) {
            course = coursesListComboBox.getSelectionModel().getSelectedItem();
        }
        String nameQuiz = nameTextField.getText();
        String level = levelsListComboBox.getValue();
        String amount = amountTextField.getText();
        isCorrectInputCourse(coursesListComboBox.getSelectionModel().getSelectedItem(), courseText);
        isCorrectInputLevel(level, levelQuizLabel);
        correctInput = isCorrectInput(nameQuiz, amount);
        if (course == null || !correctInput || level == null) {
            return null;
        } else {
            warningLabelNoFields.setVisible(false);
            warningLabelNoFields.setVisible(false);
            int amountQuestions = Integer.parseInt(amount);
            Quiz quiz = new Quiz(course, nameQuiz, level, amountQuestions);
            return quiz;
        }
    }

    private boolean isCorrectInput(String nameQuiz, String amountQuestions) {
        checkAndChangeLabelColor(nameQuiz.isEmpty(), nameQuizLabel);
        checkAndChangeLabelColor(amountQuestions.isEmpty(), amountQuestionsLabel);
        return !warningLabelNoFields.isVisible();
    }

    private void checkAndChangeLabelColor(boolean emptyTextField, Label label) {
        String errorMessageNoFields = "Je hebt niet alle velden ingevuld.\nVul de rood gekleurde velden alsnog in.";
        if (emptyTextField) {
            label.setTextFill(Color.RED);
            warningLabelNoFields.setText(errorMessageNoFields);
            warningLabelNoFields.setVisible(true);
        } else {
            warningLabelNoFields.setVisible(false);
        }
    }
    private void isCorrectInputCourse(Course course, Label label) {
        String errorMessageNoCourse = "Je hebt geen cursus gekozen voor de gebruiker.";
        if (course == null) {
            warningLabelNoCourse.setText(errorMessageNoCourse);
            warningLabelNoCourse.setVisible(true);
            label.setTextFill(Color.RED);

        } else {
            warningLabelNoCourse.setVisible(false);
        }
    }

    private void isCorrectInputLevel(String level, Label label) {
        String errorMessageNoLevel = "Je hebt geen level gekozen voor de gebruiker.";
        if (level == null) {
            warningLabelNoLevel.setText(errorMessageNoLevel);
            warningLabelNoLevel.setVisible(true);
            label.setTextFill(Color.RED);

        } else {
            warningLabelNoLevel.setVisible(false);
        }
    }
}