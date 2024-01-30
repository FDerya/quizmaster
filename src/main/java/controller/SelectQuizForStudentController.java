package controller;

import database.mysql.*;
import javacouchdb.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import model.*;
import view.Main;

import java.util.ArrayList;
import java.util.List;

public class SelectQuizForStudentController extends WarningAlertController {
    private final UserDAO userDAO;
    private final QuizDAO quizDAO;
    private final ParticipationDAO participationDAO;
    @FXML
    ListView<Quiz> quizList;

    public SelectQuizForStudentController() {
        this.quizDAO = new QuizDAO(Main.getDBaccess());
        this.userDAO = new UserDAO(Main.getDBaccess());
        this.participationDAO = new ParticipationDAO(Main.getDBaccess());
    }

    public void setup() {
        User user = User.getCurrentUser();
        List<Participation> participation = participationDAO.getParticipationByIdUserGroupNotNull(user.getIdUser());
        if (!participation.isEmpty()) {
            List<Quiz> quizzen = new ArrayList<>();
            for(Participation course: participation){
                quizzen.addAll(quizDAO.getAllByCourseId(course.getCourse().getIdCourse()));
            }
           // getCouchDBResult(quizzen.get(0), User.getCurrentUser());
            quizList.setCellFactory(param -> new ListCell<>() {
                @Override
                public void updateItem(Quiz item, boolean empty) {
                    super.updateItem(item, empty);
                    Label naam = new Label();
                    naam.setPrefWidth(200.0);
                    Label datum = new Label();
                    datum.setPrefWidth(100);
                    Label score = new Label();
                    HBox hBox = new HBox(naam, datum, score);
                    if (!(item == null || empty)) {
                        naam.setText(item.getNameQuiz());
                        datum.setText("Datum");
                        score.setText("Score");
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

   /* private QuizResult getCouchDBResult(Quiz quiz, User user) {
        QuizResultCouchDBDAO quizResultCouchDBDAO = new QuizResultCouchDBDAO(Main.getCouchDBaccess());
        QuizResult quizResult = quizResultCouchDBDAO.getSingleQuizResult(quiz, user);
        if (quizResult == null) {
            System.out.println("Fout");
        }return quizResult;
    }
    private String getCouchDBDatum(QuizResult quizResult) {
        String datum = quizResult.getLocalDate();
        if (quizResult == null) {
            System.out.println("Fout");
        }
        return datum;
    }
    private String getCouchDBScore(QuizResult quizResult) {
          String score = quizResult.getScore();
        if (quizResult == null) {
            System.out.println("Fout");
        }
        return score;
    }*/
}
