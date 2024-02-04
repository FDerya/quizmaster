package controller;

import model.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ManageUsersControllerTest {
    public List<User> testUsers() {
        // Creating testdata and adding it to an ArrayList
        User t1 = new User (1, "t1", "t1", "t1", "t1", "t1", "Student");
        User t2 = new User(2, "t2", "t2", "t2", "t2", "t2", "Student");
        User t3 = new User(3, "t3", "t3", "t3", "t3", "t3", "Student");
        User t4 = new User(4, "t4", "t4", "t4", "t4", "t4", "Student");
        User t5 = new User(5, "t5", "t5", "t5", "t5", "t5", "Functioneel Beheerder");
        User t6 = new User(6, "t6", "t6", "t6", "t6", "t6", "Administrator");
        User t7 = new User(7, "t7", "t7", "t7", "t7", "t7", "Docent");
        User t8 = new User(8, "t8", "t8", "t8", "t8", "t8", "Docent");
        User t9 = new User(9, "t9", "t9", "t9", "t9", "t9", "Docent");
        User t10 = new User(10, "t10", "t10", "t10", "t10", "t10", "Student");

        List<User> testUsers = new ArrayList<>();
        testUsers.add(t1);
        testUsers.add(t2);
        testUsers.add(t3);
        testUsers.add(t4);
        testUsers.add(t5);
        testUsers.add(t6);
        testUsers.add(t7);
        testUsers.add(t8);
        testUsers.add(t9);
        testUsers.add(t10);
        return testUsers;
    }

    // Handles edge cases: what if no user is selected, what if there is only one of a given type, what if there are more.
    public String doCounterRole(User user, List<User> userList) {
        if (user == null) {
            return "Er is geen gebruiker geselecteerd";
        } else {
            int counter = (int) userList.stream().filter(roleUsers -> roleUsers.getRole().equals(user.getRole())).count();
            return counter == 1 ? "Er is " + counter + " " + user.getRole().toLowerCase() :
                    "Er zijn " + counter + " " + getRoleTextInPlural(user.getRole()).toLowerCase();
        }
    }

    private String getRoleTextInPlural(String role) {
        String pluralRole = "";
        switch (role) {
            case "Student":
                pluralRole = "Studenten";
                break;
            case "Administrator":
                pluralRole = "Administratoren";
                break;
            case "Coördinator":
                pluralRole = "Coördinatoren";
                break;
            case "Functioneel Beheerder":
                pluralRole = "Functioneel Beheerders";
                break;
            case "Docent":
                pluralRole = "Docenten";
                break;
        }
        return pluralRole;
    }
    @Test
    void doCounterRoleForStudentsInUserlist() {
        List<User> userList = testUsers();
        String expected = "Er zijn 5 studenten";
        assertEquals(expected, doCounterRole(userList.get(0), userList));
    }

    @Test
    void doCounterRoleForAdminsInUserList() {
        List<User> userList = testUsers();
        String expected = "Er is 1 administrator";
        assertEquals(expected, doCounterRole(userList.get(5), userList));
    }

    @Test
    void doCounterRoleForTeachersInUserList() {
        List<User> userList = testUsers();
        String expected = "Er zijn 3 docenten";
        assertEquals(expected, doCounterRole(userList.get(6), userList));
    }

    @Test
    void doCounterRoleForNoUserSelected() {
        List<User> userList = testUsers();
        String expected = "Er is geen gebruiker geselecteerd";
        assertEquals(expected, doCounterRole(null, userList));
    }
}