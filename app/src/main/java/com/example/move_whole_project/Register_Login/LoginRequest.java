package com.example.move_whole_project.Register_Login;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

// create construct => 2번째거 선택(alt + enter)

public class LoginRequest extends JsonObjectRequest {
    // url 설정(서버 url)
    final static private String URL = "http://192.168.0.106:3000/signIn";

    // 생성자 만들기, 로그인에서 입력받는 데이터들 (아이디, 패스워드)
    public LoginRequest(JSONObject jsonBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        // 서버 전송 방식 (GET 방식)
        super(Method.POST, URL, jsonBody, listener, errorListener);

        Log.d("보낸 데이터",jsonBody.toString());

    }

}
