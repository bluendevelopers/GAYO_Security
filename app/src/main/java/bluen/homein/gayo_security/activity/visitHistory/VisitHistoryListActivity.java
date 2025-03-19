package bluen.homein.gayo_security.activity.visitHistory;

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
    @BindView(R.id.tv_select_type)
    TextView tvSelectedType;
    @BindView(R.id.tv_select_start_date)
    TextView tvSelectedStartDate;
    @BindView(R.id.tv_select_end_date)
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

    @OnClick(R.id.btn_search)
    void clickBtnSearch() {
        showProgress();
        getVisitHistoryList();
    }

    @OnClick(R.id.btn_delete)
    void clickBtnDeleteAll() {
        if (currentVisitHistoryList != null && !currentVisitHistoryList.isEmpty()) {
            //code

        }
    }

    @OnClick(R.id.lay_select_type)
    void selectType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(VisitHistoryListActivity.this);
        dialogView = inflater.inflate(R.layout.dialog_select_base, null);
        builder.setView(dialogView);

        ImageView ivCloseBtn = dialogView.findViewById(R.id.iv_close_btn);
        Button btnComplete = dialogView.findViewById(R.id.btn_confirm);
        NumberPicker numberPicker = dialogView.findViewById(R.id.numberPicker);

        if (visitTypeList != null && !visitTypeList.isEmpty()) {
            String[] codeNames = new String[visitTypeList.size()];
            for (int i = 0; i < visitTypeList.size(); i++) {
                codeNames[i] = visitTypeList.get(i).getEntranceName();
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
            String selectedCodeName = visitTypeList.get(selectedIndex).getEntranceName();
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
        LayoutInflater inflater = LayoutInflater.from(VisitHistoryListActivity.this);
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
        LayoutInflater inflater = LayoutInflater.from(VisitHistoryListActivity.this);
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

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
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
    private View dialogView;
    boolean mIsDelete = false; //default
    private List<ResponseDataFormat.VisitHistoryListBody.VisitHistoryInfo> currentVisitHistoryList;
    ResponseDataFormat.VisitHistoryListBody.VisitHistoryInfo deleteItem = null;

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
        popupDialog.onCallBack(new PopupDialog.DialogCallback() {
            @Override
            public void onFinish() {
                if (mIsDelete) {
                    mIsDelete = false;
                    if (deleteItem == null) {
                        getVisitHistoryList();
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
        pageNumberListAdapter = new PageNumberListAdapter(VisitHistoryListActivity.this, R.layout.item_page_number, new PageNumberListAdapter.OnPageNumberClickListener() {
            @Override
            public void clickPageNumber(int pageNumber) {
                currentPageNumber = pageNumber;
                getVisitHistoryList();
            }
        });

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

    private void deleteCallRecordItem() {
        showProgress();
        Retrofit.VisitorHistoryApi visitorHistoryApi = Retrofit.VisitorHistoryApi.retrofit.create(Retrofit.VisitorHistoryApi.class);
        Call<ResponseDataFormat.VisitHistoryListBody> call = visitorHistoryApi.deleteCallRecordPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.VisitorRecordListBody(serialCode, buildingCode, deleteItem.getHisSeq()));
        call.enqueue(new Callback<ResponseDataFormat.VisitHistoryListBody>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.VisitHistoryListBody> call, Response<ResponseDataFormat.VisitHistoryListBody> response) {
                closeProgress();
                if (response.body() != null) {
                    if (response.body().getResult().equals("OK")) {
                        String visitType = deleteItem.getVisitType();
                        String visitDate = deleteItem.getVisitDate();
                        deleteItem = null;
                        if (currentVisitHistoryList.size() == 1 && currentPageNumber > 1) {
                            currentPageNumber--;
                        }
                        showPopupDialog(visitType + "\n" + visitDate + "\n", "방문자 목록이 삭제 되었습니다.", getString(R.string.confirm));
                    } else {
                        //code
                        mIsDelete = false;
                        showWarningDialog(response.body().getMessage(), getString(R.string.confirm));

                    }
                } else {
                    //code
                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.VisitHistoryListBody> call, Throwable t) {
                closeProgress();
                hideNavigationBar();
                //code
            }
        });
    }

    private void getVisitTypeList() {
        showProgress();
        Retrofit.VisitorHistoryApi visitorHistoryApi = Retrofit.VisitorHistoryApi.retrofit.create(Retrofit.VisitorHistoryApi.class);
        Call<List<ResponseDataFormat.VisitType>> call = visitorHistoryApi.loadVisitorTypeListGet(mPrefGlobal.getAuthorization());

        call.enqueue(new Callback<List<ResponseDataFormat.VisitType>>() {
            @Override
            public void onResponse(Call<List<ResponseDataFormat.VisitType>> call, Response<List<ResponseDataFormat.VisitType>> response) {
                hideNavigationBar();
                if (response.body() != null) {
                    visitTypeList = response.body();
                    getVisitHistoryList();

                } else {
                    closeProgress();
                    //code
                }
            }

            @Override
            public void onFailure(Call<List<ResponseDataFormat.VisitType>> call, Throwable t) {
                closeProgress();
                //code
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
                    if (response.body().getMessage() == null) {
                        currentVisitHistoryList = response.body().getVisitHistoryList();
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

                        visitHistoryListAdapter.setData(response.body().getVisitHistoryList());
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
            public void onFailure(Call<ResponseDataFormat.VisitHistoryListBody> call, Throwable t) {
                closeProgress();
                hideNavigationBar();
                //code
            }
        });
    }

}