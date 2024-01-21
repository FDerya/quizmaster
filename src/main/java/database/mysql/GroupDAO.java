package database.mysql;
// Bianca Duijvesteijn, studentnummer 500940421
// Manages database operations for group entities, including retrieval, creation, and deletion.
// Works with UserDAO for user-related functionalities.

import model.*;
import view.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO extends AbstractDAO implements GenericDAO<Group> {
    // Attributes
    private UserDAO userDAO;
    private CourseDAO courseDAO;

    // Constructor with parameter UserDAO en CourseDAO
    public GroupDAO(DBAccess dBaccess, UserDAO userDAO, CourseDAO courseDAO) {
        super(Main.getDBaccess());
        this.userDAO = userDAO;
        this.courseDAO = courseDAO;
    }

    // Method to retrieve all groups
    @Override
    public List<Group> getAll() {
        List<Group> groupList = new ArrayList<>();
        String sql = "SELECT * FROM `group`;";
        try {
            setupPreparedStatement(sql);
            try (ResultSet resultSet = executeSelectStatement()) {
                while (resultSet.next()) {
                    Group group = getGroupFromResultSet(resultSet);
                    groupList.add(group);
                }
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return groupList;
    }

    // Method to retrieve a specific group based on ID
    @Override
    public Group getOneById(int idGroup) {
        String sql = "SELECT * FROM `group` WHERE idGroup = ?";
        Group group = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, idGroup);
            try (ResultSet resultSet = executeSelectStatement()) {
                if (resultSet.next()) {
                    group = getGroupFromResultSet(resultSet);
                }
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return group;
    }

    // Method to store one Group
    @Override
    public void storeOne(Group group) {
        String sql = "INSERT INTO `group` (idUser, nameGroup, amountStudent, idCourse)" +
                " VALUES (?, ?, ?, ?);";
        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setInt(1, group.getTeacher().getIdUser());
            preparedStatement.setString(2, group.getGroupName());
            preparedStatement.setInt(3, group.getAmountStudent());
            preparedStatement.setInt(4, group.getCourse().getIdCourse());
            executeManipulateStatement();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.err.println("Fout bij het opslaan van de groep: " + sqlException.getMessage());
        }
    }

    // Update the information of a Group in the database
    public Group updateOne(Group group) {
        Course course = group.getCourse();
        CourseDAO courseDAO = new CourseDAO(Main.getDBaccess(), new UserDAO(Main.getDBaccess()));

        Course existingCourse = courseDAO.getOneById(course.getIdCourse());

        if (existingCourse != null) {
            updateGroupWithCourseId(group, existingCourse.getIdCourse());
            return getOneById(group.getIdGroup());
        } else {
            System.out.println("Kan groep niet updaten. Cursus niet gevonden.");
            return null;
        }
    }

    // Update the group information in the database using the provided course ID
    private void updateGroupWithCourseId(Group group, int courseId) {
        String updateSql = "UPDATE `group` SET idCourse = ?, nameGroup = ?, amountStudent = ?, idUser = ? WHERE idGroup = ?";
        try {
            setupPreparedStatement(updateSql);
            preparedStatement.setInt(1, courseId);
            storeUpdateGroup(group);
            preparedStatement.setInt(5, group.getIdGroup());

            executeManipulateStatement();
        } catch (SQLException sqlException) {
            System.out.println("SQL error " + sqlException.getMessage());
        }
    }

    // Set the parameters for updating the group information
    private void storeUpdateGroup(Group group) throws SQLException {
        preparedStatement.setString(2, group.getGroupName());
        preparedStatement.setInt(3, group.getAmountStudent());
        preparedStatement.setInt(4, group.getTeacher().getIdUser());
    }

    // Method to create a Group object from a ResultSet
    private Group getGroupFromResultSet(ResultSet resultSet) throws SQLException {
        int idGroup = resultSet.getInt("idGroup");
        Course course = courseDAO.getOneById(resultSet.getInt("idCourse"));
        String nameGroup = resultSet.getString("nameGroup");
        int amountStudent = resultSet.getInt("amountStudent");
        User teacher = userDAO.getOneById(resultSet.getInt("idUser"));

        return createGroup(idGroup, course, nameGroup, amountStudent, teacher);
    }

    // Method to create a group instance
    private Group createGroup(int idGroup, Course nameCourse, String nameGroup, int amountStudent,
                              User administrator) {
        return new Group(idGroup, nameCourse, nameGroup, amountStudent, administrator);
    }

    // Methode to delete a group
    public void deleteGroup(Group group) {
        String sql = "DELETE FROM `group` WHERE nameGroup = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, group.getGroupName());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
            } else {
                System.out.println("Groep niet verwijderd.\nMogelijk bestaat de groep niet of er is een probleem met de query.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Counts the number of groups for a given course in the database.
    public int countGroupsForCourse(Course course) {
        String sql = "SELECT COUNT(*) FROM `Group` WHERE idCourse = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, course.getIdCourse());

            try (ResultSet resultSet = executeSelectStatement()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
