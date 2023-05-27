package com.example.move_whole_project.Main_GPS;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.move_whole_project.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// 내부에서는 GPS 갱신안되고 밖에 나가야만 GPS 정상적으로 작동함
public class Activity_GPS extends AppCompatActivity implements AutoPermissionsListener,SensorEventListener {

    // GPS에 필요한 변수들
    public Button coordinate_value_button, my_location_button;
    public TextView value_text;
    LocationManager manager;
    GPSListener gpsListener;

    SupportMapFragment mapFragment;
    GoogleMap map;

    // GPS Marker표시
    Marker myMarker;
    MarkerOptions myLocationMarker;
    Circle circle;
    CircleOptions circle1KM;

    // 현재 걸음 수
    SensorManager sensorManager;
    Sensor stepCountSensor;
    TextView stepCountView;
    Button resetButton;
    int currentSteps = 0;

    // 이동 거리
    TextView distances;

    // 날짜 표시
    TextView current_date;
    public TextView txt;



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        setTitle("GPS 현재위치 확인하기");

        // GPS
        coordinate_value_button = findViewById(R.id.coordinate_value);
        my_location_button = findViewById(R.id.my_location);
        value_text = findViewById(R.id.value_text);


        // 위도 경도 -> 주소 변환
        txt = findViewById(R.id.txt);

        // 걸음 수
        stepCountView = findViewById(R.id.steps_count);
        resetButton = findViewById(R.id.resetButton);

        // 이동 거리
        distances = findViewById(R.id.distances);

        // GPS 활동 퍼미션 체크
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {

            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        // 걸음 센서 연결
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);


        // 현재 시간 구하기
        current_date = findViewById(R.id.current_date);
        long mNow = System.currentTimeMillis();
        Date mReDate = new Date(mNow);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatDate = mFormat.format(mReDate);
        current_date.setText("날짜 : " + formatDate);


        // 디바이스에 걸음 센서의 존재 여부 체크
        if (stepCountSensor == null) {
            Toast.makeText(this, "No Step Sensor", Toast.LENGTH_SHORT).show();
        }

        // 리셋 버튼
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSteps = 0;
                stepCountView.setText("걸음 수 :  " + String.valueOf(currentSteps));
            }
        });

        // GPS 위치 서비스
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsListener = new GPSListener();

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.i("MyLocTest", "지도 준비됨");
                map = googleMap;
                if (ActivityCompat.checkSelfPermission(Activity_GPS.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Activity_GPS.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                map.setMyLocationEnabled(true);
            }
        });

        // 최초 지도 숨김
        mapFragment.getView().setVisibility(View.GONE);
        my_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 지도 보임
                mapFragment.getView().setVisibility(View.VISIBLE);
                startLocationService();
            }
        });
        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }

    public void onStart() {
        super.onStart();
        if (stepCountSensor != null) {
            // 센서 속도 설정
            // * 옵션
            // - SENSOR_DELAY_NORMAL: 20,000 초 딜레이
            // - SENSOR_DELAY_UI: 6,000 초 딜레이
            // - SENSOR_DELAY_GAME: 20,000 초 딜레이
            // - SENSOR_DELAY_FASTEST: 딜레이 없음
            //
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 걸음 센서 이벤트 발생시
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0f) {
                    currentSteps++;
                    stepCountView.setText("걸음 수 : " + currentSteps);
                    distances.setText("이동 거리 : " + Math.round((currentSteps * 0.7 )) + "m");
                    Log.d("현재 걸음 수 ", String.valueOf(currentSteps));
                    Log.d("이동 거리", String.valueOf(Math.round(currentSteps * 0.7)) + 'm');
                }
            }
          }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // 실질적 GPS 서비스
    public void startLocationService() {
        try {
            Location location = null;
            long minTime = 0;        // 0초마다 갱신 - 바로바로갱신
            float minDistance = 0;
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    double latitude = Double.parseDouble(String.format("%.7f",location.getLatitude()));  // 소숫점 7번째 자리가 1cm
                    double longitude = Double.parseDouble(String.format("%.7f",location.getLongitude()));
                    String message = "위도 : " + latitude + "\n 경도 : " + longitude;


                    // geocoder 위도 경도 좌표를 주소로 변환
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocation(
                                latitude,
                                longitude,
                                100);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (addresses != null) {
                        if (addresses.size() == 0) {
                            txt.setText("오류");
                        } else {
                            Log.d("주소", addresses.get(0).toString());
                            txt.setText(addresses.get(0).getAdminArea() + " " + //admin = 도, Locality = 시, thoroughfare = 동
                                    addresses.get(0).getLocality() + " " +
                                    addresses.get(0).getThoroughfare());
                        }
                    }
                    value_text.setText(message);
                    showCurrentLocation(latitude, longitude);
                    Log.i("MyLocTest", "최근 위치1 호출");
                }

                //위치 요청하기
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
                //manager.removeUpdates(gpsListener);
                Toast.makeText(getApplicationContext(), "내 위치1확인 요청함", Toast.LENGTH_SHORT).show();
                Log.i("MyLocTest", "requestLocationUpdates() 내 위치1에서 호출시작");

            } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String message = "위도 : " + latitude + "\n 경도 : " + longitude;

                    value_text.setText(message);
                    showCurrentLocation(latitude, longitude);
                    Log.i("MyLocTest", "최근 위치2 호출");
                }

                //위치 요청하기
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
                //manager.removeUpdates(gpsListener);
                Toast.makeText(getApplicationContext(), "내 위치2확인 요청함", Toast.LENGTH_SHORT).show();
                Log.i("MyLocTest", "requestLocationUpdates() 내 위치2에서 호출시작");
            }


        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // 위치 확인되었을때 자동으로 호출됨 (일정시간 and 일정거리) 1초 주기로 경도 위도 바뀜
    class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String message = "Latitude : " + latitude + "\nLongitude : " + longitude;
            value_text.setText(message);
            showCurrentLocation(latitude, longitude);
            Log.i("MyLocTest", "onLocationChanged() 호출되었습니다.");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // GPS provider를 이용전에 퍼미션 체크
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {

            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
            } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gpsListener);
            }

            if (map != null) {
                map.setMyLocationEnabled(true);
            }
            Log.i("MyLocTest", "onResume에서 requestLocationUpdates() 되었습니다.");
        }
    }
    
    
    @Override
    protected void onPause() {
        super.onPause();
        manager.removeUpdates(gpsListener);
        if (map != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(false);
        }
        Log.i("MyLocTest", "onPause에서 removeUpdates() 되었습니다.");

    }

    // 현재 위치 표시
    private void showCurrentLocation(double latitude, double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
        showMyLocationMarker(curPoint);
    }
    
    // 현재 위치 마커 표시
    private void showMyLocationMarker(LatLng curPoint) {
        if (myLocationMarker == null) {
            myLocationMarker = new MarkerOptions(); // 마커 객체 생성
            myLocationMarker.position(curPoint);
            myLocationMarker.title("최근위치 \n");
            myLocationMarker.snippet("*GPS로 확인한 최근위치");
            myLocationMarker.icon(BitmapDescriptorFactory.fromResource((R.drawable.marker)));
            myMarker = map.addMarker(myLocationMarker);
        } else {
            myMarker.remove(); // 마커삭제
            myLocationMarker.position(curPoint);
            myMarker = map.addMarker(myLocationMarker);
        }

        // 반경추가
        if (circle1KM == null) {
            circle1KM = new CircleOptions().center(curPoint) // 원점
                    .radius(1000)       // 반지름 단위 : m
                    .strokeWidth(1.0f);    // 선너비 0f : 선없음
            circle = map.addCircle(circle1KM);

        } else {
            circle.remove(); // 반경삭제
            circle1KM.center(curPoint);
            circle = map.addCircle(circle1KM);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
        Toast.makeText(this, "requestCode : " + requestCode + "  permissions : " + permissions + "  grantResults :" + grantResults, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDenied(int requestCode, @NonNull String[] permissions) {
        Toast.makeText(getApplicationContext(), "permissions denied : " + permissions.length, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGranted(int requestCode, @NonNull String[] permissions) {
        Toast.makeText(getApplicationContext(), "permissions granted : " + permissions.length, Toast.LENGTH_SHORT).show();
    }

}
