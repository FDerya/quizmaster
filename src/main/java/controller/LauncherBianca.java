package controller;
// Bianca Duijvesteijn, studentnummer 500940421
//Wegschrijven van de cvs bestanden. Vullen van de tabel Participation

import database.mysql.*;
import model.Course;
import model.Group;
import model.User;
import view.Main;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static controller.LauncherTom.FileReaderToArray;

public class LauncherBianca {
    // Bestands- en padnamen
    private static final String filepath = "src/main/java/database/Groepen.csv";
    private static final File userFile = new File(filepath);

    public static void main(String[] args) {
        // Configureer de toegang tot de database
        DBAccess dbAccess = Main.getDBaccess();

        // Initialisatie van data access objecten
        UserDAO userDAO = new UserDAO(dbAccess);
        CourseDAO courseDAO = new CourseDAO(dbAccess, userDAO);
        GroupDAO groupDAO = new GroupDAO(dbAccess, userDAO);

        dbAccess.openConnection();

        // Lees gegevens uit het CSV-bestand
        List<String> test = FileReaderToArray();
        List<Group> listGroups = listGroups(test, userDAO, courseDAO);
        for (Group group : listGroups) {
            groupDAO.storeOne(group);
        }
        dbAccess.closeConnection();
    }


    // Methode om gegevens uit het CSV-bestand naar een lijst te lezen
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

    // Methode om een lijst van Group-objecten te maken vanuit een lijst van strings
    public static List<Group> listGroups(List<String> list, UserDAO userDAO, CourseDAO courseDAO) {
        List<Group> groupList = new ArrayList<>();

        for (String s : list) {
            String[] lineArray = s.split(",");
            Course course = courseDAO.getOneByName(lineArray[0]);
            String groupName = lineArray[1];
            int numberOfStudents = Integer.parseInt(lineArray[2]);
            User user = userDAO.getOneByUsername(lineArray[3]);

            groupList.add(new Group(course, groupName, numberOfStudents, user));
        }
        return groupList;
    }

}

