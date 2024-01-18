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
import view.Main;

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
    private ComboBox<Quiz> quizlist;

    @FXML
    private Label titelLabel;

    @FXML
    private Label warningLabel;

    public CreateUpdateQuestionController() {
        this.questionDAO = new QuestionDAO(Main.getDBaccess());
        this.quizDAO = new QuizDAO(Main.getDBaccess());
        this.courseDAO = new CourseDAO(Main.getDBaccess());
    }

    public void setup(Question question) {
        fillComboBoxQuizzes();

        quizlist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Update the course label based on the selected quiz
                label_Course.setText(questionDAO.getCourseNameByQuizName(newValue.getNameQuiz()));
            }
        });


        if (question != null) {
            titelLabel.setText("Wijzig vraag");

            questionTextfield.setText(question.getQuestion());
            answerRightTextfield.setText(question.getAnswerRight());
            answerWrong1Textfield.setText(question.getAnswerWrong1());
            answerWrong2Textfield.setText(question.getAnswerWrong2());
            answerWrong3Textfield.setText(question.getAnswerWrong3());


            quizlist.getSelectionModel().select(question.getQuiz());
            quizlist.setPromptText("Wijzig de bijbehorende quiz:");

            label_Course.setText(questionDAO.getCourseNameByQuizName(quizlist.getSelectionModel().getSelectedItem().getNameQuiz()));


        } else {
            Quiz selectedQuiz = quizlist.getSelectionModel().getSelectedItem();
            if (selectedQuiz != null) {
                label_Course.setText(selectedQuiz.getCourse().getNameCourse());
            }
        }
    }

    //Vult de keuzelijst (ComboBox) met beschikbare quizzen.
    public void fillComboBoxQuizzes() {
        List<Quiz> allQuizzes = quizDAO.getAll();
        ObservableList<Quiz> quizObservableList =
                FXCollections.observableArrayList(allQuizzes);
        quizlist.setItems(quizObservableList);
    }

    public void doMenu(ActionEvent event) {
        Main.getSceneManager().showManageQuestionsScene();
    }


    public Question doUpdateQuestion() {
        boolean correctInput;
        String quizList = quizlist.getPromptText();
        String questionText = questionTextfield.getText();
        String answerRight = answerRightTextfield.getText();
        String answerWrong1 = answerWrong1Textfield.getText();
        String answerWrong2 = answerWrong2Textfield.getText();
        String answerWrong3 = answerWrong3Textfield.getText();

        correctInput = isCorrectInput(questionText, answerRight, answerWrong1, answerWrong2, answerWrong3);

        if (!correctInput) {
            return null;
        } else {
            return createQuestion(quizList, questionText, answerRight, answerWrong1, answerWrong2, answerWrong3);

        }
    }

    private boolean areFieldsEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private Question createQuestion(String quizText, String questionText, String answerRight,
                                    String answerWrong1, String answerWrong2, String answerWrong3) {
        String[] answers = {answerRight, answerWrong1, answerWrong2, answerWrong3};
        Quiz quiz = quizDAO.getOneByName(quizText);

        return new Question(quiz, questionText, answers[0], answers[1], answers[2], answers[3]);
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

    public void doStoreQuestion(ActionEvent actionEvent) {
        Question question = doUpdateQuestion();

        if (question != null) {
            if (areFieldsEmpty(
                    questionTextfield.getText(),
                    answerRightTextfield.getText(),
                    answerWrong1Textfield.getText(),
                    answerWrong2Textfield.getText(),
                    answerWrong3Textfield.getText(),
                    quizlist.getValue() == null ? "" : quizlist.getValue().toString())) {
                return;
            }

        }
    }

    private void storeNewQuestion(Question question) {
        questionDAO.storeOne(question);

        showSuccessMessage("Vraag is opgeslagen");
    }

    private void updateExistingQuestion(Question question) {
        Question existingQuestion = questionDAO.getOneById(question.getIdQuestion());

        if (existingQuestion != null) {
            questionDAO.updateQuestion(question);
            showSuccessMessage("Vraag gewijzigd");
        } else {
            questionDAO.storeOne(question);

            showSuccessMessage("Vraag is opgeslagen");
        }
    }

    // Display success message, wait for 2 seconds, then go back to the manage screen
    private void showSuccessMessage(String message) {
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


}
