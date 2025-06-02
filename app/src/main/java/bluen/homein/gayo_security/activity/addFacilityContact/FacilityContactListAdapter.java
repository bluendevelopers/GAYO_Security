package bluen.homein.gayo_security.activity.addFacilityContact;

import android.content.Context;
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
import bluen.homein.gayo_security.activity.callRecord.CallRecordActivity;
import bluen.homein.gayo_security.rest.ResponseDataFormat;


public class FacilityContactListAdapter extends BaseAdapter {

    public List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo> itemsList;
    private final Context context;

    public FacilityContactListAdapter(Context context) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_add_contact, parent, false);
        }
        ConstraintLayout layRoot = view.findViewById(R.id.lay_root);

        View vDivider = view.findViewById(R.id.v_divider);
        TextView tvFacilityName = view.findViewById(R.id.tv_facility_name);
        TextView tvIpAddress = view.findViewById(R.id.tv_ip_address);

        LinearLayout layPingTestBtn = view.findViewById(R.id.lay_ping_test_btn);
        LinearLayout layDeleteBtn = view.findViewById(R.id.lay_delete_btn);

        tvFacilityName.setText(itemsList.get(position).getFacilityName());
        tvIpAddress.setText(itemsList.get(position).getFacilityIPAddress());

        if (position % 2 == 0) {
            tvFacilityName.setTypeface(tvFacilityName.getTypeface(), Typeface.BOLD);
            tvIpAddress.setTypeface(tvIpAddress.getTypeface(), Typeface.BOLD);
        } else {
            layRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        if (!itemsList.isEmpty()) {
            if (position == itemsList.size() - 1) {
                vDivider.setVisibility(View.GONE);
            }
        }

        layPingTestBtn.setOnClickListener(v -> {
            //code
        });

        layDeleteBtn.setOnClickListener(v -> {
            //code
            ((AddContactActivity) context).STATUS_CODE = ((AddContactActivity) context).STATUS_DELETE_CONTACT;
            ((AddContactActivity) context).deleteItem = itemsList.get(position);
            ((AddContactActivity) context).showPopupDialog(itemsList.get(position).getFacilityName() + "\n", "연락처를 삭제 하시겠습니까?", context.getString(R.string.cancel), context.getString(R.string.confirm));

        });

        return view;
    }

    public void setData(List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo> newData) {
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