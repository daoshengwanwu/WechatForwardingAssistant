package com.daoshengwanwu.android.activity;


import android.app.Activity;
import android.content.Context;
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


public class YesActivity extends AppCompatActivity {
    public static final void launchYesActivity(@NonNull final Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        final Intent intent = new Intent(activity, YesActivity.class);

        try {
            activity.startActivity(intent);
        } catch (Throwable e) {
            Toast.makeText(activity, "启动点赞Activity失败", Toast.LENGTH_SHORT).show();
        }
    }


    private ShareData mShareData = ShareData.getInstance();

    private Button mButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes);

        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task aTask = mShareData.getActiveTask();
                if (aTask == null) {
                    mShareData.activeYesTask();
                    Toast.makeText(YesActivity.this, "开启成功", Toast.LENGTH_SHORT).show();
                    mButton.setText("关闭点赞");
                } else if (aTask.getTaskId() == Task.TaskId.TASK_YES) {
                    mShareData.stopYesTask();
                    Toast.makeText(YesActivity.this, "关闭成功", Toast.LENGTH_SHORT).show();
                    mButton.setText("激活点赞");
                } else {
                    Toast.makeText(YesActivity.this, "当前正在执行其他任务无法开启点赞任务", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mButton.setText(mShareData.getActiveTask() != null && mShareData.getActiveTask().getTaskId() == Task.TaskId.TASK_YES ? "关闭点赞" : "激活点赞");
    }
}
