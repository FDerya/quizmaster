package controller;

import database.mysql.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import model.*;
import view.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;


    public class LauncherCSVTom {
        // Eerst locatie benoemen csv
        private static final String filepath = "src/main/java/database/Quizzen.csv";
        private static final File userFile = new File(filepath);

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
            //saveQuizToTXT(dbAccess, listQuiz, quizDAO, user);

            dbAccess.closeConnection();
        }
            public void start(Stage stage) {
                ImageView imgView = new ImageView("UIControls/Save.png");
                imgView.setFitWidth(20);
                imgView.setFitHeight(20);
                Menu file = new Menu("File");
                MenuItem item = new MenuItem("Save", imgView);
                file.getItems().addAll(item);
                //Creating a File chooser
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save");
                fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
                //Adding action on the menu item
                item.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        //Opening a dialog box
                        fileChooser.showSaveDialog(stage);
                    }
                });
                //Creating a menu bar and adding menu to it.
                MenuBar menuBar = new MenuBar(file);
                Group root = new Group(menuBar);
                Scene scene = new Scene(root, 595, 355, Color.BEIGE);
                stage.setTitle("File Chooser Example");
                stage.setScene(scene);
                stage.show();
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

        private static void saveQuizToCSV(DBAccess dbAccess, List<Quiz> listQuiz, QuizDAO quizDAO) {
            dbAccess.openConnection();
            try {
                PrintWriter printWriter = new PrintWriter(fileCSV);
                for (Quiz quiz : listQuiz) {
                    printWriter.printf("%d, %s, %s, %s, %d\n", quiz.getIdQuiz(), quiz.getCourse(), quiz.getNameQuiz(), quiz.getLevel(), quiz.getAmountQuestions());
                }
                printWriter.close();

            } catch (FileNotFoundException fileNotFoundException) {
                System.out.println("File not found: " + fileNotFoundException);
            }
        }

        /*private static void saveQuizToTXT(DBAccess dbAccess, List<Quiz> listQuiz, QuizDAO quizDAO, User user) {
            dbAccess.openConnection();
            int amountQuiz = listQuiz.size();
            int amountQuestion = questionDAO.getQuestionCountFromUser(user);
            double avgQuestion = ((amountQuestion *10/amountQuiz)/10.0);
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
*/
    }
