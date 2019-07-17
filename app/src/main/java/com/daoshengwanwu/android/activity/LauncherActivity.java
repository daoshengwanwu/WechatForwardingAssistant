package com.daoshengwanwu.android.activity;


import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ShareData;


public class LauncherActivity extends AppCompatActivity {
    private EditText mLabelET;
    private EditText mContentET;
    private Button mButton;
    private ShareData mShareData = ShareData.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        mLabelET = findViewById(R.id.et_label);
        mContentET = findViewById(R.id.et_content);
        mButton = findViewById(R.id.button);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String label = mLabelET.getText().toString();
                String content = mContentET.getText().toString();

                if (!TextUtils.isEmpty(label) && !TextUtils.isEmpty(content)) {
                    Toast.makeText(LauncherActivity.this, "已激活群发任务", Toast.LENGTH_SHORT).show();
                } else {
                    mShareData.clearData();
                    Toast.makeText(LauncherActivity.this, "输入内容不可为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
