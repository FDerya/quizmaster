package controller;

import database.mysql.UserDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import model.User;
import view.Main;

public class CreateUpdateUserController {
    private final UserDAO userDAO;
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
    TextField surnameTextfield;
    @FXML
    ComboBox<String> roleComboBox;
    @FXML
    Label warningLabelNoRole;
    @FXML
    Label warningLabelNoFields;
    @FXML
    Label usernameLabel;
    @FXML
    Label passwordLabel;
    @FXML
    Label firstnameLabel;
    @FXML
    Label surnameLabel;
    @FXML
    Label roleLabel;
    @FXML
    Label createUpdateMessage;
    ObservableList<String> rollen = FXCollections.observableArrayList("Student", "Docent", "Coördinator", "Administrator", "Functioneel Beheerder");
    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), actionEvent -> Main.getSceneManager().showManageUserScene()));

    public CreateUpdateUserController() {
        this.userDAO = new UserDAO(Main.getDBaccess());
    }

    // In de setup wordt de combobox voor de rollen gevuld en worden textfields gevuld 
    // als er in het manageUsers scherm een gebruiker geselecteerd is.
    public void setup(User user) {
        roleComboBox.setItems(rollen);
        if (user != null) {
            idUser = user.getIdUser();
            titleLabel.setText("Wijzig gebruiker");
            usernameTextfield.setText(String.valueOf(user.getUsername()));
            passwordTextfield.setText(String.valueOf(user.getPassword()));
            firstNameTextfield.setText(String.valueOf(user.getFirstName()));
            prefixTextfield.setText(String.valueOf(user.getPrefix()));
            surnameTextfield.setText(String.valueOf(user.getSurname()));
            roleComboBox.getSelectionModel().select(user.getRole());
        }
    }

    // Deze methode slaat een nieuwe gebruiker op in de database of wijzigt een bestaande gebruiker.
    @FXML
    public void doSaveUser(ActionEvent actionEvent) {
        User user = createUser();
        String updateUserMessage = "Gebruiker gewijzigd";
        String createUserMessage = "Gebruiker opgeslagen";
        if (user != null) {
            if (titleLabel.getText().equals("Nieuwe gebruiker")) {
                userDAO.storeOne(user);
                createUpdateMessage.setText(createUserMessage);
            } else {
                user.setIdUser(idUser);
                userDAO.updateOne(user);
                createUpdateMessage.setText(updateUserMessage);
            }
            createUpdateMessage.setVisible(true);
        }
        timeline.play();
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
        boolean correctInput;
        String username = usernameTextfield.getText();
        String password = passwordTextfield.getText();
        String firstname = firstNameTextfield.getText();
        String prefix = prefixTextfield.getText();
        String surname = surnameTextfield.getText();
        String role = roleComboBox.getSelectionModel().getSelectedItem();
        isCorrectInputRole(role);
        correctInput = isCorrectInput(username, password, firstname, surname);
        if (role == null || !correctInput) {
            return incorrectUser();
        } else {
            return correctUser(username, password, firstname, prefix, surname, role);
        }
    }

    // Als aan de voorwaarden voor nieuwe gebruiker/wijzigen voldaan is, worden de warning labels weggehaald en return je een user
    private User correctUser(String username, String password, String firstname, String prefix, String surname, String role) {
        warningLabelNoRole.setVisible(false);
        warningLabelNoFields.setVisible(false);
        return new User(username, password, firstname, prefix, surname, role);
    }

    // Als niet aan de voorwaarden voor nieuwe/wijzigen gebruiker voldaan is, wordt een warning label getoond en return je null.
    private User incorrectUser() {
        warningLabelNoRole.setVisible(false);
        warningLabelNoFields.setVisible(false);
        return null;
    }

    // Deze methode roept checkAndChangeLabelColor aan en geeft een boolean terug of meegegeven velden ingevuld zijn of niet.
    private boolean isCorrectInput(String username, String password, String firstname, String lastname) {
        checkAndChangeLabelColor(username.isEmpty(), usernameLabel);
        checkAndChangeLabelColor(password.isEmpty(), passwordLabel);
        checkAndChangeLabelColor(firstname.isEmpty(), firstnameLabel);
        checkAndChangeLabelColor(lastname.isEmpty(), surnameLabel);
        return (!username.isEmpty() && !password.isEmpty() && !firstname.isEmpty() && !lastname.isEmpty());
    }

    // Deze methode kijkt of een tekstveld leeg is. Als het veld leeg is wordt het bijbehorende label roodgekleurd en
    // wordt er een waarschuwing getoond.
    private void checkAndChangeLabelColor(boolean emptyTextField, Label label) {
        if (emptyTextField) {
            label.setTextFill(Color.RED);
        } else {
            label.setTextFill(Color.BLACK);
        }
    }

    // Deze methode geeft een warningLabel als er geen rol gekozen is. Als de rol wel gekozen is, wordt de label weggehaald.
    private void isCorrectInputRole(String role) {
        String errorMessageNoRole = "Je hebt geen rol gekozen voor de gebruiker.";
        if (role == null) {
            warningLabelNoRole.setText(errorMessageNoRole);
            warningLabelNoRole.setVisible(true);
        } else {
            warningLabelNoRole.setVisible(false);
        }
    }
}
