package controller;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.UserDAO;
import model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LauncherEline {
// Database access, UserDAO en CourseDAO objecten aanmaken
    final static String databaseName = "Quizmaster";
    final static String mainUser = "userQuizmaster";
    final static String mainUserPassword = "pwQuizmaster";
    static DBAccess dBaccess = new DBAccess(databaseName, mainUser, mainUserPassword);
    static UserDAO userDAO = new UserDAO(dBaccess);
    static CourseDAO courseDAO = new CourseDAO(dBaccess, userDAO);

// Initialiseren van courses csv bestand, om database te vullen
    private static final String filepath = "src/main/java/database/Cursussen.csv";
    private static final File courseFile = new File(filepath);

    public static void main(String[] args) {
        dBaccess.openConnection();
        // Csv naar een ArrayList wegschrijven
        List<String> test = fileReaderCourseToArray();
        List<Course> courseList = listCourses(test);

        // Opslaan van courses in de database
        saveCoursesFromArray(courseList, courseDAO);

        dBaccess.closeConnection();
    }

    // Leest csv bestand in en slaat deze op in ArrayList
    public static List<String> fileReaderCourseToArray(){
        List<String> linesFromFile = new ArrayList<>();
        try {
            Scanner input = new Scanner(courseFile);
            while (input.hasNextLine()){
                linesFromFile.add(input.nextLine());
            }
        } catch (FileNotFoundException fileNotFound){
            System.out.println("File not found");
        }
        return linesFromFile;
    }

    // Leest Arraylist fileReaderCourseToArray() en maakt een list van Courses
    public static List<Course> listCourses(List<String> list){
        UserDAO userDAO = new UserDAO(dBaccess);
        List<Course> courseList = new ArrayList<>();
        for (String s : list){
            String[] lineArray = s.split(",");
            String nameCourse = lineArray[0];
            String difficultyCourse = lineArray[1];
            String username = lineArray[2];
            User user = userDAO.getOneByUsername(username);
            courseList.add(new Course(user, nameCourse, difficultyCourse));
        }
        return courseList;
    }

    // Sla Course via CourseDAO op in database
    public static void saveCoursesFromArray(List<Course> courseList, CourseDAO courseDAO){
        dBaccess.openConnection();
        for (Course course : courseList){
            courseDAO.storeOne(course);
        }
        dBaccess.closeConnection();
    }
}
