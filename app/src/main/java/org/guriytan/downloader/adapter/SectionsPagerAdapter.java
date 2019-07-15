package org.guriytan.downloader.adapter;

import android.content.Context;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.guriytan.downloader.R;
import org.guriytan.downloader.fragment.DownloadFragment;
import org.guriytan.downloader.fragment.FinishFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.download, R.string.finish};
    private final Context mContext;
    private SparseArray<Fragment> map;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        map = new SparseArray<>();
        map.append(0, new DownloadFragment());
        map.append(1, new FinishFragment());
        mContext = context;
    }

    @Override
    @NonNull
    public Fragment getItem(int position) {
        return map.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 2;
    }
}