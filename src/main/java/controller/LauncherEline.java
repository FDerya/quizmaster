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
    // Initialiseren van courses csv bestand, om database te vullen
    private static final String filepath = "src/main/java/database/Cursussen.csv";
    private static final File courseFile = new File(filepath);

    public static void main(String[] args) {
        // Database access, UserDAO en CourseDAO objecten aanmaken
        final String databaseName = "Quizmaster";
        final String mainUser = "userQuizmaster";
        final String mainUserPassword = "pwQuizmaster";
        DBAccess dBaccess = new DBAccess(databaseName, mainUser, mainUserPassword);
        dBaccess.openConnection();
        UserDAO userDAO = new UserDAO(dBaccess);
        CourseDAO courseDAO = new CourseDAO(dBaccess, userDAO);

        // Csv naar een ArrayList wegschrijven
        List<String> test = fileReaderCourseToArray();
        List<Course> courseList = listCourses(test, dBaccess);
        saveCoursesFromArray(dBaccess, courseList, courseDAO);


        // Opslaan van courses in de database


        // Testcourses voor het testen van de launcher
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
    public static List<Course> listCourses(List<String> list, DBAccess dBAccess){
        UserDAO userDAO = new UserDAO(dBAccess);
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
    public static void saveCoursesFromArray(DBAccess dbAccess, List<Course> courseList, CourseDAO courseDAO){
        dbAccess.openConnection();
        for (Course course : courseList){
            courseDAO.storeOne(course);
        }
        dbAccess.closeConnection();
    }

    // Difficulty van beginner/medium/gevorderd naar 1/2/3
    public static int changeDifficultyCourse(String difficulty){
        int coursedifficulty = 1;
        if (difficulty.equals("Gevorderd")){
            coursedifficulty = 3;
        } else if (difficulty.equals("Medium")) {
            coursedifficulty = 2;
        }
        return coursedifficulty;
    }

}
