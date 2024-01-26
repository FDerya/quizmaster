package controller;

import database.mysql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import model.*;
import view.Main;

import java.util.List;

public class SelectQuizForStudentController extends WarningAlertController{
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
    int course = oneQuiz.getCourse().getIdCourse();
    List<Quiz> quizzen = quizDAO.getAllByCourseId(course);
    quizList.getItems().addAll(quizzen);
}

    public void doMenu(ActionEvent event) {
        Main.getSceneManager().showWelcomeScene();
    }

    public void doQuiz(ActionEvent event) {
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        if(quiz == null){
            setEmptyChoice("quiz",true);
        }else{
            Main.getSceneManager().showFillOutQuiz(quiz);
        }
    }
}
