package bluen.homein.gayo_security.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.rest.ResponseDataFormat;


public class WorkRecordListAdapter extends BaseAdapter {

    public List<ResponseDataFormat.WorkRecordListBody.WorkRecordInfo> itemsList;
    private final Context context;

    public WorkRecordListAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {

        return itemsList != null ? itemsList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        Object item = itemsList != null ? itemsList.get(i) : null;
        return item;
    }

    @Override
    public long getItemId(int i) {
        long itemId = itemsList != null ? i : 0;
        return itemId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_work_record, parent, false);
        }
        TextView tvDivision = view.findViewById(R.id.tv_division);
        TextView tvDate = view.findViewById(R.id.tv_date);
        TextView tvWorkerPhoneNumber = view.findViewById(R.id.tv_worker_phone_number);
        TextView tvWorkStartTime = view.findViewById(R.id.tv_work_start_time);
        TextView tvWorkEndTime = view.findViewById(R.id.tv_work_end_time);

        tvDivision.setText(itemsList.get(position).getWorkDivision());
        tvDate.setText(itemsList.get(position).getWorkDate());
        tvWorkStartTime.setText(itemsList.get(position).getWorkStartTime());
        tvWorkEndTime.setText(itemsList.get(position).getWorkEndTime());
        tvWorkerPhoneNumber.setText(itemsList.get(position).getWorkerPhone());

        if (position % 2 == 0){ // 순서가 짝수면 두껍게
            tvDivision.setTypeface(tvWorkStartTime.getTypeface(), Typeface.BOLD);
            tvDate.setTypeface(tvWorkStartTime.getTypeface(), Typeface.BOLD);
            tvWorkStartTime.setTypeface(tvWorkStartTime.getTypeface(), Typeface.BOLD);
            tvWorkEndTime.setTypeface(tvWorkStartTime.getTypeface(), Typeface.BOLD);
            tvWorkerPhoneNumber.setTypeface(tvWorkStartTime.getTypeface(), Typeface.BOLD);
        }

        return view;
    }

    public void setData(List<ResponseDataFormat.WorkRecordListBody.WorkRecordInfo> newData) {
        this.itemsList = newData;
        notifyDataSetChanged();
    }

    private String changeDateFormat(String dateStr) throws ParseException {
        // 원래 날짜 형식
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 변환할 날짜 형식
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy. MM. dd");

        // 문자열을 Date 객체로 파싱
        Date date = originalFormat.parse(dateStr);
        // Date 객체를 새로운 형식의 문자열로 포맷
        return targetFormat.format(date);
    }
}