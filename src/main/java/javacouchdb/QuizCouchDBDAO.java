package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.*;

public class QuizCouchDBDAO extends AbstractCouchDBDAO{

    private Gson gson;

    public QuizCouchDBDAO(CouchDBAccess couchDBaccess) {
        super(couchDBaccess);
        gson = new Gson();
    }

    public String saveSingleQuiz(Quiz quiz){
        String jsonString = gson.toJson(quiz);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
        return saveDocument(jsonObject);
    }
    public Quiz getQuizById(String doc_Id){
        return gson.fromJson(getDocumentById(doc_Id), Quiz.class);
    }
    public Quiz getQuiz(String quiz){
        Quiz resultaat;
        for (JsonObject jsonObject : getAllDocuments()) {
            resultaat = gson.fromJson(jsonObject, Quiz.class);
            if (resultaat.getNameQuiz().equals(quiz)) {
                return resultaat;
            }
        }
        return null;
    }
    public void deleteQuiz(Quiz quiz){
        String[] idAndRev = getIdAndRevQuiz(quiz);
        deleteDocument(idAndRev[0], idAndRev[1]);
    }
   public String[] getIdAndRevQuiz(Quiz quiz){
        String[] idAndRev= new String[2];
        for (JsonObject jsonObject : getAllDocuments()) {
            if (jsonObject.has("idQuiz") && jsonObject.get("idQuiz").getAsInt() ==quiz.getIdQuiz()){

                idAndRev[0] = jsonObject.get("_id").getAsString();
                idAndRev[1] = jsonObject.get("_rev").getAsString();
            }
        }
        return idAndRev;
    }
    public String updateQuiz(Quiz quiz) {
        // Haal _id en _rev van document op behorend bij quiz
        // Zet quiz om in JsonObject
        String[] idAndRev = getIdAndRevQuiz(quiz);
        String jsonString = gson.toJson(quiz);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
        // Voeg _id en _rev toe aan JsonObject, nodig voor de update van een document.
        jsonObject.addProperty("_id" , idAndRev[0]);
        jsonObject.addProperty("_rev" , idAndRev[1]);
        return updateDocument(jsonObject);
    }
}
