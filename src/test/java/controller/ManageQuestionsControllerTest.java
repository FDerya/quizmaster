package controller;

import database.mysql.QuestionDAO;
import database.mysql.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import model.Question;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.Main;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManageQuestionsControllerTest {

    private ManageQuestionsController controller;
    private QuestionDAO questionDAO;

    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        // Initialize the controller and the QuestionDAO
        controller = new ManageQuestionsController();
        questionDAO = new QuestionDAO(Main.getDBaccess());
        userDAO = new UserDAO(Main.getDBaccess());
    }

    @Test
    public void testSetupWithQuestions() {
        // Arrange
        User.setCurrentUser(userDAO.getOneById(1));
        User currentUser = userDAO.getOneById(1);
        currentUser.setIdUser(26); // Set user ID to 1 for testing
        List<Question> userQuestions = new ArrayList<>();
        userQuestions.add(questionDAO.getOneById(1));
        // Simulate getting questions for the user from the database
        List<Question> mockedQuestions = questionDAO.getQuestionsForUser(currentUser.getIdUser());
        // Expect that the mocked questions list is not empty
        assertTrue(mockedQuestions != null && !mockedQuestions.isEmpty());

        // Act
        controller.setup();

        // Assert
        // Verify that the questionList has items set
        ListView<Question> questionList = controller.questionList;
        ObservableList<Question> items = questionList.getItems();
        assertEquals(mockedQuestions.size(), items.size());
        assertTrue(items.containsAll(mockedQuestions));
    }

    @Test
    public void testSetupWithoutQuestions() {
        // Arrange
        User currentUser = userDAO.getOneById(1);
        currentUser.setIdUser(2); // Set user ID to 2 for testing, assuming no questions for this user
        List<Question> userQuestions = new ArrayList<>();
        // Simulate getting questions for the user from the database
        List<Question> mockedQuestions = questionDAO.getQuestionsForUser(currentUser.getIdUser());
        // Expect that the mocked questions list is empty
        assertTrue(mockedQuestions == null || mockedQuestions.isEmpty());

        // Act
        controller.setup();

        // Assert
        // Verify that the questionList is empty
        ListView<Question> questionList = controller.questionList;
        ObservableList<Question> items = questionList.getItems();
        assertTrue(items.isEmpty());
    }

    // Add more tests for other methods as needed
}
