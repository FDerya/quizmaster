package controller;

import database.mysql.DBAccess;
import database.mysql.QuestionDAO;
import database.mysql.UserDAO;
import model.Course;
import model.Question;
import model.Quiz;
import model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LauncherDennis {
    private static final String filepath = "src/main/java/database/Gebruikers.csv";
    private static final File userFile = new File(filepath);

    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String PREFIX_CONNECTION_URL =
            "jdbc:mysql://localhost:3306/";
    private static final String CONNECTION_SETTINGS = "?useSSL=false" +
            "&allowPublicKeyRetrieval=true" +
            "&useJDBCCompliantTimezoneShift=true" +
            "&useLegacyDatetimeCode=false" +
            "&serverTimezone=UTC";

    public static void main(String[] args) {
        final String databaseName = "Quizmaster";
        final String mainUser = "userQuizmaster";
        final String mainUserPassword = "pwQuizmaster";
        DBAccess dBaccess = new DBAccess(databaseName, mainUser, mainUserPassword);
        UserDAO userDAO = new UserDAO(dBaccess);

        List<String> test = FileReaderToArray();
        List<User> userList = listUsers(test);

        dBaccess.openConnection();
        for (User user : userList) {
            userDAO.storeOne(user);
        }
        dBaccess.closeConnection();
    }

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

    public static List<User> listUsers(List<String> list) {
        List<User> userList = new ArrayList<>();
        for (String s : list) {
            String[] lineArray = s.split(",");
            String username = lineArray[0];
            String password = lineArray[1];
            String firstName = lineArray[2];
            String prefix = lineArray[3];
            String surname = lineArray[4];
            String role = lineArray[5];
            userList.add(new User(username, password, firstName, prefix, surname, role));
        }
        return userList;
    }

}
