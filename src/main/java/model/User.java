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

    // Constructors: all args, een met zelfgekozen password (voor csv bestand) en een met gegenereerd password
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

    public User(String username, String firstName, String prefix, String surname, String role) {
        this(0, username, getRandomPassword(), firstName, prefix, surname, role);
    }

    // Methods
    @Override
    public String toString() {
        return getFullName() + ", " + getRole();
//        String.format("%s %s %s, %s", firstName, (getPrefix() == null) ? "" : getPrefix(), getSurname(), getRole());
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

    public String getFullName() {
        return String.format("%s %s %s", firstName, (getPrefix() == null) ? "" : getPrefix(), getSurname());
    }

    private static char getRandomChar() {
        int randomInt = (int) (Math.random() * 93) + 33; // Lijken magic numbers, maar char 33 t/m 126 zijn speciale tekens
        return (char) randomInt;
    }

    private static String getRandomPassword() {
        int numberOfCharsInPassword = 10;
        StringBuilder randomPassword = new StringBuilder();
        for (int i = 0; i < numberOfCharsInPassword; i++) {
            randomPassword.append(getRandomChar());
        }
        return randomPassword.toString();
    }
}
