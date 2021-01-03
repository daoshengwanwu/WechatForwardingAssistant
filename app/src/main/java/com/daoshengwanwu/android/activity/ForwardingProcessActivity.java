package com.daoshengwanwu.android.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.model.UserGroup;
import com.daoshengwanwu.android.model.UserGroupLab;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.task.ForwardingTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ForwardingProcessActivity extends AppCompatActivity {
    private static final String EXTRA_GROUP_ID = "extra_group_id";
    private static final String EXTRA_FORWARDING_CONTENT = "extra_forwarding_content";

    private UserGroupLab mUserGroupLab = UserGroupLab.getInstance();
    private UserGroup mUserGroup;
    private String mForwardingContent;

    private ForwardingStatus mStatus = ForwardingStatus.NOT_START;

    private TextView mStatusTV;
    private Button mStartStopBtn;
    private Button mPauseResumeBtn;
    private TextView mRemainNumTV;
    private EditText mBundleSizeET;
    private EditText mPauseTimeET;
    private EditText mDeltaTimeET;
    private RecyclerView mRecyclerView;

    private ShareData mShareData = ShareData.getInstance();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    private Adapter mAdapter = new Adapter();


    public static Intent newIntent(Context context, UUID groupId, String forwardingContent) {
        Intent intent = new Intent(context, ForwardingProcessActivity.class);

        intent.putExtra(EXTRA_GROUP_ID, groupId.toString());
        intent.putExtra(EXTRA_FORWARDING_CONTENT, forwardingContent);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forwarding_process);

        setupView();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ShareData.getInstance().mIsForwardingPause) {
            mStatus = ForwardingStatus.PAUSED;
        }
        updateView();
    }

    private int getEditTextInteger(EditText editText) {
        if (editText == null) {
            return 0;
        }

        final CharSequence text = editText.getText();
        if (text == null) {
            return 0;
        }

        try {
            return Integer.parseInt(text.toString());
        } catch (Throwable e) {
            return 0;
        }
    }

    public void pauseForwarding() {
        mShareData.pauseForwardingTask();
        mStatus = ForwardingStatus.PAUSED;
        updateView();
        Toast.makeText(ForwardingProcessActivity.this, "已暂停", Toast.LENGTH_SHORT).show();
    }

    public void resumeForwarding() {
        mShareData.resumeForwardingTask();
        mStatus = ForwardingStatus.PROCESSING;
        updateView();
        Toast.makeText(ForwardingProcessActivity.this, "继续群发", Toast.LENGTH_SHORT).show();
    }

    private void setupView() {
        mStatusTV = findViewById(R.id.status_tv);
        mStartStopBtn = findViewById(R.id.start_stop_btn);
        mPauseResumeBtn = findViewById(R.id.pause_resume_btn);
        mRemainNumTV = findViewById(R.id.remaining_num_tv);
        mRecyclerView = findViewById(R.id.remaining_item_rv);
        mBundleSizeET = findViewById(R.id.et_bundle_size);
        mPauseTimeET = findViewById(R.id.et_pause_time);
        mDeltaTimeET = findViewById(R.id.et_delta_time);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mStartStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mStatus) {
                    case PAUSED:
                    case PROCESSING: {
                        mShareData.stopForwardingTask();
                        mStatus = ForwardingStatus.STOPED;
                        updateView();
                        Toast.makeText(ForwardingProcessActivity.this, "已停止", Toast.LENGTH_SHORT).show();
                    } break;

                    case FINISHED:
                    case STOPED:
                    case NOT_START: {
                        int bundleSize = getEditTextInteger(mBundleSizeET);
                        int pauseTime = getEditTextInteger(mPauseTimeET);
                        int deltaTime = getEditTextInteger(mDeltaTimeET);

                        mShareData.activeForwardingTask(ForwardingProcessActivity.this,
                                mUserGroup,
                                mForwardingContent,
                                bundleSize,
                                pauseTime,
                                deltaTime,
                                new ForwardingTask.OnForwardingTaskFinishedListener() {
                                    @Override
                                    public void onForwardingTaskFinished() {
                                        mStatus = ForwardingStatus.FINISHED;
                                        mShareData.clearData();
                                        updateView();
                                    }
                                });

                        mStatus = ForwardingStatus.PROCESSING;
                        updateView();
                        Toast.makeText(ForwardingProcessActivity.this, "已激活群发", Toast.LENGTH_SHORT).show();
                    } break;
                }
            }
        });

        mPauseResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mStatus) {
                    case PROCESSING: {
                        pauseForwarding();
                    } break;

                    case PAUSED: {
                        resumeForwarding();
                    }
                }
            }
        });
    }

    private void initData() {
        UUID groupId = UUID.fromString(getIntent().getStringExtra(EXTRA_GROUP_ID));
        mUserGroup = mUserGroupLab.getCloneUserItemsByUUID(groupId);
        mForwardingContent = getIntent().getStringExtra(EXTRA_FORWARDING_CONTENT);
    }

    private void updateView() {
        mStatusTV.setText(mStatus.toString());

        switch (mStatus) {
            case NOT_START: {
                mStartStopBtn.setText("开始");
                mPauseResumeBtn.setVisibility(View.INVISIBLE);
            } break;

            case PAUSED: {
                mStartStopBtn.setText("停止");
                mPauseResumeBtn.setText("继续");
                mPauseResumeBtn.setVisibility(View.VISIBLE);
            } break;

            case STOPED: {
                mStartStopBtn.setText("开始");
                mPauseResumeBtn.setVisibility(View.INVISIBLE);
            } break;

            case FINISHED: {
                mStartStopBtn.setVisibility(View.INVISIBLE);
                mPauseResumeBtn.setVisibility(View.INVISIBLE);
            } break;

            case PROCESSING: {
                mStartStopBtn.setText("停止");
                mPauseResumeBtn.setText("暂停");
                mPauseResumeBtn.setVisibility(View.VISIBLE);
            } break;
        }

        mRemainNumTV.setText("剩余人数： " + (mUserGroup == null ? 0 : mUserGroup.size()));
        mAdapter.updateData();
    }


    private enum ForwardingStatus {
        NOT_START("未开始"),
        PROCESSING("进行中"),
        PAUSED("已暂停"),
        FINISHED("已完成"),
        STOPED("已终止");

        private String mStatusStr;


        ForwardingStatus(String statusStr) {
            mStatusStr = statusStr;
        }


        @NotNull
        @Override
        public String toString() {
            return mStatusStr == null ? "null" : mStatusStr;
        }
    }


    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private List<UserItem> mUserItems = new ArrayList<>();


        public void updateData() {
            if (mUserGroup == null) {
                mUserItems = new ArrayList<>();
            } else {
                mUserItems = mUserGroup.getUserItems();
            }

            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_group_edit, parent, false);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bindData(getItem(position));
        }

        @Override
        public int getItemCount() {
            return mUserItems.size();
        }

        private UserItem getItem(int position) {
            return mUserItems.get(position);
        }



        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mFullNameTV;
            private TextView mDetailTV;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                mFullNameTV = itemView.findViewById(R.id.full_name_tv);
                mDetailTV = itemView.findViewById(R.id.detail_tv);
            }

            public void bindData(UserItem item) {
                mFullNameTV.setText(item.fullNickName + "  所在标签：" + item.labelText);
                mDetailTV.setText("xing: " + item.surname + ", name: " + item.name);
            }
        }
    }
}
