package bluen.homein.gayo_security.activity.callRecord;

import static org.webrtc.ContextUtils.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.call.CallActivity;
import bluen.homein.gayo_security.rest.ResponseDataFormat;


public class CallRecordListAdapter extends BaseAdapter {

    public List<ResponseDataFormat.CallRecordListBody.CallRecordInfo> itemsList;
    private final Context context;

    public CallRecordListAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {

        return itemsList != null ? itemsList.size() : 0;
    }

    @Override
    public ResponseDataFormat.CallRecordListBody.CallRecordInfo getItem(int i) {
        ResponseDataFormat.CallRecordListBody.CallRecordInfo item = itemsList != null ? itemsList.get(i) : null;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_call_record, parent, false);
        }
        ConstraintLayout layRoot = view.findViewById(R.id.lay_root);

        View vDivider = view.findViewById(R.id.v_divider);
        TextView tvDivision = view.findViewById(R.id.tv_division);
        TextView tvDate = view.findViewById(R.id.tv_date);
        TextView tvCallTime = view.findViewById(R.id.tv_call_time);
        TextView tvFacilityName = view.findViewById(R.id.tv_facility_name);

        LinearLayout layConnectBtn = view.findViewById(R.id.lay_connect_btn);
        LinearLayout layDeleteBtn = view.findViewById(R.id.lay_delete_btn);

        tvDivision.setText(itemsList.get(position).getCallType());
        tvDate.setText(itemsList.get(position).getStartDate());
        String strCallTime = itemsList.get(position).getCallMinute() + "분 " + itemsList.get(position).getCallSecond() + "초";
        tvCallTime.setText(strCallTime);
        tvFacilityName.setText(itemsList.get(position).getCallLocName());

        if (position % 2 == 0) {
            tvDivision.setTypeface(tvDivision.getTypeface(), Typeface.BOLD);
            tvDate.setTypeface(tvDate.getTypeface(), Typeface.BOLD);
            tvCallTime.setTypeface(tvCallTime.getTypeface(), Typeface.BOLD);
            tvFacilityName.setTypeface(tvFacilityName.getTypeface(), Typeface.BOLD);
        } else {
            layRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        if (!itemsList.isEmpty()) {
            if (position == itemsList.size() - 1) {
                vDivider.setVisibility(View.GONE);
            }
        }

        switch (itemsList.get(position).getCallType()) {
            case "발신":
                tvDivision.setTextColor(Color.parseColor("#f1293d"));
                break;
            case "부재중":
                tvDivision.setTextColor(Color.parseColor("#138235"));
                break;
            case "수신거부":
                tvDivision.setTextColor(Color.parseColor("#333333"));

                break;
        }

        layConnectBtn.setOnClickListener(v -> {
            //code
            //해당 연락처로 전화되도록
            Intent callIntent = new Intent(context, CallActivity.class);
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            String reCall = "reCall";
            callIntent.putExtra("reCall", getItem(position));
            //여기서 근데 세대호출인지, 기기간 전화인지 알아야할듯?
            getApplicationContext().startActivity(callIntent); // 가서 파싱
        });

        layDeleteBtn.setOnClickListener(v -> {
            //code
            ((CallRecordActivity) context).mIsDelete = true;
            ((CallRecordActivity) context).deleteItem = itemsList.get(position);
            String[] date = itemsList.get(position).getStartDate().split(" ");
            ((CallRecordActivity) context).showPopupDialog(itemsList.get(position).getCallLocName() + "\n" + date[0] + "\n", "통화 목록을 삭제 하시겠습니까?", context.getString(R.string.cancel), context.getString(R.string.confirm));
        });

        return view;
    }

    public void setData(List<ResponseDataFormat.CallRecordListBody.CallRecordInfo> newData) {
        this.itemsList = newData;
        notifyDataSetChanged();
    }

    private String changeDateFormat(String dateStr) throws ParseException {
        // 원래 날짜
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 변환할 날짜
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy aa HH : mm");

        Date date = originalFormat.parse(dateStr);
        return targetFormat.format(date);
    }
}