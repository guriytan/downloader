package org.guriytan.downloader.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.guriytan.downloader.Constant;
import org.guriytan.downloader.R;
import org.guriytan.downloader.entity.DownloadTask;
import org.guriytan.downloader.presenter.DownloadPresenter;
import org.guriytan.downloader.util.AppTools;

import java.io.IOException;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Response;

public class URLActivity extends AppCompatActivity {
    private static OkHttpClient mClient; // OKHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = new OkHttpClient.Builder().build();
        setContentView(R.layout.new_url);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final EditText editText = findViewById(R.id.url_input);
        Button button = findViewById(R.id.do_download);
        button.setOnClickListener(v -> {
            CreateDownloadTask create = new CreateDownloadTask(editText.getText().toString(), handler);
            create.execute();
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Handler handler = new Handler(msg -> {
        if (msg.what == Constant.CREATE_SUCCESS) {
            DownloadPresenter.getInstance().addTask((DownloadTask) msg.obj);
        } else {
            AppTools.alert(URLActivity.this, getApplication().getString(R.string.create_fail), 2);
        }
        finish();
        return false;
    });

    /**
     * 异步创建DownloadTask
     */
    private static class CreateDownloadTask extends AsyncTask<String, Void, DownloadTask> {
        String url;
        Handler handler;

        CreateDownloadTask(String url, Handler handler) {
            this.url = url;
            this.handler = handler;
        }

        @Override
        protected void onPostExecute(DownloadTask task) {
            Message msg = handler.obtainMessage();
            if (task.getTaskStatus() != Constant.DOWNLOAD_FAIL) {
                task.setDate(new Date());
                task.setFilePath(Constant.DOWNLOAD_PATH);
                task.setTaskId(AppTools.generateTaskId(task));
                msg.what = Constant.CREATE_SUCCESS;
                msg.obj = task;
            } else {
                msg.what = Constant.CREATE_FAIL;
            }
            handler.sendMessage(msg);
        }

        // 获取下载长度和文件名
        @Override
        protected DownloadTask doInBackground(String... params) {
            DownloadTask task = new DownloadTask();
            task.setUrl(url);
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = mClient.newCall(request).execute();
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
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return task;
        }
    }
}
