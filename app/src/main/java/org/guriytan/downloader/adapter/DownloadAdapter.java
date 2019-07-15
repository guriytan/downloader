package org.guriytan.downloader.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.irozon.sneaker.Sneaker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.guriytan.downloader.R;
import org.guriytan.downloader.entity.DownloadTask;
import org.guriytan.downloader.Constant;
import org.guriytan.downloader.entity.MessageEvent;
import org.guriytan.downloader.presenter.DownloadPresenter;
import org.guriytan.downloader.util.FileUtil;
import org.guriytan.downloader.util.StringUtil;

import java.math.BigDecimal;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class DownloadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Observer<DownloadTask> {
    private Disposable disposable;
    private List<DownloadTask> list;
    private Context context;
    private RecyclerView recyclerView;
    private Activity activity;
    private DownloadPresenter downloadPresenter;

    public DownloadAdapter(Activity activity, RecyclerView recyclerView) {
        this.context = recyclerView.getContext();
        downloadPresenter = DownloadPresenter.getInstance();
        this.list = downloadPresenter.getAllTasks();
        this.recyclerView = recyclerView;
        this.activity = activity;
        EventBus.getDefault().register(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_downloading, viewGroup, false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final DownloadTask task = list.get(i);
        TaskHolder holder = (TaskHolder) viewHolder;
        holder.bind(task);
        holder.onClick();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(DownloadTask task) {
        notifyDataSetChanged();
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    private void addItem(DownloadTask task) {
        list.add(0, task);
        notifyDataSetChanged();
    }

    private void refresh() {
        list.clear();
        list.addAll(downloadPresenter.getAllTasks());
        notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MessageEvent message) {
        alert(activity, context.getString(message.getMsg()), message.getMsgType());
        if (message.getMsgType() == Constant.SUCCESS_ALERT) {
            if (message.getTask() != null) {
                addItem(message.getTask());
            } else {
                refresh();
            }
        }
    }

    // 提示弹窗
    private static void alert(Activity activity, String msg, int msgType) {
        if (Constant.ERROR_ALERT == msgType) {
            Sneaker.with(activity)
                    .setTitle(activity.getResources().getString(R.string.title_dialog), R.color.white)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_error, R.color.white, false)
                    .sneak(R.color.colorAccent);
        } else if (Constant.SUCCESS_ALERT == msgType) {
            Sneaker.with(activity)
                    .setTitle(activity.getResources().getString(R.string.title_dialog), R.color.white)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_done, R.color.white, false)
                    .sneak(R.color.success);
        } else if (Constant.WARNING_ALERT == msgType) {
            Sneaker.with(activity)
                    .setTitle(activity.getResources().getString(R.string.title_dialog), R.color.white)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_warning, R.color.white, false)
                    .sneak(R.color.warning);
        }
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        private DownloadTask task;
        private TextView fileNameText, downSize, downSpeed, remainingTime;
        private TextView downStatus;
        private ImageView startTask, deleteTask, fileIcon;
        private NumberProgressBar progressBar;

        TaskHolder(View itemView) {
            super(itemView);
            fileNameText = itemView.findViewById(R.id.file_name);
            downSize = itemView.findViewById(R.id.down_size);
            downSpeed = itemView.findViewById(R.id.down_speed);
            remainingTime = itemView.findViewById(R.id.remaining_time);
            startTask = itemView.findViewById(R.id.start_task);
            deleteTask = itemView.findViewById(R.id.delete_task);
            fileIcon = itemView.findViewById(R.id.file_icon);
            progressBar = itemView.findViewById(R.id.number_progress_bar);
            downStatus = itemView.findViewById(R.id.down_status);
        }

        void bind(DownloadTask task) {
            this.task = task;
            fileNameText.setText(task.getFileName());
            downSize.setText(String.format(itemView.getResources().getString(R.string.down_count),
                    StringUtil.convertFileSize(task.getFileSize()), StringUtil.convertFileSize(task.getDownloadSize())));
            downSpeed.setText(String.format(itemView.getResources().getString(R.string.down_speed),
                    StringUtil.convertFileSize(task.getSpeed())));
            if (task.getFileSize() != 0 && task.getDownloadSize() != 0) {
                long speed = task.getSpeed() == 0 ? 1 : task.getSpeed();
                long time = (task.getFileSize() - task.getDownloadSize()) / speed;
                remainingTime.setText(String.format(itemView.getResources().getString(R.string.remaining_time), StringUtil.formatFromSecond((int) time)));
            }
            if (task.getDownloadSize() != 0 && task.getFileSize() != 0) {
                double f1 = new BigDecimal((float) task.getDownloadSize() / task.getFileSize()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                progressBar.setProgress((int) (f1 * 100));
            } else {
                progressBar.setProgress(0);
            }
            if ((task.getTaskStatus() == Constant.DOWNLOAD_STOP)) {
                startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_download));
                downStatus.setText(R.string.is_stop);
            } else if (task.getTaskStatus() == Constant.DOWNLOAD_FAIL) {
                startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_error));
                downStatus.setText(R.string.download_fail);
            } else if (task.getTaskStatus() == Constant.DOWNLOAD_WAIT) {
                startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_wait));
                downStatus.setText(R.string.wait_down);
            } else if (task.getTaskStatus() == Constant.DOWNLOAD_FINISH) {
                startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_done));
                downStatus.setText(R.string.finish);
            } else {
                startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_pause));
                downStatus.setText(R.string.downloading);
            }
            fileIcon.setImageDrawable(itemView.getResources().getDrawable(FileUtil.getType(task.getFileName())));
        }

        void onClick() {
            startTask.setOnClickListener(listener);
            deleteTask.setOnClickListener(listener);
            fileIcon.setOnClickListener(listener);
        }

        private View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.start_task:
                        // 启动或暂停任务
                        if (task.getTaskStatus() == Constant.DOWNLOAD_FAIL || task.getTaskStatus() == Constant.DOWNLOAD_STOP) {
                            downloadPresenter.startTask(task, DownloadAdapter.this, activity.getApplicationContext());
                        } else if (task.getTaskStatus() == Constant.DOWNLOAD_WAIT || task.getTaskStatus() == Constant.DOWNLOAD_ING) {
                            startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_download));
                            downStatus.setText(R.string.is_stop);
                            downloadPresenter.stopTask(task);
                        }
                        break;
                    case R.id.delete_task:
                        // 删除任务
                        new MaterialDialog.Builder(recyclerView.getContext())
                                .title(R.string.determine_delete)
                                .titleColor(recyclerView.getContext().getResources().getColor(R.color.colorAccent))
                                .positiveText("确认")
                                .checkBoxPromptRes(R.string.delete_data_with_file, false, null)
                                .onAny((dialog, which) -> downloadPresenter.deleteTask(task, dialog.isPromptCheckBoxChecked()))
                                .show();
                        break;
                    case R.id.file_icon:
                        // 打开文件
                        break;
                }
            }
        };
    }
}
