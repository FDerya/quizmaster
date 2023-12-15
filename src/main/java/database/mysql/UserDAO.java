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
        String sql = "INSERT INTO User (loginUser, passwordUser, firstNameUser, prefixUser, surnameUser, roleUser) VALUES (?,?,?,?,?,?);";
        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setString(1,user.getLoginUser());
            preparedStatement.setString(2, user.getPasswordUser());
            preparedStatement.setString(3, user.getFirstNameUser());
            preparedStatement.setString(4, user.getPrefixUser());
            preparedStatement.setString(5, user.getSurnameUser());
            preparedStatement.setString(6,user.getRoleUser());
            int userId = executeInsertStatementWithKey();
            user.setIdUser(userId);
        } catch (SQLException sqlError) {
            System.out.println("SQL error: " + sqlError.getMessage());
        }
    }

    @Override
    public List<User> getAll() {
        List<User> resultList = new ArrayList<>();
        String sql = "SELECT * FROM Klant";
        User user;
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                int userId = resultSet.getInt("idUser");
                String loginUser = resultSet.getString("loginUser");
                String passwordUser = resultSet.getString("passwordUser");
                String firstNameUser = resultSet.getString("firstNameUser");
                String prefixUser = resultSet.getString("prefixUser");
                String surnameUser = resultSet.getString("surnameUser");
                String roleUser = resultSet.getString("roleUser");
                user = new User(userId, loginUser,passwordUser,firstNameUser,prefixUser,surnameUser,roleUser);
                resultList.add(user);
            }
        }
        catch (SQLException sqlError){
            System.out.println("SQL erorr: " + sqlError.getMessage());
        }
        return resultList;
    };
    @Override
    public User getOneById(int id) {
        String sql = "SELECT * FROM Klant WHERE klantnummer = ?";
        User user = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                String loginUser = resultSet.getString("loginUser");
                String passwordUser = resultSet.getString("passwordUser");
                String firstNameUser = resultSet.getString("firstNameUser");
                String prefixUser = resultSet.getString("prefixUser");
                String surnameUser = resultSet.getString("surnameUser");
                String roleUser = resultSet.getString("roleUser");
                user = new User(loginUser,passwordUser,firstNameUser,prefixUser,surnameUser,roleUser);
            }
        }
        catch (SQLException sqlError){
            System.out.println("SQL error: " + sqlError.getMessage());
        }
        return user;
    };
}
