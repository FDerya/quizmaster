package controller;

import model.Course;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ManageCoursesControllerTest {

    public String doStudentCount(Course course, List<Integer[]> participationList){
        if (course == null) {
            return "Selecteer een cursus.";
        } else {
            int counter = participationList.size();
            if (counter == 0){
                return "Voor de cursus " + course + " zijn nog geen studenten ingeschreven.";
            } else {
                return "Voor de cursus " + course + " zijn studenten ingeschreven.";
            }
        }
    }

    Course testCourse = new Course(null, "testCourse", "Beginner");
    Course testCourseNull = null;
    List<Integer[]> emptyParticipation = new ArrayList<>();
    List<Integer[]> filledParticipation = Arrays.asList(new Integer[] {1}, new Integer[]{2});


    @Test
    void checkCourseIsNull() {
        String expected = "Selecteer een cursus.";
        assertEquals(expected, doStudentCount(testCourseNull, filledParticipation));
    }

    @Test
    void checkCounterIsZero() {
        String expected = "Voor de cursus " + testCourse + " zijn nog geen studenten ingeschreven.";
        assertEquals(expected, doStudentCount(testCourse, emptyParticipation));
    }

    @Test
    void checkCounterIsAboveZero() {
        String expected = "Voor de cursus " + testCourse + " zijn studenten ingeschreven.";
        assertEquals(expected, doStudentCount(testCourse, filledParticipation));
    }
}
