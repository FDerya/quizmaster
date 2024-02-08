package controller;

import org.junit.jupiter.api.Test;
import model.*;


import static org.junit.jupiter.api.Assertions.*;

class WelcomeControllerTest {
    //User user = UserDAO
int amountQuiz = 10;
int amountQuestion = 20;
int amountQuizEmpty = 0;
int amountQuestionEmpty = 0;
int amountQuizTest = 7;
String quizEmpty = "Er zijn geen quizzen met vragen";
String questionEmpty = "Deze quizzen hebben geen vragen";

    @Test
    void countAVG() {
        assert (true);
    }
    @Test
    void countAVGEmptyQuiz(){
        assertEquals(amountQuizEmpty, 0);
    }
    @Test
    void countAVGEmptyQuestion(){
        assertEquals(amountQuestionEmpty, 0);
    }
    @Test
    void countAVG
}