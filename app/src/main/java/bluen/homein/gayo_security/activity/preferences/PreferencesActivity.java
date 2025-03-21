package bluen.homein.gayo_security.activity.preferences;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.preferences.fragment.DataSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.NetworkSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.PatrolModeSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.ScreenSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.SleepModeSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.VolumeSetFragment;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.databinding.FragmentDataSetBinding;
import bluen.homein.gayo_security.databinding.FragmentNetworkSetBinding;
import bluen.homein.gayo_security.databinding.FragmentPatrolModeSetBinding;
import bluen.homein.gayo_security.databinding.FragmentSleepModeSetBinding;
import bluen.homein.gayo_security.databinding.FragmentVolumeSetBinding;
import bluen.homein.gayo_security.dialog.PopupDialog;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;

public class PreferencesActivity extends BaseActivity {


    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.lay_container)
    ConstraintLayout layContainer;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.lay_password)
    ConstraintLayout layPassword;
    @BindView(R.id.rv_button_list)
    RecyclerView rvButtonList;

    private PrefButtonListAdapter prefButtonListAdapter;
    private FragmentDataSetBinding fragmentDataSetBinding;
    private FragmentNetworkSetBinding fragmentNetworkSetBinding;
    private FragmentPatrolModeSetBinding fragmentPatrolModeSetBinding;
    private FragmentSleepModeSetBinding fragmentSleepModeSetBinding;
    private FragmentVolumeSetBinding fragmentVolumeSetBinding;
    private List<String> prefItemList = new ArrayList<>();


    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        finish();
    }

    @OnTouch(R.id.et_password)
    void clickEditText() {
        etPassword.setFocusableInTouchMode(true);
        etPassword.setFocusable(true);
    }

    @OnClick(R.id.lay_password)
    void clickLayPassword() {
        hideAndClearFocus(this, etPassword);
    }

    @OnClick(R.id.btn_password)
    void clickBtnPassword() {

        //code
        if (etPassword.getText().toString().equals("0000")) {
            prefButtonListAdapter.setAccessible(true);
            hideAndClearFocus(PreferencesActivity.this, etPassword);
            hideNavigationBar();
            layPassword.setVisibility(View.GONE);
            Fragment selectedFragment = new ScreenSetFragment();

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.lay_container, selectedFragment)
                        .commit();
            }
        }
    }

    public void onTextViewClicked(View view) {
        TextView clickedTextView = (TextView) view;
        String tvStr = ((TextView) view).getText().toString();

        int mainColor = ContextCompat.getColor(this, R.color.main_color);

        clickedTextView.setTextColor(Color.WHITE);
        clickedTextView.setBackgroundTintList(ColorStateList.valueOf(mainColor));

        Fragment selectedFragment = null;

        switch (tvStr) {
            case "화면 설정":
                selectedFragment = new ScreenSetFragment();
                break;
            case "음량 조절":
                selectedFragment = new VolumeSetFragment();
                break;
            case "슬립모드 설정":
                selectedFragment = new SleepModeSetFragment();
                break;
            case "네트워크 설정":
                selectedFragment = new NetworkSetFragment();
                break;
            case "데이터 설정":
                selectedFragment = new DataSetFragment();
                break;
            case "순찰모드 설정":
                selectedFragment = new PatrolModeSetFragment();
                break;
            case "비밀번호 변경":
//                selectedFragment = new ChangePassword();
                break;
        }
        tvPageTitle.setText("사용자 설정 _" + tvStr);

        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.lay_container, selectedFragment)
                    .commit();
        }
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

    }


    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();


    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_preferences;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        hideNavigationBar();
        prefItemList = Arrays.asList("화면 설정", "음량 조절", "슬립모드 설정", "네트워크 설정", "데이터 설정", "순찰모드 설정", "비밀번호 변경");

        prefButtonListAdapter = new PrefButtonListAdapter(PreferencesActivity.this, R.layout.item_pref_button_list, new PrefButtonListAdapter.OnPrefButtonClickListener() {
            @Override
            public void clickButton(TextView textView) {
                if (layPassword.getVisibility() != View.VISIBLE) {
                    onTextViewClicked(textView);
                }
            }
        });
        prefButtonListAdapter.setItems(prefItemList);
        rvButtonList.setAdapter(prefButtonListAdapter);

        popupDialog.onCallBack(new PopupDialog.DialogCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onNextStep() {

            }
        });


    }

    public void hideAndClearFocus(Context _context, EditText _editText) {
        InputMethodManager inputManager = (InputMethodManager) _context.getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(_editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        _editText.clearFocus();
    }
}