package database.mysql;

import model.Question;
import model.Quiz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO extends AbstractDAO implements GenericDAO<Question> {

    public QuestionDAO(DBAccess dbAccess) {
        super(dbAccess);
    }

    @Override
    public void storeOne(Question question) {
        String sql = "INSERT INTO question(idQuiz,question,answerRight, answerWrong1, answerWrong2, answerWrong3) " + "VALUES (?, ?, ?, ?, ?, ?);";
        try {
            setupPreparedStatementWithKey(sql);
            // Assuming getIdQuiz returns a String
            preparedStatement.setInt(1, question.getQuiz().getIdQuiz());
            preparedStatement.setString(2, question.getQuestion());
            preparedStatement.setString(3, question.getAnswerRight());
            preparedStatement.setString(4, question.getAnswerWrong1());
            preparedStatement.setString(5, question.getAnswerWrong2());
            preparedStatement.setString(6, question.getAnswerWrong3());
            question.setIdQuestion(executeInsertStatementWithKey());
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
    }
     //De methode die wordt gebruikt om alle vragen uit de database te halen
    @Override
    public List<Question> getAll() {
        String sql = "SELECT * FROM question;";
        List<Question> questions = new ArrayList<>();
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                questions.add(getQuestionFromResultSet(resultSet));
            }
            if (questions.isEmpty()) {
                System.out.println("Er zijn geen vragen in de database");
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return questions;
    }
    // Methode die wordt gebruikt om een specifieke vraag op ID op te halen
    @Override
    public Question getOneById(int questionId) {
        Question question = null;
        String sql = "SELECT * FROM question WHERE idQuestion = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, questionId);
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                question = getQuestionFromResultSet(resultSet);
            } else {
                System.out.println("Er zijn geen vragen met deze vraag ID in de database");
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return question;
    }

    // Helpermethode die een vraagobject maakt met de resultaatset
    private Question getQuestionFromResultSet(ResultSet resultSet) throws SQLException {
        int idQuestion = resultSet.getInt("idQuestion");
        int idQuiz = resultSet.getInt("idQuiz");
        String question = resultSet.getString("question");
        String answerRight = resultSet.getString("answerRight");
        String answerWrong1 = resultSet.getString("answerWrong1");
        String answerWrong2 = resultSet.getString("answerWrong2");
        String answerWrong3 = resultSet.getString("answerWrong3");

        // Vraagt het Quiz-object op ID op met QuizDAO
        QuizDAO quizDAO = new QuizDAO(dbAccess);
        Quiz quiz = quizDAO.getOneById(idQuiz);

        // Maakt het object Vraag met de gegenereerde informatie en stuurt het terug
        return new Question(idQuestion, quiz, question, answerRight, answerWrong1, answerWrong2, answerWrong3);
    }


}
