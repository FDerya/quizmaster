package controller;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.GroupDAO;
import database.mysql.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import model.Course;
import model.Group;
import model.User;
import view.Main;

import java.util.List;

public class AssignStudentsToGroupController {

    @FXML
    private ComboBox<Course> courseComboBox;
    @FXML
    private ComboBox<Group> groupComboBox;
    @FXML
    private ListView<User> studentList;
    @FXML
    private ListView<User> studentsInGroupList;
    @FXML
    private Label groupLabel;
    @FXML
    private Button assignButton;
    @FXML
    private Button removeButton;

    private CourseDAO courseDAO;
    private GroupDAO groupDAO;
    private UserDAO userDAO;

    // Sets up listeners for the course and group combo boxes to print selected items on change.
    public void setup() {
        courseComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldCourse, newCourse) ->
                        System.out.println("Geselecteerde cursus: " + observableValue + ", " + oldCourse + ", " + newCourse));
        groupComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldGroup, newGroup) ->
                        System.out.println("Geselecteerde groep: " + observableValue + ", " + oldGroup + ", " + newGroup));
    }

    // Initializes the controller by setting up data access objects, populating combo boxes,
    // and configuring selection modes and listeners for course and group combo boxes.
    public void initialize() {
        courseDAO = new CourseDAO(Main.getDBaccess(), new UserDAO(Main.getDBaccess()));
        groupDAO = new GroupDAO(Main.getDBaccess(), new UserDAO(Main.getDBaccess()));
        userDAO = new UserDAO(Main.getDBaccess());

        List<Course> allCourses = courseDAO.getAll();
        ObservableList<Course> courseItems = FXCollections.observableArrayList(allCourses);
        courseComboBox.setItems(courseItems);

        List<Group> allGroups = groupDAO.getAll();
        ObservableList<Group> groupItems = FXCollections.observableArrayList(allGroups);
        groupComboBox.setItems(groupItems);

        studentList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        studentsInGroupList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setup();
    }

    // Initializes data and sets the group label.
    public void initData(Group group) {
        groupLabel.setText("Selected Group: " + group.getGroupName());
    }

    // Handles the assignment of selected students to the group.
    @FXML
    private void doAssign(ActionEvent event) {
        ObservableList<User> selectedStudents = studentList.getSelectionModel().getSelectedItems();
        studentsInGroupList.getItems().addAll(selectedStudents);
        updateStudentLists();
    }

    // Handles the removal of selected students from the group.
    @FXML
    private void doRemove(ActionEvent event) {
        ObservableList<User> selectedStudents = studentsInGroupList.getSelectionModel().getSelectedItems();
        studentsInGroupList.getItems().removeAll(selectedStudents);
        updateStudentLists();
    }


    // Handles the "Menu" button click event, navigating back to the welcome scene
    @FXML
    private void doMenu() {
        try {
            Main.getSceneManager().showWelcomeScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update available and assigned students based on selected course and group
    private void updateStudentLists() {
        Course selectedCourse = courseComboBox.getValue();
        Group selectedGroup = groupComboBox.getValue();

        if (selectedCourse != null && selectedGroup != null) {
            DBAccess dbAccess = Main.getDBaccess();
            GroupDAO groupDAO = new GroupDAO(dbAccess, userDAO);

            List<User> availableStudents = groupDAO.getUsersNotInGroup(selectedCourse, selectedGroup);
            ObservableList<User> availableStudentsItems = FXCollections.observableArrayList(availableStudents);
            studentList.setItems(availableStudentsItems);

            List<User> studentsInGroup = groupDAO.getUsersInGroup(selectedGroup);
            ObservableList<User> studentsInGroupItems = FXCollections.observableArrayList(studentsInGroup);
            studentsInGroupList.setItems(studentsInGroupItems);
        } else {

            studentList.setItems(FXCollections.emptyObservableList());
            studentsInGroupList.setItems(FXCollections.emptyObservableList());
        }
    }
}