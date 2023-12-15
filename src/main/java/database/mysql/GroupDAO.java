package database.mysql;
//Bianca Duijvesteijn, studentnummer 500940421

import model.Course;
import model.Group;
import model.User;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {
    public GroupDAO(DBAccess dBaccess) {
        super(dBaccess);
    }
    public void slaGroupOp(Group group){
        String sql = "INSERT INTO group(naamGroep, naamCursus, aantalStudenten, gebruikersInlogNaam)" +
                " VALUES (?, ?, ?, ?);";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, group.getGroupName());

            // Maak een instantie van Course
            Course course = new Course();
            preparedStatement.setString(2, course.getCourseName());

            preparedStatement.setInt(3, group.getNumberOfStudents());

            // Maak een instantie van User
            User user = new User();
            preparedStatement.setString(4, user.getUserName());

            executeManipulateStatement();

        } catch (SQLException sqlFout) {
            System.out.println(sqlFout.getMessage());
        }
    }


    public List<Group> getUsersInGroup(User user) {
        List<Group> groupList = new ArrayList<>();

        String sql = "SELECT groep.naamGroep, cursus.naamCursus, groep.aantalStudenten, " +
                "CONCAT(user.VoornaamGebruiker, ' ', user.tussenvoegselgebruiker, ' ', user.achternaamGebruiker) as UserName " +
                "FROM groep JOIN user ON groep.gebruikersInlogNaam = user.gebruikerInlogNaam " +
                "JOIN cursus ON groep.naamCursus = groep.naamCursus";

        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                String groupName = resultSet.getString("naamGroep");
                String courseName = resultSet.getString("naamCursus");
                int numberOfStudents = resultSet.getInt("aantalStudenten");
                String userName = resultSet.getString("UserName");

                System.out.println("Groupname: " + groupName);

                Group group = new Group(groupName, courseName, numberOfStudents, userName);

                // Gebruik een UserDAO-methode om gebruikers op te halen op basis van de groepsnaam
                UserDAO userDAO = new UserDAO(); // Veronderstel dat je een UserDAO-klasse hebt
                List<User> usersInGroup = userDAO.getUsersByGroupName(groupName);
                group.setUserName(usersInGroup);
                groupList.add(group);
            }
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return groupList;
    }

    public List<Group> getListOfGroups(Group group) {
        List<Group> listOfGroups = new ArrayList<>();
        String sql = "SELECT * FROM groep WHERE aantalStudenten > 0;";
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();

            while (resultSet.next()) {
                String groupName = resultSet.getString("naamGroep");
                String courseName = resultSet.getString("naamCursus");
                String numberOfStudents = resultSet.getString("aantalStudenten");

                Group group = makeListOfGroups(groupName);
                listOfGroups.add(group);
            }
        } catch (SQLException foutmelding) {
            System.out.println(foutmelding.getMessage());
        }
        return listOfGroups;
    }
    public Group makeListOfGroups(String groupName, String courseName, int numberOfStudents, String userName) {
        Course course = new Course();  // Maak een nieuw Course-object
        String actualCourseName = Course.getCourseName(courseName);  // Roep de methode aan op het Course-object
        return new Group(groupName, actualCourseName, numberOfStudents, new User().getUserName(userName));
    }


}
