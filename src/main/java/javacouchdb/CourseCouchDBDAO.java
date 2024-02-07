package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Course;

public class CourseCouchDBDAO extends AbstractCouchDBDAO {
// Attributes
    private final Gson gson;

// Contructor
    public CourseCouchDBDAO (CouchDBAccess couchDBAccess) {
        super(couchDBAccess);
        gson = new Gson();
    }

// Methods
    public String saveSingleCourse(Course course) {
        String jsonString = gson.toJson(course);
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        return saveDocument(jsonObject);
    }

    public Course getCourseByDocId(String docId){
        return gson.fromJson(getDocumentById(docId), Course.class);
    }

    public Course getCourse(int idCourse) {
        Course result;
        for (JsonObject jsonObject : getAllDocuments()) {
            result = gson.fromJson(jsonObject, Course.class);
            if (result.getIdCourse() == idCourse) {
                return result;
            }
        }
        return null;
    }

    public String[] getIdAndRevCourse(Course course) {
        String[] idAndRev = new String[2];
        for (JsonObject jsonObject : getAllDocuments()) {
            if (jsonObject.has("idCourse") && jsonObject.get("idCourse").getAsInt() == course.getIdCourse()) {
                idAndRev[0] = jsonObject.get("_id").getAsString();
                idAndRev[1] = jsonObject.get("_rev").getAsString();
            }
        }
        return idAndRev;
    }

    public String updateCourse(Course course) {
        String[] idAndRev = getIdAndRevCourse(course);
        String jsonString = gson.toJson(course);
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        jsonObject.addProperty("_id", idAndRev[0]);
        jsonObject.addProperty("_rev", idAndRev[1]);
        return updateDocument(jsonObject);
    }

    public void deleteCourse(Course course) {
        String[] idAndRev = getIdAndRevCourse(course);
        deleteDocument(idAndRev[0], idAndRev[1]);
    }

}
