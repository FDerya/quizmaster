//package database.mysql;
//// Tom van Beek, 500941521.
//
//import model.*;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class QuizDAO extends AbstractDAO implements GenericDAO<Quiz> {
//    private UserDAO userDAO;
//
//    // Constructor met UserDAO als parameter
//    public QuizDAO(DBAccess dBaccess, UserDAO userDAO) {
//        super(dBaccess);
//        this.userDAO = userDAO;
//    }
//    // Maak een lijst met quizen
//    @Override
//    public List<Quiz> getAll() {
//        CourseDAO courseDAO = new CourseDAO(dbAccess);
//        List<Quiz> totalListQuiz = new ArrayList<>();
//        String sql = "SELECT * FROM Quiz;";
//        Quiz quiz = null;
//        try {
//            setupPreparedStatement(sql);
//            ResultSet resultSet = executeSelectStatement();
//            while (resultSet.next()) {
//                int id = resultSet.getInt("idQuiz");
//                int idCourse = resultSet.getInt("idCourse");
//                String name = resultSet.getString("nameQuiz");
//                int level = resultSet.getInt("levelQuiz");
//                int amountQuestion = resultSet.getInt("amountQuestion");
//                Course course = courseDAO.getOneById(idCourse);
//                quiz = new Quiz(id, course, name, level, amountQuestion);
//                totalListQuiz.add(quiz);
//            }
//        } catch (
//                SQLException sqlFout) {
//            System.out.println("SQL fout " + sqlFout.getMessage());
//        }
//        return totalListQuiz;
//    }
//
//    // Zoek een specifieke quiz aan de hand van idQuiz
//    @Override
//    public Quiz getOneById(int id) {
//        String sql = "SELECT * FROM Quiz WHERE idQuiz = ?;";
//        Quiz oneQuiz;
//        CourseDAO courseDAO = new CourseDAO(dbAccess);
//        try {
//            setupPreparedStatement(sql);
//            ResultSet resultSet = executeSelectStatement();
//            while (resultSet.next()) {
//                int idQuiz = resultSet.getInt("idQuiz");
//                int idCourse = resultSet.getInt("idCourese");
//                String name = resultSet.getString("nameQuiz");
//                int level = resultSet.getInt("levelQuiz");
//                int amountQuestion = resultSet.getInt("amountQuestion");
//                Course course = courseDAO.getOneById(idCourse);
//                oneQuiz = new Quiz(idQuiz, idCourse, name, level, amountQuestion);
//            }
//        } catch (
//                SQLException sqlFout) {
//            System.out.println("SQL fout " + sqlFout.getMessage());
//        }
//        return oneQuiz;
//    }
//
//    // Quiz opslaan in database
//    @Override
//    public void storeOne(Quiz quiz) {
//        String sql = "INSERT INTO Quiz (idCourse, nameQuiz, levelQuiz, amountQuestion) VALUES(?, ?, ?, ?);";
//        try {
//            setupPreparedStatementWithKey(sql);
//            preparedStatement.setInt(1, quiz.getCourse().getIdCourse());
//            preparedStatement.setString(2, quiz.getNameQuiz());
//            preparedStatement.setInt(3, quiz.getLevel());
//            preparedStatement.setInt(4, quiz.getAmountQuestions());
//            int primaryKey = executeInsertStatementWithKey();
//            quiz.setIdQuiz(primaryKey);
//        } catch (SQLException sqlFout) {
//            System.out.println(sqlFout) {
//                System.out.println("SQL fout " + sqlFout.getMessage());
//            }
//        }
//    }
//}
//
