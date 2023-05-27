package com.example.move_whole_project.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.move_whole_project.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {
    // 차트에 필요한 변수, 스피너
    private BarChart barChart;
    private Spinner spinnerTimeRange;
    private String[] timeRangeOptions = {"일주일", "1달", "1년"};
    private String[] spinner_weekdays = {"월", "화", "수", "목", "금", "토", "일"}; // 1주 x축 값
    private String[] spinner_months = {"1주", "2주", "3주", "4주", "5주"}; // 1달 x축 값
    private String[] spinner_year = {"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"}; // 1년 x축 값


    // 프로필 사진 클릭 이벤트
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ImageView profileImageView;
    private static final int PICK_IMAGE_REQUEST = 1;

    // json 변수
    private Map<String, String> map;
    private String dateTime;
    private int distance;
    private String location;
    private int memberId;
    private int step;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 프로필 사진 선택을 위한 ActivityResultLauncher를 등록합니다.
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri selectedImageUri = data.getData();
                                if (selectedImageUri != null) {
                                    try {
                                        // 선택한 이미지를 비트맵으로 변환하여 ImageView에 설정합니다.
                                        if (Build.VERSION.SDK_INT < 28) {
                                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                                            profileImageView.setImageBitmap(bitmap);
                                        } else {
                                            ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), selectedImageUri);
                                            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                            profileImageView.setImageBitmap(bitmap);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 변수 선언
        barChart = view.findViewById(R.id.tv_profile_graph_input);
        profileImageView = view.findViewById(R.id.tv_profile_image);
        spinnerTimeRange = view.findViewById(R.id.spinner_time_range);
        // 바 차트
        List<BarEntry> stepData = new ArrayList<>();
        barChart.setFitBars(true); // 막대 그래프의 막대를 차트에 꽉 채우도록 설정

        // X축 설정
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X축 위치 설정

        // Y축 설정
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f); // Y축 최소값 설정
        yAxis.setGranularity(100f); // Y축 간격 설정

        barChart.getAxisRight().setEnabled(false); // 오른쪽 Y축 비활성화
        barChart.getDescription().setEnabled(false); // 차트 설명 비활성화
        barChart.setTouchEnabled(false); // 차트 터치 비활성화

        barChart.animateXY(30, 30);
        barChart.invalidate(); // 차트 업데이트

        // 스피너 로직

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, timeRangeOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeRange.setAdapter(spinnerAdapter);

        spinnerTimeRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = timeRangeOptions[position];
                switch (selectedOption) {
                    case "일주일":
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(spinner_weekdays));
                        stepData.clear();
                        stepData.add(new BarEntry(0, 1000f)); // 월요일의 걸음 수
                        stepData.add(new BarEntry(1, 2000f)); // 화요일의 걸음 수
                        stepData.add(new BarEntry(2, 1500f)); // 수요일의 걸음 수
                        stepData.add(new BarEntry(3, 3000f)); // 목요일의 걸음 수
                        stepData.add(new BarEntry(4, 2500f)); // 금요일의 걸음 수
                        stepData.add(new BarEntry(5, 4000f)); // 토요일의 걸음 수
                        stepData.add(new BarEntry(6, 3500f)); // 일요일의 걸음 수
                        break;
                    case "1달":
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(spinner_months));
                        stepData.clear();
                        stepData.add(new BarEntry(0, 1000f)); // 1주의 걸음 수
                        stepData.add(new BarEntry(1, 2000f)); // 2주의 걸음 수
                        stepData.add(new BarEntry(2, 1500f)); // 3주의 걸음 수
                        stepData.add(new BarEntry(3, 3000f)); // 4주의 걸음 수
                        stepData.add(new BarEntry(4, 2500f)); // 5주의 걸음 수
                        break;
                    case "1년":
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(spinner_year));
                        stepData.clear();
                        stepData.add(new BarEntry(0, 1000f)); // 1월의 걸음 수
                        stepData.add(new BarEntry(1, 2000f)); // 2월의 걸음 수
                        stepData.add(new BarEntry(2, 1500f)); // 3월의 걸음 수
                        stepData.add(new BarEntry(3, 3000f)); // 4월의 걸음 수
                        stepData.add(new BarEntry(4, 2500f)); // 5월의 걸음 수
                        stepData.add(new BarEntry(5, 4000f)); // 6월의 걸음 수
                        stepData.add(new BarEntry(6, 3500f)); // 7월의 걸음 수
                        stepData.add(new BarEntry(7, 2800f)); // 8월의 걸음 수
                        stepData.add(new BarEntry(8, 3200f)); // 9월의 걸음 수
                        stepData.add(new BarEntry(9, 3900f)); // 10월의 걸음 수
                        stepData.add(new BarEntry(10, 2700f)); // 11월의 걸음 수
                        stepData.add(new BarEntry(11, 2200f)); // 12월의 걸음 수
                        break;
                }
                final BarDataSet dataSet = new BarDataSet(stepData, "걸음 수");
                dataSet.setColor(Color.BLACK); // 막대 그래프 색상 설정

                final BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.9f); // 막대의 너비 설정 (0.9f는 90%를 의미)

                barChart.setData(barData);
                barChart.getXAxis().setValueFormatter(xAxis.getValueFormatter());
                barChart.notifyDataSetChanged();
                barChart.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


        // 이미지 추가 리스너
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileImageClick(v);
            }
        });
        return view;
    }

    // 프로필 이미지 클릭 이벤트
    public void onProfileImageClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    profileImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}   // 지워도 됩니다, 로직은 맞는거 같은데 흠..
//        private void sendJsonDataToServer (String dateTime ,int distance, String location, int memberId, int step){
//            String url = "http://192.168.0.76:8080"; // 서버의 URL로 변경해주세요
//
//            // JSON 데이터 생성
//            JSONObject json = new JSONObject();
//            try {
//                json.put("dateTime", dateTime);
//                json.put("distance", distance);
//                json.put("location", location);
//                json.put("memberId", memberId);
//                json.put("step", step);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            // 요청 생성
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            // 응답 처리
//                            try {
//                                boolean success = response.getBoolean("success");
//                                if (success) {
//                                    sendJsonDataToServer(dateTime, distance, location, memberId, step);
//                                } else {
//                                    // 서버로 데이터 전송 실패 또는 서버에서 응답 실패
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            // 에러 처리
//                            error.printStackTrace();
//                        }
//                    });
//
//            // 요청을 큐에 추가
//            RequestQueue queue = Volley.newRequestQueue(getContext());
//            queue.add(request);
//}

