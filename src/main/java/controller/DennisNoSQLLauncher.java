package controller;

import com.google.gson.JsonObject;
import javacouchdb.CouchDBAccess;
import javacouchdb.UserCouchDBDAO;
import model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DennisNoSQLLauncher {
    public static void main(String[] args) {
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

        // Create user in couchDB
        for (User user : testUsers) {
            userCouchDBDAO.saveSingleUser(user);
        }

        // Write database to .csv file, sorted by idUser
        File userFile = new File("src/resources/usersFromCouchDB.csv");
        try {
            // ik ben aan het werk
            List<JsonObject> allUsers = userCouchDBDAO.getAllDocuments();
            PrintWriter printWriter = new PrintWriter(userFile);
            allUsers.sort(Comparator.comparing(user -> user.get("idUser").getAsInt()));
            for (JsonObject user : allUsers) {
                user.remove("_id");
                user.remove("_rev");
                printWriter.println(user.getAsJsonObject());
            }
            printWriter.close();
        } catch (FileNotFoundException fileNotFound) {
            System.out.println("Het bestand kan niet worden aangemaakt");
        }


        // Read user
        System.out.println("Student uit couchDB gehaald met idUser = 1:");
        User getUser = userCouchDBDAO.getUser(1);
        System.out.println(getUser + ", " + getUser.getRole());

        // Update user
        testUserOne.setRole("Docent");
        userCouchDBDAO.updateUser(testUserOne);
        User updatedUser = userCouchDBDAO.getUser(1);
        System.out.println("The student becomes the master:");
        System.out.println(updatedUser + ", " + updatedUser.getRole());

        // Delete user
        userCouchDBDAO.deleteUser(testUserOne);
        System.out.println("\nHelaas wordt Frits ontslagen en uit het systeem verwijderd");
        System.out.println("Hier stond ooit Frits Sissing, maar nu: " + userCouchDBDAO.getUser(1));
    }
}
