package controller;
// JavaFX controller class responsible for handling the user interface logic related to managing groups.
// Works with a GroupDAO and a CourseDAO to perform operations such as displaying, deleting, and deleting groups.
// The class also contains methods for user prompts and confirmation dialogs.

import database.mysql.CourseDAO;
import database.mysql.GroupDAO;
import database.mysql.UserDAO;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import model.Group;
import view.Main;

import java.util.Comparator;
import java.util.List;

public class ManageGroupsController extends WarningAlertController {
    private GroupDAO groupDAO;
    @FXML
    private ListView<Group> groupList;
    @FXML
    private Label groupCountLabel;
    @FXML
    private Label warningLabel;
    @FXML
    Button mainScreenButton;

    // Constructor
    public ManageGroupsController() {
        this.groupDAO = new GroupDAO(Main.getDBaccess());

    }

    // Clears the group list, retrieves and sorts groups, sets up list view properties, and updates labels
    @FXML
    public void setup() {
        groupList.setCellFactory(param -> new GroupListCell());
        List<Group> groups = groupDAO.getAll();
        setGroupListData(groups);

        updateGroupCountLabel();

        groupList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection,
                                                                          newSelection) -> updateGroupCountLabel());
        mainScreenButton.setText(Main.getMainScreenButtonText());
    }

    // Clears the existing items in the groupList and populates it with the provided list of groups
    private void setGroupListData(List<Group> groups) {
        groupList.getItems().clear();
        groups.sort(Comparator.comparing(group -> group.getCourse().getNameCourse()));
        groupList.getItems().addAll(groups);
    }

    public void setGroupCountLabel(Label groupCountLabel) {
        this.groupCountLabel = groupCountLabel;
    }

    // Creates a custom ListCell for JavaFX ListView, utilizing an HBox with Labels for group and
    // course names, setting their texts if the item is not empty or null
    private static class GroupListCell extends ListCell<Group> {
        // Creates an HBox with Labels for group name and course name, setting their texts
        private HBox createGroupListCell(Group group) {
            Label groupName = new Label();
            groupName.setPrefWidth(250.0);
            Label courseName = new Label();
            HBox hBox = new HBox(groupName, courseName);
            if (group != null) {
                groupName.setText(group.getGroupName());
                courseName.setText(" " + group.getCourse() + " ");
            }
            return hBox;
        }

        // Updates the graphical representation of a list cell with group and course information.
        @Override
        protected void updateItem(Group group, boolean empty) {
            super.updateItem(group, empty);
            setGraphic(empty || group == null ? null : createGroupListCell(group));
        }
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
        Group selectedGroup = getSelectedGroup();
        if (selectedGroup == null) {
            groupCountLabel.setText("Selecteer een groep");
        } else {
            Main.getSceneManager().showCreateUpdateGroupScene(selectedGroup);
        }
    }

    // Deletes a selected group, confirming if new or found in newGroups, and updates UI and database,
    // showing a warning and triggering setup otherwise.
    @FXML
    private void doDeleteGroup() {
        hideLabel();
        Group selectedGroup = getSelectedGroup();
        if (selectedGroup == null) {
            groupCountLabel.setText("Selecteer een groep");
            return;
        }
        boolean foundInNewGroups = isGroupInNewGroups(selectedGroup);
        if (selectedGroup.isNew() || foundInNewGroups) {
            confirmDeletion("groep", selectedGroup.getGroupName());
            // showConfirmationDialog(selectedGroup);
        } else {
            showWarningAndSetup(selectedGroup);
        }
    }

    // Checks if the selected group is present in the newGroups list
    private boolean isGroupInNewGroups(Group selectedGroup) {
        for (Group newGroup : CreateUpdateGroupController.newGroups) {
            if (newGroup.getGroupName().equals(selectedGroup.getGroupName())) {
                return true;
            }
        }
        return false;
    }

    // Removes the specified group from the database, UI list, sorts the list by course names, and
    // updates the group count label.
    private void deleteGroup(Group selectedGroup) {

        groupDAO.deleteGroup(selectedGroup);
        groupList.getItems().remove(selectedGroup);
        groupList.getItems().sort(Comparator.comparing(group -> group.getCourse().getNameCourse()));
        updateGroupCountLabel();

    }

    // Shows a warning, logs the message that the group cannot be deleted
    private void showWarningAndSetup(Group selectedGroup) {
        showWarningLabel();
        System.out.println(selectedGroup.getGroupName() + " mag niet worden verwijderd uit de database.");
        fadeOutWarningLabel();
        setup();
    }

    // Fades out the warning message
    private void fadeOutWarningLabel() {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), warningLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> fadeOut.play());
        pause.play();
    }


    // Shows warninglabel
    private void showWarningLabel() {
        Platform.runLater(() -> {
            warningLabel.setText("Deze groep mag niet worden verwijderd");
            warningLabel.setVisible(true);
        });
    }

    // Updates the group count label and selected course label based on the selected group
    protected Object updateGroupCountLabel() {
        Group selectedGroup = getSelectedGroup();
        if (selectedGroup == null) {
            hideLabel();
        } else {
            if (selectedGroup.getCourse() != null) {
                groupCountLabel.setVisible(true);
                long counter = groupDAO.countGroupsForCourse(selectedGroup.getCourse());
                updateLabels(selectedGroup, counter);
            } else {
                hideLabel();
            }
        }
        return null;
    }

    // Clears labels when no group is selected or no course is associated with the selected group.
    private void hideLabel() {
        groupCountLabel.setText("");
        groupCountLabel.setVisible(true);
    }

    // Updates labels with the group count and selected course information.
    private void updateLabels(Group selectedGroup, long counter) {
        String label;
        if (counter == 1) {
            label = counter + " groep in " + selectedGroup.getCourse();
        } else {
            label = counter + " groepen in " + selectedGroup.getCourse();
        }
        groupCountLabel.setText(label);
        groupCountLabel.setWrapText(true);
    }

    // Getter for the selected group
    public Group getSelectedGroup() {
        return groupList.getSelectionModel().getSelectedItem();
    }
}
