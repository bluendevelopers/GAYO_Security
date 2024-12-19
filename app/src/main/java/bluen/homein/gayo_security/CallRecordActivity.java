package bluen.homein.gayo_security;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.base.BaseRecyclerAdapter;
import butterknife.BindView;

public class CallRecordActivity extends BaseActivity {

    @BindView(R.id.rv_page_number)
    RecyclerView rvPageNumber;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    private PageNumberListAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_call_record;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, RecyclerView.HORIZONTAL);
        adapter = new PageNumberListAdapter(mContext, R.layout.item_page_number);

        List<String> tempList = new ArrayList<String>() {{
            add("1");
            add("2");
            add("3");
        }};

        adapter.addItems(tempList);
        if (tempList == null || tempList.isEmpty()) {
            tvEmptyView.setVisibility(View.VISIBLE);
        }
        rvPageNumber.setAdapter(adapter);

    }

    public static class PageNumberListAdapter extends BaseRecyclerAdapter<String, PageNumberListAdapter.ViewHolder> {
        private int currentPageNumber = 1;

        public PageNumberListAdapter(Context context, int resource) {
            super(context, resource);
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

                //페이지 번호 클릭 시
                layRoot.setOnClickListener(view -> {

                });

            }
        }

    }

}