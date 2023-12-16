package database.mysql;
//Bianca Duijvesteijn, studentnummer 500940421

import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO extends AbstractDAO implements GenericDAO<Group> {
    private static UserDAO userDAO;

    public GroupDAO(DBAccess dBaccess, UserDAO userDAO) {
        super(dBaccess);
        this.userDAO = userDAO;
    }

    @Override
    public List getAll() {
        List<Group> resultList = new ArrayList<>();
        String sql = "SELECT * FROM Group";
        Group group;
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                group = getGroupFromResultSet(resultSet);
                resultList.add(group);
            }
        }
        catch (SQLException sqlError){
            System.out.println("SQL erorr: " + sqlError.getMessage());
        }
        return resultList;

    }

    @Override
    public Group getOneById(int id) {
        String sql = "SELECT * FROM group WHERE userId = ?";
        Group group = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                group = getGroupFromResultSet(resultSet);
            }
        }
        catch (SQLException sqlError){
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return group;
    }


    @Override
    public void storeOne(Group group) {
        String sql = "INSERT INTO `group` (naamGroep, naamCursus, aantalStudenten, gebruikersInlogNaam)" +
                " VALUES (?, ?, ?, ?);";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, group.getGroupName());
            preparedStatement.setString(2, group.getCourseName().getNameCourse());
            preparedStatement.setInt(3, group.getNumberOfStudents());
            preparedStatement.setString(4, String.valueOf(group.getUserName().getIdUser()));

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
        String coordinatorUsername = resultSet.getString("coordinator");

        User coordinator = userDAO.getUserByUsername(coordinatorUsername);
        Course course = null;
        int difficulty = course.getDifficultyCourse();
        course = new Course(coordinator, nameCourse, difficulty);

        group = new Group(idGroup, idTeacher, course, nameGroup, amountStudent, coordinator);
        return group;
    }
}