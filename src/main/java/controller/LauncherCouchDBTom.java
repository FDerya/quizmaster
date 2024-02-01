package controller;

import database.mysql.CourseDAO;
import database.mysql.QuizDAO;
import javacouchdb.CouchDBAccess;
import javacouchdb.QuizCouchDBDAO;
import model.*;
import view.Main;

import java.io.IOException;
import java.util.List;

public class LauncherCouchDBTom {
    private static final QuizDAO quizDAO = new QuizDAO(Main.getDBaccess());
    private static final CourseDAO courseDAO = new CourseDAO(Main.getDBaccess());
    private static CouchDBAccess couchDBAccess;
    private static QuizCouchDBDAO quizCouchDBDAO;

    public static void main(String[] args) throws IOException {
        createCouchDBAccess();
        List<Quiz> quizList = quizDAO.getAll();
        //saveQuizList(quizList);
        getQuiz("1a8e646fc65d405288f79f33cf7ba2c7");
        updatQuiz("1a8e646fc65d405288f79f33cf7ba2c7");
        Course course = courseDAO.getOneByName("An");
        Quiz quizTest = new Quiz(course, "test", "medium", 12);
        quizCouchDBDAO.saveSingleQuiz(quizTest);
        deleteQuiz(quizTest);
        closeCouchDBAccess();
    }
    //Verbinding CouchDB openen
    private static void createCouchDBAccess() {
        try {
            couchDBAccess = new CouchDBAccess("quizmaster", "admin", "admin");
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
    private static void getQuiz(String code){
        Quiz quiz = quizCouchDBDAO.getQuizById(code);
        System.out.println(quiz.toString());
    }
    private static void updatQuiz(String code){
        Quiz quiz = quizCouchDBDAO.getQuizById(code);
        System.out.println(quiz.toString());
        quiz.setMinimumAmountCorrectQuestions(24);
        quizCouchDBDAO.saveSingleQuiz(quiz);
        System.out.println(quiz.toString());
    }
    private static void deleteQuiz(Quiz quiz){
        System.out.println(quiz.toString());
        quizCouchDBDAO.deleteQuiz(quiz);
    }
}
