package controller;

import database.mysql.DBAccess;
import database.mysql.GroupDAO;
import database.mysql.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Group;
import view.Main;
import view.SceneManager;

import java.util.List;

public class ManageGroupsController {
    private ListView<String> groupList;
    private database.mysql.DBAccess DBAccess;
    private database.mysql.UserDAO UserDAO;
    private GroupDAO groupDAO = new GroupDAO(DBAccess, UserDAO);
    TextField waarschuwingTextField;

    public ManageGroupsController() {
    }

    public void setup() {
        MenuItem newItem = new MenuItem("Nieuw");
        newItem.setOnAction(actionEvent -> createNewGroup());
        MenuItem updateItem = new MenuItem("Wijzig");
        updateItem.setOnAction(actionEvent -> goToUpdateGroup());
        MenuItem deleteItem = new MenuItem("Verwijder");
        deleteItem.setOnAction(actionEvent -> deleteSelectedGroup());
        Button menuButton = new Button("Menu");
        menuButton.setOnAction(actionEvent -> goToWelcomeScene());

        groupList.setContextMenu(new ContextMenu(newItem, updateItem, deleteItem));

        List<Group> groups = groupDAO.getAll();

        for (Group group : groups) {
            groupList.getItems().add(group.getGroupName());
        }
    }

    private void goToWelcomeScene() {
        Main.getSceneManager().showWelcomeScene();
    }

    private void deleteSelectedGroup() {
        String selectedGroup = groupList.getSelectionModel().getSelectedItem();
        System.out.println("Verwijder groep: " + selectedGroup);

        groupList.getItems().remove(selectedGroup);
    }

    private void createNewGroup() {

    }
    @FXML
    public void doChangeGroup(ActionEvent event) {
        Group group = groupList.getSelectionModel().getSelectedItem();

        if (group == null) {
            waarschuwingTextField.setVisible(true);
            waarschuwingTextField.setText("Je moet eerst een groep selecteren");
       } else {
        loadView(group).getSceneManager().showExistingCustomerScene(group);
    }
    }

    private void goToUpdateGroup() {
        if (!groupList.getSelectionModel().isEmpty()) {
            String selectedGroupName = groupList.getSelectionModel().getSelectedItem();

            Group selectedGroup = groupDAO.getGroupByName(selectedGroupName);

            Main.getSceneManager().showCreateUpdateGroupScene(selectedGroup);
        } else {
            System.out.println("Geen groep geselecteerd");
        }
    }

@FXML
    public void doMenu(ActionEvent actionEvent) {
        loadView("Menu.fxml");
    }


    public void doCreateGroup(ActionEvent event) {
        SceneManager sceneManager = new SceneManager((Stage) ((Node) event.getSource()).getScene().getWindow());
        sceneManager.showManageGroupsScene();
    }


    public void doUpdateGroup() {
        loadView("UpdateGroup.fxml");
    }

    public void doDeleteGroup() {
        loadView("DeleteGroup.fxml");
    }

    private void loadView(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();

           CreateUpdateGroupController controller = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
