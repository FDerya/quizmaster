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
//        List<String> test = FileReaderToArray();
//        List<Group> listGroups = listGroups(test, userDAO, courseDAO);

//        for (Group group : listGroups) {
//            groupDAO.storeOne(group);
//        }

        // Lees gegevens uit het CSV-bestand en vul de "participation" tabel
        List<String> test = FileReaderToArray();
        List<Group> listGroups = listGroups(test, userDAO, courseDAO);

        for (Group group : listGroups) {
            storeGroupAndParticipation(groupDAO, group);
        }


        dbAccess.closeConnection();
    }
    private static void storeGroupAndParticipation(GroupDAO groupDAO, Group group) {
        groupDAO.storeOne(group);

        // Haal de werkelijke gegevens op voor de invoegoperatie
        int groupId = group.getIdGroup();
        Course course = group.getCourseName();
        int userId = group.getUserName().getIdUser();

        System.out.println("groupId: " + groupId + ", courseId: " + course.getIdCourse() + ", userId: " + userId);

        // Controleer of de groep bestaat
        if (groupId > 0) {
            // Voer de invoegoperatie uit in de "participation" tabel
            fillParticipationTable(groupId, course.getIdCourse(), userId);
        } else {
            System.err.println("Groep met ID " + groupId + " bestaat niet.");
        }
    }



    // Methode om gegevens in te voegen in de "participation" tabel
    private static void fillParticipationTable(int groupId, int courseId, int userId) {
        String insertQuery = "INSERT INTO participation (idGroup, idCourse, idUser) VALUES (?, ?, ?)";

        try (
                Connection connection = DriverManager.getConnection(String.valueOf(Main.getDBaccess()));
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)
        ) {
            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, courseId);
            preparedStatement.setInt(3, userId);
            preparedStatement.executeUpdate();

            System.out.println("Invoegen van participatiegegevens geslaagd.");
        } catch (SQLException e) {
            System.err.println("SQL-fout: " + e.getMessage());
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

