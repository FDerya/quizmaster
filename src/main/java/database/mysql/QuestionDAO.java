package database.mysql;

import model.Question;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO extends AbstractDAO implements GenericDAO<Question> {

    public QuestionDAO (DBAccess dbAccess) {super(dbAccess);}

    @Override
    public void storeOne(Question question) {
        String sql =  "INSERT INTO question(nameQuiz, question, answerGood, answerWrong1, answerWrong2, answerWrong3) " +
                "VALUES(?, ?, ?, ?, ?, ?);";
        try {

                setupPreparedStatementWithKey(sql);
                preparedStatement.setString(1, question.getnameQuiz());
                preparedStatement.setString(2, question.getQuestionText());
                preparedStatement.setString(3, question.getAnswerRight());
                preparedStatement.setString(4, question.getAnswerWrong1());
                preparedStatement.setString(5, question.getAnswerWrong2());
                preparedStatement.setString(6, question.getAnswerWrong3());
                question.setIdQuestion(executeInsertStatementWithKey());

            }catch(SQLException sqlException){
                System.out.println(sqlException.getMessage());
            }
        }

    @Override
    public ArrayList<Question> getAll() {
        String sql = "SELECT * FROM Question;";
        ArrayList<Question> questions = new ArrayList<>();

        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                int idQuestion = resultSet.getInt(1);
                String nameQuiz = resultSet.getString(2);
                String questionText = resultSet.getString(3);
                String getAnswerRight = resultSet.getString(4);
                String getAnswerWrong1 = resultSet.getString(5);
                String getAnswerWrong2 = resultSet.getString(6);
                String getAnswerWrong3 = resultSet.getString(7);
                questions.add(new Question(idQuestion, nameQuiz, questionText,
                        getAnswerRight, getAnswerWrong1, getAnswerWrong2, getAnswerWrong3));
            }
            if (questions.isEmpty()) {
                System.out.println("Er zijn geen vragen in de database");
            }
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
        return questions;
    }

    @Override
    public Question getOneById(int questionId) {

        Question question = null;
        String sql = "SELECT * FROM question WHERE idQuestion = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, questionId);
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                int idQuestion = resultSet.getInt(1);
                String nameQuiz = resultSet.getString(2);
                String questionText = resultSet.getString(3);
                String answerRight = resultSet.getString(4);
                String getAnswerWrong1 = resultSet.getString(5);
                String getAnswerWrong2 = resultSet.getString(6);
                String getAnswerWrong3 = resultSet.getString(7);
                question = new Question(idQuestion, nameQuiz, questionText,
                        answerRight, getAnswerWrong1, getAnswerWrong2, getAnswerWrong3);
            } else {
                System.out.println("Er zijn geen vragen met deze vraag ID in de database");
            }
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
        return question;
    }

}
