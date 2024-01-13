package view;

import database.mysql.DBAccess;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static SceneManager sceneManager = null;
    public static Stage primaryStage = null;
    private static DBAccess dbAccess = null;
    private static final String databaseName = "Quizmaster";
    private static final String mainUser = "userQuizmaster";
    private static final String mainUserPassword = "pwQuizmaster";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Make IT Work - Project 1");
        getSceneManager().showLoginScene();
        primaryStage.show();
    }

    public static SceneManager getSceneManager() {
        if (sceneManager == null) {
            sceneManager = new SceneManager(primaryStage);
        }
        return sceneManager;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static DBAccess getDBaccess() {
        if (dbAccess == null) {
            dbAccess = new DBAccess(databaseName, mainUser,mainUserPassword);
            dbAccess.openConnection();
        }
        return dbAccess;
    }
}