package bluen.homein.gayo_security.activity.preferences.fragment;

import android.view.View;
import android.widget.ImageView;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.preferences.PreferencesActivity;
import bluen.homein.gayo_security.base.BaseFragment;
import butterknife.BindView;
import butterknife.OnClick;

public class PatrolModeSetFragment extends BaseFragment {

    @BindView(R.id.iv_auto_open)
    ImageView ivAutoOpen;
    @BindView(R.id.iv_call_patrolman)
    ImageView ivCallPatrolMan;
    @BindView(R.id.iv_call_main_contact)
    ImageView ivCallMainContact;

    int selectedNumber = 0;

    @OnClick(R.id.tv_save_btn)
    void clickSaveBtn() {
        mPrefGlobal.setPatrolDoorOpen(selectedNumber);
        ((PreferencesActivity) activity).showPopupDialog(null, "성공적으로\n저장 되었습니다.", "확 인");

    }

    @OnClick({R.id.iv_auto_open, R.id.iv_call_patrolman,
            R.id.iv_call_main_contact})
    void clickButtonView(View view) {

        ivAutoOpen.setImageResource(R.drawable.box_non_select);
        ivCallPatrolMan.setImageResource(R.drawable.box_non_select);
        ivCallMainContact.setImageResource(R.drawable.box_non_select);

        switch (view.getId()) {

            case R.id.iv_auto_open:
                selectedNumber = 0;
                ivAutoOpen.setImageResource(R.drawable.box_select);
                break;

            case R.id.iv_call_patrolman:
                selectedNumber = 1;
                ivCallPatrolMan.setImageResource(R.drawable.box_select);
                break;

            case R.id.iv_call_main_contact:
                selectedNumber = 2;
                ivCallMainContact.setImageResource(R.drawable.box_select);
                break;

        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_patrol_mode_set;
    }

    @Override
    protected void initFragment() {

    }

    @Override
    protected void initFragmentView(View v) {
        if (mPrefGlobal != null) {
            selectedNumber = mPrefGlobal.getPatrolDoorOpen();
            switch (mPrefGlobal.getPatrolDoorOpen()) {
                case 0:
                    clickButtonView(v.findViewById(R.id.iv_auto_open));
                    break;
                case 1:
                    clickButtonView(v.findViewById(R.id.iv_call_patrolman));
                    break;
                case 2:
                    clickButtonView(v.findViewById(R.id.iv_call_main_contact));
                    break;
            }
        }
    }
}
