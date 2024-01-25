package controller;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.QuestionDAO;
import database.mysql.QuizDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import model.Question;
import model.Quiz;
import model.User;
import view.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;

public class CreateUpdateQuestionController {
    private QuestionDAO questionDAO;
    private QuizDAO quizDAO;
    private CourseDAO courseDAO;
    @FXML
    private TextField answerRightTextfield;

    @FXML
    private TextField answerWrong1Textfield;

    @FXML
    private TextField answerWrong2Textfield;

    @FXML
    private TextField answerWrong3Textfield;

    @FXML
    private TextField questionTextfield;

    @FXML
    private Label label_Antwood_1;

    @FXML
    private Label label_Antwood_2;

    @FXML
    private Label label_Antwood_3;

    @FXML
    private Label label_Course;

    @FXML
    private Label label_Juist;

    @FXML
    private Label label_Quiz;

    @FXML
    private Label label_Vraag;

    @FXML
    private ComboBox<String> quizlist;


    @FXML
    private Label titelLabel;

    @FXML
    private Label warningLabel;

    private Boolean isUpdate;

    private int existingQuestionId;

    public CreateUpdateQuestionController() {
        this.questionDAO = new QuestionDAO(Main.getDBaccess());
        this.quizDAO = new QuizDAO(Main.getDBaccess());
        this.courseDAO = new CourseDAO(Main.getDBaccess());
    }

    public void setup(Question question) {
        // Filling up the combo box
        fillComboBoxQuizzes();
        // Show course that is asocciated with user
        quizlist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Update the course label based on the selected quiz
                Quiz quiz = quizDAO.getOneByName(newValue);
                label_Course.setText(quizDAO.getOneByName(quiz.getNameQuiz()).getCourse().getNameCourse());
            }
        });
        // Update Question
        if (question != null) {
            isUpdate = true;
            existingQuestionId = question.getIdQuestion();
            // Fill out question fields
            titelLabel.setText("Wijzig vraag");
            questionTextfield.setText(question.getQuestion());
            answerRightTextfield.setText(question.getAnswerRight());
            answerWrong1Textfield.setText(question.getAnswerWrong1());
            answerWrong2Textfield.setText(question.getAnswerWrong2());
            answerWrong3Textfield.setText(question.getAnswerWrong3());
            String quizName = question.getQuiz().getNameQuiz();
            quizlist.getSelectionModel().select(quizName);
            quizlist.setPromptText("Wijzig de bijbehorende quiz:");
            label_Course.setText(quizDAO.getOneByName(quizName).getCourse().getNameCourse());
        }
        // New Question
        else {
            isUpdate = false;
            String selectedQuizName = quizlist.getSelectionModel().getSelectedItem();
            if (selectedQuizName != null) {
                Quiz quiz = quizDAO.getOneByName(selectedQuizName);
                label_Course.setText(quiz.getCourse().getNameCourse());
            }
        }
    }

    public void doMenu(ActionEvent event) {
        Main.getSceneManager().showManageQuestionsScene();
    }

    // Opslaan
    public void doStoreQuestion(ActionEvent actionEvent) {
        Question question = doUpdateQuestion();

        if (question != null) {
            // Check if the quiz in the question is not null
            if (!isUpdate && question.getQuiz() != null) {
                // Only attempt to store the question if the quiz is not null
                questionDAO.storeOne(question);
                clearFields();
                warningLabel.setText("Vraag is opgeslagen");
            } else if (isUpdate && question != null) {
                updateExistingQuestion(question);
            } else {
                warningLabel.setVisible(true);
                warningLabel.setText("Field/s are empty");
            }
        }
    }

    private void updateExistingQuestion(Question question) {
        question.setIdQuestion(existingQuestionId);
        Question existingQuestion = questionDAO.getOneById(existingQuestionId);

        if (existingQuestion != null) {
            questionDAO.updateQuestion(question);
            showSuccessMessage("Vraag gewijzigd!");
        }
    }

    public Question doUpdateQuestion() {
        boolean correctInput;
        // Gets values from textfields
        String selectedQuiz = quizlist.getValue();
        String questionText = questionTextfield.getText();
        String answerRight = answerRightTextfield.getText();
        String answerWrong1 = answerWrong1Textfield.getText();
        String answerWrong2 = answerWrong2Textfield.getText();
        String answerWrong3 = answerWrong3Textfield.getText();
        // Checks if the fields are not empty
        correctInput = isCorrectInput(questionText, answerRight, answerWrong1, answerWrong2, answerWrong3);
        // If fields are empty, return false
        if (!correctInput) {
            return null;
        } else {
            // If fields are not empty, return question
            return createQuestion(selectedQuiz, questionText, answerRight, answerWrong1, answerWrong2, answerWrong3);
        }
    }

    // Checks if fields are empty
    private boolean areFieldsEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // Creating a new question
    private Question createQuestion(String quizName, String questionText, String answerRight,
                                    String answerWrong1, String answerWrong2, String answerWrong3) {
        String[] answers = {answerRight, answerWrong1, answerWrong2, answerWrong3};

        // Using quizName to return the full quiz
        Quiz quizNew = quizDAO.getOneByName(quizName);
        return new Question(quizNew, questionText, answers[0], answers[1], answers[2], answers[3]);
    }


    private boolean isCorrectInput(String questionText, String answerRight,
                                   String answerWrong1, String answerWrong2, String answerWrong3) {

        // Returns false if any fields are empty
        boolean anyFieldEmpty = checkAndChangeLabelColor(questionText.isEmpty(), label_Vraag) |
                checkAndChangeLabelColor(answerRight.isEmpty(), label_Juist) |
                checkAndChangeLabelColor(answerWrong1.isEmpty(), label_Antwood_1) |
                checkAndChangeLabelColor(answerWrong2.isEmpty(), label_Antwood_2) |
                checkAndChangeLabelColor(answerWrong3.isEmpty(), label_Antwood_3);

        return !anyFieldEmpty;
    }

    // Checks if fields are empty or not
    private boolean checkAndChangeLabelColor(boolean emptyTextField, Label label) {
        String errorMessageNoFields = "Je hebt niet alle velden ingevuld.\nVul de rood gekleurde velden alsnog in.";
        if (emptyTextField) {
            label.setTextFill(Color.RED);
            warningLabel.setText(errorMessageNoFields);
            warningLabel.setVisible(true);
            return true;
        } else {
            warningLabel.setVisible(false);
            label.setTextFill(Color.BLACK);
            return false;
        }
    }


    // Display success message, wait for 2 seconds, then go back to the manage screen
    private void showSuccessMessage(String message) {
        warningLabel.setVisible(true);
        warningLabel.setText(message);

        // Create a timeline with a KeyFrame to wait for 2 seconds
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    // After 2 seconds, go back to the manage screen
                    Main.getSceneManager().showManageQuestionsScene();
                })
        );

        timeline.play();
    }

    //Vult de keuzelijst (ComboBox) met beschikbare quizzen.
//Vult de keuzelijst (ComboBox) met beschikbare quizzen.
    public void fillComboBoxQuizzes() {
        // Get the current user
        User currentUser = User.getCurrentUser();
        if (currentUser != null) {
            // Get quizzes for the current user
            List<Quiz> userQuizzes = quizDAO.getQuizzesFromUser(currentUser);

            // Extract quiz names from the list of quizzes
            List<String> quizNames = new ArrayList<>();
            for (Quiz quiz : userQuizzes) {
                quizNames.add(quiz.getNameQuiz());
            }

            // Display the user's quizzes in the ComboBox
            quizlist.setItems(FXCollections.observableArrayList(quizNames));
        }
    }

    public void clearFields() {
        questionTextfield.clear();
        answerRightTextfield.clear();
        answerWrong1Textfield.clear();
        answerWrong2Textfield.clear();
        answerWrong3Textfield.clear();
        warningLabel.setText("");
        quizlist.setValue(null);
        quizlist.getSelectionModel().clearSelection();
    }
}


