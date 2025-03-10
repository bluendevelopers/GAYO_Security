package bluen.homein.gayo_security.activity.workRecord;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

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
        View vDivider = view.findViewById(R.id.v_divider);
        ConstraintLayout layRoot = view.findViewById(R.id.lay_root);
        TextView tvDivision = view.findViewById(R.id.tv_division);
        TextView tvDate = view.findViewById(R.id.tv_date);
        TextView tvWorkerPhoneNumber = view.findViewById(R.id.tv_worker_phone_number);
        TextView tvWorkStartTime = view.findViewById(R.id.tv_work_start_time);
        TextView tvWorkEndTime = view.findViewById(R.id.tv_work_end_time);

        tvDivision.setText(itemsList.get(position).getWorkType());
        tvDate.setText(itemsList.get(position).getWorkDate());
        tvWorkStartTime.setText(itemsList.get(position).getWorkStartTime());
        tvWorkEndTime.setText(itemsList.get(position).getWorkEndTime());
        tvWorkerPhoneNumber.setText(itemsList.get(position).getWorkerPhone());

        if (position % 2 == 0) {
            tvDivision.setTypeface(tvWorkStartTime.getTypeface(), Typeface.BOLD);
            tvDate.setTypeface(tvWorkStartTime.getTypeface(), Typeface.BOLD);
            tvWorkStartTime.setTypeface(tvWorkStartTime.getTypeface(), Typeface.BOLD);
            tvWorkEndTime.setTypeface(tvWorkStartTime.getTypeface(), Typeface.BOLD);
            tvWorkerPhoneNumber.setTypeface(tvWorkStartTime.getTypeface(), Typeface.BOLD);
        } else {
            layRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        if (itemsList.get(position).getWorkType().equals("순찰")) {
            tvDivision.setTextColor(Color.parseColor("#f1293d"));
        }

        if (!itemsList.isEmpty()) {
            if (position == itemsList.size() - 1) {
                vDivider.setVisibility(View.GONE);
            }
        }
        return view;
    }

    public void setData(List<ResponseDataFormat.WorkRecordListBody.WorkRecordInfo> newData) {
        this.itemsList = newData;
        notifyDataSetChanged();
    }

}