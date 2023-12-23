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

    // Method to retrieve all groups
    @Override
    public List<Group> getAll() {
        List<Group> resultList = new ArrayList<>();
        String sql = "SELECT * FROM Group";
        try {
            setupPreparedStatement(sql);
            try (ResultSet resultSet = executeSelectStatement()) {
                while (resultSet.next()) {
                    resultList.add(getGroupFromResultSet(resultSet));
                }
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return resultList;
    }

    // Method to retrieve the count of groups associated with a course
    public int getCountOfGroupsByCourse(String courseName) {
        String sql = "SELECT COUNT(*) FROM Group WHERE nameCourse = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, courseName);

            try (ResultSet resultSet = executeSelectStatement()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return 0;
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
        int idTeacher = resultSet.getInt("idTeacher");
        String nameCourse = resultSet.getString("nameCourse");
        String nameGroup = resultSet.getString("nameGroup");
        int amountStudent = resultSet.getInt("amountStudent");
        int administratorUserId = resultSet.getInt("userId");
        String administratorUserName = resultSet.getString("userName");
        String difficulty = resultSet.getString("difficultyCourse");

        User administrator = getAdministrator(administratorUserId, administratorUserName);
        Course course = createCourse(administrator, nameCourse, difficulty);

        return createGroup(idGroup, idTeacher, course, nameGroup, amountStudent, administrator);
    }

    // Method to get the administrator based on ID or username
    private User getAdministrator(int administratorUserId, String administratorUsername) {
        User administator = userDAO.getOneById(administratorUserId);
        if (administator == null) {
            administator = getUserByUsername(administratorUsername);
        }
        return administator;
    }

    // Method to get a user based on username
    private User getUserByUsername(String username) {
        return userDAO.getAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // Method to create a Course object
    private Course createCourse(User administrator, String nameCourse, String difficulty) {
        return new Course(administrator, nameCourse, difficulty);
    }

    // Method to create a Group object
    private Group createGroup(int idGroup, int idTeacher, Course course, String nameGroup,
                              int amountStudent, User administrator) {
        return new Group(idGroup, idTeacher, course, nameGroup, amountStudent, administrator);
    }
    // Method to get a group by name
    public Group getGroupByName(String selectedGroupName) {
        List<Group> groups = getAll();
        for (Group group : groups) {
            if (group.getGroupName().equals(selectedGroupName)) {
                return group;
            }
        }
        return null;
    }

    // Method to delete a group
    public void deleteGroup(Group group) {
        String sql = "DELETE FROM Group WHERE groupName = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, group.getGroupName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



