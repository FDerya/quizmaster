package controller;
// Bianca Duijvesteijn, studentnummer 500940421
//

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Group;
import view.Main;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateUpdateGroupController implements Initializable {
    @FXML
    private TextField groupNameField;

    @FXML
    private ListView<String> groupList;

    // Initializes the controller with a group if provided
    public void setup(Group group) {

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
    // Handles the "Create Group" button click event, adding a new group to the list
    @FXML
    public void doCreateGroup() {
        String nameGroup = groupNameField.getText();

        groupList.getItems().add(nameGroup);
    }
    // Initializes the controller, setting a default group name in the text field
    public void initialize(URL location, ResourceBundle resources) {
        groupNameField.setText("DefaultGroupName");

    }
    // Placeholder for handling the "Update Group" button click event
    public void doUpdateGroup(ActionEvent actionEvent) {
    }
}
