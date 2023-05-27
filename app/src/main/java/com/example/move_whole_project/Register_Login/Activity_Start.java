package com.example.move_whole_project.Register_Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.move_whole_project.R;

public class Activity_Start extends AppCompatActivity {

    private Button btn_login, btn_signup, btn_google;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
        btn_google = findViewById(R.id.btn_google);

        // 로그인 버튼을 누를 때
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Start.this, Activity_Login.class);
                startActivity(intent);

            }
        });


        // 회원가입 버튼을 누를 때
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Start.this, Activity_Signup.class);
                startActivity(intent);

            }
        });

        // 구글 로그인 버튼을 눌렀을때
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"아직 안만듬",Toast.LENGTH_SHORT).show();
                return;

            }
        });


    }
}