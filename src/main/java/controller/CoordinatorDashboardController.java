package controller;

import database.mysql.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.*;
import view.Main;
import javacouchdb.*;

import java.util.ArrayList;
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
                    aantal.setText(" " + item.getAmountQuestions() + " ");
                }
                setGraphic(hBox);
            }
        });

        quizList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldQuiz, newQuiz) -> {
                    List<String> questions = questionDAO.getQuestionNamesForQuiz(newQuiz);
                    questionList.setItems(FXCollections.observableList(questions));
                });

        questionList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        /*questionList.getSelectionModel().getSelectedItems().addListener(
                (observableValue, oldQuestion, newQuestion) -> {
            if (questionList != null) {
                int maxAmount = quizList.getSelectionModel().getSelectedItem().getAmountQuestions();
                int questionsize = questionList.getItems().size();
                if (questionsize == maxAmount) {
                    System.out.println("Maximum bereikt");
                }
            }
        });*/
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

    public void doSave(ActionEvent event) {
        List<String> questions = new ArrayList<>();
        questions.addAll(questionList.getSelectionModel().getSelectedItems());
        List<Question> quizVragen = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            quizVragen.add(questionDAO.getQuestionByName(questions.get(i)));
        }
        //questionCouchDBDAO.saveQuestionsForQuiz(quizVragen);
    Main.getSceneManager().showCoordinatorDashboard();}

    public void doMenu() {Main.getSceneManager().showWelcomeScene();}


}
