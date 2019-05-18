package com.example.hxx.refreshtest;

import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    SwipeRefreshLayout swipeRefreshLayout;
    TextView textView;
    List<String> mlist=new ArrayList<>();
    RecyclerView recyclerView;
    private int LastVisibleItem=0;
    private final int PAGE_COUNT=10;
    MyAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initview();//顶部，下拉刷新
        initlayout();
        initswiperefresh();
        initrecycleview();//底部，上拉刷新
        additem();

    }






    private void initrecycleview() {
         adapter=new MyAdapter(getDatas(0,PAGE_COUNT),this,getDatas(0,PAGE_COUNT).size()>0 ?true :false);
        //传三个参数 第一个getdatas里的划定的加载的item数，第二个context，第三个判断剩下的item数是否还能继续加载，是的话loadmore就是true
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },3000);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView,newState);

                //加载新的一页
                if (newState==recyclerView.SCROLL_STATE_IDLE){
                    if (adapter.isFadetips()==false&&LastVisibleItem+1==adapter.getItemCount()){
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                upDataRecycleView(adapter.getRealLastPosition(),adapter.getRealLastPosition()+PAGE_COUNT);

                            }
                        },3000);
                    }

                    //tips被隐藏了，说明是最后一页了，该一直加载到底部
                    if (adapter.isFadetips()==true&&LastVisibleItem+2==adapter.getItemCount()){
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                upDataRecycleView(adapter.getRealLastPosition(),adapter.getRealLastPosition()+PAGE_COUNT);
                            }
                        },3000);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LastVisibleItem=manager.findLastVisibleItemPosition();
            }
        });
    }
    //划定最开始呈现的item数
    public List<String> getDatas(final int firstIndex,final int lastIndex) {
        List<String> reslist=new ArrayList<>();
        for (int i=firstIndex;i<lastIndex;i++){
            if (i<mlist.size()){
                reslist.add(mlist.get(i));
            }
        }
        return reslist;//把mlist的数据拷贝到第一页的list
    }

    public void upDataRecycleView(int fromIndex,int toIndex){
        List newsData=getDatas(fromIndex, toIndex);
        if (newsData.size()>0){
            adapter.upDates(newsData,true);
        }else {
            adapter.upDates(null,false);
        }
    }

    public void initswiperefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::initswiperefresh);

    }


    private void initlayout() {
        recyclerView=(RecyclerView)findViewById(R.id.recycleview);
        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipe);
    }

    private void additem() {
        for (int i=0;i<20;i++){
            mlist.add("LOL"+i);

        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        adapter.reseData();
        upDataRecycleView(0,PAGE_COUNT);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                
                swipeRefreshLayout.setRefreshing(false);
            }
        },3000);
    }}


