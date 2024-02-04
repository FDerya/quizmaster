package database.mysql;

import model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UserDAO extends AbstractDAO implements GenericDAO<User> {

    public UserDAO(DBAccess dbAccess) {
        super(dbAccess);
    }

    // CREATE METHODS
    // Stores a user to the database from a given user. idUser is set because database uses auto-increment.
    public void storeOne(User user) {
        String sql = "INSERT INTO user(username, password, firstName, prefix, surname, role) VALUES (?,?,?,?,?,?) ;";
        try {
            setupPreparedStatementWithKey(sql);
            createUpdateUser(user);
            int idUser = executeInsertStatementWithKey();
            user.setIdUser(idUser);
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
    }

    // READ METHODS
    // Get all users from the database, sorted by surname first, then firstname.
    public List<User> getAll() {
        List<User> resultList = new ArrayList<>();
        String sql = "SELECT * FROM User ORDER BY surname, firstName;";
        return makeUserList(resultList, sql);
    }

    // Get all coordinators from databse
    public List<User> getAllCoordinators() {
        List<User> resultList = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE role = 'Coördinator' ORDER BY surname;";
        return makeUserList(resultList, sql);
    }

    // Search the database for a user with a given idUser. Returns that user if it's in the database.
    public User getOneById(int idUser) {
        String sql = "SELECT * FROM User WHERE idUser = ?";
        User user = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, idUser);
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                user = getUser(resultSet);
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return user;
    }

    // Needed to verify login credentials: you return an object User with a given userName,
    // where you can check its password.
    public User getOneByUsername(String userName) {
        String sql = "SELECT * FROM User WHERE username = ?";
        User user = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, userName);
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                user = getUser(resultSet);
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return user;
    }

    // UPDATE METHODS
    // Updates a given user
    public void updateOne(User user) {
        String sql = "UPDATE User SET username = ?, password = ?, firstName = ?, prefix = ?, surname = ?, role = ? WHERE idUser = ?;";
        try {
            setupPreparedStatement(sql);
            createUpdateUser(user);
            preparedStatement.setInt(7, user.getIdUser());
            executeManipulateStatement();
        } catch (SQLException sqlException) {
            System.out.println("SQL error " + sqlException.getMessage());
        }
    }

    // DELETE METHODS
    // Removes a user from the database
    public void removeOne(User user) {
        String sql = "DELETE FROM user WHERE idUser = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, user.getIdUser());
            executeManipulateStatement();
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
    }

    // Sets the parameters for the SQL statement, from the given user
    private void createUpdateUser(User user) throws SQLException {
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.setString(3, user.getFirstName());
        preparedStatement.setString(4, user.getPrefix());
        preparedStatement.setString(5, user.getSurname());
        preparedStatement.setString(6, user.getRole());
    }

    // Creates and returns a user from the database query.
    private static User getUser(ResultSet resultSet) throws SQLException {
        int idUser = resultSet.getInt("idUser");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String firstName = resultSet.getString("firstName");
        String prefix = resultSet.getString("prefix");
        String surname = resultSet.getString("surname");
        String role = resultSet.getString("role");
        return new User(idUser, username, password, firstName, prefix, surname, role);
    }

// Creates a List of users from a resultset and adds all users to that list.
    private List<User> makeUserList(List<User> resultList, String sql) {
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while(resultSet.next()) {
                User user = getUser(resultSet);
                resultList.add(user);
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return resultList;
    }
}

