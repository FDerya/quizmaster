//
// Deze DAO is gemaakt door Eline van Tunen, 500636756
//

package database.mysql;

import model.Course;
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
    public CourseDAO(DBAccess dbAccess){
        super(dbAccess);
    }


// Get all courses
    @Override
    public List<Course> getAll() {
        List<Course> resultList = new ArrayList<>();
        String sql = "SELECT * FROM Course ORDER BY nameCourse;";
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

// Get courses by idUser
    public List<Course> getAllByIdUser(int id) {
        List<Course> resultList = new ArrayList<>();
        String sql = "SELECT * FROM Course WHERE idUser = ? ORDER BY nameCourse;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()){
                resultList.add(getCourse(resultSet));
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return resultList;
    }


// Get courses by id
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

// Get courses by name
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



// Save a new course
    @Override
    public void storeOne(Course course) {
        String sql = "INSERT INTO course (idUser, nameCourse, difficultyCourse) VALUES(?, ?, ?);";
        try {
            setupPreparedStatementWithKey(sql);
            storeCourse(course);
            int primaryKey = executeInsertStatementWithKey();
            course.setIdCourse(primaryKey);
        } catch (SQLException sqlException){
            System.out.println("SQL fout " + sqlException.getMessage());
        }
    }


// Update one course
    public void updateOne(Course course){
        String sql = "UPDATE Course SET idUser = ?, nameCourse = ?, difficultyCourse = ? WHERE idCourse = ?;";
        try {
            setupPreparedStatement(sql);
            storeCourse(course);
            preparedStatement.setInt(4, course.getIdCourse());
            executeManipulateStatement();
        } catch (SQLException sqlException){
            System.out.println("SQL error" + sqlException.getMessage());
        }
    }


// Delete one course
    public void deleteOne(Course course){
        String sql = "DELETE FROM course WHERE idCourse = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, course.getIdCourse());
            executeManipulateStatement();
        } catch (SQLException sqlFout){
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
    }


// Create course object from resultSet
    private Course getCourse(ResultSet resultSet) throws SQLException {
        UserDAO userDAO = new UserDAO(dbAccess);
        int idCourse = resultSet.getInt("idCourse");
        int idCoordinator = resultSet.getInt("idUser");
        String nameCourse = resultSet.getString("nameCourse");
        String difficultyCourse = resultSet.getString("difficultyCourse");
        User user = userDAO.getOneById(idCoordinator);
        return new Course(idCourse, user, nameCourse, difficultyCourse);
    }


// Method of preparedStatements for saving and updating a course


    private void storeCourse(Course course) throws SQLException{
        preparedStatement.setInt(1, course.getCoordinator().getIdUser());
        preparedStatement.setString(2, course.getNameCourse());
        preparedStatement.setString(3, course.getDifficultyCourse());
    }

}
