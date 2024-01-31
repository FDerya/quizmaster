package controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class FillOutQuizControllerTest {

    public String checkAnswers(String[] givenAnsers) {
        int counter = 0;
        String singular = "Je hebt de volgende vraag niet ingevuld: ";
        String plural = "Je hebt de volgende vragen niet ingevuld: ";
        String endOfSentence = "\nWeet je zeker dat je wilt inleveren?";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < givenAnsers.length; i++) {
            if (givenAnsers[i].isEmpty()) {
                stringBuilder.append(i + 1).append(", ");
                counter++;
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length()-2);
        }

        if (counter == 0) {
            return endOfSentence;
        } else if (counter == 1) {
            return singular + stringBuilder + endOfSentence;
        } else {
            return plural + stringBuilder + endOfSentence;
        }
    }

    String[] givenAnswersTwoNotGiven = {"A", "", "B", "C", ""};
    String[] givenAnswersOneNotGiven = {"A", "C", "B", "C", ""};
    String[] givenAnswersAllGiven = {"A", "D", "B", "C", "F"};

    @Test
    void checkAnswersTwoNotGiven() {
        String expected = "Je hebt de volgende vragen niet ingevuld: 2, 5" +
                "\nWeet je zeker dat je wilt inleveren?";
        assertEquals(expected, checkAnswers(givenAnswersTwoNotGiven));
    }

    @Test
    void checkAnswersOneNotGiven() {
        String expected = "Je hebt de volgende vraag niet ingevuld: 5" +
                "\nWeet je zeker dat je wilt inleveren?";
        assertEquals(expected, checkAnswers(givenAnswersOneNotGiven));
    }

    @Test
    void checkAnswersAllGiven() {
        String expected = "\nWeet je zeker dat je wilt inleveren?";
        assertEquals(expected, checkAnswers(givenAnswersAllGiven));
    }
}