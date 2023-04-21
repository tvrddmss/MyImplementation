package tvrddmss.mylistview.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayoutManager;

/**
 * @ProjectName: MyImplementation
 * @Package: tvrddmss.mylistview.flowlayout
 * @ClassName: MyFlexboxLayoutManager
 * @Description: java类作用描述
 * @Author: tvrddmss
 * @CreateDate: 2023/4/21 23:09
 * @UpdateUser: tvrddmss
 * @UpdateDate: 2023/4/21 23:09
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MyFlexboxLayoutManagerJava extends FlexboxLayoutManager {


    public MyFlexboxLayoutManagerJava(Context context) {
        super(context);
    }

    public MyFlexboxLayoutManagerJava(Context context, int flexDirection) {
        super(context, flexDirection);
    }

    public MyFlexboxLayoutManagerJava(Context context, int flexDirection, int flexWrap) {
        super(context, flexDirection, flexWrap);
    }

    public MyFlexboxLayoutManagerJava(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof RecyclerView.LayoutParams) {
            return new LayoutParams(lp);
        } else if (lp instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams(lp);
        } else {
            return new LayoutParams(lp);
        }
    }
}