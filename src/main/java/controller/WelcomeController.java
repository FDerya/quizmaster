package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import model.Course;
import model.Quiz;
import model.User;
import view.Main;

public class WelcomeController {

    User testUser = new User("Denshi", "ww", "Denshi", "null","Kerio", "Student");
    Course testCourse = new Course(testUser, "test course", "Gevorderd");
    Quiz testQuiz = new Quiz(1, testCourse, "Test", 1, 1);

    @FXML
    private Label welcomeLabel;
    @FXML
    private MenuButton taskMenuButton;

    public void setup() {
        welcomeLabel.setText("Welkom " + User.getCurrentUser().getFirstName());
        // Menuitems voor de student
        MenuItem item1 = new MenuItem("In- en uitschrijven");
        item1.setOnAction(actionEvent -> Main.getSceneManager().showStudentSignInOutScene());
        MenuItem item2 = new MenuItem("Quiz selecteren");
        item2.setOnAction(actionEvent -> Main.getSceneManager().showSelectQuizForStudent());

        // Menuitems voor de coördinator
        MenuItem item3 = new MenuItem("Coördinator dashboard");
        item3.setOnAction(actionEvent -> Main.getSceneManager().showCoordinatorDashboard());

        // Menuitems voor de administrator
        MenuItem item4 = new MenuItem("Cursusbeheer");
        item4.setOnAction(actionEvent -> Main.getSceneManager().showManageCoursesScene());
        MenuItem item5 = new MenuItem("Studenten toewijzen aan groepen");
        item5.setOnAction(actionEvent -> Main.getSceneManager().showAssignStudentsToGroupScene());

        // Menuitems voor de functioneel beheerder
        MenuItem item6 = new MenuItem("Studentenbeheer");
        item6.setOnAction(actionEvent -> Main.getSceneManager().showManageUserScene());

        // Shows menu buttons by role
        String role = User.currentUser.getRole();
        switch (role) {
            case "Student":
                taskMenuButton.getItems().add(item1);
                taskMenuButton.getItems().add(item2);
                break;
            case "Coördinator":
                taskMenuButton.getItems().add(item3);
                break;
            case "Administrator":
                taskMenuButton.getItems().add(item4);
                taskMenuButton.getItems().add(item5);
                break;
            case "Functioneel Beheerder":
                taskMenuButton.getItems().add(item6);
        }

    }

    public void doLogout() {
        // Verandert de currentUser naar null
        User.setCurrentUser(null);
        Main.getSceneManager().showLoginScene();
    }
}
