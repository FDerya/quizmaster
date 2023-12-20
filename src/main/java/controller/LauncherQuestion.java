package controller;

import database.mysql.DBAccess;
import database.mysql.QuestionDAO;
import model.Question;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LauncherQuestion {

    private static final String FILE_PATH = "src/main/java/database/Vragen.csv";

    public static void main(String[] args) {
        final String databaseName = "Quizmaster";
        final String mainUser = "root";
        final String mainUserPassword = "ftm7874tr";

        try {
            DBAccess dBaccess = new DBAccess(databaseName, mainUser, mainUserPassword);
            dBaccess.openConnection();

            QuestionDAO questionDAO = new QuestionDAO(dBaccess);

            List<String> linesFromFile = readLinesFromFile(FILE_PATH);
            List<Question> questionList = createQuestionList(linesFromFile);

            saveQuestionsToDatabase(dBaccess, questionList, questionDAO);

            dBaccess.closeConnection();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    private static List<String> readLinesFromFile(String filePath) throws FileNotFoundException {
        List<String> linesFromFile = new ArrayList<>();
        try (Scanner input = new Scanner(new File(filePath))) {
            while (input.hasNextLine()) {
                linesFromFile.add(input.nextLine());
            }
        }
        return linesFromFile;
    }

    private static List<Question> createQuestionList(List<String> lines) {
        List<Question> questionList = new ArrayList<>();
        for (String line : lines) {
            String[] lineArray = line.split(";");
            String questionText = lineArray[0];
            String answerRight = lineArray[1];
            String answerWrong1 = lineArray[2];
            String answerWrong2 = lineArray[3];
            String answerWrong3 = lineArray[4];
            String nameQuiz = lineArray[5];
            questionList.add(new Question( nameQuiz , questionText, answerRight, answerWrong1, answerWrong2, answerWrong3));
        }
        return questionList;
    }

    private static void saveQuestionsToDatabase(DBAccess dBaccess, List<Question> questionList, QuestionDAO questionDAO) {
        dBaccess.openConnection();
        for (Question question : questionList) {
            questionDAO.storeOne(question);
        }
        dBaccess.closeConnection();
    }
}

