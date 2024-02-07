package controller;

import model.Course;
import model.Group;
import model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManageGroupsControllerTest {

    // Method to update labels based on selected group and counter
    protected String updateLabels(Group selectedGroup, Long counter) {
        String label;
        if (counter == null) {
            label = "Selecteer een groep.";
        } else if (counter == 1) {
            label = "1 groep in " + selectedGroup.getCourse().getNameCourse();
        } else {
            label = counter + " groepen in " + selectedGroup.getCourse().getNameCourse();
        }

        return label;
    }

    // Define user, course, and test group for testing
    User coordinator = new User("SmiFra", "H6%&df3L",
            "Frank", "de", "smit", "docent");
    Course course = new Course(coordinator, "Algebra", "medium");
    User teacher = new User("JanDo", "P@ssw0rd", "Jan", "", "Doe", "student");
    Group testGroup = new Group(999, course, "TestGroup", 25, teacher);

    // Test when selected group is null
    @Test
    void checkGroupIsNull() {
        String expected = "Selecteer een groep.";
        assertEquals(expected, updateLabels(null, null));
    }

    // Test when counter is zero
    @Test
    void checkGroupIsZero() {
        String expected = "0 groepen in Algebra";
        assertEquals(expected, updateLabels(testGroup, 0L));
    }

    // Test when counter is one
    @Test
    void checkCountIsOne() {
        String expected = "1 groep in Algebra";
        assertEquals(expected, updateLabels(testGroup, 1L));
    }

    // Test when counter is more than one
    @Test
    void checkCountIsMoreThenOne() {
        String expected = "3 groepen in Algebra";
        assertEquals(expected, updateLabels(testGroup, 3L));
    }
}