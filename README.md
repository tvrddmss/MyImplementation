# MyImplementation
用继承自RecyclerView，手写的下拉刷新，上拉加载更多的控件，支持androidx


样例如下：
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

