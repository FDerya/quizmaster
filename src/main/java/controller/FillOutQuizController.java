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

import java.time.LocalDateTime;
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
        amountOfCorrectQuestionsToPassQuiz = quiz.getMinimumAmountCorrectQuestions();
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

    // When you press the next question button: saves answers to arrays (given answer to given answers, correct answer
    // to correct answers), clears the questionarea, reverts the givenAnswer String back to empty, changes the
    // questionNumber counter to the next question. If you're on the last question, changes the nextQuestionButton
    // to the turnInButton. It then fills the questionArea with the new question, changes the title and shows the
    // previousQuestion button if you're above question 1.
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

    // Clears the question area, changes the questionNumber counter to the previous question. Then fills the
    // questionArea, sets the title label and shows the previousQuestion button if you're above question 1.
    // Also hides the turnInButton and shows the nextQuestion button.
    public void doPreviousQuestion() {
        questionArea.clear();
        questionNumber--;
        fillQuestionArea(questionList);
        setTitleLabel(questionList);
        previousQuestionButton.setVisible(questionNumber > 1);
        nextQuestionButton.setVisible(true);
        turnInButton.setVisible(false);
    }

    // Turn in quiz, first checks if all answers are given. If not: shows an alert with questionnumbers not filled in.
    // Else turns in the quiz.
    public void doTurnIn() {
        saveAnswersToArrays();
        boolean allQuestionsAnswered = true;
        for (String answer : givenAnswers) {
            if (answer.isEmpty()) {
                allQuestionsAnswered = false;
                break;
            }
        }
        if (allQuestionsAnswered) {
            turnInQuiz();
        } else {
            showAlert();
        }
    }

    // Method to turn in quiz, make a quizresult and save it to the couchDB.
    private void turnInQuiz() {
        int amountOfCorrectQuestions = getResult(givenAnswers, correctAnswers);
        int totalQuestions = givenAnswers.length;
        String score = amountOfCorrectQuestions + " / " + totalQuestions;
        String result = amountOfCorrectQuestions >= amountOfCorrectQuestionsToPassQuiz ? "behaald" : "niet behaald";
        QuizResult quizResult = new QuizResult(selectedQuiz.getNameQuiz(), score, result, LocalDateTime.now().toString(),
                User.getCurrentUser().getFullName());
        quizResult.incrementAttemptCount();
        quizResultCouchDBDAO.saveSingleQuizResult(quizResult);
        Main.getSceneManager().showStudentFeedback(selectedQuiz);
    }

    // Saves given and correct answers to arrays.
    private void saveAnswersToArrays() {
        givenAnswers[questionNumber - 1] = givenAnswer;
        correctAnswers[questionNumber - 1] = questionList.get(questionNumber - 1).getAnswerRight();
    }

    // Shows alert to turn in quiz when you haven't filled in all questions.
    public void showAlert() {
        Alert turnInAlert = new Alert(Alert.AlertType.CONFIRMATION);
        turnInAlert.setTitle("Inleveren");
        turnInAlert.setHeaderText("Inleveren quiz");
        turnInAlert.setContentText("Je staat op het punt je quiz in te leveren.\n" +
                     checkAnswers(givenAnswers));
        ButtonType buttonCancel = new ButtonType("Annuleer");
        ButtonType buttonContinue = new ButtonType("Inleveren");
        turnInAlert.getButtonTypes().setAll(buttonCancel, buttonContinue);
        Optional<ButtonType> result = turnInAlert.showAndWait();
        if (result.isPresent() && result.get() == buttonContinue) {
            turnInQuiz();
        }
    }

    // Aborts quiz
    public void doMenu() {
        Alert turnInAlert = new Alert(Alert.AlertType.CONFIRMATION);
        turnInAlert.setTitle("Quiz afbreken");
        turnInAlert.setHeaderText("Quiz afbreken");
        turnInAlert.setContentText("Je staat op het punt je quiz af te breken.\n" +
                "Je antwoorden worden niet opgeslagen." + "\nWeet je het zeker?");
        ButtonType buttonCancel = new ButtonType("Annuleer");
        ButtonType buttonContinue = new ButtonType("Afbreken");
        turnInAlert.getButtonTypes().setAll(buttonCancel, buttonContinue);
        Optional<ButtonType> result = turnInAlert.showAndWait();
        if (result.isPresent() && result.get() == buttonContinue) {
            Main.getSceneManager().showWelcomeScene();
        }
    }

    // Shuffles questions and returns them to a new list.
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

    // Shuffles answers and returns them to a new list.
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

    // Compares the givenAnswers to the correctAnswers array and returns amount of correct answers in the givenAnswers.
    private int getResult(String[]givenAnswers, String[]correctAnswers) {
        int counter = 0;
        for (int i = 0; i < givenAnswers.length; i++) {
            if (givenAnswers[i].equals(correctAnswers[i])) {
                counter++;
            }
        }
        return counter;
    }

    // Handles warning if you haven't filled in all questions.
    public String checkAnswers(String[] givenAnsers) {
        int counter = 0;
        String singular = "Je hebt de volgende vraag niet ingevuld: ";
        String plural = "Je hebt de volgende vragen niet ingevuld: ";
        String endOfSentence = "\nWeet je zeker dat je wilt inleveren?";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < givenAnsers.length; i++) {
            if (givenAnsers[i].isEmpty()) {
                stringBuilder.append(i + 1).append(", ");
                counter++;
            }
        }
        removeLastComma(stringBuilder);
        if (counter == 0) {
            return endOfSentence;
        } else if (counter == 1) {
            return singular + stringBuilder + endOfSentence;
        }
        return plural + stringBuilder + endOfSentence;
    }

    private static void removeLastComma(StringBuilder stringBuilder) {
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length()-2);
        }
    }
}
