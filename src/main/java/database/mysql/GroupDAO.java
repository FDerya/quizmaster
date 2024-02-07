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
    private UserDAO userDAO = new UserDAO(Main.getDBaccess());
    private CourseDAO courseDAO = new CourseDAO(Main.getDBaccess());

    // Constructor with parameter UserDAO en CourseDAO
    public GroupDAO(DBAccess dBaccess, UserDAO userDAO, CourseDAO courseDAO) {
        super(Main.getDBaccess());
        this.userDAO = userDAO;
        this.courseDAO = courseDAO;
    }

    public GroupDAO(DBAccess dbAccess) {super(dbAccess);}

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
        }
    }

    // Update the information of a Group in the database using the provided group ID
    public Group updateGroupById(Group group) {
        String sql = "UPDATE `group` SET idUser = ?, nameGroup = ?, amountStudent = ?, idCourse = ? " +
                "WHERE `idGroup` = ?";
        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setInt(1, group.getTeacher().getIdUser());
            preparedStatement.setString(2, group.getGroupName());
            preparedStatement.setInt(3, group.getAmountStudent());
            preparedStatement.setInt(4, group.getCourse().getIdCourse());
            preparedStatement.setInt(5, group.getIdGroup());

            executeManipulateStatement();

            return group;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
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
        String sql = "DELETE FROM `group` WHERE idGroup = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, String.valueOf(group.getIdGroup()));
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
            } else {
                System.out.println("Groep niet verwijderd.\nMogelijk bestaat de groep niet of er " +
                        "is een probleem met de query.");
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

    // Retrieves a list of groups associated with a specified course ID from the database
    public List<Group> getGroupsByIdCourse(int idCourse) {

        List<Group> resultList = new ArrayList<>();
        String sql = "SELECT * FROM `group` WHERE idCourse = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, idCourse);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                resultList.add(getGroupFromResultSet(resultSet));
            }
        } catch (SQLException sqlFout) {
            System.out.println("SQL error " + sqlFout.getMessage());
        }
        return resultList;
    }

    // Checks if a group with a given name and course ID exists in the database
    public boolean groupExists(Group group) {
        String sql = "SELECT COUNT(*) FROM `group` WHERE nameGroup = ? AND idCourse = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, group.getGroupName());
            preparedStatement.setInt(2, group.getCourse().getIdCourse());

            try (ResultSet resultSet = executeSelectStatement()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}