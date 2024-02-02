package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Group;
import view.Main;

import java.util.List;

public class GroupCouchDBDAO extends AbstractCouchDBDAO {
    private Gson gson;

    // Initializes the GroupCouchDBDAO
    public GroupCouchDBDAO(CouchDBAccess couchDBAccess) {
        super(couchDBAccess);
        gson = new Gson();
    }

    // Converts a Group object to a JsonObject for storage using save().
    public String saveSingleGroup(Group group) {
        String jsonString = gson.toJson(group);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
        return saveDocument(jsonObject);
    }

    // Retrieves a Group from CouchDB using its document ID.
    public Group getGroupById(String doc_Id) {
        return gson.fromJson(getDocumentById(doc_Id), Group.class);
    }

    // Retrieves a Group from CouchDB based on idGroup and idCourse.
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

    // Deletes a Group from CouchDB.
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
        for (JsonObject jsonObject : getAllDocuments()) {
            String[] idAndRev = checkGroupDocument(jsonObject, group);
            if (idAndRev != null) {
                return idAndRev;
            }
        }
        return new String[0];
    }

    // Checks if the given JsonObject represents a Group with matching idGroup and idCourse.
    private String[] checkGroupDocument(JsonObject jsonObject, Group group) {
        JsonElement idGroupElement = jsonObject.get("idGroup");
        JsonElement idCourseElement = jsonObject.get("idCourse");

        if (idGroupElement != null && idCourseElement != null &&
                idGroupElement.isJsonPrimitive() && idCourseElement.isJsonPrimitive()) {

            int idGroupValue = idGroupElement.getAsInt();
            int idCourseValue = idCourseElement.getAsInt();

            if (idGroupValue == group.getIdGroup() && idCourseValue == group.getCourse().getIdCourse()) {
                return new String[]{jsonObject.get("_id").getAsString(), jsonObject.get("_rev").getAsString()};
            }
        }
        return null;
    }

    // Updates a Group document in CouchDB.
    public String updateGroup(Group group) {
        String[] idAndRev = getIdAndRevOfGroup(group);
        String jsonString = gson.toJson(group);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
        jsonObject.addProperty("_id", idAndRev[0]);
        jsonObject.addProperty("_rev", idAndRev[1]);
        return updateDocument(jsonObject);
    }
}
