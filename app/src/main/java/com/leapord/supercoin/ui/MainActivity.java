package com.leapord.supercoin.ui;


import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.leapord.supercoin.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                switchFragment(0);
                return true;
            case R.id.navigation_dashboard:
                switchFragment(1);
                return true;
            case R.id.navigation_notifications:
                switchFragment(2);
                return true;
        }
        return false;
    };

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentList.add(new HomeFragment());
        fragmentList.add(new HistoryFragment());
        fragmentList.add(new ActionFragment());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragmentList.get(0))
                .commitAllowingStateLoss();

    }

    private void switchFragment(int index) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragmentList.get(index))
                .commitAllowingStateLoss();
    }


}
