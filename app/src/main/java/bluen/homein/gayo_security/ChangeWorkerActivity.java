package bluen.homein.gayo_security;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bluen.homein.gayo_security.adapter.PageNumberListAdapter;
import bluen.homein.gayo_security.adapter.WorkerListAdapter;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.rest.Retrofit;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeWorkerActivity extends BaseActivity {

    @BindView(R.id.lay_before_btn)
    LinearLayout layBeforeBtn;
    @BindView(R.id.lay_next_btn)
    LinearLayout layNextBtn;
    @BindView(R.id.rv_page_number)
    RecyclerView rvPageNumber;
    @BindView(R.id.tv_phone_number)
    TextView tvCurrentWorkerPhoneNumber;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    @BindView(R.id.lv_worker_list)
    ListView lvWorkerList;

    private WorkerListAdapter workerListAdapter;
    private PageNumberListAdapter pageNumberListAdapter;
    private List<String> pageList;
    private int currentPageNumber = 1; //default


    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    @OnClick(R.id.lay_before_btn)
    void clickBeforeBtn() {
        currentPageNumber--;
        getWorkerList();
    }

    @OnClick(R.id.lay_next_btn)
    void clickNextBtn() {
        currentPageNumber++;
        getWorkerList();
    }

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        finish();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_change_worker;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        hideNavigationBar();
        pageNumberListAdapter = new PageNumberListAdapter(ChangeWorkerActivity.this, R.layout.item_page_number);
        workerListAdapter = new WorkerListAdapter(ChangeWorkerActivity.this);

        lvWorkerList.setAdapter(workerListAdapter);
        lvWorkerList.setEmptyView(tvEmptyView);

        getWorkerList();
    }

    public void clickPageNumber(int pageNumber) {
        this.currentPageNumber = pageNumber;
        getWorkerList();
    }

    private void getWorkerList() {
        showProgress();

        Retrofit.WorkerChangePageApi workerChangePageApi = Retrofit.WorkerChangePageApi.retrofit.create(Retrofit.WorkerChangePageApi.class);

        Call<ResponseDataFormat.WorkerListBody> call = workerChangePageApi.workerListPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.WorkerListBody(serialCode, buildingCode, currentPageNumber)); // test
        call.enqueue(new Callback<ResponseDataFormat.WorkerListBody>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.WorkerListBody> call, Response<ResponseDataFormat.WorkerListBody> response) {
                closeProgress();
                hideNavigationBar();

                if (response.body() != null) {
                    if (response.body().getMessage() == null) {
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

                        tvCurrentWorkerPhoneNumber.setText(response.body().getCurrentManagerInfo().getWorkerPhoneNumber());
                        workerListAdapter.setData(response.body().getWorkerList());

                        pageNumberListAdapter.setItems(pageList);
                        pageNumberListAdapter.setData(currentPageNumber);
                        rvPageNumber.setAdapter(pageNumberListAdapter);
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.WorkerListBody> call, Throwable t) {
                closeProgress();
                hideNavigationBar();

            }
        });

    }

}