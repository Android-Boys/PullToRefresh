package com.scwang.smartrefresh.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.header.MaterialHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smartrefresh.layout.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉刷新 上啦加载，网络连接异常，数据为null，连接超时
 */
public class PullToRefreshView extends FrameLayout implements com.scwang.smartrefresh.layout.api.RefreshState {

    private View mPullToRefreshView;
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mSmartRefreshLayout;
    private PullToRefreshAdapter mAdapter;

    private PullToRefreshLoadMoreListener mPullToRefreshLoadMoreListener;
    private OnRefreshLoadMoreListener mOnRefreshLoadMoreListener;

    private static int PAGENUMBER = 10;//每页的数目

    private StateView mStateView;


    public PullToRefreshView(@NonNull Context context) {
        super(context);
    }

    public PullToRefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void init() {

        mPullToRefreshView = View.inflate(getContext(), R.layout.activity_pullto_refresh, this);
        mRecyclerView = (RecyclerView) mPullToRefreshView.findViewById(R.id.mrecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSmartRefreshLayout = (SmartRefreshLayout) mPullToRefreshView.findViewById(R.id.mSmartRefreshLayout);
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setEnableHeaderTranslationContent(false);
                layout.setPrimaryColorsId(R.color.colorPrimary, R.color.colorPrimary);//全局设置主题颜色
                return new MaterialHeader(context);
            }
        });
        mOnRefreshLoadMoreListener = new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (checkNetWork()) {
                    if (mPullToRefreshLoadMoreListener != null) {
                        mSmartRefreshLayout.setEnableLoadMore(false);
                        mPullToRefreshLoadMoreListener.onRefresh(refreshLayout);
                    } else {
                        mSmartRefreshLayout.finishRefresh(1000);
                    }
                }else {
                    Toast.makeText(getContext(), "网络异常,请检查网络", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (checkNetWork()) {
                    if (mPullToRefreshLoadMoreListener != null) {
                        mSmartRefreshLayout.setEnableRefresh(false);
                        mPullToRefreshLoadMoreListener.onLoadMore(refreshLayout);
                    } else {
                        mSmartRefreshLayout.finishLoadMore(1000);
                    }
                }
            }
        };
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(mOnRefreshLoadMoreListener);
        mStateView = new StateView(getContext());
        mStateView.setmStateCallBack(new StateView.StateCallBack() {
            @Override
            public void onClick(View v) {
                mSmartRefreshLayout.setVisibility(VISIBLE);
                mStateView.setVisibility(INVISIBLE);
                autoRefresh();
            }
        });
        mStateView.setVisibility(INVISIBLE);
        addView(mStateView);

    }


    /**
     * 数据从下拉刷新来
     *
     * @param data
     * @param <T>
     */
    public <T> void addDataFromRefresh(List<T> data) {
        mAdapter.getData().clear();
        mAdapter.setNewData(data);//添加新数据
        mSmartRefreshLayout.finishRefresh();
        mSmartRefreshLayout.setEnableLoadMore(true);
        checkShowState(data);
    }


    public <T> void addDataFromLoadMore(List<T> data) {
        mAdapter.addData(data);
        mSmartRefreshLayout.finishLoadMore();
        mSmartRefreshLayout.setEnableRefresh(true);

    }

    /**
     * 根据内容的条数判断是否为显示EmptyView 还是没有更多
     */
    public void checkShowState(List data) {
        if (data.size() == 0) {
            showDifferentState(StateView.State.EMPTY);
        } else if (data.size() < PAGENUMBER) {
            showDifferentState(StateView.State.NOPAGE);
        }else if (data.size()==PAGENUMBER){
            showDifferentState(StateView.State.NORMAL);
        }
    }
    /**
     * 判断是否有网络
     */
    public boolean checkNetWork(){
        boolean mb=NetworkUtil.isNetworkConnected(getContext());
        if (!mb){
            showDifferentState(StateView.State.NETWORk);
        }
        return mb;
    }


    /**
     * 调用PullToRefreshView.setAdapter   得到要用的mAdapter 对象
     *
     * @param mList           数据类型
     * @param layoutResId
     * @param adapterCallBack
     * @param <T>
     */
    public <T> void setAdapter(List<T> mList, int layoutResId, final ConvertAdapterCallBack<T> adapterCallBack) {
        mAdapter = new PullToRefreshAdapter<T>(layoutResId, mList) {
            @Override
            protected void convert(BaseViewHolder helper, T item) {
                adapterCallBack.convert(helper, item);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * adapter 重写的抽象方法
     * @param <T>
     */
    public interface ConvertAdapterCallBack<T> {
        public void convert(BaseViewHolder helper, T item);
    }


    /**
     * 上拉，下拉在activity中的回调
     */
    public interface PullToRefreshLoadMoreListener {
        public void onRefresh(RefreshLayout refreshLayout);

        public void onLoadMore(RefreshLayout refreshLayout);
    }

    public static void setPAGENUMBER(int PAGENUMBER) {
        PullToRefreshView.PAGENUMBER = PAGENUMBER;
    }

    public void setmPullToRefreshLoadMoreListener(PullToRefreshLoadMoreListener mPullToRefreshLoadMoreListener) {
        this.mPullToRefreshLoadMoreListener = mPullToRefreshLoadMoreListener;
    }

    public void autoRefresh() {
        mSmartRefreshLayout.autoRefresh();
    }


    @Override
    public void showDifferentState(StateView.State state) {
        if (StateView.State.NORMAL==state){
            mSmartRefreshLayout.setVisibility(VISIBLE);
            mStateView.setVisibility(INVISIBLE);
        }else {
            mStateView.showDifferentState(state);
            mSmartRefreshLayout.finishLoadMore();
            mSmartRefreshLayout.finishRefresh();
            mAdapter.getData().clear();
            mAdapter.notifyDataSetChanged();
            mSmartRefreshLayout.setVisibility(GONE);
            mStateView.setVisibility(View.VISIBLE);
        }
    }
}
