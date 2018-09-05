package com.scwang.smartrefresh.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StateView extends FrameLayout {

    private View rootView;
    private Context context;
    private ImageView ivState;
    private TextView tvTips;
    private StateCallBack mStateCallBack;
    /**
     * 数据为null
     * 加载失败,没有网络
     * 数据不足一页
     */
    public enum State {
        EMPTY, FAIL, NETWORk, NOPAGE,NORMAL
    }


    public StateView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView();
    }


    private void initView() {
        rootView = View.inflate(context, R.layout.layout_state_view, this);
        ivState = (ImageView) rootView.findViewById(R.id.ivState);
        tvTips = (TextView) rootView.findViewById(R.id.tvTips);
        ivState.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStateCallBack!=null){
                    mStateCallBack.onClick(v);
                }else {
                    Toast.makeText(context, "请初始化StateCallBack", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showDifferentState(State state){
        switch (state){
            case EMPTY:
                ivState.setImageResource(R.mipmap.ic_state_empty);
                tvTips.setText("没有数据,点击重试");
                break;
            case NETWORk:
                ivState.setImageResource(R.mipmap.ic_state_no_network);
                tvTips.setText("网络异常,点击重试");
                break;
            case FAIL:
                ivState.setImageResource(R.mipmap.ic_state_error);
                tvTips.setText("加载失败,点击重试");
                break;
        }
    }

    public void setmStateCallBack(StateCallBack mStateCallBack) {
        this.mStateCallBack = mStateCallBack;
    }

    public interface StateCallBack{
           public void onClick(View v);
    }
}
