package controller;

import javacouchdb.CouchDBAccess;
import javacouchdb.UserCouchDBDAO;
import model.User;

public class DennisNoSQLLauncher {
    public static void main(String[] args) {
        // Aanmaken testdata
        User testUser = new User(1, "Test", "test", "Frits", null, "Sissing", "Student");

        // Initialize couchDB access
        CouchDBAccess couchDBAccess = new CouchDBAccess("quizmaster", "admin", "admin");
        UserCouchDBDAO userCouchDBDAO = new UserCouchDBDAO(couchDBAccess);

        // Open connection
        couchDBAccess.getClient();

        // Create user
        userCouchDBDAO.saveSingleUser(testUser);

        // Read user
        System.out.println("Student uit couchDB gehaald met idUser = 1:");
        System.out.println(userCouchDBDAO.getUser(1));

        // Update user
        testUser.setRole("Docent");
        userCouchDBDAO.updateUser(testUser);
        System.out.println("The student becomes the master:");
        System.out.println(userCouchDBDAO.getUser(1));

        // Delete user
        userCouchDBDAO.deleteUser(testUser);
        System.out.println("\nHelaas wordt Frits ontslagen en uit het systeem verwijderd");
        System.out.println("Hier stond ooit Frits Sissing, maar nu: " + userCouchDBDAO.getUser(1));
    }
}
