package bluen.homein.gayo_security.activity.preferences.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.VibrationEffect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.preferences.PreferencesActivity;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;


public class AllowedIpListAdapter extends BaseAdapter {

    public List<RequestDataFormat.IpAddressBody> itemsList;
    private final Context context;

    public AllowedIpListAdapter(Context context, List<RequestDataFormat.IpAddressBody> itemsList) {
        super();
        this.context = context;
        this.itemsList = itemsList;
    }


    @Override
    public int getCount() {
        return itemsList != null ? itemsList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return itemsList != null ? itemsList.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_allow_ip, parent, false);
        }

        EditText etAllowIp1 = view.findViewById(R.id.et_allow_ip_1);
        EditText etAllowIp2 = view.findViewById(R.id.et_allow_ip_2);
        EditText etAllowIp3 = view.findViewById(R.id.et_allow_ip_3);
        EditText etAllowIp4 = view.findViewById(R.id.et_allow_ip_4);
        ImageView ivBtnType = view.findViewById(R.id.iv_btn_type);

        if (etAllowIp1.getTag() instanceof TextWatcher) {
            etAllowIp1.removeTextChangedListener((TextWatcher) etAllowIp1.getTag());
            etAllowIp2.removeTextChangedListener((TextWatcher) etAllowIp2.getTag());
            etAllowIp3.removeTextChangedListener((TextWatcher) etAllowIp3.getTag());
            etAllowIp4.removeTextChangedListener((TextWatcher) etAllowIp4.getTag());
        }

        // 초기값 설정
        if (!itemsList.get(position).getIpAddress().isEmpty()) {
            if (!itemsList.get(position).getIpAddress().equals("...")) {
                String[] ipArray = itemsList.get(position).getIpAddress().split("\\.", -1);

                etAllowIp1.setText(ipArray[0]);
                etAllowIp2.setText(ipArray[1]);
                etAllowIp3.setText(ipArray[2]);
                etAllowIp4.setText(ipArray[3]);
            } else {
                etAllowIp1.setText("");
                etAllowIp2.setText("");
                etAllowIp3.setText("");
                etAllowIp4.setText("");
            }
        }

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String newIp = etAllowIp1.getText().toString().trim() + "." +
                        etAllowIp2.getText().toString().trim() + "." +
                        etAllowIp3.getText().toString().trim() + "." +
                        etAllowIp4.getText().toString().trim();

                itemsList.get(position).setIpAddress(newIp);  // 아이템 데이터 갱신
            }
        };

        etAllowIp1.addTextChangedListener(watcher);
        etAllowIp2.addTextChangedListener(watcher);
        etAllowIp3.addTextChangedListener(watcher);
        etAllowIp4.addTextChangedListener(watcher);

        etAllowIp1.setTag(watcher);
        etAllowIp2.setTag(watcher);
        etAllowIp3.setTag(watcher);
        etAllowIp4.setTag(watcher);

        // 삭제 버튼 처리
        if (position == 0) {
            ivBtnType.setImageResource(R.drawable.plus_b);
            ivBtnType.setOnClickListener(v -> {
                ((PreferencesActivity) context).vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                if (itemsList.size() < 5) {
                    itemsList.add(new RequestDataFormat.IpAddressBody("..."));
                    notifyDataSetChanged();
                } else {
                    ((PreferencesActivity) context)
                            .showPopupDialog("허용 IP 는 최대 5개 까지\n등록 가능합니다.",  context.getString(R.string.confirm));
                }
            });
        } else {
            ivBtnType.setImageResource(R.drawable.minus_b);
            ivBtnType.setOnClickListener(v -> {
                ((PreferencesActivity) context).vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                itemsList.remove(position);
                notifyDataSetChanged();
            });
        }

        return view;
    }

    public void setData(ArrayList<RequestDataFormat.IpAddressBody> newData) {
        this.itemsList = newData;
        notifyDataSetChanged();
    }

    public void checkBlankItem() {
        for (RequestDataFormat.IpAddressBody item : itemsList) {
            String ip = item.getIpAddress();

            if (ip == null || ip.trim().isEmpty()) {
                continue;  // 체크 통과하고 삭제 처리 예정
            }

            if (ip.equals("...")) {
                continue; // 허용되는 형태
            }

            String[] parts = ip.split("\\.", -1);  // 추후 사용할 형태로 검사 (-1로 빈칸 포함)

            if (parts.length != 4) {
                ((PreferencesActivity) context)
                        .showPopupDialog("허용 IP를 공란이 없도록 입력해주세요.",  context.getString(R.string.confirm));
                return;
            }

            for (String part : parts) {
                if (part.trim().isEmpty()) {
                    ((PreferencesActivity) context)
                            .showPopupDialog("허용 IP를 공란이 없도록 입력해주세요.",  context.getString(R.string.confirm));
                    return;
                }
            }
        }
    }

}