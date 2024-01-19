package database.mysql;

import model.Course;
import model.Question;
import model.Quiz;
import model.User;
import view.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO extends AbstractDAO implements GenericDAO<Question> {
    private QuizDAO quizDAO;
    public QuestionDAO(DBAccess dbAccess) {
        super(dbAccess);
        this.quizDAO = new QuizDAO(Main.getDBaccess());
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


    public void removeOne(Question selectedQuestion) {
        String sql = "DELETE FROM question WHERE idQuestion = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, selectedQuestion.getIdQuestion());
            executeManipulateStatement();
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }

    }

    public int getQuestionCountForQuiz(Quiz quiz) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM question WHERE idQuiz = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, quiz.getIdQuiz());
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return count;
    }


    public List<Question> getQuestionsForQuiz(Quiz quiz) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM question WHERE idQuiz = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, quiz.getIdQuiz());
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                questions.add(getQuestionFromResultSet(resultSet));
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return questions;
    }

    public void deleteOne(Question question) {
        String sql = "DELETE FROM question WHERE idQuestion = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, question.getIdQuestion());
            executeManipulateStatement();
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
    }

    public int getLastQuestionId() {
        int lastQuestionId = 0;
        String sql = "SELECT MAX(idQuestion) FROM question;";
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                lastQuestionId = resultSet.getInt(1);
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return lastQuestionId;
    }

    public void updateQuestion(Question question) {
        String sql = "UPDATE question SET idQuiz=?, question=?, answerRight=?, answerWrong1=?, answerWrong2=?, answerWrong3=? WHERE idQuestion=?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, question.getQuiz().getIdQuiz());
            preparedStatement.setString(2, question.getQuestion());
            preparedStatement.setString(3, question.getAnswerRight());
            preparedStatement.setString(4, question.getAnswerWrong1());
            preparedStatement.setString(5, question.getAnswerWrong2());
            preparedStatement.setString(6, question.getAnswerWrong3());
            preparedStatement.setInt(7, question.getIdQuestion());
            executeManipulateStatement();
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
    }

    public List<Question> getQuestionsForUser(int userId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT q.* " +
                "FROM question q " +
                "JOIN quiz z ON q.idQuiz = z.idQuiz " +
                "JOIN course c ON z.idCourse = c.idCourse " +
                "JOIN user u ON c.idUser = u.idUser " +
                "WHERE u.idUser = ?;";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                questions.add(getQuestionFromResultSet(resultSet));
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return questions;
    }



    public List<Quiz> getQuizNamesForUser(int userId) {
        List<Quiz> quizNames = new ArrayList<>();
        String sql = "SELECT q.idQuiz, q.nameQuiz, q.levelQuiz, q.amountQuestion, c.nameCourse " +
                "FROM quiz q " +
                "JOIN course c ON q.idCourse = c.idCourse " +
                "WHERE EXISTS (SELECT 1 FROM course c2 WHERE c2.idCourse = c.idCourse AND c2.idUser = ?)";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                String nameQuiz = resultSet.getString("nameQuiz");
                Quiz quiz = quizDAO.getOneByName(nameQuiz);
                quizNames.add(quiz);
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL fout " + sqlException.getMessage());
        }
        return quizNames;
    }


    // Retrieve the course name based on the question ID
    public String getCourseNameByQuestionId(int questionId) {
        String sql = "SELECT c.nameCourse " +
                "FROM quiz q " +
                "JOIN course c ON q.idCourse = c.idCourse " +
                "JOIN question qu ON q.idQuiz = qu.idQuiz " +
                "WHERE qu.idQuestion = ?;";

        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, questionId);
            ResultSet resultSet = executeSelectStatement();

            if (resultSet.next()) {
                return resultSet.getString("nameCourse");
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL error" + sqlException.getMessage());
        }

        return null; // Return null if course name is not found
    }

    public String getCourseNameByQuizName(String quizName) {
        String sql = "SELECT c.nameCourse " +
                "FROM quiz q " +
                "JOIN course c ON q.idCourse = c.idCourse " +
                "WHERE q.nameQuiz = ?;";

        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, quizName);
            ResultSet resultSet = executeSelectStatement();

            if (resultSet.next()) {
                return resultSet.getString("nameCourse");
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL error" + sqlException.getMessage());
        }

        return null; // Return null if course name is not found
    }


}
