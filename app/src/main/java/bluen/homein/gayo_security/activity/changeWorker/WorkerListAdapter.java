package bluen.homein.gayo_security.activity.changeWorker;

import android.content.Context;
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


public class WorkerListAdapter extends BaseAdapter {

    public List<ResponseDataFormat.WorkerListBody.WorkerInfo> itemsList;
    private final Context context;

    public WorkerListAdapter(Context context) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_worker_list, parent, false);
        }
        ConstraintLayout layRoot = view.findViewById(R.id.lay_root);
        View vDivider = view.findViewById(R.id.v_divider);
        TextView tvRowNum = view.findViewById(R.id.tv_no);
        TextView tvPhoneNumber = view.findViewById(R.id.tv_phone_number);
        TextView tvSelectWorkerBtn = view.findViewById(R.id.tv_select_worker_btn);

        tvRowNum.setText(String.valueOf(itemsList.get(position).getRowNum()));
        tvPhoneNumber.setText(itemsList.get(position).getPhoneNumber());

        if (position % 2 == 0) {
            tvRowNum.setTypeface(tvRowNum.getTypeface(), Typeface.BOLD);
            tvPhoneNumber.setTypeface(tvPhoneNumber.getTypeface(), Typeface.BOLD);
        } else {
            layRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        if (!itemsList.isEmpty()) {
            if (position == itemsList.size() - 1) {
                vDivider.setVisibility(View.GONE);
            }
        }
        tvSelectWorkerBtn.setOnClickListener(v -> {
            if (!((ChangeWorkerActivity) context).mPrefGlobal.getPatrolMode()) {
                ((ChangeWorkerActivity) context).setSelectedWorkerInfo(itemsList.get(position));
                ((ChangeWorkerActivity) context).showPopupDialog("현재 근무자를", itemsList.get(position).getPhoneNumber(), "으로 변경하시겠습니까?", "취 소",  context.getString(R.string.confirm));
            } else {
                ((ChangeWorkerActivity) context).selectedWorkerInfo = null;
                ((ChangeWorkerActivity) context).showWarningDialog("순찰 모드 중에는\n근무자 변경이 불가능 합니다.",  context.getString(R.string.confirm));
            }
        });


        return view;
    }

    public void setData(List<ResponseDataFormat.WorkerListBody.WorkerInfo> newData) {
        this.itemsList = newData;
        notifyDataSetChanged();
    }

}