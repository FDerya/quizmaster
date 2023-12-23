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
    // Attributen nodig voor initialiseren database en Filereader.
    private static final String databaseName = "Quizmaster";
    private static final String mainUser = "userQuizmaster";
    private static final String mainUserPassword = "pwQuizmaster";
    private static final String filepathGebruikers = "src/main/java/database/Gebruikers.csv";
    private static final String filepathTestusers = "src/main/java/database/testusers.csv";
    private static final File userFileGebruikers = new File(filepathGebruikers);
    private static final File userFileTestusers = new File(filepathTestusers);

    public static void main(String[] args) {

        // Aanmaken database access object en userDAO object.
        DBAccess dBaccess = new DBAccess(databaseName, mainUser, mainUserPassword);
        // Openen van database en aanroepen userDAO
        dBaccess.openConnection();
        UserDAO userDAO = new UserDAO(dBaccess);

        // Aanroepen methodes om het csv weg te schrijven naar uiteindelijk een ArrayList met Users.
        List<String> gebruikerscsv = FileReaderToArray(userFileGebruikers);
        List<User> gebruikersList = listUsers(gebruikerscsv);
        List<String> testuserscsv = FileReaderToArray(userFileTestusers);
        List<User> testuserList = listUsers(testuserscsv);


        // Het opslaan van de gebruikers en testusers in de database.
        // Gecomment omdat de gebruikers er anders meerdere keren in voor kunnen komen.
        // saveUsersFromArray(gebruikersList, userDAO);
        // saveUsersFromArray(testuserList, userDAO);

        // Sluiten database
        dBaccess.closeConnection();
    }

    // Deze methode leest een csv-bestand met gebruikers in en slaat deze regel voor regel op in een ArrayList van Strings.
    public static List<String> FileReaderToArray(File file) {
        List<String> linesFromFile = new ArrayList<>();
        try {
            Scanner input = new Scanner(file);
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

    // Deze methode opent de database, haalt de gebruikers uit een ArrayList van Users en slaat ze via de UserDAO
    // op in de database. Daarna wordt de database gesloten.
    private static void saveUsersFromArray(List<User> userList, UserDAO userDAO) {
        for (User user : userList) {
            userDAO.storeOne(user);
        }
    }
}
