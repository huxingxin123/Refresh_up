讲一下swiperefreshlayout的上拉加载

 

Swipe自带下拉刷新，我们只需要把swipe初始化，在新线程中把要更新的数据传上去了好了

 

下拉加载，要理解下拉加载的原理

 

正常情况下，我们都是要加载一页，然后拉到最下面，最下面的item显示：正在刷新，然后在sleep几秒之后，再加载出下一页，一直到最后一页，判断了没有item可以再组成一页了，就显示加载完毕，理解了这一点，自定义下拉加载就简单了。

 

布局：swipe里镶套一个recycle

两个子布局，一个正常布局，一个脚布局

 

适配器

首先，两个状态，一个是正常状态VIEW_NORMAL，一个是滑倒最后一个item的状态VIEW_FOOT

然后我们要判断（boolean）是否要继续向下加载，加载到最后“正在加载”那个要消失

所以我们两个持有器（viewholder），各自的子布局在其中初始化

,adapter里有一个方法，叫viewtype，他通过判断当前item处于什么位置而返回一个参数，通过这个参数我们找到对应的持有器

值得注意的是，因为我们加了个脚item，所以此时的item比list中的要多一个，

通过持有期器在oncreateview找到自己相应的布局，

然后在onbindview进行数据操作

普通状态，正常操作

脚持有器，判断是否还能继续加载，如果能，就让foot item显示正在加载

如果不能，就显示加载到最后了

然后开个新线程，sleep几秒，把是否能继续加载重新设置为true，目的是下次进来又能继续上拉加载

public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
     if (viewHolder instanceof ViewHolder){
         ((ViewHolder) viewHolder).textView.setText(mlist.get(i));
     }else if (viewHolder instanceof FooterHolder){
         ((FooterHolder) viewHolder).tips.setVisibility(View.*VISIBLE*);
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
                         ((FooterHolder) viewHolder).tips.setVisibility(View.*GONE*);
                         loadmore=true;
                         fadetips=true;
                     }
                 },3000);
             }
         }
     }
 }

 

最后我们在adapter中定义一个方法，你肯定会疑惑，怎么分页，这个方法的作用就是把list的几个item取出来，组成一个页

 

Adapter准备好了，接下来就是在activity中操作

 

初始化这些就不说了，最重要的是下拉刷新那段代码 initrecycle（）

 

```
private void initrecycleview() {

     adapter=new MyAdapter(getDatas(0,PAGE_COUNT),this,getDatas(0,PAGE_COUNT).size()>0 ?true :false);

    //传三个参数 第一个getdatas里的划定的加载的item数，第二个context，第三个判断剩下的item数是否还能继续加载，是的话loadmore就是true

    LinearLayoutManager manager=new LinearLayoutManager(this);

    recyclerView.setAdapter(adapter);

    recyclerView.setLayoutManager(manager);

    recyclerView.setItemAnimator(new DefaultItemAnimator());

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

        mlist.add("2333"+i);



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

}
```

adpter要传入三个重要参数，第一个，你要加载的第一页的item，第二个，context，第三个，是否能加载更多

```
public List<String> getDatas(final int firstIndex,final int lastIndex) {

    List<String> reslist=new ArrayList<>();

    for (int i=firstIndex;i<lastIndex;i++){

        if (i<mlist.size()){

            reslist.add(mlist.get(i));

        }

    }

    return reslist;//把mlist的数据拷贝到第一页的list

}
```

这里这个getDatas（）方法是将list前几个拷贝进一个新的集合

 

然后给recyclerview设置适配器，布局管理器等，接下来进行重要的

重写onScrollChanged()

在这个方法里，要判断继续加载这个foot item是否消失了，如果没消失，说明还能继续加载下一页，如果消失了，说明不能加载下一页，就是最后一页了

 

其他的方法都比较好理解了

 

![img](file:///C:/Users/HXX/AppData/Local/Temp/msohtmlclip1/01/clip_image002.jpg)

这个图片供大家理解

###  

### 总结

自定义下拉刷新，最重要的就是分页加载以及判断是否能继续加载（loadmore），因此每一次加载，调用onbindview 在mainactivity中调用，都是要判断loadmore是否为true，tips是否是false

思路：recycler里有一个方法叫viewtype，通过viewtype返回的参数确定viewholder，从而通过viewholder对item在onbindview里进行操作，通过判断loadmore和fadetips来判断是下拉加载还是到底了，然后加载的话开个新线程sleep几秒钟，再添上要加载的下一页

再activity中实现要给adapter划定一次要加载的item数量，通过listener来判断loadmore和是否到底了，然后继续划定要加载的内容，这个内容拷贝自传入要加载iten的list，这样就差不多实现这个功能了。
