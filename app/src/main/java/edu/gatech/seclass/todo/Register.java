package edu.gatech.seclass.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;


import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import edu.gatech.seclass.todo.Bean.UserBean;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private EditText Et_glass,Et_name,Et_password;
    private Button Bt_add_student;
    private String stu_class,stu_name,stu_pass,sex;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initListener();
        sex = "男";
        Et_glass.setOnFocusChangeListener(new View.OnFocusChangeListener() {//当焦点在密码输入框时，更换小猫图片状态
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    imageView.setImageResource(R.drawable.login_l);
                }else {
                    imageView.setImageResource(R.drawable.login_a);
                }
            }
        });
        Et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    imageView.setImageResource(R.drawable.login_l);
                }else {
                    imageView.setImageResource(R.drawable.login_a);
                }
            }
        });
    }
    public void initView(){
        imageView = findViewById(R.id.login_bg);
        Et_glass = findViewById(R.id.stu_class);
        Et_name = findViewById(R.id.stu_name);
        Bt_add_student = findViewById(R.id.add_student);
        Et_password = findViewById(R.id.stu_pass);
    }
    public void initListener(){
        Bt_add_student.setOnClickListener(this);
    }
    public void getText(){//获取输入信息
        stu_class = Et_glass.getText().toString();
        stu_name = Et_name.getText().toString();
        stu_pass = Et_password.getText().toString();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(Register.this,Login.class);
        Register.this.startActivity(intent);
        Register.this.finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_student:
                getText();
                if (stu_class.isEmpty()||stu_name.isEmpty()||stu_pass.isEmpty()){//判读输入是否为空
                    Toast.makeText(getApplicationContext(),"  ",Toast.LENGTH_SHORT).show();
                }else if (stu_pass.equals(stu_class)){
                    AddUser();
                }else {
                    Toast.makeText(getApplicationContext(),"两次密码输入不一致",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public void AddUser(){//将注册的信息加入数据库，完成注册操作
        UserBean userBean = new UserBean();
        userBean.setName(stu_name);
        userBean.setPassword(stu_pass);
        userBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    Toast.makeText(Register.this, "register success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(Register.this,Login.class);
                    intent.putExtra("name",stu_name);
                    intent.putExtra("password",stu_pass);
                    Register.this.startActivity(intent);
                    Register.this.finish();
                }else{
                    Log.e("sssss",e.getMessage());
                }
            }
        });
    }
}