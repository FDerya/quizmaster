package controller;
// Tom van Beek, 500941521.

import database.mysql.CourseDAO;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
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
    Label quizSaveLabel;

    @FXML
    ComboBox<String> levelsListComboBox;
    @FXML
    ComboBox<Course> coursesListComboBox;

    ObservableList<String> levelsList = FXCollections.observableArrayList("Beginner", "Medium", "Gevorderd");
    List<Course> courssList = courseDAO.getAllByIdUser(User.getCurrentUser().getIdUser());
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

    // Menu terug gaan naar ManageQuiz
    public void doMenuBack(ActionEvent event) {
        Main.getSceneManager().showManageQuizScene();
    }

    // Naar hoofdmenu
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }

    // Quiz opslaan, met melding nieuwe of gewijzigde quiz
    public void doCreateUpdateQuiz(ActionEvent event) throws InterruptedException {
        Quiz quiz = createQuiz();
        if (quiz != null) {
            if (titelLabel.getText().equals("Maak nieuwe Quiz")) {
                newQuiz(quiz);
            } else {
                updateQuiz(quiz);
            }
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.millis(2000),
                    ae -> Main.getSceneManager().showManageQuizScene()));
            timeline.play();



        }

    }

    private void newQuiz(Quiz quiz) throws InterruptedException {
        String newQuizAlert = "Nieuwe quiz is toegevoegd";
        quizSaveLabel.setText(newQuizAlert);
        quizSaveLabel.setVisible(true);
        quiz.setIdQuiz(idQuiz);
        quizDAO.storeOne(quiz);
    }

    private void updateQuiz(Quiz quiz) throws InterruptedException {
        String updateQuizAlert = "Quiz is gewijzigd";
        quizSaveLabel.setText(updateQuizAlert);
        quizSaveLabel.setVisible(true);
        quizDAO.updateOne(quiz);
    }

    // Alert weergeven met vertraging
    private static void showAlert(String message) throws InterruptedException {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setContentText(message);
        Main.getSceneManager().showManageQuizScene();
    }

    // Quiz maken om op te kunnen slaan en checken of alles is ingevuld
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
            warningLabelNoFields.setVisible(!correctInput);
            return null;
        } else {
            warningLabelNoFields.setVisible(false);
            int amountQuestions = Integer.parseInt(amount);
            Quiz quiz = new Quiz(course, nameQuiz, level, amountQuestions);
            return quiz;
        }
    }

    // Check of textvelden zijn ingevuld
    private boolean isCorrectInput(String nameQuiz, String amountQuestions) {
        checkAndChangeLabelColor(nameQuiz.isEmpty(), nameQuizLabel);
        checkAndChangeLabelColor(amountQuestions.isEmpty(), amountQuestionsLabel);
        return (!nameQuiz.isEmpty() && !amountQuestions.isEmpty());
    }

    // Niet-ingevulde velden rood markeren en melding tonen
    private void checkAndChangeLabelColor(boolean emptyfields, Label label) {
        if (emptyfields) {
            label.setTextFill(Color.RED);
        } else {
            label.setTextFill(Color.BLACK);
        }
    }

    // Melding tonen en tekst rood kleuren wanneer geen cursus is gekozen
    private void isCorrectInputCourse(Course course, Label label) {
        String errorMessageNoCourse = "Je hebt geen cursus gekozen voor de gebruiker.";
        if (course == null) {
            if (courseLabel.getText().equals("")) {
                warningLabelNoCourse.setText(errorMessageNoCourse);
                warningLabelNoCourse.setVisible(true);
                label.setTextFill(Color.RED);

            } else {
                warningLabelNoCourse.setVisible(false);
                label.setTextFill(Color.BLACK);
            }
        }
    }
// Melding tonen en tekst rood kleuren wanneer geen level is gekozen

    private void isCorrectInputLevel(String level, Label label) {
        String errorMessageNoLevel = "Je hebt geen level gekozen voor de gebruiker.";
        if (level == null) {
            warningLabelNoLevel.setText(errorMessageNoLevel);
            warningLabelNoLevel.setVisible(true);
            label.setTextFill(Color.RED);

        } else {
            warningLabelNoLevel.setVisible(false);
            label.setTextFill(Color.BLACK);
        }
    }
}