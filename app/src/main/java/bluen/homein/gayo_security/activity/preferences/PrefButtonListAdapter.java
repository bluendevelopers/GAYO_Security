package bluen.homein.gayo_security.activity.preferences;

import android.content.Context;
import android.os.VibrationEffect;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.base.BaseRecyclerAdapter;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;

public class PrefButtonListAdapter extends BaseRecyclerAdapter<String, PrefButtonListAdapter.ViewHolder> {
    private OnPrefButtonClickListener listener;  // 콜백 인터페이스 변수
    private Context context;
    private boolean isAccessible = false;
    public int selectedPosition = 999;
    public Gayo_SharedPreferences mPrefGlobal = null;

    public PrefButtonListAdapter(Context context, int resource, Gayo_SharedPreferences mPrefGlobal, OnPrefButtonClickListener listener) {
        super(context, resource);
        this.context = context;
        this.mPrefGlobal = mPrefGlobal;
        this.listener = listener;
    }

    public interface OnPrefButtonClickListener {
        void clickButton(TextView textView);
    }

    @Override
    protected ViewHolder onCreateViewHolderBase(View view) {
        return new ViewHolder(view);
    }

//    @Override
//    protected void onBindViewHolderBase(ViewHolder holder, int position) {
//
//        holder.tvPrefName.setText(mData.get(position));
//        holder.layBtnBackground.setOnClickListener(view -> {
//            if (isAccessible) {
//
//                holder.layBtnBackground.setBackgroundDrawable(context.getDrawable(R.drawable.btn_blue_fill_border_radius_15));
//                holder.tvPrefName.setTextColor(ContextCompat.getColor(context, R.color.white));
//
//                listener.clickButton(holder.tvPrefName); // 그에 맞는 화면 변경
//            } else {
//                ((PreferencesActivity) context).showPopupDialog("비밀번호 입력 후 사용 가능합니다.",  getString(R.string.confirm));
//            }
//        });
//
//    }

    @Override
    protected void onBindViewHolderBase(ViewHolder holder, int position) {

        holder.tvPrefName.setText(mData.get(position));

        if (selectedPosition == position) {
            // 선택된 상태
            holder.layBtnBackground.setBackgroundDrawable(
                    context.getDrawable(R.drawable.btn_fill_border_radius_15_blue));
            holder.tvPrefName.setTextColor(
                    ContextCompat.getColor(context, R.color.white));
        } else {
            // 선택되지 않은 상태
            holder.layBtnBackground.setBackgroundDrawable(
                    context.getDrawable(R.drawable.background_radius_15_fill));
            holder.tvPrefName.setTextColor(
                    ContextCompat.getColor(context, R.color.black));
        }

        holder.layBtnBackground.setOnClickListener(view -> {
            ((PreferencesActivity) context).vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            if (mPrefGlobal.getAuthorization() != null) {
                if (isAccessible) {
                    int previousSelected = selectedPosition;
                    selectedPosition = position; // 현재 클릭한 position을 선택 상태로 저장

                    notifyItemChanged(previousSelected);
                    notifyItemChanged(selectedPosition);

                    listener.clickButton(holder.tvPrefName);
                } else {
                    ((PreferencesActivity) context)
                            .showPopupDialog("비밀번호 입력 후\n사용 가능합니다.",  context.getString(R.string.confirm));
                }
            } else {
                ((PreferencesActivity) context)
                        .showPopupDialog("데이터 세팅 또는 데이터 불러오기\n완료 후 사용 가능합니다.",  context.getString(R.string.confirm));
            }
        });
    }

    public void setAccessible(boolean _isAccessible) {
        this.isAccessible = _isAccessible;
    }

    // Recycler View Holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layBtnBackground;
        TextView tvPrefName;

        ViewHolder(@NonNull View v) {
            super(v);
            layBtnBackground = v.findViewById(R.id.lay_button_background);
            tvPrefName = v.findViewById(R.id.tv_pref_name);

        }
    }

}
