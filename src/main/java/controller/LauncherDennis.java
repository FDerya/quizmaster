// Launcher gemaakt door Dennis Koelemeijer, studentnummer 500940711
// Doel van deze launcher is om een aantal eigen werkzaamheden te bewijzen. Dit wordt per werkzaamheid beschreven in de comments.

package controller;

import database.mysql.DBAccess;
import database.mysql.UserDAO;
import model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LauncherDennis {
    // Nodig voor het initialiseren van het gebruikers csv bestand, om de database te vullen.
    private static final String filepath = "src/main/java/database/Gebruikers.csv";
    private static final File userFile = new File(filepath);

    public static void main(String[] args) {
        // Aanmaken database access object en userDAO object.
        final String databaseName = "Quizmaster";
        final String mainUser = "userQuizmaster";
        final String mainUserPassword = "pwQuizmaster";
        DBAccess dBaccess = new DBAccess(databaseName, mainUser, mainUserPassword);
        UserDAO userDAO = new UserDAO(dBaccess);

        // Aanroepen methodes om het csv weg te schrijven naar uiteindelijk een ArrayList met Users.
        List<String> test = FileReaderToArray();
        List<User> userList = listUsers(test);

        // Het opslaan van de gebruikers in de database
        dBaccess.openConnection();
        for (User user : userList) {
            userDAO.storeOne(user);
        }
        dBaccess.closeConnection();
    }

    // Deze methode leest een csv-bestand in en slaat deze regel voor regel op in een ArrayList van Strings.
    public static List<String> FileReaderToArray() {
        List<String> linesFromFile = new ArrayList<>();
        try {
            Scanner input = new Scanner(userFile);
            while (input.hasNextLine()) {
                linesFromFile.add(input.nextLine());
            }
        } catch (FileNotFoundException notFound) {
            System.out.println("File not found.");
        }
        return linesFromFile;
    }

    // Deze methode leest een ArrayList van Strings, splitst elke regel in een array van Strings
    // en maakt vervolgens een object User vanuit de array.
    public static List<User> listUsers(List<String> list) {
        List<User> userList = new ArrayList<>();
        for (String s : list) {
            String[] lineArray = s.split(",");
            String username = lineArray[0];
            String password = lineArray[1];
            String firstName = lineArray[2];
            String prefix = lineArray[3];
            String surname = lineArray[4];
            String role = lineArray[5];
            userList.add(new User(username, password, firstName, prefix, surname, role));
        }
        return userList;
    }

}
