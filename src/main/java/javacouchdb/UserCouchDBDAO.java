package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.User;

public class UserCouchDBDAO extends AbstractCouchDBDAO {
    private final Gson gson;

    public UserCouchDBDAO(CouchDBAccess couchDBaccess) {
        super(couchDBaccess);
        gson = new Gson();
    }

    // CREATE METHODS
    public String saveSingleUser(User user) {
        String jsonString = gson.toJson(user);
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        return saveDocument(jsonObject);
    }

    // READ METHODS
    public User getUserByDocId(String doc_Id) {
        return gson.fromJson(getDocumentById(doc_Id), User.class);
    }

    public User getUser(int idUser) {
        User result;
        for (JsonObject jsonObject : getAllDocuments()) {
            result = gson.fromJson(jsonObject, User.class);
            if (result.getIdUser() == idUser) {
                return result;
            }
        }
        return null;
    }

    // UPDATE METHODS
    public String updateUser(User user) {
        String[] idAndRev = getIdAndRevUser(user);
        String jsonString = gson.toJson(user);
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        jsonObject.addProperty("_id", idAndRev[0]);
        jsonObject.addProperty("_rev", idAndRev[1]);
        return updateDocument(jsonObject);
    }

    // DELETE METHODS
    public void deleteUser(User user) {
        String[] idAndRev = getIdAndRevUser(user);
        deleteDocument(idAndRev[0], idAndRev[1]);
    }

    // Method to get ID and Rev, needed for updating and deleting
    public String[] getIdAndRevUser(User user) {
        String[] idAndRev = new String[2];
        for (JsonObject jsonObject : getAllDocuments()) {
            if (jsonObject.has("idUser") && jsonObject.get("idUser").getAsInt() == user.getIdUser()) {
                idAndRev[0] = jsonObject.get("_id").getAsString();
                idAndRev[1] = jsonObject.get("_rev").getAsString();
            }
        }
        return idAndRev;
    }

}




