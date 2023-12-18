package database.mysql;
// Tom van Beek, 500941521.
import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class QuizDAO extends AbstractDAO implements GenericDAO<Quiz>{
    public List<Quiz> getAll() {
        CourseDAO courseDAO = new CourseDAO(dbAccess);
        List<Quiz> totaalQuiz = new ArrayList<>();
        String sql = "SELECT * FROM Quiz;";
        Quiz quiz = null;
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                int id = resultSet.getInt("idQuiz");
                int idCourse = resultSet.getInt("idCourse");
                String naam = resultSet.getString("nameQuiz");
                int level = resultSet.getInt("levelQuiz");
                int aantal = resultSet.getInt("amountQuestion");
                Course course = courseDAO.getOne(idCourse);
                quiz = new Quiz(id, course, naam, level, aantal);
                totaalQuiz.add(quiz);
            }
        } catch (
                SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return totaalQuiz;
    }
    public Quiz getOneById(int id) {
        String sql = "SELECT * FROM Quiz WHERE idQuiz = ?;";
        Quiz quiz;
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                String naam = resultSet.getString("nameQuiz");
                int level = resultSet.getInt("levelQuiz");
                int aantal = resultSet.getInt("amountQuestion");
                quiz = new Quiz(quiz.getIdQuiz(), quiz.getCourse(), naam, level, aantal);
            }
        } catch (
                SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return quiz;
    }

    public void storeOne(Quiz quiz){
        String sql = "INSERT INTO Quiz (idCourse, nameQuiz, levelQuiz, amountQuestion) VALUES(?, ?, ?, ?);";
        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setInt(1,quiz.getCourse().getIdCourse());
            preparedStatement.setString(2, quiz.getNameQuiz());
            preparedStatement.setInt(3, quiz.getLevel());
            preparedStatement.setInt(4, quiz.getAmountQuestions());
            int primaryKey=executeInsertStatementWithKey();
            quiz.setIdQuiz(primaryKey);
        } catch (SQLException sqlFout) {
            System.out.println(sqlFout);
        }
    }


}

