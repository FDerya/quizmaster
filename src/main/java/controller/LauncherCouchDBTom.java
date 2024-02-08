package controller;

import database.mysql.CourseDAO;
import javacouchdb.CouchDBAccess;
import javacouchdb.QuizCouchDBDAO;
import model.*;
import view.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LauncherCouchDBTom {
    private static final CourseDAO courseDAO = new CourseDAO(Main.getDBaccess());
    private static CouchDBAccess couchDBAccess;
    private static QuizCouchDBDAO quizCouchDBDAO;

    public static void main(String[] args) throws IOException {
        createCouchDBAccess();
        Course course = courseDAO.getOneByName("An");
        Quiz quizTest1 = new Quiz(1,course, "Sander", "Gevorderd", 12);
        Quiz quizTest2 = new Quiz(2,course, "Gerke", "Gevorderd", 10);
        Quiz quizTest3 = new Quiz(3,course, "Francine", "Beginner", 8);
        Quiz quizTest4 = new Quiz(4,course, "Huub", "Medium", 6);

        List<Quiz> testList = new ArrayList<>();
        testList.add(quizTest1);
        testList.add(quizTest2);
        testList.add(quizTest3);
        testList.add(quizTest4);

        saveQuizList(testList);
        getQuiz(testList.get(0).getNameQuiz());
        System.out.println(testList.get(0).getNameQuiz() + " is aanwezig");
        testList.get(1).setMinimumAmountCorrectQuestions(24);
        updateQuiz(testList.get(1));
        System.out.println(testList.get(1).getNameQuiz() + " is ook aanwezig en verbeterd zichzelf");
        deleteQuiz(testList.get(3));
        System.out.println(testList.get(3).getNameQuiz() + " is met pensioen en mag verwijderd uit het systeem");
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
    private static void getQuiz(String code){
        Quiz quiz = quizCouchDBDAO.getQuiz(code);
    }
    private static void updateQuiz(Quiz quiz){
        quizCouchDBDAO.updateQuiz(quiz);

    }
    private static void deleteQuiz(Quiz quiz){
        quizCouchDBDAO.deleteQuiz(quiz);
    }
}
