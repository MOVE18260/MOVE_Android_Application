package com.example.move_whole_project.Register_Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.move_whole_project.Main_GPS.Activity_Main;
import com.example.move_whole_project.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Activity_Login extends AppCompatActivity {

    // 에딧 텍스트와 버튼
    private EditText et_id, et_pass;
    private Button btn_login;

    JSONObject jsonBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        btn_login = findViewById(R.id.btn_login);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 에딧 텍스트로 받은 값
                String email = et_id.getText().toString();
                String password = et_pass.getText().toString();

                // 아이디나 패스워드를 입력하지 않으면

                if(email.length() == 0 || password.length() == 0){
                    Toast.makeText(getApplicationContext(), "아이디와 패스워드를 입력해주세요",Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    // 현재 액티비티(로그인 액티비티)를 다시 시작
                    finish();
                    overridePendingTransition(0,0);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }


                // 아이디와 패스워드를 모두 입력했을때
                else{

                    jsonBody = new JSONObject();
                    try{
                        jsonBody.put("email",email);
                        jsonBody.put("password",password);
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }

                    Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // JSONObject 형태로 데이터를 담아서 보내준다.(운반체계)
                            // try~catch 로 이를 감싸준다.
                            try {
                                JSONObject jsonObject = response;
                                boolean success = jsonObject.getBoolean("success"); // 서버 통신이 잘되었는지 안되었는지 알려준다.

                                if(success){ // 로그인에 성공한 경우

                                    // php 파일에서 키값으로 설정한대로 getString 안을 채워준다.
                                    // key 값으로 json 값을 가져온다.

                                    Toast.makeText(getApplicationContext(), "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Activity_Login.this, Activity_Main.class);
                                    // 다음 인텐트로 넘어갈때 이곳에서 작성했던 내용을 넘겨준다.(아이디, 패스워드)

                                    startActivity(intent);

                                } else { // 로그인에 실패한 경우
                                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다. 다시 입력 해주세요", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("에러","발생");
                        }
                    };

                    // 서버로 Volley를 이용해서 요청을 함
                    LoginRequest loginRequest = new LoginRequest(jsonBody, responseListener,errorListener);
                    RequestQueue queue = Volley.newRequestQueue(Activity_Login.this);
                    queue.add(loginRequest);

                }


            }
        });


    }
}