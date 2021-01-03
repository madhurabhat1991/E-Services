/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author madhu
 */
@Named(value = "login")
@SessionScoped
public class Login implements Serializable {

    private String loginId;
    private String password;
    private Account account;
    private String type;
    DataStorage data = new SQL_Database();

    /**
     * Creates a new instance of Login
     */
    public Login() {
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String login() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            return ("internalError");
        }

        type = data.login(loginId, password);
        //outcomes - student, faculty, loginFailed, internalError

        if (type.equals("student") || type.equals("faculty")) {
            if (type.equals("student")) {
                account = new Student(loginId, type);
            } else if (type.equals("faculty")) {
                account = new Faculty(loginId, type);
            }

            return "main";
        }
        return type;
        //outcomes - main, loginFailed, internalError
    }

}
