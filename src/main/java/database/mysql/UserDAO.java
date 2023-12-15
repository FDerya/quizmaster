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

    @Override
    public void storeOne(User user) {
        String sql = "INSERT INTO User (username, password, firstName, prefix, surname, role) VALUES (?,?,?,?,?,?);";
        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setString(1,user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getPrefix());
            preparedStatement.setString(5, user.getSurname());
            preparedStatement.setString(6,user.getRole());
            int userId = executeInsertStatementWithKey();
            user.setIdUser(userId);
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
    }

    @Override
    public List<User> getAll() {
        List<User> resultList = new ArrayList<>();
        String sql = "SELECT * FROM User";
        User user;
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                int userId = resultSet.getInt("idUser");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String firstName = resultSet.getString("firstname");
                String prefix = resultSet.getString("prefix");
                String surname = resultSet.getString("surname");
                String role = resultSet.getString("role");
                user = new User(userId, username,password,firstName,prefix,surname,role);
                resultList.add(user);
            }
        }
        catch (SQLException sqlError){
            System.out.println("SQL erorr: " + sqlError.getMessage());
        }
        return resultList;
    }
    @Override
    public User getOneById(int id) {
        String sql = "SELECT * FROM User WHERE userId = ?";
        User user = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                String username = resultSet.getString("login");
                String password = resultSet.getString("password");
                String firstName = resultSet.getString("firstName");
                String prefix = resultSet.getString("prefix");
                String surname = resultSet.getString("surname");
                String role = resultSet.getString("role");
                user = new User(username,password,firstName,prefix,surname,role);
            }
        }
        catch (SQLException sqlError){
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return user;
    }
}
