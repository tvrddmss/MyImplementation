package tvrddmss.mylistview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.JustifyContent;

import tvrddmss.mylistview.flowlayout.MyFlexboxLayoutManager;


/**
 * @ProjectName: mydemo
 * @Package: esa.mylibrary.uicomponent
 * @ClassName: MyRecyclerView
 * @Description: java类作用描述
 * @Author: Administrator
 * @CreateDate: 2023/04/10 14:38
 * @UpdateUser: Administrator
 * @UpdateDate: 2023/04/10 14:38
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MyRecyclerView extends RecyclerView implements RecyclerView.OnTouchListener {

    //region 初始化
    public MyRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //endregion

    //region 属性
    public Boolean getCanLoadMore() {
        return canLoadMore;
    }

    public void setCanLoadMore(Boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public Boolean getCanRefresh() {
        return canRefresh;
    }

    public void setCanRefresh(Boolean canRefresh) {
        this.canRefresh = canRefresh;
    }

    private Boolean canLoadMore = true;//允许加载更多标志
    private Boolean canRefresh = true;//允许下拉刷新标志


    public boolean isIsloading() {
        return isloading;
    }

    private boolean isloading = false;//是否在数据加载请求中，不能操作
    private IOnScrollListener listener;//事件监听
    private float mLastY;//监听移动的位置


    private int paddingTop;
    private int paddingBottom;
    private int paddingLeft;
    private int paddingRight;
    //endregion

    /**
     * @return void
     * @description
     * @author tvrddmss
     * @time 2023/4/21 22:26
     */
    public void setFlowLayout() {
        //设置主轴方向为横轴
        MyFlexboxLayoutManager manager = new MyFlexboxLayoutManager(getContext(), FlexDirection.ROW);
        //设置item沿主轴方向的位置
        manager.setJustifyContent(JustifyContent.FLEX_START);
        //设置item 沿次轴方向的位置
        manager.setAlignItems(AlignItems.FLEX_START);
        this.setLayoutManager(manager);

//        /* 对齐方式 */
//        justify-content: center;     /* 居中排列 */
//        justify-content: start;      /* 从行首开始排列 */
//        justify-content: end;        /* 从行尾开始排列 */
//        justify-content: flex-start; /* 从行首起始位置开始排列 */
//        justify-content: flex-end;   /* 从行尾位置开始排列 */
//        justify-content: left;       /* 一个挨一个在对齐容器得左边缘 */
//        justify-content: right;      /* 元素以容器右边缘为基准，一个挨着一个对齐, */
//
//        /* 基线对齐 */
//        justify-content: baseline;
//        justify-content: first baseline;
//        justify-content: last baseline;
//
//        /* 分配弹性元素方式 */
//        justify-content: space-between;  /* 均匀排列每个元素
//                                   首个元素放置于起点，末尾元素放置于终点 */
//        justify-content: space-around;  /* 均匀排列每个元素
//                                   每个元素周围分配相同的空间 */
//        justify-content: space-evenly;  /* 均匀排列每个元素
//                                   每个元素之间的间隔相等 */
//        justify-content: stretch;       /* 均匀排列每个元素
//                                   'auto'-sized 的元素会被拉伸以适应容器的大小 */
//
//        /* 溢出对齐方式 */
//        justify-content: safe center;
//        justify-content: unsafe center;
//
//        /* 全局值 */
//        justify-content: inherit;
//        justify-content: initial;
//        justify-content: unset;
    }

    //region 初始方法

    /**
     * 初始化
     */
    private void init() {
        paddingTop = this.getPaddingTop();
        paddingBottom = this.getPaddingBottom();
        paddingLeft = this.getPaddingLeft();
        paddingRight = this.getPaddingRight();

//        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
//        this.setLayoutManager(flowLayoutManager);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        this.setLayoutManager(linearLayoutManager);//为recyclerview绑定布局  label为recyclerview


//        DividerItemDecoration normalDividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
//        this.addItemDecoration(normalDividerItemDecoration);
//        this.setNestedScrollingEnabled(false);

        setFlowLayout();

        this.setOnTouchListener(this);

    }

    //endregion

    //region 调用数据方法
    private void refreshData() {
        if (canRefresh && getListener() != null) {
            isloading = true;
            showHeader();
            getListener().onRefresh();
        }
    }

    private void loadMoreData() {
        if (canLoadMore && getListener() != null) {
            isloading = true;
            if (mWrapRecyclerAdapter.mFooterViews.indexOfValue(footerMore) < 0) {
                mWrapRecyclerAdapter.addFooterView(footerMore);
                ViewGroup.LayoutParams params = footerMore.getLayoutParams();
                params.height = 200;
                footerMore.setLayoutParams(params);
            }

            getListener().onLoadMore();
        }
    }

    private boolean isAllData() {
        boolean result = true;

        if (getListener() != null) {
            result = getListener().isAllData();
        }
        return result;
    }
    //endregion

    //region 滑动事件

    int onTouchModel = 0;//0：啥也不干，1：要刷新，-1：下拉，不刷新，2：要加载更多,-2:上拉，已加载全部

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        //数据加载中不能操作
        if (isloading) {
            return false;
        }
        if (mLastY == -1) {
            mLastY = motionEvent.getRawY();
        }

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_MOVE:
                final float deltaY = motionEvent.getRawY() - mLastY;
                //向上移动
                if (deltaY < 0) {
                    //是否滑到底部
                    if (!this.canScrollVertically(1) && canLoadMore) {
                        if (!isAllData()) {
                            //上拉加载更多
                            onTouchModel = 2;
                            int height = -(int) deltaY / 3;
                            if (mWrapRecyclerAdapter.mFooterViews.indexOfValue(footerMore) < 0) {
                                mWrapRecyclerAdapter.addFooterView(footerMore);
                            }
                            if (height <= 200) {
                                ViewGroup.LayoutParams params = footerMore.getLayoutParams();
                                params.height = height;
                                footerMore.setLayoutParams(params);
                            } else {
                                ViewGroup.LayoutParams params = footerMore.getLayoutParams();
                                params.height = 200;
                                footerMore.setLayoutParams(params);
                                this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + height - 200);
                            }
                        } else {
                            //不加载更多
                            onTouchModel = -2;
                            int height = -(int) deltaY / 3;
                            if (mWrapRecyclerAdapter.mFooterViews.size() == 0) {
                                mWrapRecyclerAdapter.addFooterView(footerNoMore);
                            }
                            if (height <= 200) {
                                ViewGroup.LayoutParams params = footerNoMore.getLayoutParams();
                                params.height = height;
                                footerNoMore.setLayoutParams(params);
                            } else {
                                this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + height - 200);
                            }
                        }

                    }

                }
                //向下移动
                else if (deltaY > 0) {
                    //是否滑到顶部,是否允许刷新
                    if (this.getScrollY() <= 0 && canRefresh) {
                        int height = (int) deltaY / 3;
                        if (height <= 200) {
                            header_text.setText("继续下拉");
                            ViewGroup.LayoutParams params = header.getLayoutParams();
                            params.height = height;
                            header.setLayoutParams(params);
                            //下拉不刷新
                            onTouchModel = -1;
                        } else {
                            header_text.setText("开始刷新");
                            this.setPadding(paddingLeft, paddingTop + height - 200, paddingRight, paddingBottom);
                            //刷新模式
                            onTouchModel = 1;
                        }

                    }

                }

                break;

            case MotionEvent.ACTION_DOWN:
                mLastY = motionEvent.getRawY();
                break;
            case MotionEvent.ACTION_UP:
//                this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                switch (onTouchModel) {
                    case 0:
                        break;
                    case -1: {
                        this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                        closeHeader();
                    }
                    break;
                    case 1: {
                        mWrapRecyclerAdapter.notifyDataSetChanged();
                        this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                        //不关闭动画-调用刷新
                        refreshData();
                    }
                    break;
                    case -2: {
                        this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                        closeFooter();
                    }
                    break;
                    case 2: {
                        this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                        //调用加载更多
                        loadMoreData();
                    }
                    break;
                }
                //清空模式
                onTouchModel = 0;
                //重置滑动开始点
                mLastY = -1;
                break;

        }
        return false;

    }

    //endregion

    //region 外部监听用于触发数据获取加载
    //事件监听
    public interface IOnScrollListener {

        void onRefresh();

        void onLoadMore();

        boolean isAllData();

    }

    public IOnScrollListener getListener() {

        return listener;

    }

    //设置事件监听
    public void setListener(IOnScrollListener listener) {

        this.listener = listener;

    }

    //endregion

    //region 控制loading 效果

    public void showHeader() {
        ViewGroup.LayoutParams params = header.getLayoutParams();
        params.height = 200;
        header.setLayoutParams(params);
    }

    private void closeHeader() {
        //延时关闭头部动画
        if (mWrapRecyclerAdapter.mHeaderViews.size() >= 0) {
            int height = header.getLayoutParams().height;
            int dur = 0;
            while (height > 0) {
                height -= 10;
                dur += 10;

                if (height < 0) {
                    height = 0;
                }
                final int h = height;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams params = header.getLayoutParams();
                        params.height = h;
                        header.setLayoutParams(params);
                    }
                }, dur);
            }

        }
    }

    private void closeFooter() {
        //延时关闭底部动画
        if (mWrapRecyclerAdapter.mFooterViews.indexOfValue(footerNoMore) >= 0) {
            int height = footerNoMore.getLayoutParams().height;
            int dur = 0;
            while (height > 0) {
                height -= 10;
                dur += 10;

                if (height < 0) {
                    height = 0;
                }
                final int h = height;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams params = footerNoMore.getLayoutParams();
                        params.height = h;
                        footerNoMore.setLayoutParams(params);
                        if (h == 0) {
                            MyRecyclerView.this.mWrapRecyclerAdapter.removeFooterView(footerNoMore);
                        }
                    }
                }, dur);
            }

        }
        if (mWrapRecyclerAdapter.mFooterViews.indexOfValue(footerMore) >= 0) {
            int height = footerMore.getLayoutParams().height;
            int dur = 0;
            while (height > 0) {
                height -= 10;
                dur += 10;

                if (height < 0) {
                    height = 0;
                }
                final int h = height;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams params = footerMore.getLayoutParams();
                        params.height = h;
                        footerMore.setLayoutParams(params);
                        if (h == 0) {
                            MyRecyclerView.this.mWrapRecyclerAdapter.removeFooterView(footerMore);
                        }
                    }
                }, dur);
            }

        }
    }
    //endregion

    //region 表头及表尾实现-数据监听
    private LinearLayout header = new LinearLayout(getContext());
    private TextView header_text = new TextView(getContext());

    private LinearLayout footerNoMore = new LinearLayout(getContext());
    private LinearLayout footerMore = new LinearLayout(getContext());
    // 包裹了一层的头部底部Adapter
    private WrapRecyclerAdapter mWrapRecyclerAdapter;
    // 这个是列表数据的Adapter
    private Adapter mAdapter;

    @Override
    public void setAdapter(Adapter adapter) {

        // 为了防止多次设置Adapter
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(mDataObserver);
            mAdapter = null;
        }
        this.mAdapter = adapter;
        if (adapter instanceof WrapRecyclerAdapter) {
            mWrapRecyclerAdapter = (WrapRecyclerAdapter) adapter;
        } else {
            mWrapRecyclerAdapter = new WrapRecyclerAdapter(adapter);
        }

        {
            header_text.setText("刷新");
            header_text.setHeight(200);
//        header_text.setGravity(Gravity.CENTER_VERTICAL);
//        header.addView(header_text);

            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource((R.drawable.loading));

            Drawable drawable = imageView.getDrawable();
            ((AnimatedImageDrawable) drawable).start();
            header.addView(imageView);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    0);
//        layoutParams.gravity = Gravity.CENTER_VERTICAL;
            header.setLayoutParams(layoutParams);
            header.setPadding(100, 10, 100, 10);
            header.setBackgroundColor(Color.WHITE);
            header.setGravity(Gravity.CENTER_VERTICAL);
            mWrapRecyclerAdapter.addHeaderView(header);
        }

        {
            LinearLayout.LayoutParams layoutParams_footer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    0);
            footerNoMore.setLayoutParams(layoutParams_footer);
            footerNoMore.setPadding(100, 10, 100, 10);
            footerNoMore.setGravity(Gravity.CENTER);

            TextView tv = new TextView(getContext());
            tv.setText("已经到底了！");
            tv.setGravity(Gravity.CENTER);
            footerNoMore.addView(tv);
        }
        {
            LinearLayout.LayoutParams layoutParams_footer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    0);
            footerMore.setLayoutParams(layoutParams_footer);
            footerMore.setPadding(100, 10, 100, 10);
            footerMore.setGravity(Gravity.CENTER);

            TextView tv = new TextView(getContext());
            tv.setText("加载中。。。！");
            tv.setGravity(Gravity.CENTER);
            footerMore.addView(tv);
        }

        super.setAdapter(mWrapRecyclerAdapter);
        // 注册一个观察者
        mAdapter.registerAdapterDataObserver(mDataObserver);
        // 解决GridLayout添加头部和底部也要占据一行
        mWrapRecyclerAdapter.adjustSpanSize(this);


    }

    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (mAdapter == null) return;
            // 观察者列表Adapter更新 包裹的也需要更新不然列表的notifyDataSetChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyDataSetChanged();

            //关闭头部动画
            closeHeader();

            //是否滑到底部
            if (!MyRecyclerView.this.canScrollVertically(1)) {
                if (canLoadMore && !isAllData()) {
                    loadMoreData();
                    return;
                }
            }
            //关闭尾部动画
            closeFooter();
            //关闭更新限制
            isloading = false;
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            // 观察者列表Adapter更新 包裹的也需要更新不然列表的notifyDataSetChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemRemoved(positionStart);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (mAdapter == null) return;
            // 观察者列表Adapter更新 包裹的也需要更新不然列表的notifyItemMoved没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            // 观察者列表Adapter更新 包裹的也需要更新不然列表的notifyItemChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemChanged(positionStart);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (mAdapter == null) return;
            // 观察者列表Adapter更新 包裹的也需要更新不然列表的notifyItemChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemChanged(positionStart, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            // 观察者列表Adapter更新 包裹的也需要更新不然列表的notifyItemInserted没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemInserted(positionStart);
        }
    };

    public class WrapRecyclerAdapter extends Adapter {

        private final static String TAG = "WrapRecyclerAdapter";

        // 用来存放底部和头部View的集合比Map要高效一些
        // 可以点击进入看一下官方的解释

        /**
         * SparseArrays map integers to Objects.Unlike a normal array of Objects,
         * <p>
         * there can be gaps in the indices.It is intended to be more memory efficient
         * <p>
         * than using a HashMap to map Integers to Objects, both because it avoids
         * <p>
         * auto-boxing keys and its data structure doesn't rely on an extra entry object
         * <p>
         * for each mapping.
         */

        private SparseArray mHeaderViews;

        private SparseArray mFooterViews;

        // 基本的头部类型开始位置用于viewType
        private int BASE_ITEM_TYPE_HEADER = 10000000;
        // 基本的底部类型开始位置用于viewType
        private int BASE_ITEM_TYPE_FOOTER = 20000000;
        // 列表的Adapter
        private Adapter mAdapter;

        public WrapRecyclerAdapter(Adapter adapter) {

            this.mAdapter = adapter;

            mHeaderViews = new SparseArray<>();

            mFooterViews = new SparseArray<>();

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // viewType 可能就是 SparseArray 的key
            if (isHeaderViewType(viewType)) {
                View headerView = (View) mHeaderViews.get(viewType);
                return createHeaderFooterViewHolder(headerView);
            }

            if (isFooterViewType(viewType)) {
                View footerView = (View) mFooterViews.get(viewType);
                return createHeaderFooterViewHolder(footerView);
            }

            return mAdapter.onCreateViewHolder(parent, viewType);

        }

        /**
         * 是不是底部类型
         */

        private boolean isFooterViewType(int viewType) {

            int position = mFooterViews.indexOfKey(viewType);

            return position >= 0;

        }

        /**
         * 创建头部或者底部的ViewHolder
         */

        private ViewHolder createHeaderFooterViewHolder(View view) {

            return new ViewHolder(view) {

            };

        }

        /**
         * 是不是头部类型
         */

        private boolean isHeaderViewType(int viewType) {

            int position = mHeaderViews.indexOfKey(viewType);

            return position >= 0;

        }

        @Override

        public void onBindViewHolder(ViewHolder holder, int position) {

            if (isHeaderPosition(position) || isFooterPosition(position)) {

                return;

            }

// 计算一下位置

            position = position - mHeaderViews.size();

            mAdapter.onBindViewHolder(holder, position);

        }

        @Override

        public int getItemViewType(int position) {

            if (isHeaderPosition(position)) {

// 直接返回position位置的key

                return mHeaderViews.keyAt(position);

            }

            if (isFooterPosition(position)) {

// 直接返回position位置的key

                position = position - mHeaderViews.size() - mAdapter.getItemCount();

                return mFooterViews.keyAt(position);

            }

// 返回列表Adapter的getItemViewType

            position = position - mHeaderViews.size();

            return mAdapter.getItemViewType(position);

        }

        /**
         * 是不是底部位置
         */

        private boolean isFooterPosition(int position) {

            return position >= (mHeaderViews.size() + mAdapter.getItemCount());

        }

        /**
         * 是不是头部位置
         */

        private boolean isHeaderPosition(int position) {

            return position < mHeaderViews.size();

        }

        @Override

        public int getItemCount() {
            // 条数三者相加 = 底部条数 + 头部条数 + Adapter的条数
            return mAdapter.getItemCount() + mHeaderViews.size() + mFooterViews.size();
        }

        /**
         * 获取列表的Adapter
         */

        private Adapter getAdapter() {
            return mAdapter;
        }

        // 添加头部
        public void addHeaderView(View view) {
            int position = mHeaderViews.indexOfValue(view);
            if (position < 0) {
                mHeaderViews.put(BASE_ITEM_TYPE_HEADER++, view);
            }
            notifyDataSetChanged();

        }

        // 添加底部
        public void addFooterView(View view) {

            int position = mFooterViews.indexOfValue(view);

            if (position < 0) {

                mFooterViews.put(BASE_ITEM_TYPE_FOOTER++, view);

            }

            notifyDataSetChanged();

        }

        // 移除头部
        public void removeHeaderView(View view) {

            int index = mHeaderViews.indexOfValue(view);

            if (index < 0) return;

            mHeaderViews.removeAt(index);

            notifyDataSetChanged();

        }

        // 移除底部
        public void removeFooterView(View view) {

            int index = mFooterViews.indexOfValue(view);

            if (index < 0) return;

            mFooterViews.removeAt(index);

            notifyDataSetChanged();

        }

        /**
         * 解决GridLayoutManager添加头部和底部不占用一行的问题
         *
         * @param recycler
         * @version 1.0
         */

        public void adjustSpanSize(RecyclerView recycler) {

            if (recycler.getLayoutManager() instanceof GridLayoutManager) {

                final GridLayoutManager layoutManager = (GridLayoutManager) recycler.getLayoutManager();

                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                    @Override

                    public int getSpanSize(int position) {

                        boolean isHeaderOrFooter =

                                isHeaderPosition(position) || isFooterPosition(position);

                        return isHeaderOrFooter ? layoutManager.getSpanCount() : 1;

                    }

                });

            }

        }

    }

    //endregion

    //region 公有方法

    public void refresh() {
        refreshData();
    }
    //endregioin
}
