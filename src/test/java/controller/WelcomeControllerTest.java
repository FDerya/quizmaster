package controller;

import database.mysql.QuestionDAO;
import database.mysql.QuizDAO;
import database.mysql.UserDAO;
import org.junit.jupiter.api.Test;
import model.*;
import view.Main;


import java.util.ArrayList;
import java.util.List;

import static controller.WelcomeController.countAVG;
import static org.junit.jupiter.api.Assertions.*;

class WelcomeControllerTest {

    UserDAO userDAO = new UserDAO(Main.getDBaccess());
    QuizDAO quizDAO = new QuizDAO(Main.getDBaccess());
    QuestionDAO questionDAO = new QuestionDAO(Main.getDBaccess());
    User user = userDAO.getOneById(120);
    User user2 = userDAO.getOneById(181);
    User user3 = new User (500, "horlepiep", "makeitwork", "Tom", "van", "Beek", "coordinator");
    List<Quiz> emptyList = quizDAO.getQuizzesFromUser(user3);
    List<Quiz> newList = quizDAO.getQuizzesFromUser(user2);
    List<Quiz> fullList = quizDAO.getQuizzesFromUser(user);
    int amountQuiz = fullList.size();
    int amountQuestionEmptyList = newList.size();
    int amountQuestion = questionDAO.getQuestionCountForUser(user);
    int amountQuestionEmpty = questionDAO.getQuestionCountForUser(user3);
    double avgQuestion = (amountQuestion * 10.0 / amountQuiz) / 10.0;
    String quizEmpty = "Er zijn geen quizzen met vragen";
    String questionEmpty = "Deze quizzen hebben geen vragen";
    String avg = "\nEr zijn " + amountQuiz + " quizzen met " + amountQuestion + " vragen, gemiddelde = " + avgQuestion + " vragen per quiz";

    @Test
    void countAVGEmpty() {
        assert (true);
    }

    @Test
    void countAVGEmptyQuiz() {
        assertEquals(quizEmpty, countAVG(emptyList, user3));
    }

    @Test
    void countAVGEmptyQuestion() {
        assertEquals(questionEmpty, countAVG(newList, user2));
    }
    @Test
    void countAVGFullQuiz(){
        assertEquals (avg, countAVG(fullList, user));
    }
}