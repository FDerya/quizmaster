package controller;

import database.mysql.UserDAO;
import javafx.beans.value.ChangeListener;
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
    @FXML
    private Label errorMessage;

    public LoginController() {
        userDAO = new UserDAO(Main.getDBaccess());
    }

    // Nadat de constructor is aangeroepen, wordt de listener aan nameTextField en passwordField gegeven.
    // Deze zorgt dat de errorMessage na foutief inloggen verdwijnt zodra je begint te typen in een van deze textfields.
    public void initialize() {
        nameTextField.textProperty().addListener(changeErrorMessage());
        passwordField.textProperty().addListener(changeErrorMessage());
    }

    private ChangeListener<String> changeErrorMessage() {
        return (observable, oldValue, newValue) -> errorMessage.setVisible(newValue.isEmpty());
    }

    public void doLogin() {
        String loginErrorMessage = "Combinatie van gebruikersnaam en wachtwoord is onjuist. \nProbeer het opnieuw.";
        User user = userDAO.getOneByUsername(nameTextField.getText());
        try {
            if (passwordField.getText().equals(user.getPassword())) {
                successfulLogin(user);
            } else {
                failedLogin(loginErrorMessage);
            }
        } catch (NullPointerException nullPointerException) {
            failedLogin(loginErrorMessage);
        }
    }

    private void successfulLogin(User user) {
        User.setCurrentUser(user);
        if (user.getRole().equals("Docent")) {
            errorMessage.setVisible(true);
            errorMessage.setText("Er zijn momenteel geen taken beschikbaar voor een docent");
        } else {
            Main.getSceneManager().showWelcomeScene();
        }
    }

    private void failedLogin(String loginErrorMessage) {
        errorMessage.setText(loginErrorMessage);
        nameTextField.clear();
        passwordField.clear();
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
