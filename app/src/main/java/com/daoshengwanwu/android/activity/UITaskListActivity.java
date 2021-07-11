package com.daoshengwanwu.android.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.UIForwardingTask;
import com.daoshengwanwu.android.model.UIForwardingTaskLab;

import java.util.ArrayList;
import java.util.List;


public class UITaskListActivity extends AppCompatActivity {
    public static final void launchForwardingTaskListActivity(@NonNull final Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        final Intent intent = new Intent(activity, UITaskListActivity.class);

        try {
            activity.startActivity(intent);
        } catch (Throwable e) {
            Toast.makeText(activity, "启动点赞Activity失败", Toast.LENGTH_LONG).show();
        }
    }


    private UIForwardingTaskLab mUIForwardingTaskLab = UIForwardingTaskLab.getInstance();

    private RecyclerView mRecyclerView;
    private Adapter mAdapter = new Adapter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitask_list);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.updateDataAndViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ui_task_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_ui_task) {
            mAdapter.updateDataAndViews();

            startActivity(UITaskEditActivity.newIntent(this, null));
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private List<UIForwardingTask> mData = new ArrayList<>();


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forwarding_content_list, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bindData(getItem(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public void updateDataAndViews() {
            mData = mUIForwardingTaskLab.getAllUIForwardingTask();
            notifyDataSetChanged();
        }

        private UIForwardingTask getItem(int position) {
            return mData.get(position);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private UIForwardingTask mTask;
            private TextView mTextView;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                mTextView = itemView.findViewById(R.id.text_view);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(UITaskEditActivity.newIntent(UITaskListActivity.this, mTask.getId()));
                    }
                });
            }

            public void bindData(UIForwardingTask task) {
                mTask = task;

                mTextView.setText(task.getTaskName());
            }
        }
    }
}
