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
    private UserDAO userDAO;
    private CourseDAO courseDAO;
    private GroupDAO groupDAO;

// Constructors
    public ParticipationDAO(DBAccess dBaccess, UserDAO userDAO, CourseDAO courseDAO, GroupDAO groupDAO) {
        super(dBaccess);
        this.userDAO = userDAO;
        this.courseDAO = courseDAO;
        this.groupDAO = groupDAO;
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
        String sql = "INSERT INTO Participation (idUser, idCourse, idGroep) VALUES(?, ?, ?);";
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

// Participation object maken vanuit resultSet
    private Participation getParticipation(ResultSet resultSet) throws SQLException {
        UserDAO userDAO = new UserDAO(dbAccess);
        CourseDAO courseDAO = new CourseDAO(dbAccess, userDAO);
        GroupDAO groupDAO = new GroupDAO(dbAccess, userDAO);
        int idUser = resultSet.getInt("idUser");
        int idCourse = resultSet.getInt("idCourse");
        int idGroup = resultSet.getInt("idGroep");
        User user = userDAO.getOneById(idUser);
        Course course = courseDAO.getOneById(idCourse);
        Group group = groupDAO.getOneById(idGroup);
        return new Participation(user, course, group);
    }
}
