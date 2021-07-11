package com.daoshengwanwu.android.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ForwardingContentLab;
import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.model.UserGroup;
import com.daoshengwanwu.android.model.UserGroupLab;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.task.LoadLabelUsersTask;
import com.daoshengwanwu.android.task.RegLoadUsersTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;


public class GroupEditActivity extends AppCompatActivity {
    private static final String EXTRA_GROUP_ID = "extra_group_id";

    private EditText mGroupNameET;
    private EditText mKeywordET;
    private EditText mLabelNameET;
    private Button mImportBtn;
    private Button mRegImportBtn;
    private TextView mUserCountTV;
    private RecyclerView mRecyclerView;

    private Adapter mAdapter = new Adapter();
    private ShareData mShareData = ShareData.getInstance();
    private String mKeyword;
    private UserGroup mUserGroup;

    private String mForwardingContent = "";


    private List<PopupWindow> mPopupWindows = new ArrayList<>();


    public static Intent newIntent(@NonNull Context packageContext, @NonNull UUID groupId) {
        Intent intent = new Intent(packageContext, GroupEditActivity.class);

        intent.putExtra(EXTRA_GROUP_ID, groupId.toString());

        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.enter_forwarding: {
                if (TextUtils.isEmpty(mForwardingContent)) {
                    Toast.makeText(this, "请点击星号编辑群发内容后再进入群发.", Toast.LENGTH_LONG).show();
                    break;
                }

                Intent intent = ForwardingProcessActivity.newIntent(GroupEditActivity.this, mUserGroup.getUUID(), mForwardingContent);
                startActivity(intent);
            } break;

            case R.id.edit_content: {
                View rootView = LayoutInflater.from(this).inflate(R.layout.dialog_enter_forwarding, null, false);
                final EditText et = rootView.findViewById(R.id.content_et);
                et.setText(mForwardingContent);

                final AlertDialog dialog = new AlertDialog.Builder(this).
                        setTitle("设置群发内容").
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mForwardingContent = et.getText().toString();
                                ForwardingContentLab.getInstance().putContent(GroupEditActivity.this, mUserGroup.getUUID(), mForwardingContent);
                                if (!TextUtils.isEmpty(mForwardingContent)) {
                                    Toast.makeText(GroupEditActivity.this, "成功", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).
                        setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        setView(rootView).
                        setCancelable(false).
                        create();

                dialog.show();
            } break;
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    @Override
    public void finish() {
        if (mUserGroup.getUserItems().size() <= 0) {
            UserGroupLab.getInstance().removeUserGroups(getApplicationContext(), Arrays.asList(new UserGroup[] { mUserGroup }));
        }

        super.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        mUserGroup = UserGroupLab.getInstance().getUserItemsByUUID(
                UUID.fromString(getIntent().getStringExtra(EXTRA_GROUP_ID)));

        mGroupNameET = findViewById(R.id.group_name_et);
        mKeywordET = findViewById(R.id.search_keyword_et);
        mLabelNameET = findViewById(R.id.label_name_et);
        mImportBtn = findViewById(R.id.import_btn);
        mRegImportBtn = findViewById(R.id.btn_reg_import);
        mRecyclerView = findViewById(R.id.users_rv);
        mUserCountTV = findViewById(R.id.user_count_tv);


        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mImportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String labelName = mLabelNameET.getText() + "";

                if (TextUtils.isEmpty(labelName)) {
                    Toast.makeText(GroupEditActivity.this, "请输入正确的标签名称", Toast.LENGTH_LONG).show();
                    return;
                }

                mShareData.activeLoadToForwardingTask(
                        GroupEditActivity.this,
                        labelName,
                        new LoadLabelUsersTask.OnLabelUsersInfoLoadFinishedListener() {

                    @Override
                    public void onLabelUsersInfoLoadFinished(Set<UserItem> labelUsersInfo) {
                        final String groupName = mUserGroup.getGroupName();
                        if (groupName == null || groupName.startsWith("新建分组#")) {
                            mUserGroup.setGroupName(labelName);
                        } else {
                            mUserGroup.setGroupName(groupName + "," + labelName);
                        }

                        mUserGroup.mergeUserItems(labelUsersInfo);
                        UserGroupLab.getInstance().putOrMergeUserItems(GroupEditActivity.this, mUserGroup);
                        updateView();
                        Toast.makeText(GroupEditActivity.this, "导入联系人成功", Toast.LENGTH_LONG).show();
                    }
                });

                Toast.makeText(GroupEditActivity.this, "激活成功，现在请手动切换到微信的" + labelName + "标签页下", Toast.LENGTH_LONG).show();
            }
        });

        mRegImportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String regStr = mLabelNameET.getText() + "";

                if (TextUtils.isEmpty(regStr)) {
                    Toast.makeText(GroupEditActivity.this, "请输入正确的正则表达式", Toast.LENGTH_LONG).show();
                    return;
                }

                Pattern pattern = null;
                try {
                    pattern = Pattern.compile(regStr);
                } catch (Throwable e) {
                    // ignore
                }

                if (pattern == null) {
                    Toast.makeText(GroupEditActivity.this, "请输入正确的正则表达式", Toast.LENGTH_LONG).show();
                    return;
                }

                mShareData.activeLoadForwardingUserWithRegTask(
                        GroupEditActivity.this,
                        pattern,
                        new RegLoadUsersTask.OnUsersInfoLoadFinishedListener() {
                            @Override
                            public void onUsersInfoLoadFinished(Set<UserItem> labelUsersInfo) {
                                final String groupName = mUserGroup.getGroupName();
                                if (groupName == null || groupName.startsWith("新建分组#")) {
                                    mUserGroup.setGroupName(regStr);
                                } else if (!groupName.contains(regStr)) {
                                    mUserGroup.setGroupName(groupName + "," + regStr);
                                }

                                mUserGroup.mergeUserItems(labelUsersInfo);
                                UserGroupLab.getInstance().putOrMergeUserItems(GroupEditActivity.this, mUserGroup);
                                updateView();
                                Toast.makeText(GroupEditActivity.this, "导入联系人成功", Toast.LENGTH_LONG).show();
                            }
                        });

                Toast.makeText(GroupEditActivity.this, "激活成功，现在请手动切换到微信的通讯录页下", Toast.LENGTH_LONG).show();
            }
        });

        mGroupNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mUserGroup.setGroupName(s.toString());
                UserGroupLab.getInstance().updateGroup(GroupEditActivity.this, mUserGroup);
            }
        });

        mKeywordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mKeyword = s.toString();
                mAdapter.setKeyword(mKeyword);
            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for (PopupWindow window : mPopupWindows) {
                    window.dismiss();
                }
                mPopupWindows.clear();
                return false;
            }
        });

        updateView();

        mForwardingContent = ForwardingContentLab.getInstance().getContent(mUserGroup.getUUID());
    }

    private void updateView() {
        mAdapter.setData(mUserGroup);
        mUserCountTV.setText("人数： " + mAdapter.getItemCount());
        mGroupNameET.setText(mUserGroup.getGroupName());
    }


    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private String mKeyword;
        private UserGroup mUserGroup;
        private List<UserItem> mUserItems;


        public void setKeyword(String keyword) {
            mKeyword = keyword;
            notifyDataSetChanged();
        }

        public void setData(UserGroup userGroup) {
            if (userGroup == null) {
                return;
            }

            mUserGroup = userGroup;
            mUserItems = new ArrayList<>(mUserGroup.getUserItems());
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.item_user_group_edit, parent, false);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            UserItem item = getItem(position);
            if (!TextUtils.isEmpty(mKeyword)) {
                if (!item.fullNickName.contains(mKeyword) && !item.labelText.contains(mKeyword)) {
                    setViewHeight(holder.itemView, 0);
                } else {
                    setViewHeight(holder.itemView, ViewGroup.LayoutParams.WRAP_CONTENT);
                    holder.bindData(getItem(position));
                }
            } else {
                setViewHeight(holder.itemView, ViewGroup.LayoutParams.WRAP_CONTENT);
                holder.bindData(getItem(position));
            }
        }

        @Override
        public int getItemCount() {
            return mUserItems == null ? 0 : mUserItems.size();
        }

        private UserItem getItem(int position) {
            return mUserItems.get(position);
        }

        private void setViewHeight(View view, int height) {
            if (view == null) {
                return;
            }

            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = height;
            view.setLayoutParams(params);

            if (height == 0) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            private UserItem mUserItem;
            private TextView mFullNameTV;
            private TextView mDetailTV;


            public ViewHolder(@NonNull final View itemView) {
                super(itemView);

                mFullNameTV = itemView.findViewById(R.id.full_name_tv);
                mDetailTV = itemView.findViewById(R.id.detail_tv);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View rootView = LayoutInflater.from(GroupEditActivity.this).inflate(R.layout.popup_window, null, false);
                        final PopupWindow window = new PopupWindow(rootView, 400, ViewGroup.LayoutParams.WRAP_CONTENT);
                        View delView = rootView.findViewById(R.id.delete_tv);
                        delView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mUserGroup.removeUserItem(mUserItem.fullNickName, mUserItem.labelText);
                                mUserItems = mUserGroup.getUserItems();
                                updateView();
                                window.dismiss();
                            }
                        });


                        window.showAsDropDown(itemView, 100, 10);
                        mPopupWindows.add(window);
                    }
                });
            }

            public void bindData(UserItem userItem) {
                mUserItem = userItem;

                mFullNameTV.setText(mUserItem.fullNickName + "  所在标签：" + mUserItem.labelText);
                mDetailTV.setText("xing: " + mUserItem.surname + ", name: " + mUserItem.name);
            }
        }
    }
}
