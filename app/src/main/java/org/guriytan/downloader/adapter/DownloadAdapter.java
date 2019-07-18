package org.guriytan.downloader.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.irozon.sneaker.Sneaker;
import com.moos.library.HorizontalProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.guriytan.downloader.R;
import org.guriytan.downloader.Constant;
import org.guriytan.downloader.callback.DownloadCallback;
import org.guriytan.downloader.callback.ResultCallback;
import org.guriytan.downloader.entity.Result;
import org.guriytan.downloader.entity.TaskInfo;
import org.guriytan.downloader.manager.DownloadManager;
import org.guriytan.downloader.util.FileUtil;
import org.guriytan.downloader.util.StringUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * 显示所有下载任务的适配器
 */
public class DownloadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ResultCallback {
    private List<TaskInfo> list; // 所有下载任务列表
    private Context context; // 上下文
    private Activity activity; //父活动
    private static DownloadManager downloadManager; // 下载管理器

    public DownloadAdapter(Activity activity, RecyclerView recyclerView) {
        this.context = recyclerView.getContext();
        downloadManager = new DownloadManager.Builder().initialCallback(this).build();
        this.list = downloadManager.getAllTasks();
        this.activity = activity;
        EventBus.getDefault().register(this);
    }

    /**
     * 对每一个下载任务分配TaskHolder
     *
     * @param viewGroup 父显示窗口
     * @param i         下载任务序号
     * @return TaskHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_task, viewGroup, false);
        return new TaskHolder(view);
    }

    /**
     * 设置长按监听器用于删除操作，并对TaskHolder对象显示具体任务信息
     *
     * @param viewHolder TaskHolder对象
     * @param i          任务序号
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final TaskInfo info = list.get(i); // 任务信息
        TaskHolder holder = (TaskHolder) viewHolder;
        View view = activity.getLayoutInflater().inflate(R.layout.item_comfirm_delete, null);
        CheckBox checkBox = view.findViewById(R.id.confirm_delete); // 是否删除文件，若文件未完成则默认删除所有文件
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton(R.string.confirm, (dialog, which) -> downloadManager.delete(info, checkBox.isChecked()))
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create();
        // 设置长按监听器
        holder.itemView.setOnLongClickListener(v -> {
            alertDialog.show();
            return true;
        });
        // 显示信息
        holder.bind(info);
        // 设置下载按钮监听器
        holder.onClick();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 刷新全局
     */
    private void refresh() {
        list.clear();
        list.addAll(downloadManager.getAllTasks());
        notifyDataSetChanged();
    }

    /**
     * 用于处理MainActivity传来的url字符串消息以创建任务
     *
     * @param result 消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Result result) {
        if (result.getType() == Constant.MSG_CREATE) {
            downloadManager.add((String) result.getMessage());
        }
    }

    /**
     * 新建任务或删除任务回调成功的接口
     *
     * @param result type为1，表示操作成功
     */
    @Override
    public void onSuccess(Result result) {
        Sneaker.with(activity)
                .setTitle(activity.getResources().getString(R.string.title_dialog), R.color.white)
                .setMessage((String) result.getMessage(), R.color.white)
                .setDuration(2000)
                .autoHide(true)
                .setIcon(R.drawable.ic_done, R.color.white, false)
                .sneak(R.color.success);
        refresh();
    }

    /**
     * 新建任务或删除任务回调成功的接口
     *
     * @param result type为2，表示操作失败
     */
    @Override
    public void onError(Result result) {
        Sneaker.with(activity)
                .setTitle(activity.getResources().getString(R.string.title_dialog), R.color.white)
                .setMessage((String) result.getMessage(), R.color.white)
                .setDuration(2000)
                .autoHide(true)
                .setIcon(R.drawable.ic_error, R.color.white, false)
                .sneak(R.color.colorAccent);
        refresh();
    }

    /**
     * 具体每一条下载任务信息的显示组件，实现了下载回调用于显示进度信息
     */
    class TaskHolder extends RecyclerView.ViewHolder implements DownloadCallback {
        private TaskInfo info;
        private TextView fileNameText, downSize, downSpeed, remainingTime, downStatus;
        private ImageView startTask, fileIcon;
        private HorizontalProgressView progressBar;

        TaskHolder(View itemView) {
            super(itemView);
            fileNameText = itemView.findViewById(R.id.file_name);
            downSize = itemView.findViewById(R.id.down_size);
            downSpeed = itemView.findViewById(R.id.down_speed);
            remainingTime = itemView.findViewById(R.id.remaining_time);
            startTask = itemView.findViewById(R.id.start_task);
            fileIcon = itemView.findViewById(R.id.file_icon);
            progressBar = itemView.findViewById(R.id.number_progress_bar);
            downStatus = itemView.findViewById(R.id.down_status);
        }

        void bind(TaskInfo info) {
            this.info = info;
            fileNameText.setText(info.getFileName()); // 设置文件名
            remainingTime.setText(""); // 重置剩余时间
            downSpeed.setText(""); // 重置下载速度
            // 显示进度条的进度
            if (info.getDownloadSize() != 0 && info.getFileSize() != 0) {
                double f1 = new BigDecimal((float) info.getDownloadSize() / info.getFileSize()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                progressBar.setProgress((int) (f1 * 100));
            } else {
                progressBar.setProgress(0);
            }
            // 显示已下载大小信息
            downSize.setText(String.format(itemView.getResources().getString(R.string.down_count),
                    StringUtil.convertFileSize(info.getDownloadSize()), StringUtil.convertFileSize(info.getFileSize()), (int) progressBar.getProgress()));
            // 显示当前下载状态
            if ((info.getTaskStatus() == Constant.MSG_PAUSE)) {
                startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_download));
                downStatus.setText(R.string.is_stop);
            } else if (info.getTaskStatus() == Constant.MSG_FINISH) {
                startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_done));
                downStatus.setText(R.string.finish);
            } else if (info.getTaskStatus() == Constant.MSG_FAIL) {
                startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_error));
                downStatus.setText(R.string.download_fail);
            }
            // 设置文件类型对应图标
            fileIcon.setImageDrawable(itemView.getResources().getDrawable(FileUtil.getType(info.getFileName())));
        }

        /**
         * 对下载状态绑定下载监听器，对文件类型图标绑定打开监听器
         */
        void onClick() {
            startTask.setOnClickListener(listener);
            fileIcon.setOnClickListener(listener);
        }

        private View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.start_task:
                        // 启动或暂停任务
                        if (info.getTaskStatus() == Constant.MSG_FAIL || info.getTaskStatus() == Constant.MSG_PAUSE) {
                            startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_pause));
                            downStatus.setText(R.string.downloading);
                            downloadManager.download(info, TaskHolder.this);
                        } else if (info.getTaskStatus() == Constant.MSG_WAIT || info.getTaskStatus() == Constant.MSG_PROGRESS) {
                            downloadManager.pause(info);
                        }
                        break;
                    case R.id.file_icon:
                        // 打开文件
                        if (info.getTaskStatus() == Constant.MSG_FINISH)
                            Toast.makeText(itemView.getContext(), "尚未支持", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        /**
         * 任务已完成则刷新并显示已完成且允许打开文件
         */
        @Override
        public void onFinished() {
            notifyDataSetChanged();
        }
        /**
         * 任务进度回调，刷新下载速度、剩余时间、下载进度条
         */
        @Override
        public void onProgress() {
            downSpeed.setText(String.format(itemView.getResources().getString(R.string.down_speed),
                    StringUtil.convertFileSize(info.getSpeed())));
            long speed = info.getSpeed() == 0 ? 1 : info.getSpeed();
            long time = (info.getFileSize() - info.getDownloadSize()) / speed;
            remainingTime.setText(String.format(itemView.getResources().getString(R.string.remaining_time), StringUtil.formatFromSecond((int) time)));
            double f1 = new BigDecimal((float) info.getDownloadSize() / info.getFileSize()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            progressBar.setProgress((int) (f1 * 100));
        }
        /**
         * 任务暂停则刷新并显示暂停
         */
        @Override
        public void onPause() {
            notifyDataSetChanged();
        }
        /**
         * 任务重置则刷新并显示重置后的已暂停状态
         */
        @Override
        public void onReset() {
            notifyDataSetChanged();
        }
        /**
         * 任务等待显示等待下载状态
         */
        @Override
        public void onWait() {
            startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_wait));
            downStatus.setText(R.string.wait_down);
        }
        /**
         * 任务错误则刷新显示错误状态
         */
        @Override
        public void onFail() {
            notifyDataSetChanged();
        }
        /**
         * 任务已删除则刷新以删除任务显示
         */
        @Override
        public void onDelete() {
            notifyDataSetChanged();
        }
    }
}
