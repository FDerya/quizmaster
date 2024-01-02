package controller;
// Bianca Duijvesteijn, studentnummer 500940421
// This controller is responsible for managing groups in a JavaFX application.
// It provides functionalities such as creating, updating, and deleting groups, as well as displaying
// relevant information to the user.

import database.mysql.GroupDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Group;
import view.Main;

import java.util.List;
import java.util.Optional;

public class ManageGroupsController {
    @FXML
    private ListView<Group> groupList;
    @FXML
    private GroupDAO groupDAO;
    @FXML
    private final TextField warningTextField = new TextField();
    @FXML
    private Label courseNameLabel;
    @FXML
    private Label groupCountLabel;

    // Initializes the controller and creates a GroupDAO with database access
    public ManageGroupsController() {
        this.groupDAO = new GroupDAO(Main.getDBaccess());
    }

    // Initializes UI components and calls configuration methods
    @FXML
    public void initialize() {
        setup();
        configureGroupListCellFactory();
        configureLabels();
        configureGroupListListener();
    }

    // Retrieves the list of groups and configures the ListView
    @FXML
    public void setup() {
        List<Group> groups = groupDAO.getAll();
        groupList.getSelectionModel().clearSelection();
        groupList.getItems().setAll(groups);
        courseNameLabel.setVisible(false);
        groupCountLabel.setVisible(false);
    }

    // Configures the appearance of cells in the ListView for displaying groups
    private void configureGroupListCellFactory() {
        groupList.setCellFactory(param -> new ListCell<Group>() {
            @Override
            protected void updateItem(Group item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getGroupName());
                }
            }
        });
    }

    // Initializes text labels with default text
    private void configureLabels() {
        courseNameLabel.setText("Course: ");
        groupCountLabel.setText("Number of groups following the same course: ");
    }

    // Adds a listener to the ListView for handling selected groups
    private void configureGroupListListener() {
        groupList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleGroupSelection();
            }
        });
    }

    // Handles the selection of a group and updates the labels accordingly
    private void handleGroupSelection() {
        Group selectedGroup = groupList.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            updateLabels(selectedGroup);
        } else {
            handleNoGroupSelected();
        }
        groupList.refresh();
    }

    // Updates the labels based on the selected group
    private void updateLabels(Group selectedGroup) {
        int idGroup = selectedGroup.getIdGroup();
        String courseName = groupDAO.getCourseNameForGroup(idGroup);
        if (courseName != null) {
            int groupCount = groupDAO.getGroupCountForSameCourse(selectedGroup);
            if (groupCount != -1) {
                showGroupInfo(courseName, groupCount);
            } else {
                showErrorMessage("No groups found following the same course.");
            }
        } else {
            showNoCourseInfo();
        }
    }

    // Displays information about the selected group
    private void showGroupInfo(String courseName, int groupCount) {
        courseNameLabel.setText("Course: " + courseName);
        groupCountLabel.setText("Number of groups following the same course: " + groupCount);
        showLabels();
    }

    // Displays an error message when course information is missing for the selected group
    private void showNoCourseInfo() {
        courseNameLabel.setText("No course information available for this group.");
        groupCountLabel.setText("");
        clearErrorMessage();
        showLabels();
    }

    // Handles the situation where no group is selected
    private void handleNoGroupSelected() {
        showErrorMessage("No group selected.");
        courseNameLabel.setText("");
        groupCountLabel.setText("");
        showLabels();
    }

    // Makes the labels visible
    private void showLabels() {
        courseNameLabel.setVisible(true);
        groupCountLabel.setVisible(true);
    }

    // Displays an error message in a text field
    private void showErrorMessage(String message) {
        warningTextField.setText(message);
        warningTextField.setVisible(true);
    }

    // Clears the error message in the text field
    private void clearErrorMessage() {
        warningTextField.setText("");
        warningTextField.setVisible(false);
    }

    // Handles the "Menu" button click event, navigating back to the welcome scene
    @FXML
    private void doMenu() {
        try {
            Main.getSceneManager().showWelcomeScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handles the creation of a new group, opening the corresponding UI
    @FXML
    private void doCreateGroup() {
        Main.getSceneManager().showCreateUpdateGroupScene(null);
    }

    // Handles the update of an existing group, opening the UI to assign students to groups
    @FXML
    private void doUpdateGroup() {
        Main.getSceneManager().showCreateUpdateGroupScene(null);
    }

    // Handles the deletion of an existing group, updating the UI and database accordingly
    @FXML
    private void doDeleteGroup() {
        Group selectedGroup = groupList.getSelectionModel().getSelectedItem();

        if (selectedGroup == null) {
            showWarning("You must first select a group.");
            return;
        }

        if (confirmDeletion(selectedGroup)) {
            deleteGroup(selectedGroup);
            removeFromGroupList(selectedGroup);
        }
    }

    // Displays a warning in a dialog
    private void showWarning(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // Requests user confirmation for deleting a group
    private boolean confirmDeletion(Group selectedGroup) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Group");
        alert.setHeaderText("Group " + selectedGroup.getGroupName() + " will be deleted.");
        alert.setContentText("Are you sure?");
        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    // Deletes a group from the database
    private void deleteGroup(Group selectedGroup) {
        groupDAO.deleteGroup(selectedGroup);
    }

    // Removes a group from the list
    private void removeFromGroupList(Group selectedGroup) {
        groupList.getItems().remove(selectedGroup);
    }
}