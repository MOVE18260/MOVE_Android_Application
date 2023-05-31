package com.example.move_whole_project.Request;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class StepRequest extends JsonObjectRequest {

    final static private String URL= "http://192.168.142.48:3000/step";

    public StepRequest(JSONObject jsonBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        super(Method.PUT, URL, jsonBody, listener, errorListener);

        Log.d("보낼 데이터",jsonBody.toString());

    };

}
