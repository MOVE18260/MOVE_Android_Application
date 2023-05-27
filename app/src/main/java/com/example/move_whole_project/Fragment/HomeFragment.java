package com.example.move_whole_project.Fragment;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.move_whole_project.R;

// 현재 시간을 확인하는 핸들러 및 함수


public class HomeFragment extends Fragment implements SensorEventListener {

    // 걸음수 측정 로직 (센서 활용)
    private SensorManager sensorManager;
    private Sensor stepCntSensor;

    // 걸음수 측정을 위한 변수

    int stepCnt = 0;
    int totalCnt = 0;
    TextView tv_stepCnt;
    ImageView iv_dino;
    AnimationDrawable an_dino;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);


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
        // Inflate the layout for this fragment
        return view;
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