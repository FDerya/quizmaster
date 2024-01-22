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
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import model.User;
import view.Main;

import java.util.List;

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
    Label messageLabel;
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
    ObservableList<String> userRoles = FXCollections.observableArrayList("Student", "Docent", "Coördinator", "Administrator", "Functioneel Beheerder");
    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), actionEvent -> Main.getSceneManager().showManageUserScene()));


    public CreateUpdateUserController() {
        this.userDAO = new UserDAO(Main.getDBaccess());
    }

    public void initialize() {
        // When the page initializes, gives the textfield a property to set the messagelabel to false when you type in the field.
        for (TextField textField : textFieldsInArray()) {
            textField.textProperty().addListener((observableValue, oldValue, newValue) -> messageLabel.setVisible(false));
        }
        // Sets a max amount of characters to the username, same as the max amount in the SQL database.
        int maxCharsUsername = 10;
        usernameTextfield.setTextFormatter(setMaxAmountOfCharactersInTextField(maxCharsUsername));
        int maxCharsPassword = 25;
        passwordTextfield.setTextFormatter(setMaxAmountOfCharactersInTextField(maxCharsPassword));
        int maxCharsFirstname = 45;
        firstNameTextfield.setTextFormatter(setMaxAmountOfCharactersInTextField(maxCharsFirstname));
        int maxCharsPrefix = 10;
        prefixTextfield.setTextFormatter(setMaxAmountOfCharactersInTextField(maxCharsPrefix));
        int maxCharsSurname = 45;
        surnameTextfield.setTextFormatter(setMaxAmountOfCharactersInTextField(maxCharsSurname));
    }

    private static TextFormatter<String> setMaxAmountOfCharactersInTextField(int maxLength) {
        StringConverter<String> converter = new DefaultStringConverter();
        return new TextFormatter<>(converter, "", c ->
                c.getControlNewText().length() <= maxLength ? c : null);
    }

    // In de setup wordt de combobox voor de rollen gevuld en worden textfields gevuld 
    // als er in het manageUsers scherm een gebruiker geselecteerd is.
    public void setup(User user) {
        roleComboBox.setItems(userRoles);
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
        if (user != null) {
            if (checkForDuplicates(user)) {
                usernameLabel.setTextFill(Color.RED);
                messageLabel.setVisible(true);
                messageLabel.setText("Gebruikersnaam " + user.getUsername() + " is al in gebruik, kies een andere gebruikersnaam");
            } else {
                usernameLabel.setTextFill(Color.BLACK);
                saveUser(user);
            }
        }
    }

    private void saveUser(User user) {
        if (titleLabel.getText().equals("Nieuwe gebruiker")) {
            saveNewUser(user);
        } else {
            saveExistingUser(user);
        }
        messageLabel.setVisible(true);
    }

    private void saveExistingUser(User user) {
        String updateUserMessage = "Gebruiker " + user.getFullName() + " gewijzigd";
        user.setIdUser(idUser);
        userDAO.updateOne(user);
        messageLabel.setText(updateUserMessage);
        timeline.play();
    }

    private void saveNewUser(User user) {
        String createUserMessage = "Gebruiker " + user.getFullName() + " opgeslagen";
        userDAO.storeOne(user);
        messageLabel.setText(createUserMessage);
        clearTextFieldsAndRoleComboBox();
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
        messageLabel.setVisible(false);
        return new User(username, password, firstname, prefix, surname, role);
    }

    // Als niet aan de voorwaarden voor nieuwe/wijzigen gebruiker voldaan is, wordt een warning label getoond en return je null.
    private User incorrectUser() {
        messageLabel.setVisible(true);
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

    // Checks if a textfield is empty.
    // If its empty, the label next to the textfield is colored red and a warning is showed.
    private void checkAndChangeLabelColor(boolean emptyTextField, Label label) {
        if (emptyTextField) {
            label.setTextFill(Color.RED);
        } else {
            label.setTextFill(Color.BLACK);
        }
    }

    // Shows warninglabel and sets label color if a role is null.
    private void isCorrectInputRole(String role) {
        if (role == null) {
            messageLabel.setVisible(true);
            roleLabel.setTextFill(Color.RED);
        } else {
            messageLabel.setVisible(false);
            roleLabel.setTextFill(Color.BLACK);
        }
    }

    public void clearTextFieldsAndRoleComboBox() {
        // Clear textfields
        for (TextField textField : textFieldsInArray()) {
            textField.clear();
        }
        // Clear combobox and show prompt text
        roleComboBox.getSelectionModel().select(null);
        roleComboBox.setPromptText("Kies een rol");
        roleComboBox.setButtonCell(new ListCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Kies een rol");
                } else {
                    setText(item);
                }
            }
        });
    }

    private TextField[] textFieldsInArray() {
        return new TextField[]{usernameTextfield, passwordTextfield, firstNameTextfield, prefixTextfield, surnameTextfield};
    }

    private boolean checkForDuplicates(User user) {
        // Sets the id of the user, if you are editing an existing user.
        if (!titleLabel.getText().equals("Nieuwe gebruiker")) {
            user.setIdUser(idUser);
        }

        // Creates a list from the database, iterates through that list and sets the value of boolean duplicate
        // if the fullname and the userid are identical with the user in the parameter.
        List<User> allUsers = userDAO.getAll();
        boolean duplicate = false;
            for (User userInUserList : allUsers) {
                if ((userInUserList.getUsername().equals(user.getUsername())) && (userInUserList.getIdUser() != user.getIdUser())) {
                    duplicate = true;
                    break;
                }
            }
        return duplicate;
    }
}
