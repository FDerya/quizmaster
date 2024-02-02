package controller;
// Manages the signing in and out of courses for a student, updating the UI and database accordingly

import database.mysql.CourseDAO;
import database.mysql.ParticipationDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import model.Course;
import model.Participation;
import model.User;
import view.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StudentSignInOutController {
    @FXML
    private ListView<Course> signedOutCourseList;
    @FXML
    private ListView<Course> signedInCourseList;
    @FXML
    private Label warningLabel;
    private List<Course> allCourses;
    private List<Course> enrolledCourses;
    private List<Course> notEnrolledCourses;
    private int idUser;

    // Initializes the controller with the provided user ID
    public void setup(int idUser) {
        this.idUser = idUser;
        enrolledCourses = new ArrayList<>();
        notEnrolledCourses = new ArrayList<>();
        initializeListViews(this.idUser);
    }

    // Populates the list views with courses, filtering out enrolled courses and those already in the signed out list
    public void initializeListViews(int userId) {
        CourseDAO courseDAO = new CourseDAO(Main.getDBaccess());
        allCourses = courseDAO.getAll();
        enrolledCourses = getEnrolledCourses(userId);
        notEnrolledCourses = allCourses.stream()
                .filter(course -> !enrolledCourses.stream().anyMatch(enrolledCourse -> enrolledCourse.getIdCourse()
                        == course.getIdCourse()))
                .collect(Collectors.toList());
        notEnrolledCourses = notEnrolledCourses.stream()
                .filter(course -> !signedOutCourseList.getItems().contains(course))
                .collect(Collectors.toList());
        initializeSignedInCoursesListView(notEnrolledCourses);
        initializeSignedOutCoursesListView(enrolledCourses);
    }

    // Initializes the signed in courses list view with the filtered courses to display
    private void initializeSignedInCoursesListView(List<Course> notEnrolledCourses) {
        List<Course> coursesToDisplay = notEnrolledCourses.stream()
                .filter(course -> !enrolledCourses.contains(course))
                .sorted(Comparator.comparing(Course::getNameCourse))
                .collect(Collectors.toList());

        signedInCourseList.getItems().setAll(coursesToDisplay);
        signedInCourseList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        signedInCourseList.refresh();
    }

    // Initializes the signed out courses list view with enrolled courses
    private void initializeSignedOutCoursesListView(List<Course> enrolledCourses) {
        enrolledCourses.sort(Comparator.comparing(Course::getNameCourse));
        signedOutCourseList.getItems().setAll(enrolledCourses);
        signedOutCourseList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        signedOutCourseList.refresh();
    }


    // Retrieves the list of courses in which the student is enrolled
    private List<Course> getEnrolledCourses(int userId) {
        ParticipationDAO participationDAO = new ParticipationDAO(Main.getDBaccess());
        List<Participation> participations = participationDAO.getParticipationByIdUser(userId);

        List<Course> enrolledCourses = new ArrayList<>();
        for (Participation participation : participations) {
            Course course = participation.getCourse();
            if (course != null) {
                enrolledCourses.add(course);
            }
        }
        return enrolledCourses;
    }

    // Handles the action to return to the welcome scene
    @FXML
    private void doMenu() {
        try {
            Main.getSceneManager().showWelcomeScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handles the action when the student signs in for selected courses, updating views and database accordingly
    public void doSignIn() {
        List<Course> selectedCourses = new ArrayList<>(signedInCourseList.getSelectionModel().getSelectedItems());

        if (selectedCourses.isEmpty()) {
            setWarningLabel("Selecteer een cursus");
            return;
        }

        for (Course selectedCourse : selectedCourses) {
            if (!enrolledCourses.contains(selectedCourse)) {
                enrolledCourses.add(selectedCourse);
                updateListViews();
                updateEnrolledCoursesInDatabase(selectedCourse, true);
            }
        }
    }

    // Handles the action when the student signs out from selected courses, updating views and database accordingly
    public void doSignOut() {
        List<Course> selectedCourses = new ArrayList<>(signedOutCourseList.getSelectionModel().getSelectedItems());
        ParticipationDAO participationDAO = new ParticipationDAO(Main.getDBaccess());
        if (selectedCourses.isEmpty()) {
            setWarningLabel("Selecteer een cursus");
            return;
        }
        for (Course course : selectedCourses) {
            if (enrolledCourses.contains(course)) {
                enrolledCourses.remove(course);
                participationDAO.deleteParticipation(idUser, course.getIdCourse());
            }
        }
        updateListViews();
    }

    // Sets warninglabel
    private void setWarningLabel(String message) {
        warningLabel.setText(message);
        warningLabel.setVisible(true);
    }


    // Updates the list views after changes in enrolled courses, refreshing the displayed courses
    private void updateListViews() {
        signedInCourseList.getItems().clear();
        initializeSignedInCoursesListView(notEnrolledCourses);

        signedOutCourseList.getItems().clear();
        initializeSignedOutCoursesListView(enrolledCourses);
    }

    // Adds new participations to the database
    private void addParticipationsToDatabase(ParticipationDAO participationDAO, List<Participation> participationsToAdd) {
        for (Participation newParticipation : participationsToAdd) {
            participationDAO.storeOneWhereGroupIsNull(newParticipation.getUser().getIdUser(), newParticipation.getCourse().getIdCourse());
        }
    }


    // Updates the database with newly enrolled courses if any
    private void updateEnrolledCoursesInDatabase(Course selectedCourse, boolean isEnrolled) {
        ParticipationDAO participationDAO = new ParticipationDAO(Main.getDBaccess());

        if (isEnrolled) {
            Participation newParticipation = new Participation(User.getCurrentUser(), selectedCourse, null);
            addParticipationsToDatabase(participationDAO, Collections.singletonList(newParticipation));
        }
    }
}




