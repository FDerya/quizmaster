package database.mysql;
// Tom van Beek, 500941521.

import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO extends AbstractDAO implements GenericDAO<Quiz> {
    private UserDAO userDAO;
    private CourseDAO courseDAO = new CourseDAO(dbAccess, userDAO);
    private Quiz quiz = null;

    // Constructor met UserDAO als parameter
    public QuizDAO(DBAccess dBaccess, UserDAO userDAO) {
        super(dBaccess);
        this.userDAO = userDAO;
    }

    public QuizDAO(DBAccess dBaccess) {
        super(dBaccess);
    }

    // Maak een lijst met quizzen
    @Override
    public List<Quiz> getAll() {
        List<Quiz> totalListQuiz = new ArrayList<>();
        String sql = "SELECT * FROM Quiz;";
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
            preparedStatement.setInt(1, quiz.getCourse().getIdCourse());
            preparedStatement.setString(2, quiz.getNameQuiz());
            preparedStatement.setString(3, quiz.getLevel());
            preparedStatement.setInt(4, quiz.getAmountQuestions());
            int primaryKey = executeInsertStatementWithKey();
            quiz.setIdQuiz(primaryKey);
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
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
}
