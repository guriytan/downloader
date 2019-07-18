package org.guriytan.downloader.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.obsez.android.lib.filechooser.ChooserDialog;

import org.guriytan.downloader.R;
import org.guriytan.downloader.Constant;
import org.guriytan.downloader.application.App;

/**
 * 设置页面
 */
public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView local_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ActionBar actionBar = getSupportActionBar();
        // 返回主页
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // 全局设置
        sharedPreferences = App.getSharedPreferences();
        initialTextView();
        initialSwitch();
        initialSeekBar();
    }

    /**
     * 用finish()方法销毁当前页面
     *
     * @param item 菜单选项
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 物理返回按钮行为
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * 下载路径设置
     */
    private void initialTextView() {
        final String path = sharedPreferences.getString(Constant.DOWNLOAD_PATH_KEY, Constant.DOWNLOAD_PATH);
        local_path = findViewById(R.id.local_path);
        local_path.setText(path);
        local_path.setOnClickListener(v -> new ChooserDialog(SettingsActivity.this)
                .withFilter(true, false)
                .withStartFile(path)
                .withChosenListener((folder, file) -> {
                    local_path.setText(folder);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constant.DOWNLOAD_PATH_KEY, folder);
                    editor.apply();
                })
                .build()
                .show());
    }

    /**
     * 是否允许移动网络下载设置
     */
    private void initialSwitch() {
        boolean check = sharedPreferences.getBoolean(Constant.ALLOW_MOBILE_NET_KEY, Constant.ALLOW_MOBILE_NET);
        Switch allow_mobile = findViewById(R.id.mobile_net);
        allow_mobile.setChecked(check);
        allow_mobile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constant.ALLOW_MOBILE_NET_KEY, isChecked);
            editor.apply();
        });
    }

    /**
     * 同时最大下载数设置
     */
    private void initialSeekBar() {
        int maximum = sharedPreferences.getInt(Constant.MAXIMUM_DOWNLOAD_KEY, Constant.MAXIMUM_DOWNLOAD);
        SeekBar seekBar = findViewById(R.id.maximum_download);
        seekBar.setProgress(maximum);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Constant.MAXIMUM_DOWNLOAD_KEY, seekBar.getProgress());
                editor.apply();
            }
        });
    }
}