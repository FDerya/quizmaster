package controller;
// Bianca Duijvesteijn, studentnummer 500940421
// The controller initializes a screen with data from a group when it is given and sets
// allows the user to add or update groups. It retrieves course and teacher data,
// shows warnings for invalid input and has methods for saving and showing groups.

import database.mysql.CourseDAO;
import database.mysql.GroupDAO;
import database.mysql.UserDAO;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
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
    private static final int MAX_NAME_LENGTH = 45;
    private final ObservableList<Course> allCourses = FXCollections.observableArrayList();
    private final ObservableList<String> groupList = FXCollections.observableArrayList();
    public static List<Group> newGroups = new ArrayList<>();
    private Group selectedGroup;
    private int idGroup;
    @FXML
    Label titleLabel;
    @FXML
    Label courseLabel;
    @FXML
    Label nameGroupLabel;
    @FXML
    Label amountStudentLabel;
    @FXML
    Label teacherLabel;
    @FXML
    TextField nameGroupTextField;
    @FXML
    TextField amountStudentTextField;
    @FXML
    ComboBox<Course> courseComboBox;
    @FXML
    ComboBox<User> teacherComboBox;
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
        limitTextFieldLength(nameGroupTextField, MAX_NAME_LENGTH);
        amountStudentTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                amountStudentTextField.setText(oldValue);
            }
        });
        initializeTeacherComboBox();
        this.selectedGroup = group;
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

    // Limits the length of the input in a TextField to the specified length
    private void limitTextFieldLength(TextField textField, int maxLength) {
        StringConverter<Integer> converter = new IntegerStringConverter();
        TextFormatter<Integer> textFormatter = new TextFormatter<>(
                converter,
                null,
                change -> {
                    String newText = change.getControlNewText();
                    if (newText.length() <= maxLength) {
                        return change;
                    }
                    return null;
                }
        );
        textField.setTextFormatter(textFormatter);
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
        }
    }

    // Retrieves a list of teachers from the database
    public List<User> getTeachers() {
        try {
            return userDAO.getAll().stream()
                    .filter(user -> "Docent".equals(user.getRole()))
                    .sorted(Comparator.comparing(User::getSurname)
                            .thenComparing(User::getPrefix)
                            .thenComparing(User::getFirstName))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Custom ListCell implementation for displaying User objects in a ComboBox
    public class UserListCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setText(null);
            } else {
                setText(String.format("%s", user.getFullName()));
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
        try {
            groupDAO.storeOne(group);
            setWarningLabel("Groep '" + group.getGroupName() + "' opgeslagen", Color.BLACK);

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(this::doShowManageGroups);
            delay.play();
        } catch (Exception e) {
            e.printStackTrace();
            setWarningLabel("Fout bij opslaan groep", Color.RED);
        }
    }

    // Handles the updating of an existing group
    private void updateExistingGroup(Group group) {
        if (isGroupNameUnique(group.getGroupName(), selectedGroup)) {
            group.setIdGroup(idGroup);
            Group updatedGroup = groupDAO.updateOne(group);

            if (updatedGroup != null) {
                setWarningLabel("Groep '" + updatedGroup.getGroupName() + "' gewijzigd", Color.BLACK);

                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(this::doShowManageGroups);
                delay.play();
            } else {
                setWarningLabel("Groep kon niet worden gewijzigd", Color.RED);
            }
        }
    }

    // Navigates to the "Manage Groups" scene and closes the currently displayed alert
    @FXML
    public void doShowManageGroups(ActionEvent actionEvent) {
        Main.getSceneManager().showManageGroupsScene();
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

    // Creates and returns a new group after validating input and performing necessary operations
    private Group createGroup() {
        Group newGroup = validateAndBuildGroup();

        if (newGroup != null) {
            handleGroupNameValidation(newGroup);
            handleGroupSave(newGroup);
            return newGroup;
        }

        return null;
    }

    // Validates input and builds a new group object if input is correct
    private Group validateAndBuildGroup() {
        boolean correctInput = validateInput();

        if (correctInput) {
            Group newGroup = buildGroupObject();
            newGroup.setNew(true);
            return newGroup;
        }
        return null;
    }

    // Handles validation of the group name, displaying a warning if the name is not unique
    private void handleGroupNameValidation(Group newGroup) {
        if (selectedGroup != null && !newGroup.getGroupName().equals(selectedGroup.getGroupName()) &&
                !isGroupNameUnique(newGroup.getGroupName(), selectedGroup)) {
            setWarningLabel("De groep bestaat al. Kies een andere naam.", Color.RED);
        }
    }

    // Handles the save operation for the new group, updating the group list and displaying a success message
    private void handleGroupSave(Group newGroup) {
        updateGroupList(newGroup);
        resetLabelColors();

        if (!newGroups.contains(newGroup)) {
            newGroups.add(newGroup);
        }

        setWarningLabel("Groep '" + newGroup.getGroupName() + "' opgeslagen", Color.BLACK);
    }

    // Checks if the group name is unique, considering the edited group
    private boolean isGroupNameUnique(String groupName, Group editingGroup) {
        try {
            List<Group> allGroups = groupDAO.getAll();
            for (Group group : allGroups) {
                if (group.getGroupName().equals(groupName) && (editingGroup == null || group.getIdGroup() != editingGroup.getIdGroup())) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            setWarningLabel("Fout bij het controleren van de groepsnaam.", Color.RED);
            return false;
        }
        return true;
    }

    // Validates input fields, returns true if all fields are filled, false otherwise.
    private boolean validateInput() {
        Map<String, String> inputFields = new LinkedHashMap<>();
        inputFields.put("course", "Je hebt niet alles ingevuld");
        inputFields.put("nameGroup", "Je hebt niet alles ingevuld");
        inputFields.put("amountStudent", "Je hebt niet alles ingevuld");
        inputFields.put("teacher", "Je hebt niet alles ingevuld");

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
                break;
            }
        }
        return correctInput;
    }

    // Sets the warning label based on the provided map of input fields and their corresponding error messages.
    private void setWarningLabel(String message, Color color) {
        warningLabel.setText(message);
        warningLabel.setTextFill(color);
        warningLabel.setVisible(true);

        resetLabelColors();
        setLabelErrorColor(courseLabel, "course");
        setLabelErrorColor(nameGroupLabel, "nameGroup");
        setLabelErrorColor(amountStudentLabel, "amountStudent");
        setLabelErrorColor(teacherLabel, "teacher");
    }

    // validates input fields, updates labels with error messages, displays a red warning label
    // if errors are present, and resets label colors.
    private void setWarningLabel(Map<String, String> inputFields) {
        StringBuilder error = new StringBuilder();
        boolean hasError = false;

        for (Map.Entry<String, String> entry : inputFields.entrySet()) {
            String fieldValue = getInputValue(entry.getKey());
            if (fieldValue.isEmpty()) {
                error.append(entry.getValue()).append("\n");

                setLabelErrorColor(entry.getKey());
                hasError = true;
                break;
            }
        }
        if (hasError) {
            setWarningLabel(error.toString().trim(), Color.BLACK);
            return;
        }
        resetLabelColors();
    }

    // sets the text color of the specific label in the grid to red
    private void setLabelErrorColor(String fieldName) {
        resetLabelColors();

        switch (fieldName) {
            case "course":
                setLabelErrorColor(courseLabel, fieldName);
                break;
            case "nameGroup":
                setLabelErrorColor(nameGroupLabel, fieldName);
                break;
            case "amountStudent":
                setLabelErrorColor(amountStudentLabel, fieldName);
                break;
            case "teacher":
                setLabelErrorColor(teacherLabel, fieldName);
                break;
        }
    }

    // Set the text color of the labels in the grid to red
    private void setLabelErrorColor(Label label, String fieldName) {
        String fieldValue = getInputValue(fieldName);
        if (fieldValue.isEmpty()) {
            label.setTextFill(Color.RED);
        } else {
            resetLabelColor(label);
        }
    }

    // Resets the text color of the labels in the grid to the default color
    private void resetLabelColor(Label label) {
        label.setTextFill(Color.BLACK);
    }

    // Resets the text colors of labels
    private void resetLabelColors() {
        resetLabelColor(courseLabel);
        resetLabelColor(nameGroupLabel);
        resetLabelColor(amountStudentLabel);
        resetLabelColor(teacherLabel);
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
            case "teacher":
                User selectedTeacher = teacherComboBox.getSelectionModel().getSelectedItem();
                return String.valueOf(selectedTeacher != null ? selectedTeacher.getFullName() : "");
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
        if (groupList != null) {
            groupList.add(newGroup.getGroupName());
        }
    }
}