package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import model.User;
import view.Main;

public class WelcomeController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private MenuButton taskMenuButton;

    public void setup() {
        welcomeLabel.setText("Welkom " + User.getCurrentUser().getFirstName() + "\nJe bent ingelogd als " + User.getCurrentUser().getRole().toLowerCase());
        // Shows menu buttons by role
        String role = User.currentUser.getRole();
        switch (role) {
            case "Student":
                initializeMenuItemsStudent();
                break;
            case "Coördinator":
                initializeMenuItemsCoordinator();
                break;
            case "Administrator":
                initializeMenuItemsAdministrator();
                break;
            case "Functioneel Beheerder":
                initializeMenuItemsFunctionalManagement();
                break;
        }
    }

    public void doLogout() {
        // Verandert de currentUser naar null
        User.setCurrentUser(null);
        Main.getSceneManager().showLoginScene();
    }

    public void initializeMenuItemsStudent() {
        MenuItem sMenuItem1 = new MenuItem("In- en uitschrijven");
        sMenuItem1.setOnAction(actionEvent -> Main.getSceneManager().showStudentSignInOutScene());
        MenuItem sMenuItem2 = new MenuItem("Quiz selecteren");
        sMenuItem2.setOnAction(actionEvent -> Main.getSceneManager().showSelectQuizForStudent());

        taskMenuButton.getItems().add(sMenuItem1);
        taskMenuButton.getItems().add(sMenuItem2);
    }

    public void initializeMenuItemsCoordinator() {
        MenuItem cMenuItem1 = new MenuItem("Coördinator dashboard");
        cMenuItem1.setOnAction(actionEvent -> Main.getSceneManager().showCoordinatorDashboard());
        MenuItem cMenuItem2 = new MenuItem("Quizbeheer");
        cMenuItem2.setOnAction(actionEvent -> Main.getSceneManager().showManageQuizScene());
        MenuItem cMenuItem3 = new MenuItem("Vragenbeheer");
        cMenuItem3.setOnAction(actionEvent -> Main.getSceneManager().showManageQuestionsScene());

        taskMenuButton.getItems().add(cMenuItem1);
        taskMenuButton.getItems().add(cMenuItem2);
        taskMenuButton.getItems().add(cMenuItem3);
    }

    public void initializeMenuItemsAdministrator() {
        MenuItem aMenuItem1 = new MenuItem("Cursusbeheer");
        aMenuItem1.setOnAction(actionEvent -> Main.getSceneManager().showManageCoursesScene());
        MenuItem aMenuItem2 = new MenuItem("Groepenbeheer");
        aMenuItem2.setOnAction(actionEvent -> Main.getSceneManager().showManageGroupsScene());
        MenuItem aMenuItem3 = new MenuItem("Studenten toewijzen aan groepen");
        aMenuItem3.setOnAction(actionEvent -> Main.getSceneManager().showAssignStudentsToGroupScene());

        taskMenuButton.getItems().add(aMenuItem1);
        taskMenuButton.getItems().add(aMenuItem2);
        taskMenuButton.getItems().add(aMenuItem3);
    }

    public void initializeMenuItemsFunctionalManagement() {
        MenuItem fMenuItem1 = new MenuItem("Gebruikersbeheer");
        fMenuItem1.setOnAction(actionEvent -> Main.getSceneManager().showManageUserScene());

        taskMenuButton.getItems().add(fMenuItem1);
    }
}
