package controller;

import database.mysql.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import view.Main;

public class CreateUpdateUserController {
    private final UserDAO userDAO;

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

    private int idUser;

    public void setup(User user) {
        roleComboBox.setItems(rollen);
        if (user != null) {
            idUser = user.getIdUser();
            titleLabel.setText("Wijzig gebruiker");
            usernameTextfield.setText(String.valueOf(user.getUsername()));
            passwordTextfield.setText(String.valueOf(user.getPassword()));
            firstNameTextfield.setText(String.valueOf(user.getFirstName()));
            prefixTextfield.setText(String.valueOf(user.getPrefix()));
            lastNameTextfield.setText(String.valueOf(user.getSurname()));
            roleComboBox.getSelectionModel().select(user.getRole());
            System.out.println(idUser);
        }
    }

    @FXML
    public void doSaveUser(ActionEvent actionEvent) {
        User user = createUser();
        if (user != null) {
            if (titleLabel.getText().equals("Nieuwe gebruiker")) {
                userDAO.storeOne(user);
                Alert saved = new Alert(Alert.AlertType.INFORMATION);
                saved.setContentText("Gebruiker opgeslagen");
                saved.show();
            } else {
                user.setIdUser(idUser);
                userDAO.updateOne(user);
                Alert updated = new Alert(Alert.AlertType.INFORMATION);
                updated.setContentText("Gebruiker gewijzigd");
                updated.show();
            }
        }
    }

    @FXML
    public void doShowManageUsers(ActionEvent actionEvent) {
        Main.getSceneManager().showManageUserScene();
    }

    @FXML
    public void doShowMenu(ActionEvent actionEvent) {
        Main.getSceneManager().showWelcomeScene();
    }

    private User createUser() {
        StringBuilder error = new StringBuilder();
        boolean correctInput = true;
        String username = usernameTextfield.getText();
        String password = passwordTextfield.getText();
        String firstname = firstNameTextfield.getText();
        String prefix = prefixTextfield.getText();
        String lastname = lastNameTextfield.getText();
        String role = roleComboBox.getSelectionModel().getSelectedItem();

        if (username.isEmpty()) {
            error.append("Je moet een gebruikersnaam invullen. ");
            correctInput = false;
        }

        if (password.isEmpty()) {
            error.append("Je moet een wachtwoord invullen. ");
            correctInput = false;
        }

        if (firstname.isEmpty()) {
            error.append("Je moet een voornaam invullen. ");
            correctInput = false;
        }

        if (lastname.isEmpty()) {
            error.append("Je moet een achternaam invullen. ");
            correctInput = false;
        }

        if (role.isEmpty() || role == null) {
            error.append("Er is geen rol gekozen." );
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
}
