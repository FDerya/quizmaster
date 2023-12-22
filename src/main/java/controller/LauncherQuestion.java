package controller;

import database.mysql.DBAccess;
import database.mysql.QuestionDAO;
import database.mysql.QuizDAO;
import model.Question;
import model.Quiz;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LauncherQuestion {

    private static final String FILE_PATH = "src/main/java/database/Vragen.csv";

    public static void main(String[] args) {
        final String databaseName = "Quizmaster";
        final String mainUser = "userQuizmaster";
        final String mainUserPassword = "pwQuizmaster";

        DBAccess dBaccess = new DBAccess(databaseName, mainUser, mainUserPassword);
        dBaccess.openConnection();

        QuestionDAO questionDAO = new QuestionDAO(dBaccess);

        List<String> linesFromFile = readLinesFromFile(FILE_PATH);
        List<Question> questionList = createQuestionList(linesFromFile,dBaccess );

        saveQuestionsToDatabase(dBaccess, questionList, questionDAO);

        dBaccess.closeConnection();
    }

    // Hulpmethode om regels uit een bestand te lezen
    private static List<String> readLinesFromFile(String filePath) {
        List<String> linesFromFile = new ArrayList<>();
        try {
            try (Scanner input = new Scanner(new File(filePath))) {
                while (input.hasNextLine()) {
                    linesFromFile.add(input.nextLine());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
        return linesFromFile;
    }

    // Hulpmethode om Question objecten te maken van de regels
    private static List<Question> createQuestionList(List<String> lines, DBAccess dBaccess) {
        List<Question> questionList = new ArrayList<>();
        for (String line : lines) {
            QuizDAO quizDAO = new QuizDAO(dBaccess);
            String[] lineArray = line.split(";");
            String question = lineArray[0];
            String answerRight = lineArray[1];
            String answerWrong1 = lineArray[2];
            String answerWrong2 = lineArray[3];
            String answerWrong3 = lineArray[4];
            Quiz quiz = quizDAO.getOneByName(lineArray[5]);
            questionList.add(new Question(quiz, question, answerRight, answerWrong1, answerWrong2, answerWrong3));
        }
        return questionList;
    }

    // Hulpmethode om vragen naar de database op te slaan
    private static void saveQuestionsToDatabase(DBAccess dBaccess, List<Question> questionList, QuestionDAO questionDAO) {
        dBaccess.openConnection();
        for (Question question : questionList) {
            questionDAO.storeOne(question);
        }
        dBaccess.closeConnection();
    }
}

