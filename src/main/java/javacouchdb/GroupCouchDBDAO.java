package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Group;

public class GroupCouchDBDAO extends AbstractCouchDBDAO {
    private final Gson gson;

    // Initializes the GroupCouchDBDAO
    public GroupCouchDBDAO(CouchDBAccess couchDBAccess, Gson gson) {
        this.gson = gson;
    }

    // Converts a Group object to a JsonObject for storage using save().
    public String saveSingleGroup(Group group) {
        try {
            String jsonString = gson.toJson(group);
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            return saveDocument(jsonObject);
        } catch (Exception e) {
            System.err.println("Error saving group to CouchDB: " + e.getMessage());
            return null;
        }
    }

    // Create a Group in couchDB
    public void createGroup(Group group) {
        saveSingleGroup(group);
    }

    // Retrieves a Group from CouchDB using its document ID
    public Group getGroupById(String doc_Id) {
        return gson.fromJson(getDocumentById(doc_Id), Group.class);
    }

    // Retrieves a Group from CouchDB based on idGroup and idCourse
    public Group getGroup(int idGroup, int idCourse) {
        Group resultaat;
        for (JsonObject jsonObject : getAllDocuments()) {
            resultaat = gson.fromJson(jsonObject, Group.class);
            if (resultaat.getIdGroup() == idGroup && (resultaat.getCourse().getIdCourse() == idCourse)) {
                return resultaat;
            }
        }
        return null;
    }

    // Deletes a Group from CouchDB
    public void deleteGroup(Group group) {
        String[] idAndRev = getIdAndRevOfGroup(group);
        if (idAndRev != null && idAndRev.length == 2) {
            deleteDocument(idAndRev[0], idAndRev[1]);
        } else {
            System.err.println("Kan geen _id en _rev verkrijgen voor het verwijderen van het document.");
        }
    }

    // Finds the _id and _rev of a document corresponding to a Group with idGroup and idCourse.
    public String[] getIdAndRevOfGroup(Group group) {
        String[] idAndRev = new String[2];
        for (JsonObject jsonObject : getAllDocuments()) {
            if (jsonObject.has("idGroup") && jsonObject.get("idGroup").getAsInt() == group.getIdGroup()) {
                idAndRev[0] = jsonObject.get("_id").getAsString();
                idAndRev[1] = jsonObject.get("_rev").getAsString();
                break;
            }
        }
        return idAndRev;
    }

    // Updates a Group document in CouchDB.
    public String updateGroup(String id, String rev, Group group) {
        String jsonString = gson.toJson(group);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
        jsonObject.addProperty("_id", id);
        jsonObject.addProperty("_rev", rev);
        return updateDocument(jsonObject);
    }
}



