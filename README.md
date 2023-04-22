# MyImplementation
用继承自RecyclerView，手写的下拉刷新，上拉加载更多的控件，支持androidx

引用

    implementation 'com.github.tvrddmss:MyImplementation:1.4'

layout.xml

    <tvrddmss.mylistview.MyRecyclerView
        android:id="@+id/myRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"></tvrddmss.mylistview.MyRecyclerView>


样例如下：

//        lv.setCanRefresh(false);
//        lv.setCanLoadMore(false);

        JSONArray jsonArray = new JSONArray();
        myAdapter = new RefreshRecycleAdapter(getContext(), jsonArray);
        lv.setAdapter(myAdapter);

        //间隔线
        lv.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        //设置监听
        lv.setListener(new MyRecyclerView.IOnScrollListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            myAdapter.list = new JSONArray();
                            for (int i = 0; i < 8; i++) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("test", i);
                                myAdapter.list.put(jsonObject);
                            }
                            MyLog.d("notifyChanged");
                            myAdapter.notifyDataSetChanged();


                        } catch (Exception ex) {
                        }
                    }
                }, 3000);

            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("test", myAdapter.getItemCount());
                            myAdapter.list.put(jsonObject);
                            myAdapter.notifyDataSetChanged();
                        } catch (Exception ex) {
                        }
                    }
                }, 3000);
            }

            @Override
            public boolean isAllData() {
                if (myAdapter.getItemCount() >= 20) {
                    return true;
                } else {
                    return false;
                }
            }
        });

