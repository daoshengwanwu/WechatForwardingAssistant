package com.daoshengwanwu.android.activity;


import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.*;

import java.util.UUID;


public class UITaskEditActivity extends AppCompatActivity {
    private static final String EXTRA_UUID = "extra_uuid";
    private static final int REQUEST_SEL_GROUP = 0;
    private static final int REQUEST_SEL_CONTENT = 1;

    private UIForwardingTask mUIForwardingTask;

    private Button mSelGroupBtn;
    private Button mSelContentBtn;
    private Button mStartBtn;
    private EditText mEditText;


    public static Intent newIntent(Context context, UUID uuid) {
        Intent intent = new Intent(context, UITaskEditActivity.class);

        intent.putExtra(EXTRA_UUID, uuid.toString());

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitask_edit);

        mUIForwardingTask = UIForwardingTaskLab.getInstance().getUIForwardingTask(UUID.fromString(getIntent().getStringExtra(EXTRA_UUID)));

        mSelContentBtn = findViewById(R.id.btn_sel_content);
        mSelGroupBtn = findViewById(R.id.btn_sel_group);
        mStartBtn = findViewById(R.id.btn_start_forwarding);
        mEditText = findViewById(R.id.edit_text);

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
                //TODO:: 打开群发状态控制页面
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

                UUID uuid = UUID.fromString(data.getStringExtra("user_group_uuid"));
                mUIForwardingTask.setUserGroup(UserGroupLab.getInstance().getUserItemsByUUID(uuid));
                updateViews();
            } break;
        }

        setUIForwardingTaskName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forwarding_content_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            mUIForwardingTask.getForwardingContent().setContent(mEditText.getText().toString());
            setUIForwardingTaskName();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUIForwardingTaskName() {
        ForwardingContent content = mUIForwardingTask.getForwardingContent();
        UserGroup group = mUIForwardingTask.getUserGroup();

        String name = "";

        if (group != null) {
            name += ": " + group.getGroupName();
        }

        if (content != null) {
            name += content.getContent();
        }

        mUIForwardingTask.setTaskName(name);

        UIForwardingTaskLab.getInstance().putForwrdingTaskLab(this, mUIForwardingTask);
    }

    private void updateViews() {
        if (mUIForwardingTask.getForwardingContent() != null) {
            mEditText.setVisibility(View.VISIBLE);
            mStartBtn.setVisibility(View.VISIBLE);
            mEditText.setText(mUIForwardingTask.getForwardingContent().getContent());
        }

        if (mUIForwardingTask.getUserGroup() != null) {
            mSelGroupBtn.setText(mUIForwardingTask.getUserGroup().getGroupName());
        }
    }
}
