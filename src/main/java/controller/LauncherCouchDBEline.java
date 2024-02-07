package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javacouchdb.CouchDBAccess;
import javacouchdb.CourseCouchDBDAO;
import model.Course;
import model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LauncherCouchDBEline {
    public static void main(String[] args) {
    // Create test data
        User testUser = new User(1, "Eve", "eve", "Eve", null, "Addams", "Coördinator");
        Course testCourse1 = new Course(1, testUser, "Test Course 1", "Beginner");
        Course testCourse2 = new Course(2, testUser, "Test Course 2", "Medium");
        Course testCourse3 = new Course(3, testUser, "Test Course 3", "Gevorderd");

    // Make list of test users
        List<Course> testCourses = new ArrayList<>();
        testCourses.add(testCourse1);
        testCourses.add(testCourse2);
        testCourses.add(testCourse3);

    // Setup access to couchDB
        CouchDBAccess couchDBAccess = new CouchDBAccess("test_courses_quizmaster", "admin", "admin");
        CourseCouchDBDAO courseCouchDBDAO = new CourseCouchDBDAO(couchDBAccess);

    // Open connection
        couchDBAccess.getClient();

    // Create a course in CouchDB
        for (Course course : testCourses) {
            courseCouchDBDAO.saveSingleCourse(course);
        }

    // Create a .csv from couchDB
        File courseFile = new File("src/resources/coursesFromCouchDB.csv");
        Gson gson = new Gson();
        try {
            Course singleCourse;
            List<JsonObject> allCourses = courseCouchDBDAO.getAllDocuments();
            PrintWriter printWriter = new PrintWriter(courseFile);
            for (JsonObject course : allCourses) {
                singleCourse = gson.fromJson(course, Course.class);
                printWriter.println(singleCourse);
                printWriter.println("JSON: " + gson.toJson(singleCourse));
            }
            printWriter.close();
        } catch (FileNotFoundException fileNotFound){
            System.out.println("Dit bestand kan niet worden gemaakt.");
        }

    // Adding testCourse1 for testing after deleting it
        courseCouchDBDAO.saveSingleCourse(testCourse1);

    // Showing course in terminal
        System.out.println("Course form couchDB:");
        Course getCourse = courseCouchDBDAO.getCourse(1);
        System.out.println(getCourse + " with difficulty: " + getCourse.getDifficultyCourse());

    // Updating course in couchDB
        testCourse1.setDifficultyCourse("Medium");
        courseCouchDBDAO.updateCourse(testCourse1);
        Course updatedCourse = courseCouchDBDAO.getCourse(1);
        System.out.println("Course is updated to medium difficulty:");
        System.out.println(updatedCourse + " with difficulty: " + updatedCourse.getDifficultyCourse());

    // Deleting course form couchDB
        courseCouchDBDAO.deleteCourse(testCourse1);
        System.out.println("Test course 1 is deleted from the system.");
        System.out.println("It now shows as: " + courseCouchDBDAO.getCourse(1));
    }
}
