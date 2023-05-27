package com.example.move_whole_project.Register_Login;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.move_whole_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Activity_Signup extends AppCompatActivity {

    // 회원가입시 사용하게될 에딧텍스트, 텍스트뷰, 스피너, 버튼
    private EditText  et_nickname, et_id, et_pass, et_pass_check;

    private Spinner sp_gender, sp_region;
    private Button btn_birth,btn_signup;

    // 출생년월 선택 버튼

    // 출생년월일을 담는 전역 변수

    int YEAR = 0;

    JSONObject jsonBody;

    // 데이트 피커에 해당
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            Log.d("YearMonthPickerTest", "year= "+ year +", monthOfYear= "+ monthOfYear + ", dayOfMonth= "+dayOfMonth);
            // 전역 변수에 담아준다.
            YEAR = year;

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        // 아이디에 해당하는 에딧텍스트(이름,닉네임, 아이디, 패스워드, 패스워드 다시)

        et_nickname = findViewById(R.id.et_nickname);
        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_password);
        et_pass_check = findViewById(R.id.et_check);

        // 성별, 지역 스피너

        sp_gender = findViewById(R.id.sp_gender);
        sp_region = findViewById(R.id.sp_region);


        String[] gender_array = getResources().getStringArray(R.array.gender_array);

        ArrayAdapter<String> ad_gender = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gender_array);
        ad_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_gender.setAdapter(ad_gender);

        String[] region_array = getResources().getStringArray(R.array.region_array);

        ArrayAdapter<String> ad_region = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, region_array);
        ad_region.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_region.setAdapter(ad_region);



        // 출생년월 선택

        btn_birth = findViewById(R.id.btn_birth);

        btn_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                YearMonthPicker pd = new YearMonthPicker();
                pd.setListener(d);
                pd.show(getSupportFragmentManager(), "YearMonthPickerTest");

            }
        });


        // 회원가입 버튼을 누르면 생기는 일

        btn_signup = findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // EditText에 입력되어 있는값을 get(가져온다)해준다.
                String nickname = et_nickname.getText().toString();
                String email = et_id.getText().toString();
                String password = et_pass.getText().toString();
                String userPasscheck = et_pass_check.getText().toString();

                // 태어난 년도(생년)
                int birthDate = YEAR;


                // Spinner 에서 선택된 값
                // 아무것도 선택하지 않았을때를 대비해서 문자열을 비워준다.
                String sex = sp_gender.getSelectedItem().toString();
                if (sex.equals("성별을 선택하세요.")){
                    sex = "";
                }

                String province = sp_region.getSelectedItem().toString();
                if(province.equals("지역을 선택하세요.")){
                    province = "";
                }


                String finalSex = sex;
                String finalProvince = province;


                // 만약 입력된 값이 하나라도 없는 상태라면

                if(nickname.length() == 0 || email.length() == 0 || password.length() == 0 || userPasscheck.length() == 0 ||
                        finalSex.length() == 0 || finalProvince.length() == 0 || birthDate == 0 ){
                    Toast.makeText(getApplicationContext(), "입력되지 않은 부분이 있습니다. 다시 입력해주세요",Toast.LENGTH_SHORT).show();
                    // 현재 액티비티를 끝내고 다시 실행
                    Intent intent = getIntent();
                    finish();
                    overridePendingTransition(0,0);
                    startActivity(intent);
                    overridePendingTransition(0,0);

                }


                // 모든 값들이 입력 되었을때

                else{
                   // 모든 값들이 입력되도 이상한 값이 있을 수 있다
                   // 그럴때를 대비해 다시 액티비티를 시작할 수 있도록 한다.

                    Pattern pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$");
                    Matcher matcher = pattern.matcher(email);

                    // 아이디가 이메일 형식이 아니거나 패스워드가 일치하지 않을때
                    if(!matcher.find() || !password.equals(userPasscheck)){
                        Toast.makeText(getApplicationContext(), "아이디나 비밀번호에 문제가 있습니다. 다시 확인해주세요",Toast.LENGTH_SHORT).show();
                        // 현재 액티비티를 끝내고 다시 실행
                        Intent intent = getIntent();
                        finish();
                        overridePendingTransition(0,0);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }


                    // 입력한 값들이 이상이 없을때
                    // 아이디가 이메일 형식이고, 비밀번호가 일치하고, 모든 값이 입력이 되었을때
                    else{
                        jsonBody = new JSONObject();
                        try{
                            jsonBody.put("birthDate",birthDate);
                            jsonBody.put("email",email);
                            jsonBody.put("password",password);
                            jsonBody.put("nickname",nickname);
                            jsonBody.put("sex",finalSex);
                            jsonBody.put("province",finalProvince);

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        // volley 구문: 웹서버와 http 통신을 하기위한 구문
                        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // JSONObject 형태로 데이터를 담아서 보내준다.(운반체계)
                                // try~catch 로 이를 감싸준다.
                                Log.d("회원","요청");
                                try{
                                    JSONObject jsonObject = response;
                                    boolean success = jsonObject.getBoolean("success"); // 서버 통신이 잘되었는지 안되었는지 알려줌
                                    if(success){ // 회원 등록에 성공한 경우
                                        Toast.makeText(getApplicationContext(), "회원 등록에 성공하였습니다.", Toast.LENGTH_SHORT).show();

                                        // 회원 등록에 성공했을때 시작하는 인텐트
                                        Intent intent = new Intent(Activity_Signup.this, Activity_SignupDone.class);

                                        startActivity(intent);
                                    }
                                    else{ // 회원 등록에 실패한 경우
                                        Toast.makeText(getApplicationContext(), "회원 등록에 실패했습니다.",Toast.LENGTH_SHORT).show();
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
                                Log.d("오류","발생");
                            }
                        };

                        // 서버로 Volley를 이용해서 요청을 한다. 회원가입 처리완성
                        RegisterRequest registerRequest = new RegisterRequest(jsonBody,responseListener, errorListener);
                        RequestQueue queue = Volley.newRequestQueue(Activity_Signup.this);
                        queue.add(registerRequest);

                    }

                }
            }

        });

    }
}