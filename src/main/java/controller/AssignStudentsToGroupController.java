package controller;

import database.mysql.CourseDAO;
import database.mysql.GroupDAO;
import database.mysql.ParticipationDAO;
import database.mysql.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Course;
import model.Group;
import model.Participation;
import model.User;
import view.Main;

import java.util.ArrayList;
import java.util.Comparator;
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
    @FXML
    Label warningLabel;
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
        courseComboBox.getSelectionModel().selectedItemProperty()
                .addListener(((observableValue, oldCourse, newCourse) -> refreshStudentList(newCourse)));
    }

    private void refreshStudentList(Course course) {
        studentList.getItems().clear();
        List<Participation> participationPerCourse = participationDAO.getParticipationPerCourse(course.getIdCourse());
        fillListView(participationPerCourse, studentList);
    }

    public void fillStudentInGroupList() {
        groupComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldGroup, newGroup) -> refreshStudentInGroupList(newGroup));
    }

    private void refreshStudentInGroupList(Group group) {
        if (group != null) {
            Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
            studentsInGroupList.getItems().clear();
            List<Participation> participationPerGroup = participationDAO.getParticipationInGroup(selectedCourse, group);
            fillListView(participationPerGroup, studentsInGroupList);
        } else {
            studentsInGroupList.getItems().clear();
        }
    }

    public void fillListView(List<Participation> participations, ListView<User> students) {
        List<User> users = new ArrayList<>();
        for (Participation participation : participations) {
            users.add(participation.getUser());
            users.sort(Comparator.comparing(User::getSurname));
        }
        students.getItems().addAll(FXCollections.observableArrayList(users));
        students.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void getGroupsPerCourse() {
        courseComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldCourse, newCourse) -> {
            groupComboBox.getItems().clear();
            setGroupComboBoxPromptText();
            List<Group> groupsPerCourse = groupDAO.getGroupsByIdCourse(newCourse.getIdCourse());
            groupComboBox.setItems(FXCollections.observableList(groupsPerCourse));
            warningLabel.setVisible(groupsPerCourse.isEmpty());
        }));
    }

    public void doAssign() {
        Group selectedGroup = groupComboBox.getSelectionModel().getSelectedItem();
        Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        ObservableList<User> selectedUsers = studentList.getSelectionModel().getSelectedItems();
        for (User user : selectedUsers) {
            participationDAO.updateGroup(selectedGroup, selectedCourse, user);
        }
        refreshStudentList(selectedCourse);
        refreshStudentInGroupList(selectedGroup);
    }

    public void doRemove() {
        Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        Group selectedGroup = groupComboBox.getSelectionModel().getSelectedItem();
        ObservableList<User> selectedUsers = studentsInGroupList.getSelectionModel().getSelectedItems();
        for (User user : selectedUsers) {
            participationDAO.updateGroupToNull(selectedCourse, user);
        }
        refreshStudentList(selectedCourse);
        refreshStudentInGroupList(selectedGroup);
    }

    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }

    private void setGroupComboBoxPromptText() {
        groupComboBox.setPromptText("Groep");
        groupComboBox.setButtonCell(new ListCell<>() {
            protected void updateItem(Group group, boolean empty) {
                super.updateItem(group, empty);
                if (empty || group == null){
                    setText("Groep");
                } else {
                    setText(group.getGroupName());
                }
            }
        });
    }
}

