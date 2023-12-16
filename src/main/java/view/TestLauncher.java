package view;

import database.mysql.DBAccess;
import database.mysql.QuestionDAO;
import javafx.application.Application;
import javafx.stage.Stage;
import model.Question;

import java.util.List;

public class TestLauncher extends Application {

    public static void main(String[] args) {
      //  launch(args);
        DBAccess dbAccess = new DBAccess("quizmaster","root","ftm7874tr" );
        QuestionDAO questionDAO = new QuestionDAO(dbAccess);
        dbAccess.openConnection();
        List<Question> questions = questionDAO.getAll();
        for (Question question : questions){
            System.out.println(question);
        }

    }

    @Override
    public void start(Stage primaryStage) {
        // Create an instance of SceneManager
        SceneManager sceneManager = new SceneManager(primaryStage);

        // Call the method to show the Manage Questions scene
        sceneManager.showManageQuestionsScene();

        // Set up any additional configurations for your primary stage if needed
        primaryStage.setTitle("Your Application Title");
        primaryStage.show();



    }
}

