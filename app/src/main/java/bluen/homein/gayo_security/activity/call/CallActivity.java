package bluen.homein.gayo_security.activity.call;

import static bluen.homein.gayo_security.activity.addFacilityContact.AddContactActivity.RESULT_CODE_ADD_CONTACT;

import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.androidslidr.Slidr;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.addFacilityContact.AddContactActivity;
import bluen.homein.gayo_security.base.BaseActivity;
import butterknife.BindView;
import butterknife.OnClick;

public class CallActivity extends BaseActivity {

    private static final int REQUEST_CODE_ADD_CONTACT = 1;

    @BindView(R.id.rv_facility_list)
    RecyclerView rvFacilityList;
    @BindView(R.id.lay_car_number_info)
    ConstraintLayout layCarNumberInfo;
    @BindView(R.id.tv_car_number)
    TextView tvCarNumber;

    @BindView(R.id.tv_apt_dong_number)
    TextView tvDongNumber;
    @BindView(R.id.tv_apt_ho_number)
    TextView tvHoNumber;
    @BindView(R.id.slidr_volume)
    Slidr slidrVolume;

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        finish();
    }

    @OnClick(R.id.lay_add_contact_btn)
    void clickChangeWorkerBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        Intent intent = new Intent(CallActivity.this, AddContactActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_ADD_CONTACT) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_call;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        hideNavigationBar();


    }

//    public static class PageNumberListAdapter extends BaseRecyclerAdapter<String, PageNumberListAdapter.ViewHolder> {
//        private int currentPageNumber = 1;
//
//        public PageNumberListAdapter(Context context, int resource) {
//            super(context, resource);
//        }
//
//        @Override
//        protected ViewHolder onCreateViewHolderBase(View view) {
//            return new ViewHolder(view);
//        }
//
//        @Override
//        protected void onBindViewHolderBase(ViewHolder holder, int position) {
//
//            holder.tvPageNumber.setText(mData.get(position));
//
//            if (position == mData.size() - 1) {
//                holder.vMarginRight.setVisibility(View.GONE);
//            }
//
//            if (mData.get(position).equals(String.valueOf(currentPageNumber))) {
//                holder.tvPageNumber.setTextColor(Color.WHITE);
//                holder.tvPageNumber.setTypeface(holder.tvPageNumber.getTypeface(), Typeface.BOLD);
//
//            } else {
//                holder.tvPageNumber.setTextColor(Color.BLACK);
//                holder.tvPageNumber.setBackground(null);
//
//            }
//
//
//        }
//
//        // Recycler View Holder
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            ConstraintLayout layRoot;
//            TextView tvPageNumber;
//            View vMarginRight;
//
//
//            ViewHolder(@NonNull View v) {
//                super(v);
//
//                layRoot = v.findViewById(R.id.lay_root);
//                tvPageNumber = v.findViewById(R.id.tv_page_number);
//                vMarginRight = v.findViewById(R.id.v_margin_right);
//
//                //페이지 번호 클릭 시
//                layRoot.setOnClickListener(view -> {
//
//                });
//
//            }
//        }
//
//    }

}