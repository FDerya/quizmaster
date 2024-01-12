package controller;

import database.mysql.CourseDAO;
import database.mysql.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Course;
import model.User;
import view.Main;

import java.util.List;
import java.util.Optional;

public class CreateUpdateCourseController {
// Attributes
    private final CourseDAO courseDAO;
    private final UserDAO userDAO = new UserDAO(Main.getDBaccess());
    private int idCourse;
    @FXML
    Label titleLabel;
    @FXML
    Label courseNameLabel;
    @FXML
    Label coordinatorLabel;
    @FXML
    Label levelLabel;
    @FXML
    Label warningLabel;
    @FXML
    TextField courseNameTextField;
    @FXML
    ComboBox<User> coordinatorComboBox;
    @FXML
    ComboBox<String> levelComboBox;
    ObservableList<String> levelOptions = FXCollections.observableArrayList("Beginner", "Medium", "Gevorderd");
    List<User> coordinators = userDAO.getAllCoordinators();
    ObservableList<User> coordinatorOptions = FXCollections.observableArrayList(coordinators);

// Contructor
    public CreateUpdateCourseController(){
        this.courseDAO = new CourseDAO(Main.getDBaccess());
    }

// Method to setup the createUpdateCourse page
    public void setup(Course course) {
        levelComboBox.setItems(levelOptions);
        coordinatorComboBox.setItems(coordinatorOptions);
        if (course != null){
            idCourse = course.getIdCourse();
            titleLabel.setText("Wijzig cursus");
            courseNameTextField.setText(String.valueOf(course.getNameCourse()));
            coordinatorComboBox.setValue(course.getCoordinator());
            levelComboBox.setValue(course.getDifficultyCourse());
        }
    }

// Method to save a new or updated course
    public void doSaveCourse(ActionEvent actionEvent){
        Course course = createNewCourse();
        String updateCourseAlert = "Cursus gewijzigd";
        String newCourseAlert = "Cursus toegevoegd";
        if (course != null){
            if (titleLabel.getText().equals("Nieuwe Cursus")){
                courseDAO.storeOne(course);
                warningLabel.setText(newCourseAlert);
                warningLabel.setVisible(true);

            }else {
                course.setIdCourse(idCourse);
                courseDAO.updateOne(course);
            }
        }
    }

// Method to get back to the Welcome page
    public void doMenu(ActionEvent actionEvent) {Main.getSceneManager().showWelcomeScene();}

// Method to get back to the manageCourse page
    public void doShowManageCourse(ActionEvent actionEvent) {Main.getSceneManager().showManageCoursesScene();}

// Method to create a new course with a warning if not all fields are filled in
    private Course createNewCourse(){
        String error = "Je hebt niet alle velden ingevuld.\nAlle velden zijn verplicht.";
        String courseName = courseNameTextField.getText();
        User coordinator = coordinatorComboBox.getSelectionModel().getSelectedItem();
        String level = levelComboBox.getSelectionModel().getSelectedItem();
        if (courseName.isEmpty() || coordinator == null || level == null) {
            warningLabel.setText(error);
            warningLabel.setVisible(true);
            return null;
        } else {
            return new Course(0, coordinator, courseName, level);
        }
    }
}
