package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javacouchdb.CouchDBAccess;
import javacouchdb.UserCouchDBDAO;
import model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DennisNoSQLLauncher {
    public static void main(String[] args) {
        Gson gson = new Gson();
        // Making testdata
        User testUserOne = new User(1, "Test", "test", "Frits", null, "Sissing", "Student");
        User testUserTwo = new User(2, "Dekoe", "dekoe", "Dennis", null, "Koelemeijer", "Functioneel Beheerder");
        User testUserThree = new User(3, "Sinterklaas", "Ozosnel", "Sint", "st", "Nicolaas", "Administrator");

        // Creating and filling list of testusers
        List<User> testUsers = new ArrayList<>();
        testUsers.add(testUserOne);
        testUsers.add(testUserTwo);
        testUsers.add(testUserThree);

        // Initialize couchDB access
        CouchDBAccess couchDBAccess = new CouchDBAccess("quizmaster", "admin", "admin");
        UserCouchDBDAO userCouchDBDAO = new UserCouchDBDAO(couchDBAccess);

        // Open connection
        couchDBAccess.getClient();

//        // Create user in couchDB
//        for (User user : testUsers) {
//            userCouchDBDAO.saveSingleUser(user);
//        }

        // Write database to .csv file
        File userFile = new File("src/resources/usersFromCouchDB.csv");
        try {
            PrintWriter printWriter = new PrintWriter(userFile);
            for (JsonObject user : userCouchDBDAO.getAllDocuments()) {
                String userToJson = gson.toJson(user);
                System.out.println(userToJson);}
            printWriter.close();
        } catch (FileNotFoundException fileNotFound) {
            System.out.println("Het bestand kan niet worden aangemaakt");
        }


//        // Read user
//        System.out.println("Student uit couchDB gehaald met idUser = 1:");
//        System.out.println(userCouchDBDAO.getUser(1));
//
//        // Update user
//        testUserOne.setRole("Docent");
//        userCouchDBDAO.updateUser(testUserOne);
//        System.out.println("The student becomes the master:");
//        System.out.println(userCouchDBDAO.getUser(1));
//
//        // Delete user
//        userCouchDBDAO.deleteUser(testUserOne);
//        System.out.println("\nHelaas wordt Frits ontslagen en uit het systeem verwijderd");
//        System.out.println("Hier stond ooit Frits Sissing, maar nu: " + userCouchDBDAO.getUser(1));
    }
}
