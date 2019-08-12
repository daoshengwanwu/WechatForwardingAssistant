package com.daoshengwanwu.android.activity;


import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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

        intent.putExtra(EXTRA_UUID, id.toString());

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forwarding_edit);

        Intent intent = getIntent();
        mForwardingContent = ForwardingContentLab.getInstance().getForwardingContent(UUID.fromString(intent.getStringExtra(EXTRA_UUID)));

        mEditText = findViewById(R.id.edit_text);
        mEditText.setText(mForwardingContent.getContent());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forwarding_content_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            ForwardingContentLab.getInstance().putForwardingContent(this, mForwardingContent);
        }
        return super.onOptionsItemSelected(item);
    }
}
