package view;

import database.mysql.DBAccess;
import database.mysql.UserDAO;
import model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LauncherDennis {
    private static final String filepath = "src/main/java/database/Gebruikers.csv";
    private static final File userFile = new File(filepath);

    public static void main(String[] args) {
        DBAccess dBaccess = new DBAccess("Quizmaster", "userQuizmaster", "pwQuizmaster");
        UserDAO userDAO = new UserDAO(dBaccess);

        List<String> test = FileReaderToArray();
        List<User> userList = listUsers(test);

        for (User user : userList) {
            userDAO.storeOne(user);
        }
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
