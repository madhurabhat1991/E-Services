/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author madhu
 */
@Named(value = "searchCourse")
@SessionScoped
public class SearchCourse implements Serializable {

    private String inputCourseId;
    private ArrayList<Course> searchCourses;

    /**
     * Creates a new instance of SearchCourse
     */
    public SearchCourse() {
    }

    public String getInputCourseId() {
        return inputCourseId;
    }

    public void setInputCourseId(String inputCourseId) {
        this.inputCourseId = inputCourseId;
    }

    public ArrayList<Course> getSearchCourses() {
        return searchCourses;
    }

    public void setSearchCourses(ArrayList<Course> searchCourses) {
        this.searchCourses = searchCourses;
    }

    public String searchCourse(Account account) {
        searchCourses = new ArrayList<>();
        for (Course each : account.getAllCourses()) {
            if (each.getCourseId().toLowerCase().contains(inputCourseId.toLowerCase())) {
                searchCourses.add(each);
            }
        }
        setInputCourseId("");
        return "searchCourse";
    }

}
