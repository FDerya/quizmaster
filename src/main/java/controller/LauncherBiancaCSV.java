package controller;
// Bianca Duijvesteijn, studentnummer 500940421
// Reads CSV data into a list, and creating a list of Group objects,
// which are then stored in the database

import database.mysql.*;
import model.Course;
import model.Group;
import model.User;
import view.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LauncherBiancaCSV {
    // File and path names
    private static final String filepath = "src/main/java/database/Groepen.csv";
    private static final File userFile = new File(filepath);

    public static void main(String[] args) {
        // Configure access to the database
        DBAccess dbAccess = Main.getDBaccess();

        // Initialization of data access objects
        UserDAO userDAO = new UserDAO(Main.getDBaccess());
        CourseDAO courseDAO = new CourseDAO(Main.getDBaccess());
        GroupDAO groupDAO = new GroupDAO(dbAccess, userDAO, courseDAO);

        // Read data from the CSV file
        List<String> test = FileReaderToArray();
        List<Group> listGroups = listGroups(test, userDAO, courseDAO);
        for (Group group : listGroups) {
            groupDAO.storeOne(group);
        }
        Main.getDBaccess().closeConnection();
        System.out.println("Connectie gesloten");
    }

    // Method to read data from the CSV file into a list
    public static List<String> FileReaderToArray() {
        List<String> linesFromFile = new ArrayList<>();
        try {
            Scanner input = new Scanner(userFile);
            while (input.hasNextLine()) {
                linesFromFile.add(input.nextLine());
            }
        } catch (FileNotFoundException notFound) {
            notFound.printStackTrace();
            System.out.println("File not found.");
        }
        return linesFromFile;
    }

    // Method to create a list of Group objects from a list of strings
    public static List<Group> listGroups(List<String> list, UserDAO userDAO, CourseDAO courseDAO) {
        List<Group> groupList = new ArrayList<>();

        for (String s : list) {
            String[] lineArray = s.split(",");
            Course course = courseDAO.getOneByName(lineArray[0]);
            String groupName = lineArray[1];
            int numberOfStudents = Integer.parseInt(lineArray[2]);
            User user = userDAO.getOneByUsername(lineArray[3]);

            groupList.add(new Group(0,course,groupName,numberOfStudents,user));
        }
        return groupList;
    }
}
