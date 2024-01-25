package controller;

import database.mysql.CourseDAO;
import database.mysql.GroupDAO;
import database.mysql.ParticipationDAO;
import database.mysql.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import model.Course;
import model.Participation;
import model.User;
import view.Main;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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



