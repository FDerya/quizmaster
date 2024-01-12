package controller;

import database.mysql.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import view.Main;

import java.util.List;
import java.util.Optional;

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

    // Methode die ervoor zorgt dat de listView gevuld is en dat de eerste counter aangeroepen wordt.
    public void setup() {
        List<User> users = userDAO.getAll();
        userList.getItems().addAll(users);
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
    public void doDeleteUser (ActionEvent event) {
        User user = userList.getSelectionModel().getSelectedItem();
        if (user == null) {
            warningLabel.setVisible(true);
        } else {
            warningLabel.setVisible(false);
            deleteWithAlert(user);
        }
    }

    // Deze methode handelt het verwijderen van de gebruiker, met waarschuwing, af.
    private void deleteWithAlert(User user) {
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("Verwijder gebruiker");
        deleteAlert.setHeaderText(null);
        deleteAlert.setContentText("Je gaat gebruiker " + user.getFullName() + " verwijderen.\n" +
                        "Dit kan niet ongedaan gemaakt worden.\n");
        ButtonType buttonYes = new ButtonType("Verwijder", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("Annuleer", ButtonBar.ButtonData.NO);
        deleteAlert.getButtonTypes().setAll(buttonYes,buttonNo);
        Optional<ButtonType> clickedButton = deleteAlert.showAndWait();
        if (clickedButton.isPresent() && clickedButton.get() == buttonYes) {
            userDAO.removeOne(user);
            userList.getItems().remove(user);
        }
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
            int counter = (int) users.stream().filter(roleUsers -> roleUsers.getRole().equals(user.getRole())).count();
            roleCounter.setVisible(true);
            roleCounter.setText("Van het type " + user.getRole().toLowerCase() + " zijn er " + counter + " gebruikers");
        }
    }
}
