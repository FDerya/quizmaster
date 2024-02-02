package controller;

import database.mysql.CourseDAO;
import database.mysql.GroupDAO;
import database.mysql.ParticipationDAO;
import database.mysql.UserDAO;
import javacouchdb.QuizResultCouchDBDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.*;
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
    QuizResultCouchDBDAO quizResultCouchDBDAO;
    final String noCourseNoGroupMessage = "Kies eerst een cursus en groep";
    final String noGroupMessage = "Kies eerst een groep";
    final String noUserSelectedMessage = "Selecteer eerst een of meer gebruikers";

    // Controller
    public AssignStudentsToGroupController() {
        this.participationDAO = new ParticipationDAO(Main.getDBaccess());
        this.courseDAO = new CourseDAO(Main.getDBaccess());
        this.userDAO = new UserDAO(Main.getDBaccess());
        this.groupDAO = new GroupDAO(Main.getDBaccess(), userDAO, courseDAO);
        this.quizResultCouchDBDAO = new QuizResultCouchDBDAO(Main.getCouchDBaccess());
    }

    // Fills the comboboxes and userlists
    public void setup() {
        fillCourseComboBox();
        fillStudentList();
        fillGroupComboBox();
        fillStudentInGroupList();
    }

    private void fillCourseComboBox() {
        List<Course> allCourses = courseDAO.getAll();
        ObservableList<Course> courseComboBoxList = FXCollections.observableArrayList(allCourses);
        courseComboBox.setItems(courseComboBoxList);
    }

    private void fillStudentList() {
        courseComboBox.getSelectionModel().selectedItemProperty()
                .addListener(((observableValue, oldCourse, newCourse) -> refreshStudentList(newCourse)));
    }

    // Refreshes the studentList if when clicking the doAssign or doRemove button.
    private void refreshStudentList(Course course) {
        studentList.getItems().clear();
        List<Participation> participationPerCourse = participationDAO.getParticipationPerCourse(course.getIdCourse());
        fillListView(participationPerCourse, studentList);
    }

    public void fillStudentInGroupList() {
        groupComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldGroup, newGroup) -> refreshStudentInGroupList(newGroup));
    }

    // Refreshes the studentInGroupList if when clicking the doAssign or doRemove button.
    private void refreshStudentInGroupList(Group group) {
        if (group != null) {
            warningLabel.setVisible(false);
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

    public void fillGroupComboBox() {
        courseComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldCourse, newCourse) -> {
            groupComboBox.getItems().clear();
            setGroupComboBoxPromptText();
            List<Group> groupsPerCourse = groupDAO.getGroupsByIdCourse(newCourse.getIdCourse());
            groupComboBox.setItems(FXCollections.observableList(groupsPerCourse));
            warningLabel.setText("Voor deze cursus zijn er nog geen groepen aangemaakt.");
            warningLabel.setVisible(groupsPerCourse.isEmpty());
        }));
    }

    // Assigns students to a group and handles errors when course, group and/or users are not selected.
    public void doAssign() {
        ObservableList<User> selectedUsers = studentList.getSelectionModel().getSelectedItems();
        Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        Group selectedGroup = groupComboBox.getSelectionModel().getSelectedItem();
        if (selectedGroup == null && selectedCourse == null) {
            setWarningLabel(noCourseNoGroupMessage);
        } else if (selectedGroup == null) {
            setWarningLabel(noGroupMessage);
        } else if (selectedUsers.isEmpty()) {
            setWarningLabel(noUserSelectedMessage);
        } else {
            assignStudentToGroup(selectedUsers, selectedGroup, selectedCourse);
        }
    }

    // Assigns students when there are no errors
    private void assignStudentToGroup(ObservableList<User> selectedUsers, Group selectedGroup, Course selectedCourse) {
        warningLabel.setVisible(false);
        for (User user : selectedUsers) {
            participationDAO.updateGroup(selectedGroup, selectedCourse, user);
        }
        refreshStudentList(selectedCourse);
        refreshStudentInGroupList(selectedGroup);
    }

    // Removes students from a group and handles errors when course, group and/or users are not selected.
    public void doRemove() {
        Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        Group selectedGroup = groupComboBox.getSelectionModel().getSelectedItem();
        ObservableList<User> selectedUsers = studentsInGroupList.getSelectionModel().getSelectedItems();
        if (selectedCourse == null && selectedGroup == null) {
            setWarningLabel(noCourseNoGroupMessage);
        } else if (selectedGroup == null) {
            setWarningLabel(noGroupMessage);
        } else if (selectedUsers.isEmpty()) {
            setWarningLabel(noUserSelectedMessage);
        } else {
            removeStudentFromGroup(selectedCourse, selectedUsers, selectedGroup);
        }
    }
    


    // Removes students from a group when there are no errors
    private void removeStudentFromGroup(Course selectedCourse, ObservableList<User> selectedUsers, Group selectedGroup) {
        warningLabel.setVisible(false);
        for (User user : selectedUsers) {
            participationDAO.updateGroupToNull(selectedCourse, user);
        }
        refreshStudentList(selectedCourse);
        refreshStudentInGroupList(selectedGroup);
    }

    // Sets text for warning label in a given situation and shows the warning label.
    private void setWarningLabel(String warningLabelMessage) {
        warningLabel.setText(warningLabelMessage);
        warningLabel.setVisible(true);
    }

    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }

    // Sets the prompt text for groupComboBox if there are no groups selected or assigned to a course
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

