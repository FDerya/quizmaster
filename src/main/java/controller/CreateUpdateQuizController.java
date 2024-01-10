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
    ComboBox<String> levelComboBox;
    @FXML
    ComboBox<String> courseComboBox;

    @FXML
    private TextField levelTextField;
    @FXML
    ListView<Course> courseList;

    ObservableList<String> level = FXCollections.observableArrayList("Beginner", "Medium", "Gevorderd");
    ObservableList<String> course = FXCollections.observableArrayList(courseList.getItems().toString());

    private final DBAccess dbAccess;

    public CreateUpdateQuizController() {
        this.dbAccess = Main.getDBaccess();
    }

    public void setup(Quiz quizOne) {
        if (!(quizOne == null)) {
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
        } else {
            createQuiz();
        }
    }
        private void createQuiz(){
        Quiz quizNew = null;
            titelLabel.setText("Maak nieuwe Quiz");
            courseComboBox.getSelectionModel().getSelectedItem();
            nameTextField.getText();
            levelComboBox.getSelectionModel().getSelectedItem();
            amountTextField.getText();
            amountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    amountTextField.setText(oldValue);
                }
            });
            }
        public void doMenuBack (ActionEvent event){
            Main.getSceneManager().showManageQuizScene();
        }

        public void doMenu () {
            Main.getSceneManager().showWelcomeScene();
        }

        public void doCreateUpdateQuiz (ActionEvent event){
            Quiz quiz = null;
            QuizDAO quizDAO = new QuizDAO(dbAccess);
            CourseDAO courseDAO = new CourseDAO(dbAccess);
            Course course = courseDAO.getOneByName(courseTextField.getText());
            String nameQuiz = nameTextField.getText();
            String level = levelTextField.getText();
            int amountQuestions = 5;
            quiz = new Quiz(course, nameQuiz, level, amountQuestions);
            quizDAO.storeOne(quiz);
        }

    }