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

public class CreateUpdateUserController extends WarningAlertController {
    private final UserDAO userDAO;
    private int idUser;         // saves the users id in the global scope, so you can set it later on to edit a user.
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
    ObservableList<String> userRoles = FXCollections.observableArrayList("Student", "Docent", "Coördinator",
            "Administrator", "Functioneel Beheerder");
    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), actionEvent ->
            Main.getSceneManager().showManageUserScene()));

    public CreateUpdateUserController() {
        this.userDAO = new UserDAO(Main.getDBaccess());
    }

    public void initialize() {
        // When the page initializes, gives the textfield a property to set the messagelabel to false
        // when you type in the field.
        for (TextField textField : textFieldsInArray()) {
            textField.textProperty().addListener((observableValue, oldValue, newValue) ->
                    warningLabel.setVisible(false));
        }
        setMaxCharsForTextField();
    }

    // Sets a max amount of characters to the textfields, same as the max amount in the SQL database.
    private void setMaxCharsForTextField() {
        int maxCharsUsername = 10;
        int maxCharsPassword = 25;
        int maxCharsFirstname = 45;
        int maxCharsPrefix = 10;
        int maxCharsSurname = 45;
        usernameTextfield.setTextFormatter(setMaxAmountOfCharactersInTextField(maxCharsUsername));
        passwordTextfield.setTextFormatter(setMaxAmountOfCharactersInTextField(maxCharsPassword));
        firstNameTextfield.setTextFormatter(setMaxAmountOfCharactersInTextField(maxCharsFirstname));
        prefixTextfield.setTextFormatter(setMaxAmountOfCharactersInTextField(maxCharsPrefix));
        surnameTextfield.setTextFormatter(setMaxAmountOfCharactersInTextField(maxCharsSurname));
    }

    private static TextFormatter<String> setMaxAmountOfCharactersInTextField(int maxLength) {
        StringConverter<String> converter = new DefaultStringConverter();
        return new TextFormatter<>(converter, "", c ->
                c.getControlNewText().length() <= maxLength ? c : null);
    }

    // Fills the combobox with the userRoles. If a user is selected when you enter the screen,
    // it fills in the textfields and comboboxes.
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

    // Before saving the user, it creates a user from the given input, checks if all mandatory fields are filled in and
    // if there are no duplicates in the database. If all conditions are met, it saves or updates the user.
    @FXML
    public void doSaveUser(ActionEvent actionEvent) {
        User user = createUser();
        if (user != null) {
            if (checkForDuplicates(user)) {
                usernameLabel.setTextFill(Color.RED);
                showSame(checkForDuplicates(user), "gebruikers");
            } else {
                usernameLabel.setTextFill(Color.BLACK);
                saveUser(user);
            }
        }
    }

    // Saves or updates a user to the database
    private void saveUser(User user) {
        if (titleLabel.getText().equals("Nieuwe gebruiker")) {
            saveNewUser(user);
        } else {
            updateUser(user);
        }
        warningLabel.setVisible(true);
    }

    private void updateUser(User user) {
        user.setIdUser(idUser);
        userDAO.updateOne(user);
        showUpdated("Gebruiker " + user.getFullName());
        timeline.play();
    }

    private void saveNewUser(User user) {
        userDAO.storeOne(user);
        showSaved("Gebruiker " + user.getFullName());
        clearTextFieldsAndRoleComboBox();
    }

    @FXML
    public void doShowManageUsers(ActionEvent actionEvent) {
        Main.getSceneManager().showManageUserScene();
    }

    @FXML
    public void doShowMenu(ActionEvent actionEvent) {
        Main.getSceneManager().showWelcomeScene();
    }

    // Creates an object user. If not all fields are filled in correctly,
    // shows a warning label and marks the incorrect fields.
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

    // Removes the warning label and returns the user if all conditions for creating/updating are met
    private User correctUser(String username, String password, String firstname, String prefix, String surname, String role) {
        showWarningLabel(false);
        return new User(username, password, firstname, prefix, surname, role);
    }

    // Shows a warning label if not all conditions for creating/updating are met
    private User incorrectUser() {
        showWarningLabel(true);
        return null;
    }

    // Checks if a field is filled in, marks the textlabel for that textfield red.
    private boolean isCorrectInput(String username, String password, String firstname, String lastname) {
        checkAndChangeLabelColor(username.isEmpty(), usernameLabel);
        checkAndChangeLabelColor(password.isEmpty(), passwordLabel);
        checkAndChangeLabelColor(firstname.isEmpty(), firstnameLabel);
        checkAndChangeLabelColor(lastname.isEmpty(), surnameLabel);
        return (!username.isEmpty() && !password.isEmpty() && !firstname.isEmpty() && !lastname.isEmpty());
    }

    // Shows warninglabel and sets label color if a role is null.
    private void isCorrectInputRole(String role) {
        if (role == null) {
            roleLabel.setTextFill(Color.RED);
        } else {
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

    // All textFields in an array, to loop through the array.
    private TextField[] textFieldsInArray() {
        return new TextField[]{usernameTextfield, passwordTextfield, firstNameTextfield, prefixTextfield, surnameTextfield};
    }

    private boolean checkForDuplicates(User user) {
        // Sets the id of the user, if you are not creating a new user.
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
