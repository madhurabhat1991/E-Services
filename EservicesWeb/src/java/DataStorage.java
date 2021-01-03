
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
public interface DataStorage {

    void initializeDatabase();

    String login(String id, String password);

    ArrayList<Course> getAllCourses();

    ArrayList<Course> getEnrolledCourses(String loginId, String type);

    String RegisterCourse(String courseId, String loginId);

    ArrayList<ClassSchedule> getClassSchedule(ArrayList<Course> courses);

    String dropCourse(String courseId, String loginId);
    
    ArrayList<String> getStudents(String courseId);
}
