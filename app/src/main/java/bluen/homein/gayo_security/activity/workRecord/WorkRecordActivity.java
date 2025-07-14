package bluen.homein.gayo_security.activity.workRecord;

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

import androidx.recyclerview.widget.DividerItemDecoration;
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

public class WorkRecordActivity extends BaseActivity {
    @BindView(R.id.lay_before_btn)
    LinearLayout layBeforeBtn;
    @BindView(R.id.lay_next_btn)
    LinearLayout layNextBtn;

    @BindView(R.id.rv_page_number)
    RecyclerView rvPageNumber;
    @BindView(R.id.lv_work_record)
    ListView lvWorkRecord;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    @BindView(R.id.tv_select_type)
    TextView tvSelectedType;
    @BindView(R.id.tv_select_start_date)
    TextView tvSelectedStartDate;
    @BindView(R.id.tv_select_end_date)
    TextView tvSelectedEndDate;
    @BindView(R.id.tv_selected_worker_phone_number)
    TextView tvSelectedWorkerPhoneNumber;

    private View dialogView;
    private PageNumberListAdapter pageNumberListAdapter;
    private WorkRecordListAdapter workRecordListAdapter;
    private int currentPageNumber = 1; //default
    private String startDate = ""; //default
    private String endDate = ""; //default
    private int selectedTypeIndex = 0; //default
    private int selectedNumberIndex = 0; //default
    private List<String> pageList;
    private List<ResponseDataFormat.EtcType> workRecordTypeList;
    private List<ResponseDataFormat.WorkerListBody.WorkerInfo> workerPhoneNumberList;


    @OnClick(R.id.lay_before_btn)
    void clickBeforeBtn() {
        currentPageNumber--;
        getWorkRecordList();
    }

    @OnClick(R.id.lay_next_btn)
    void clickNextBtn() {
        currentPageNumber++;
        getWorkRecordList();
    }

    @OnClick(R.id.btn_search)
    void clickBtnSearch() {
        showProgress();
        getWorkRecordList();
    }

    @OnClick(R.id.lay_select_type)
    void selectType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(WorkRecordActivity.this);
        dialogView = inflater.inflate(R.layout.dialog_select_base, null);
        builder.setView(dialogView);

        ImageView ivCloseBtn = dialogView.findViewById(R.id.iv_close_btn);
        Button btnComplete = dialogView.findViewById(R.id.btn_confirm);
        NumberPicker numberPicker = dialogView.findViewById(R.id.numberPicker);

        if (workRecordTypeList != null && !workRecordTypeList.isEmpty()) {
            String[] codeNames = new String[workRecordTypeList.size()];
            for (int i = 0; i < workRecordTypeList.size(); i++) {
                codeNames[i] = workRecordTypeList.get(i).getCodeName();
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
            String selectedCodeName = workRecordTypeList.get(selectedIndex).getCodeName();
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
        LayoutInflater inflater = LayoutInflater.from(WorkRecordActivity.this);
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
        LayoutInflater inflater = LayoutInflater.from(WorkRecordActivity.this);
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

    @OnClick(R.id.lay_select_worker_phone_number)
    void selectWorkerPhoneNumber() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(WorkRecordActivity.this);
        dialogView = inflater.inflate(R.layout.dialog_select_base, null);
        builder.setView(dialogView);

        ImageView ivCloseBtn = dialogView.findViewById(R.id.iv_close_btn);
        Button btnComplete = dialogView.findViewById(R.id.btn_confirm);
        NumberPicker numberPicker = dialogView.findViewById(R.id.numberPicker);

        if (workerPhoneNumberList != null && !workerPhoneNumberList.isEmpty()) {
            String[] codeNames = new String[workerPhoneNumberList.size()];
            for (int i = 0; i < workerPhoneNumberList.size(); i++) {
                if (!workerPhoneNumberList.get(i).getManagerPhone().isEmpty()) {
                    codeNames[i] = workerPhoneNumberList.get(i).getManagerPhone();
                } else {
                    codeNames[i] = "전체";
                }
            }

            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(codeNames.length - 1);
            numberPicker.setDisplayedValues(codeNames);
            numberPicker.setValue(selectedNumberIndex);
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
            String selectedCodeName = workerPhoneNumberList.get(selectedIndex).getManagerPhone();
            selectedNumberIndex = selectedIndex;
            if (!selectedCodeName.isEmpty()) {
                tvSelectedWorkerPhoneNumber.setText(selectedCodeName);
            } else {
                tvSelectedWorkerPhoneNumber.setText("전체");
            }
            dialog.dismiss();
        });

        ivCloseBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }


    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_work_record;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        hideNavigationBar();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, RecyclerView.HORIZONTAL);
        pageNumberListAdapter = new PageNumberListAdapter(WorkRecordActivity.this, R.layout.item_page_number, new PageNumberListAdapter.OnPageNumberClickListener() {
            @Override
            public void clickPageNumber(int pageNumber) {
                currentPageNumber = pageNumber;
                getWorkRecordList();
            }
        });

        // 최초 진입 시 당일 전체로 검색 api 실행

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        startDate = formattedDate;
        endDate = formattedDate;
        tvSelectedStartDate.setText(startDate);
        tvSelectedEndDate.setText(endDate);

        workRecordListAdapter = new WorkRecordListAdapter(WorkRecordActivity.this);
        lvWorkRecord.setAdapter(workRecordListAdapter);
        lvWorkRecord.setEmptyView(tvEmptyView);

        getTypeList();
    }

    private void getTypeList() {
        showProgress();
        Retrofit.WorkRecordApi workRecordApi = Retrofit.WorkRecordApi.retrofit.create(Retrofit.WorkRecordApi.class);
        Call<List<ResponseDataFormat.EtcType>> call = workRecordApi.loadWorkTypeListGet(mPrefGlobal.getAuthorization());

        call.enqueue(new Callback<List<ResponseDataFormat.EtcType>>() {
            @Override
            public void onResponse(Call<List<ResponseDataFormat.EtcType>> call, Response<List<ResponseDataFormat.EtcType>> response) {
                if (response.body() != null) {
                    workRecordTypeList = response.body();
                    getWorkerPhoneNumberList();
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

    private void getWorkerPhoneNumberList() {
        Retrofit.WorkRecordApi workRecordApi = Retrofit.WorkRecordApi.retrofit.create(Retrofit.WorkRecordApi.class);
        Call<List<ResponseDataFormat.WorkerListBody.WorkerInfo>> call = workRecordApi.workerPhoneNumberListPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.WorkerBody(Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode()));

        call.enqueue(new Callback<List<ResponseDataFormat.WorkerListBody.WorkerInfo>>() {
            @Override
            public void onResponse(Call<List<ResponseDataFormat.WorkerListBody.WorkerInfo>> call, Response<List<ResponseDataFormat.WorkerListBody.WorkerInfo>> response) {
                if (response.body() != null) {
                    if (workerPhoneNumberList == null) {
                        workerPhoneNumberList = new ArrayList<>();
                        workerPhoneNumberList.add(new ResponseDataFormat.WorkerListBody.WorkerInfo(""));
                        workerPhoneNumberList.addAll(response.body());
                        getWorkRecordList();
                    }
                } else {
                    closeProgress();

                }
            }

            @Override
            public void onFailure(Call<List<ResponseDataFormat.WorkerListBody.WorkerInfo>> call, Throwable t) {
                closeProgress();

            }
        });


    }

    private void getWorkRecordList() {
        showProgress();
        Retrofit.WorkRecordApi workRecordApi = Retrofit.WorkRecordApi.retrofit.create(Retrofit.WorkRecordApi.class);

        RequestDataFormat.WorkRecordListBody workRecordListBody =
                new RequestDataFormat.WorkRecordListBody(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode(), Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode(), currentPageNumber, startDate, endDate, workerPhoneNumberList.get(selectedNumberIndex).getManagerPhone(), workRecordTypeList.get(selectedTypeIndex).getCodeNumber());
        Call<ResponseDataFormat.WorkRecordListBody> call = workRecordApi.workRecordListPost(mPrefGlobal.getAuthorization(), workRecordListBody); // test
        call.enqueue(new Callback<ResponseDataFormat.WorkRecordListBody>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.WorkRecordListBody> call, Response<ResponseDataFormat.WorkRecordListBody> response) {
                closeProgress();
                hideNavigationBar();
                if (response.body() != null) {
                    if (response.body().getMessage() == null) {
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

                        if (currentPageNumber <= 1) {
                            currentPageNumber = 1;
                            layBeforeBtn.setVisibility(View.INVISIBLE);
                        }
                        if (currentPageNumber >= response.body().getPageCountInfo().getTotalPageCnt()) {
                            currentPageNumber = response.body().getPageCountInfo().getTotalPageCnt();
                            layNextBtn.setVisibility(View.INVISIBLE);
                        }

                        workRecordListAdapter.setData(response.body().getWorkRecordList());


                        pageNumberListAdapter.setItems(pageList);
                        pageNumberListAdapter.setPageNumber(currentPageNumber);
                        rvPageNumber.setAdapter(pageNumberListAdapter);
                    } else {
//                    showPopupDialog("");
                    }
                } else {

                }

            }

            @Override
            public void onFailure(Call<ResponseDataFormat.WorkRecordListBody> call, Throwable t) {
                closeProgress();
                hideNavigationBar();

            }
        });

    }

//    private void createSelectDatePopup(TextView _tvSelectDate, int statusNumber) {
//        LayoutInflater inflater = LayoutInflater.from(WorkRecordActivity.this);
//
//        //popup
//        dialogView = inflater.inflate(R.layout.dialog_select_date_base, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(WorkRecordActivity.this);
//        builder.setView(dialogView);
//        AlertDialog dialog = builder.create();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        dialog.show();
//
//        Window window = dialog.getWindow();
//        if (window != null) {
//            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//            layoutParams.copyFrom(window.getAttributes());
//
//            layoutParams.gravity = Gravity.BOTTOM;
//            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//
//            window.setAttributes(layoutParams);
//        }
//
//        Button btnComplete = dialogView.findViewById(R.id.btn_confirm);
//        ImageView ivCloseBtn = dialogView.findViewById(R.id.iv_close_btn);
//        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
//
//        // DatePicker 최소 날짜를 오늘 날짜로 설정
//        if (statusNumber == STATUS_ONE_DAY) {
//            datePicker.setMinDate(today.getTimeInMillis());
//            tvPopupTitle.setText("키 발급 날짜를 선택하세요");
//        } else if (statusNumber == STATUS_START_DATE) {
//            tvPopupTitle.setText("키 발급 시작 날짜를 선택하세요");
//            datePicker.setMinDate(today.getTimeInMillis());
//
//
//        } else {
//            tvPopupTitle.setText("키 발급 종료 날짜를 선택하세요");
//            String startDateStr = tvSelectStartDate.getText().toString();
//            try {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//                Date startDate = sdf.parse(startDateStr);
//                if (startDate != null) {
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTime(startDate);
//
//                    calendar.add(Calendar.DAY_OF_MONTH, 1);
//
//                    datePicker.setMinDate(calendar.getTimeInMillis());
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//                datePicker.setMinDate(today.getTimeInMillis());
//            }
//        }
//        datePicker.init(year, month, day, null);
//        // selectedDate = String.format("%04d. %02d. %02d", year, month + 1, day);
//
//        final String[] selectedTime = new String[1];
//
//        selectedTime[0] = String.format("%04d. %02d. %02d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
//
//        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
//            @Override
//            public void onDateChanged(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
//                //selectedDate = String.format("%04d. %02d. %02d", selectedYear, selectedMonth + 1, selectedDay);
//
//                selectedTime[0] = String.format("%04d. %02d. %02d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
//            }
//        });
//
//        // 클릭 이벤트 처리
//        btnComplete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (selectedTime[0] != null && !selectedTime[0].equals("")) {
//                    _tvSelectDate.setText(selectedTime[0]);
//                    year = datePicker.getYear();
//                    month = datePicker.getMonth();
//                    day = datePicker.getDayOfMonth();
//                }
//
//                if (statusNumber == STATUS_ONE_DAY) {
//                    try {
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//                        Date date = sdf.parse(selectedTime[0]);
//                        if (date != null) {
//                            Calendar calendar = Calendar.getInstance();
//                            calendar.setTime(date);
//                            setWeekDay(calendar);
//                        }
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                } else if (statusNumber == STATUS_START_DATE && !tvSelectEndDate.getText().toString().isEmpty()) {
//                    String startDateStr = tvSelectStartDate.getText().toString();
//                    String endDateStr = tvSelectEndDate.getText().toString();
//
//                    try {
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//                        Date startDate = sdf.parse(startDateStr);
//                        Date endDate = sdf.parse(endDateStr);
//
//                        if (startDate != null && endDate != null && (startDate.equals(endDate) || startDate.after(endDate))) {
//                            // 시작 날짜가 종료 날짜와 같거나 더 미래인 경우
//                            Calendar calendar = Calendar.getInstance();
//                            calendar.setTime(startDate);
//                            calendar.add(Calendar.DAY_OF_MONTH, 1); // 다음 날로 설정
//
//                            String newEndDateStr = sdf.format(calendar.getTime());
//
//                            // tvSelectEndDate 업데이트
//                            tvSelectEndDate.setText(newEndDateStr);
//                        }
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                        // 날짜 파싱 오류 처리
//                    }
//                }
//
//                dialog.dismiss();
//
//            }
//        });
//        ivCloseBtn.setOnClickListener(v -> {
//            dialog.dismiss();
//        });
//
//    }

}