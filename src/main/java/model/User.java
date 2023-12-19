// Model user, gemaakt door Dennis Koelemeijer, studentnummer 500940711

package model;

public class User {
    // Attributes
    private int idUser;
    private String username;
    private String password;
    private String firstName;
    private String prefix;
    private String surname;
    private String role;
    public static User currentUser;

    // Constructors
    public User(int idUser, String username, String password, String firstName, String prefix, String surname, String role) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.prefix = prefix;
        this.surname = surname;
        this.role = role;
    }

    public User(String username, String password, String firstName, String prefix, String surname, String role) {
        this(0, username, password, firstName, prefix, surname, role);
    }

    // Methods
    @Override
    public String toString() {
        StringBuilder resultString = new StringBuilder("");
        resultString.append(firstName).append(" ");
        if (!prefix.equals("")) {
            resultString.append(prefix).append(" ");
        }
        resultString.append(surname).append(", ");
        resultString.append(role);
        return resultString.toString();
    }

    // Getters & Setters

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        User.currentUser = currentUser;
    }
}
