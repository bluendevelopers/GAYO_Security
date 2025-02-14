package bluen.homein.gayo_security.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bluen.homein.gayo_security.ChangeWorkerActivity;
import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.base.BaseRecyclerAdapter;
import bluen.homein.gayo_security.rest.ResponseDataFormat;

public class PageNumberListAdapter extends BaseRecyclerAdapter<String, PageNumberListAdapter.ViewHolder> {
    private int currentPageNumber = 1;

    public PageNumberListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void setData(int newPageNumber) {
        this.currentPageNumber = newPageNumber;
        notifyDataSetChanged();
    }

    @Override
    protected ViewHolder onCreateViewHolderBase(View view) {
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolderBase(ViewHolder holder, int position) {

        holder.tvPageNumber.setText(mData.get(position));

        if (position == mData.size() - 1) {
            holder.vMarginRight.setVisibility(View.GONE);
        }

        if (mData.get(position).equals(String.valueOf(currentPageNumber))) {
            holder.tvPageNumber.setTextColor(Color.WHITE);
            holder.tvPageNumber.setTypeface(holder.tvPageNumber.getTypeface(), Typeface.BOLD);

        } else {
            holder.tvPageNumber.setTextColor(Color.BLACK);
            holder.tvPageNumber.setBackground(null);

        }
        //페이지 번호 클릭 시
        holder.layRoot.setOnClickListener(view -> {
            ((ChangeWorkerActivity) mContext).clickPageNumber(Integer.parseInt(mData.get(position)));
        });
    }

    // Recycler View Holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layRoot;
        TextView tvPageNumber;
        View vMarginRight;

        ViewHolder(@NonNull View v) {
            super(v);

            layRoot = v.findViewById(R.id.lay_root);
            tvPageNumber = v.findViewById(R.id.tv_page_number);
            vMarginRight = v.findViewById(R.id.v_margin_right);


        }
    }

}
