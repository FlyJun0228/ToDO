package edu.gatech.seclass.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import edu.gatech.seclass.todo.Bean.UserBean;
import edu.gatech.seclass.todo.Tool.SP;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText Et_login_name,Et_login_pass;
    private Button Bt_login;
    private String sc_num,sc_password;
    private CheckBox cb_pass,cb_login;
    private boolean aBoolean,bBoolean;
    private String user,password;
    private TextView textView;
    private SP sp;
    private Boolean login,re;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "5b99cf7cb7c8e836bfab4b255c050bf6");//云数据库初始化
        setContentView(R.layout.activity_login);
        initView();
        initListener();

        Et_login_pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {//当焦点在密码输入框时，更换小猫图片状态
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    imageView.setImageResource(R.drawable.login_l);
                }else {
                    imageView.setImageResource(R.drawable.login_a);
                }
            }
        });


        re = sp.getre();//从本地获取相关值，进行是否需要自动登录操作
        if (re == true){
            Intent intent = new Intent();
            intent.setClass(Login.this, MainActivity.class);
            Login.this.startActivity(intent);
            Login.this.finish();
            cb_login.setChecked(true);
        }else {
            user = sp.getUserName();
            password = sp.getUserPassword();
            login = sp.getLogin();
            if (login == true) {//如果记住密码为真，则把本地保存的账号和密码自动填充输入框
                Et_login_name.setText(user);
                Et_login_pass.setText(password);
                cb_pass.setChecked(true);
                aBoolean = true;
            } else {

            }
            if (getIntent().getStringExtra("name") == null) {

            } else {//注册完成后，则把注册成功的账号和密码自动填入输入框
                user = getIntent().getStringExtra("name");
                password = getIntent().getStringExtra("password");
                Et_login_name.setText(user);
                Et_login_pass.setText(password);
            }
        }
    }
    public void initView(){//界面控件的绑定
        imageView = findViewById(R.id.login_bg);
        Et_login_name = findViewById(R.id.login_name);
        Et_login_pass = findViewById(R.id.login_password);
        Bt_login = findViewById(R.id.login_button);
        cb_pass = findViewById(R.id.cb_password);
        cb_login = findViewById(R.id.cb_login);
        textView = findViewById(R.id.register);
        sp = new SP(getApplicationContext());
    }
    public void initListener() {
        textView.setOnClickListener(this);
        Bt_login.setOnClickListener(this);
        cb_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//记住密码方框的复选
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (cb_pass.isChecked()){
                    aBoolean = true;
                }else {
                    aBoolean = false;
                }
            }
        });
        cb_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//自动登录方框的复选
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (cb_login.isChecked()){
                    bBoolean = true;
                }else {
                    bBoolean = false;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_button:
                getText();
                if (sc_num.isEmpty()||sc_password.isEmpty()){//判断输入是否为空
                    Toast.makeText(getApplicationContext(),"please input something",Toast.LENGTH_SHORT).show();
                }else {
                    LoginStudent(sc_num,sc_password);
                }
                break;
            case R.id.register:
                Intent intent = new Intent();
                intent.setClass(Login.this, Register.class);
                Login.this.startActivity(intent);
                Login.this.finish();
                break;
        }

    }
    public void LoginStudent(String name,String password){
        String bql = "select * from UserBean where name = ? ";//从云数据库里查询是否有该账号以及该账号密码
        new BmobQuery<UserBean>().doSQLQuery(bql, new SQLQueryListener<UserBean>() {
            @Override
            public void done(BmobQueryResult<UserBean> bmobQueryResult, BmobException e) {
                if (e == null) {
                    List<UserBean> list = (List<UserBean>) bmobQueryResult.getResults();
                    if (list != null && list.size() > 0) {
                        if (list.get(0).getPassword().equals(password)){//如果密码一致，则登录成功
                            Toast.makeText(getApplicationContext(),"login successful",Toast.LENGTH_SHORT).show();
                            sp.SaveUser(name,password,aBoolean,bBoolean);//将相关信息存入本地数据库
                            Intent intent = new Intent();
                            intent.setClass(Login.this, MainActivity.class);
                            Login.this.startActivity(intent);
                            Login.this.finish();
                        }else{
                            Toast.makeText(getApplicationContext(),"wrong password",Toast.LENGTH_SHORT).show();
                        }
                    } else {

                    }
                } else {
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        }, name);
    }
    public void getText(){//获取输入的账号和密码
        sc_num = Et_login_name.getText().toString();
        sc_password = Et_login_pass.getText().toString();
    }
}