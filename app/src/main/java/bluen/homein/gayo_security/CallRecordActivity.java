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

import bluen.homein.gayo_security.adapter.CallRecordListAdapter;
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

public class CallRecordActivity extends BaseActivity {
    @BindView(R.id.lay_before_btn)
    LinearLayout layBeforeBtn;
    @BindView(R.id.lay_next_btn)
    LinearLayout layNextBtn;


    @BindView(R.id.rv_page_number)
    RecyclerView rvPageNumber;
    @BindView(R.id.lv_call_record)
    ListView lvCallRecord;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    @BindView(R.id.tv_selected_start_date)
    TextView tvSelectedStartDate;
    @BindView(R.id.tv_selected_end_date)
    TextView tvSelectedEndDate;

    private CallRecordListAdapter callRecordListAdapter;
    private PageNumberListAdapter pageNumberListAdapter;
    private int currentPageNumber = 1; //default
    private String startDate = ""; //default
    private String endDate = ""; //default
    private List<String> pageList;
    private List<ResponseDataFormat.EtcType> callTypeList;
    private int selectedTypeIndex = 0; //default

    @OnClick(R.id.lay_before_btn)
    void clickBeforeBtn() {
        currentPageNumber--;
        getCallRecordList();
    }

    @OnClick(R.id.lay_next_btn)
    void clickNextBtn() {
        currentPageNumber++;
        getCallRecordList();
    }

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_call_record;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        hideNavigationBar();
        pageNumberListAdapter = new PageNumberListAdapter(CallRecordActivity.this, R.layout.item_page_number);

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        startDate = formattedDate;
        endDate = formattedDate;
        tvSelectedStartDate.setText(startDate);
        tvSelectedEndDate.setText(endDate);

        callRecordListAdapter = new CallRecordListAdapter(CallRecordActivity.this);

        lvCallRecord.setAdapter(callRecordListAdapter);
        lvCallRecord.setEmptyView(tvEmptyView);


        getCallTypeList();

    }

    private void getCallTypeList() {
        showProgress();
        Retrofit.CallRecordApi callRecordApi = Retrofit.CallRecordApi.retrofit.create(Retrofit.CallRecordApi.class);

        Call<List<ResponseDataFormat.EtcType>> call = callRecordApi.loadCallStateTypeListGet(mPrefGlobal.getAuthorization());

        call.enqueue(new Callback<List<ResponseDataFormat.EtcType>>() {
            @Override
            public void onResponse(Call<List<ResponseDataFormat.EtcType>> call, Response<List<ResponseDataFormat.EtcType>> response) {
                if (response.body() != null) {
                    callTypeList = response.body();
                    getCallRecordList();

                } else {
                    closeProgress();

                }
            }

            @Override
            public void onFailure(Call<List<ResponseDataFormat.EtcType>> call, Throwable t) {
                closeProgress();

            }
        });
    }

    private void getCallRecordList() {

        showProgress();
        Retrofit.CallRecordApi callRecordApi = Retrofit.CallRecordApi.retrofit.create(Retrofit.CallRecordApi.class);

        Call<ResponseDataFormat.CallRecordListBody> call = callRecordApi.callRecordListPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.CallRecordListBody(serialCode, buildingCode, currentPageNumber, startDate, endDate, callTypeList.get(selectedTypeIndex).getCodeNumber()));

        call.enqueue(new Callback<ResponseDataFormat.CallRecordListBody>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.CallRecordListBody> call, Response<ResponseDataFormat.CallRecordListBody> response) {
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

                    callRecordListAdapter.setData(response.body().getCallRecordList());
                    pageNumberListAdapter.setItems(pageList);
                    rvPageNumber.setAdapter(pageNumberListAdapter);
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.CallRecordListBody> call, Throwable t) {
                closeProgress();
                hideNavigationBar();

            }
        });
    }


}