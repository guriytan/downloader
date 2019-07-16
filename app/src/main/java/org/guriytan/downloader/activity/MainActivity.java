package org.guriytan.downloader.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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

import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.guriytan.downloader.Constant;
import org.guriytan.downloader.R;
import org.guriytan.downloader.adapter.DownloadAdapter;
import org.guriytan.downloader.entity.DownloadTask;
import org.guriytan.downloader.entity.MessageEvent;
import org.guriytan.downloader.presenter.DownloadPresenter;
import org.guriytan.downloader.util.AppTools;
import org.guriytan.downloader.util.FileUtil;
import org.guriytan.downloader.util.StringUtil;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static OkHttpClient mClient; // OKHttpClient;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FileUtil.mkdirs(AppTools.getDownloadPath());

        mClient = new OkHttpClient.Builder().build();
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        initialViewPage(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        initialButton(fab);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0
                || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish();
        }
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(context, getResources().getString(R.string.exist_check), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else super.onBackPressed();
    }

    private void initialViewPage(RecyclerView recyclerView) {
        LinearLayoutManager manager = new LinearLayoutManager(context,
                RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        // recyclerView注册adpater
        DownloadAdapter downloadAdapter = new DownloadAdapter(this, recyclerView);
        recyclerView.setAdapter(downloadAdapter);
    }

    private void initialButton(FloatingActionButton fab) {
        View view = getLayoutInflater().inflate(R.layout.item_url_input, null);
        EditText editText = view.findViewById(R.id.text_input_dialog);
        AlertDialog createTask = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle(getString(R.string.url_download_placeholder))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                    String url = editText.getText().toString();
                    if (StringUtil.isHttpUrl(url)) {
                        CreateDownloadTask task = new CreateDownloadTask(editText.getText().toString(), handler);
                        new Thread(task).start();
                    } else
                        EventBus.getDefault().post(new MessageEvent(Constant.ERROR_ALERT, R.string.create_fail));
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .setNeutralButton(getString(R.string.title_choose_file), (dialog, which) -> chooseTorrent()).create();
        fab.setOnClickListener(v -> createTask.show());
    }

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

    private Handler handler = new Handler(msg -> {
        if (msg.what == Constant.CREATE_SUCCESS) {
            DownloadPresenter.getInstance().addTask((DownloadTask) msg.obj);
        } else {
            EventBus.getDefault().post(new MessageEvent(Constant.ERROR_ALERT, R.string.create_fail));
        }
        return false;
    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

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
     * 异步创建DownloadTask
     */
    private static class CreateDownloadTask implements Runnable {
        private String url;
        private Handler handler;

        CreateDownloadTask(String url, Handler handler) {
            this.url = url;
            this.handler = handler;
        }

        @Override
        public void run() {
            DownloadTask task = new DownloadTask();
            task.setUrl(url);
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();
            mClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    EventBus.getDefault().post(new MessageEvent(Constant.ERROR_ALERT, R.string.error_url));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if (response.isSuccessful()) {
                        long contentLength = response.body().contentLength();
                        task.setFileSize(contentLength == 0 ? Constant.CREATE_FAIL : contentLength);
                        if (contentLength == Constant.CREATE_FAIL) {
                            task.setTaskStatus(Constant.DOWNLOAD_FAIL);
                        } else {
                            task.setTaskStatus(Constant.DOWNLOAD_STOP);
                        }
                        String realUrl = response.request().url().toString();
                        String fileName = realUrl.substring(realUrl.lastIndexOf("/") + 1);
                        task.setFileName(fileName);
                    } else task.setTaskStatus(Constant.DOWNLOAD_FAIL);
                    Message msg = handler.obtainMessage();
                    if (task.getTaskStatus() != Constant.DOWNLOAD_FAIL) {
                        task.setDate(new Date());
                        task.setFilePath(Constant.DOWNLOAD_PATH);
                        task.setTaskId(StringUtil.generateTaskId(task));
                        msg.what = Constant.CREATE_SUCCESS;
                        msg.obj = task;
                    } else {
                        msg.what = Constant.CREATE_FAIL;
                    }
                    handler.sendMessage(msg);
                }
            });
        }
    }
}