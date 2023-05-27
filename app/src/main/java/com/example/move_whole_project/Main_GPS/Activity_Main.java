package com.example.move_whole_project.Main_GPS;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.move_whole_project.Fragment.AvatarFragment;
import com.example.move_whole_project.Fragment.HomeFragment;
import com.example.move_whole_project.Fragment.MissionFragment;
import com.example.move_whole_project.Fragment.ShopFragment;
import com.example.move_whole_project.R;
import com.example.move_whole_project.Fragment.ProfileFragment;
import com.google.android.material.navigation.NavigationBarView;



public class Activity_Main extends AppCompatActivity {

    HomeFragment homeFragment;
    MissionFragment missionFragment;
    ProfileFragment recordFragment;
    ShopFragment shopFragment;

    AvatarFragment avatarFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 프래그 먼트(화면 선언)
        homeFragment = new HomeFragment();
        missionFragment = new MissionFragment();
        recordFragment = new ProfileFragment();
        shopFragment = new ShopFragment();
        avatarFragment = new AvatarFragment();

        // 초기 화면은 홈 프래그먼트로 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.containers, homeFragment).commit();


        // BottomNavigationView 기능 구현
        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigationview);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, homeFragment).commit();
                        return true;
                    case R.id.mission:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, missionFragment).commit();
                        return true;
                    case R.id.record:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, recordFragment).commit();
                        return true;
                    case R.id.shop:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, shopFragment).commit();
                        return true;
                    case R.id.avatar:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, avatarFragment).commit();
                        return true;
                }
                return false;

            }
        });
    }
}