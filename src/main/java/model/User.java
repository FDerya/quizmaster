package model;
public class User {
    // Attributes
    private int idUser;
    private String loginUser;
    private String passwordUser;
    private String firstNameUser;
    private String prefixUser;
    private String surnameUser;
    private String roleUser;

    // Constructors
    public User (int idUser, String loginUser, String passwordUser, String firstNameUser, String prefixUser, String surnameUser, String roleUser) {
        this.idUser = idUser;
        this.loginUser = loginUser;
        this.passwordUser = passwordUser;
        this.firstNameUser = firstNameUser;
        this.prefixUser = prefixUser;
        this.surnameUser = surnameUser;
        this.roleUser = roleUser;
    }

    public User(String loginUser, String passwordUser, String firstNameUser, String prefixUser, String surnameUser, String roleUser) {
        this(0, loginUser, passwordUser, firstNameUser, prefixUser, surnameUser, roleUser);
    }

    // Methods
    @Override
    public String toString() {
        return String.format("Gebruiker met als rol %s", roleUser);
    }

    // Getters & Setters

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getPasswordUser() {
        return passwordUser;
    }

    public void setPasswordUser(String passwordUser) {
        this.passwordUser = passwordUser;
    }

    public String getFirstNameUser() {
        return firstNameUser;
    }

    public void setFirstNameUser(String firstNameUser) {
        this.firstNameUser = firstNameUser;
    }

    public String getPrefixUser() {
        return prefixUser;
    }

    public void setPrefixUser(String prefixUser) {
        this.prefixUser = prefixUser;
    }

    public String getSurnameUser() {
        return surnameUser;
    }

    public void setSurnameUser(String surnameUser) {
        this.surnameUser = surnameUser;
    }

    public String getRoleUser() {
        return roleUser;
    }

    public void setRoleUser(String roleUser) {
        this.roleUser = roleUser;
    }
}