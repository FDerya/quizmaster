package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.QuizResult;

public class QuizResultCouchDBDAO extends AbstractCouchDBDAO {
    private final Gson gson;

    public QuizResultCouchDBDAO(CouchDBAccess couchDBaccess) {
        super(couchDBaccess);
        gson = new Gson();
    }

    public String saveSingleQuizResult(QuizResult quizResult) {
        String jsonString = gson.toJson(quizResult);
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        return saveDocument(jsonObject);
    }
}
