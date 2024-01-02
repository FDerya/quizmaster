package database.mysql;
// Bianca Duijvesteijn, studentnummer 500940421
// Manages database operations for Group entities, including retrieval, creation, and deletion.
// Associates with UserDAO for user-related functionalities.

import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO extends AbstractDAO implements GenericDAO<Group> {
    private UserDAO userDAO;

    // Constructor with UserDAO as a parameter
    public GroupDAO(DBAccess dBaccess, UserDAO userDAO) {
        super(dBaccess);
        this.userDAO = userDAO;
    }

    public GroupDAO(DBAccess dBaccess) {
        super(dBaccess);
    }

    // Method to retrieve all groups
    @Override
    public List<Group> getAll() {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT * FROM `Group`;";
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {

                Group group = new Group();
                group.setIdGroup(resultSet.getInt("idGroup"));
                group.setGroupName(resultSet.getString("nameGroup"));
                groups.add(group);
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return groups;
    }

    // Method to retrieve a specific group based on ID
    @Override
    public Group getOneById(int id) {
        String sql = "SELECT * FROM group WHERE userId = ?";
        Group group = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
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

    // Method to store a new group
    @Override
    public void storeOne(Group group) {
        String sql = "INSERT INTO `group` (idUser, nameGroup, amountStudent)" +
                " VALUES (?, ?, ?);";
        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setInt(1, group.getUserName().getIdUser());
            preparedStatement.setString(2, group.getGroupName());
            preparedStatement.setInt(3, group.getAmountStudent());
            executeManipulateStatement();
        } catch (SQLException sqlFout) {
            System.out.println(sqlFout.getMessage());
        }
    }

    // Method to create a Group object from a ResultSet
    private Group getGroupFromResultSet(ResultSet resultSet) throws SQLException {
        int idGroup = resultSet.getInt("idGroup");
        String nameGroup = resultSet.getString("nameGroup");
        int amountStudent = resultSet.getInt("amountStudent");

        int courseId = resultSet.getInt("idCourse");

        User administrator = getAdministrator(resultSet.getInt("userId"), resultSet.getString("userName"));
        Course course = getCourseById(courseId);

        return createGroup(idGroup, course, nameGroup, amountStudent, administrator);
    }

    // Method to retrieve a Course object by its ID
    private Course getCourseById(int courseId) {
        String sql = "SELECT * FROM Course WHERE idCourse = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, courseId);
            try (ResultSet resultSet = executeSelectStatement()) {
                if (resultSet.next()) {
                    return getCourseFromResultSet(resultSet);
                }
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return null;
    }

    // Method to create a Course object from a ResultSet
    private Course getCourseFromResultSet(ResultSet resultSet) throws SQLException {
        int idCourse = resultSet.getInt("idCourse");
        String nameCourse = resultSet.getString("nameCourse");
        String difficulty = resultSet.getString("difficultyCourse");

        return new Course(idCourse, nameCourse, difficulty);
    }

    // Method to get the administrator based on ID or username
    private User getAdministrator(int administratorUserId, String administratorUsername) {
        User administrator = userDAO.getOneById(administratorUserId);
        if (administrator == null) {
            administrator = getUserByUsername(administratorUsername);
        }
        return administrator;
    }

    // Method to get a user based on username
    private User getUserByUsername(String username) {
        return userDAO.getAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // Method to create a Group object
    private Group createGroup(int idGroup, Course nameCourse, String nameGroup, int amountStudent,
                              User administrator) {
        return new Group(idGroup, nameCourse, nameGroup, amountStudent, administrator);
    }

    // Method to delete a group
    public void deleteGroup(Group group) {
        int groupId = getGroupIdByGroupName(group.getGroupName());
        if (groupId == -1) {
            System.out.println("Groep niet gevonden.");
            return;
        }
        String sql = "DELETE FROM `group` WHERE idGroup = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, groupId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Groep succesvol verwijderd.");
            } else {
                System.out.println("Geen groep verwijderd. Mogelijk bestaat de groep niet of er is een probleem met de query.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve the ID of a group based on its name
    private int getGroupIdByGroupName(String groupName) {
        String sql = "SELECT idGroup FROM `group` WHERE nameGroup = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, groupName);

            try (ResultSet resultSet = executeSelectStatement()) {
                if (resultSet.next()) {
                    return resultSet.getInt("idGroup");
                }
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return -1;
    }

    // Method to get users not in the specified group
    public List<User> getUsersNotInGroup(Course course, Group group) {
        String sql = "SELECT * FROM User WHERE idUser NOT IN (SELECT idUser FROM GroupUser WHERE idGroup = ?)";
        List<User> usersNotInGroup = new ArrayList<>();
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, group.getIdGroup());

            try (ResultSet resultSet = executeSelectStatement()) {
                while (resultSet.next()) {
                    usersNotInGroup.add(createUserFromResultSet(resultSet));
                }
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return usersNotInGroup;
    }

    // Method to get users in the specified group
    public List<User> getUsersInGroup(Group group) {
        String sql = "SELECT * FROM User WHERE idUser IN (SELECT idUser FROM GroupUser WHERE idGroup = ?)";
        List<User> usersInGroup = new ArrayList<>();
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, group.getIdGroup());
            try (ResultSet resultSet = executeSelectStatement()) {
                while (resultSet.next()) {
                    usersInGroup.add(createUserFromResultSet(resultSet));
                }
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return usersInGroup;
    }

    // Method to create a User object from a ResultSet
    private User createUserFromResultSet(ResultSet resultSet) throws SQLException {
        int idUser = resultSet.getInt("idUser");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String firstName = resultSet.getString("firstName");
        String prefix = resultSet.getString("prefix");
        String surname = resultSet.getString("surname");
        String role = resultSet.getString("role");
        return new User(idUser, username, password, firstName, prefix, surname, role);
    }

    // Method to get the name of the course associated with a group by group ID
    public String getCourseNameForGroup(int groupId) {
        String sql = "SELECT c.nameCourse " +
                "FROM course c " +
                "JOIN participation p ON c.idCourse = p.idCourse " +
                "WHERE p.idGroup = ?";

        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, groupId);

            try (ResultSet resultSet = executeSelectStatement()) {
                if (resultSet.next()) {
                    return resultSet.getString("nameCourse");
                } else {
                    System.out.println("No results found for the query.");
                }
            }
        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return null;
    }

    // Method to get the count of groups with the same course as the given group
    public int getGroupCountForSameCourse(Group group) {
        if (group == null || group.getCourseName() == null) {
            return -1;
        }
        int courseId = group.getCourseName().getIdCourse();

        String sql = "SELECT COUNT(DISTINCT idGroup) " +
                "FROM participation " +
                "WHERE idCourse = ? AND idGroup <> ?";

        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, courseId);
            preparedStatement.setInt(2, group.getIdGroup());

            try (ResultSet resultSet = executeSelectStatement()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    System.out.println("No results found for the query.");
                    return -1;
                }
            }
        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return -1;
    }
}



