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

    // Adds a listener to the nameTextField and passwordField, that hides the errorMessage label when you start typing.
    public void initialize() {
        nameTextField.textProperty().addListener(changeErrorMessage());
        passwordField.textProperty().addListener(changeErrorMessage());
    }

    private ChangeListener<String> changeErrorMessage() {
        return (observable, oldValue, newValue) -> errorMessage.setVisible(newValue.isEmpty());
    }

    // Checks if username and password check out with eachother. If it fails, shows a login error.
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

    // Changes the static user to the logged in user, so he's logged in in the whole program.
    // If a teacher logs in, shows an error message.
    private void successfulLogin(User user) {
        User.setCurrentUser(user);
        if (user.getRole().equals("Docent")) {
            errorMessage.setVisible(true);
            errorMessage.setText("Er zijn momenteel geen taken beschikbaar voor een docent");
        } else {
            Main.getSceneManager().showWelcomeScene();
        }
    }

    // After a failed login, shows the loginerror and clears the textfields.
    private void failedLogin(String loginErrorMessage) {
        errorMessage.setText(loginErrorMessage);
        nameTextField.clear();
        passwordField.clear();
    }

    public void doQuit(ActionEvent event) {
        // changes currentUser to null
        User.setCurrentUser(null);
        // closes the database
        Main.getDBaccess().closeConnection();
        // closes the application
        System.exit(0);
    }
}
