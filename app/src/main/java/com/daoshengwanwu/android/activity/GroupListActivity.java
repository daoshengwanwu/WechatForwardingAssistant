package com.daoshengwanwu.android.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.UserGroup;
import com.daoshengwanwu.android.model.UserGroupLab;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class GroupListActivity extends AppCompatActivity {
    private final Adapter mAdapter = new Adapter();
    private final Set<UserGroup> mSelectedUserGroups = new HashSet<>();


    public static Intent newIntent(Context context) {
        return new Intent(context, GroupListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.updateView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.updateView();
        UserGroupLab.getInstance().saveAllGroupToSP(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add: {
                UserGroup group = UserGroupLab.getInstance().createUserGroup(this, "新建分组#" + UserGroupLab.getInstance().size());
                mAdapter.updateView();
                Intent intent = GroupEditActivity.newIntent(this, group.getUUID());
                startActivity(intent);
            } break;

            case R.id.delete: {
                if (mSelectedUserGroups.size() <= 0) {
                    Toast.makeText(this, "请先选中群组", Toast.LENGTH_LONG).show();
                    break;
                }

                UserGroupLab.getInstance().removeUserGroups(this, mSelectedUserGroups);
                mAdapter.updateView();
            } break;

            case R.id.apply: {
                setSelectedUserGroupsResultAndFinish();
            } break;
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    private void setSelectedUserGroupsResultAndFinish() {
        final Set<UserGroup> selectedUserGroupSet = mSelectedUserGroups;
        if (selectedUserGroupSet == null || selectedUserGroupSet.isEmpty()) {
            Toast.makeText(this, "请选中至少一个分组", Toast.LENGTH_LONG).show();
            return;
        }
        
        final List<UUID> selGroupUUIDList = new ArrayList<>();
        for (UserGroup userGroup : selectedUserGroupSet) {
            if (userGroup.getUUID() != null) {
                selGroupUUIDList.add(userGroup.getUUID());
            }
        }
        
        setSelectedUserGroupsResultAndFinish(selGroupUUIDList);
    }

    private void setSelectedUserGroupsResultAndFinish(@Nullable final List<UUID> userGroups) {
        if (userGroups != null && userGroups.size() > 0) {
            final Intent data = new Intent();

            data.putExtra("user_group_uuid", new Gson().toJson(userGroups));

            setResult(RESULT_OK, data);
            finish();
        }

        finish();
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private List<UserGroup> mData = new ArrayList<>();


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_list, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bindData(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public void updateView() {
            mData = UserGroupLab.getInstance().getAllUserGroups();
            notifyDataSetChanged();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            private UserGroup mCurGroup;

            private TextView mTitleTV;
            private TextView mSizeTV;
            private CheckBox mCheckBox;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                mTitleTV = itemView.findViewById(R.id.group_title_tv);
                mSizeTV = itemView.findViewById(R.id.group_size_tv);
                mCheckBox = itemView.findViewById(R.id.checkbox);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(GroupEditActivity.newIntent(
                                GroupListActivity.this, mCurGroup.getUUID()));

                    }
                });

                mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mSelectedUserGroups.add(mCurGroup);
                        } else {
                            mSelectedUserGroups.remove(mCurGroup);
                        }
                    }
                });
            }

            public void bindData(UserGroup group) {
                mCurGroup = group;

                mTitleTV.setText(mCurGroup.getGroupName());
                mSizeTV.setText("人数： " + mCurGroup.size());

                if (mSelectedUserGroups.contains(mCurGroup)) {
                    mCheckBox.setChecked(true);
                } else {
                    mCheckBox.setChecked(false);
                }
            }
        }
    }
}
