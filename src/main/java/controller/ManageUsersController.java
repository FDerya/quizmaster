package controller;

import database.mysql.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
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
    @FXML
    GridPane deleteUserGrid;
    @FXML
    Label deleteWarningLabel;

    public ManageUsersController() {
        this.userDAO = new UserDAO(Main.getDBaccess());
    }

    // Methode die ervoor zorgt dat de listView gevuld is en dat de eerste counter aangeroepen wordt.
    public void setup() {
        List<User> users = userDAO.getAll();
        userList.getItems().addAll(users);
        userList.getSelectionModel().selectFirst();
        doCounterRole();
        userList.getSelectionModel().selectedItemProperty().addListener((observableValue, user, t1) -> {
                doCounterRole();
                warningLabel.setVisible(false);
        });
    }

    // Terug naar welcomeScene passend bij de rol.
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }

     // Door naar scherm createUpdateUser om een nieuwe gebruiker toe te voegen.
    public void doCreateUser() {
        Main.getSceneManager().showCreateUpdateUserScene(null);
    }

    // Door naar scherm createUpdateUser om een bestaande gebruiker te wijzigen.
    public void doUpdateUser(ActionEvent event) {
        User user = userList.getSelectionModel().getSelectedItem();
        if (user != null) {
            Main.getSceneManager().showCreateUpdateUserScene(user);
        } else {
            warningLabel.setVisible(true);
        }
    }

    // Zorgt ervoor dat er een waarschuwing komt als je een gebruiker wilt verwijderen.
    public void doAskDeleteUser (ActionEvent event) {
        User user = userList.getSelectionModel().getSelectedItem();
        if (user == null) {
            warningLabel.setVisible(true);
        } else {
            warningLabel.setVisible(false);
            deleteUserGrid.setVisible(true);
            deleteWarningLabel.setText("Je gaat gebruiker " + user + " verwijderen. \n" +
                    "Dit kan niet ongedaan gemaakt worden.\n" +
                    "Weet je het zeker?");
        }
    }

    // Actie om de gebruiker definitief te verwijderen.
    public void doDeleteUser(ActionEvent event) {
        User user = userList.getSelectionModel().getSelectedItem();
        if (user != null) {
            userDAO.removeOne(user);
            userList.getItems().remove(user);
            deleteUserGrid.setVisible(false);
        } else {
            warningLabel.setText("Houd de gebruiker geselecteerd.");
            warningLabel.setVisible(true);
        }
    }

    // Actie om de gebruiker niet te verwijderen en het grid weer onzichtbaar te maken
    public void doNotDeleteUser(ActionEvent event) {
        deleteUserGrid.setVisible(false);
    }

    // Deze methode telt het aantal gebruikers dat eenzelfde rol heeft als de geselecteerde gebruiker.
    public void doCounterRole() {
        User user = userList.getSelectionModel().getSelectedItem();
        List<User> users = userDAO.getAll();

        if (user == null) {
            roleCounter.setText("Selecteer een gebruiker om te zien \nhoeveel gebruikers dezelfde rol hebben \n" +
                    "of om een actie uit te voeren.");
            roleCounter.setVisible(true);
        } else {
            int counter = 0;
            for (User roleUsers : users) {
                if (roleUsers.getRole().equals(user.getRole())) {
                    counter++;
                }
            }
            roleCounter.setVisible(true);
            roleCounter.setText("Van het type " + user.getRole().toLowerCase() + " zijn er " + counter + " gebruikers");
        }
    }
}
