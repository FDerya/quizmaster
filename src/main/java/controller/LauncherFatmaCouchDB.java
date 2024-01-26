package controller;
import com.google.gson.Gson;
import database.mysql.QuizDAO;
import javacouchdb.CouchDBAccess;
import javacouchdb.QuestionCouchDBDAO;
import model.Quiz;
import model.Question;
import view.Main;

import java.io.IOException;

public class LauncherFatmaCouchDB {

    private static CouchDBAccess couchDBaccess;
    private static QuestionCouchDBDAO questionCouchDBDAO;


    public static void main(String[] args)  {
        QuizDAO quizDAO =  new QuizDAO(Main.getDBaccess());
        // CouchDB setup
        CouchDBAccess couchDBAccess = new CouchDBAccess("quizmaster", "admin", "admin");
        Gson gson = new Gson();
        QuestionCouchDBDAO questionDAO = new QuestionCouchDBDAO(couchDBAccess, gson);

        // Test CRUD operations
        testCRUDOperations(questionDAO, quizDAO);
    }

    private static void testCRUDOperations(QuestionCouchDBDAO questionDAO , QuizDAO quizDAO) {
        // Create a Quiz
        Quiz quiz = quizDAO.getOneById(2);

        // Create a Question
        Question question = new Question(2, quiz, "What is the capital of France?", "Paris", "Berlin", "London", "Madrid");

        // Save Question
        String documentId = questionDAO.saveSingleQuestion(question);
        System.out.println("Saved Question with ID: " + documentId);

        // Retrieve the saved question
        Question retrievedQuestion = questionDAO.getQuestionById(documentId);
        System.out.println("Retrieved Question: " + retrievedQuestion);

        // Update the question
        Question update_question =  questionDAO.getQuestionById("0cfb18768c7d4b1a9fc3c0bd87a6daa5");
        System.out.println("Question text: " + update_question.getQuestion() );
        update_question.setQuestion("Why is France");
        questionDAO.updateQuestion(update_question);
        System.out.println("Updated Successfully");

        // Delete the question
        questionDAO.deleteQuestion(retrievedQuestion);
        System.out.println("Deleted Question with ID: " + documentId);


    }
}

