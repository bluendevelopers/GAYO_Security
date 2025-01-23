package bluen.homein.gayo_security;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import bluen.homein.gayo_security.adapter.WorkRecordListAdapter;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.base.BaseRecyclerAdapter;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.rest.Retrofit;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkRecordActivity extends BaseActivity {

    @BindView(R.id.rv_page_number)
    RecyclerView rvPageNumber;
    @BindView(R.id.lv_work_record)
    ListView lvWorkRecord;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    @BindView(R.id.tv_selected_start_date)
    TextView tvSelectedStartDate;

    private PageNumberListAdapter pageNumberListAdapter;
    private int currentPageNumber = 1; //default
    private String startDate = ""; //default
    private String endDate = ""; //default
    private String workerPhoneNumber = ""; //default
    private String division = "00"; //default
    private List<String> pageList;
    private List<ResponseDataFormat.WorkRecordListBody.WorkRecordInfo> workerRecordList;
    private WorkRecordListAdapter workRecordListAdapter;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_work_record;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, RecyclerView.HORIZONTAL);
        pageNumberListAdapter = new PageNumberListAdapter(mContext, R.layout.item_page_number);

        // 최초진입시 당일 전체로 검색 api 실행

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        startDate = formattedDate;
        tvSelectedStartDate.setText(startDate);

        workRecordListAdapter = new WorkRecordListAdapter(WorkRecordActivity.this);
        lvWorkRecord.setAdapter(workRecordListAdapter);
        lvWorkRecord.setEmptyView(tvEmptyView);
        getDivisionList();
        getWorkRecordList();
    }

    private void getDivisionList() {
    }


    private void getWorkRecordList( ) {
        showProgress();

        Retrofit.WorkRecordApi workRecordApi = Retrofit.WorkRecordApi.retrofit.create(Retrofit.WorkRecordApi.class);

        Call<ResponseDataFormat.WorkRecordListBody> call = workRecordApi.workRecordListPost(mPrefGlobal.getAuthorization(),new RequestDataFormat.WorkRecordListBody(serialCode,buildingCode,currentPageNumber, startDate, endDate,workerPhoneNumber,division)); // test
        call.enqueue(new Callback<ResponseDataFormat.WorkRecordListBody>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.WorkRecordListBody> call, Response<ResponseDataFormat.WorkRecordListBody> response) {
                closeProgress();
                if (response.body() != null) {
                    if (response.body().getMessage() == null) {
                        workRecordListAdapter.setData(response.body().getWorkRecordList());

//                        pageNumberListAdapter.addItems(pageList);
//                        rvPageNumber.setAdapter(pageNumberListAdapter);
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.WorkRecordListBody> call, Throwable t) {
                closeProgress();

            }
        });

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