package controller;
// Bianca Duijvesteijn, studentnummer 500940421
// Performs JSON-related operations using Gson, executes operations on a CouchDB database,
// and saves a list of Group objects to CouchDB

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LauncherCouchDBBianca {

    private static CouchDBAccess couchDBaccess;
    private static GroupCouchDBDAO groupCouchDBDAO;

    // Entry point for the application, initializes CouchDB, saves group list, performs JSON examples,
    // CouchDB operations, and closes CouchDB.
    public static void main(String[] args) throws IOException {
        initializeCouchDB();

        saveGroupList(buildGroupList());
        performJsonExamples();
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
            System.out.println("Connection 1 open");
            performRetrieveAndUpdateOperations();
            performDeleteOperation();
            printAllDocuments();
        }
    }
    // Performs retrieve and update operations on CouchDB
    private static void performRetrieveAndUpdateOperations() {
        try {
            Group idGroup = groupCouchDBDAO.getGroupById("00a1cd5e54ba408aa8bc003b7a006b9b");
            System.out.println("Group by ID: " + idGroup);

            String updateResult = groupCouchDBDAO.updateGroup(idGroup);
            System.out.println(updateResult.equals("updated")
                    ? "Group updated successfully."
                    : "Failed to update group. Result: " + updateResult);

            Group idGroupAndCourse = groupCouchDBDAO.getGroup(999, 999);
            if (idGroupAndCourse != null) {
                System.out.println("Group by ID and Course: " + idGroupAndCourse);
                groupCouchDBDAO.updateGroup(idGroupAndCourse);
            } else {
                System.out.println("Group not found with ID and Course: 999, 999");
                System.out.println("Attempting to retrieve Group with ID: " + idGroup);
                CouchDBAccess couchDBaccess = new CouchDBAccess("jouwDatabaseNaam",
                        "jouwGebruikersnaam", "jouwWachtwoord");
                InputStream group = couchDBaccess.getClient().find(String.valueOf(idGroup.getIdGroup()));
            }
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }


/*    // Performs retrieve and update operations on CouchDB
    private static void performRetrieveAndUpdateOperations() {
        try {
            Group idGroup = retrieveGroupById("00a1cd5e54ba408aa8bc003b7a006b9b");
            displayGroupDetails("Group by ID", idGroup);

            String updateResult = updateGroup(idGroup);
            displayUpdateResult(updateResult);

            Group idGroupAndCourse = retrieveGroupByCourseId(999, 999);
            if (idGroupAndCourse != null) {
                displayGroupDetails("Group by ID and Course", idGroupAndCourse);
                updateGroup(idGroupAndCourse);
            } else {
                displayGroupNotFoundMessage(999, 999, idGroup);
                attemptToRetrieveGroupById(idGroup);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }*/

    // Retrieves a group by its ID
    private static Group retrieveGroupById(String groupId) throws Exception {
        return groupCouchDBDAO.getGroupById(groupId);
    }

    // Retrieves a group by its ID and Course ID
    private static Group retrieveGroupByCourseId(int groupId, int courseId) throws Exception {
        return groupCouchDBDAO.getGroup(groupId, courseId);
    }

    // Updates a group in CouchDB
    private static String updateGroup(Group group) throws Exception {
        return groupCouchDBDAO.updateGroup(group);
    }

    // Displays details of a group
    private static void displayGroupDetails(String message, Group group) {
        System.out.println(message + ": " + group);
    }

    // Displays the result of the update operation
    private static void displayUpdateResult(String updateResult) {
        System.out.println(updateResult.equals("updated")
                ? "Group updated successfully."
                : "Failed to update group. Result: " + updateResult);
    }

    // Displays a message when a group is not found
    private static void displayGroupNotFoundMessage(int groupId, int courseId, Group idGroup) {
        System.out.println("Group not found with ID and Course: " + groupId + ", " + courseId);
    }

    // Attempts to retrieve a group by its ID
    private static void attemptToRetrieveGroupById(Group idGroup) {
        System.out.println("Attempting to retrieve Group with ID: " + idGroup);
        CouchDBAccess couchDBaccess = new CouchDBAccess("jouwDatabaseNaam",
                "jouwGebruikersnaam", "jouwWachtwoord");
        InputStream group = couchDBaccess.getClient().find(String.valueOf(idGroup.getIdGroup()));
    }

    // Handles exceptions
    private static void handleException(Exception e) {
        System.err.println("Exception: " + e.getMessage());
    }

    // Performs delete operation on CouchDB
    private static void performDeleteOperation() {
        Group idGroupAndCourse = groupCouchDBDAO.getGroup(999, 999);
        groupCouchDBDAO.deleteGroup(idGroupAndCourse);
    }

    // Prints all documents from a CouchDB database
    private static void printAllDocuments() {
        System.out.println();
        System.out.println("----------  Alle documenten ------------");
        List<JsonObject> allDocs = couchDBaccess.getClient().view("_all_docs").includeDocs(true).query(JsonObject.class);
        allDocs.forEach(jsonObject -> System.out.println(jsonObject.getAsJsonObject()));
        System.out.println("------------------------------------------");
    }


    // Read JSON strings from a file and convert them to a list of Group objects
    private static List<Group> buildGroupList() {
        Gson gson = new Gson();
        List<Group> groupList = new ArrayList<>();
        try {
            GroupDAO groupDAO = new GroupDAO(Main.getDBaccess(), new UserDAO(Main.getDBaccess()), new CourseDAO(Main.getDBaccess()));
            List<Group> sqlGroupData = groupDAO.getAll();
            groupList.addAll(sqlGroupData);
            appendGroupsToFile(groupList, "src/resources/groupJson.txt");
        } catch (IOException e) {
            System.err.println("Fout bij het schrijven naar het JSON-bestand: " + e.getMessage());
        }
        return groupList;
    }

    // Reads existing group entries from the specified file and returns them as a set of JSON-formatted strings.
    private static Set<String> readExistingGroupsFromFile(String filePath) throws IOException {
        Set<String> existingGroups = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                existingGroups.add(line.trim());
            }
        }

        return existingGroups;
    }

    // Appends new group entries to the specified file, skipping duplicates based on JSON content.
    private static void appendGroupsToFile(List<Group> groupList, String filePath) throws IOException {
        Gson gson = new Gson();

        try (FileWriter writer = new FileWriter(filePath, true)) {
            Set<String> existingGroups = readExistingGroupsFromFile(filePath);

            for (Group group : groupList) {
                String jsonContent = gson.toJson(group);

                if (!existingGroups.contains(jsonContent.trim())) {
                    writer.write(jsonContent + System.lineSeparator());
                } else {
                    System.out.println("Group with ID " + group.getIdGroup() + " and Course ID " +
                            group.getCourse().getIdCourse() + " already exists in the file. Skipping...");
                }
            }
        }
    }

    // Save a list of Group objects to CouchDB
    private static void saveGroupList(List<Group> groupList) {
        if (couchDBaccess != null && couchDBaccess.getClient() != null) {
            System.out.println("Connection 2 open");
            try {
                for (Group group : groupList) {
                    String[] idAndRev = groupCouchDBDAO.getIdAndRevOfGroup(group);

                    if (idAndRev != null && idAndRev.length > 0) {
                        String id = idAndRev[0];
                        String rev = idAndRev[1];

                        groupCouchDBDAO.saveSingleGroup(group);
                    } else {
                        System.out.println("Group with ID " + group.getIdGroup() + " already exists in the database. Skipping...");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error saving groups in CouchDB: " + e.getMessage());
            }
        }
    }
}
