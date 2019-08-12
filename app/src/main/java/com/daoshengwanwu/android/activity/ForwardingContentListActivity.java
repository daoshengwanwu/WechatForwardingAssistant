package com.daoshengwanwu.android.activity;


import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ForwardingContent;
import com.daoshengwanwu.android.model.ForwardingContentLab;

import java.util.List;


public class ForwardingContentListActivity extends AppCompatActivity {
    private ForwardingContentLab mForwardingContentLab = ForwardingContentLab.getInstance();

    private Adapter mAdapter = new Adapter();
    private RecyclerView mRecyclerView;

    private Mode mCurMode = Mode.MODE_SELECT;


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ForwardingContentListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forwarding_content_list);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forwarding_content_list, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_content) {
            ForwardingContent forwardingContent = new ForwardingContent("");
            mForwardingContentLab.putForwardingContent(this, forwardingContent);
            mAdapter.updateDataAndViews();
            startActivity(ForwardingEditActivity.newIntent(this, forwardingContent.getId()));
        }

        if (item.getItemId() == R.id.switch_mode) {
            if (mCurMode == Mode.MODE_EDIT) {
                mCurMode = Mode.MODE_SELECT;
                item.setTitle("编辑");
            } else if (mCurMode == Mode.MODE_SELECT) {
                mCurMode = Mode.MODE_EDIT;
                item.setTitle("退出编辑");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private List<ForwardingContent> mContents = mForwardingContentLab.getAllForwardingContents();


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
            return mContents.size();
        }

        public void updateDataAndViews() {
            mContents = mForwardingContentLab.getAllForwardingContents();
            notifyDataSetChanged();
        }

        private ForwardingContent getItem(int position) {
            return mContents.get(position);
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            private ForwardingContent mForwardingContent;
            private TextView mTextView;


            public ViewHolder(@NonNull final View itemView) {
                super(itemView);

                mTextView = itemView.findViewById(R.id.text_view);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCurMode == Mode.MODE_EDIT) {
                            startActivity(ForwardingEditActivity.newIntent(itemView.getContext(), mForwardingContent.getId()));
                        } else if (mCurMode == Mode.MODE_SELECT) {
                            Intent data = new Intent();
                            data.putExtra("forwarding_content_uuid", mForwardingContent.getId().toString());
                            setResult(RESULT_OK, data);
                        }
                    }
                });
            }

            public void bindData(ForwardingContent forwardingContent) {
                mForwardingContent = forwardingContent;

                mTextView.setText(forwardingContent == null ? "" : forwardingContent.getContent());
            }
        }
    }

    private enum Mode {
        MODE_EDIT, MODE_SELECT
    }
}
