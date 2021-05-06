package com.daoshengwanwu.android.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.daoshengwanwu.android.R;


public class LauncherActivity extends AppCompatActivity {
    private Button mYesButton;
    private Button mCleanButton;
    private Button mForwardingButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        setupView();
    }

    private void setupView() {
        mYesButton = findViewById(R.id.btn_yes_assistant);
        mCleanButton = findViewById(R.id.btn_clean_assistant);
        mForwardingButton = findViewById(R.id.btn_forward_assistant);

        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YesActivity.launchYesActivity(LauncherActivity.this);
            }
        });

        mCleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CleanActivity.launchCleanActivity(LauncherActivity.this);
            }
        });

        mForwardingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UITaskListActivity.launchForwardingTaskListActivity(LauncherActivity.this);
            }
        });
    }
}
