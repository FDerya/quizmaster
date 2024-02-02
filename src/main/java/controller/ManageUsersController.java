package controller;

import database.mysql.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.User;
import view.Main;
import java.util.List;

public class ManageUsersController extends WarningAlertController {
    // Attributes and FXML
    private final UserDAO userDAO;
    @FXML
    ListView<User> userList;
    @FXML
    Label userInformation;
    @FXML
    Button mainScreenButton;

    // Controller
    public ManageUsersController() {
        this.userDAO = new UserDAO(Main.getDBaccess());
    }

    // Fills the listview from the userDAO and shows the role counter.
        public void setup() {
        List<User> users = userDAO.getAll();
        userList.getItems().addAll(users);
        userList.setCellFactory(param -> createHBox());
        doCounterRole();
        userList.getSelectionModel().selectedItemProperty().addListener((observableValue, user, t1) -> doCounterRole());
        mainScreenButton.setText(Main.getMainScreenButtonText());
    }

    // Makes a Hbox which shows the user fullname and the role of the user next to it.
    private static ListCell<User> createHBox() {
        return new ListCell<>() {
            @Override
            public void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                Label nameUser = new Label();
                nameUser.setPrefWidth(200.0);
                Label roleUser = new Label();
                HBox hbox = new HBox(nameUser, roleUser);
                if (!(item == null || empty)) {
                    nameUser.setText(item.getFullName());
                    roleUser.setText(item.getRole());
                }
                setGraphic(hbox);
            }
        };
    }

    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }

    // Go to the createUpdateUser screen, parameter null so it doesn't fill the textboxes and comboboxes
    public void doCreateUser() {
        Main.getSceneManager().showCreateUpdateUserScene(null);
    }

    // Go to the createUpdateUser sceen. If a user is selected, the information of the user is loaded into the
    // textfields, else it gives you a warning you have to select a user.
    public void doUpdateUser(ActionEvent event) {
        User user = userList.getSelectionModel().getSelectedItem();
        if (user != null) {
            Main.getSceneManager().showCreateUpdateUserScene(user);
        } else {
            userInformation.setText("Kies een gebruiker");
        }
    }

    // handles if a user is selected and sets the warningLabel to delete the user.
    public void doDeleteUser (ActionEvent event) {
        User user = userList.getSelectionModel().getSelectedItem();
        if (user == null) {
            warningLabel.setVisible(true);
        } else {
            warningLabel.setVisible(false);
            deleteUser(user);
            userList.getSelectionModel().clearSelection();
        }
    }

    // Shows warning alert to delete the selected user. After you press yes on the warning alert,
    // it removes the user from the database and the userlist.
    private void deleteUser(User user) {
        if (confirmDeletion(user.getFullName(), "gebruiker")) {
            userDAO.removeOne(user);
            userList.getItems().remove(user);
        }
    }

    // Counts the number of users that has the same role as the selected user.
    public void doCounterRole() {
        User user = userList.getSelectionModel().getSelectedItem();
        List<User> users = userDAO.getAll();

        if (user == null) {
            userInformation.setText("Kies een gebruiker");
            userInformation.setVisible(true);
        } else {
            int counter = (int) users.stream().filter(roleUsers -> roleUsers.getRole().equals(user.getRole())).count();
            userInformation.setVisible(true);
            userInformation.setText(counter == 1 ? "Er is " + counter + " " + user.getRole().toLowerCase() :
                    "Er zijn " + counter + " " + getRoleTextInPlural(user.getRole()).toLowerCase());
            }
    }

    private String getRoleTextInPlural(String role) {
        String pluralRole = "";
        switch (role) {
            case "Student":
                pluralRole = "Studenten";
                break;
            case "Administrator":
                pluralRole = "Administratoren";
                break;
            case "Coördinator":
                pluralRole = "Coördinatoren";
                break;
            case "Functioneel Beheerder":
                pluralRole = "Functioneel Beheerders";
                break;
            case "Docent":
                pluralRole = "Docenten";
                break;
        }
        return pluralRole;
    }
}
