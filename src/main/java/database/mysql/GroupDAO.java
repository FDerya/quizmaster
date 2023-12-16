package database.mysql;
// Bianca Duijvesteijn, studentnummer 500940421

import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO extends AbstractDAO implements GenericDAO<Group> {
    private static UserDAO userDAO;

    // Constructor met UserDAO als parameter
    public GroupDAO(DBAccess dBaccess, UserDAO userDAO) {
        super(dBaccess);
        this.userDAO = userDAO;
    }

    // Methode om alle groepen op te halen
    @Override
    public List<Group> getAll() {
        List<Group> resultList = new ArrayList<>();
        String sql = "SELECT * FROM Group";
        try {
            // Voorbereiden van de SQL statement
            setupPreparedStatement(sql);
            // Uitvoeren van de select statement
            try (ResultSet resultSet = executeSelectStatement()) {
                // Verwerken van de resultaten
                while (resultSet.next()) {
                    resultList.add(getGroupFromResultSet(resultSet));
                }
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return resultList;
    }


    // Methode om een specifieke groep op te halen op basis van ID
    @Override
    public Group getOneById(int id) {
        String sql = "SELECT * FROM group WHERE userId = ?";
        Group group = null;
        try {
            // Voorbereiden van de SQL statement
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            // Uitvoeren van de select statement
            try (ResultSet resultSet = executeSelectStatement()) {
                // Verwerken van de resultaten
                if (resultSet.next()) {
                    return getGroupFromResultSet(resultSet);
                }
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return group;
    }


    // Methode om een nieuwe groep op te slaan
    @Override
    public void storeOne(Group group) {
        String sql = "INSERT INTO `group` (naamGroep, naamCursus, aantalStudenten, gebruikersInlogNaam)" +
                " VALUES (?, ?, ?, ?);";
        try {
            // Voorbereiden van de SQL statement
            setupPreparedStatement(sql);
            // Invullen van de parameters
            preparedStatement.setString(1, group.getGroupName());
            preparedStatement.setString(2, group.getCourseName().getNameCourse());
            preparedStatement.setInt(3, group.getAmountStudent());
            preparedStatement.setString(4, String.valueOf(group.getUserName().getIdUser()));

            // Uitvoeren van de insert statement
            executeManipulateStatement();
        } catch (SQLException sqlFout) {
            System.out.println(sqlFout.getMessage());
        }
    }

    // Methode om een Group-object te maken vanuit een ResultSet
    private Group getGroupFromResultSet(ResultSet resultSet) throws SQLException {
        Group group;

        // Ophalen van de velden uit het ResultSet
        int idGroup = resultSet.getInt("idGroup");
        int idTeacher = resultSet.getInt("idTeacher");
        String nameCourse = resultSet.getString("nameCourse");
        String nameGroup = resultSet.getString("nameGroup");
        int amountStudent = resultSet.getInt("amountStudent");
        String coordinatorUsername = resultSet.getString("coordinator");

        // Ophalen van de bijbehorende User
        User coordinator = userDAO.getUserByRole(coordinatorUsername);

        // Ophalen van de moeilijkheidsgraad (difficulty) van de Course
        int difficulty = resultSet.getInt("difficultyCourse");
        Course course = new Course(coordinator, nameCourse, difficulty);

        // Aanmaken van het Group-object
        group = new Group(idGroup, idTeacher, course, nameGroup, amountStudent, coordinator);
        return group;
    }
}
