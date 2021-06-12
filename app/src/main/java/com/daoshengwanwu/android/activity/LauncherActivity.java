package com.daoshengwanwu.android.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.daoshengwanwu.android.FloatWindowManager;
import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.page.Page;
import com.daoshengwanwu.android.service.AuxiliaryService;
import com.daoshengwanwu.android.task.LoadImportViewResourceIdNameTask;
import com.daoshengwanwu.android.task.LoadPageFeatureTask;
import com.daoshengwanwu.android.task.Task;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;


public class LauncherActivity extends AppCompatActivity {
    private ViewGroup mMainMenuContainer;
    private Button mYesButton;
    private Button mCleanButton;
    private Button mForwardingButton;

    private ViewGroup mInitAssistantContainer;
    private Button mInitAssistantBtn;

    private ViewGroup mOpenAccessibilitySettingsContainer;
    private Button mOpenAccessibilitySettingsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        setupView();
        updateView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateView();
    }

    private void setupView() {
        mMainMenuContainer = findViewById(R.id.cl_main_menu_container);
        mYesButton = findViewById(R.id.btn_yes_assistant);
        mCleanButton = findViewById(R.id.btn_clean_assistant);
        mForwardingButton = findViewById(R.id.btn_forward_assistant);

        mOpenAccessibilitySettingsContainer = findViewById(R.id.rl_open_accessibility_settings_container);
        mOpenAccessibilitySettingsBtn = findViewById(R.id.btn_open_accessibility_settings);

        mInitAssistantContainer = findViewById(R.id.rl_init_assistant_container);
        mInitAssistantBtn = findViewById(R.id.btn_init_assistant);

        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YesActivity.launchYesActivity(LauncherActivity.this);
            }
        });

        mCleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CleanActivity.launchCleanActivity(LauncherActivity.this);
            }
        });

        mForwardingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UITaskListActivity.launchForwardingTaskListActivity(LauncherActivity.this);
            }
        });

        mOpenAccessibilitySettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AuxiliaryService.isAccessibilitySettingsOn(getApplicationContext())) {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    updateView();
                }
            }
        });

        mInitAssistantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isWechatAssistantInited()) {
                    final FloatWindowManager floatWindowManager = FloatWindowManager.getInstance();
                    if (!floatWindowManager.isShown()) {
                        floatWindowManager.show();
                    }

                    ShareData.getInstance().activeLoadPageFeautresTask(getApplicationContext(),
                            new LoadPageFeatureTask.OnLoadPageFeatureFinishedListener() {
                        @Override
                        public void onLoadPageFeatureFinished() {
                            ShareData.getInstance().clearData();
                            floatWindowManager.setNextButtonOnClickListener(null);
                            floatWindowManager.setButtonOnClickListener(null);

                            ShareData.getInstance().activeLoadImportViewResourceIdNameTask(getApplicationContext(), new LoadImportViewResourceIdNameTask.OnLoadImportViewResourceIdFinishListener() {
                                @Override
                                public void onLoadImportViewResourceIdFinished() {
                                    ShareData.getInstance().clearData();
                                    floatWindowManager.setNextButtonOnClickListener(null);
                                    floatWindowManager.setButtonOnClickListener(null);

                                    if (Page.isAllPagesReady()) {
                                        floatWindowManager.setText("初始化完成");
                                        floatWindowManager.hide();
                                        updateView();
                                    }
                                }
                            });
                        }
                    });

                } else {
                    updateView();
                }
            }
        });
    }

    private void updateView() {
        if (!AuxiliaryService.isAccessibilitySettingsOn(getApplicationContext())) {
            updateViewToOpenAccessibilitySettings();
        } else if (!isWechatAssistantInited()) {
            updateViewToInitAssistant();
        } else {
            updateViewToMainMenu();
        }
    }

    private boolean isWechatAssistantInited() {
        return Page.isAllPagesReady();
    }

    private void hideAllContainer() {
        mInitAssistantContainer.setVisibility(View.INVISIBLE);
        mMainMenuContainer.setVisibility(View.INVISIBLE);
        mOpenAccessibilitySettingsContainer.setVisibility(View.INVISIBLE);
    }

    private void updateViewToOpenAccessibilitySettings() {
        hideAllContainer();

        mOpenAccessibilitySettingsContainer.setVisibility(View.VISIBLE);
    }

    private void updateViewToInitAssistant() {
        hideAllContainer();

        mInitAssistantContainer.setVisibility(View.VISIBLE);
    }

    private void updateViewToMainMenu() {
        hideAllContainer();

        mMainMenuContainer.setVisibility(View.VISIBLE);
    }
}
