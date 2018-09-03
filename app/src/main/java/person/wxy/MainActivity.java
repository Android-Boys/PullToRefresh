package person.wxy;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.PullToRefreshView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.header.MaterialHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PullToRefreshView mPullToRefreshView;
    private List<Item>items=new ArrayList<>();
    private Handler mHandler;

    private int i=0;
    private int n=1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPullToRefreshView=findViewById(R.id.mPullToRefreshView);



        mPullToRefreshView.setAdapter(items, R.layout.item, new PullToRefreshView.ConvertAdapterCallBack<Item>() {
            @Override
            public void convert(BaseViewHolder helper, Item item) {
                helper.setText(R.id.mTextView,item.getName());
            }
        });


        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what==1){
                    i=10;
                    n=1;
                    List<Item>items=new ArrayList<>();
                    for (;n<=i;n++){
                        Item item=new Item();
                        item.setName(""+n);
                        items.add(item);
                    }
                    mPullToRefreshView.addDataFromRefresh(items);
                }else {
                    i=i+10;
                    n=i-10;
                    List<Item>items=new ArrayList<>();
                    for (;n<=i;n++){
                        Item item=new Item();
                        item.setName(""+n);
                        items.add(item);
                    }
                    mPullToRefreshView.addDataFromLoadMore(items);
                }
            }
        };


        mPullToRefreshView.setmPullToRefreshLoadMoreListener(new PullToRefreshView.PullToRefreshLoadMoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mHandler.sendEmptyMessageDelayed(1,3000);
            }
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mHandler.sendEmptyMessageDelayed(2,3000);
            }
        });


        mPullToRefreshView.autoRefresh();
    }
}
