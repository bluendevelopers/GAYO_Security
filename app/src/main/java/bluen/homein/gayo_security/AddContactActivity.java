package bluen.homein.gayo_security;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bluen.homein.gayo_security.adapter.FacilityContactListAdapter;
import bluen.homein.gayo_security.adapter.PageNumberListAdapter;
import bluen.homein.gayo_security.adapter.WorkerListAdapter;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.base.BaseRecyclerAdapter;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.rest.Retrofit;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddContactActivity extends BaseActivity {
    @BindView(R.id.lay_before_btn)
    LinearLayout layBeforeBtn;
    @BindView(R.id.lay_next_btn)
    LinearLayout layNextBtn;

    @BindView(R.id.rv_page_number)
    RecyclerView rvPageNumber;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;

    private FacilityContactListAdapter facilityContactListAdapter;
    private PageNumberListAdapter pageNumberListAdapter;
    private List<String> pageList;
    private int currentPageNumber = 1; //default

    @OnClick(R.id.lay_before_btn)
    void clickBeforeBtn() {
        currentPageNumber--;
        getFacilityContactList();
    }


    @OnClick(R.id.lay_next_btn)
    void clickNextBtn() {
        currentPageNumber++;
        getFacilityContactList();
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
        return R.layout.activity_add_contact;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        hideNavigationBar();
        getFacilityContactList();
    }

    private void getFacilityContactList() {

        showProgress();

        Retrofit.AddFacilityContactApi addFacilityContactApi = Retrofit.AddFacilityContactApi.retrofit.create(Retrofit.AddFacilityContactApi.class);

        Call<ResponseDataFormat.FacilityContactListBody> call = addFacilityContactApi.loadContactListPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.ContactBody(serialCode, buildingCode, currentPageNumber));

        call.enqueue(new Callback<ResponseDataFormat.FacilityContactListBody>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.FacilityContactListBody> call, Response<ResponseDataFormat.FacilityContactListBody> response) {
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

                        facilityContactListAdapter.setData(response.body().getFacilityContactList());

                        pageNumberListAdapter.setItems(pageList);
                        pageNumberListAdapter.setData(currentPageNumber);
                        rvPageNumber.setAdapter(pageNumberListAdapter);
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.FacilityContactListBody> call, Throwable t) {
                closeProgress();
                hideNavigationBar();

            }
        });

    }


}