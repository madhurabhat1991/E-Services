
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.faces.context.FacesContext;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author madhu
 */
public class Account {

    private String loginId;
    private String type;
    private Course selectedCourse;
    private String confirmation;
    DataStorage data = new SQL_Database();

    public Account(String loginId, String type) {
        this.loginId = loginId;
        this.type = type;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Course getSelectedCourse() {
        return selectedCourse;
    }

    public void setSelectedCourse(Course selectedCourse) {
        this.selectedCourse = selectedCourse;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public DataStorage getData() {
        return data;
    }

    public void setData(DataStorage data) {
        this.data = data;
    }

    public ArrayList<Course> getAllCourses() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("internalError");
        }

        return data.getAllCourses();
    }

    public String selectCourse(Course course) {
        selectedCourse = course;
        return "coursePage";
    }

    public ArrayList<Course> getEnrolledCourses() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("internalError");
        }

        return data.getEnrolledCourses(loginId, type);
    }

    public String RegisterCourse() {
        if (type.equals("student")) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (Exception e) {
                e.printStackTrace();
                return ("internalError");
            }

            //selected course should not be in enrolled courses list
            for (Course each : getEnrolledCourses()) {
                if (each.getCourseId().equals(getSelectedCourse().getCourseId())) {
                    //course already registered
                    confirmation = "Course is already registered!";
                    return "registerConfirmation";
                }
            }

            //selected course should not be closed
            if (getSelectedCourse().getStatus().equals("closed")) {
                confirmation = "Course is closed. Cannot register!";
                return "registerConfirmation";
            }

            confirmation = getData().RegisterCourse(getSelectedCourse().getCourseId(), getLoginId());
            return "registerConfirmation";
        }
        return "";
    }

    public ArrayList<ClassSchedule> getClassSchedule() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("internalError");
        }

        return data.getClassSchedule(getEnrolledCourses());
    }

    public String dropCourse(String courseId) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            return ("internalError");
        }

        confirmation = data.dropCourse(courseId, loginId);
        return "dropConfirmation";
    }

    public String viewBill() {
        DecimalFormat df = new DecimalFormat("$##.00");
        return df.format(1000 * getEnrolledCourses().size());
    }
    
    public ArrayList<String> getStudents(String courseId){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("internalError");
        }
        
        return data.getStudents(courseId);
    }

    public String signOut() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index.xhtml";
    }

}
