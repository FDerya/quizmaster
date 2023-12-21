//
// Dit model is gemaakt door Eline van Tunen, 500636756
//

package model;

public class Participation {
// Attributes
    private User user;
    private Course course;
    private Group group;

// Constructors
    public Participation(User user, Course course, Group group){
        this.user = user;
        this.course = course;
        this.group = group;
    }

// Getters & Setters
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }
    public Group getGroup() {
        return group;
    }
    public void setGroup(Group group) {
        this.group = group;
    }
}
