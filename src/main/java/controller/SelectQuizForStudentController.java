package controller;

import database.mysql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import model.*;
import view.Main;

import java.util.List;

public class SelectQuizForStudentController extends WarningAlertController {
    private UserDAO userDAO;
    private QuizDAO quizDAO;
    private ParticipationDAO participationDAO;
    @FXML
    ListView<Quiz> quizList;

    public SelectQuizForStudentController() {
        this.quizDAO = new QuizDAO(Main.getDBaccess());
        this.userDAO = new UserDAO(Main.getDBaccess());
        this.participationDAO = new ParticipationDAO(Main.getDBaccess());
    }

    public void setup() {
        User user = User.getCurrentUser();
        Participation oneQuiz = participationDAO.getOneById(user.getIdUser());
        if (oneQuiz != null && oneQuiz.getGroup() != null) {
            int course = oneQuiz.getCourse().getIdCourse();
            List<Quiz> quizzen = quizDAO.getAllByCourseId(course);
            quizList.setCellFactory(param -> new ListCell<>() {
                @Override
                public void updateItem(Quiz item, boolean empty) {
                    super.updateItem(item, empty);
                    Label naam = new Label();
                    naam.setPrefWidth(200.0);
                    Label aantal = new Label();
                    HBox hBox = new HBox(naam, aantal);
                    if (!(item == null || empty)) {
                        naam.setText(item.getNameQuiz());
                        aantal.setText("datum + cijfer");
                    }
                    setGraphic(hBox);
                }
            });
            quizList.getItems().addAll(quizzen);
        }
    }

    public void doMenu(ActionEvent event) {
        Main.getSceneManager().showWelcomeScene();
    }

    public void doQuiz(ActionEvent event) {
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        if (quiz == null) {
            setEmptyChoice("quiz", true);
        } else {
            Main.getSceneManager().showFillOutQuiz(quiz);
        }
    }
}
