package com.daoshengwanwu.android.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ForwardingContent;
import com.daoshengwanwu.android.model.ForwardingContentLab;

import java.util.UUID;


public class ForwardingEditActivity extends AppCompatActivity {
    private static final String EXTRA_UUID = "extra_uuid";
    private EditText mEditText;
    private ForwardingContent mForwardingContent;


    public static Intent newIntent(Context context, UUID id) {
        Intent intent = new Intent(context, ForwardingEditActivity.class);

        if (id != null) {
            intent.putExtra(EXTRA_UUID, id.toString());
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forwarding_edit);

        Intent intent = getIntent();
        String uuidStr = intent.getStringExtra(EXTRA_UUID);
        if (!TextUtils.isEmpty(uuidStr)) {
            mForwardingContent = ForwardingContentLab.getInstance().getForwardingContent(UUID.fromString(uuidStr));
        }

        if (mForwardingContent == null) {
            mForwardingContent = new ForwardingContent("");
        }

        mEditText = findViewById(R.id.edit_text);
        mEditText.setText(mForwardingContent.getContent());
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mForwardingContent.setContent(s.toString());
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forwarding_content_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            if (!TextUtils.isEmpty(mForwardingContent.getContent())) {
                ForwardingContentLab.getInstance().putForwardingContent(this, mForwardingContent);
            }

            finish();
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }
}
