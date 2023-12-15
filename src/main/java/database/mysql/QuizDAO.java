package database.mysql;

import database.mysql.AbstractDAO;
import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class QuizDAO {
}
/*
public class QuizDAO extends AbstractDAO implements GenericDAO{

    List<Quiz> vragenLijst = new ArrayList<>();
    String sql = "SELECT (nameQuiz, levelQuiz, amountQuestion) FROM Quiz WHERE nameQuiz = ?;";
    public List<Quiz> getAll() {
    Quiz quiz;
		try {
        setupPreparedStatement(sql);
        preparedStatement.setString(1, quiz.getCourse());

        ResultSet resultSet = executeSelectStatement();
        while (resultSet.next()) {
            String naam = resultSet.getString("nameQuiz");
            int level = resultSet.getInt("levelQuiz"));
            int aantal = resultSet.getInt("amountQuestion"));
            quiz = new Quiz(quiz.getCourse(), level, aantal);
            vragenLijst.add(quiz);
        }
    }
		catch (
    SQLException sqlFout){
        System.out.println("SQL fout " + sqlFout.getMessage());
    }
		return vragenLijst;
    }
}*/

