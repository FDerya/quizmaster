package controller;

import database.mysql.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.*;
import view.Main;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorDashboardController extends WarningAlertController {

    @FXML
    private ListView<Course> courseList;
    @FXML
    private ListView<Quiz> quizList;
    @FXML
    private ListView<String> questionList;
    private final DBAccess dbAccess;
    @FXML
    Button mainScreenButton;

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

    User currentUser = User.getCurrentUser();

    public void setup() {
        mainScreenButton.setText(Main.getMainScreenButtonText());
        setCourseList();
        setQuizList();

    }

    private void setCourseList() {
        List<Course> courseUserList = courseDAO.getAllByIdUser(currentUser.getIdUser());
        courseList.setItems(FXCollections.observableList(courseUserList));
        courseList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldCourse, newCourse) -> {
                    List<Quiz> quizzen = quizDAO.getAllByCourseId(newCourse.getIdCourse());
                    if (quizzen.isEmpty()) {
                        showEmpty("cursus", "quizzen");
                        quizList.setItems(null);
                        questionList.setItems(null);
                    } else {
                        warningLabel.setVisible(false);
                        questionList.setItems(null);
                        quizList.setItems(FXCollections.observableList(quizzen));
                    }
                });
    }

    private void setQuizList() {
        makeColumns();
        quizList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldQuiz, newQuiz) -> {
                    if (newQuiz != null) {
                        List<String> questions = questionDAO.getQuestionNamesForQuiz(newQuiz);
                        List<Integer> maxAmount = new ArrayList<>();
                        int max = quizList.getSelectionModel().getSelectedItem().getMinimumAmountCorrectQuestions();
                        maxAmount.add(max);
                        questionList.setItems(FXCollections.observableList(questions));
                        //questionQuizList.setItems(FXCollections.observableList(maxAmount));
                    }
                });
    }

    private void makeColumns() {
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
                    aantal.setText(" " + item.getMinimumAmountCorrectQuestions() + " ");
                }
                setGraphic(hBox);
            }
        });
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
            setEmptyChoice("vraag", true);
        } else {
            Main.getSceneManager().showCreateUpdateQuestionScene(question);
        }
    }

    public void doMenu() {
        Main.getSceneManager().showWelcomeScene();
    }


}
