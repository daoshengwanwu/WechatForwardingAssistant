package com.daoshengwanwu.android.activity;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

            //TODO:: 开启UITask编辑页面

        }
        return super.onOptionsItemSelected(item);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private List<UIForwardingTask> mData = new ArrayList<>();


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
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

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void bindData(UIForwardingTask task) {

            }
        }
    }
}
