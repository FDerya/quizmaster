package controller;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.GroupDAO;
import database.mysql.UserDAO;
import model.Course;
import model.Group;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class LauncherBianca {
    // Bestands- en padnamen
    private static final String filepath = "src/main/java/database/Groep.csv";
    private static final File userFile = new File(filepath);
    // Indexen voor CSV-bestand
    private static final int INDEX_ID_GROUP = 0;
    private static final int INDEX_ID_TEACHER = 1;
    private static final int INDEX_GROUP_NAME = 2;
    private static final int INDEX_COURSE_NAME = 3;
    private static final int INDEX_NUM_OF_STUDENTS = 4;
    private static final int INDEX_USER_NAME = 5;

    public static void main(String[] args) {
        //Configureer de toegang tot de database

        final String databaseName = "Quizmaster";
        final String mainUser = "userQuizmaster";
        final String mainUserPassword = "pwQuizmaster";
        DBAccess dBaccess = new DBAccess(databaseName, mainUser, mainUserPassword);

        // Initialisatie van data access objecten
        UserDAO userDAO = new UserDAO(dBaccess);
        CourseDAO courseDAO = new CourseDAO(dBaccess, userDAO);
        GroupDAO groupDAO = new GroupDAO(dBaccess, userDAO);

        // Lees gegevens uit het CSV-bestand
        List<String> test = FileReaderToArray();
        List<Group> listGroups = listGroups(test, userDAO, courseDAO);

        try {
            // Open de databaseconnectie
            dBaccess.openConnection();
            // Sla elke groep op in de database
            for (Group group : listGroups) {
                groupDAO.storeOne(group);
            }
        } catch (Exception e) {
            // Vang eventuele uitzonderingen af en druk de stack trace af
            e.printStackTrace();
        } finally {
            // Sluit de databaseconnectie, ongeacht of er fouten zijn opgetreden
            dBaccess.closeConnection();
        }
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
            // Vang FileNotFoundException af en druk de stack trace af
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
            int idGroup = Integer.parseInt(lineArray[INDEX_ID_GROUP]);
            int idTeacher = Integer.parseInt(lineArray[INDEX_ID_TEACHER]);
            String groupName = lineArray[INDEX_GROUP_NAME];
            String courseName = lineArray[INDEX_COURSE_NAME];
            int numberOfStudents = Integer.parseInt(lineArray[INDEX_NUM_OF_STUDENTS]);
            String userName = lineArray[INDEX_USER_NAME];

            // Maak een nieuwe User en Course instantie
            User user = userDAO.getOneById(Integer.parseInt(userName));
            Course course = courseDAO.getOneById(Integer.parseInt(courseName));

            // Maak een nieuwe Group-instantie en voeg toe aan de lijst
            groupList.add(new Group(idGroup, idTeacher, course, groupName, numberOfStudents, user));
        }
        return groupList;
    }
}

