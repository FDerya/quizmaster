package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.*;

import javax.xml.transform.Result;
import java.time.LocalDate;

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
    public QuizResult getSingleQuizResult(Quiz quiz, User user){
        QuizResult result;
        for (JsonObject jsonObject : getAllDocuments()) {
            result = gson.fromJson(jsonObject, QuizResult.class);
            if (result.getQuiz().equals(quiz.getNameQuiz()) && result.getUser().equals(user.getUsername())) {
                return result;
            }
        }
        return null;
    }
    }

