package com.example.move_whole_project.Register_Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.move_whole_project.Main_GPS.Activity_Main;
import com.example.move_whole_project.R;

public class Activity_SignupDone extends AppCompatActivity {

    private TextView tv_nickname;
    private Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_done);

        tv_nickname = findViewById(R.id.tv_nickname);
        btn_start = findViewById(R.id.btn_start);

        Intent intent = getIntent();
        String userNickname = intent.getStringExtra("userNickname");

        tv_nickname.setText(userNickname+"님!");

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_SignupDone.this, Activity_Main.class);
                startActivity(intent);

            }
        });


    }
}