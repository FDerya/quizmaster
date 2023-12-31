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
        welcomeLabel.setText("Welkom " + User.getCurrentUser().getFirstName() + "\nU bent ingelogd als " + User.getCurrentUser().getRole().toLowerCase());
        // Menuitems voor de student
        MenuItem sMenuItem1 = new MenuItem("In- en uitschrijven");
        sMenuItem1.setOnAction(actionEvent -> Main.getSceneManager().showStudentSignInOutScene());
        MenuItem sMenuItem2 = new MenuItem("Quiz selecteren");
        sMenuItem2.setOnAction(actionEvent -> Main.getSceneManager().showSelectQuizForStudent());

        // Menuitems voor de coördinator
        MenuItem cMenuItem1 = new MenuItem("Coördinator dashboard");
        cMenuItem1.setOnAction(actionEvent -> Main.getSceneManager().showCoordinatorDashboard());
        MenuItem cMenuItem2 = new MenuItem("Quizbeheer");
        cMenuItem2.setOnAction(actionEvent -> Main.getSceneManager().showManageQuizScene());
        MenuItem cMenuItem3 = new MenuItem("Vragenbeheer");
        cMenuItem3.setOnAction(actionEvent -> Main.getSceneManager().showManageQuestionsScene());

        // Menuitems voor de administrator
        MenuItem aMenuItem1 = new MenuItem("Cursusbeheer");
        aMenuItem1.setOnAction(actionEvent -> Main.getSceneManager().showManageCoursesScene());
        MenuItem aMenuItem2 = new MenuItem("Studenten toewijzen aan groepen");
        aMenuItem2.setOnAction(actionEvent -> Main.getSceneManager().showAssignStudentsToGroupScene());

        // Menuitems voor de functioneel beheerder
        MenuItem fMenuItem1 = new MenuItem("Studentenbeheer");
        fMenuItem1.setOnAction(actionEvent -> Main.getSceneManager().showManageUserScene());

        // Shows menu buttons by role
        String role = User.currentUser.getRole();
        switch (role) {
            case "Student":
                taskMenuButton.getItems().add(sMenuItem1);
                taskMenuButton.getItems().add(sMenuItem2);
                break;
            case "Coördinator":
                taskMenuButton.getItems().add(cMenuItem1);
                taskMenuButton.getItems().add(cMenuItem2);
                taskMenuButton.getItems().add(cMenuItem3);
                break;
            case "Administrator":
                taskMenuButton.getItems().add(aMenuItem1);
                taskMenuButton.getItems().add(aMenuItem2);
                break;
            case "Functioneel Beheerder":
                taskMenuButton.getItems().add(fMenuItem1);
        }
    }

    public void doLogout() {
        // Verandert de currentUser naar null
        User.setCurrentUser(null);
        Main.getSceneManager().showLoginScene();
    }
}
