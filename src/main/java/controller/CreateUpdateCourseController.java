package controller;

import database.mysql.CourseDAO;
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
import model.Course;
import model.User;
import view.Main;

import java.util.List;

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
    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), actionEvent
            -> Main.getSceneManager().showManageCoursesScene()));


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
        setColoursBackToBlack();
        Course course = createNewCourse();
        if (course != null){
            if (titleLabel.getText().equals("Nieuwe Cursus")){
                courseDAO.storeOne(course);
                warningLabel.setText("De cursus " + course + " is toegevoegd");
            }else {
                course.setIdCourse(idCourse);
                courseDAO.updateOne(course);
                warningLabel.setText("De cursus " + course + " is aangepast en opgeslagen");
            }
            timeline.play();
        }
    }

// Method to get back to the Welcome page
    public void doMenu() {Main.getSceneManager().showWelcomeScene();}

// Method to get back to the manageCourse page
    public void doShowManageCourse(ActionEvent actionEvent) {Main.getSceneManager().showManageCoursesScene();}

// Method to create a new course with a warning if not all fields are filled in
    private Course createNewCourse(){
        String courseName = courseNameTextField.getText();
        User coordinator = coordinatorComboBox.getSelectionModel().getSelectedItem();
        String level = levelComboBox.getSelectionModel().getSelectedItem();
        if (courseName.isEmpty() || coordinator == null || level == null) {
            doWhenFieldEmpty(courseName, coordinator, level);
            return null;
        } else if (checkExistenceCourseName(courseName)) {
            warningLabel.setText("De naam van de cursus bestaat al, kies een nieuwe naam.");
            return null;
        } else {
            return new Course(0, coordinator, courseName, level);
        }
    }

// Method to set the labels to the colour red when a field is left empty and show a warning
    private void doWhenFieldEmpty(String courseName, User coordinator, String level) {
        warningLabel.setText("Je hebt niet alles ingevuld.");
        warningLabel.setVisible(true);
        if (courseName.isEmpty()) {courseNameLabel.setTextFill(Color.RED);}
        if (coordinator == null) {coordinatorLabel.setTextFill(Color.RED);}
        if (level == null) {levelLabel.setTextFill(Color.RED);}
    }
    private void setColoursBackToBlack() {
        courseNameLabel.setTextFill(Color.BLACK);
        coordinatorLabel.setTextFill(Color.BLACK);
        levelLabel.setTextFill(Color.BLACK);
    }

// Method to check whether the course name already exists
    private boolean checkExistenceCourseName(String courseName) {
        List<Course> courses = courseDAO.getAll();
        boolean existenceCourseName = false;
        for (Course course1 : courses){
            if (courseName.equals(course1.getNameCourse())) {
                existenceCourseName = true;
                break;
            }
        }
        return existenceCourseName;
    }
}
