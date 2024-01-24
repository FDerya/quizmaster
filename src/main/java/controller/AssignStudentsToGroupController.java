package controller;

import database.mysql.CourseDAO;
import database.mysql.GroupDAO;
import database.mysql.ParticipationDAO;
import database.mysql.UserDAO;
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
    GroupDAO groupDAO;
    UserDAO userDAO;

    public AssignStudentsToGroupController() {
        this.participationDAO = new ParticipationDAO(Main.getDBaccess());
        this.courseDAO = new CourseDAO(Main.getDBaccess());
        this.userDAO = new UserDAO(Main.getDBaccess());
        this.groupDAO = new GroupDAO(Main.getDBaccess(), userDAO, courseDAO);
    }

    public void setup() {
        List<Course> allCourses = courseDAO.getAll();
        ObservableList<Course> courseComboBoxList = FXCollections.observableArrayList(allCourses);
        courseComboBox.setItems(courseComboBoxList);
        fillStudentList();
        getGroupsPerCourse();
        fillStudentInGroupList();
    }

    private void fillStudentList() {
        courseComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldCourse, newCourse) -> {
            studentList.getItems().clear();
            List<Participation> participationPerCourse = participationDAO.getParticipationPerCourse(newCourse.getIdCourse());
            List<User> usersInParticipation = new ArrayList<>();
            for (Participation participation : participationPerCourse) {
                usersInParticipation.add(participation.getUser());
            }
            studentList.getItems().addAll(FXCollections.observableArrayList(usersInParticipation));
            studentList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }));
    }
    private void fillStudentInGroupList() {
        Group selectedGroup = groupComboBox.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            courseComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldCourse, newCourse) -> {
                studentsInGroupList.getItems().clear();
                List<Participation> participationPerCourse = participationDAO.getParticipationInGroup(newCourse.getIdCourse(), selectedGroup.getIdGroup());
                List<User> usersInGroup = new ArrayList<>();
                for (Participation participation : participationPerCourse) {
                    usersInGroup.add(participation.getUser());
                }
                studentsInGroupList.getItems().addAll(FXCollections.observableArrayList(usersInGroup));
                studentsInGroupList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            }));
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
        courseComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldCourse, newCourse) -> {
            groupComboBox.getItems().clear();
    //        List<Group> groupsPerCourse = groupDAO.getGroupsByIdCourse(newCourse.getIdCourse());
    //        groupComboBox.setItems(FXCollections.observableList(groupsPerCourse));
        }));
//        Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
//        List<Participation> participationPerCourse = participationDAO.getGroupsPerCourse(selectedCourse.getIdCourse());
//        List<Group> groupsPerCourse = new ArrayList<>();
//        for (Participation participation : participationPerCourse) {
//            groupsPerCourse.add(participation.getGroup());
//        }
//
//        ObservableList<Group> groupComboBoxList = FXCollections.observableArrayList(groupsPerCourse);
//        groupComboBox.setItems(groupComboBoxList);
    }
    public void doRemove() {
        Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        ObservableList<User> selectedUsers = studentList.getSelectionModel().getSelectedItems();
        for (User user : selectedUsers) {
            participationDAO.updateGroup(null, selectedCourse, user);
        }
    }

    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }
}

