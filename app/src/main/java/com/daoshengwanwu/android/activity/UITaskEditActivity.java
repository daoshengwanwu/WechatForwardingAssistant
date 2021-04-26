package com.daoshengwanwu.android.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ForwardingContent;
import com.daoshengwanwu.android.model.ForwardingContentLab;
import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.model.UIForwardingTask;
import com.daoshengwanwu.android.model.UIForwardingTaskLab;
import com.daoshengwanwu.android.model.UserGroup;
import com.daoshengwanwu.android.model.UserGroupLab;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class UITaskEditActivity extends AppCompatActivity {
    private static final String EXTRA_UUID = "extra_uuid";
    private static final int REQUEST_SEL_GROUP = 0;
    private static final int REQUEST_SEL_CONTENT = 1;

    private UIForwardingTask mUIForwardingTask;

    private Button mSelGroupBtn;
    private Button mSelContentBtn;
    private Button mStartBtn;
    private Button mSelReceiverBtn;
    private EditText mEditText;


    public static Intent newIntent(Context context, UUID uuid) {
        Intent intent = new Intent(context, UITaskEditActivity.class);

        if (uuid != null) {
            intent.putExtra(EXTRA_UUID, uuid.toString());
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitask_edit);

        String uuidStr = getIntent().getStringExtra(EXTRA_UUID);
        if (uuidStr != null) {
            mUIForwardingTask = UIForwardingTaskLab.getInstance().getUIForwardingTask(UUID.fromString(uuidStr));
        }

        if (mUIForwardingTask == null) {
            mUIForwardingTask = new UIForwardingTask(
                    getApplicationContext(),
                    null,
                    new ForwardingContent(""),
                    "");

        }

        mSelContentBtn = findViewById(R.id.btn_sel_content);
        mSelGroupBtn = findViewById(R.id.btn_sel_group);
        mStartBtn = findViewById(R.id.btn_start_forwarding);
        mEditText = findViewById(R.id.edit_text);
        mSelReceiverBtn = findViewById(R.id.btn_start_select_receiver);

        mSelContentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(ForwardingContentListActivity.newIntent(UITaskEditActivity.this), REQUEST_SEL_CONTENT);
            }
        });

        mSelGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(GroupListActivity.newIntent(UITaskEditActivity.this), REQUEST_SEL_GROUP);
            }
        });

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUIForwardingTask.getForwardingContent() != null &&
                        mUIForwardingTask.getMergeUserGroup() != null) {

                    startActivity(ForwardingProcessActivity.newIntent(
                            UITaskEditActivity.this,
                            mUIForwardingTask.getMergeUserGroup().getUUID(),
                            mUIForwardingTask.getForwardingContent().getContent()));

                } else {
                    Toast.makeText(UITaskEditActivity.this,
                            "请指定群发内容和群发分组之后再开启群发", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSelReceiverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUIForwardingTask.getMergeUserGroup() != null) {
                    ShareData.getInstance().activeSelectReceiverTask(
                            UITaskEditActivity.this.getApplicationContext(),
                            mUIForwardingTask.getMergeUserGroup());
                }
            }
        });

        updateViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_SEL_CONTENT: {
                if (data == null) {
                    return;
                }

                UUID uuid = UUID.fromString(data.getStringExtra("forwarding_content_uuid"));
                mUIForwardingTask.setForwardingContent(ForwardingContentLab.getInstance().getForwardingContent(uuid));

                updateViews();
            } break;

            case REQUEST_SEL_GROUP: {
                if (data == null) {
                    return;
                }

                final List<UserGroup> userGroupList = readUserGroupsFromUUIDList(data.getStringExtra("user_group_uuid"));
                mUIForwardingTask.setUserGroupList(getApplicationContext(), userGroupList);
                updateViews();
            } break;
        }

        setUIForwardingTaskNameAndSaveTaskToLab(false);
    }

    @Nullable
    private List<UserGroup> readUserGroupsFromUUIDList(@NonNull final String uuidListStr) {
        if (TextUtils.isEmpty(uuidListStr)) {
            return null;
        }

        try {
             final List<UUID> uuidList = new Gson().fromJson(uuidListStr, new TypeToken<ArrayList<UUID>>() {}.getType());
             if (uuidList == null || uuidList.isEmpty()) {
                 return null;
             }

             final List<UserGroup> userGroupList = new ArrayList<>();
             for (UUID uuid : uuidList) {
                 final UserGroup userGroup = UserGroupLab.getInstance().getUserItemsByUUID(uuid);
                 if (userGroup == null) {
                     continue;
                 }

                 userGroupList.add(userGroup);
             }

             return userGroupList;
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forwarding_content_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (mUIForwardingTask != null && isSavable() &&
            UIForwardingTaskLab.getInstance().getUIForwardingTask(mUIForwardingTask.getId()) == null) {

            new AlertDialog.Builder(this)
                    .setTitle("是否保存内容")
                    .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setUIForwardingTaskNameAndSaveTaskToLab(true);
                            finish();
                        }
                    })
                    .create()
                    .show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            mUIForwardingTask.getForwardingContent().setContent(mEditText.getText().toString());
            setUIForwardingTaskNameAndSaveTaskToLab(true);
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    private boolean isSavable() {
        setUIForwardingTaskNameAndSaveTaskToLab(false);
        return mUIForwardingTask != null &&
                !": ".equals(mUIForwardingTask.getTaskName());
    }

    private void setUIForwardingTaskNameAndSaveTaskToLab(boolean isSave) {
        ForwardingContent content = mUIForwardingTask.getForwardingContent();
        UserGroup group = mUIForwardingTask.getMergeUserGroup();

        String name = "";

        if (group != null) {
            name += group.getGroupName();
        }

        if (content != null) {
            name += ": " + content.getContent();
        }

        mUIForwardingTask.setTaskName(name);

        if (isSave) {
            if (isSavable()) {
                UIForwardingTaskLab.getInstance().putForwrdingTaskLab(this, mUIForwardingTask);
                finish();
            } else {
                Toast.makeText(this, "请不要保存空任务", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateViews() {
        if (mUIForwardingTask.getForwardingContent() != null &&
                !TextUtils.isEmpty(mUIForwardingTask.getForwardingContent().getContent())) {

            mEditText.setVisibility(View.VISIBLE);
            mEditText.setText(mUIForwardingTask.getForwardingContent().getContent());
        }

        if (mUIForwardingTask.getMergeUserGroup() != null) {
            mSelGroupBtn.setText(mUIForwardingTask.getMergeUserGroup().getGroupName());
        }

        if (mUIForwardingTask.getMergeUserGroup() != null) {

            if (!TextUtils.isEmpty(mUIForwardingTask.getForwardingContent().getContent())) {
                mStartBtn.setVisibility(View.VISIBLE);
            }

            mSelReceiverBtn.setVisibility(View.VISIBLE);
        }
    }
}
