package com.example.hxx.refreshtest;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import org.w3c.dom.NameList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MyAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mlist;
    public static final int VIEW_FOOT=1;
    public static final int VIEW_NORMAL=2;
    private Context context;
    private boolean loadmore=true;
    private boolean fadetips=false;

    public MyAdapter(List<String> mlist, Context context, boolean loadmore) {
        this.mlist = mlist;
        this.context = context;
        this.loadmore = loadmore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i==VIEW_FOOT){
            return new FooterHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.footer,viewGroup,false));
        }
        else {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolder){

            ((ViewHolder) viewHolder).textView.setText(mlist.get(i));

        }else if (viewHolder instanceof FooterHolder){
            ((FooterHolder) viewHolder).tips.setVisibility(View.VISIBLE);
            if (loadmore==true){
                fadetips=false;
                if (mlist.size()>0){
                    ((FooterHolder) viewHolder).tips.setText("正在加载");
                }

            }else {
                if (mlist.size()>0){
                    ((FooterHolder) viewHolder).tips.setText("没有更多数据了");
                    (new android.os.Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((FooterHolder) viewHolder).tips.setVisibility(View.GONE);
                            loadmore=true;
                            fadetips=true;
                        }
                    },3000);
                }
            }
        }
    }



    public boolean isFadetips(){
        return fadetips;
    }

    @Override
    public int getItemCount() {
        return mlist.size()+1;
    }

    public int getRealLastPosition(){
        return mlist.size();
    }

    //要加载的下一页的item的设置
    public void upDates(List<String> data,boolean loadmore){
        if (data!=null){
            mlist.addAll(data);
        }
        this.loadmore=loadmore;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position==getItemCount()-1){
            return VIEW_FOOT;
        }else {
            return VIEW_NORMAL;
        }
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=(TextView) itemView.findViewById(R.id.text);
        }
    }

    static class FooterHolder extends RecyclerView.ViewHolder{
        TextView tips;
        public FooterHolder(@NonNull View itemView) {
            super(itemView);
            tips=(TextView) itemView.findViewById(R.id.tips);
        }
    }

    public void reseData(){
        mlist=new ArrayList<>();
    }
}
