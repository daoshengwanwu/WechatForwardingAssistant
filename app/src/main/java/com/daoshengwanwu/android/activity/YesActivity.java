package com.daoshengwanwu.android.activity;


import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.task.Task;


public class YesActivity extends AppCompatActivity {
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
    }
}
