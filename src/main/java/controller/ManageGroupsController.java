package controller;
// Bianca Duijvesteijn, studentnummer 500940421
// This controller is responsible for managing groups in a JavaFX application.
// It provides functionalities such as creating, updating, and deleting groups, as well as displaying
// relevant information to the user.

import database.mysql.DBAccess;
import database.mysql.GroupDAO;
import database.mysql.UserDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import model.Group;
import view.Main;

import java.io.IOException;
import java.util.List;

public class ManageGroupsController {
    @FXML
    private ListView<String> groupList;
    @FXML
    private GroupDAO groupDAO;
    @FXML
    private final TextField waarschuwingTextField = new TextField();

    // Initializes the ManageGroupsController, setting up the UI components and event listeners
    public void setup() {
        DBAccess dbAccess = Main.getDBaccess();
        UserDAO userDAO = new UserDAO(Main.getDBaccess());
        groupDAO = new GroupDAO(dbAccess, userDAO);

        List<Group> groups = groupDAO.getAll();
        for (Group group : groups) {
            groupList.getItems().add(group.getGroupName());
        }

        Label groupsInCourseInfoLabel = new Label();
        groupList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Group selectedGroup = groupDAO.getGroupByName(newValue);
                int groupsWithSameCourse = groupDAO.getCountOfGroupsByCourse(selectedGroup.getCourseName().getNameCourse());
                groupsInCourseInfoLabel.setText("Aantal groepen die deze cursus volgen: " + groupsWithSameCourse);
            } else {
                groupsInCourseInfoLabel.setText("Informatie niet beschikbaar");
            }
        });
    }

    // Handles the "Menu" button click event, navigating back to the welcome scene
    @FXML
    private void doMenu() {
        Main.getSceneManager().showWelcomeScene();

    }

    // Handles the creation of a new group, opening the corresponding UI
    public void doCreateGroup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("path/to/createUpdateGroup.fxml"));
            Parent root = loader.load();

            CreateUpdateGroupController controller = loader.getController();
            controller.initialize(null, null);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handles the update of an existing group, opening the UI to assign students to groups
    @FXML
    private void doUpdateGroup() {
        String selectedGroupName = groupList.getSelectionModel().getSelectedItem();
        if (selectedGroupName == null) {
            waarschuwingTextField.setVisible(true);
            waarschuwingTextField.setText("Je moet eerst een groep selecteren");
        } else {
            Group selectedGroup = getGroupByName(selectedGroupName);
            openAssignStudentsToGroupsScene(selectedGroup);
        }
    }

    // Handles the deletion of an existing group, updating the UI and database accordingly
    public void doDeleteGroup() {
        String selectedGroupName = groupList.getSelectionModel().getSelectedItem();
        if (selectedGroupName != null) {
            Group selectedGroup = getGroupByName(selectedGroupName);
            groupDAO.deleteGroup(selectedGroup);

            Platform.runLater(() -> groupList.getItems().remove(selectedGroupName));
        } else {
            waarschuwingTextField.setVisible(true);
            waarschuwingTextField.setText("Je moet eerst een groep selecteren");
        }
    }

    // Retrieves a group by its name
    private Group getGroupByName(String selectedGroupName) {
        return groupDAO.getGroupByName(selectedGroupName);
    }

    // Opens the UI to assign students to groups for a given group
    private void openAssignStudentsToGroupsScene(Group group) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("assignStudentsToGroups.fxml"));
            Parent root = loader.load();

            AssignStudentsToGroupController controller = loader.getController();
            controller.initData(group);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

