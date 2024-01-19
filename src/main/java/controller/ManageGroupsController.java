package controller;
// Bianca Duijvesteijn, studentnummer 500940421
// JavaFX controller class responsible for handling the user interface logic related to managing groups.
// Works with a GroupDAO and a CourseDAO to perform operations such as displaying, deleting, and deleting groups.
// The class also contains methods for user prompts and confirmation dialogs.

import database.mysql.CourseDAO;
import database.mysql.GroupDAO;
import database.mysql.UserDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Group;
import view.Main;

import java.util.List;
import java.util.Optional;


public class ManageGroupsController {
    private final GroupDAO groupDAO;
    @FXML
    private final TextField warningTextField = new TextField();
    @FXML
    private ListView<Group> groupList;
    @FXML
    private Label groupCountLabel;
    @FXML
    private Label selectedCourseLabel;

    // Constructor
    public ManageGroupsController() {
        this.groupDAO = new GroupDAO(Main.getDBaccess(), new UserDAO(Main.getDBaccess()),
                new CourseDAO(Main.getDBaccess()));
    }

    // Clears the group list, retrieves and sorts groups, sets up list view properties, and updates labels
    @FXML
    public void setup() {
        setGroupListData(groupDAO.getAll());
        groupList.setCellFactory(param -> new GroupListCell());
        warningTextField.setVisible(false);
        groupList.getSelectionModel().selectedItemProperty().addListener((observableValue, group, t1) -> {
            updateGroupCountLabel();
        });
    }

    // Clears the existing items in the groupList and populates it with the provided list of groups.
    private void setGroupListData(List<Group> groups) {
        groupList.getItems().clear();
        groups.sort((group1, group2) -> group1.getCourse().getNameCourse().compareTo(group2.getCourse().getNameCourse()));
        groupList.getItems().addAll(groups);
    }


    // Handles the click event on the menu button and navigates back to the welcome scene
    @FXML
    private void doMenu() {
        try {
            Main.getSceneManager().showWelcomeScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handles the creation of a new group and opens the corresponding user interface
    @FXML
    private void doCreateGroup() {
        Main.getSceneManager().showCreateUpdateGroupScene(null);
    }

    // Handles the update of an existing group and opens the corresponding user interface
    @FXML
    private void doUpdateGroup() {
        Group selectedGroup = groupList.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            showWarning();
        } else {
            Main.getSceneManager().showCreateUpdateGroupScene(selectedGroup);
        }
    }

    // Handles the deletion of an existing group, updating the user interface and the database
    @FXML
    private void doDeleteGroup() {
        Group selectedGroup = groupList.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            showWarning();
            return;
        }
        if (confirmDeletion(selectedGroup)) {
            groupDAO.deleteGroup(selectedGroup);
            groupList.getItems().remove(selectedGroup);
            groupList.getItems().sort((group1, group2) -> group1.getCourse().getNameCourse().compareTo(group2
                    .getCourse().getNameCourse()));
            updateGroupCountLabel();
        }
    }

    // Displays a warning in a dialog box, regarding the delete
    private void showWarning() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Waarschuwing");
            alert.setHeaderText(null);
            alert.setContentText("Selecteer een groep.");
            alert.showAndWait();
        });
    }

    // Prompts user for confirmation before deleting a group
    private boolean confirmDeletion(Group selectedGroup) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Verwijder groep");
        alert.setHeaderText("Verwijder " + selectedGroup.getGroupName() + " van de cursus "
                + selectedGroup.getCourse().getNameCourse());
        alert.setContentText("Weet je het zeker?");
        ButtonType buttonTypeYes = new ButtonType("Ja");
        ButtonType buttonTypeNo = new ButtonType("Nee");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    // Custom ListCell implementation for the Group class
    class GroupListCell extends ListCell<Group> {
        @Override
        protected void updateItem(Group item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                setText(item.getCourse().getNameCourse() + ", (" + item.getGroupName() + ")");
            } else {
                setText("");
            }
        }
    }

    // Updates the group count label and selected course label based on the selected group
    private void updateGroupCountLabel() {
        Group selectedGroup = groupList.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            clearLabels();
        } else {
            if (selectedGroup.getCourse() != null) {
                groupCountLabel.setVisible(true);
                long counter = groupDAO.countGroupsForCourse(selectedGroup.getCourse());
                updateLabels(selectedGroup, counter);
            } else {
                clearLabels();
            }
        }
    }

    // Clears labels when no group is selected or no course is associated with the selected group.
    private void clearLabels() {
        groupCountLabel.setText("");
        groupCountLabel.setVisible(true);
        selectedCourseLabel.setText("Selecteer een cursus");
    }

    // Updates labels with the group count and selected course information.
    private void updateLabels(Group selectedGroup, long counter) {
        groupCountLabel.setText(counter + " groep(en) in deze cursus");
        selectedCourseLabel.setText("Cursus: " + selectedGroup.getCourse().getNameCourse());
    }
}