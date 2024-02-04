package controller;
//This launcher class manages JSON operations and interacts with a CouchDB database.
// It initializes the connection and DAO, retrieves groups from an SQL database, converts them to JSON,
// and saves them to a file. It also reads JSON data from the file and saves it to CouchDB.
// Additionally, it demonstrates JSON examples.
//
//Furthermore, it performs CRUD operations on CouchDB groups: retrieving, creating, updating with a new user,
// and deleting based on ID and course. Finally, it prints all CouchDB documents.

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LauncherCouchDBBianca {
    private static CouchDBAccess couchDBaccess;
    private static GroupCouchDBDAO groupCouchDBDAO;

    // Entry point for the application, initializes CouchDB, saves group list, performs JSON examples,
    // CouchDB operations, and closes CouchDB.
    public static void main(String[] args) throws IOException {
        initializeCouchDB();

        List<Group> groupListFromSQL = buildGroupListFromSQL();
        convertAndSaveToJson(groupListFromSQL);
        saveGroupsToCouchDB();

        performJsonExamples();
        performCouchDBOperations();

        closeCouchDB();
    }

    // Initialize CouchDB connection and DAO
    private static void initializeCouchDB() {
        try {
            couchDBaccess = new CouchDBAccess("quizmaster", "admin", "admin");
            Gson gson = new Gson();
            groupCouchDBDAO = new GroupCouchDBDAO(couchDBaccess, gson);
            System.out.println("CouchDB initialized successfully.");
        } catch (Exception e) {
            System.err.println("Error initializing CouchDB: " + e.getMessage());
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

    // Fetches a list of groups from an SQL database and returns it
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

    // Converts a list of groups to JSON format and saves it to a file
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

    // Reads JSON-formatted group data from a file and saves it to CouchDB
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

    // Saves a single group object to CouchDB
    private static void saveGroupToCouchDB(Group group) {
        try {
            String documentId = groupCouchDBDAO.saveSingleGroup(group);
            if (documentId != null) {
                System.out.println("Groep met ID " + group.getIdGroup() + " is succesvol opgeslagen in CouchDB " +
                        "met document ID: " + documentId);
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
            System.out.println("Connection Perform CouchDB Operations open");
            initializeCouchDB();
            getGroupById("01e83a9e1423481dac09c0cd026e9871");
            getGroupFromCouchDB(24, 21);
            createGroup();
            updateGroupWithNewUser();
            performDeleteOperation();
            printAllDocuments();
        }
    }

    // Perform retrieve operation to get a group by ID from CouchDB
    public static Group getGroupById(String doc_Id) {
        try {
            Group group = groupCouchDBDAO.getGroupById(doc_Id);
            if (group != null) {
                System.out.println("Retrieved group by doc_Id \nGroup details");
                System.out.println("Group ID: " + group.getIdGroup() + "\nCourse name: " + group.getCourse() +
                        "\nCourse ID: " + group.getCourse().getIdCourse());
                return group;
            } else {
                System.out.println("Group not found with ID: " + doc_Id);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Exception while getting group by docId: " + e.getMessage());
            return null;
        }
    }

    // Retrieves group from CouchDB with idGroup and idCourse
    private static void getGroupFromCouchDB(int idGroup, int idCourse) {
        try {
            Group group = groupCouchDBDAO.getGroup(idGroup, idCourse);
            if (group != null) {
                System.out.println("Retrieved group by idGroup and idCourse \nGroup details:");
                System.out.println("Group ID: " + group.getIdGroup() + "\nCourse name: " + group.getCourse() +
                        "\nCourse ID: " + group.getCourse().getIdCourse() + "\nAmount of Students: "
                        + group.getAmountStudent() + "\nTeacher: " + group.getTeacher());
            } else {
                System.err.println("Group not found with IDGroup: " + idGroup + " and IDCourse: " + idCourse);
            }
        } catch (Exception e) {
            System.err.println("Exception while getting group from CouchDB: " + e.getMessage());
        }
    }

    private static void createGroup() {
        User userDocent = new User(999, "SmiFra", "H&@g76cD", "Frank",
                "de", "Smit", "Docent");
        User userCoordinator = new User(999, "VisNat", "D43v@h8G", "Natasja",
                "", "Visser", "Coordinator");
        Course course = new Course(999, userCoordinator, "Algebra", "Medium");
        Group group = new Group(999, course, "Groep 1", 25, userDocent);

        groupCouchDBDAO.createGroup(group);
    }

    // Perform update operation on a group in CouchDB
    private static void updateGroupWithNewUser() {
        Group existingGroup = groupCouchDBDAO.getGroup(999, 999);
        if (existingGroup != null) {
            User newUser = new User(998, "BoersVi", "DR%ml98%&", "Viola",
                    "", "Boersma", "Docent");
            existingGroup.setTeacher(newUser);

            String[] idAndRev = groupCouchDBDAO.getIdAndRevOfGroup(existingGroup);

            if (idAndRev != null && idAndRev.length == 2) {
                String docId = idAndRev[0];
                String docRev = idAndRev[1];

                groupCouchDBDAO.updateGroup(docId, docRev, existingGroup);
            } else {
                System.err.println("Fout bij het bijwerken van de groep: Kan de ID en revisie van de groep " +
                        "niet vinden");
            }
        } else {
            System.err.println("Fout bij het bijwerken van de groep: Groep niet gevonden");
        }
    }

    // Performs delete operation on CouchDB
    private static void performDeleteOperation() {
        Group groupToDelete = groupCouchDBDAO.getGroup(999, 999);
        if (groupToDelete != null) {
            String idGroup = String.valueOf(groupToDelete.getIdGroup());
            String idCourse = String.valueOf(groupToDelete.getCourse().getIdCourse());
            groupCouchDBDAO.deleteGroup(groupToDelete);
            System.out.println("Groep met ID " + idGroup + " en cursus ID " + idCourse + " is succesvol verwijderd");
        } else {
            System.out.println("Groep niet gevonden met ID en cursus: 999, 999");
        }
    }

    // Prints all documents from a CouchDB database
    private static void printAllDocuments() {
        System.out.println();
        System.out.println("----------  All documents ------------");
        List<JsonObject> allDocs = couchDBaccess.getClient().view("_all_docs").includeDocs(true)
                .query(JsonObject.class);
        allDocs.forEach(jsonObject -> System.out.println(jsonObject.getAsJsonObject()));
        System.out.println("------------------------------------------");
    }
}
