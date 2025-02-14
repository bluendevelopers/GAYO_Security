package bluen.homein.gayo_security;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import bluen.homein.gayo_security.adapter.PageNumberListAdapter;
import bluen.homein.gayo_security.adapter.WorkRecordListAdapter;
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
    @BindView(R.id.tv_selected_start_date)
    TextView tvSelectedStartDate;
    @BindView(R.id.tv_selected_end_date)
    TextView tvSelectedEndDate;

    private View dialogView;
    private PageNumberListAdapter pageNumberListAdapter;
    private WorkRecordListAdapter workRecordListAdapter;
    private int currentPageNumber = 1; //default
    private String startDate = ""; //default
    private String endDate = ""; //default
    private String workerPhoneNumber = ""; //default
    private String division = "00"; //default
    private List<String> pageList;
    private List<ResponseDataFormat.WorkRecordListBody.WorkRecordInfo> workerRecordList;

    @OnClick(R.id.lay_before_btn)
    void clickBeforeBtn() {
    }

    @OnClick(R.id.lay_next_btn)
    void clickNextBtn() {
    }

    @OnClick(R.id.lay_start_date_btn)
    void showWeekdaySelectionPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(WorkRecordActivity.this);
        dialogView = inflater.inflate(R.layout.dialog_select_date_base, null);
        builder.setView(dialogView);

        ImageView ivCloseBtn = dialogView.findViewById(R.id.iv_close_btn);
        Button btnComplete = dialogView.findViewById(R.id.btn_confirm);

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
        return R.layout.activity_work_record;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        hideNavigationBar();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, RecyclerView.HORIZONTAL);
        pageNumberListAdapter = new PageNumberListAdapter(WorkRecordActivity.this, R.layout.item_page_number);

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

        getDivisionList();
        getWorkRecordList();
    }

    private void getDivisionList() {

    }

    private void getWorkRecordList() {
        showProgress();

        Retrofit.WorkRecordApi workRecordApi = Retrofit.WorkRecordApi.retrofit.create(Retrofit.WorkRecordApi.class);

        Call<ResponseDataFormat.WorkRecordListBody> call = workRecordApi.workRecordListPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.WorkRecordListBody(serialCode, buildingCode, currentPageNumber, startDate, endDate, workerPhoneNumber, division)); // test
        call.enqueue(new Callback<ResponseDataFormat.WorkRecordListBody>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.WorkRecordListBody> call, Response<ResponseDataFormat.WorkRecordListBody> response) {
                closeProgress();
                hideNavigationBar();

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

                    workRecordListAdapter.setData(response.body().getWorkRecordList());


                    pageNumberListAdapter.setItems(pageList);
                    pageNumberListAdapter.setData(currentPageNumber);
                    rvPageNumber.setAdapter(pageNumberListAdapter);
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
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MM. dd", Locale.KOREA);
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
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MM. dd", Locale.KOREA);
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
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MM. dd", Locale.KOREA);
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