
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author madhu
 */
public class SQL_Database implements DataStorage {

    private String DATABASE_URL;
    private String db_id;
    private String db_psw;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    @Override
    public void initializeDatabase() {
        DATABASE_URL = "jdbc:mysql://mis-sql.uhcl.edu/bhatm8403?useSSL=false";
        db_id = "bhatm8403";
        db_psw = "madhu123";

        connection = null;
        statement = null;
        resultSet = null;
    }

    @Override
    public String login(String id, String password) {
        initializeDatabase();

        try {
            connection = DriverManager.getConnection(DATABASE_URL, db_id, db_psw);
            statement = connection.createStatement();

            resultSet = statement.executeQuery("select * from student "
                    + "where loginId = '" + id + "'");

            if (resultSet.next()) {
                //id is present in student table - compare password
                if (password.equals(resultSet.getString("password"))) {
                    return "student";
                }
            } else {
                //id is not in student. Either in faculty or not present
                resultSet = statement.executeQuery("select * from faculty "
                        + "where loginId = '" + id + "'");

                if (resultSet.next()) {
                    //id is present in faculty table - compare password
                    if (password.equals(resultSet.getString("password"))) {
                        return "faculty";
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "internalError";

        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "loginFailed";
    }

    @Override
    public ArrayList<Course> getAllCourses() {
        initializeDatabase();
        ArrayList<Course> courses = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(DATABASE_URL, db_id, db_psw);
            statement = connection.createStatement();

            resultSet = statement.executeQuery("select * from course ");

            while (resultSet.next()) {
                courses.add(new Course(resultSet.getString("courseId"), resultSet.getString("instructor"),
                        resultSet.getInt("capacity"), resultSet.getInt("enrolled"),
                        resultSet.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("internalError");

        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return courses;
    }

    @Override
    public ArrayList<Course> getEnrolledCourses(String loginId, String type) {
        initializeDatabase();
        ArrayList<Course> courses = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(DATABASE_URL, db_id, db_psw);
            statement = connection.createStatement();

            if (type.equals("student")) {
                resultSet = statement.executeQuery("select * from course\n"
                        + "where courseId in\n"
                        + "(select course from enrolled\n"
                        + "where studentId = '" + loginId + "'\n"
                        + "and status = 'enrolled')");
            } else if (type.equals("faculty")) {
                resultSet = statement.executeQuery("select * from course\n"
                        + "where instructor = '" + loginId + "'");
            }

            while (resultSet.next()) {
                courses.add(new Course(resultSet.getString("courseId"), resultSet.getString("instructor"),
                        resultSet.getInt("capacity"), resultSet.getInt("enrolled"),
                        resultSet.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("internalError");

        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return courses;
    }

    @Override
    public String RegisterCourse(String courseId, String loginId) {
        initializeDatabase();

        try {
            connection = DriverManager.getConnection(DATABASE_URL, db_id, db_psw);
            statement = connection.createStatement();

            connection.setAutoCommit(false);

            //enroll in enrolled table
            int r = statement.executeUpdate("insert into enrolled values "
                    + "('" + loginId + "', '" + courseId + "', 'enrolled')");

            //increment enrolled count in course table for the course            
            r = statement.executeUpdate("update course\n"
                    + "set enrolled = enrolled + 1\n"
                    + "where courseId = '" + courseId + "'");

            //change status to closed if enrolled = capacity           
            r = statement.executeUpdate("update course\n"
                    + "set status = 'closed'\n"
                    + "where courseId = '" + courseId + "'\n"
                    + "and enrolled = capacity");

            connection.commit();
            connection.setAutoCommit(true);
            return ("Course registered successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            return ("internalError");
        } finally {
            try {
                //resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ArrayList<ClassSchedule> getClassSchedule(ArrayList<Course> courses) {
        initializeDatabase();
        ArrayList<ClassSchedule> schedules = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(DATABASE_URL, db_id, db_psw);
            statement = connection.createStatement();

            for (Course each : courses) {
                resultSet = statement.executeQuery("select * from course_schedule "
                        + "where courseId = '" + each.getCourseId() + "'");

                while (resultSet.next()) {
                    schedules.add(new ClassSchedule(resultSet.getString("courseId"), resultSet.getString("time"),
                            resultSet.getString("classroom"), each));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("internalError");

        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return schedules;
    }

    @Override
    public String dropCourse(String courseId, String loginId) {
        initializeDatabase();

        try {
            connection = DriverManager.getConnection(DATABASE_URL, db_id, db_psw);
            statement = connection.createStatement();

            connection.setAutoCommit(false);

            //dropped in enrolled table
            int r = statement.executeUpdate("update enrolled\n"
                    + "set status = 'dropped'\n"
                    + "where studentId = '" + loginId + "'\n"
                    + "and course = '" + courseId + "'");

            //decrement enrolled count in course table for the course            
            r = statement.executeUpdate("update course\n"
                    + "set enrolled = enrolled - 1\n"
                    + "where courseId = '" + courseId + "'");

            //change status to open if closed
            r = statement.executeUpdate("update course\n"
                    + "set status = 'open'\n"
                    + "where courseId = '" + courseId + "'\n"
                    + "and status = 'closed'");

            connection.commit();
            connection.setAutoCommit(true);
            return ("Course dropped successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            return ("internalError");
        } finally {
            try {
                //resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ArrayList<String> getStudents(String courseId) {
        initializeDatabase();
        ArrayList<String> students = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(DATABASE_URL, db_id, db_psw);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select studentId\n"
                    + "from enrolled\n"
                    + "where course = '" + courseId + "'\n"
                    + "and status = 'enrolled'");

            while (resultSet.next()) {
                students.add(resultSet.getString("studentId"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("internalError");

        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return students;
    }

}
