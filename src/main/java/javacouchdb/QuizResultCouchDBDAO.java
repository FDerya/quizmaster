package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    public List<QuizResult> getQuizResults(Quiz quiz, User user) {
        List<QuizResult> resultList = new ArrayList<>();
        for (JsonObject jsonObject : getAllDocuments()) {
            QuizResult result = gson.fromJson(jsonObject, QuizResult.class);
            if (result.getQuiz().equals(quiz.getNameQuiz()) && result.getUser().equals(user.getFullName())) {
                resultList.add(result);
            }
        }
        // resultList op datum sorteren
        resultList.sort(Comparator.comparing(quizResult -> quizResult.getLocalDateTime()));
        return resultList;
    }
}

