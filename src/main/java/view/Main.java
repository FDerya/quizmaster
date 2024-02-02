package view;

import database.mysql.DBAccess;
import javacouchdb.CouchDBAccess;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static SceneManager sceneManager = null;
    public static Stage primaryStage = null;
    private static DBAccess dbAccess = null;
    private static CouchDBAccess couchDBAccess = null;
    private static final String DATABASE_NAME = "Quizmaster";
    private static final String MAIN_USER = "userQuizmaster";
    private static final String MAIN_USER_PASSWORD = "pwQuizmaster";
    private static final String COUCHDB_DATABASE_NAME = "quizmaster-quizresult";
    private static final String COUCHDB_MAIN_USER = "admin";
    private static final String COUCHDB_PASSWORD = "admin";
    private static final String MAIN_SCREEN_BUTTON = "Beginscherm";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Quizmaster - Prlwytzkofsky College");
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
            dbAccess = new DBAccess(DATABASE_NAME, MAIN_USER, MAIN_USER_PASSWORD);
            dbAccess.openConnection();
        }
        return dbAccess;
    }

    public static CouchDBAccess getCouchDBaccess() {
        if (couchDBAccess == null) {
            couchDBAccess = new CouchDBAccess(COUCHDB_DATABASE_NAME, COUCHDB_MAIN_USER, COUCHDB_PASSWORD);
            couchDBAccess.getClient();
        }
        return couchDBAccess;
    }
}