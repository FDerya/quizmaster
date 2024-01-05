package database.mysql;
// Bianca Duijvesteijn, studentnummer 500940421
// Beheert databasebewerkingen voor groepsentiteiten, inclusief ophalen, maken en verwijderen.
// Werkt samen met UserDAO voor gebruikersgerelateerde functionaliteiten.

import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class GroupDAO extends AbstractDAO implements GenericDAO<Group> {
    private UserDAO userDAO;

    // Constructor met UserDAO als parameter
    public GroupDAO(DBAccess dBaccess, UserDAO userDAO) {
        super(dBaccess);
        this.userDAO = userDAO;
    }

    public GroupDAO(DBAccess dBaccess) {
        super(dBaccess);
    }

    // Methode om alle groepen op te halen
    @Override
    public List<Group> getAll() {
        List<Group> groupList = new ArrayList<>();
        String sql = "SELECT * FROM `Group`;";
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                Group group = getGroupFromResultSet(resultSet);
                groupList.add(group);
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return groupList;
    }

    // Methode om een specifieke groep op te halen op basis van ID
    @Override
    public Group getOneById(int idGroup) {
        String sql = "SELECT * FROM group WHERE idGroup = ?";
        Group group = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, idGroup);
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                group = getGroupFromResultSet(resultSet);
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return null;
    }

    // Methode om een nieuwe groep op te slaan
    @Override
    public void storeOne(Group group) {
        String sql = "INSERT INTO `group` (nameCourse, nameGroup, amountStudent, userName)" +
                " VALUES (?, ?, ?);";
        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setString(1, group.getCourseName().getNameCourse());
            preparedStatement.setString(2, group.getGroupName());
            preparedStatement.setInt(3, group.getAmountStudent());
            preparedStatement.setString(4, group.getUserName().getUsername());
            executeManipulateStatement();
        } catch (SQLException sqlFout) {
            System.out.println(sqlFout.getMessage());
        }
    }

    // Methode om een Group-object te maken van een ResultSet
    private Group getGroupFromResultSet(ResultSet resultSet) throws SQLException {
        int idGroup = resultSet.getInt("idGroup");
        Course nameCourse = getCourseByCourseName("nameCourse"); 
        String nameGroup = resultSet.getString("nameGroup");
        int amountStudent = resultSet.getInt("amountStudent");
        User userName = getUserByUsername(resultSet.getString("userName"));

        return createGroup(idGroup, nameCourse, nameGroup, amountStudent, userName);
    }

    private Course getCourseByCourseName(String nameCourse) {
        String sql = "SELECT * FROM Course WHERE nameCourse = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, nameCourse);
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

    // Methode om een Cursusobject te maken op basis van een ResultSet
    private Course getCourseFromResultSet(ResultSet resultSet) throws SQLException {
        int idCourse = resultSet.getInt("idCourse");
        String nameCourse = resultSet.getString("nameCourse");
        String difficulty = resultSet.getString("difficultyCourse");

        return new Course(idCourse, nameCourse, difficulty);
    }

    // Methode om een gebruiker te verkrijgen op basis van gebruikersnaam
    private User getUserByUsername(String username) {
        return userDAO.getAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // Methode om een groepsobject te maken
    private Group createGroup(int idGroup, Course nameCourse, String nameGroup, int amountStudent,
                              User administrator) {
        return new Group(idGroup, nameCourse, nameGroup, amountStudent, administrator);
    }

    // Methode om een groep te verwijderen
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

    // Methode om de ID van een groep op te halen op basis van de naam
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

    // Methode om gebruikers te krijgen die niet in de opgegeven groep zitten
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

    // Methode om gebruikers in de opgegeven groep te krijgen
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

    // Methode om een User-object te maken op basis van een ResultSet
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

    // Methode om de naam van de cursus op te halen die aan een groep is gekoppeld op basis van idGroup
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

    // Methode om het aantal groepen te bepalen met hetzelfde parcours als de gegeven groep
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



