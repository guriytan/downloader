package org.guriytan.downloader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;

import org.guriytan.downloader.R;
import org.guriytan.downloader.entity.DownloadTask;
import org.guriytan.downloader.Constant;
import org.guriytan.downloader.fragment.DownloadFragment;
import org.guriytan.downloader.presenter.DownloadPresenter;
import org.guriytan.downloader.util.AppTools;

import java.math.BigDecimal;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class DownloadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Observer<Integer> {
    private Disposable d; // 可以用于取消注册的监听者
    private List<DownloadTask> list;
    private Context context;
    private DownloadFragment downloadFragment;
    private DownloadPresenter downloadPresenter;

    public DownloadAdapter(Context context, DownloadFragment downloadFragment) {
        this.context = context;
        downloadPresenter = DownloadPresenter.getInstance();
        this.list = downloadPresenter.getDownloadTaskList();
        this.downloadFragment = downloadFragment;
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
        holder.setPosition(i);
        holder.bind(task);
        holder.onClick();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.d = d;
    }

    @Override
    public void onNext(Integer position) {
        notifyDataSetChanged();
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    private void startTask(DownloadTask task, int position) {
        downloadPresenter.startTask(task, position, this);
    }

    private void stopTask(DownloadTask task, int position) {
        downloadPresenter.stopTask(task, position);
    }

    private void deleteTask(final DownloadTask task, int position) {
        String[] items = {downloadFragment.getContext().getString(R.string.delete_data_with_file)};
        new LovelyChoiceDialog(downloadFragment.getContext())
                .setTopColorRes(R.color.colorAccent)
                .setTitle(R.string.determine_delete)
                .setIcon(R.drawable.ic_error)
                .setItemsMultiChoice(items, ((positions, items1) -> DownloadPresenter.getInstance().deleteTask(task, items1.size() > 0, position))).show();
    }

    public void removeItem(int position) {
        if (list.size() != 0) {
            list.remove(position);
            notifyDataSetChanged();
        }
    }

    public void addItem(DownloadTask task) {
        list.add(0, task);
        notifyDataSetChanged();
    }

    public void refresh() {
        list.clear();
        list.addAll(downloadPresenter.getDownloadTaskList());
        notifyDataSetChanged();
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        private int position;
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
                    AppTools.convertFileSize(task.getFileSize()), AppTools.convertFileSize(task.getDownloadSize())));
            downSpeed.setText(String.format(itemView.getResources().getString(R.string.down_speed),
                    AppTools.convertFileSize(task.getSpeed())));
            if (task.getFileSize() != 0 && task.getDownloadSize() != 0) {
                long speed = task.getSpeed() == 0 ? 1 : task.getSpeed();
                long time = (task.getFileSize() - task.getDownloadSize()) / speed;
                remainingTime.setText(String.format(itemView.getResources().getString(R.string.remaining_time), AppTools.formatFromSecond((int) time)));
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
                startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_fail));
                downStatus.setText(R.string.download_fail);
            } else if (task.getTaskStatus() == Constant.DOWNLOAD_WAIT) {
                startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_wait));
                downStatus.setText(R.string.wait_down);
            } else {
                startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_stop));
                downStatus.setText(R.string.downloading);
            }
        }

        void setPosition(int position) {
            this.position = position;
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
                        if (task.getTaskStatus() == Constant.DOWNLOAD_FAIL || task.getTaskStatus() == Constant.DOWNLOAD_STOP) {
                            startTask(task, position);
                        } else {
                            startTask.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_download));
                            downStatus.setText(R.string.is_stop);
                            stopTask(task, position);
                        }
                        break;
                    case R.id.delete_task:
                        deleteTask(task, position);
                        break;
                }
            }
        };
    }
}
