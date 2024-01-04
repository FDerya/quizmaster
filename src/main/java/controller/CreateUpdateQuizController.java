package controller;

import javafx.event.ActionEvent;
import model.*;
import database.mysql.DBAccess;
import database.mysql.QuizDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import view.Main;

public class CreateUpdateQuizController {
    @FXML
    ListView<Quiz> quizList;
    @FXML
    TextField waarschuwingsTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField courseTextField;
    @FXML
    TextField amountTextField;
    @FXML
    private Label titelLabel;
    @FXML
    private TextField levelTextField;
    @FXML
    ListView<Course> courseList;

    private final DBAccess dbAccess;

    public CreateUpdateQuizController() {
        this.dbAccess = Main.getDBaccess();
    }

    public void setup(Quiz quizOne) {
        titelLabel.setText("Wijzig Quiz");
        courseTextField.setText(String.valueOf(quizOne.getCourse().getNameCourse()));
        nameTextField.setText(quizOne.getNameQuiz());
        levelTextField.setText(String.valueOf(quizOne.getLevel()));
        amountTextField.setText(String.valueOf(quizOne.getAmountQuestions()));
        amountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                amountTextField.setText(oldValue);
            }
        });
    }
    public void doMenuBack(ActionEvent event) { Main.getSceneManager().showManageQuizScene();}

    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }

    public void doCreateUpdateQuiz() {
        Quiz quiz = null;
        QuizDAO quizDAO = new QuizDAO(dbAccess);
        Course course = courseList.getSelectionModel().getSelectedItem();
        String nameQuiz = quizList.getSelectionModel().getSelectedItem().getNameQuiz();
        String level = nameTextField.getText();
        int amountQuestions = 5;
        quiz = new Quiz(course, nameQuiz, level, amountQuestions);
        quizDAO.storeOne(quiz);
    }

}