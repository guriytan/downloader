package org.guriytan.downloader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yarolegovich.lovelydialog.LovelyChoiceDialog;

import org.guriytan.downloader.R;
import org.guriytan.downloader.entity.DownloadTask;
import org.guriytan.downloader.fragment.FinishFragment;
import org.guriytan.downloader.presenter.DownloadPresenter;
import org.guriytan.downloader.util.AppTools;

import java.io.File;
import java.util.List;

public class FinishAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DownloadTask> list;
    private Context context;
    private FinishFragment finishFragment;
    private DownloadPresenter downloadPresenter;

    public FinishAdapter(Context context, FinishFragment finishFragment) {
        this.context = context;
        this.finishFragment = finishFragment;
        downloadPresenter = DownloadPresenter.getInstance();
        this.list = downloadPresenter.getFinishTaskList();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_download_success, parent, false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final DownloadTask task = list.get(position);
        TaskHolder taskHolder = (TaskHolder) holder;
        taskHolder.setPosition(position);
        taskHolder.bind(task);
        taskHolder.onClick();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void deleteTask(final DownloadTask task, int position) {
        String[] items = {finishFragment.getContext().getString(R.string.delete_data_with_file)};
        new LovelyChoiceDialog(finishFragment.getContext())
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

    public void refresh() {
        list.clear();
        list.addAll(downloadPresenter.getFinishTaskList());
        notifyDataSetChanged();
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        private int position;
        private DownloadTask task;
        private TextView fileNameText, downSize;
        private ImageView fileIcon, deleteTask;
        private TextView btnOpen, fileIsDele;

        TaskHolder(View itemView) {
            super(itemView);
            fileNameText = itemView.findViewById(R.id.file_name);
            downSize = itemView.findViewById(R.id.down_size);
            fileIcon = itemView.findViewById(R.id.file_icon);
            deleteTask = itemView.findViewById(R.id.delete_task);
            btnOpen = itemView.findViewById(R.id.btn_open);
            fileIsDele = itemView.findViewById(R.id.file_is_dele);
        }

        void bind(DownloadTask task) {
            this.task = task;
            String filePath = task.getFilePath() + File.separator + task.getFileName();
            fileNameText.setText(task.getFileName());

//            if (task.getThumbnailPath() != null && FileTools.isVideoFile(filePath)) {
//                x.image().bind(fileIcon, task.getThumbnailPath());
//            } else {
//                String filename = task.getFile() ? task.getmFileName() : "";
//                fileIcon.setImageDrawable(itemView.getResources().getDrawable(FileTools.getFileIcon(filename)));
//            }
            downSize.setText(AppTools.convertFileSize(task.getDownloadSize()));
            if (AppTools.exists(filePath)) {
                fileIsDele.setVisibility(View.GONE);
                btnOpen.setVisibility(View.VISIBLE);
                fileNameText.setTextColor(itemView.getResources().getColor(R.color.dimgray));
                downSize.setTextColor(itemView.getResources().getColor(R.color.gray_8f));
//                String suffix = Util.getFileSuffix(task.getFileName());
//                if (FileTools.isVideoFile(task.getmFileName())) {
//                    btnOpen.setText(itemView.getResources().getString(R.string.play));
//                } else if ("TORRENT".equals(suffix) || "APK".equals(suffix) || (!task.getFile() && task.getTaskType() == Const.BT_DOWNLOAD)) {
//                    btnOpen.setText(itemView.getResources().getString(R.string.open));
//                } else {
                btnOpen.setVisibility(View.INVISIBLE);
//                }
//            } else if (task.getFile() && !FileTools.exists(filePath)) {
//                fileIsDele.setVisibility(View.VISIBLE);
//                fileNameText.setTextColor(itemView.getResources().getColor(R.color.gray_cc));
//                downSize.setTextColor(itemView.getResources().getColor(R.color.gray_cc));
//                btnOpen.setText("重新下载");
//                btnOpen.setVisibility(View.VISIBLE);
//            } else if (!task.getFile()) {
//                btnOpen.setVisibility(View.VISIBLE);
//            }
            }
        }

        void setPosition(int position) {
            this.position = position;
        }

        void onClick() {
            btnOpen.setOnClickListener(listener);
            deleteTask.setOnClickListener(listener);
        }

        private View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_open:

                        break;
                    case R.id.delete_task:
                        deleteTask(task, position);
                        break;
                }
            }
        };
    }
}
