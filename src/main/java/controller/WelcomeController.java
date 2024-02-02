package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javacouchdb.QuizResultCouchDBDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import model.QuizResult;
import model.User;
import view.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class WelcomeController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private MenuButton taskMenuButton;
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
        // Verandert de currentUser naar null
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
        //MenuItem aMenuItem4 = new MenuItem("Exporteer groepen");
        //aMenuItem4.setOnAction(actionEvent -> doShowSaveAlert());
       // MenuItem aMenuItem5 = new MenuItem("Exporteer quizresultaten");
        //aMenuItem5.setOnAction(actionEvent -> doShowSaveAlert());


        taskMenuButton.getItems().add(aMenuItem1);
        taskMenuButton.getItems().add(aMenuItem2);
        taskMenuButton.getItems().add(aMenuItem3);
        taskMenuButton.getItems().add(aMenuItem4);
        //taskMenuButton.getItems().add(aMenuItem5);

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
        typeOfPrint = type;
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

    // Saves the quizresult into a given textfile. After you save, a savedLabel shows up on the screen and
    // disappears after 2 seconds.
    private void showLabelToSave(TextField fileName) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), actionEvent ->
                savedLabel.setVisible(false)));
        textfile = fileName.getText();
        createTextFile();
        savedLabel.setText("Bestand " + textfile + " is opgeslagen in de map: " + path);
        savedLabel.setVisible(true);
        timeline.play();
    }

    private void createTextFile() {
        try {
            printToTextFile(typeOfPrint);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("Het bestand kan niet worden aangemaakt.");
        }
    }

    // Initiates a printWriter, to write the quizresult into a text file.
    // If a user has multiple quizresults, only shows the username once.
    private void printToTextFile(String typeToPrint) throws FileNotFoundException {
        File file = new File(path + textfile + extension);
        PrintWriter printWriter = new PrintWriter(file);
        switch (typeToPrint) {
            case "quizresultaat":
                printQuizResults(printWriter);
                break;
        }
        printWriter.close();
    }

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
}
