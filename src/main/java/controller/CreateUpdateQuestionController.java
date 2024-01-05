package controller;

import database.mysql.DBAccess;
import database.mysql.QuestionDAO;
import database.mysql.QuizDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Question;
import model.Quiz;
import view.Main;

import java.util.List;

public class CreateUpdateQuestionController {
    private  QuestionDAO questionDAO;
    private QuizDAO quizDAO;
    @FXML
    private Label titelLabel;

    @FXML
    private TextField questionNumberTextfield;
    @FXML
    private TextField quizTextfield;

    @FXML
    private TextField questionTextfield;

    @FXML
    private TextField answerRightTextfield;

    @FXML
    private TextField answerWrong1Textfield;

    @FXML
    private TextField answerWrong2Textfield;

    @FXML
    private TextField answerWrong3Textfield;
    @FXML
    private ComboBox<Quiz> quizlist;
    @FXML
    private TextField warningTextfield;


    public CreateUpdateQuestionController() {
        this.questionDAO = new QuestionDAO(Main.getDBaccess());
        this.quizDAO = new QuizDAO(Main.getDBaccess());
    }

    public void setup(Question question) {
        titelLabel.setText("Wijzig vraag");
        quizTextfield.setText(question.getQuiz().getNameQuiz());
        questionNumberTextfield.setText(String.valueOf(question.getIdQuestion()));
        questionTextfield.setText(question.getQuestion());
        answerRightTextfield.setText(question.getAnswerRight());
        answerWrong1Textfield.setText(question.getAnswerWrong1());
        answerWrong2Textfield.setText(question.getAnswerWrong2());
        answerWrong3Textfield.setText(question.getAnswerWrong3());
        quizlist.setPromptText("Wijzig de bijbehorende quiz:");
        fillComboBoxQuizzes();
    }
     //Vult de keuzelijst (ComboBox) met beschikbare quizzen.
    public void fillComboBoxQuizzes() {
        List<Quiz> allQuizzes = quizDAO.getAll();
        ObservableList<Quiz> quizObservableList =
                FXCollections.observableArrayList(allQuizzes);
        quizlist.setItems(quizObservableList);
    }

    public void doMenu(ActionEvent event) {
        Main.getSceneManager().showCoordinatorDashboard();
    }

   // Werkt de huidige vraag bij met de gegevens uit de velden.
  //  @return De bijgewerkte vraag of null als er ongeldige gegevens zijn ingevoerd.
    public Question doUpdateQuestion() {
        // Update the currentQuestion object with the data from the fields
        String  quizText = quizTextfield.getText();
        String questionText = questionTextfield.getText();
        String answerRight = answerRightTextfield.getText();
        String answerWrong1 = answerWrong1Textfield.getText();
        String answerWrong2 = answerWrong2Textfield.getText();
        String answerWrong3 = answerWrong3Textfield.getText();
        Quiz quiz = quizDAO.getOneByName(quizText);
        Question question = null;
        if (questionText.isEmpty() || answerRight.isEmpty() || answerWrong1.isEmpty() || answerWrong2.isEmpty() || answerWrong3.isEmpty()) {
            warningTextfield.appendText("Alle velden moeten worden ingevuld!\n");
            Alert foutmelding = new Alert(Alert.AlertType.ERROR);
            foutmelding.setContentText(warningTextfield.getText());
            foutmelding.show();
            question = null;
        } else {
           question = new Question(quiz, questionText, answerRight, answerWrong1, answerWrong2,
                    answerWrong3);
        }

        return question;
    }

    //Slaat de vraag op of geeft een foutmelding weer als de invoergegevens ongeldig zijn
    public void doStoreQuestion(ActionEvent actionEvent) {
        Question question = doUpdateQuestion();
    if (question != null) {
        if (questionNumberTextfield.getText().isEmpty()) {
            questionDAO.storeOne(question);
            questionNumberTextfield.setText(String.valueOf(question.getIdQuestion()));
            showAlert("Vraag is opgeslagen", "Informatie");
        } else {
            int id = Integer.parseInt(questionNumberTextfield.getText());
            question.setIdQuestion(id);
            //questionDAO.updateQuestion(question);
            showAlert("Vraag gewijzigd", "Informatie");
        }
    }
}
    //Toont een waarschuwingsvenster met het opgegeven bericht en titel.
    private void showAlert(String content, String title) {
        Alert saved = new Alert(Alert.AlertType.INFORMATION);
        saved.setContentText(content);
        saved.setTitle(title);
        saved.show();
    }
}
