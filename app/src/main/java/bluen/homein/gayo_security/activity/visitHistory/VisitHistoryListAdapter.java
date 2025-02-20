package bluen.homein.gayo_security.activity.visitHistory;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.callRecord.CallRecordActivity;
import bluen.homein.gayo_security.rest.ResponseDataFormat;


public class VisitHistoryListAdapter extends BaseAdapter {

    public List<ResponseDataFormat.VisitHistoryListBody.VisitHistoryInfo> itemsList;
    private final Context context;

    public VisitHistoryListAdapter(Context context) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_visit_history_list, parent, false);
        }

        ConstraintLayout layRoot = view.findViewById(R.id.lay_root);
        View vDivider = view.findViewById(R.id.v_divider);
        TextView tvVisitType = view.findViewById(R.id.tv_visit_type);
        TextView tvVisitDate = view.findViewById(R.id.tv_visit_date);
        TextView tvVisitTime = view.findViewById(R.id.tv_visit_time);

        ConstraintLayout layDeleteBtn = view.findViewById(R.id.lay_delete_btn);


        tvVisitType.setText(itemsList.get(position).getVisitType());
        tvVisitDate.setText(itemsList.get(position).getVisitDate());
        tvVisitTime.setText(itemsList.get(position).getVisitingTime());

        if (position % 2 == 0) {
            tvVisitType.setTypeface(tvVisitType.getTypeface(), Typeface.BOLD);
            tvVisitDate.setTypeface(tvVisitDate.getTypeface(), Typeface.BOLD);
            tvVisitTime.setTypeface(tvVisitTime.getTypeface(), Typeface.BOLD);
        } else {
            layRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        if (!itemsList.isEmpty()) {
            if (position == itemsList.size() - 1) {
                vDivider.setVisibility(View.GONE);
            }
        }

        layDeleteBtn.setOnClickListener(v -> {
            //code
            ((VisitHistoryListActivity) context).mIsDelete = true;
            ((VisitHistoryListActivity) context).deleteItem = itemsList.get(position);
            ((VisitHistoryListActivity) context).showPopupDialog(itemsList.get(position).getVisitType() + "\n" + itemsList.get(position).getVisitDate() + "\n", "방문자 목록을 삭제 하시겠습니까?", context.getString(R.string.cancel), context.getString(R.string.confirm));
        });

        return view;
    }

    public void setData(List<ResponseDataFormat.VisitHistoryListBody.VisitHistoryInfo> newData) {
        this.itemsList = newData;
        notifyDataSetChanged();
    }

    private String changeDateFormat(String dateStr) throws ParseException {
        // 원래 날짜
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 변환할 날짜
        SimpleDateFormat targetFormat = new SimpleDateFormat("aa HH : mm");

        Date date = originalFormat.parse(dateStr);
        return targetFormat.format(date);
    }
}