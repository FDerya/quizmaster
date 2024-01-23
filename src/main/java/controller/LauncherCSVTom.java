package controller;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.QuizDAO;
import database.mysql.UserDAO;
import model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LauncherCSVTom {
    // Eerst locatie benoemen csv
    private static final String filepath = "src/main/java/database/Quizzen.csv";
    private static final File userFile = new File(filepath);

    private static DBAccess dbAccess = null;

    public static void main(String[] args) {
        final String databaseName = "Quizmaster";
        final String mainUser = "userQuizmaster";
        final String mainUserPassword = "pwQuizmaster";
        DBAccess dbAccess = new DBAccess(databaseName, mainUser, mainUserPassword);
        dbAccess.openConnection();
        UserDAO userDAO = new UserDAO(dbAccess);
        CourseDAO courseDAO = new CourseDAO(dbAccess, userDAO);
        QuizDAO quizDAO = new QuizDAO(dbAccess, userDAO);
       // Course course = new Course(21, "c1", "medium");
       // Quiz TestTom = new Quiz(course, "Jazz", "Medium", 6);
       // quizDAO.storeOne(TestTom);
        // Methodes aanroepen om het csv weg te schrijven naar een ArrayList met Quizzen.
        List<String> test = FileReaderToArray();
        List<Quiz> quizList = listQuiz(test, dbAccess);
       //saveQuizFromArray(dbAccess, quizList, quizDAO);
        // Quizzen opslaan in de database. Even achter "//", anders herhaalt de opdracht zich en heb je teveel info in de DBMS
        saveQuizFromArray(dbAccess, quizList, quizDAO);
        dbAccess.closeConnection();
    }

    // Methode om Strings vanuit de csv op te slaan in een ArrayList.
    public static List<String> FileReaderToArray() {
        List<String> linesFromFile = new ArrayList<>();
        try {
            Scanner input = new Scanner(userFile);
            while (input.hasNextLine()) {
                linesFromFile.add(input.nextLine());
            }
        } catch (FileNotFoundException notFound) {
            System.out.println("File not found.");
        }
        return linesFromFile;
    }

    // Methode om een array van quizzen als object aan te maken.
    public static List<Quiz> listQuiz(List<String> list, DBAccess dbAccess) {
        UserDAO userDAO = new UserDAO(dbAccess);
        CourseDAO courseDAO = new CourseDAO(dbAccess, userDAO);
        List<Quiz> quizList = new ArrayList<>();
        for (String s : list) {
            String[] lineArray = s.split(",");
            String quizName = lineArray[0];
            String difficulty = lineArray[1];
            int amountQuestion = Integer.parseInt(lineArray[2]);
            String courseName = lineArray[3];
            Course course = courseDAO.getOneByName(courseName);
            quizList.add(new Quiz(course, quizName, difficulty, amountQuestion));
        }
        return quizList;
    }

    // Methode om na opening van de database de quizzen uit een ArrayList te halen en ze via de QuizDAO
    // op te slaan in de database. Met afsluiten van de database.
    private static void saveQuizFromArray(DBAccess dbAccess, List<Quiz> quizList, QuizDAO quizDAO) {
        dbAccess.openConnection();
        for (Quiz quiz : quizList) {
            quizDAO.storeOne(quiz);
        }
        dbAccess.closeConnection();
    }

}
