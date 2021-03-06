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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.UserGroup;
import com.daoshengwanwu.android.model.UserGroupLab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class GroupListActivity extends AppCompatActivity {
    private Adapter mAdapter = new Adapter();
    private RecyclerView mRecyclerView;
    private Set<UserGroup> mSelectedUserGroups = new HashSet<>();

    private Mode mCurMode = Mode.MODE_SELECT;


    public static Intent newIntent(Context context) {
        return new Intent(context, GroupListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        mRecyclerView = findViewById(R.id.recycler_view);
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
                UserGroupLab.getInstance().removeUserGroups(this, mSelectedUserGroups);
                mAdapter.updateView();
            } break;

            case R.id.switch_mode: {
                if (mCurMode == Mode.MODE_SELECT) {
                    mCurMode = Mode.MODE_EDIT;
                    item.setTitle("退出编辑");
                    break;
                }

                if (mCurMode == Mode.MODE_EDIT) {
                    mCurMode = Mode.MODE_SELECT;
                    item.setTitle("编辑");
                    break;
                }
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
                        if (mCurMode == Mode.MODE_EDIT) {
                            startActivity(GroupEditActivity.newIntent(
                                    GroupListActivity.this, mCurGroup.getUUID()));
                        } else if (mCurMode == Mode.MODE_SELECT) {
                            Intent data = new Intent();

                            data.putExtra("user_group_uuid", mCurGroup.getUUID().toString());

                            setResult(RESULT_OK, data);
                            finish();
                        }
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

    private enum Mode {
        MODE_EDIT, MODE_SELECT
    }
}
