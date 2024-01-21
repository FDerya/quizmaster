package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import model.Course;
import view.Main;

public class StudentSignInOutController {

    @FXML
    private ListView <Course> signedOutCourseList;
    @FXML
    private ListView <Course> signedInCourseList;

    public void setup() {
        signedInCourseList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldCourse, newCourse) ->
                        System.out.println("Geselecteerde cursus: " + observableValue + ", " + oldCourse + ", " + newCourse));
        signedOutCourseList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldGroup, newGroup) ->
                        System.out.println("Geselecteerde groep: " + observableValue + ", " + oldGroup + ", " + newGroup));
    }


    @FXML
    private void doMenu() {
        try {
            Main.getSceneManager().showWelcomeScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doSignIn() {}

    public void doSignOut() {}
}
