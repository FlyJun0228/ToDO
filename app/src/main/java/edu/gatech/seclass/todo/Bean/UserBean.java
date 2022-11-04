package edu.gatech.seclass.todo.Bean;


import cn.bmob.v3.BmobObject;

public class UserBean extends BmobObject {
    private String name;
    private String password;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }




}
