package controller;

import database.mysql.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import view.Main;

import java.util.Optional;

public class CreateUpdateUserController {
    private final UserDAO userDAO;
    private final String newUserAlertMessage = "Gebruiker opgeslagen";
    private final String updateUserAlertMessage = "Gebruiker gewijzigd";
    private int idUser;         // Opslaan van idUser omdat deze nodig is om een gebruiker te wijzigen.
    @FXML
    Label titleLabel;
    @FXML
    TextField usernameTextfield;
    @FXML
    TextField passwordTextfield;
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
            passwordTextfield.setText(String.valueOf(user.getPassword()));
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
        StringBuilder error = new StringBuilder();
        error.append("Je hebt meerdere velden niet ingevuld: \n");
        boolean correctInput = true;
        String username = usernameTextfield.getText();
        String password = passwordTextfield.getText();
        String firstname = firstNameTextfield.getText();
        String prefix = prefixTextfield.getText();
        String lastname = lastNameTextfield.getText();
        String role = roleComboBox.getSelectionModel().getSelectedItem();

        // Idee voor foutieve invoer: sterretjes zetten bij verplichte invoer. Geef melding: niet alles ingevuld, kijk naar de met * gemarkeerde velden

        if (username.isEmpty()) {
            error.append("Je moet een gebruikersnaam invullen. \n");
            correctInput = false;
        }

        if (password.isEmpty()) {
            error.append("Je moet een wachtwoord invullen. \n");
            correctInput = false;
        }

        if (firstname.isEmpty()) {
            error.append("Je moet een voornaam invullen. \n");
            correctInput = false;
        }

        if (lastname.isEmpty()) {
            error.append("Je moet een achternaam invullen. \n");
            correctInput = false;
        }

        if (role == null) {
            error.append("Er is geen rol gekozen.\n" );
            correctInput = false;
        }

        if (!correctInput) {
            warningLabel.setText(error.toString());
            warningLabel.setVisible(true);
            return null;
        } else {
            return new User(0, username, password, firstname, prefix, lastname, role);
        }
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
