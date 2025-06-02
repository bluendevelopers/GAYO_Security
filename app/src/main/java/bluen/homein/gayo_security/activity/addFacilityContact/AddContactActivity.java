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
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
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
    public static final int RESULT_CODE_ADD_CONTACT_REFRESH = 4;

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
    @BindView(R.id.et_ip_address_1)
    EditText etIpAddress1;
    @BindView(R.id.et_ip_address_2)
    EditText etIpAddress2;
    @BindView(R.id.et_ip_address_3)
    EditText etIpAddress3;
    @BindView(R.id.et_ip_address_4)
    EditText etIpAddress4;

    private FacilityContactListAdapter facilityContactListAdapter;
    private PageNumberListAdapter pageNumberListAdapter;
    private List<String> pageList;
    private int currentPageNumber = 1; //default
    private int currentTotalPageCount = 0; //default
    public int STATUS_CODE = 0;
    public final int STATUS_ADD_CONTACT = 1;
    public final int STATUS_DELETE_CONTACT = 2;
    public final int STATUS_LOAD_ALL_CONTACT = 3;

    ResponseDataFormat.FacilityContactListBody.FacilityContactInfo deleteItem = null;
    private List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo> currentContactList;

    @OnClick(R.id.lay_top)
    void clickLayTop() {
        hideAndClearFocus(this, etFacilityName);
        hideAndClearFocus(this, etIpAddress1);
        hideAndClearFocus(this, etIpAddress2);
        hideAndClearFocus(this, etIpAddress3);
        hideAndClearFocus(this, etIpAddress4);

    }

    @OnClick(R.id.lay_filter)
    void clickLayFilter() {
        hideAndClearFocus(this, etFacilityName);
        hideAndClearFocus(this, etIpAddress1);
        hideAndClearFocus(this, etIpAddress2);
        hideAndClearFocus(this, etIpAddress3);
        hideAndClearFocus(this, etIpAddress4);

    }

    @OnClick(R.id.btn_add_contact)
    void clickAddContract() {
        clickLayTop();
        STATUS_CODE = STATUS_ADD_CONTACT;
        showPopupDialog(etFacilityName.getText().toString() + "\n", "연락처를 추가 하시겠습니까?", getString(R.string.cancel), getString(R.string.confirm));

    }

    @OnClick(R.id.btn_load_contact)
    void clickLoadContact() {
        //code
        STATUS_CODE = STATUS_LOAD_ALL_CONTACT;
        showWarningDialog("서버 클라우드를 연결하여\n" + "모든 연락처를 불러옵니다.\n(기존 등록된 연락처 정보는 덮어 씌워집니다.)", "취 소", "진행 하기");
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
        setResult(RESULT_CODE_ADD_CONTACT_REFRESH);
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
                if (STATUS_CODE == STATUS_ADD_CONTACT) {
                    STATUS_CODE = 0;
                    getFacilityContactList();
                } else if (STATUS_CODE == STATUS_DELETE_CONTACT) {
                    STATUS_CODE = 0;
                    if (deleteItem == null) {
                        getFacilityContactList();
                    } else {
                        deleteItem = null;
                    }
                } else {
                    STATUS_CODE = 0;
                }
            }

            @Override
            public void onNextStep() {
                if (STATUS_CODE == STATUS_ADD_CONTACT) {

                    if (etFacilityName.getText().toString().trim().isEmpty()) {
                        showWarningDialog("시설 명을 입력해주세요.", getString(R.string.confirm));
                        return;
                    }
                    if (etIpAddress1.getText().toString().trim().isEmpty()) {
                        showWarningDialog("IP 주소를 입력해주세요.", getString(R.string.confirm));
                        return;
                    }
                    if (etIpAddress2.getText().toString().trim().isEmpty()) {
                        showWarningDialog("IP 주소를 입력해주세요.", getString(R.string.confirm));
                        return;
                    }
                    if (etIpAddress3.getText().toString().trim().isEmpty()) {
                        showWarningDialog("IP 주소를 입력해주세요.", getString(R.string.confirm));
                        return;
                    }
                    if (etIpAddress4.getText().toString().trim().isEmpty()) {
                        showWarningDialog("IP 주소를 입력해주세요.", getString(R.string.confirm));
                        return;
                    }
                    addContactItem();

                } else if (STATUS_CODE == STATUS_DELETE_CONTACT) {
                    if (deleteItem != null) {
                        deleteContactItem();
                    } else {
                        STATUS_CODE = 0;
                        getFacilityContactList();
                    }
                } else if (STATUS_CODE == STATUS_LOAD_ALL_CONTACT) {
                    loadAllContactList();
                } else {
                    getFacilityContactList();

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

    private void loadAllContactList() {
        showProgress();

        if (mPrefGlobal.getAllDeviceList() != null) {
            if (!mPrefGlobal.getAllDeviceList().isEmpty()) {

                List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo> list = new ArrayList<>();

                for (int i = 0; i < mPrefGlobal.getAllDeviceList().size(); i++) {
                    RequestDataFormat.DeviceNetworkBody item = mPrefGlobal.getAllDeviceList().get(i);

                    if (item.getIpAddress().equals(Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceNetworkBody().getIpAddress())) {
                        continue;
                    }
                    ResponseDataFormat.FacilityContactListBody.FacilityContactInfo facilityContactInfo =
                            new ResponseDataFormat.FacilityContactListBody.FacilityContactInfo(i, item.getFacilityName(), item.getIpAddress(), item.getSerialCode());
                    showProgress();
                    if (i == mPrefGlobal.getAllDeviceList().size() - 1) {
                        addContactItem(facilityContactInfo, true);
                    } else {
                        addContactItem(facilityContactInfo, false);
                    }
                    list.add(facilityContactInfo);
                }

                mPrefGlobal.setContactsList(list);
                getFacilityContactList();

            } else {

            }
        }
        closeProgress();
    }

    private void addContactItem(ResponseDataFormat.FacilityContactListBody.FacilityContactInfo facilityContactInfo, boolean finalIndex) {

        Retrofit.AddFacilityContactApi facilityContactApi = Retrofit.AddFacilityContactApi.retrofit.create(Retrofit.AddFacilityContactApi.class);
        Call<ResponseDataFormat.FacilityContactListBody> call = facilityContactApi.addContactPost(mPrefGlobal.getAuthorization(),
                new RequestDataFormat.ContactBody(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode(), Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode()
                        , facilityContactInfo.getFacilityName(), facilityContactInfo.getFacilityIPAddress()));

        call.enqueue(new Callback<ResponseDataFormat.FacilityContactListBody>() {

            @Override
            public void onResponse(Call<ResponseDataFormat.FacilityContactListBody> call, Response<ResponseDataFormat.FacilityContactListBody> response) {
                if (finalIndex) {
                    closeProgress();
                }
                hideNavigationBar();
                if (response.body() != null) {
                    if (response.body().getResult().equals("OK")) {

                    }
                } else if (response.errorBody() != null) {
                    String error = "";
                    try {
                        error = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showWarningDialog(ErrorBodyParser.JsonParser(error)[ErrorBodyParser.ERROR_MESSAGE_NUM].replace("\"", ""), getString(R.string.confirm));

                } else {
                    STATUS_CODE = 0;

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

    private void addContactItem() {
        String facilityName = etFacilityName.getText().toString().trim();
        String ipAddress = etIpAddress1.getText().toString().trim() + etIpAddress2.getText().toString().trim() + etIpAddress3.getText().toString().trim() + etIpAddress4.getText().toString().trim();

        RequestDataFormat.ContactBody contactBody = null;

        if (!mPrefGlobal.getAllDeviceList().isEmpty()) {
            for (RequestDataFormat.DeviceNetworkBody item : mPrefGlobal.getAllDeviceList()) {
                if (ipAddress.equals(item.getIpAddress())) {
                    contactBody = new RequestDataFormat.ContactBody(
                            Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode(), Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode(), facilityName, ipAddress);
                    contactBody.setConnSerialCode(item.getSerialCode());
                    break;
                }
            }
        }

        if (contactBody == null) {
            showWarningDialog("해당 ip 주소와 일치하는\n시설이 존재하지 않습니다.", getString(R.string.confirm));
            return;
        }
        showProgress();

        Retrofit.AddFacilityContactApi facilityContactApi = Retrofit.AddFacilityContactApi.retrofit.create(Retrofit.AddFacilityContactApi.class);
        Call<ResponseDataFormat.FacilityContactListBody> call = facilityContactApi.addContactPost(mPrefGlobal.getAuthorization(), contactBody);
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
                        STATUS_CODE = 0;
                        //여기에 setSerialCode 추가!!!!!
                        showPopupDialog(facilityName, "연락처가 추가 되었습니다.", getString(R.string.confirm));
                    } else {
                        STATUS_CODE = 0;

                        showWarningDialog(response.body().getMessage(), getString(R.string.confirm));
                    }
                } else if (response.errorBody() != null) {
                    String error = "";
                    try {
                        error = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showWarningDialog(ErrorBodyParser.JsonParser(error)[ErrorBodyParser.ERROR_MESSAGE_NUM].replace("\"", ""), getString(R.string.confirm));

                } else {
                    STATUS_CODE = 0;

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
        Call<ResponseDataFormat.FacilityContactListBody> call = facilityContactApi.deleteContactPost(mPrefGlobal.getAuthorization()
                , new RequestDataFormat.ContactBody(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode(), Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode()
                        , deleteItem.getFacilityName(), deleteItem.getFacilityIPAddress()));

        call.enqueue(new Callback<ResponseDataFormat.FacilityContactListBody>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.FacilityContactListBody> call, Response<ResponseDataFormat.FacilityContactListBody> response) {
                closeProgress();
                hideNavigationBar();
                if (response.body() != null) {
                    if (response.body().getResult().equals("OK")) {
                        String facilityName = deleteItem.getFacilityName();

                        for (ResponseDataFormat.FacilityContactListBody.FacilityContactInfo item : mPrefGlobal.getContactsList()) {
                            if (item.getFacilityIPAddress().equals(deleteItem.getFacilityIPAddress())) {
                                mPrefGlobal.getContactsList().remove(item);
                                mPrefGlobal.setContactsList(mPrefGlobal.getContactsList());
                                break;
                            }
                        }
                        deleteItem = null;
                        if (currentContactList.size() == 1 && currentPageNumber > 1) {
                            currentPageNumber--;
                        }

                        showPopupDialog(facilityName, "연락처가 삭제 되었습니다.", getString(R.string.confirm));

                    } else {
                        STATUS_CODE = 0;

                        showWarningDialog(response.body().getMessage(), getString(R.string.confirm));
                        //code
                    }
                } else if (response.errorBody() != null) {
                    String error = "";
                    try {
                        error = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showWarningDialog(ErrorBodyParser.JsonParser(error)[ErrorBodyParser.ERROR_MESSAGE_NUM].replace("\"", ""), getString(R.string.confirm));

                } else {
                    STATUS_CODE = 0;

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

        Call<ResponseDataFormat.FacilityContactListBody> call = facilityContactApi.loadContactListPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.ContactBody(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode(), Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode(), currentPageNumber));

        call.enqueue(new Callback<ResponseDataFormat.FacilityContactListBody>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.FacilityContactListBody> call, Response<ResponseDataFormat.FacilityContactListBody> response) {
                closeProgress();
                etFacilityName.setFocusableInTouchMode(true);
                etFacilityName.setFocusable(true);
                etIpAddress1.setFocusableInTouchMode(true);
                etIpAddress1.setFocusable(true);
                etIpAddress2.setFocusableInTouchMode(true);
                etIpAddress2.setFocusable(true);
                etIpAddress3.setFocusableInTouchMode(true);
                etIpAddress3.setFocusable(true);
                etIpAddress4.setFocusableInTouchMode(true);
                etIpAddress4.setFocusable(true);

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
                etIpAddress1.setFocusableInTouchMode(true);
                etIpAddress1.setFocusable(true);
                etIpAddress2.setFocusableInTouchMode(true);
                etIpAddress2.setFocusable(true);
                etIpAddress3.setFocusableInTouchMode(true);
                etIpAddress3.setFocusable(true);
                etIpAddress4.setFocusableInTouchMode(true);
                etIpAddress4.setFocusable(true);
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