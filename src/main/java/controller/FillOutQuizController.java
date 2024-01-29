package controller;

import database.mysql.QuestionDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import model.Question;
import model.Quiz;
import view.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FillOutQuizController {
    QuestionDAO questionDAO = new QuestionDAO(Main.getDBaccess());
    String givenAnswer = "";
    @FXML
    private Label titleLabel;
    @FXML
    private TextArea questionArea;
    @FXML
    private Button previousQuestionButton;
    @FXML
    private Button nextQuestionButton;

    public void setup(Quiz quiz) {
        List<Question> questionList = questionDAO.getQuestionsForQuiz(quiz);
        Collections.shuffle(questionList);
        if (titleLabel.getText().equals("Vraag 1")) {
            previousQuestionButton.setVisible(false);
        }
        String question = questionList.get(0).getQuestion();
        String correctAnswer = questionList.get(0).getAnswerRight();
        String wrongAnswer1 = questionList.get(0).getAnswerWrong1();
        String wrongAnswer2 = questionList.get(0).getAnswerWrong2();
        String wrongAnswer3 = questionList.get(0).getAnswerWrong3();

        List<String> answerList = new ArrayList<>();
        answerList.add(correctAnswer);
        answerList.add(wrongAnswer1);
        answerList.add(wrongAnswer2);
        answerList.add(wrongAnswer3);
        Collections.shuffle(answerList);
        questionArea.setText(titleLabel.getText() +
                "\n" + question + "\n" +
                "\nA: " + answerList.get(0) +
                "\nB: " + answerList.get(1) +
                "\nC: " + answerList.get(2) +
                "\nD: " + answerList.get(3));

    }

    public void doRegisterA() {
        givenAnswer = "A";
    }

    public void doRegisterB() {
        givenAnswer = "B";
    }

    public void doRegisterC() {
        givenAnswer = "C";
    }

    public void doRegisterD() {
        givenAnswer = "D";
    }

    public void doNextQuestion() {
        System.out.println(givenAnswer);
    }

    public void doPreviousQuestion() {}

    @FXML
    private void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }

    private List<Question> shuffleQuestions(List<Question> oldList) {
        List<Question> newList = new ArrayList<>();
        for (int i = 1; i <= oldList.size(); i++) {
            int randomNumber = (int) (Math.random() * oldList.size());
            newList.add(oldList.get(randomNumber));
            oldList.remove(randomNumber);
        }
        return newList;
    }

    private List<String> shuffleAnswers(List<String> oldList) {
        List<String> newList = new ArrayList<>();
        for (int i = 1; i <= oldList.size(); i++) {
            int randomNumber = (int) (Math.random() * oldList.size());
            newList.add(oldList.get(randomNumber));
            oldList.remove(randomNumber);
        }
        return newList;
    }
}
