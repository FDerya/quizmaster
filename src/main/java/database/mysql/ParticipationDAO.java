//
// Dit model is gemaakt door Eline van Tunen, 500636756
//

package database.mysql;

import model.Course;
import model.Group;
import model.Participation;
import model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParticipationDAO extends AbstractDAO implements GenericDAO<Participation> {

    // Constructors
    public ParticipationDAO(DBAccess dBaccess) {
        super(dBaccess);
    }

    // Alle participations ophalen
    @Override
    public List<Participation> getAll() {
        List<Participation> resultList = new ArrayList<>();
        String sql = "SELECT * FROM Participation;";
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()){
                resultList.add(getParticipation(resultSet));
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return resultList;
    }
    public List<Participation> getParticipationPerCourse(int id){
        List<Participation> resultList = new ArrayList<>();
        String sql = "Select * From Participation WHERE idCourse = ? AND idGroup is null;";
        return getParticipationList(id, resultList, sql);
    }

    private List<Participation> getParticipationList(int id, List<Participation> resultList, String sql) {
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()){
                resultList.add(getParticipation(resultSet));
            }
        } catch (SQLException sqlFout){
            System.out.println("SQL error " + sqlFout.getMessage());
        }
        return resultList;
    }

    public List<Participation> getParticipationInGroup(Course course, Group group){
        List<Participation> resultList = new ArrayList<>();
        String sql = "Select * From Participation WHERE idCourse = ? AND idGroup = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, course.getIdCourse());
            preparedStatement.setInt(2, group.getIdGroup());
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()){
                resultList.add(getParticipation(resultSet));
            }
        } catch (SQLException sqlFout){
            System.out.println("SQL error " + sqlFout.getMessage());
        }
        return resultList;
    }

    public List<Participation> getGroupsPerCourse(int id) {
        List<Participation> resultList = new ArrayList<>();
        String sql = "SELECT * FROM Participation WHERE idCourse = ? AND idGroup is not null";
        return getParticipationList(id, resultList, sql);
    }

    public List<Participation> getParticipationByIdUser(int idUser) {
        List<Participation> resultList = new ArrayList<>();
        String sql = "SELECT * FROM participation WHERE idUser = ?;";
        return getParticipationList(idUser, resultList, sql);
    }

    // Participations ophalen op basis van ID
    @Override
    public Participation getOneById(int id) {
        String sql = "SELECT * FROM Participation WHERE idUser = ?;";
        Participation participation = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = executeSelectStatement()) {
                if (resultSet.next()) {
                    participation = getParticipation(resultSet);
                    return participation;
                }
            }
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return participation;
    }

    // Nieuwe participation opslaan
    @Override
    public void storeOne(Participation participation) {
        String sql = "INSERT INTO Participation (idUser, idCourse, idGroup) VALUES(?, ?, ?);";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, participation.getUser().getIdUser());
            preparedStatement.setInt(2, participation.getCourse().getIdCourse());
            preparedStatement.setInt(3, participation.getGroup().getIdGroup());
            executeManipulateStatement();
        } catch (SQLException sqlException){
            System.out.println("SQL fout " + sqlException.getMessage());
        }
    }

    public void storeOneWhereGroupIsNull(int idUser, int idCourse) {
        String sql = "INSERT INTO Participation (idUser, idCourse) VALUES(?, ?);";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, idUser);
            preparedStatement.setInt(2, idCourse);
            executeManipulateStatement();
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
    }

    public void updateGroup(Group group, Course course, User user) {
        String sql = "UPDATE Participation SET idGroup = ? WHERE idCourse = ? AND idUser = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, group.getIdGroup());
            preparedStatement.setInt(2, course.getIdCourse());
            preparedStatement.setInt(3, user.getIdUser());
            executeManipulateStatement();
        } catch (SQLException sqlException) {
            System.out.println("SQL error " + sqlException.getMessage());
        }
    }

    public void updateGroupToNull(Course course, User user) {
        String sql = "UPDATE Participation SET idGroup = null WHERE idCourse = ? AND idUser = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, course.getIdCourse());
            preparedStatement.setInt(2, user.getIdUser());
            executeManipulateStatement();
        } catch (SQLException sqlException) {
            System.out.println("SQL error " + sqlException.getMessage());
        }
    }

    public void deleteParticipation(int idUser, int idCourse) {
        String sql = "DELETE FROM participation WHERE idUser = ? AND idCourse = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, idUser);
            preparedStatement.setInt(2, idCourse);
            executeManipulateStatement();
        } catch (SQLException sqlException) {
            System.out.println("SQL error " + sqlException.getMessage());
        }
    }

    // Participation object maken vanuit resultSet
    private Participation getParticipation(ResultSet resultSet) throws SQLException {
        UserDAO userDAO = new UserDAO(dbAccess);
        CourseDAO courseDAO = new CourseDAO(dbAccess, userDAO);
        GroupDAO groupDAO = new GroupDAO(dbAccess, userDAO, courseDAO);
        int idUser = resultSet.getInt("idUser");
        int idCourse = resultSet.getInt("idCourse");
        int idGroup = resultSet.getInt("idGroup");
        User user = userDAO.getOneById(idUser);
        Course course = courseDAO.getOneById(idCourse);
        Group group = groupDAO.getOneById(idGroup);
        return new Participation(user, course, group);
    }
}
