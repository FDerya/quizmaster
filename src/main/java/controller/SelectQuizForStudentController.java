package controller;

import database.mysql.*;
import javacouchdb.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import model.*;
import view.Main;

import java.util.ArrayList;
import java.util.List;

public class SelectQuizForStudentController extends WarningAlertController {
    private final UserDAO USERDAO;
    private final QuizDAO QUIZDAO;
    private final ParticipationDAO PARTICIPATIONDAO;
    private QuizResultCouchDBDAO quizResultCouchDBDAO = new QuizResultCouchDBDAO(Main.getCouchDBaccess());

    private QuizResult latestQuizResult;
    private List<QuizResult> finishedQuizzes = new ArrayList<>();
    private List<String> datum = new ArrayList<>();
    private List<String> score = new ArrayList<>();
    @FXML
    ListView<Quiz> quizList;
    @FXML
    Button mainScreenButton;

    public SelectQuizForStudentController() {
        this.QUIZDAO = new QuizDAO(Main.getDBaccess());
        this.USERDAO = new UserDAO(Main.getDBaccess());
        this.PARTICIPATIONDAO = new ParticipationDAO(Main.getDBaccess());
    }

    public void setup() {
        mainScreenButton.setText(Main.getMainScreenButtonText());
        User user = User.getCurrentUser();
        List<Participation> participation = PARTICIPATIONDAO.getParticipationByIdUserGroupNotNull(user.getIdUser());

        if (!participation.isEmpty()) {
            List<Quiz> quizzen = new ArrayList<>();
            for (Participation course : participation) {
                quizzen.addAll(QUIZDAO.getAllByCourseId(course.getCourse().getIdCourse()));
            }
            makeColumn();
            quizList.getItems().addAll(quizzen);
        }
    }

    private void makeColumn() {
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
                    datum.setText(getCouchDBDatum(item.getNameQuiz()));
                    score.setText(getCouchDBScore(item.getNameQuiz()));
                }
                setGraphic(hBox);
            }
        });
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


    private String getCouchDBDatum(String nameQuiz) {
        finishedQuizzes = quizResultCouchDBDAO.getQuizResults(QUIZDAO.getOneByName(nameQuiz), User.getCurrentUser());
        if (!finishedQuizzes.isEmpty()) {
            QuizResult lastQuiz = finishedQuizzes.get(finishedQuizzes.size() - 1);
            String datum = lastQuiz.getLocalDate();
            return datum;
        }else return "";
    }

    private String getCouchDBScore(String nameQuiz) {
        finishedQuizzes = quizResultCouchDBDAO.getQuizResults(QUIZDAO.getOneByName(nameQuiz), User.getCurrentUser());
        if (!finishedQuizzes.isEmpty()) {
            QuizResult lastQuiz = finishedQuizzes.get(finishedQuizzes.size() - 1);
            String score = lastQuiz.getScore();
            return score;
        }else return "";
    }
}