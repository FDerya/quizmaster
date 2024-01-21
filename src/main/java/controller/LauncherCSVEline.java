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

public class LauncherCSVEline {
// Create database access, UserDAO and CourseDAO objects
    final static String databaseName = "Quizmaster";
    final static String mainUser = "userQuizmaster";
    final static String mainUserPassword = "pwQuizmaster";
    static DBAccess dbAccess = new DBAccess(databaseName, mainUser, mainUserPassword);
    static UserDAO userDAO = new UserDAO(dbAccess);
    static CourseDAO courseDAO = new CourseDAO(dbAccess, userDAO);

// Initialize courses of csv file, to fill the database
    private static final String filepath = "src/main/java/database/Cursussen.csv";
    private static final File courseFile = new File(filepath);


    public static void main(String[] args) {
        dbAccess.openConnection();
        // Csv to an ArrayList
        List<String> test = fileReaderCourseToArray();
        List<Course> courseList = listCourses(test);

        // Save courses in database
        saveCoursesFromArray(courseList, courseDAO);

        dbAccess.closeConnection();
    }

    // Reads csv file and saves it in an ArrayList
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

    // Reads Arraylist fileReaderCourseToArray() and creates a list of Courses
    public static List<Course> listCourses(List<String> list){
        UserDAO userDAO = new UserDAO(dbAccess);
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

    // Saves Course via CourseDAO in database
    public static void saveCoursesFromArray(List<Course> courseList, CourseDAO courseDAO){
        dbAccess.openConnection();
        for (Course course : courseList){
            courseDAO.storeOne(course);
        }
        dbAccess.closeConnection();
    }

    public static DBAccess getDbAccess() {
        return dbAccess;
    }
}
