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
import java.util.Comparator;
import java.util.List;

public class LauncherCouchDBDennis {
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
        CouchDBAccess couchDBAccess = new CouchDBAccess("usersinquizmaster", "admin", "admin");
        UserCouchDBDAO userCouchDBDAO = new UserCouchDBDAO(couchDBAccess);

        // Open connection
        couchDBAccess.getClient();

        // Create user in couchDB
        for (User user : testUsers) {
            userCouchDBDAO.saveSingleUser(user);
        }

        // Write database to .csv file, sorted by idUser.
        // Per user: first line is the toString of the user, second line are the JSON values
        File userFile = new File("src/resources/usersFromCouchDB.csv");
        Gson gson = new Gson();
        try {
            User singleUser;
            List<JsonObject> allUsers = userCouchDBDAO.getAllDocuments();
            PrintWriter printWriter = new PrintWriter(userFile);
            allUsers.sort(Comparator.comparing(user -> user.get("idUser").getAsInt()));
            for (JsonObject user : allUsers) {
                singleUser = gson.fromJson(user, User.class);
                printWriter.println("Gebruiker: " + singleUser);
                printWriter.println("JSON waarde: " + gson.toJson(singleUser));
            }
            printWriter.close();
        } catch (FileNotFoundException fileNotFound) {
            System.out.println("Het bestand kan niet worden aangemaakt");
        }


        // Reads user
        System.out.println("Student uit couchDB gehaald met idUser = 1:");
        User getUser = userCouchDBDAO.getUser(1);
        System.out.println(getUser + ", " + getUser.getRole());

        // Updates user
        testUserOne.setRole("Docent");
        userCouchDBDAO.updateUser(testUserOne);
        User updatedUser = userCouchDBDAO.getUser(1);
        System.out.println("The student becomes the master:");
        System.out.println(updatedUser + ", " + updatedUser.getRole());

        // Deletes user
        userCouchDBDAO.deleteUser(testUserOne);
        System.out.println("\nHelaas wordt Frits ontslagen en uit het systeem verwijderd");
        System.out.println("Hier stond ooit Frits Sissing, maar nu: " + userCouchDBDAO.getUser(1));
    }
}
