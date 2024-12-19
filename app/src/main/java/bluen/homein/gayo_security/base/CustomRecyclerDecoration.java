package bluen.homein.gayo_security.base;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class CustomRecyclerDecoration extends RecyclerView.ItemDecoration {
    private final int divHeight;

    public CustomRecyclerDecoration(Context context, int divHeightInDp) {
        this.divHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, divHeightInDp, context.getResources().getDisplayMetrics()
        );
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = divHeight;
    }
}