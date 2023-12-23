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
        String loginErrorMessage = "Combinatie van gebruikersnaam en wachtwoord is onjuist";
        User user = userDAO.getOneByUsername(nameTextField.getText());
        try {
            if (passwordField.getText().equals(user.getPassword())) {
                User.setCurrentUser(user);
                if (user.getRole().equals("Docent")) {
                    errorMessage.setText("Er zijn momenteel geen taken beschikbaar voor een docent");
                } else {
                    Main.getSceneManager().showWelcomeScene();
                }
            } else {
            errorMessage.setText(loginErrorMessage);
            }
        } catch (NullPointerException nullPointerException) {
            errorMessage.setText(loginErrorMessage);
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
