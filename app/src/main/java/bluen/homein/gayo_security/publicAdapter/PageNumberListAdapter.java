package bluen.homein.gayo_security.publicAdapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.base.BaseRecyclerAdapter;


public class PageNumberListAdapter extends BaseRecyclerAdapter<String, PageNumberListAdapter.ViewHolder> {

    private int currentPageNumber = 1;
    private OnPageNumberClickListener listener;

    public PageNumberListAdapter(Context context, int resource,
                                 OnPageNumberClickListener listener) {
        super(context, resource);
        this.listener = listener;
    }

    public void setPageNumber(int newPageNumber) {
        this.currentPageNumber = newPageNumber;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        int total = mData.size();
        return Math.min(5, total);
    }

    private int getStartPage() {
        int total = mData.size();
        int halfWindow = 5 / 2; // 2
        int start = currentPageNumber - halfWindow;

        // if we’re too far left, snap to 1
        if (start < 1) {
            start = 1;
        }
        // if window overruns the end, snap so the last page is shown
        if (start + getItemCount() - 1 > total) {
            start = Math.max(1, total - getItemCount() + 1);
        }
        return start;
    }

    private int pageNumberForPosition(int position) {
        return getStartPage() + position;
    }

    @Override
    protected ViewHolder onCreateViewHolderBase(View view) {
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolderBase(ViewHolder holder, int position) {
        int pageNum = pageNumberForPosition(position);
        holder.tvPageNumber.setText(String.valueOf(pageNum));

        if (position == getItemCount() - 1) {
            holder.vMarginRight.setVisibility(View.GONE);
        } else {
            holder.vMarginRight.setVisibility(View.VISIBLE);
        }

        if (pageNum == currentPageNumber) {
            holder.tvPageNumber.setTextColor(Color.WHITE);
            holder.tvPageNumber.setTypeface(
                    holder.tvPageNumber.getTypeface(), Typeface.BOLD);
        } else {
            holder.tvPageNumber.setTextColor(Color.BLACK);
            holder.tvPageNumber.setTypeface(
                    holder.tvPageNumber.getTypeface(), Typeface.NORMAL);
            holder.tvPageNumber.setBackground(null);
        }

        holder.layRoot.setOnClickListener(v ->
                listener.clickPageNumber(pageNum)
        );
    }

    public interface OnPageNumberClickListener {
        void clickPageNumber(int pageNumber);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layRoot;
        TextView tvPageNumber;
        View vMarginRight;

        ViewHolder(@NonNull View v) {
            super(v);
            layRoot       = v.findViewById(R.id.lay_root);
            tvPageNumber  = v.findViewById(R.id.tv_page_number);
            vMarginRight  = v.findViewById(R.id.v_margin_right);
        }
    }
}
