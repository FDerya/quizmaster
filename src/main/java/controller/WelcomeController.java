package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import database.mysql.*;
import javacouchdb.QuizResultCouchDBDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import model.Group;
import model.Question;
import model.QuizResult;
import model.User;
import view.Main;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class WelcomeController {
    @FXML
    Label welcomeLabel;
    @FXML
    MenuButton taskMenuButton;
    @FXML
    Label savedLabel;
    private final QuizResultCouchDBDAO quizResultCouchDBDAO = new QuizResultCouchDBDAO(Main.getCouchDBaccess());
    List<JsonObject> listJsonObject = new ArrayList<>();
    List<QuizResult> listQuizResult = new ArrayList<>();
    final String path = "src/resources/";
    final String extension = ".txt";
    String textfile;
    String typeOfPrint;

    public void setup() {
        welcomeLabel.setText("Welkom " + User.getCurrentUser().getFirstName() + "\nJe bent ingelogd als " + User.getCurrentUser().getRole().toLowerCase());
        // Shows menu buttons by role
        String role = User.currentUser.getRole();
        switch (role) {
            case "Student":
                initializeMenuItemsStudent();
                break;
            case "Coördinator":
                initializeMenuItemsCoordinator();
                break;
            case "Administrator":
                initializeMenuItemsAdministrator();
                break;
            case "Functioneel Beheerder":
                initializeMenuItemsFunctionalManagement();
                break;
        }

        listJsonObject = quizResultCouchDBDAO.getAllDocuments();
        listQuizResult = fillQuizResultList();
    }

    public void doLogout() {
        // Changes current user to null, so no user is logged in anymore.
        User.setCurrentUser(null);
        Main.getSceneManager().showLoginScene();
    }

    public void initializeMenuItemsStudent() {
        MenuItem sMenuItem1 = new MenuItem("In- en uitschrijven");
        sMenuItem1.setOnAction(actionEvent -> Main.getSceneManager().showStudentSignInOutScene());
        MenuItem sMenuItem2 = new MenuItem("Quiz selecteren");
        sMenuItem2.setOnAction(actionEvent -> Main.getSceneManager().showSelectQuizForStudent());

        taskMenuButton.getItems().add(sMenuItem1);
        taskMenuButton.getItems().add(sMenuItem2);
    }

    public void initializeMenuItemsCoordinator() {
        MenuItem cMenuItem1 = new MenuItem("Coördinator dashboard");
        cMenuItem1.setOnAction(actionEvent -> Main.getSceneManager().showCoordinatorDashboard());
        MenuItem cMenuItem2 = new MenuItem("Quizbeheer");
        cMenuItem2.setOnAction(actionEvent -> Main.getSceneManager().showManageQuizScene());
        MenuItem cMenuItem3 = new MenuItem("Vragenbeheer");
        cMenuItem3.setOnAction(actionEvent -> Main.getSceneManager().showManageQuestionsScene());

        taskMenuButton.getItems().add(cMenuItem1);
        taskMenuButton.getItems().add(cMenuItem2);
        taskMenuButton.getItems().add(cMenuItem3);
    }

    public void initializeMenuItemsAdministrator() {
        MenuItem aMenuItem1 = new MenuItem("Cursusbeheer");
        aMenuItem1.setOnAction(actionEvent -> Main.getSceneManager().showManageCoursesScene());
        MenuItem aMenuItem2 = new MenuItem("Groepenbeheer");
        aMenuItem2.setOnAction(actionEvent -> Main.getSceneManager().showManageGroupsScene());
        MenuItem aMenuItem3 = new MenuItem("Studenten toewijzen aan groepen");
        aMenuItem3.setOnAction(actionEvent -> Main.getSceneManager().showAssignStudentsToGroupScene());
        MenuItem aMenuItem4 = new MenuItem("Exporteer quizresultaten");
        aMenuItem4.setOnAction(actionEvent -> doShowSaveTextFileAlert("quizresultaat"));
        MenuItem aMenuItem5 = new MenuItem("Exporteer groepen");
        aMenuItem5.setOnAction(actionEvent -> doShowSaveTextFileAlert("groepen"));
        MenuItem aMenuItem6 = new MenuItem("Exporteer question");
        aMenuItem6.setOnAction(actionEvent -> doShowSaveTextFileAlert("question"));

        taskMenuButton.getItems().add(aMenuItem1);
        taskMenuButton.getItems().add(aMenuItem2);
        taskMenuButton.getItems().add(aMenuItem3);
        taskMenuButton.getItems().add(aMenuItem4);
        taskMenuButton.getItems().add(aMenuItem5);
        taskMenuButton.getItems().add(aMenuItem6);
    }

    public void initializeMenuItemsFunctionalManagement() {
        MenuItem fMenuItem1 = new MenuItem("Gebruikersbeheer");
        fMenuItem1.setOnAction(actionEvent -> Main.getSceneManager().showManageUserScene());

        taskMenuButton.getItems().add(fMenuItem1);
    }

    // Fills a List<QuizResult> from the List<JsonObject>, by converting the JsonObject to a QuizResult.
    // Sorts by username.
    public List<QuizResult> fillQuizResultList() {
        Gson gson = new Gson();
        List<QuizResult> resultList = new ArrayList<>();
        for (JsonObject jsonObject : listJsonObject) {
            QuizResult quizResult = gson.fromJson(jsonObject, QuizResult.class);
            resultList.add(quizResult);
        }
        resultList.sort(Comparator.comparing(QuizResult::getUser));
        return resultList;
    }

    // Shows a pop-up where you can name the textfile to where the quizresults are exported
    public void doShowSaveTextFileAlert(String type) {
        typeOfPrint = type; // Saves the typeOfPrint in the global scope, to access it in another method later on
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exporteer " + type);
        alert.setHeaderText("Voer de gewenste bestandsnaam in");
        GridPane gridPane = new GridPane();
        TextField fileName = new TextField(type);
        gridPane.add(fileName, 0, 0);
        alert.getDialogPane().setContent(gridPane);
        ButtonType buttonCancel = new ButtonType("Annuleer");
        ButtonType buttonContinue = new ButtonType("Opslaan");
        alert.getButtonTypes().setAll(buttonCancel, buttonContinue);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonContinue) {
            showLabelToSave(fileName);
        }
    }

    // Saves the typeOfPrint into a given textfile. After you save, a savedLabel shows up on the screen and
    // disappears after 3 seconds.
    private void showLabelToSave(TextField fileName) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), actionEvent ->
                savedLabel.setVisible(false)));
        textfile = fileName.getText();
        createTextFile();
        savedLabel.setText("Bestand " + textfile + " is opgeslagen in de map: " + path);
        savedLabel.setVisible(true);
        timeline.play();
    }

    // Creates the textFile with the printToTextFile method.
    private void createTextFile() {
        try {
            printToTextFile(typeOfPrint);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("Het bestand kan niet worden aangemaakt.");
        }
    }

    // Initiates a printWriter, to write the typeOfPrint into a text file. Depending on the typeOfPrint,
    // chooses the correct way to print, via a switch statement.

    private void printToTextFile(String typeToPrint) throws FileNotFoundException {
        File file = new File(path + textfile + extension);
        PrintWriter printWriter = new PrintWriter(file);
        switch (typeToPrint) {
            case "quizresultaat":
                printQuizResults(printWriter);
                break;
            case "groepen":
                printGroups(printWriter);
                break;
            case "question":
                printQuestion(printWriter);
                break;
        }
        printWriter.close();
    }

    // Prints the quizresult. If a user has multiple quizresults, only shows the username once.
    private void printQuizResults(PrintWriter printWriter) {
        String lastUsername = null;
        for (QuizResult quizResult : listQuizResult) {
            String currentUsername = quizResult.getUser();
            if (!currentUsername.equals(lastUsername)) {
                printWriter.println("Naam student: " + quizResult.getUser());
                lastUsername = currentUsername;
            }
            printWriter.println("\tQuiz: " + quizResult.getQuiz());
            printWriter.println("\tDatum gemaakt: " + quizResult.getLocalDateTime());
            printWriter.println("\tResultaat: " + quizResult.getScore());
            printWriter.println("\tBehaald: " + quizResult.getResult());
            printWriter.println();
        }
    }

    private void printQuestion(PrintWriter printWriter) {
        QuestionDAO questionDAO = new QuestionDAO(Main.getDBaccess());

        List<Question> questionsList = questionDAO.getAll();
        for (Question question : questionsList) {
            printWriter.println("\tQuiz: " + question.getQuiz().getNameQuiz());
            printWriter.println("\tVragen: " + question.getQuestion());
            printWriter.println("\tJuist Antwoord: " + question.getAnswerRight());
            printWriter.println("\tOnjuist Antwoord 1: " + question.getAnswerWrong1());
            printWriter.println("\tOnjuist Antwoord 2: " + question.getAnswerWrong2());
            printWriter.println("\tOnjuist Antwoord 3: " + question.getAnswerWrong3());
            printWriter.println();
        }
    }


    // Prints group information along with associated students' details to a file
    private void printGroups(PrintWriter printWriter) {
        String fileName = path + textfile + extension;
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
            List<Group> groups = getSortedGroups();
            ParticipationDAO participationDAO = new ParticipationDAO(Main.getDBaccess());
            for (Group group : groups) {
                writer.write(writeGroupInfo(group));
                writer.write(writeStudentsInfo(participationDAO, group.getIdGroup()));
                writer.write("\n");
            }
        } catch (IOException | SQLException e) {
            System.err.println("Fout bij het schrijven naar het bestand: " + e.getMessage());
        }
    }

    // Returns specific group details as a string
    private static String writeGroupInfo(Group group) {
        return "Cursus: " + group.getCourse().getNameCourse() + "\n" +
                "Groep: " + group.getGroupName() + "\n" +
                "Aantal studenten: " + group.getAmountStudent() + "\n" +
                "Docent: " + group.getTeacher().getFullName() + "\n";
    }

    // Returns the full names of students associated with a particular group as a string
    private static String writeStudentsInfo(ParticipationDAO participationDAO, int groupId) throws SQLException {
        StringBuilder studentsInfo = new StringBuilder("\tStudenten:\n");
        List<String> studentFullNames = participationDAO.getStudentsFullNamesByGroupId(groupId);
        studentFullNames.sort(String::compareToIgnoreCase);
        for (String studentFullName : studentFullNames) {
            studentsInfo.append("\t\t- ").append(studentFullName).append("\n");
        }
        return studentsInfo.toString();
    }

    // Returns a sorted list of courses
    private List<Group> getSortedGroups() throws SQLException {
        UserDAO userDAO = new UserDAO(Main.getDBaccess());
        CourseDAO courseDAO = new CourseDAO(Main.getDBaccess(), userDAO);
        GroupDAO groupDAO = new GroupDAO(Main.getDBaccess(), userDAO, courseDAO);
        List<Group> groups = groupDAO.getAll();
        groups.sort(Comparator.comparing(group -> group.getCourse().getNameCourse()));
        return groups;
    }
}
