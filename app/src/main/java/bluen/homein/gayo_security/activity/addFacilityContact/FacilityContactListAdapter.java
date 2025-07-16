package bluen.homein.gayo_security.activity.addFacilityContact;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import bluen.homein.gayo_security.rest.ResponseDataFormat;


public class FacilityContactListAdapter extends BaseAdapter {

    public List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo> itemsList;
    private final Context context;
    private View dialogView;

    public FacilityContactListAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {

        return itemsList != null ? itemsList.size() : 0;
    }

    @Override
    public ResponseDataFormat.FacilityContactListBody.FacilityContactInfo getItem(int i) {
        ResponseDataFormat.FacilityContactListBody.FacilityContactInfo item = itemsList != null ? itemsList.get(i) : null;
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
//        ((AddContactActivity)context).GayoPref
        String[] myIdAddress = Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceNetworkBody().getIpAddress().split("\\.", -1);
        String[] itemAddress = getItem(position).getFacilityIPAddress().split("\\.", -1);

        layPingTestBtn.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            dialogView = inflater.inflate(R.layout.dialog_ping_test, null);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ImageView ivCloseBtn = dialogView.findViewById(R.id.iv_close_btn);

            TextView tvIpAddress1 = dialogView.findViewById(R.id.et_ip_address_1);
            TextView tvIpAddress2 = dialogView.findViewById(R.id.et_ip_address_2);
            TextView tvIpAddress3 = dialogView.findViewById(R.id.et_ip_address_3);
            TextView tvIpAddress4 = dialogView.findViewById(R.id.et_ip_address_4);

            TextView tvTestIpAddress1 = dialogView.findViewById(R.id.et_test_ip_address_1);
            TextView tvTestIpAddress2 = dialogView.findViewById(R.id.et_test_ip_address_2);
            TextView tvTestIpAddress3 = dialogView.findViewById(R.id.et_test_ip_address_3);
            TextView tvTestIpAddress4 = dialogView.findViewById(R.id.et_test_ip_address_4);
            Button btnTestStart = dialogView.findViewById(R.id.dialog_btn_test_start);
            Button btnResult = dialogView.findViewById(R.id.dialog_btn_result);

            tvIpAddress1.setText(myIdAddress[0]);
            tvIpAddress2.setText(myIdAddress[1]);
            tvIpAddress3.setText(myIdAddress[2]);
            tvIpAddress4.setText(myIdAddress[3]);

            tvTestIpAddress1.setText(itemAddress[0]);
            tvTestIpAddress2.setText(itemAddress[1]);
            tvTestIpAddress3.setText(itemAddress[2]);
            tvTestIpAddress4.setText(itemAddress[3]);

            View decorView = dialog.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);

            ivCloseBtn.setOnClickListener(w -> {
                dialog.dismiss();
            });

            btnTestStart.setOnClickListener(w -> {
                //핑테스트 테스트 시작
                //code
            });

            btnResult.setOnClickListener(w -> {
                //핑테스트 결과 보기
                //code
            });

            dialog.show();

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(window.getAttributes());

                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                window.setAttributes(layoutParams);
            }
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