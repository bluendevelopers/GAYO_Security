package bluen.homein.gayo_security.activity.addFacilityContact;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.dialog.PopupDialog;
import bluen.homein.gayo_security.publicAdapter.PageNumberListAdapter;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.rest.Retrofit;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddContactActivity extends BaseActivity {

    public static final int RESULT_CODE_ADD_CONTACT = 3;

    @BindView(R.id.lay_before_btn)
    LinearLayout layBeforeBtn;
    @BindView(R.id.lay_next_btn)
    LinearLayout layNextBtn;
    @BindView(R.id.lv_add_contact)
    ListView lvAddContact;
    @BindView(R.id.rv_page_number)
    RecyclerView rvPageNumber;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    @BindView(R.id.et_facility_name)
    EditText etFacilityName;
    @BindView(R.id.et_ip_address)
    EditText etIpAddress;

    private FacilityContactListAdapter facilityContactListAdapter;
    private PageNumberListAdapter pageNumberListAdapter;
    private List<String> pageList;
    private int currentPageNumber = 1; //default
    private int currentTotalPageCount = 0; //default
    private boolean mIsAdd = false;
    boolean mIsDelete = false;
    ResponseDataFormat.FacilityContactListBody.FacilityContactInfo deleteItem = null;
    private List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo> currentContactList;

    @OnClick(R.id.lay_top)
    void clickLayTop() {
        hideAndClearFocus(this, etFacilityName);
        hideAndClearFocus(this, etIpAddress);

    }

    @OnClick(R.id.lay_filter)
    void clickLayFilter() {
        hideAndClearFocus(this, etFacilityName);
        hideAndClearFocus(this, etIpAddress);

    }

    @OnClick(R.id.btn_add_contact)
    void clickAddContract() {
        clickLayTop();
        mIsAdd = true;
        showPopupDialog(etFacilityName.getText().toString() + "\n", "연락처를 추가 하시겠습니까?", getString(R.string.cancel), getString(R.string.confirm));

    }

    @OnClick(R.id.btn_load_contact)
    void clickLoadContact() {
        //code
    }

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
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        setResult(RESULT_CODE_ADD_CONTACT);
        finish();
    }

    @OnClick(R.id.lay_back_btn)
    void clickBackBtn() {
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
        popupDialog.onCallBack(new PopupDialog.DialogCallback() {
            @Override
            public void onFinish() {
                if (mIsAdd) {
                    mIsAdd = false;
                    getFacilityContactList();
                } else if (mIsDelete) {
                    mIsDelete = false;
                    if (deleteItem == null) {
                        getFacilityContactList();
                    } else {
                        deleteItem = null;
                    }
                }
            }

            @Override
            public void onNextStep() {
                if (mIsAdd) {

                    if (etFacilityName.getText().toString().trim().isEmpty()) {
                        showWarningDialog("시설 명을 입력해주세요.", getString(R.string.confirm));
                        return;
                    }
                    if (etIpAddress.getText().toString().trim().isEmpty()) {
                        showWarningDialog("IP 주소를 입력해주세요.", getString(R.string.confirm));
                        return;
                    }
                    addContactItem();

                } else if (mIsDelete) {
                    if (deleteItem != null) {
                        deleteContactItem();
                    } else {
                        mIsDelete = false;
                        getFacilityContactList();
                    }
                } else {

                }
            }
        });
        pageNumberListAdapter = new PageNumberListAdapter(AddContactActivity.this, R.layout.item_page_number, new PageNumberListAdapter.OnPageNumberClickListener() {
            @Override
            public void clickPageNumber(int pageNumber) {
                currentPageNumber = pageNumber;
                getFacilityContactList();
            }
        });
        facilityContactListAdapter = new FacilityContactListAdapter(AddContactActivity.this);
        lvAddContact.setAdapter(facilityContactListAdapter);
        lvAddContact.setEmptyView(tvEmptyView);
        getFacilityContactList();
    }

    private void addContactItem() {
        showProgress();

        String facilityName = etFacilityName.getText().toString().trim();
        String ipAddress = etIpAddress.getText().toString().trim();
        Retrofit.AddFacilityContactApi facilityContactApi = Retrofit.AddFacilityContactApi.retrofit.create(Retrofit.AddFacilityContactApi.class);
        Call<ResponseDataFormat.FacilityContactListBody> call = facilityContactApi.addContactPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.ContactBody(serialCode, buildingCode, facilityName, ipAddress));
        call.enqueue(new Callback<ResponseDataFormat.FacilityContactListBody>() {

            @Override
            public void onResponse(Call<ResponseDataFormat.FacilityContactListBody> call, Response<ResponseDataFormat.FacilityContactListBody> response) {
                closeProgress();
                hideNavigationBar();
                if (response.body() != null) {
                    if (response.body().getResult().equals("OK")) {
                        if (currentContactList.size() == 4 && currentPageNumber == currentTotalPageCount) {
                            currentPageNumber = currentTotalPageCount + 1;
                        }
                        showPopupDialog(facilityName, "연락처가 추가 되었습니다.", getString(R.string.confirm));
                    } else {
                        mIsAdd = false;
                        showWarningDialog(response.body().getMessage(), getString(R.string.confirm));
                    }
                } else if (response.errorBody() != null) {
                    String error = "";
                    try {
                        error = response.errorBody().string();
                        // "response.errorBody().string();" will return the correct value ONLY ONCE.
                        // So store the value in a variable with the first call.
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showWarningDialog(ErrorBodyParser.JsonParser(error)[ErrorBodyParser.ERROR_MESSAGE_NUM].replace("\"", ""), getString(R.string.confirm));

                } else {
                    mIsAdd = false;
                    //code

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.FacilityContactListBody> call, Throwable t) {
                closeProgress();
                hideNavigationBar();
                //code

            }
        });

    }

    private void deleteContactItem() {
        showProgress();

        Retrofit.AddFacilityContactApi facilityContactApi = Retrofit.AddFacilityContactApi.retrofit.create(Retrofit.AddFacilityContactApi.class);
        Call<ResponseDataFormat.FacilityContactListBody> call = facilityContactApi.deleteContactPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.ContactBody(serialCode, buildingCode, deleteItem.getFacilityName(), deleteItem.getFacilityIPAddress()));

        call.enqueue(new Callback<ResponseDataFormat.FacilityContactListBody>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.FacilityContactListBody> call, Response<ResponseDataFormat.FacilityContactListBody> response) {
                closeProgress();
                hideNavigationBar();
                if (response.body() != null) {
                    if (response.body().getResult().equals("OK")) {
                        String facilityName = deleteItem.getFacilityName();
                        deleteItem = null;
                        if (currentContactList.size() == 1 && currentPageNumber > 1) {
                            currentPageNumber--;
                        }
                        showPopupDialog(facilityName, "연락처가 삭제 되었습니다.", getString(R.string.confirm));

                    } else {
                        mIsDelete = false;
                        showWarningDialog(response.body().getMessage(), getString(R.string.confirm));
                        //code
                    }
                } else {
                    mIsDelete = false;
                    //code
                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.FacilityContactListBody> call, Throwable t) {
                closeProgress();
                hideNavigationBar();
                //code

            }
        });
    }

    private void getFacilityContactList() {

        showProgress();

        Retrofit.AddFacilityContactApi facilityContactApi = Retrofit.AddFacilityContactApi.retrofit.create(Retrofit.AddFacilityContactApi.class);

        Call<ResponseDataFormat.FacilityContactListBody> call = facilityContactApi.loadContactListPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.ContactBody(serialCode, buildingCode, currentPageNumber));

        call.enqueue(new Callback<ResponseDataFormat.FacilityContactListBody>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.FacilityContactListBody> call, Response<ResponseDataFormat.FacilityContactListBody> response) {
                closeProgress();
                etFacilityName.setFocusableInTouchMode(true);
                etFacilityName.setFocusable(true);
                etIpAddress.setFocusableInTouchMode(true);
                etIpAddress.setFocusable(true);

                hideNavigationBar();

                if (response.body() != null) {

                    if (response.body().getMessage() == null) {
                        if (response.body().getFacilityContactList() != null) {
                            currentContactList = response.body().getFacilityContactList();
                            pageList = new ArrayList<>();
                            layBeforeBtn.setVisibility(View.VISIBLE);
                            layNextBtn.setVisibility(View.VISIBLE);
                            if (response.body().getPageCountInfo().getTotalPageCnt() != 0) {
                                for (int i = 1; i <= response.body().getPageCountInfo().getTotalPageCnt(); i++) {
                                    if (!pageList.contains(String.valueOf(i))) {
                                        pageList.add(String.valueOf(i));
                                    }
                                }
                            } else {
                                layNextBtn.setVisibility(View.INVISIBLE);
                                if (!pageList.contains("1")) {
                                    pageList.add("1");
                                }
                            }

                            if (currentPageNumber == 1) {
                                layBeforeBtn.setVisibility(View.INVISIBLE);
                            }
                            if (response.body().getPageCountInfo() != null) {
                                if (currentPageNumber == response.body().getPageCountInfo().getTotalPageCnt()) {
                                    currentTotalPageCount = response.body().getPageCountInfo().getTotalPageCnt();
                                    layNextBtn.setVisibility(View.INVISIBLE);
                                }
                            }

                            facilityContactListAdapter.setData(response.body().getFacilityContactList());

                            pageNumberListAdapter.setItems(pageList);
                            pageNumberListAdapter.setPageNumber(currentPageNumber);
                            rvPageNumber.setAdapter(pageNumberListAdapter);
                        } else {

                        }
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.FacilityContactListBody> call, Throwable t) {
                closeProgress();
                etFacilityName.setFocusableInTouchMode(true);
                etFacilityName.setFocusable(true);
                etIpAddress.setFocusableInTouchMode(true);
                etIpAddress.setFocusable(true);
                hideNavigationBar();

            }
        });

    }

    public void hideAndClearFocus(Context _context, EditText _editText) {
        if (_editText.isFocused()) {
            InputMethodManager inputManager = (InputMethodManager) _context.getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(_editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            _editText.clearFocus();
        }
    }

}