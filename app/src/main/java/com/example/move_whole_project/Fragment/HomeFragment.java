package com.example.move_whole_project.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.move_whole_project.R;
import com.example.move_whole_project.Request.StepRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// 현재 시간을 확인하는 핸들러 및 함수


public class HomeFragment extends Fragment implements SensorEventListener {

    // 걸음수 측정 로직 (센서 활용)
    private SensorManager sensorManager;
    private Sensor stepCntSensor;

    // 실시간 시간 핸들러
    private Handler handler;

    // 걸음수 측정을 위한 변수
    int stepCnt = 0;
    int totalCnt = 0;
    String Time = "";
    String Email = "";
    String Location = "";

    TextView tv_stepCnt;
    ImageView iv_dino;
    AnimationDrawable an_dino;

    private LocationManager locationManager;
    final LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            String provider = location.getProvider();
            double longitude =location.getLongitude();// 위도
            double latitude = location.getLatitude(); // 경도
            double altitude = location.getAltitude(); // 고도
            Log.d("위치","위도 "+longitude+"경도 "+latitude+"고도 "+altitude);

            Geocoder geocorder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses;
            try{
                addresses =  geocorder.getFromLocation(latitude, longitude, 100);
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }
            Location = addresses.get(0).getAdminArea()+" "+addresses.get(0).getLocality()+" "+addresses.get(0).getThoroughfare();
            Log.d("주소",Location);
        }
    };

    StepRequest stepRequest;
    Response.ErrorListener errorListener;
    Response.Listener<JSONObject> listener;
    RequestQueue queue;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 위치 관리자 초기화
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, gpsLocationListener);
        }


        // 프래그먼트로 email 받아오기
        Email = this.getArguments().getString("email");

        tv_stepCnt = (TextView) view.findViewById(R.id.tv_stepCnt);
        iv_dino = (ImageView) view.findViewById(R.id.iv_dino);
        an_dino = (AnimationDrawable) iv_dino.getBackground();

        // 걸음수 측정 센서:
        sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepCntSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // 센서 존재 여부 확인
        if(stepCntSensor == null){
            Toast.makeText(getContext(), "걸음수 측정 센서 없음", Toast.LENGTH_SHORT).show();
        }
        // 어떤 프레그먼트로 돌아가면 계속 초기화되어서 보이는데 그렇게 보이지 않게 하기 위한 코드
        if(totalCnt != 0){
            tv_stepCnt.setText(String.valueOf(totalCnt));
        }


        // 리스너 설정
        listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONObject jsonObject = response;
                    boolean updated = jsonObject.getBoolean("update");
                    if(updated){
                        Log.d("걸음수","갱신");
                    }
                    else{
                        Log.d("걸음수","갱신X");
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

       errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("걸음수","에러발생");
            }
        };


       // 정각이 되면 서버에 시간, 위치, 걸음수, 이메일 전송해 업데이트
        handler = new Handler(Looper.getMainLooper());
        updateStep();

        // Inflate the layout for this fragment
        return view;
    }

    private void updateStep(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 현재 시간 가져오기
                Calendar calendar = Calendar.getInstance();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int date = calendar.get(Calendar.DATE);
                int hour = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                int tomorow = calendar.get(Calendar.DATE) + 1;


                Log.d("시간",year+"년 "+month+"월 "+date+"일 "+hour+"시 "+minute+"분 "+second+"초 ");
                Log.d("내일은",tomorow+"일");
                Time = dateFormat.format(calendar.getTime());
                Log.d("시간",Time);

                // 초가 0이되면 업데이트 하기
                if(second == 0){
                    JSONObject jsonBody = new JSONObject();
                    try{
                        jsonBody.put("email",Email);
                        jsonBody.put("step",totalCnt);
                        jsonBody.put("time",Time);
                        jsonBody.put("location",Location);
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                    stepRequest = new StepRequest(jsonBody,listener,errorListener);
                    queue = Volley.newRequestQueue(getActivity());
                    queue.add(stepRequest);
                }

                // 24시간이 지난 이후에 오늘 걸음수가 초기화되는 코드
                if (hour == 0 && minute == 0 && second == 0){
                    totalCnt = 0;
                    Log.d("하루","지남");
                }


                updateStep();

            }
        }, 1000);
    }

    public void onStart(){
        super.onStart();
        // 센서가 존재한다면
        if(stepCntSensor != null){
            // 센서 속도 설정
            sensorManager.registerListener(this, stepCntSensor,SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // 걸음 센서 이벤트 발생시
        if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
                // 센서 이벤트가 발생할때 마다 걸음 수 증가
                if(sensorEvent.values[0] ==1.0f){
                    stepCnt++;
                    totalCnt = stepCnt;
                    tv_stepCnt.setText(String.valueOf(totalCnt));
                    an_dino.start();
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            an_dino.stop();
                            //딜레이 후 시작할 코드 작성
                        }
                    }, 2000);// 2초 정도 딜레이를 준 후 시작
                }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



}