package bluen.homein.gayo_security;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.base.BaseRecyclerAdapter;
import bluen.homein.gayo_security.base.CustomRecyclerDecoration;
import butterknife.BindView;

public class WorkRecordActivity extends BaseActivity {

    @BindView(R.id.rv_page_number)
    RecyclerView rvPageNumber;
    private PageNumberListAdapter adapter;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_work_record;
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
        rvPageNumber.setAdapter(adapter);

    }

    public static class PageNumberListAdapter extends BaseRecyclerAdapter<String, PageNumberListAdapter.ViewHolder> {
        private int currentPageNumber = 1;

        public PageNumberListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        protected PageNumberListAdapter.ViewHolder onCreateViewHolderBase(View view) {
            return new PageNumberListAdapter.ViewHolder(view);
        }

        @Override
        protected void onBindViewHolderBase(PageNumberListAdapter.ViewHolder holder, int position) {
            holder.tvPageNumber.setText(mData.get(position));

            if (mData.get(position).equals(String.valueOf(currentPageNumber))) {
                holder.tvPageNumber.setTextColor(Color.WHITE);
                holder.tvPageNumber.setTypeface(holder.tvPageNumber.getTypeface(), Typeface.BOLD);

            } else {
                holder.tvPageNumber.setTextColor(Color.BLACK);
                holder.layRoot.setBackground(null);

            }

        }

        // Recycler View Holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            ConstraintLayout layRoot;
            TextView tvPageNumber;


            ViewHolder(@NonNull View v) {
                super(v);

                layRoot = v.findViewById(R.id.lay_root);
                tvPageNumber = v.findViewById(R.id.tv_page_number);

                //페이지 번호 클릭 시
                layRoot.setOnClickListener(view -> {

                });

            }
        }

    }

}