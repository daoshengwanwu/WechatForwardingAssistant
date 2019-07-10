package com.daoshengwanwu.android.activity;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.daoshengwanwu.android.R;


public class GroupEditActivity extends AppCompatActivity {
    private EditText mGroupNameET;
    private EditText mKeywordET;
    private EditText mLabelNameET;
    private Button mImportBtn;
    private RecyclerView mRecyclerView;

    private Adapter mAdapter = new Adapter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        mGroupNameET = findViewById(R.id.group_name_et);
        mKeywordET = findViewById(R.id.search_keyword_et);
        mLabelNameET = findViewById(R.id.label_name_et);
        mImportBtn = findViewById(R.id.import_btn);
        mRecyclerView = findViewById(R.id.users_rv);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.item_user_group_edit, parent, false);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bindData();
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mFullNameTV;
            private TextView mDetailTV;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                mFullNameTV = itemView.findViewById(R.id.full_name_tv);
                mDetailTV = itemView.findViewById(R.id.detail_tv);
            }

            public void bindData() {

            }
        }
    }
}
