package com.daoshengwanwu.android.activity;


import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.UIForwardingTask;
import com.daoshengwanwu.android.model.UIForwardingTaskLab;

import java.util.ArrayList;
import java.util.List;


public class UITaskListActivity extends AppCompatActivity {
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
            UIForwardingTask task = new UIForwardingTask(null, null, "");
            mUIForwardingTaskLab.putForwrdingTaskLab(this, task);
            mAdapter.updateDataAndViews();

            startActivity(UITaskEditActivity.newIntent(this, task.getId()));
        }
        return super.onOptionsItemSelected(item);
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