package controller;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.ParticipationDAO;
import database.mysql.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import model.Course;
import model.Participation;
import view.Main;

import java.util.List;

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

    @FXML
    public void setup() {
        List<Course> courses = courseDAO.getAll();
        courseList.getItems().addAll(courses);
        courseList.getSelectionModel().selectedItemProperty().addListener((observableValue, course, t1) -> {
            doStudentCount();
            warningLabel.setVisible(false);
        });
    }

    public void doMenu(){
        Main.getSceneManager().showWelcomeScene();
    }

    public void doCreateCourse(){
        Main.getSceneManager().showCreateUpdateCourseScene(null);
    }

    public void doUpdateCourse(ActionEvent actionEvent){
        Course course = courseList.getSelectionModel().getSelectedItem();
        Main.getSceneManager().showCreateUpdateCourseScene(course);
    }

    public void doAskDeleteUser(ActionEvent actionEvent){
        Course course = courseList.getSelectionModel().getSelectedItem();
        if (course == null){
            warningLabel.setVisible(true);
        } else {
            warningLabel.setVisible(false);
            deleteCourseGrid.setVisible(true);
            deleteWarningLabel.setText("Je gaat de cursus \"" + course + "\" verwijderen. \n" +
                    "Dit kan niet ongedaan gemaakt worden.\nWeet je het zeker?");
        }
    }
    public void doDeleteCourse(ActionEvent actionEvent){
        Course course = courseList.getSelectionModel().getSelectedItem();
        if (course != null){
            courseDAO.deleteOne(course);
            courseList.getItems().remove(course);
            deleteCourseGrid.setVisible(false);
        } else {
            warningLabel.setText("Houd de cursus geselecteerd.");
            warningLabel.setVisible(true);
        }
    }
    public void doNotDeleteCourse(ActionEvent actionEvent) {
        deleteCourseGrid.setVisible(false);
    }

    public void doStudentCount(){
        Course course = courseList.getSelectionModel().getSelectedItem();
        List<Participation> participation = participationDAO.getParticipationPerCourse(course.getIdCourse());
        if (course == null) {
            studentCounter.setText("Selecteer een cursus om te zien " +
                    "hoeveel student zijn ingeschreven\n" +
                    "of om een actie uit te voeren.");
            studentCounter.setVisible(true);
        } else {
            int counter = 0;
            for (Participation p : participation){
                counter++;
            }
            if (counter == 0){
                studentCounter.setText("Voor de cursus " + course + " zijn nog geen studenten ingeschreven.");
            } else {
                studentCounter.setText("Voor de cursus " + course + " zijn " + counter + " studenten ingeschreven.");
            }
            studentCounter.setVisible(true);
        }
    }
}
