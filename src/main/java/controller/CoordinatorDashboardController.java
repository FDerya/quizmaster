package controller;

import database.mysql.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import model.*;
import view.Main;

import java.util.List;

public class CoordinatorDashboardController extends WarningAlertController {

    @FXML
    private ListView<Course> courseList;
    @FXML
    private ListView<Quiz> quizList;
    @FXML
    private ListView<String> questionList;
    @FXML
    private Label waarschuwingsLabel;
    private final DBAccess dbAccess;

    private final QuestionDAO questionDAO;
    private final CourseDAO courseDAO;
    private final UserDAO userDAO;
    private final QuizDAO quizDAO;

    public CoordinatorDashboardController() {
        this.userDAO = new UserDAO(Main.getDBaccess());
        this.dbAccess = Main.getDBaccess();
        this.courseDAO = new CourseDAO(dbAccess, userDAO);
        this.quizDAO = new QuizDAO(Main.getDBaccess());
        this.questionDAO = new QuestionDAO(Main.getDBaccess());
    }

    public void setup() {
        User currentUser = User.getCurrentUser();
        List<Course> courseUserList = courseDAO.getAllByIdUser(currentUser.getIdUser());
        courseList.setItems(FXCollections.observableList(courseUserList));
        courseList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldCourse, newCourse) -> {
                    List<Quiz> quizzen = quizDAO.getAllByCourseId(newCourse.getIdCourse());
                    quizList.setItems(FXCollections.observableList(quizzen));
                });

        quizList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldQuiz, newQuiz) -> {
                    displayQuestionsForQuiz(newQuiz);
                });


        // Vragen laden voor de initieel geselecteerde test
        Quiz initialSelectedQuiz = quizList.getSelectionModel().getSelectedItem();
        if (initialSelectedQuiz != null) {
            displayQuestionsForQuiz(initialSelectedQuiz);
        }

        quizList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldQuiz, newQuiz) ->
                        displayQuestionsForQuiz(newQuiz));


        questionList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private void displayQuizForCourse(Course initialSelectedCourse) {
        // Verwijder bestaande vragen
        questionList.getItems().clear();
        quizList.getItems().clear();

        // Vragen ophalen en tonen voor de geselecteerde test
        if (initialSelectedCourse != null) {
            List<Quiz> quiz = questionDAO.getQuizForCourse(initialSelectedCourse);
            quizList.getItems().addAll(quiz);
        }
    }

    private void displayQuestionsForQuiz(Quiz quiz) {
        // Verwijder bestaande vragen
        questionList.getItems().clear();

        // Vragen ophalen en tonen voor de geselecteerde test
        if (quiz != null) {
            List<String> questions = questionDAO.getQuestionNamesForQuiz(quiz);
            questionList.getItems().addAll(questions);
        }
    }

    public void doNewQuiz() {
        Main.getSceneManager().showCreateUpdateQuizScene(null);
    }

    public void doEditQuiz() {
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        if (quiz == null) {
            setEmptyChoice("quiz", true);
        } else {
            Main.getSceneManager().showCreateUpdateQuizScene(quiz);
        }
    }

    public void doNewQuestion() {
        Main.getSceneManager().showCreateUpdateQuestionScene(null);
    }

    public void doEditQuestion() {
        Question question = questionDAO.getQuestionByName(questionList.getSelectionModel().getSelectedItem());

        if (question == null) {
            waarschuwingsLabel.setText("Je moet eerst een question kiezen");
            waarschuwingsLabel.setVisible(true);

        } else {
            Main.getSceneManager().showCreateUpdateQuestionScene(question);
        }
    }

    public void doSave() {Main.getSceneManager().showCoordinatorDashboard();}

    public void doMenu() {Main.getSceneManager().showWelcomeScene();}


}
