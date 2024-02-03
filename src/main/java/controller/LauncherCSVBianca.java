package controller;
// Reads CSV data into a list, and creating a list of Group objects,
// which are then stored in the database

import database.mysql.*;
import model.Course;
import model.Group;
import model.User;
import view.Main;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LauncherCSVBianca {
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


        // Insert groups into the database if they don't already exist
        for (Group group : listGroups) {
            if (!groupDAO.groupExists(group)) {
                groupDAO.storeOne(group);
            } else {
                System.out.println(group.getCourse().getNameCourse() + ", " + group.getGroupName()
                        + ", already exists in the database.");
            }
        }

        // Write group overview
        writeGroupsToFile(listGroups);

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

    // Writes group information along with associated students' details to a file named "group_overview.txt"
    public static void writeGroupsToFile(List<Group> groups) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("group_overview.txt"))) {
            ParticipationDAO participationDAO = new ParticipationDAO(Main.getDBaccess());

            for (Group group : groups) {
                writeGroupInfo(writer, group);
                writeStudentsInfo(writer, participationDAO, group.getIdGroup());
            }
            System.out.println("Groepsoverzicht succesvol opgeslagen in group_overview.txt");
        } catch (IOException | SQLException e) {
            System.err.println("Fout bij het schrijven naar het bestand: " + e.getMessage());
        }
    }

    // Writes specific group details
    private static void writeGroupInfo(BufferedWriter writer, Group group) throws IOException {
        writer.write("Groep: " + group.getGroupName() + "\n");
        writer.write("Cursus: " + group.getCourse().getNameCourse() + "\n");
        writer.write("Aantal studenten: " + group.getAmountStudent() + "\n");
        writer.write("Docent: " + group.getTeacher().getFullName() + "\n");
    }

    // Writes the full names of students associated with a particular group
    private static void writeStudentsInfo(BufferedWriter writer, ParticipationDAO participationDAO,
                                          int groupId) throws IOException, SQLException {
        writer.write("Studenten:\n");
        List<String> studentFullNames = participationDAO.getStudentsFullNamesByGroupId(groupId);
        for (String studentFullName : studentFullNames) {
            writer.write("- " + studentFullName + "\n");
        }
        writer.write("\n");
    }

}



