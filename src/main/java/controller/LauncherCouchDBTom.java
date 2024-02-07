package controller;

import database.mysql.CourseDAO;
import database.mysql.QuizDAO;
import javacouchdb.CouchDBAccess;
import javacouchdb.QuizCouchDBDAO;
import model.*;
import view.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LauncherCouchDBTom {
    private static final QuizDAO quizDAO = new QuizDAO(Main.getDBaccess());
    private static final CourseDAO courseDAO = new CourseDAO(Main.getDBaccess());
    private static CouchDBAccess couchDBAccess;
    private static QuizCouchDBDAO quizCouchDBDAO;

    public static void main(String[] args) throws IOException {
        createCouchDBAccess();
        //List<Quiz> quizList = quizDAO.getAll();
        //saveQuizList(quizList);
        //getQuiz("Python Basis");
        //updateQuiz("Java Basis");
        Course course = courseDAO.getOneByName("An");
        Quiz quizTest1 = new Quiz(1,course, "test1", "medium", 12);
        Quiz quizTest2 = new Quiz(2,course, "test2", "gevorder", 10);
        Quiz quizTest3 = new Quiz(3,course, "test3", "beginner", 8);
        Quiz quizTest4 = new Quiz(4,course, "test4", "medium", 6);

        List<Quiz> testList = new ArrayList<>();
        testList.add(quizTest1);
        testList.add(quizTest2);
        testList.add(quizTest3);
        testList.add(quizTest4);

        //saveQuizList(testList);
        updateQuiz(testList.get(1));
        //deleteQuiz(testList.get(3));
        closeCouchDBAccess();
    }
    //Verbinding CouchDB openen
    private static void createCouchDBAccess() {
        try {
            couchDBAccess = new CouchDBAccess("quizzesinquizmaster", "admin", "admin");
            quizCouchDBDAO = new QuizCouchDBDAO(couchDBAccess);
        } catch (Exception exception) {
            System.err.println("Fout bij het creeren toegang: " + exception.getMessage());

        }
    }
    // Verbinding CouchDB verbreken
    private static void closeCouchDBAccess() {
        try {
            if (couchDBAccess != null) {
                couchDBAccess.getClient().shutdown();
            }
        } catch (Exception exception) {
            System.err.println("Fout bij sluiten verbinding: " + exception.getMessage());
        }
    }
    //Een lijst quizzen opslaan in CouchDB
    private static void saveQuizList(List<Quiz> quizList) {
        if (couchDBAccess.getClient() != null) {
            System.out.println("Connectie open");
            for (Quiz quiz : quizList) {
                quizCouchDBDAO.saveSingleQuiz(quiz);
            }
        }
    }

    public static void saveSingleQuiz(Quiz quiz){
        if (couchDBAccess.getClient()!= null){
            System.out.println("Connectie open");
            quizCouchDBDAO.saveSingleQuiz(quiz);
        }
    }
    private static void getQuiz(String code){
        Quiz quiz = quizCouchDBDAO.getQuiz(code);
        System.out.println(quiz.getNameQuiz());
    }
    private static void updateQuiz(Quiz quiz){
        Quiz updateQuiz = quizCouchDBDAO.updateQuiz(quiz);
        System.out.println(quiz.getNameQuiz());
        quiz.setMinimumAmountCorrectQuestions(24);
        quizCouchDBDAO.saveSingleQuiz(quiz);
        System.out.println(quiz.getNameQuiz());
    }
    private static void deleteQuiz(Quiz quiz){
        System.out.println(quiz.getNameQuiz());
        quizCouchDBDAO.deleteQuiz(quiz);
    }
}
