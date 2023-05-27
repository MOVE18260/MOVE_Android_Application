package com.example.move_whole_project.Register_Login;


import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

// create construct => 2번째걸 선택(alt + enter)

public class RegisterRequest extends JsonObjectRequest {

    // url 설정(서버 url), 임의로 변경할 수 있음
    final static private String URL = "http://192.168.0.106:3000/signUp";

    // 생성자 만들기, 회원가입에서 입력받는 데이터(Json형태로 전달)
    public RegisterRequest(JSONObject jsonBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        // 서버 전송 방식 POST 방식
        super(Method.POST, URL, jsonBody, listener, errorListener);

        Log.d("보낸 데이터",jsonBody.toString());
    }


}
