package com.example.move_whole_project.Fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.move_whole_project.R;

;


public class MissionFragment extends Fragment {

    // 미션 내용, 체크 박스
    private TextView missionContent1;
    private TextView missionContent2;
    private TextView missionContent3;
    private TextView missionContent4;

    private CheckBox missionCheckbox1;
    private CheckBox missionCheckbox2;
    private CheckBox missionCheckbox3;
    private CheckBox missionCheckbox4;

    // 프로그레스 바, 텍스트
    private ProgressBar progressBar;

    private TextView missionProgressText;


    // 걸음 수
    public int totalCnt = 0;

    public static MissionFragment newInstance(int totalCnt) {
        MissionFragment fragment = new MissionFragment();
        Bundle args = new Bundle();
        args.putInt("totalCnt", totalCnt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission, container, false);

        // 미션 내용, 체크 박스
        missionContent1 = view.findViewById(R.id.mission_content1);
        missionContent2 = view.findViewById(R.id.mission_content2);
        missionContent3 = view.findViewById(R.id.mission_content3);
        missionContent4 = view.findViewById(R.id.mission_content4);

        missionCheckbox1 = view.findViewById(R.id.mission_checkbox1);
        missionCheckbox2 = view.findViewById(R.id.mission_checkbox2);
        missionCheckbox3 = view.findViewById(R.id.mission_checkbox3);
        missionCheckbox4 = view.findViewById(R.id.mission_checkbox4);

        // 프로그레스 바, 텍스트
        progressBar = view.findViewById(R.id.mission_progressbar);
        missionProgressText = view.findViewById(R.id.mission_progress_text);

        // 사용자가 건드리지 못하게
        missionCheckbox1.setEnabled(false);
        missionCheckbox2.setEnabled(false);
        missionCheckbox3.setEnabled(false);
        missionCheckbox4.setEnabled(false);

        updateMissions();
        updateProgress();
        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            totalCnt = getArguments().getInt("totalCnt");
        }
    }
    
    // 프로그레스 업데이트를 위한 프로그레스, checkCount 1개 올라갈 때마다 진행도 25%씩 늘어야함
    private void updateProgress() {
        int checkedCount = 0;
        if (missionCheckbox1.isChecked()) {
            checkedCount++;
        }
        if (missionCheckbox2.isChecked()) {
            checkedCount++;
        }
        if (missionCheckbox3.isChecked()) {
            checkedCount++;
        }
        if (missionCheckbox4.isChecked()) {
            checkedCount++;
        }

        int progress = (int) ((checkedCount / 4.0) * 100);
        progressBar.setProgress(progress);

    }

    // mission 업데이트, 이 함수에서 체크박스로 체크 및 밑줄이 그여야됨
    public int updateMissions() {
        if (totalCnt >= 10) {
            missionCheckbox1.setChecked(true);
            updateTextViewStrikeThrough(missionContent1, true);
        }
        if (totalCnt >= 20) {
            missionCheckbox2.setChecked(true);
            updateTextViewStrikeThrough(missionContent2, true);
        }
        if (totalCnt >= 30) {
            missionCheckbox3.setChecked(true);
            updateTextViewStrikeThrough(missionContent3, true);
        }
        if (totalCnt >= 40) {
            missionCheckbox4.setChecked(true);
            updateTextViewStrikeThrough(missionContent4, true);
        }
        updateProgress();
        return totalCnt;
    }
    
    // 총 걸음 수 계산 이후 미션 업데이트
    public void setTotalCount(int count) {
        totalCnt = count;
        updateMissions();
    }

    private void updateTextViewStrikeThrough(TextView textView, boolean isChecked) {
        if (isChecked) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}