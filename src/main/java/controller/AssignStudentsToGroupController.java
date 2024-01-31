package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import database.mysql.CourseDAO;
import database.mysql.GroupDAO;
import database.mysql.ParticipationDAO;
import database.mysql.UserDAO;
import javacouchdb.QuizResultCouchDBDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import model.*;
import view.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
    @FXML
    Label savedLabel;
    ParticipationDAO participationDAO;
    CourseDAO courseDAO;
    GroupDAO groupDAO;
    UserDAO userDAO;
    QuizResultCouchDBDAO quizResultCouchDBDAO;
    final String noCourseNoGroupMessage = "Kies eerst een cursus en groep";
    final String noGroupMessage = "Kies eerst een groep";
    final String noUserSelectedMessage = "Selecteer eerst een of meer gebruikers";
    final String path = "src/resources/";
    String textfile = "quizresults";
    final String extension = ".txt";
    File file = new File(path+textfile+extension);
    Gson gson = new Gson();
    List<JsonObject> listJsonObject = new ArrayList<>();
    List<QuizResult> listQuizResult = new ArrayList<>();
    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), actionEvent ->
            savedLabel.setVisible(false)));

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
        listJsonObject = quizResultCouchDBDAO.getAllDocuments();
        listQuizResult = fillQuizResultList();
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
    
    // Fills a List<QuizResult> from the List<JsonObject>, by converting the JsonObject to a QuizResult.
    // Sorts by username.
    public List<QuizResult> fillQuizResultList() {
        List<QuizResult> resultList = new ArrayList<>();
        for (JsonObject jsonObject : listJsonObject) {
            QuizResult quizResult = gson.fromJson(jsonObject, QuizResult.class);
            resultList.add(quizResult);
        }
        resultList.sort(Comparator.comparing(QuizResult::getUser));
        return resultList;
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
    
    // Shows a pop-up where you can name the textfile to where the quizresults are exported
    public void doShowSaveAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exporteren quizresultaten");
        alert.setHeaderText("Voer de gewenste bestandsnaam in");
        GridPane gridPane = new GridPane();
        TextField fileName = new TextField("quizresults");
        gridPane.add(fileName, 0,0);
        alert.getDialogPane().setContent(gridPane);
        ButtonType buttonCancel = new ButtonType("Annuleer");
        ButtonType buttonContinue = new ButtonType("Opslaan");
        alert.getButtonTypes().setAll(buttonCancel, buttonContinue);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonContinue) {
            saveQuizResult(fileName);
        }
    }

    // Saves the quizresult into a given textfile. After you save, a savedLabel shows up on the screen and
    // disappears after 2 seconds.
    private void saveQuizResult(TextField fileName) {
        textfile = fileName.getText();
        createQuizResultTextFile();
        savedLabel.setText("Bestand " + textfile + " is opgeslagen in de map: " + path);
        savedLabel.setVisible(true);
        timeline.play();
    }

    private void createQuizResultTextFile() {
        try {
            printQuizResults();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("Het bestand kan niet worden aangemaakt.");
        }
    }

    // Initiates a printWriter, to write the quizresult into a text file.
    // If a user has multiple quizresults, only shows the username once.
    private void printQuizResults() throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(file);
        String lastUsername = null;
        for (QuizResult quizResult : listQuizResult) {
            String currentUsername = quizResult.getUser();
            if (!currentUsername.equals(lastUsername)) {
                printWriter.println("Naam student: " + quizResult.getUser());
                lastUsername = currentUsername;
            }
            printWriter.println("Quiz: " + quizResult.getQuiz());
            printWriter.println("Datum gemaakt: " + quizResult.getLocalDate());
            printWriter.println("Behaald: " + (quizResult.getScore().equals("behaald") ? "ja" : "nee"));
            printWriter.println();
        }
        printWriter.close();
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

