//
// Deze DAO is gemaakt door Eline van Tunen, 500636756
//

package database.mysql;

import model.Course;
import model.Group;
import model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO extends AbstractDAO implements GenericDAO<Course> {
    private UserDAO userDAO;
    private Course course = null;


// Constructors
    public CourseDAO(DBAccess dbAccess, UserDAO userDAO) {
        super(dbAccess);
        this.userDAO = userDAO;
    }

// Alle courses ophalen
    @Override
    public List<Course> getAll() {
        List<Course> resultList = new ArrayList<>();
        String sql = "SELECT * FROM Course;";
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()){
                resultList.add(getCourse(resultSet));
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return resultList;
    }


// Course ophalen op basis van ID
    @Override
    public Course getOneById(int id) {
        String sql = "SELECT * FROM Course WHERE idCourse = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = executeSelectStatement()) {
                if (resultSet.next()) {
                    course = getCourse(resultSet);
                    return course;
                }
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return course;
    }

// Course ophalen op basis van naam
    public Course getOneByName(String courseName) {
        String sql = "SELECT * FROM Course WHERE nameCourse = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, courseName);
            ResultSet resultSet = executeSelectStatement();

            if (resultSet.next()) {
                course = getCourse(resultSet);
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return course;
    }


// Nieuwe course opslaan
    @Override
    public void storeOne(Course course) {
        String sql = "INSERT INTO course (idUser, nameCourse, difficultyCourse) VALUES(?, ?, ?);";
        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setInt(1, course.getCoordinator().getIdUser());
            preparedStatement.setString(2, course.getNameCourse());
            preparedStatement.setString(3, course.getDifficultyCourse());
            int primaryKey = executeInsertStatementWithKey();
            course.setIdCourse(primaryKey);
        } catch (SQLException sqlException){
            System.out.println("SQL fout " + sqlException.getMessage());
        }
    }


// Course object maken vanuit resultSet
    private Course getCourse(ResultSet resultSet) throws SQLException {
        UserDAO userDAO = new UserDAO(dbAccess);
        int idCourse = resultSet.getInt("idCourse");
        int idCoordinator = resultSet.getInt("idUser");
        String nameCourse = resultSet.getString("nameCourse");
        String difficultyCourse = resultSet.getString("difficultyCourse");
        User user = userDAO.getOneById(idCoordinator);
        return new Course(idCourse, user, nameCourse, difficultyCourse);
    }

}
