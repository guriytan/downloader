package org.guriytan.downloader.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.obsez.android.lib.filechooser.ChooserDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.guriytan.downloader.Constant;
import org.guriytan.downloader.R;
import org.guriytan.downloader.adapter.DownloadAdapter;
import org.guriytan.downloader.entity.Result;
import org.guriytan.downloader.manager.DownloadManager;
import org.guriytan.downloader.util.AppTools;
import org.guriytan.downloader.util.FileUtil;

import java.io.File;

/**
 * 主页面
 */
public class MainActivity extends AppCompatActivity {
    private Context context;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取数据库操作类，用于退出时使下载任务全部暂停
        downloadManager = DownloadManager.getInstance();
        // 创建下载文件夹
        FileUtil.mkdirs(new File(AppTools.getDownloadPath()));

        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        // 设置RecyclerView用于显示每条下载任务
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        initialViewPage(recyclerView);
        // 添加下载任务入口
        FloatingActionButton fab = findViewById(R.id.fab);
        initialButton(fab);
        // 工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 检查权限
        checkPermission();
    }

    /**
     * 若APP没有存储空间权限则主动申请
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    /**
     * 若用户不给予权限则退出
     *
     * @param requestCode  请求状态码
     * @param permissions  权限数据
     * @param grantResults 是否得到权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 若用户不给予权限则退出
        if (grantResults.length == 0
                || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish();
        }
    }

    private long exitTime = 0;

    /**
     * 双击退出程序
     */
    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(context, getResources().getString(R.string.exist_check), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else super.onBackPressed();
    }

    /**
     * 向recyclerView加载布局并注册adpater
     *
     * @param recyclerView 显示窗口
     */
    private void initialViewPage(RecyclerView recyclerView) {
        LinearLayoutManager manager = new LinearLayoutManager(context,
                RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        //
        DownloadAdapter downloadAdapter = new DownloadAdapter(this, recyclerView);
        recyclerView.setAdapter(downloadAdapter);
    }

    /**
     * 设置添加任务弹窗
     *
     * @param fab 悬浮按钮
     */
    private void initialButton(FloatingActionButton fab) {
        View view = getLayoutInflater().inflate(R.layout.item_url_input, null);
        EditText editText = view.findViewById(R.id.text_input_dialog);
        AlertDialog createTask = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle(getString(R.string.url_download_placeholder))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> EventBus.getDefault().post(new Result(Constant.MSG_CREATE, editText.getText().toString())))
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .setNeutralButton(getString(R.string.title_choose_file), (dialog, which) -> chooseTorrent()).create();
        fab.setOnClickListener(v -> createTask.show());
    }

    /**
     * 设置添加种子文件弹窗，目前未实现种子下载
     */
    private void chooseTorrent() {
        new ChooserDialog(MainActivity.this)
                .withFilter(false, false, Constant.SUFFIX)
                .withStartFile(Constant.DOWNLOAD_PATH)
                .withResources(R.string.title_choose_file, R.string.confirm, R.string.cancel)
                .withChosenListener((path, file) ->
                        Toast.makeText(MainActivity.this, "FILE: " + path, Toast.LENGTH_SHORT).show())
                .build()
                .show();
    }

    /**
     * 加载菜单
     *
     * @param menu 菜单
     * @return true为成功
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单设置行为
     *
     * @param item 菜单选项
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.exist) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 退出时使所有正在下载的任务暂停
     */
    @Override
    protected void onDestroy() {
        downloadManager.pauseAll();
        super.onDestroy();
    }
}