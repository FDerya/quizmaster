package controller;

import database.mysql.QuestionDAO;
import javacouchdb.QuizResultCouchDBDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Question;
import model.Quiz;
import model.QuizResult;
import model.User;
import view.Main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FillOutQuizController {
    QuestionDAO questionDAO = new QuestionDAO(Main.getDBaccess());
    QuizResultCouchDBDAO quizResultCouchDBDAO = new QuizResultCouchDBDAO(Main.getCouchDBaccess());

    int questionNumber = 1;
    @FXML
    private Label titleLabel;
    @FXML
    private TextArea questionArea;
    @FXML
    private Button previousQuestionButton;
    @FXML
    private Button nextQuestionButton;
    @FXML
    private Button turnInButton;
    List<Question> questionList = new ArrayList<>();
    List<String> answerList = new ArrayList<>();
    List<List<String>> listOfAnswerList = new ArrayList<>();
    String[] correctAnswers;
    String[] givenAnswers;
    String givenAnswer = "";
    int amountOfCorrectQuestionsToPassQuiz;
    Quiz selectedQuiz;

    // Fills list of questions, extracts answers from the questions and shuffles them into a new arrayList.
    public void setup(Quiz quiz) {
        questionList = shuffleQuestions(questionDAO.getQuestionsForQuiz(quiz));
        for (Question question : questionList) {
            extractAnswersFromQuestion(question);
        }
        fillQuestionArea(questionList);
        correctAnswers = new String[questionList.size()];
        givenAnswers = new String[questionList.size()];
        amountOfCorrectQuestionsToPassQuiz = quiz.getAmountQuestions();
        selectedQuiz = quiz;
    }

    private void extractAnswersFromQuestion(Question question) {
        String rightAnswer = question.getAnswerRight();
        String wrongAnswer1 = question.getAnswerWrong1();
        String wrongAnswer2 = question.getAnswerWrong2();
        String wrongAnswer3 = question.getAnswerWrong3();
        List<String> answersOfQuestion = new ArrayList<>();
        answersOfQuestion.add(rightAnswer);
        answersOfQuestion.add(wrongAnswer1);
        answersOfQuestion.add(wrongAnswer2);
        answersOfQuestion.add(wrongAnswer3);
        listOfAnswerList.add(shuffleAnswers(answersOfQuestion));
    }

    private void fillQuestionArea(List<Question> questionList) {
        String question = questionList.get(questionNumber-1).getQuestion();
        answerList = listOfAnswerList.get(questionNumber-1);
        questionArea.setText(question + "\n" +
                "\nA: " + answerList.get(0) +
                "\nB: " + answerList.get(1) +
                "\nC: " + answerList.get(2) +
                "\nD: " + answerList.get(3));
    }

    private void setTitleLabel(List<Question> questionList) {
        if (questionNumber < questionList.size()) {
            titleLabel.setText("Vraag " + questionNumber);
        } else if (questionNumber == questionList.size()) {
            titleLabel.setText("Vraag " + questionNumber + " (laatste vraag)");
        }
    }


    public void doRegisterA() {
        givenAnswer = answerList.get(0);
    }

    public void doRegisterB() {
        givenAnswer = answerList.get(1);
    }

    public void doRegisterC() {
        givenAnswer = answerList.get(2);
    }

    public void doRegisterD() {
        givenAnswer = answerList.get(3);
    }

    public void doNextQuestion() {
        saveAnswersToArrays();
        questionArea.clear();
        givenAnswer = "";
        questionNumber++;
        if (questionNumber == questionList.size()) {
            nextQuestionButton.setVisible(false);
            turnInButton.setVisible(true);
        }
        fillQuestionArea(questionList);
        setTitleLabel(questionList);
        previousQuestionButton.setVisible(questionNumber > 1);
    }

    public void doPreviousQuestion() {
        questionArea.clear();
        questionNumber--;
        fillQuestionArea(questionList);
        setTitleLabel(questionList);
        previousQuestionButton.setVisible(questionNumber > 1);
        nextQuestionButton.setVisible(true);
        turnInButton.setVisible(false);
    }

    public void doTurnIn() {
        saveAnswersToArrays();
        if (showAlert()) {
            int amountOfCorrectQuestions = getResult(givenAnswers, correctAnswers);
            String score = amountOfCorrectQuestions >= amountOfCorrectQuestionsToPassQuiz ? "behaald" : "niet behaald";
            QuizResult quizResult = new QuizResult(selectedQuiz.getNameQuiz(), score, LocalDate.now().toString(),
                    User.getCurrentUser().getFullName());
            quizResultCouchDBDAO.saveSingleQuizResult(quizResult);
            Main.getSceneManager().showStudentFeedback(selectedQuiz);
        }
    }

    private void saveAnswersToArrays() {
        givenAnswers[questionNumber - 1] = givenAnswer;
        correctAnswers[questionNumber - 1] = questionList.get(questionNumber - 1).getAnswerRight();
    }

    public boolean showAlert() {
        Alert turnInAlert = new Alert(Alert.AlertType.CONFIRMATION);
        turnInAlert.setTitle("Inleveren");
        turnInAlert.setHeaderText("Inleveren quiz");
        turnInAlert.setContentText("Je staat op het punt je quiz in te leveren.\n" +
                     checkAnswers(givenAnswers));
        ButtonType buttonCancel = new ButtonType("Annuleer");
        ButtonType buttonContinue = new ButtonType("Inleveren");
        turnInAlert.getButtonTypes().setAll(buttonCancel, buttonContinue);
        Optional<ButtonType> result = turnInAlert.showAndWait();
        return (result.isPresent() && result.get() == buttonContinue);
    }

    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }

    public void doQuizSelect() {
        Main.getSceneManager().showSelectQuizForStudent();
    }

    private List<Question> shuffleQuestions(List<Question> oldList) {
        int originalSizeOldList = oldList.size();
        List<Question> newList = new ArrayList<>();
        for (int i = 1; i <= originalSizeOldList; i++) {
            int randomNumber = (int) (Math.random() * oldList.size());
            newList.add(oldList.get(randomNumber));
            oldList.remove(randomNumber);
        }
        return newList;
    }

    private List<String> shuffleAnswers(List<String> oldList) {
        int originalSizeOldList = oldList.size();
        List<String> newList = new ArrayList<>();
        for (int i = 1; i <= originalSizeOldList; i++) {
            int randomNumber = (int) (Math.random() * oldList.size());
            newList.add(oldList.get(randomNumber));
            oldList.remove(randomNumber);
        }
        return newList;
    }

    private int getResult(String[]givenAnswers, String[]correctAnswers) {
        int counter = 0;
        for (int i = 0; i < givenAnswers.length; i++) {
            if (givenAnswers[i].equals(correctAnswers[i])) {
                counter++;
            }
        }
        return counter;
    }

    public String checkAnswers(String[] givenAnsers) {
        int counter = 0;
        String singular = "Je hebt de volgende vraag niet ingevuld: ";
        String plural = "Je hebt de volgende vragen niet ingevuld: ";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < givenAnsers.length; i++) {
            if (givenAnsers[i].isEmpty()) {
                stringBuilder.append(i + 1).append(", ");
                counter++;
            }
        }
        removeLastComma(stringBuilder);
        if (counter == 0) {
            return "";
        } else if (counter == 1) {
            return singular + stringBuilder;
        }
        return plural + stringBuilder;
    }

    private static void removeLastComma(StringBuilder stringBuilder) {
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length()-2);
        }
    }
}
