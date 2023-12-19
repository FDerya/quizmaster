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

public class LauncherTom {
    private static final String filepath = "src/main/java/database/Quizzen.csv";
    private static final File userFile = new File(filepath);

    public static void main(String[] args) {
        final String databaseName = "Quizmaster";
        final String mainUser = "userQuizmaster";
        final String mainUserPassword = "pwQuizmaster";
        DBAccess dBaccess = new DBAccess(databaseName, mainUser, mainUserPassword);
        dBaccess.openConnection();
        UserDAO userDAO = new UserDAO(dBaccess);
        CourseDAO courseDAO = new CourseDAO(dBaccess, userDAO);
        QuizDAO quizDAO = new QuizDAO(dBaccess, userDAO);

        // Aanroepen methodes om het csv weg te schrijven naar uiteindelijk een ArrayList met Quizzen.
        List<String> test = FileReaderToArray();
        List<Quiz> quizList = listQuiz(test, dBaccess);
        saveQuizFromArray(dBaccess, quizList, quizDAO);

        // Het opslaan van de quizzen in de database. Gecomment omdat de quizzen er anders meerdere keren in voor
        // kunnen komen.
        // saveQuizFromArray(dBaccess, quizList, quizDAO);
        dBaccess.closeConnection();
    }

    // Deze methode leest een csv-bestand in en slaat deze regel voor regel op in een ArrayList van Strings.
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

    // Deze methode leest een ArrayList van Strings, splitst elke regel in een array van Strings
    // en maakt vervolgens een object Quiz vanuit de array.
    public static List<Quiz> listQuiz(List<String> list, DBAccess dBAccess) {
        UserDAO userDAO = new UserDAO(dBAccess);
        CourseDAO courseDAO = new CourseDAO(dBAccess, userDAO);
        List<Quiz> quizList = new ArrayList<>();
        for (String s : list) {
            String[] lineArray = s.split(",");
            String quizName = lineArray[0];
            String difficulty = lineArray[1];
            int difficultyCourse = 1;
            if (difficulty.equals("Gevorderd")) {
                difficultyCourse = 3;
            } else if (difficulty.equals("Medium")) {
                difficultyCourse = 2;
            }
            int amountQuestion = Integer.parseInt(lineArray[2]);
            String courseName = lineArray[3];
            Course course = courseDAO.getOneByName(courseName);
            quizList.add(new Quiz(course, quizName, difficultyCourse, amountQuestion));
        }
        return quizList;
    }

    // Deze methode opent de database, haalt de quizzen uit een ArrayList van Quizzen en slaat ze via de QuizDAO
    // op in de database. Daarna wordt de database gesloten.
    private static void saveQuizFromArray(DBAccess dBaccess, List<Quiz> quizList, QuizDAO quizDAO) {
        dBaccess.openConnection();
        for (Quiz quiz : quizList) {
            quizDAO.storeOne(quiz);
        }
        dBaccess.closeConnection();
    }

}
