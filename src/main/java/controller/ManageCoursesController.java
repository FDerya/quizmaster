package controller;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.ParticipationDAO;
import database.mysql.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Course;
import model.Participation;
import view.Main;

import java.util.List;
import java.util.Optional;

public class ManageCoursesController {
// Attributes
    private final ParticipationDAO participationDAO = new ParticipationDAO(Main.getDBaccess());
    private final CourseDAO courseDAO;

    @FXML
    ListView<Course> courseList;
    @FXML
    Button newCourseButton;
    @FXML
    Button menuButton;
    @FXML
    GridPane deleteCourseGrid;
    @FXML
    Label deleteWarningLabel;
    @FXML
    Button deleteYesButton;
    @FXML
    Button deleteNoButton;
    @FXML
    Label warningLabel;
    @FXML
    Label studentCounter;

//Constructor
    public ManageCoursesController(){
        this.courseDAO = new CourseDAO(Main.getDBaccess());
    }

// Method to setup the manageCourses screen
    @FXML
    public void setup() {
        List<Course> courses = courseDAO.getAll();
        courseList.getItems().addAll(courses);
        doStudentCount();
        courseList.getSelectionModel().selectedItemProperty().addListener((observableValue, course, t1) -> {
            doStudentCount();
            warningLabel.setVisible(false);
        });
    }

// Method to go back to the welcome screen
    public void doMenu(){
        Main.getSceneManager().showWelcomeScene();
    }

// Method to go to the createUpdateCourse screen
    public void doCreateCourse(){
        Main.getSceneManager().showCreateUpdateCourseScene(null);
    }

// Method to change a course in the database
    public void doUpdateCourse(ActionEvent actionEvent){
        Course course = courseList.getSelectionModel().getSelectedItem();
        Main.getSceneManager().showCreateUpdateCourseScene(course);
    }

// Method to delete a course from the database
    public void doDeleteCourse(ActionEvent actionEvent){
        Course course = courseList.getSelectionModel().getSelectedItem();
        if (course != null){
            showDeleteAlert(course);
        } else {
            warningLabel.setText("Houd de cursus geselecteerd.");
            warningLabel.setVisible(true);
        }
    }

// Shows an alert when deleting a course to ask whether to continue
    public void showDeleteAlert(Course course){
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setContentText("Je gaat de cursus " + course.getNameCourse() + " verwijderen.\n" +
                "Deze actie kan niet ongedaan gemaakt worden.\n");
        deleteAlert.setTitle("Verwijder cursus");
        deleteAlert.setHeaderText(null);
        ButtonType buttonYes = new ButtonType("Verwijderen", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("Annuleren", ButtonBar.ButtonData.NO);
        deleteAlert.getButtonTypes().setAll(buttonYes, buttonNo);
        Optional<ButtonType> result = deleteAlert.showAndWait();
        if (result.get().equals(buttonYes)){
            courseDAO.deleteOne(course);
            courseList.getItems().remove(course);
        } else if (result.get().equals(buttonNo)) {
            deleteAlert.close();
        }
    }

// Counts how many students are enrolled in a course
    public void doStudentCount(){
        Course course = courseList.getSelectionModel().getSelectedItem();
        if (course == null) {
            studentCounter.setText("Selecteer een cursus om te zien " +
                    "hoeveel student zijn ingeschreven,\nof om een actie uit te voeren.");
        } else {
            List<Participation> participation = participationDAO.getParticipationPerCourse(course.getIdCourse());
            int counter = participation.size();
            if (counter == 0){
                studentCounter.setText("Voor de cursus " + course + " zijn nog geen studenten ingeschreven.");
            } else {
                studentCounter.setText("Voor de cursus " + course + " zijn " + counter + " studenten ingeschreven.");
            }
        }
    }
}
