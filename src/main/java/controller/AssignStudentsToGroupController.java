package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import model.Course;
import model.Group;
import model.User;
import view.Main;

public class AssignStudentsToGroupController {

    @FXML
    ComboBox<Course> courseComboBox;
    @FXML
    ComboBox<Group> groupComboBox;
    @FXML
    ListView<User> studentList;
    @FXML
    ListView<User> studentsInGroupList;

    public void setup() {
        courseComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldCourse, newCourse) ->
                        System.out.println("Geselecteerde cursus: " + observableValue + ", " + oldCourse + ", " + newCourse));
        groupComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldGroup, newGroup) ->
                        System.out.println("Geselecteerde groep: " + observableValue + ", " + oldGroup + ", " + newGroup));
    }

    public void doAssign() {}

    public void doRemove() {}

    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }
}

