package database.mysql;
// Bianca Duijvesteijn, studentnummer 500940421

import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO extends AbstractDAO implements GenericDAO<Group> {
    private UserDAO userDAO;

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

    // Methode om een specifieke groep op te halen op basis van ID
    @Override
    public Group getOneById(int id) {
        String sql = "SELECT * FROM group WHERE userId = ?";
        Group group = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = executeSelectStatement()) {
                if (resultSet.next()) {
                    return getGroupFromResultSet(resultSet);
                }
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return group;
    }


    public void storeOne(Group group) {
        //Controleert of alle gegevens zijn ingevuld
        if (group.getGroupName() == null || group.getCourseName() == null || group.getAmountStudent() < 0 || group.getUserName() == null) {
            System.out.println("Ongeldige gegevens. Opslaan geannuleerd.");
            return;
        }
        String sql = "INSERT INTO `group` (groupName, courseName, amountStudent, userName)" +
                " VALUES (?, ?, ?, ?);";
        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setString(1, group.getGroupName());
            preparedStatement.setString(2, group.getCourseName().getNameCourse());
            preparedStatement.setInt(3, group.getAmountStudent());
            preparedStatement.setString(4, String.valueOf(group.getUserName().getIdUser()));
            int primaryKey = executeInsertStatementWithKey();
            group.setIdGroup(primaryKey);

            executeManipulateStatement();
        } catch (SQLException sqlFout) {
            System.out.println(sqlFout.getMessage());
        }
    }

    private Group getGroupFromResultSet(ResultSet resultSet) throws SQLException {
        Group group;

        int idGroup = resultSet.getInt("idGroup");
        int idTeacher = resultSet.getInt("idTeacher");
        String nameCourse = resultSet.getString("nameCourse");
        String nameGroup = resultSet.getString("nameGroup");
        int amountStudent = resultSet.getInt("amountStudent");
        int coordinatorUserId = resultSet.getInt("userId");
        String coordinatorUsername = resultSet.getString("userName");

        // Ophalen van de bijbehorende User
        User coordinator = userDAO.getOneById(coordinatorUserId);
        if (coordinator == null) {
            // Als we de gebruiker niet kunnen vinden op basis van userId, proberen we het op basis van gebruikersnaam
            coordinator = userDAO.getAll().stream()
                    .filter(user -> user.getUsername().equals(coordinatorUsername))
                    .findFirst()
                    .orElse(null);
        }
        String difficulty = resultSet.getString("difficultyCourse");
        Course course = new Course(coordinator, nameCourse, difficulty);

        group = new Group(idGroup, idTeacher, course, nameGroup, amountStudent, coordinator);
        return group;
    }
}
