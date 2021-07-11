package com.daoshengwanwu.android.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.task.Task;


public class CleanActivity extends AppCompatActivity {
    public static final void launchCleanActivity(@NonNull final Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        final Intent intent = new Intent(activity, CleanActivity.class);

        try {
            activity.startActivity(intent);
        } catch (Throwable e) {
            Toast.makeText(activity, "启动清理Activity失败", Toast.LENGTH_LONG).show();
        }
    }


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
                        Toast.makeText(CleanActivity.this, "当前正在执行： " + mShareData.getActiveTask() + "无法开启新的任务", Toast.LENGTH_LONG).show();
                        mIsStart = false;
                        return;
                    }

                    mShareData.activeCleanTask();
                    mCleanBtn.setText("关闭自动选中复选框");
                    Toast.makeText(CleanActivity.this, "成功开启", Toast.LENGTH_LONG).show();
                } else {
                    mShareData.stopCleanTask();
                    mCleanBtn.setText("开启自动选中复选框");
                }
            }
        });

        mIsStart = mShareData.getActiveTask() != null && mShareData.getActiveTask().getTaskId() == Task.TaskId.TASK_CLEAN;
        mCleanBtn.setText(mIsStart ? "关闭自动选中复选框" : "开启自动选中复选框");
    }
}
