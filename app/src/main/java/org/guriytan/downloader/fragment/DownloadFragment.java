package org.guriytan.downloader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.guriytan.downloader.Constant;
import org.guriytan.downloader.R;
import org.guriytan.downloader.adapter.DownloadAdapter;
import org.guriytan.downloader.entity.MessageEvent;
import org.guriytan.downloader.util.AppTools;

public class DownloadFragment extends Fragment {
    private DownloadAdapter downloadAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frm_download_ing, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initFragment(); // 注册adapter并注入list
    }

    private void initFragment() {
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        // recyclerView注册adpater
        downloadAdapter = new DownloadAdapter(getContext(), this);
        recyclerView.setAdapter(downloadAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MessageEvent message) {
        AppTools.alert(this.getActivity(), message.getMsg(), message.getMsgType());
        if (message.getMsgType() == Constant.SUCCESS_ALERT) {
            if (message.getAddOrRemove() == 0) {
                downloadAdapter.addItem(message.getTask());
            } else {
                downloadAdapter.removeItem(message.getPosition());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        downloadAdapter.refresh();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }
}
