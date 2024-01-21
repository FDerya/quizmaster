package controller;

import database.mysql.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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
    Label userInformation;

    public ManageUsersController() {
        this.userDAO = new UserDAO(Main.getDBaccess());
    }

    // Methode die ervoor zorgt dat de listView gevuld is en dat de eerste counter aangeroepen wordt.
    public void setup() {
        List<User> users = userDAO.getAll();
        userList.getItems().addAll(users);
        userList.setCellFactory(param -> new ListCell<>() {
            @Override
            public void updateItem(User item, boolean empty) {
            super.updateItem(item, empty);
            Label nameUser = new Label();
            nameUser.setPrefWidth(200.0);
            Label roleUser = new Label();
            HBox hbox = new HBox(nameUser, roleUser);
            if (!(item == null || empty)) {
                nameUser.setText(item.getFullName());
                roleUser.setText(item.getRole());
            }
            setGraphic(hbox);
            }
        });
        doCounterRole();
        userList.getSelectionModel().selectedItemProperty().addListener((observableValue, user, t1) -> doCounterRole());
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
            userInformation.setText("Kies een gebruiker");
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
            userList.getSelectionModel().clearSelection();
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
            userInformation.setText("Kies een gebruiker");
            userInformation.setVisible(true);
        } else {
            int counter = (int) users.stream().filter(roleUsers -> roleUsers.getRole().equals(user.getRole())).count();
            userInformation.setVisible(true);
            userInformation.setText(counter == 1 ? "Er is " + counter + " " + user.getRole().toLowerCase() :
                    "Er zijn " + counter + " " + getRoleTextInPlural(user.getRole()).toLowerCase());
            }
    }

    private String getRoleTextInPlural(String role) {
        String pluralRole = "";
        switch (role) {
            case "Student":
                pluralRole = "Studenten";
                break;
            case "Administrator":
                pluralRole = "Administratoren";
                break;
            case "Coördinator":
                pluralRole = "Coördinatoren";
                break;
            case "Functioneel Beheerder":
                pluralRole = "Functioneel Beheerders";
                break;
            case "Docent":
                pluralRole = "Docenten";
                break;
        }
        return pluralRole;
    }
}
