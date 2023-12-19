package database.mysql;

import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO extends AbstractDAO implements GenericDAO<Course> {
    private UserDAO userDAO;

    // Constructor met UserDAO als parameter
    public CourseDAO(DBAccess dBaccess, UserDAO userDAO) {
        super(dBaccess);
        this.userDAO = userDAO;
    }

    public List<Course> getAll() {
        List<Course> totalListCourse = new ArrayList<>();
        String sql = "SELECT * FROM Course;";
        Course course = null;
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                int idCourse = resultSet.getInt("idCourse");
                int idUser = resultSet.getInt("idUser");
                String nameCourse = resultSet.getString("nameCourse");
                int levelCourse = resultSet.getInt("difficultyCourse");
                User user = userDAO.getOneById(idUser);
                course = new Course(idCourse, user, nameCourse, levelCourse);
                totalListCourse.add(course);
            }
        } catch (
                SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return totalListCourse;
    }

    @Override
    public Course getOneById(int id) {
        String sql = "SELECT * FROM Course WHERE idCourse = ?;";
        Course oneCourse = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                int idCourse = resultSet.getInt("idCourese");
                int idUser = resultSet.getInt("idUser");
                String nameCourse = resultSet.getString("nameCourse");
                int level = resultSet.getInt("difficultyCourse");
                User user = userDAO.getOneById(idUser);
                oneCourse = new Course(idCourse, user, nameCourse, level);
            }
        } catch (
                SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return oneCourse;
    }
    public Course getOneByName(String name) {
        String sql = "SELECT * FROM Course WHERE nameCourse = ?;";
        Course oneCourse = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, name);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                int idCourse = resultSet.getInt("idCourse");
                int idUser = resultSet.getInt("idUser");
                String nameCourse = resultSet.getString("nameCourse");
                int level = resultSet.getInt("difficultyCourse");
                User user = userDAO.getOneById(idUser);
                oneCourse = new Course(idCourse, user, nameCourse, level);
            }
        } catch (
                SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return oneCourse;
    }
    @Override
    public void storeOne(Course course) {
        String sql = "INSERT INTO Course (idCourse, idUser, nameCourse, difficultyCourse) VALUES(?, ?, ?, ?);";
        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setInt(1, course.getIdCourse());
            preparedStatement.setInt(2, course.getCoordinator().getIdUser());
            preparedStatement.setString(3, course.getNameCourse());
            preparedStatement.setInt(4, course.getDifficultyCourse());
            int primaryKey = executeInsertStatementWithKey();
            course.setIdCourse(primaryKey);
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
    }
}
