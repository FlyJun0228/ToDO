package edu.gatech.seclass.todo.model;

public class InfoModel {

    private String num;
    public String province;
    public String city;

    public InfoModel(String num, String province, String city) {
        this.num = num;
        this.province = province;
        this.city = city;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
