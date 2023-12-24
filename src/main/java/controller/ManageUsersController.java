package controller;

import database.mysql.UserDAO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.User;
import view.Main;

import java.util.List;

public class ManageUsersController {
    private final UserDAO userDAO;
    @FXML
    ListView<User> userList;
    @FXML
    Label warningLabel;
    @FXML
    Label roleCounter;

    public ManageUsersController() {
        this.userDAO = new UserDAO(Main.getDBaccess());
    }

    public void setup() {
        List<User> users = userDAO.getAll();
        userList.getItems().addAll(users);
        userList.getSelectionModel().selectFirst();
        doCounterRole();
        userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue<? extends User> observableValue, User user, User t1) {
                doCounterRole();
            }
        });
    }

    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }

     public void doCreateUser() {
        Main.getSceneManager().showCreateUpdateUserScene(null);
    }

    public void doUpdateUser(ActionEvent event) {
        User user = userList.getSelectionModel().getSelectedItem();
        if (user == null) {
            warningLabel.setVisible(true);
        } else {
            Main.getSceneManager().showCreateUpdateUserScene(user);
        }
    }

    public void doDeleteUser(ActionEvent event) {
        User user = userList.getSelectionModel().getSelectedItem();
        if (user == null) {
            warningLabel.setVisible(true);
        } else {
            userDAO.removeOne(user);
            userList.getItems().remove(user);
        }
    }

    public void doCounterRole() {
        User user = userList.getSelectionModel().getSelectedItem();
        List<User> users = userDAO.getAll();
        int counter = 0;

        for (User param : users) {
            if (param.getRole().equals(user.getRole())) {
                counter++;
            }
        }
        roleCounter.setVisible(true);
        roleCounter.setText("Van het type " + user.getRole().toLowerCase() + " zijn er " + counter + " gebruikers");
    }
}
