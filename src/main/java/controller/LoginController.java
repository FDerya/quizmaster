package controller;

import database.mysql.DBAccess;
import database.mysql.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import model.User;
import view.Main;

import java.util.List;

public class LoginController {

    private final UserDAO userDAO;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField passwordField;

    public LoginController() {
        userDAO = new UserDAO(Main.getDBaccess());
    }

    public void doLogin() {
        User user = userDAO.getOneByUsername(nameTextField.getText());
        if (passwordField.getText().contains(user.getPassword())) {
            Main.getSceneManager().showWelcomeScene();
        }
    }

    public void doQuit(ActionEvent event) {
        Main.getDBaccess().closeConnection();
        System.exit(0);
    }
}
