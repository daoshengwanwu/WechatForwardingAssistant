package com.daoshengwanwu.android.activity;


import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ShareData;


public class CleanActivity extends AppCompatActivity {
    private boolean mIsStart = false;
    private Button mCleanBtn;

    private ShareData mShareData = ShareData.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);

        mCleanBtn = findViewById(R.id.btn_clean);
        mCleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsStart = !mIsStart;

                if (mIsStart) {
                    if (mShareData.getActiveTask() != null) {
                        Toast.makeText(CleanActivity.this, "当前正在执行： " + mShareData.getActiveTask() + "无法开启新的任务", Toast.LENGTH_SHORT).show();
                        mIsStart = false;
                        return;
                    }

                    mShareData.activeCleanTask();
                    mCleanBtn.setText("关闭自动选中复选框");
                    Toast.makeText(CleanActivity.this, "成功开启", Toast.LENGTH_SHORT).show();
                } else {
                    mShareData.stopCleanTask();
                    mCleanBtn.setText("开启自动选中复选框");
                }
            }
        });
    }
}
