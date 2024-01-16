package database.mysql;
// Tom van Beek, 500941521.

import model.*;
import view.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO extends AbstractDAO implements GenericDAO<Quiz> {
    private UserDAO userDAO = new UserDAO(Main.getDBaccess());
    private CourseDAO courseDAO = new CourseDAO(Main.getDBaccess(), userDAO);
    private Quiz quiz = null;

    // Constructor met UserDAO als parameter
    public QuizDAO(DBAccess dbAccess, UserDAO userDAO) {
        super(dbAccess);
        this.userDAO = userDAO;
    }

    public QuizDAO(DBAccess dbAccess) {
        super(dbAccess);
    }

    // Maak een lijst met quizzen
    @Override
    public List<Quiz> getAll() {
        List<Quiz> totalListQuiz = new ArrayList<>();
        String sql = "SELECT * FROM Quiz ORDER BY nameQuiz;";
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                quiz = getQuiz(resultSet, courseDAO, quiz);
                totalListQuiz.add(quiz);
            }
        } catch (
                SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return totalListQuiz;
    }

    // Zoek een specifieke quiz aan de hand van idQuiz
    @Override
    public Quiz getOneById(int id) {
        String sql = "SELECT * FROM Quiz WHERE idQuiz = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                quiz = getQuiz(resultSet, courseDAO, quiz);
            }
        } catch (
                SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return quiz;
    }

    // Zoek een specifieke quiz aan de hand van de naam
    public Quiz getOneByName(String quizName) {
        String sql = "SELECT * FROM Quiz WHERE nameQuiz = ?;";
        CourseDAO courseDAO = new CourseDAO(dbAccess, userDAO);
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, quizName);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                quiz = getQuiz(resultSet, courseDAO, quiz);
            }
        } catch (
                SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return quiz;
    }

    // Quiz opslaan in database
    @Override
    public void storeOne(Quiz quiz) {
        String sql = "INSERT INTO Quiz (idCourse, nameQuiz, levelQuiz, amountQuestion) VALUES(?, ?, ?, ?);";
        try {

            setupPreparedStatementWithKey(sql);
            storeQuiz(quiz);
            int primaryKey = executeInsertStatementWithKey();
            quiz.setIdQuiz(primaryKey);
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
    }

    //Update a quiz
    public void updateOne(Quiz quiz) {
        String sql = "UPDATE Quiz SET idCourse = ?, nameQuiz = ?, levelQuiz = ?, amountQuestion = ? WHERE idQuiz = ?;";
        try {
            setupPreparedStatement(sql);
            storeQuiz(quiz);
            preparedStatement.setInt(5, quiz.getIdQuiz());
            executeManipulateStatement();
        } catch (SQLException sqlException) {
            System.out.println("SQL error" + sqlException.getMessage());
        }
    }

    // Methode voor de storeOne en updateOne
    private void storeQuiz(Quiz quiz) throws SQLException {
        preparedStatement.setInt(1, quiz.getCourse().getIdCourse());
        preparedStatement.setString(2, quiz.getNameQuiz());
        preparedStatement.setString(3, quiz.getLevel());
        preparedStatement.setInt(4, quiz.getAmountQuestions());
    }

    private Quiz getQuiz(ResultSet resultSet, CourseDAO courseDAO, Quiz quiz) throws SQLException {
        int idQuiz = resultSet.getInt("idQuiz");
        int idCourse = resultSet.getInt("idCourse");
        String name = resultSet.getString("nameQuiz");
        String level = resultSet.getString("levelQuiz");
        int amountQuestion = resultSet.getInt("amountQuestion");
        Course course = courseDAO.getOneById(idCourse);
        quiz = new Quiz(idQuiz, course, name, level, amountQuestion);
        return quiz;
    }

    // Verwijder een quiz uit de lijst aan de hand van idQuiz
    public void deleteQuiz(Quiz quizDelete) {
        String sql = "DELETE FROM Quiz WHERE idQuiz =?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, quizDelete.getIdQuiz());
            executeManipulateStatement();
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
    }

    // Presenteert een lijst met vragen van de quiz
    public List<Question> getQuestions(Quiz quiz) {
        QuestionDAO questionDAO = new QuestionDAO(dbAccess);
        List<Question> quizList = new ArrayList<>();
        List<Integer> randomNumbers = new ArrayList<>(getRandomNumbers(quiz));
        for (int i = 0; i < quiz.getAmountQuestions(); i++) {
            quizList.add(questionDAO.getOneById(randomNumbers.get(i)));
        }
        return quizList;
    }

    // Produceert een List van willekeurige cijfers, afhankelijk van het aantal vragen wat nodig is in de quiz
    public List<Integer> getRandomNumbers(Quiz quiz) {
        QuestionDAO questionDAO = new QuestionDAO(dbAccess);
        List<Integer> questionAmount = new ArrayList<>();
        List<Integer> quizRandomAmount = new ArrayList<>();
        int maxQuestions = questionDAO.getQuestionCountForQuiz(quiz);
        for (int i = 1; i <= maxQuestions; i++) {
            questionAmount.add(i);
        }
        for (int i = 1; i <= quiz.getAmountQuestions(); i++) {
            int randomNumber = (int) (Math.random() * questionAmount.size());
            quizRandomAmount.add(questionAmount.get(randomNumber));
            questionAmount.remove(randomNumber);
        }
        return quizRandomAmount;
    }

    public List<Quiz> getQuizzesFromUser(User user) {
        List<Quiz> quizList = new ArrayList<>();
        String sql = "Select idQuiz, q.idCourse, nameQuiz, levelQuiz, amountQuestion from quiz q join course c on q.idCourse = c.idCourse where idUser = ?;";
        CourseDAO courseDAO = new CourseDAO(dbAccess, userDAO);
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, user.getIdUser());
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                quiz = getQuiz(resultSet, courseDAO, quiz);
                quizList.add(quiz);
            }
        } catch (
                SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return quizList;
    }
}


