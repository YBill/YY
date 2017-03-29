package com.ioyouyun.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ioyouyun.R;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.PlatformSharedUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VersionActivity extends AppCompatActivity {

    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_msg)
    TextView tvMsg;

    boolean isDev = false;
    int count = 0;
    long oldTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        ButterKnife.bind(this);
        initView();
        initData();
        isDev = PlatformSharedUtil.getInstance().getDeveloper();
    }

    @OnClick({R.id.tv_version})
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - oldTime > 800) {
            oldTime = System.currentTimeMillis();
            count = 0;
        } else if (currentTime - oldTime < 800) {
            oldTime = System.currentTimeMillis();
            count++;
        }

        if (isDev) {
            Toast.makeText(VersionActivity.this, "您已经处于开发者模式", Toast.LENGTH_SHORT).show();
            if (count == 6) {
                count = 0;
                showDialog();
            }
        } else {
            if (count == 3) {
                count = 0;
                showDialog();
            }
        }
    }

    private void showDialog() {
        final EditText editText = new EditText(this);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Input Code").setView(editText)
                .setPositiveButton(getResources().getString(R.string.string_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        if ("developer".equals(text)) {
                            Toast.makeText(VersionActivity.this, "您已经处于开发者模式", Toast.LENGTH_SHORT).show();
                            PlatformSharedUtil.getInstance().setDeveloper(true);
                            setResult(RESULT_OK);
                            finish();
                        } else if ("closedev".equals(text)) {
                            Toast.makeText(VersionActivity.this, "您已经关闭开发者模式", Toast.LENGTH_SHORT).show();
                            PlatformSharedUtil.getInstance().setDeveloper(false);
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.string_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        alertDialog.show();
    }

    private void $setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        $setToolBar();
        tvMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void initData() {
        tvVersion.setText(FunctionUtil.getVersion());

        tvMsg.append(getResources().getString(R.string.version_1));
        tvMsg.append("\n");
        tvMsg.append("\n");
        tvMsg.append(getResources().getString(R.string.version_2));
        tvMsg.append("\n");
        tvMsg.append("\n");
        tvMsg.append(getResources().getString(R.string.version_3));
        tvMsg.append("\n");
        tvMsg.append("\n");
        tvMsg.append(getResources().getString(R.string.version_4));
        tvMsg.append("\n");
        tvMsg.append("\n");
        tvMsg.append(getResources().getString(R.string.version_5));
    }
}
