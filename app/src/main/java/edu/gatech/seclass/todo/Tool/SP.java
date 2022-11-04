package edu.gatech.seclass.todo.Tool;

import android.content.Context;
import android.content.SharedPreferences;

public class SP {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    public SP(Context context){
        this.context =  context;
        sharedPreferences = context.getSharedPreferences("User",Context.MODE_PRIVATE);
    }
        public void SaveUser(String name,String password,Boolean rem,Boolean login){//将用户名等信息存到本地 如果“记住密码”被框选 那么则会自动登录
        editor = sharedPreferences.edit();
        editor.putString("name",name);
        editor.putString("password",password);
        editor.putBoolean("rem",rem);
        editor.putBoolean("login",login);
        editor.commit();
    }
    public void SetLog(boolean b){
        editor = sharedPreferences.edit();
        editor.putBoolean("log",b);
        editor.commit();
    }
    public void SetRem(boolean b){
        editor = sharedPreferences.edit();
        editor.putBoolean("rem",b);
        editor.commit();
    }
    public Boolean getLog(){
        return sharedPreferences.getBoolean("log",false);
    }//将本地信息读取
    public Boolean getre(){
        return  sharedPreferences.getBoolean("login",false);
    }
    public String getUserName(){
       return sharedPreferences.getString("name","");
    }
    public String getUserPassword(){
      return  sharedPreferences.getString("password","");
    }
    public Boolean getLogin(){
        return  sharedPreferences.getBoolean("rem",false);
    }
}
