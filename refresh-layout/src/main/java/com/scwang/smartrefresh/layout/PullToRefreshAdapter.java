package com.scwang.smartrefresh.layout;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public abstract class PullToRefreshAdapter <T>extends BaseQuickAdapter<T,BaseViewHolder> {
    public PullToRefreshAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

}
