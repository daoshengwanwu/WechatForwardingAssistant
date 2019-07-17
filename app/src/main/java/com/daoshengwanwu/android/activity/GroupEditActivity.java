package com.daoshengwanwu.android.activity;


import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.model.UserGroup;
import com.daoshengwanwu.android.model.UserGroupLab;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.task.LoadLabelUsersTask;

import java.util.*;


public class GroupEditActivity extends AppCompatActivity {
    private EditText mGroupNameET;
    private EditText mKeywordET;
    private EditText mLabelNameET;
    private Button mImportBtn;
    private TextView mUserCountTV;
    private RecyclerView mRecyclerView;

    private Adapter mAdapter = new Adapter();
    private ShareData mShareData = ShareData.getInstance();
    private String mKeyword;
    private final UserGroup mUserGroup = new UserGroup("");


    private List<PopupWindow> mPopupWindows = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        mGroupNameET = findViewById(R.id.group_name_et);
        mKeywordET = findViewById(R.id.search_keyword_et);
        mLabelNameET = findViewById(R.id.label_name_et);
        mImportBtn = findViewById(R.id.import_btn);
        mRecyclerView = findViewById(R.id.users_rv);
        mUserCountTV = findViewById(R.id.user_count_tv);


        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mImportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String labelName = mLabelNameET.getText() + "";
                mShareData.activeLoadToForwardingTask(
                        GroupEditActivity.this,
                        labelName,
                        new LoadLabelUsersTask.OnLabelUsersInfoLoadFinishedListener() {

                    @Override
                    public void onLabelUsersInfoLoadFinished(Set<UserItem> labelUsersInfo) {
                        mUserGroup.mergeUserItems(labelUsersInfo);
                        UserGroupLab.getInstance().putOrMergeUserItems(mUserGroup);
                        updateView();
                    }
                });
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
    }

    private void updateView() {
        mAdapter.setData(mUserGroup);
        mUserCountTV.setText("人数： " + mAdapter.getItemCount());
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
                        final PopupWindow window = new PopupWindow(rootView, 200, ViewGroup.LayoutParams.WRAP_CONTENT);
                        View delView = rootView.findViewById(R.id.delete_tv);
                        delView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mUserGroup.removeUserItem(mUserItem.fullNickName, mUserItem.labelText);
                                mUserItems = new ArrayList<>(mUserGroup.getUserItems());
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
