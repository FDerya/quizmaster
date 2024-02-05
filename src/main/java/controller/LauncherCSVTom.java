package controller;

import database.mysql.*;

import model.*;
import view.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


    public class LauncherCSVTom {
        // Eerst locatie benoemen csv
        private static final String filepath = "src/main/java/database/Quizzen.csv";
        private static final File quizFile = new File(filepath);

        private static DBAccess dbAccess = null;
        private static QuestionDAO questionDAO = new QuestionDAO(Main.getDBaccess());
        private static File fileCSV = new File("src/main/java/database/testQuiz.csv");
        private static File fileTXT = new File("src/main/java/database/saveQuizTXT.txt");
            public static void main(String[] args) throws FileNotFoundException {
            final String databaseName = "Quizmaster";
            final String mainUser = "userQuizmaster";
            final String mainUserPassword = "pwQuizmaster";
            DBAccess dbAccess = new DBAccess(databaseName, mainUser, mainUserPassword);
            dbAccess.openConnection();
            UserDAO userDAO = new UserDAO(dbAccess);
            CourseDAO courseDAO = new CourseDAO(dbAccess, userDAO);
            QuizDAO quizDAO = new QuizDAO(dbAccess, userDAO);
            User user = userDAO.getOneById(120);
            // Course course = new Course(21, "c1", "medium");
            // Quiz TestTom = new Quiz(course, "Jazz", "Medium", 6);
            // quizDAO.storeOne(TestTom);
            // Methodes aanroepen om het csv weg te schrijven naar een ArrayList met Quizzen.
            List<String> test = FileReaderToArray();
            List<Quiz> quizList = listQuiz(test, dbAccess);
            List<Quiz> listQuiz = quizDAO.getQuizzesFromUser(user);

            //Objecten wegschrijven naar een csv-bestand
            //saveQuizToCSV(dbAccess,listQuiz,quizDAO);
            // Quizzen opslaan in de database. Even achter "//", anders herhaalt de opdracht zich en heb je teveel info in de DBMS
            // saveQuizFromArray(dbAccess, quizList, quizDAO);
            saveQuizToTXT(dbAccess, listQuiz, quizDAO, user);

            dbAccess.closeConnection();
        }

            // Methode om Strings vanuit de csv op te slaan in een ArrayList.
        public static List<String> FileReaderToArray() {
            List<String> linesFromFile = new ArrayList<>();
            try {
                Scanner input = new Scanner(quizFile);
                while (input.hasNextLine()) {
                    linesFromFile.add(input.nextLine());
                }
            } catch (FileNotFoundException notFound) {
                System.out.println("Bestand niet gevonden.");
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

        private static void saveQuizToCSV(DBAccess dbAccess, List<Quiz> listQuiz, QuizDAO quizDAO) {
            dbAccess.openConnection();
            try {
                PrintWriter printWriter = new PrintWriter(fileCSV);
                for (Quiz quiz : listQuiz) {
                    printWriter.printf("%d, %s, %s, %s, %d\n", quiz.getIdQuiz(), quiz.getCourse(), quiz.getNameQuiz(), quiz.getLevel(), quiz.getMinimumAmountCorrectQuestions());
                }
                printWriter.close();

            } catch (FileNotFoundException fileNotFoundException) {
                System.out.println("File not found: " + fileNotFoundException);
            }
        }

        private static void saveQuizToTXT(DBAccess dbAccess, List<Quiz> listQuiz, QuizDAO quizDAO, User user) {
            dbAccess.openConnection();
            int amountQuiz = listQuiz.size();
            int amountQuestion = questionDAO.getQuestionCountForUser(user);
            double avgQuestion = (Math.round(amountQuestion *10/amountQuiz)/10.0);
            try {
                PrintWriter printWriter = new PrintWriter(fileTXT);
                printWriter.printf("%-30s %-30s %-15s %-10s\n", "Cursus", "Quiznaam", "Level", "Aantal vragen per quiz");
                for (Quiz quiz : listQuiz) {
                    printWriter.printf("%-30s %-30s %-15s %-10d\n", quiz.getCourse(), quiz.getNameQuiz(), quiz.getLevel(), questionDAO.getQuestionCountForQuiz(quiz));
                }
                printWriter.println("\nEr zijn "+amountQuiz+ " quizzen met "+amountQuestion+ " vragen, gemiddelde = "+ avgQuestion);
                printWriter.close();

            } catch (FileNotFoundException fileNotFoundException) {
                System.out.println("File not found: " + fileNotFoundException);
            }
        }

    }
