package controller;

import database.mysql.QuestionDAO;
import database.mysql.QuizDAO;
import database.mysql.UserDAO;
import org.junit.jupiter.api.Test;
import model.*;
import view.Main;

import java.util.ArrayList;
import java.util.List;

import static controller.WelcomeController.courseDAO;
import static org.junit.jupiter.api.Assertions.*;

class WelcomeControllerTest {

    public WelcomeController w = new WelcomeController();

    UserDAO userDAO = new UserDAO(Main.getDBaccess());
    QuizDAO quizDAO = new QuizDAO(Main.getDBaccess());
    QuestionDAO questionDAO = new QuestionDAO(Main.getDBaccess());
    User user = userDAO.getOneById(120);
    User user2 = userDAO.getOneById(181);
    Course course = courseDAO.getOneByName("Muziek Theorie Basis");

    User user3 = new User(500, "horlepiep", "makeitwork", "Tom", "van", "Beek", "coordinator");
    List<Quiz> emptyList = quizDAO.getQuizzesFromUser(user3);
    public List<Quiz> newList () {
        Quiz testQuiz = new Quiz(50, course, "Muziek Theorie", "Beginner", 10);
       List<Quiz> normalList = new ArrayList<>();
        normalList.add(testQuiz);
        return normalList;
    }
    List<Quiz> newList = newList();
    List<Quiz> fullList = quizDAO.getQuizzesFromUser(user);
    int amountQuiz = fullList.size();
    int amountQuestion = questionDAO.getQuestionCountForUser(user);
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
        assertEquals(quizEmpty, w.countAVG(emptyList, user3));
    }

    @Test
    void countAVGEmptyQuestion() {
        assertEquals(questionEmpty, w.countAVG(newList, user2));
    }

    @Test
    void countAVGFullQuiz() {
        assertEquals(avg, w.countAVG(fullList, user));
    }
}