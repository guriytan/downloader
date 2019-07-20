package org.guriytan.downloader.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.obsez.android.lib.filechooser.ChooserDialog;

import org.guriytan.downloader.R;
import org.guriytan.downloader.util.AppTools;

/**
 * 设置页面
 */
public class SettingsActivity extends AppCompatActivity {

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
        final String path = AppTools.getDownloadPath();
        local_path = findViewById(R.id.local_path);
        local_path.setText(path);
        local_path.setOnClickListener(v -> new ChooserDialog(SettingsActivity.this)
                .withFilter(true, false)
                .withStartFile(path)
                .withChosenListener((folder, file) -> {
                    local_path.setText(folder);
                    AppTools.setDownloadPath(folder);
                })
                .build()
                .show());
    }

    /**
     * 是否允许移动网络下载设置
     */
    private void initialSwitch() {
        boolean check = AppTools.isAllowMobileNet();
        Switch allow_mobile = findViewById(R.id.mobile_net);
        allow_mobile.setChecked(check);
        allow_mobile.setOnCheckedChangeListener((buttonView, isChecked) -> AppTools.setAllowMobileNet(isChecked));
    }

    /**
     * 同时最大下载数、下载任务线程数设置
     */
    private void initialSeekBar() {
        int maximum = AppTools.getMaximumDownload();
        SeekBar maximumDownload = findViewById(R.id.maximum_download);
        maximumDownload.setProgress(maximum);
        maximumDownload.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AppTools.setMaximumDownload(seekBar.getProgress());
            }
        });
        int thread = AppTools.getThreadNumber();
        SeekBar threadNumber = findViewById(R.id.thread_number);
        threadNumber.setProgress(thread);
        threadNumber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AppTools.setThreadNumber(seekBar.getProgress());
            }
        });
    }
}