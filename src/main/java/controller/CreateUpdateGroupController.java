package controller;
// Bianca Duijvesteijn, studentnummer 500940421
// The controller initializes a screen with data from a group when it is given and sets
// allows the user to add or update groups. It retrieves course and teacher data,
// shows warnings for invalid input and has methods for saving and showing groups.

import database.mysql.CourseDAO;
import database.mysql.GroupDAO;
import database.mysql.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Course;
import model.Group;
import model.User;
import view.Main;

import java.util.*;
import java.util.stream.Collectors;

public class CreateUpdateGroupController {
    private final UserDAO userDAO;
    private final GroupDAO groupDAO;
    private final CourseDAO courseDAO;
    private int idGroup;
    private Alert alert;
    private final ObservableList<Course> allCourses = FXCollections.observableArrayList();
    @FXML
    private ListView<String> groupList;
    @FXML
    Label titleLabel;
    @FXML
    TextField nameGroupTextField;
    @FXML
    TextField amountStudentTextField;
    @FXML
    ComboBox<Course> courseComboBox;
    @FXML
    private ComboBox<User> teacherComboBox;
    @FXML
    Label warningLabel;

    // Initializes the CreateUpdateGroupController with the provided group.
    public CreateUpdateGroupController() {
        this.groupDAO = new GroupDAO(Main.getDBaccess(), new UserDAO(Main.getDBaccess()), new CourseDAO(Main.getDBaccess()));
        this.courseDAO = new CourseDAO(Main.getDBaccess());
        this.userDAO = new UserDAO(Main.getDBaccess());
    }

    // Sets up the controller with the specified group, initializing UI elements and populating fields.
    public void setup(Group group) {
        idGroup = (group != null) ? group.getIdGroup() : 0;
        setLabelsAndTitle(group);
        initializeCourseComboBox();
        initializeTeacherComboBox();
        populateFields(group);
    }

    // Sets labels and title based on the provided group.
    private void setLabelsAndTitle(Group group) {
        titleLabel.setText((group != null) ? "Wijzig groep" : "Nieuwe groep");
    }

    // Initializes the course combo box with a sorted list of all available courses.
    private void initializeCourseComboBox() {
        List<Course> courses = courseDAO.getAll();
        allCourses.setAll(courses);
        allCourses.sort(Comparator.comparing(Course::getNameCourse));
        courseComboBox.setItems(allCourses);
    }

    // Initializes the teacher combo box with a list of all available teachers.
    private void initializeTeacherComboBox() {
        List<User> teachers = getTeachers();
        teacherComboBox.getItems().setAll(teachers);
        teacherComboBox.setCellFactory(param -> new UserListCell());
    }

    // Populates form fields with information from the provided group.
    private void populateFields(Group group) {
        if (group != null) {
            courseComboBox.getSelectionModel().select(group.getCourse());
            nameGroupTextField.setText(group.getGroupName());
            amountStudentTextField.setText(String.valueOf(group.getAmountStudent()));

            User groupTeacher = group.getTeacher();
            if (groupTeacher != null) {
                teacherComboBox.getSelectionModel().select(groupTeacher);
            } else {
                teacherComboBox.getSelectionModel().selectFirst();
            }
        } else {
            clearFormFields();
        }
    }

    // Clears all form fields.
    private void clearFormFields() {
        courseComboBox.getSelectionModel().clearSelection();
        nameGroupTextField.clear();
        amountStudentTextField.clear();
        teacherComboBox.getSelectionModel().clearSelection();
    }

    // Retrieves a list of teachers from the database
    public List<User> getTeachers() {
        return userDAO.getAll().stream()
                .filter(user -> "Docent".equals(user.getRole()))
                .sorted(Comparator.comparing(User::getSurname)
                        .thenComparing(User::getPrefix)
                        .thenComparing(User::getFirstName))
                .collect(Collectors.toList());
    }

    // Custom ListCell implementation for displaying User objects in a ComboBox
    public class UserListCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setText(null);
            } else {
                setText(String.format("%s %s, %s", user.getPrefix(), user.getSurname(), user.getFirstName()));
            }
        }
    }

    // Handles the "Save Group" button click event, stores or updates the group in the database
    @FXML
    public void doSaveGroup(ActionEvent actionEvent) {
        Group group = createGroup();
        if (group != null) {
            if (titleLabel.getText().equals("Nieuwe groep")) {
                saveNewGroup(group);
            } else {
                updateExistingGroup(group);
            }
        }
    }

    // Handles the saving of a new group, storing it in the database and displaying a confirmation alert
    private void saveNewGroup(Group group) {
        groupDAO.storeOne(group);
        showAlert(Alert.AlertType.INFORMATION, "Groep opgeslagen");
    }

    // Handles the updating of an existing group, modifying it in the database and displaying appropriate alerts
    private void updateExistingGroup(Group group) {
        group.setIdGroup(idGroup);
        Group updatedGroup = groupDAO.updateOne(group);

        if (updatedGroup != null) {
            showAlert(Alert.AlertType.INFORMATION, "Groep gewijzigd");

        } else {
            showAlert(Alert.AlertType.ERROR, "Groep kon niet worden gewijzigd");
        }
    }

    // Displays an alert with the specified type and content text
    private void showAlert(Alert.AlertType alertType, String contentText) {
        alert = new Alert(alertType);
        alert.setContentText(contentText);
        alert.show();
    }

    // Navigates to the "Manage Groups" scene and closes the currently displayed alert
    @FXML
    public void doShowManageGroups(ActionEvent actionEvent) {
        Main.getSceneManager().showManageGroupsScene();
        closeAlert();
    }

    // Closes the currently displayed alert
    private void closeAlert() {
        if (alert.isShowing()) {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.close();
        }
    }

    // Handles the "Menu" button click event, navigating back to the welcome scene
    @FXML
    private void doShowMenu(ActionEvent actionEvent) {
        try {
            Main.getSceneManager().showWelcomeScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handles the "Create Group" button click event, creating a new group and adding it to the list
    public Group createGroup() {
        boolean correctInput = validateInput();

        if (correctInput) {
            Group newGroup = buildGroupObject();
            updateGroupList(newGroup);

            return newGroup;
        } else {
            return null;
        }
    }

    // Validates input fields, returns true if all fields are filled, false otherwise.
    private boolean validateInput() {
        Map<String, String> inputFields = Map.of(
                "course", "Er moet een cursus worden geselecteerd.",
                "nameGroup", "U moet een groepsnaam invoeren.",
                "amountStudent", "U dient een maximaal aantal studenten in te voeren.",
                "selectedTeacher", "Er moet een docent worden geselecteerd."
        );
        boolean correctInput = validateFields(inputFields);
        if (!correctInput) {
            setWarningLabel(inputFields);
        }
        return correctInput;
    }

    // Validates input fields based on the provided map, returns true if all fields are filled, false otherwise.
    private boolean validateFields(Map<String, String> inputFields) {
        boolean correctInput = true;

        for (Map.Entry<String, String> entry : inputFields.entrySet()) {
            String fieldValue = getInputValue(entry.getKey());
            if (fieldValue.isEmpty()) {
                correctInput = false;
                break;  // Stop the loop if an empty field is found
            }
        }
        return correctInput;
    }

    // Sets the warning label based on the provided map of input fields and their corresponding error messages.
    private void setWarningLabel(Map<String, String> inputFields) {
        StringBuilder error = new StringBuilder();

        for (Map.Entry<String, String> entry : inputFields.entrySet()) {
            String fieldValue = getInputValue(entry.getKey());
            if (fieldValue.isEmpty()) {
                error.append(entry.getValue()).append("\n"); // Add a newline for each error
            }
        }
        warningLabel.setText(error.toString().trim()); // Trim to remove the trailing newline
        warningLabel.setVisible(true);
    }

    // Retrieves the value of a specific input field based on the field name.
    private String getInputValue(String fieldName) {
        switch (fieldName) {
            case "course":
                Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
                return String.valueOf(selectedCourse != null ? selectedCourse.getNameCourse() : "");
            case "nameGroup":
                return nameGroupTextField.getText();
            case "amountStudent":
                return amountStudentTextField.getText();
            case "selectedTeacher":
                User selectedTeacher = teacherComboBox.getSelectionModel().getSelectedItem();
                return (selectedTeacher != null) ? selectedTeacher.toString() : "";
            default:
                return "";
        }
    }

    // Builds a Group object based on the selected input values.
    private Group buildGroupObject() {
        Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        String nameGroup = nameGroupTextField.getText();
        String amountStudent = amountStudentTextField.getText();
        User selectedTeacher = teacherComboBox.getSelectionModel().getSelectedItem();

        return new Group(0, selectedCourse, nameGroup, Integer.parseInt(amountStudent), selectedTeacher);
    }

    // Updates the group list with the name of the newly added group.
    private void updateGroupList(Group newGroup) {
        if (groupList != null && groupList.getItems() != null) {
            groupList.getItems().add(newGroup.getGroupName());
        }
    }
}