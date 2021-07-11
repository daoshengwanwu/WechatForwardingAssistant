package com.daoshengwanwu.android.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.model.UserGroup;
import com.daoshengwanwu.android.model.UserGroupLab;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.task.ForwardingTask;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;


public class ForwardingProcessActivity extends AppCompatActivity {
    private static final String EXTRA_GROUP_ID = "extra_group_id";
    private static final String EXTRA_FORWARDING_CONTENT = "extra_forwarding_content";


    public static Intent newIntent(Context context, UUID groupId, String forwardingContent) {
        Intent intent = new Intent(context, ForwardingProcessActivity.class);

        intent.putExtra(EXTRA_GROUP_ID, groupId.toString());
        intent.putExtra(EXTRA_FORWARDING_CONTENT, forwardingContent);

        return intent;
    }


    private UserGroupLab mUserGroupLab = UserGroupLab.getInstance();
    private UserGroup mUserGroup;
    private final List<Pattern> mRegPatterns = new ArrayList<>();
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
    private LinearLayout mRegRVContainer;
    private RecyclerView mRegRV;
    private final Adapter mAdapter = new Adapter();
    private final RegAdapter mRegAdapter = new RegAdapter();

    private ShareData mShareData = ShareData.getInstance();


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        if (item.getItemId() == R.id.save_current_group) {
            saveCurrentUserGroup(null);
            Toast.makeText(this, "保存当前群组成功", Toast.LENGTH_LONG).show();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mUserGroup != null && mUserGroup.size() > 0) {
            getMenuInflater().inflate(R.menu.menu_forwarding_progress, menu);
            return true;
        }

        return super.onCreateOptionsMenu(menu);
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

    private void saveCurrentUserGroup(final String groupName) {
        final UserGroup curUserGroup = new UserGroup(mUserGroup);
        if (curUserGroup ==  null) {
            return;
        }

        if (!TextUtils.isEmpty(groupName)) {
            curUserGroup.setGroupName(groupName);
        } else {
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH) + 1;
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            final int hour = calendar.get(Calendar.HOUR_OF_DAY);
            final int min = calendar.get(Calendar.MINUTE);
            final int second = calendar.get(Calendar.SECOND);

            String suffix = "->saved at: " + String.format(
                    Locale.getDefault(),
                    "%02d%02d%02d,%02d:%02d:%02d",
                    year % 100,
                    month,
                    day,
                    hour,
                    min,
                    second);

            curUserGroup.setGroupName(curUserGroup.getGroupName() + suffix);
        }

        mUserGroupLab.updateGroup(getApplicationContext(), curUserGroup);
    }

    private void modifyRegItem(final int position) {
        final EditText contentView = new EditText(this);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        contentView.setLayoutParams(layoutParams);
        contentView.setHint("在此输入正则表达式");
        try {
            contentView.setText(mRegPatterns.get(position).toString());
        } catch (Throwable e) {
            // ignore
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("编辑正则匹配项")
                .setView(contentView)
                .create();

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mStatus != ForwardingStatus.NOT_START &&
                        mStatus != ForwardingStatus.FINISHED &&
                        mStatus != ForwardingStatus.STOPED) {

                    // 当前群发任务已经开始，不可以再添加匹配项
                    SingleSubThreadUtil.showToast(getApplicationContext(), "当前群发任务已开始，不可再添加匹配项", Toast.LENGTH_LONG);
                    return;

                }

                try {
                    final String regStr = contentView.getText().toString();
                    Pattern pattern = Pattern.compile(regStr);
                    mRegPatterns.set(position, pattern);
                    alertDialog.dismiss();
                    updateView();
                } catch (Throwable e) {
                    SingleSubThreadUtil.showToast(ForwardingProcessActivity.this, "请检查正则表达式是否正确: " + e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        });

        alertDialog.show();
    }

    private void addRegUser() {
        if (mStatus != ForwardingStatus.NOT_START &&
            mStatus != ForwardingStatus.FINISHED &&
            mStatus != ForwardingStatus.STOPED) {

            // 当前群发任务已经开始，不可以再添加匹配项
            SingleSubThreadUtil.showToast(getApplicationContext(), "当前群发任务已开始，不可再添加匹配项", Toast.LENGTH_LONG);
            return;

        }

        mRegPatterns.add(Pattern.compile(""));
        modifyRegItem(mRegPatterns.size() - 1);
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

        Toast.makeText(ForwardingProcessActivity.this, "已暂停", Toast.LENGTH_LONG).show();
    }

    public void resumeForwarding() {
        mShareData.resumeForwardingTask();
        mStatus = ForwardingStatus.PROCESSING;
        updateView();

        Toast.makeText(ForwardingProcessActivity.this, "继续群发", Toast.LENGTH_LONG).show();
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
        mRegRV = findViewById(R.id.rv_reg);
        mRegRVContainer = findViewById(R.id.ll_reg_rv_container);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mRegRV.setLayoutManager(new LinearLayoutManager(this));
        mRegRV.setAdapter(mRegAdapter);

        mStartStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mStatus) {
                    case PAUSED:
                    case PROCESSING: {
                        mShareData.stopForwardingTask();
                        mStatus = ForwardingStatus.STOPED;
                        updateView();
                        Toast.makeText(ForwardingProcessActivity.this, "已停止", Toast.LENGTH_LONG).show();
                    } break;

                    case FINISHED:
                    case STOPED:
                    case NOT_START: {
                        int bundleSize = getEditTextInteger(mBundleSizeET);
                        int pauseTime = getEditTextInteger(mPauseTimeET);
                        int deltaTime = getEditTextInteger(mDeltaTimeET);

                        mShareData.activeForwardingTask(ForwardingProcessActivity.this,
                                mUserGroup,
                                mRegPatterns,
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
                        Toast.makeText(ForwardingProcessActivity.this, "已激活群发", Toast.LENGTH_LONG).show();
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
        if (mRegPatterns.size() > 0) {
            mRegRVContainer.setVisibility(View.VISIBLE);
        } else {
            mRegRVContainer.setVisibility(View.GONE);
        }

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
        mRegAdapter.notifyDataSetChanged();
        supportInvalidateOptionsMenu();
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


    private class RegAdapter extends RecyclerView.Adapter<RegAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_group_edit, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bindData(mRegPatterns.get(position));
        }

        @Override
        public int getItemCount() {
            return mRegPatterns.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mFullNameTV;
            private TextView mDetailTV;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                mFullNameTV = itemView.findViewById(R.id.full_name_tv);
                mDetailTV = itemView.findViewById(R.id.detail_tv);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modifyRegItem(getAdapterPosition());
                    }
                });
            }

            private void bindData(Pattern pattern) {
                mFullNameTV.setText(pattern.toString());
            }
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
