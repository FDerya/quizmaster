package controller;

import database.mysql.CourseDAO;
import database.mysql.ParticipationDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import model.Course;
import model.Group;
import model.Participation;
import model.User;
import view.Main;

import java.util.ArrayList;
import java.util.List;

public class AssignStudentsToGroupController {
    @FXML
    ComboBox<Course> courseComboBox;
    @FXML
    ComboBox<Group> groupComboBox;
    @FXML
    ListView<User> studentList;
    @FXML
    ListView<User> studentsInGroupList;
    ParticipationDAO participationDAO;
    CourseDAO courseDAO;

    public AssignStudentsToGroupController() {
        this.participationDAO = new ParticipationDAO(Main.getDBaccess());
        this.courseDAO = new CourseDAO(Main.getDBaccess());
    }

    public void setup() {
        List<Course> allCourses = courseDAO.getAll();
        ObservableList<Course> courseComboBoxList = FXCollections.observableArrayList(allCourses);
        courseComboBox.setItems(courseComboBoxList);
        Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            getGroupsPerCourse();
        }
    }

    public void doAssign() {
        Group selectedGroup = groupComboBox.getSelectionModel().getSelectedItem();
        Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        ObservableList<User> selectedUsers = studentList.getSelectionModel().getSelectedItems();
        for (User user : selectedUsers) {
            participationDAO.updateGroup(selectedGroup, selectedCourse, user);
        }
    }

    public void getGroupsPerCourse() {
        Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        List<Participation> participationPerCourse = participationDAO.getGroupsPerCourse(selectedCourse.getIdCourse());
        List<Participation> allStudentsPerCourse = participationDAO.getParticipationPerCourse(selectedCourse.getIdCourse());
        List<Group> groupsPerCourse = new ArrayList<>();
        List<User> studentsInCourse = new ArrayList<>();
        for (Participation participation : participationPerCourse) {
            groupsPerCourse.add(participation.getGroup());
        }
        for (Participation participation : allStudentsPerCourse) {
            studentsInCourse.add(participation.getUser());
        }
        ObservableList<Group> groupComboBoxList = FXCollections.observableArrayList(groupsPerCourse);
        groupComboBox.setItems(groupComboBoxList);

        studentList.getItems().addAll(studentsInCourse);
        studentList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void doRemove() {}

    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }
}
