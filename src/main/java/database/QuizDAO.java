package database;

import database.mysql.AbstractDAO;
import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO extends AbstractDAO {
    public Quiz makeQuiz(Question question)
    List<Quiz> vragenLijst = new ArrayList<>();
    String sql = "SELECT (nameQuiz, levelQuiz, amountQuestion) FROM Quiz WHERE nameQuiz = ?;";
    Quiz quiz;
		try {
        setupPreparedStatement(sql);
        preparedStatement.setString(1, quiz.getCourse());

        ResultSet resultSet = executeSelectStatement();
        while (resultSet.next()) {
            String naam = resultSet.getString("nameQuiz");
            String level = resultSet.getInt("levelQuiz");
            String aantal = resultSet.getInt("amountQuestion");
            quiz = new Quiz(naam, level, aantal);
            vragenLijst.add(quiz);
        }
    }
		catch (
    SQLException sqlFout){
        System.out.println("SQL fout " + sqlFout.getMessage());
    }
		return vragenLijst;
}
}
