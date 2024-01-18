package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.*;

import java.util.List;

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
    public Quiz getQuiz(Quiz quiz){
        Quiz resultaat;
        for (JsonObject jsonObject : getAllDocuments()) {
            resultaat = gson.fromJson(jsonObject, Quiz.class);
            if (resultaat.equals(quiz)) {
                return resultaat;
            }
        }
        return null;
    }
}
