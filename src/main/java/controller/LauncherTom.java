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
    // Eerst locatie benoemen csv
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

        // Methodes aanroepen om het csv weg te schrijven naar een ArrayList met Quizzen.
        List<String> test = FileReaderToArray();
        List<Quiz> quizList = listQuiz(test, dBaccess);
        saveQuizFromArray(dBaccess, quizList, quizDAO);

        // Quizzen opslaan in de database. Even achter "//", anders herhaalt de opdracht zich en heb je teveel info in de DBMS
        // saveQuizFromArray(dBAccess, quizList, quizDAO);
        dBaccess.closeConnection();
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
    public static List<Quiz> listQuiz(List<String> list, DBAccess dBAccess) {
        UserDAO userDAO = new UserDAO(dBAccess);
        CourseDAO courseDAO = new CourseDAO(dBAccess, userDAO);
        List<Quiz> quizList = new ArrayList<>();
        for (String s : list) {
            String[] lineArray = s.split(",");
            String quizName = lineArray[0];
            String difficulty = lineArray[1];
            int amountQuestion = Integer.parseInt(lineArray[2]);
            String courseName = lineArray[3];
            Course course = courseDAO.getOneByName(courseName);
            quizList.add(new Quiz(course, quizName, getLevelQuiz(difficulty), amountQuestion));
        }
        return quizList;
    }

    // Methode om na opening van de database de quizzen uit een ArrayList te halen en ze via de QuizDAO
    // op te slaan in de database. Met afsluiten van de database.
    private static void saveQuizFromArray(DBAccess dBaccess, List<Quiz> quizList, QuizDAO quizDAO) {
        dBaccess.openConnection();
        for (Quiz quiz : quizList) {
            quizDAO.storeOne(quiz);
        }
        dBaccess.closeConnection();
    }
    //Methode om de String "Beginner/Medium/Gevorderd" om te zetten in int difficulty (1, 2, 3)
    private static int getLevelQuiz(String difficulty){
        int difficultyQuiz = 1;
         if (difficulty.equals("Medium")) {
             difficultyQuiz = 2;
         } else if (difficulty.equals("Gevorderd")) {
                difficultyQuiz = 3;
        }return difficultyQuiz;
    }

}
