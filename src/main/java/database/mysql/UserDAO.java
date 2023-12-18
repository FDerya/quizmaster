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

    // Methode om een lijst van alle gebruikers te krijgen vanuit de database.
    public List<User> getAll() {
        List<User> resultList = new ArrayList<>();
        String sql = "SELECT * FROM User;";

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

    // Methode om een enkele gebruiker op te vragen uit de SQL database, waarvan je de parameter id meegeeft.
    // Je krijgt je select statement terug in de vorm van een object User.
    public User getOneById(int idUser) {
        String sql = "SELECT * FROM User WHERE idUser = ?";
        User user = null;

        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, idUser);
            ResultSet resultSet = executeSelectStatement();

            if (resultSet.next()) {
//                String username = resultSet.getString("username");
//                String password = resultSet.getString("password");
//                String firstName = resultSet.getString("firstName");
//                String prefix = resultSet.getString("prefix");
//                String surname = resultSet.getString("surname");
//                String role = resultSet.getString("role");
                user = getUser(resultSet);
//            new User(idUser, username, password, firstName, prefix, surname, role);
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return user;
    }
    
    // Methode nodig bij het inloggen: het vraagt om een userName en geeft een object User terug, waarvan je met de
    // getters het wachtwoord kan controleren.
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

    // Methode om een object User (zonder userId, want de tabel User gebruikt auto-increment) toe te voegen aan SQL.
    public void storeOne(User user) {
        String sql = "INSERT INTO user(username, password, firstName, prefix, surname, role) VALUES (?,?,?,?,?,?) ;";

        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getPrefix());
            preparedStatement.setString(5, user.getSurname());
            preparedStatement.setString(6, user.getRole());
            int idUser = executeInsertStatementWithKey();
            user.setIdUser(idUser);
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
    }

    private static User getUser(ResultSet resultSet) throws SQLException {
        int idUser = resultSet.getInt("idUser");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String firstName = resultSet.getString("firstName");
        String prefix = resultSet.getString("prefix");
        String surname = resultSet.getString("surname");
        String role = resultSet.getString("role");
        User user = new User(idUser, username, password, firstName, prefix, surname, role);
        return user;
    }

}

