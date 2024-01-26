package javacouchdb;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Question;

public class QuestionCouchDBDAO extends AbstractCouchDBDAO {

    private final Gson gson;

    public QuestionCouchDBDAO(CouchDBAccess couchDBaccess, Gson gson) {
        super(couchDBaccess);
        this.gson = gson;
    }

    public String saveSingleQuestion (Question question ) {
        String jsonString = gson.toJson(question);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
        return saveDocument(jsonObject);
    }

    public Question getQuestionById(String doc_Id) {
        return gson.fromJson(getDocumentById(doc_Id), Question.class);
    }

    public Question getQuestion (int idQuestion, int idQuiz) {
        Question resultaat;
        for (JsonObject jsonObject : getAllDocuments()) {
            resultaat = gson.fromJson(jsonObject, Question.class);
            if (resultaat.getIdQuestion() == idQuestion && (resultaat.getQuiz().getIdQuiz()== idQuiz)) {
                return resultaat;
            }
        }
        return null;
    }

    public String[] getIdAndRevQuestion(Question question) {
        String[] idAndRev = new String[2];
        for (JsonObject jsonObject : getAllDocuments()) {
            if (jsonObject.has("idQuestion") && jsonObject.get("idQuestion").getAsInt() == question.getIdQuestion()) {
                idAndRev[0] = jsonObject.get("_id").getAsString();
                idAndRev[1] = jsonObject.get("_rev").getAsString();
            }
        }
        return idAndRev;
    }

    public void deleteQuestion(Question question){
        String[] idAndRev = getIdAndRevQuestion(question);
        deleteDocument(idAndRev[0], idAndRev[1]);
    }

    public String updateQuestion(Question question) {
        String[] idAndRev = getIdAndRevQuestion(question);
        String jsonString = gson.toJson(question);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
        jsonObject.addProperty("_id", idAndRev[0]);
        jsonObject.addProperty("_rev", idAndRev[1]);
        return updateDocument(jsonObject);
    }

}
