package controller;
// Tom van Beek, 500941521.

import database.mysql.CourseDAO;
import javafx.animation.KeyFrame;
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

public class CreateUpdateQuizController extends WarningAlertController {
    private final DBAccess dbAccess;
    private int idQuiz;
    CourseDAO courseDAO = new CourseDAO(Main.getDBaccess());
    private final QuizDAO QUIZDAO = new QuizDAO(Main.getDBaccess());
    @FXML
    private TextField nameTextField;
    @FXML
    TextField amountTextField;
    @FXML
    private Label titleLabel;
    @FXML
    private Label courseLabel;
    @FXML
    private Label courseText;
    @FXML
    Label nameQuizLabel;
    @FXML
    Label amountQuestionsLabel;
    @FXML
    Button mainScreenButton;
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
        mainScreenButton.setText(Main.getMainScreenButtonText());
        levelsListComboBox.setItems(levelsList);
        coursesListComboBox.setItems(coursesList);
        titleLabel.setText("Nieuwe quiz");
        setupInput();
        if (quizOne != null) {
            idQuiz = quizOne.getIdQuiz();
            titleLabel.setText("Wijzig quiz");
            courseLabel.setText(quizOne.getCourse().getNameCourse());
            nameTextField.setText(quizOne.getNameQuiz());
            levelsListComboBox.setValue(quizOne.getLevel());
            amountTextField.setText(String.valueOf(quizOne.getMinimumAmountCorrectQuestions()));
        } else {
            courseText.setText("Naam cursus");
            courseLabel.setVisible(false);
            courseText.setVisible(true);
            coursesListComboBox.setVisible(true);
        }
    }

    private void setupInput() {
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (nameTextField.getText().length() > MAXLENGTH) {
                String s = nameTextField.getText(0, MAXLENGTH);
                nameTextField.setText(s);
            }
        });
        amountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                amountTextField.setText(oldValue);
            }
        });
    }

    // Menu terug gaan naar ManageQuiz
    public void doMenuBack(ActionEvent event) {
        Main.getSceneManager().showManageQuizScene();
    }

    // Naar hoofdmenu
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }

    // Naar CoordinatorDashboard
    public void doDashboard() {
        Main.getSceneManager().showCoordinatorDashboard();
    }

    // Quiz opslaan, met melding nieuwe of gewijzigde quiz
    public void doCreateUpdateQuiz(ActionEvent event) {
        Quiz quiz = createQuiz();
        if (quiz != null) {
            if (checkDuplicate(quiz)) {
                showSame(true, "quiz");
            } else {
                if (titleLabel.getText().equals("Wijzig quiz")) {
                    updateQuiz(quiz);
                } else if (titleLabel.getText().equals("Nieuwe quiz")) {
                    newQuiz(quiz);
                }
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(2000),
                        ae -> Main.getSceneManager().showManageQuizScene()));
                timeline.play();
            }
        }
    }

    private boolean checkDuplicate(Quiz nameQuiz) {
        if (titleLabel.getText().equals("Wijzig quiz")) {
            nameQuiz.setIdQuiz(idQuiz);
        }
        boolean showDuplicate = false;
        List<Quiz> quizNamen = QUIZDAO.getAll();
        for (Quiz naamquiz : quizNamen) {
            if (nameQuiz.getNameQuiz().equals(naamquiz.getNameQuiz()) && (nameQuiz.getIdQuiz() != naamquiz.getIdQuiz())) {
                checkAndChangeLabelColor(true, nameQuizLabel);
                showDuplicate = true;
            } else showSame(false, "quiz");
        }
        return showDuplicate;
    }


    private void newQuiz(Quiz quiz) {
        showSaved(quiz.getNameQuiz());
        QUIZDAO.storeOne(quiz);
    }

    private void updateQuiz(Quiz quiz) {
        showUpdated(quiz.getNameQuiz());
        quiz.setIdQuiz(idQuiz);
        QUIZDAO.updateOne(quiz);
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
        String minimumAmount = amountTextField.getText();
        isCorrectInputCourse(coursesListComboBox.getSelectionModel().getSelectedItem(), courseText);
        isCorrectInputComboBox(level);
        correctInput = isCorrectInput(nameQuiz, minimumAmount);
        if (course == null || !correctInput || level == null) {
            showWarningLabel(true);
            return null;
        } else {
            showWarningLabel(false);
            int minimumAmountCorrectQuestions = Integer.parseInt(minimumAmount);
            Quiz newQuiz = new Quiz(course, nameQuiz, level, minimumAmountCorrectQuestions);
            return newQuiz;
        }
    }


    // Check of textvelden zijn ingevuld
    private boolean isCorrectInput(String nameQuiz, String amountQuestions) {
        checkAndChangeLabelColor(nameQuiz.isEmpty(), nameQuizLabel);
        checkAndChangeLabelColor(amountQuestions.isEmpty(), amountQuestionsLabel);
        return (!amountQuestions.isEmpty() && !nameQuiz.isEmpty());
    }

    // Melding tonen en tekst rood kleuren wanneer geen cursus is gekozen
    private void isCorrectInputCourse(Course course, Label label) {
        if (course == null) {
            if (courseLabel.getText().equals("")) {
                label.setTextFill(Color.RED);
            }
        } else {
            label.setTextFill(Color.BLACK);
        }
    }
}