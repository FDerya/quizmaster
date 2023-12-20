package controller;

import database.mysql.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.User;
import view.Main;

public class LoginController {

    private final UserDAO userDAO;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField passwordField;
    // Made a label that shows an error if login failed.
    @FXML
    private Label errorMessage;

    public LoginController() {
        userDAO = new UserDAO(Main.getDBaccess());
    }

    public void doLogin() {
        User user = userDAO.getOneByUsername(nameTextField.getText());
        try {
            if (passwordField.getText().equals(user.getPassword())) {
                User.setCurrentUser(user);
                Main.getSceneManager().showWelcomeScene();
            } else {
            errorMessage.setText("Fout: combinatie van gebruikersnaam en wachtwoord is onjuist");
            }
        } catch (NullPointerException nullPointerException) {
            errorMessage.setText("Fout: combinatie van gebruikersnaam en wachtwoord is onjuist");
            System.out.println(nullPointerException.getMessage());
        }
    }

    public void doQuit(ActionEvent event) {
        // Verandert de currentUser naar null
        User.setCurrentUser(null);
        // Sluit de database
        Main.getDBaccess().closeConnection();
        // Sluit de applicatie
        System.exit(0);
    }
}
