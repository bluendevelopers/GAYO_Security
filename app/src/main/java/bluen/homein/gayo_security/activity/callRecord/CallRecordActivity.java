package bluen.homein.gayo_security.activity.callRecord;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    @BindView(R.id.tv_select_type)
    TextView tvSelectedType;
    @BindView(R.id.tv_select_start_date)
    TextView tvSelectedStartDate;
    @BindView(R.id.tv_select_end_date)
    TextView tvSelectedEndDate;

    private View dialogView;
//    private Calendar today = Calendar.getInstance();
//    private int year = today.get(Calendar.YEAR);
//    private int month = today.get(Calendar.MONTH);
//    private int day = today.get(Calendar.DAY_OF_MONTH);

    private CallRecordListAdapter callRecordListAdapter;
    private PageNumberListAdapter pageNumberListAdapter;
    private int currentPageNumber = 1; //default
    private String startDate = ""; //default
    private String endDate = ""; //default
    private List<String> pageList;
    private List<ResponseDataFormat.EtcType> callTypeList;
    private int selectedTypeIndex = 0; //default
    boolean mIsDelete = false; //default
    private List<ResponseDataFormat.CallRecordListBody.CallRecordInfo> currentCallRecordList;
    ResponseDataFormat.CallRecordListBody.CallRecordInfo deleteItem = null;

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
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        finish();
    }

    @OnClick(R.id.btn_search)
    void clickBtnSearch() {
        showProgress();
        getCallRecordList();
    }

    @OnClick(R.id.btn_delete)
    void clickBtnDeleteAll() {
        if (currentCallRecordList != null && !currentCallRecordList.isEmpty()) {
            //code

        }
    }

    @OnClick(R.id.lay_select_type)
    void selectType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(CallRecordActivity.this);
        dialogView = inflater.inflate(R.layout.dialog_select_base, null);
        builder.setView(dialogView);

        ImageView ivCloseBtn = dialogView.findViewById(R.id.iv_close_btn);
        Button btnComplete = dialogView.findViewById(R.id.btn_confirm);
        NumberPicker numberPicker = dialogView.findViewById(R.id.numberPicker);

        if (callTypeList != null && !callTypeList.isEmpty()) {
            String[] codeNames = new String[callTypeList.size()];
            for (int i = 0; i < callTypeList.size(); i++) {
                codeNames[i] = callTypeList.get(i).getCodeName();
            }

            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(codeNames.length - 1);
            numberPicker.setDisplayedValues(codeNames);
            numberPicker.setValue(selectedTypeIndex);
        }

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |       // 네비게이션바 숨김
                        View.SYSTEM_UI_FLAG_FULLSCREEN |            // 상태바 숨김
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY         // 몰입 모드 유지
        );
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);

        btnComplete.setOnClickListener(v -> {
            int selectedIndex = numberPicker.getValue();
            String selectedCodeName = callTypeList.get(selectedIndex).getCodeName();
            selectedTypeIndex = selectedIndex;
            tvSelectedType.setText(selectedCodeName);
            dialog.dismiss();
        });

        ivCloseBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    @OnClick(R.id.lay_select_start_date)
    void selectStartDate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(CallRecordActivity.this);
        dialogView = inflater.inflate(R.layout.dialog_select_date_base, null);
        builder.setView(dialogView);

        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);

        ImageView ivCloseBtn = dialogView.findViewById(R.id.iv_close_btn);
        Button btnComplete = dialogView.findViewById(R.id.btn_confirm);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

        datePicker.init(year, month, day, null);
        // selectedDate = String.format("%04d. %02d. %02d", year, month + 1, day);

        final String[] selectedTime = new String[1];

        selectedTime[0] = String.format("%04d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                //selectedDate = String.format("%04d. %02d. %02d", selectedYear, selectedMonth + 1, selectedDay);

                selectedTime[0] = String.format("%04d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |       // 네비게이션바 숨김
                        View.SYSTEM_UI_FLAG_FULLSCREEN |            // 상태바 숨김
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY         // 몰입 모드 유지
        );
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);

        btnComplete.setOnClickListener(v -> {

            if (selectedTime[0] != null && !selectedTime[0].equals("")) {
                String startDateStr = selectedTime[0];

                tvSelectedStartDate.setText(startDateStr);
                startDate = startDateStr;

                String endDateStr = tvSelectedEndDate.getText().toString();

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                    Date _startDate = sdf.parse(startDateStr);
                    Date _endDate = sdf.parse(endDateStr);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(_startDate);

                    if (_startDate != null && _endDate != null && _startDate.after(_endDate)) {
                        // 시작 날짜가 종료 날짜 보다 미래인 경우 같은 날로 설정
                        tvSelectedEndDate.setText(sdf.format(calendar.getTime()));
                        endDate = startDateStr;
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                    // 날짜 파싱 오류 처리
                }
            }

            dialog.dismiss();

        });

        ivCloseBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    @OnClick(R.id.lay_select_end_date)
    void selectEndDate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(CallRecordActivity.this);
        dialogView = inflater.inflate(R.layout.dialog_select_date_base, null);
        builder.setView(dialogView);
        Calendar today = Calendar.getInstance();

        ImageView ivCloseBtn = dialogView.findViewById(R.id.iv_close_btn);
        Button btnComplete = dialogView.findViewById(R.id.btn_confirm);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

        String startDateStr = tvSelectedStartDate.getText().toString();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            Date startDate = sdf.parse(startDateStr);
            if (startDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);

                calendar.add(Calendar.DAY_OF_MONTH, 0);

                datePicker.setMinDate(calendar.getTimeInMillis());
                datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);

            }
        } catch (ParseException e) {
            e.printStackTrace();
            datePicker.setMinDate(today.getTimeInMillis());
            Calendar calendar = Calendar.getInstance();
            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);

        }

        final String[] selectedTime = new String[1];

        selectedTime[0] = String.format("%04d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                //selectedDate = String.format("%04d. %02d. %02d", selectedYear, selectedMonth + 1, selectedDay);

                selectedTime[0] = String.format("%04d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |       // 네비게이션바 숨김
                        View.SYSTEM_UI_FLAG_FULLSCREEN |            // 상태바 숨김
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY         // 몰입 모드 유지
        );
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedTime[0] != null && !selectedTime[0].equals("")) {
                    tvSelectedEndDate.setText(selectedTime[0]);
                }

                String startDateStr = tvSelectedStartDate.getText().toString();
                String endDateStr = tvSelectedEndDate.getText().toString();

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                    Date _startDate = sdf.parse(startDateStr);
                    Date _endDate = sdf.parse(endDateStr);

                    if (_startDate != null && _endDate != null && (_startDate.equals(_endDate) || _startDate.after(_endDate))) {
                        // 시작 날짜가 종료 날짜와 같거나 더 미래인 경우
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(_startDate);
                        calendar.add(Calendar.DAY_OF_MONTH, 0);

                        String newEndDateStr = sdf.format(calendar.getTime());

                        // tvSelectEndDate 업데이트
                        tvSelectedEndDate.setText(newEndDateStr);
                        endDate = tvSelectedEndDate.getText().toString();

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    // 날짜 파싱 오류 처리
                }

                dialog.dismiss();

            }
        });

        ivCloseBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();


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
        popupDialog.onCallBack(new PopupDialog.DialogCallback() {
            @Override
            public void onFinish() {
                if (mIsDelete) {
                    mIsDelete = false;
                    if (deleteItem == null) {
                        getCallRecordList();
                    } else {
                        deleteItem = null;
                    }
                }
            }

            @Override
            public void onNextStep() {
                if (mIsDelete) {
                    if (deleteItem != null) {
                        deleteCallRecordItem();
                    }
                }
            }
        });
        pageNumberListAdapter = new PageNumberListAdapter(CallRecordActivity.this, R.layout.item_page_number, new PageNumberListAdapter.OnPageNumberClickListener() {
            @Override
            public void clickPageNumber(int pageNumber) {
                currentPageNumber = pageNumber;
                getCallRecordList();
            }
        });

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

    private void deleteCallRecordItem() {
        showProgress();
        Retrofit.CallRecordApi callRecordApi = Retrofit.CallRecordApi.retrofit.create(Retrofit.CallRecordApi.class);
        Call<ResponseDataFormat.CallRecordListBody> call = callRecordApi.deleteCallRecordPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.CallRecordListBody(serialCode, buildingCode, deleteItem.getCallSeq()));

        call.enqueue(new Callback<ResponseDataFormat.CallRecordListBody>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.CallRecordListBody> call, Response<ResponseDataFormat.CallRecordListBody> response) {
                closeProgress();
                if (response.body() != null) {
                    if (response.body().getResult().equals("OK")) {
                        String facilityName = deleteItem.getCallLocName();
                        String[] date = deleteItem.getStartDate().split(" ");
                        deleteItem = null;
                        if (currentCallRecordList.size() == 1 && currentPageNumber > 1) {
                            currentPageNumber--;
                        }
                        showPopupDialog(facilityName + "\n" + date[0] + "\n", "통화 목록이 삭제 되었습니다.", getString(R.string.confirm));
                    } else {
                        mIsDelete = false;
                        showWarningDialog(response.body().getMessage(), getString(R.string.confirm));
                    }
                } else {
                    mIsDelete = false;
                    //code
                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.CallRecordListBody> call, Throwable t) {
                closeProgress();
                hideNavigationBar();
                //code
            }
        });
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
                //code

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
                    if (response.body().getMessage() == null) {
                        currentCallRecordList = response.body().getCallRecordList();
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
                        if (currentPageNumber == response.body().getPageCountInfo().getTotalPageCnt()) {
                            layNextBtn.setVisibility(View.INVISIBLE);
                        }

                        callRecordListAdapter.setData(response.body().getCallRecordList());
                        pageNumberListAdapter.setItems(pageList);
                        pageNumberListAdapter.setPageNumber(currentPageNumber);
                        rvPageNumber.setAdapter(pageNumberListAdapter);
                    } else {
                        //code
                    }
                } else {
                    //code

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.CallRecordListBody> call, Throwable t) {
                closeProgress();
                hideNavigationBar();
                //code

            }
        });
    }


}