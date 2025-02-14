package bluen.homein.gayo_security;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import bluen.homein.gayo_security.adapter.PageNumberListAdapter;
import bluen.homein.gayo_security.adapter.VisitHistoryListAdapter;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.rest.Retrofit;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitHistoryListActivity extends BaseActivity {

    @BindView(R.id.lay_before_btn)
    LinearLayout layBeforeBtn;
    @BindView(R.id.lay_next_btn)
    LinearLayout layNextBtn;

    @BindView(R.id.rv_page_number)
    RecyclerView rvPageNumber;
    @BindView(R.id.lv_visit_history)
    ListView lvVisitHistory;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    @BindView(R.id.tv_start_date)
    TextView tvSelectedStartDate;
    @BindView(R.id.tv_end_date)
    TextView tvSelectedEndDate;

    @OnClick(R.id.lay_before_btn)
    void clickBeforeBtn() {
        currentPageNumber--;
        getVisitHistoryList();
    }

    @OnClick(R.id.lay_next_btn)
    void clickNextBtn() {
        currentPageNumber++;
        getVisitHistoryList();
    }

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        finish();
    }

    private PageNumberListAdapter pageNumberListAdapter;
    private VisitHistoryListAdapter visitHistoryListAdapter;
    private int currentPageNumber = 1; //default
    private String startDate = ""; //default
    private String endDate = ""; //default
    private List<String> pageList;
    private List<ResponseDataFormat.VisitType> visitTypeList;
    private int selectedTypeIndex = 0; //default
    private List<ResponseDataFormat.VisitHistoryListBody.VisitHistoryInfo> visitHistoryInfoList;

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_visit_list;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        hideNavigationBar();
        pageNumberListAdapter = new PageNumberListAdapter(VisitHistoryListActivity.this, R.layout.item_page_number);

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        startDate = formattedDate;
        endDate = formattedDate;
        tvSelectedStartDate.setText(startDate);
        tvSelectedEndDate.setText(endDate);

        visitHistoryListAdapter = new VisitHistoryListAdapter(VisitHistoryListActivity.this);

        lvVisitHistory.setAdapter(visitHistoryListAdapter);
        lvVisitHistory.setEmptyView(findViewById(R.id.tv_empty_view));
        getVisitTypeList();

    }

    private void getVisitTypeList() {
        showProgress();
        Retrofit.VisitorHistoryApi visitorHistoryApi = Retrofit.VisitorHistoryApi.retrofit.create(Retrofit.VisitorHistoryApi.class);
        Call<List<ResponseDataFormat.VisitType>> call = visitorHistoryApi.loadVisitorTypeListGet(mPrefGlobal.getAuthorization());

        call.enqueue(new Callback<List<ResponseDataFormat.VisitType>>() {
            @Override
            public void onResponse(Call<List<ResponseDataFormat.VisitType>> call, Response<List<ResponseDataFormat.VisitType>> response) {
                if (response.body() != null) {

                    visitTypeList = response.body();
                    getVisitHistoryList();

                } else {
                    closeProgress();

                }
            }

            @Override
            public void onFailure(Call<List<ResponseDataFormat.VisitType>> call, Throwable t) {
                closeProgress();

            }
        });
    }

    private void getVisitHistoryList() {
        Retrofit.VisitorHistoryApi visitorHistoryApi = Retrofit.VisitorHistoryApi.retrofit.create(Retrofit.VisitorHistoryApi.class);

        Call<ResponseDataFormat.VisitHistoryListBody> call = visitorHistoryApi.visitHistoryListPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.VisitorRecordListBody(serialCode, buildingCode, currentPageNumber, startDate, endDate, visitTypeList.get(selectedTypeIndex).getEntranceCode()));

        call.enqueue(new Callback<ResponseDataFormat.VisitHistoryListBody>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.VisitHistoryListBody> call, Response<ResponseDataFormat.VisitHistoryListBody> response) {
                closeProgress();
                hideNavigationBar();

                if (response.body() != null) {

                    pageList = new ArrayList<>();

                    if (response.body().getPageCountInfo().getTotalPageCnt() != 0) {
                        for (int i = 1; i <= response.body().getPageCountInfo().getTotalPageCnt(); i++) {
                            if (!pageList.contains(String.valueOf(i))) {
                                pageList.add(String.valueOf(i));
                            }
                        }
                    } else {
                        if (!pageList.contains("1")) {
                            pageList.add("1");
                        }
                    }
                    layBeforeBtn.setVisibility(View.VISIBLE);
                    layNextBtn.setVisibility(View.VISIBLE);
                    if (currentPageNumber == 1) {
                        layBeforeBtn.setVisibility(View.INVISIBLE);
                    } else if (currentPageNumber == response.body().getPageCountInfo().getTotalPageCnt()) {
                        layNextBtn.setVisibility(View.INVISIBLE);
                    }

                    visitHistoryListAdapter.setData(response.body().getVisitHistoryList());
                    pageNumberListAdapter.setItems(pageList);
                    rvPageNumber.setAdapter(pageNumberListAdapter);
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.VisitHistoryListBody> call, Throwable t) {
                closeProgress();
                hideNavigationBar();

            }
        });
    }

}