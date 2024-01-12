package controller;

import database.mysql.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import view.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CreateUpdateUserController {
    private final UserDAO userDAO;
    private int idUser;         // Opslaan van idUser omdat deze nodig is om een gebruiker te wijzigen.
    @FXML
    Label titleLabel;
    @FXML
    TextField usernameTextfield;
    @FXML
    TextField firstNameTextfield;
    @FXML
    TextField prefixTextfield;
    @FXML
    TextField lastNameTextfield;
    @FXML
    ComboBox<String> roleComboBox;
    @FXML
    Label warningLabel;
    @FXML
    Label usernameLabel;
    @FXML
    Label firstnameLabel;
    @FXML
    Label surnameLabel;
    @FXML
    Label roleLabel;
    ObservableList<String> rollen = FXCollections.observableArrayList("Student", "Docent", "Coördinator", "Administrator", "Functioneel Beheerder");

    public CreateUpdateUserController() {
        this.userDAO = new UserDAO(Main.getDBaccess());
    }

    // In de setup wordt de combobox voor de rollen gevuld en worden textfields gevuld 
    // als er in het manageUsers scherm een gebruiker geselecteerd is.
    public void setup(User user) {
        roleComboBox.setItems(rollen);
        Main.primaryStage.setTitle("Nieuwe gebruiker");
        if (user != null) {
            Main.primaryStage.setTitle("Wijzig gebruiker");
            idUser = user.getIdUser();
            titleLabel.setText("Wijzig gebruiker");
            usernameTextfield.setText(String.valueOf(user.getUsername()));
            firstNameTextfield.setText(String.valueOf(user.getFirstName()));
            prefixTextfield.setText(String.valueOf(user.getPrefix()));
            lastNameTextfield.setText(String.valueOf(user.getSurname()));
            roleComboBox.getSelectionModel().select(user.getRole());
        }
    }

    // Deze methode slaat een nieuwe gebruiker op in de database of wijzigt een bestaande gebruiker.
    @FXML
    public void doSaveUser(ActionEvent actionEvent) {
        User user = createUser();
        String updateUserAlertMessage = "Gebruiker gewijzigd";
        String newUserAlertMessage = "Gebruiker opgeslagen";
        if (user != null) {
            if (titleLabel.getText().equals("Nieuwe gebruiker")) {
                userDAO.storeOne(user);
                showAlert(newUserAlertMessage);
            } else {
                user.setIdUser(idUser);
                userDAO.updateOne(user);
                showAlert(updateUserAlertMessage);
            }
        }
    }

    // Actie om terug te gaan naar het manageUsers scherm
    @FXML
    public void doShowManageUsers(ActionEvent actionEvent) {
        Main.getSceneManager().showManageUserScene();
    }

    // Actie om terug te gaan naar het welcomeScene scherm
    @FXML
    public void doShowMenu(ActionEvent actionEvent) {
        Main.getSceneManager().showWelcomeScene();
    }

    // Methode om een nieuw object User te maken. Als foutieve informatie ingevuld wordt,
    // wordt hier een melding over gegeven.
    private User createUser() {
        String errorMessage = "Je hebt niet alle velden ingevuld.\nVelden met een * zijn verplicht.";
        boolean correctInput;
        String username = usernameTextfield.getText();
        String firstname = firstNameTextfield.getText();
        String prefix = prefixTextfield.getText();
        String lastname = lastNameTextfield.getText();
        String role = roleComboBox.getSelectionModel().getSelectedItem();
        correctInput = isCorrectInput(username, firstname, lastname, role);
        if (!correctInput) {
            warningLabel.setText(errorMessage);
            warningLabel.setVisible(true);
            return null;
        } else {
            return new User(username, firstname, prefix, lastname, role);
        }
    }

    private boolean isCorrectInput(String username, String firstname, String lastname, String role) {
        if (username.isEmpty() || firstname.isEmpty() || lastname.isEmpty() || role == null) {
            List<Label> mandatoryFields = new ArrayList<>(Arrays.asList(usernameLabel, firstnameLabel, surnameLabel, roleLabel));
            for (Label label : mandatoryFields) {
                label.setText(label.getText() + " *");
            }
            return false;
        }
        return true;
    }

    // Methode om een Alert te laten zien en je daarna terug te sturen naar de manage user scene
    private static void showAlert(String alertMessage) {
        Alert saved = new Alert(Alert.AlertType.INFORMATION);
        saved.setContentText(alertMessage);
        Optional<ButtonType> result = saved.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Main.getSceneManager().showManageUserScene();
        }
    }
}
