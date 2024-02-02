package controller;
// Bianca Duijvesteijn, studentnummer 500940421
// Performs JSON-related operations using Gson, executes operations on a CouchDB database,
// and saves a list of Group objects to CouchDB

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import database.mysql.CourseDAO;
import database.mysql.GroupDAO;
import database.mysql.UserDAO;
import javacouchdb.CouchDBAccess;
import javacouchdb.GroupCouchDBDAO;
import model.Course;
import model.Group;
import model.User;
import view.Main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class LauncherCouchDBBianca {

    private static CouchDBAccess couchDBaccess;
    private static GroupCouchDBDAO groupCouchDBDAO;

    // Entry point for the application, initializes CouchDB, saves group list, performs JSON examples,
    // CouchDB operations, and closes CouchDB.
    public static void main(String[] args) throws IOException {
        initializeCouchDB();

        /*List<Group> groupListFromSQL = buildGroupListFromSQL();
        convertAndSaveToJson(groupListFromSQL);
        saveGroupsToCouchDB();*/

        // performJsonExamples();
        performCouchDBOperations();

        closeCouchDB();
    }

    // Initialize CouchDB connection and DAO
    private static void initializeCouchDB() {
        try {
            couchDBaccess = new CouchDBAccess("quizmaster", "admin", "admin");
            groupCouchDBDAO = new GroupCouchDBDAO(couchDBaccess);
        } catch (Exception e) {
            System.err.println("Fout bij het initialiseren van CouchDB: " + e.getMessage());
        }
    }

    // Close CouchDB connection
    private static void closeCouchDB() {
        try {
            if (couchDBaccess != null) {
                couchDBaccess.getClient().shutdown();
            }
        } catch (Exception e) {
            System.err.println("Fout bij het sluiten van CouchDB: " + e.getMessage());
        }
    }

    private static List<Group> buildGroupListFromSQL() {
        List<Group> groupList = new ArrayList<>();
        try {
            GroupDAO groupDAO = new GroupDAO(Main.getDBaccess(), new UserDAO(Main.getDBaccess()),
                    new CourseDAO(Main.getDBaccess()));
            groupList = groupDAO.getAll();

            return groupList;
        } catch (Exception e) {
            System.err.println("Error fetching groups from SQL database: " + e.getMessage());
            return groupList;
        }
    }

    private static void convertAndSaveToJson(List<Group> groupListFromSQL) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("src/resources/groupJson.txt")) {
            for (Group group : groupListFromSQL) {
                String json = gson.toJson(group);
                writer.write(json + "\n");
            }

            System.out.println("Gegevens zijn succesvol geconverteerd en opgeslagen in src/resources/groupJson.txt");
        } catch (IOException e) {
            System.err.println("Fout bij het schrijven naar JSON-bestand: " + e.getMessage());
        }
    }

    private static void saveGroupsToCouchDB() {
        Gson gson = new Gson();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/groupJson.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JsonObject jsonObject = JsonParser.parseString(line).getAsJsonObject();
                Group group = gson.fromJson(jsonObject, Group.class);
                saveGroupToCouchDB(group);
            }
            System.out.println("Gegevens zijn succesvol opgeslagen in CouchDB.");
        } catch (IOException e) {
            System.err.println("Fout bij het lezen van JSON-bestand: " + e.getMessage());
        }
    }

    private static void saveGroupToCouchDB(Group group) {
        try {
            String documentId = groupCouchDBDAO.saveSingleGroup(group);
            if (documentId != null) {
                System.out.println("Groep met ID " + group.getIdGroup() + " is succesvol opgeslagen in CouchDB met document ID: " + documentId);
            } else {
                System.out.println("Fout bij het opslaan van groep met ID " + group.getIdGroup() + " in CouchDB.");
            }
        } catch (Exception e) {
            System.err.println("Fout bij het opslaan van groep in CouchDB: " + e.getMessage());
        }
    }


    // Perform JSON-related examples
    private static void performJsonExamples() {
        System.out.println("Groep als json:");
        System.out.println(createSimpleGroupAndConvertToJson());
        System.out.println();
        System.out.println("Groep als JSON:");
        System.out.println(createComplexGroupAndConvertToJson());
        System.out.println();
        System.out.println("Json omgezet naar Object");
        System.out.println(convertJsonStringToObject());
        System.out.println();
        System.out.println("Json omgezet naar groep");
        System.out.println(convertJsonStringToGroupObject());
        System.out.println();
    }

    // Create a simple Group object and convert it to JSON
    private static String createSimpleGroupAndConvertToJson() {
        Gson gson = new Gson();
        Group myGroup = new Group();
        myGroup.setIdGroup(1);
        myGroup.setGroupName("MyGroup");
        myGroup.setAmountStudent(10);
        return gson.toJson(myGroup);
    }

    // Create a complex Group object, set an ID and convert it to JSON
    private static String createComplexGroupAndConvertToJson() {
        Gson gson = new Gson();
        User teacher = new User("ZwiJoh", "password", "Johanna", "",
                "Zwinkels", "Docent");
        Course course = new Course(999, teacher, "TestCursus", "Medium");
        Group myGroup2 = new Group(999, course, "Test123", 20, teacher);
        myGroup2.setIdGroup(999);
        return gson.toJson(myGroup2);
    }

    // Convert a JSON string to an Object
    private static Object convertJsonStringToObject() {
        Gson gson = new Gson();
        String jsonObjectFromString = "{\"idGroup\": 999, \"course\": {\"idCourse\": 999, \"docent\": " +
                "{\"idUser\": 999, \"username\": \"ZwiJoh\", \"password\": \"password\"," +
                " \"firstName\": \"Johanna\", \"prefix\": \"\", \"surname\": \"Zwinkels\", \"role\": " +
                "\"Docent\"}, \"nameCourse\": \"TestCursus\", \"difficultyCourse\": \"Intermediate\"}, " +
                "\"groupName\": \"TestGroep\"," +
                " \"amountStudent\": 25, \"teacher\": {\"idUser\": 999, \"username\": \"ZwiJoh\", " +
                "\"password\": \"password\", \"firstName\": \"Johanna\", \"prefix\": \"\", \"surname\": " +
                "\"Zwinkels\", \"role\": \"Docent\"}}";
        return gson.fromJson(jsonObjectFromString, Object.class);
    }

    // Convert a JSON string to a Group object
    private static Group convertJsonStringToGroupObject() {
        Gson gson = new Gson();
        String jsonObjectFromString = "{\"idGroup\": 999, \"course\": {\"idCourse\": 999, \"docent\": " +
                "{\"idUser\": 999, \"username\": \"ZwiJoh\", \"password\": \"password\"," +
                " \"firstName\": \"Johanna\", \"prefix\": \"\", \"surname\": \"Zwinkels\", \"role\": " +
                "\"Docent\"}, \"nameCourse\": \"TestCursus\", \"difficultyCourse\": \"Intermediate\"}, " +
                "\"groupName\": \"TestGroep\"," +
                " \"amountStudent\": 25, \"teacher\": {\"idUser\": 999, \"username\": \"ZwiJoh\", " +
                "\"password\": \"password\", \"firstName\": \"Johanna\", \"prefix\": \"\", \"surname\": " +
                "\"Zwinkels\", \"role\": \"Docent\"}}";
        return gson.fromJson(jsonObjectFromString, Group.class);
    }


    // Performs various CouchDB operations
    private static void performCouchDBOperations() {
        if (couchDBaccess != null && couchDBaccess.getClient() != null) {
            //System.out.println("Connection Perform CouchDB Operations open");
            performRetrieveAndUpdateOperations();
            //performDeleteOperation();
           // printAllDocuments();
        }
    }

    // Performs retrieve and update operations on CouchDB
    private static void performRetrieveAndUpdateOperations() {
        try {
            retrieveGroupById("01e83a9e1423481dac09c0cd026e9871");

            Group groupByIdAndCourse = groupCouchDBDAO.getGroup(24, 21);
            if (groupByIdAndCourse != null) {
                System.out.println("Group by ID and Course: " + groupByIdAndCourse);
                updateGroup(groupByIdAndCourse);
            } else {
                System.out.println("Group not found with IDGroup and IDCourse");
            }
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }


    // Perform retrieve operation to get a group by ID from CouchDB
    private static void retrieveGroupById(String id) {
        try {
            Group group = groupCouchDBDAO.getGroupById(id);
            if (group != null) {
                System.out.println("Group by ID: " + group);
            } else {
                System.out.println("Group not found with ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("Exception while retrieving group by ID: " + e.getMessage());
        }
    }

    // Perform update operation on a group in CouchDB
    private static void updateGroup(Group group) {
        try {
            String updateResult = groupCouchDBDAO.updateGroup(group);
            if (updateResult != null && updateResult.equals("updated")) {
                System.out.println("Group updated successfully.");
            } else {
                System.out.println("Failed to update group. Result: " + updateResult);
            }
        } catch (Exception e) {
            System.err.println("Exception while updating group: " + e.getMessage());
        }
    }


    // Performs delete operation on CouchDB
    private static void performDeleteOperation() {
        Group idGroupAndCourse = groupCouchDBDAO.getGroup(999, 999);
        if (idGroupAndCourse != null) {
            groupCouchDBDAO.deleteGroup(idGroupAndCourse);
        } else {
            System.out.println("Group not found with ID and Course: 999, 999");
        }
    }

    // Prints all documents from a CouchDB database
    private static void printAllDocuments() {
        System.out.println();
        System.out.println("----------  Alle documenten ------------");
        List<JsonObject> allDocs = couchDBaccess.getClient().view("_all_docs").includeDocs(true).query(JsonObject.class);
        allDocs.forEach(jsonObject -> System.out.println(jsonObject.getAsJsonObject()));
        System.out.println("------------------------------------------");
    }
}

