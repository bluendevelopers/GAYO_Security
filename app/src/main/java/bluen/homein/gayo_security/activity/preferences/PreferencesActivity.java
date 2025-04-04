package bluen.homein.gayo_security.activity.preferences;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.preferences.fragment.ChangePasswordFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.DataSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.NetworkSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.PatrolModeSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.ScreenSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.SleepModeSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.VolumeSetFragment;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.dialog.PopupDialog;
import butterknife.BindView;
import butterknife.OnClick;

public class PreferencesActivity extends BaseActivity {

    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.lay_container)
    ConstraintLayout layContainer;
    @BindView(R.id.tv_wrong_password)
    TextView tvWrongPassword;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.lay_password)
    ConstraintLayout layPassword;
    @BindView(R.id.rv_button_list)
    RecyclerView rvButtonList;

    private PrefButtonListAdapter prefButtonListAdapter;
    private List<String> prefItemList = new ArrayList<>();
    private String clickedBtnName = "";
    private int fragmentRequestedNumber = 0;
    public final int REQUEST_REFRESH_NUMBER = 1;
    public final int REQUEST_SETTING_SUCCESS_NUMBER = 2;
    public final int REQUEST_SAVE_DATA = 3;
    public final int REQUEST_GET_TOKEN = 4;
    public final int REQUEST_GET_NETWORK_DATA = 5;

    public interface NetworkInterface {
        void refreshFragment();
        void saveData();
        void getNetworkData();
        void getToken();
    }

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        setResult(-1);
        finish();
    }

    @OnClick(R.id.lay_password)
    void clickLayPassword() {
        hideAndClearFocus(this, etPassword);
        layPassword.requestFocus();

    }

    @OnClick(R.id.btn_password)
    void clickBtnPassword() {
        //code
        if (etPassword.getText().toString().equals(mPrefGlobal.getDevicePassword())) {
            prefButtonListAdapter.setAccessible(true);
            hideAndClearFocus(PreferencesActivity.this, etPassword);
            hideNavigationBar();
            layPassword.setVisibility(View.GONE);
            prefButtonListAdapter.selectedPosition = 0;
            prefButtonListAdapter.notifyDataSetChanged();
            Fragment selectedFragment = new ScreenSetFragment();

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.lay_container, selectedFragment)
                        .commit();
            }
        } else {
            etPassword.setBackgroundDrawable(getDrawable(R.drawable.btn_border_radius_15_stroke_red));
            tvWrongPassword.setVisibility(View.VISIBLE);
        }
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

        popupDialog.onCallBack(new PopupDialog.DialogCallback() {
            @Override
            public void onFinish() {
                fragmentRequestedNumber = 0;
            }

            @Override
            public void onNextStep() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.lay_container);
                if (currentFragment != null) {

                    switch (fragmentRequestedNumber) {
                        case REQUEST_REFRESH_NUMBER:
                            ((NetworkInterface) currentFragment).refreshFragment();
                            fragmentRequestedNumber = 0;
                            break;
                        case REQUEST_GET_TOKEN:
                            ((NetworkInterface) currentFragment).getToken();
                            fragmentRequestedNumber = 0;
                            break;
                        case REQUEST_SAVE_DATA:
                            ((NetworkInterface) currentFragment).saveData();
                            fragmentRequestedNumber = 0;
                            break;
                        case REQUEST_GET_NETWORK_DATA:
                            ((NetworkInterface) currentFragment).getNetworkData();
                            fragmentRequestedNumber = 0;
                            break;
                        case REQUEST_SETTING_SUCCESS_NUMBER:
                            finish();
                            break;

                    }

                }

            }
        });

        prefItemList = Arrays.asList("화면 설정", "음량 조절", "슬립모드 설정", "네트워크 설정", "데이터 설정", "순찰모드 설정", "비밀번호 변경");
        prefButtonListAdapter = new PrefButtonListAdapter(PreferencesActivity.this, R.layout.item_pref_button_list, mPrefGlobal, new PrefButtonListAdapter.OnPrefButtonClickListener() {
            @Override
            public void clickButton(TextView textView) {
                if (layPassword.getVisibility() != View.VISIBLE) {
                    onTextViewClicked(textView);
                }
            }
        });
        prefButtonListAdapter.setItems(prefItemList);
        rvButtonList.setAdapter(prefButtonListAdapter);

        if (mPrefGlobal.getAuthorization() != null) {
            Typeface originalTypeface = ResourcesCompat.getFont(PreferencesActivity.this, R.font.pretendard_regular);

            etPassword.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        clickBtnPassword();
                        return true;
                    } else {
                        return false;
                    }

                }
            });

            etPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Typeface currentTypeface = etPassword.getTypeface();

                    if (s.length() > 0) {
                        etPassword.setTypeface(originalTypeface, Typeface.BOLD);
                    } else {
                        etPassword.setTypeface(originalTypeface, Typeface.NORMAL);
                    }
                    etPassword.setBackgroundDrawable(getDrawable(R.drawable.btn_border_radius_15));
                    tvWrongPassword.setVisibility(View.INVISIBLE);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else {
            Fragment selectedFragment = new NetworkSetFragment();
            layPassword.setVisibility(View.GONE);
            prefButtonListAdapter.selectedPosition = 3;

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
        clickedBtnName = tvStr;

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
                selectedFragment = new ChangePasswordFragment();
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


    public void hideAndClearFocus(Context _context, EditText _editText) {
        InputMethodManager inputManager = (InputMethodManager) _context.getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(_editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        _editText.clearFocus();
    }

    public void setFragmentRequested(int num) {
        fragmentRequestedNumber = num;
    }
}