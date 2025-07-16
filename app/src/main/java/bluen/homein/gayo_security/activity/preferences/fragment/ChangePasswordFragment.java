package bluen.homein.gayo_security.activity.preferences.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import static org.webrtc.ContextUtils.getApplicationContext;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.io.IOException;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.preferences.PreferencesActivity;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.base.BaseFragment;
import bluen.homein.gayo_security.preference.Gayo_Preferences;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.rest.Retrofit;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends BaseFragment {

    @BindView(R.id.et_current_password)
    EditText etCurrentPassword;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.et_new_password_re)
    EditText etNewPasswordRe;
    @BindView(R.id.tv_wrong_current_password)
    TextView tvWrongCurrentPassword;
    @BindView(R.id.tv_wrong_password_re)
    TextView tvWrongPasswordRe;
    @BindView(R.id.tv_wrong_password)
    TextView tvWrongPassword;

    Gayo_SharedPreferences mPrefGlobal = null;

    @OnClick(R.id.tv_save_btn)
    void clickBtnSave() {

        if (etCurrentPassword.getText().toString().isEmpty()) {
            //popup
            activity.showPopupDialog(null, "현재 비밀번호를 입력해주세요.", getString(R.string.confirm));
            return;
        }
        if (etNewPassword.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "새 비밀번호를 입력해주세요.", getString(R.string.confirm));
            return;
        }
        if (etNewPasswordRe.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "새 비밀번호 확인을 입력해주세요.", getString(R.string.confirm));
            return;
        }

        if (mPrefGlobal.getDevicePassword().equals(etCurrentPassword.getText().toString())) {

            if (etNewPasswordRe.getText().toString().equals(etCurrentPassword.getText().toString())) {
                activity.showPopupDialog(null, "현재 비밀번호와 새 비밀번호가 같습니다.", getString(R.string.confirm));
                return;
            }

            if (mPrefGlobal != null) {
                if (etNewPassword.getText().toString().equals(etNewPasswordRe.getText().toString())) {
                    activity.showProgress();
                    //api
                    Retrofit.PreferencesApi preferencesApi = Retrofit.PreferencesApi.retrofit.create(Retrofit.PreferencesApi.class);
                    Call<ResponseDataFormat.PasswordDataBody> call = preferencesApi.changePassword(mPrefGlobal.getAuthorization(), new ResponseDataFormat.PasswordDataBody(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode(), Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode(), etNewPassword.getText().toString(), etNewPasswordRe.getText().toString()));


                    call.enqueue(new Callback<ResponseDataFormat.PasswordDataBody>() {
                        @Override
                        public void onResponse(Call<ResponseDataFormat.PasswordDataBody> call, Response<ResponseDataFormat.PasswordDataBody> response) {
                            activity.closeProgress();

                            if (response.body() != null) {
                                if (response.body().getResult().equals("OK")) {
                                    mPrefGlobal.setDevicePassword(etNewPassword.getText().toString());
                                    myPassword = mPrefGlobal.getDevicePassword();
                                    etCurrentPassword.setText("");
                                    etNewPassword.setText("");
                                    etNewPasswordRe.setText("");
                                    activity.showPopupDialog(null, "비밀번호 변경에 성공하였습니다.", getString(R.string.confirm));
                                } else {
                                    // 실패 ui
                                    etNewPassword.setBackgroundDrawable(appContext.getDrawable(R.drawable.btn_border_radius_15_stroke_red));
                                    etNewPasswordRe.setBackgroundDrawable(appContext.getDrawable(R.drawable.btn_border_radius_15_stroke_red));
                                    tvWrongPassword.setVisibility(View.VISIBLE);
                                    tvWrongPasswordRe.setVisibility(View.VISIBLE);
                                    tvWrongPassword.setText("비밀번호가 맞지 않습니다.");
                                    tvWrongPasswordRe.setText("비밀번호가 맞지 않습니다.");
                                }
                            } else {
                                ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_NONE);
                                String error = "";
                                try {
                                    error = response.errorBody().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String errorMessage = BaseActivity.ErrorBodyParser.JsonParser(error)[BaseActivity.ErrorBodyParser.ERROR_MESSAGE_NUM].replace("\"", "");
                                if (errorMessage != null) {
                                    Log.e("ChangePasswordFragment", errorMessage);
                                    activity.showWarningDialog(errorMessage, getString(R.string.confirm));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseDataFormat.PasswordDataBody> call, Throwable t) {
                            activity.closeProgress();

                        }
                    });

                } else {
                    etNewPassword.setBackgroundDrawable(appContext.getDrawable(R.drawable.btn_border_radius_15_stroke_red));
                    etNewPasswordRe.setBackgroundDrawable(appContext.getDrawable(R.drawable.btn_border_radius_15_stroke_red));
                    tvWrongPassword.setVisibility(View.VISIBLE);
                    tvWrongPasswordRe.setVisibility(View.VISIBLE);
                    tvWrongPassword.setText("새 비밀번호가 서로 일치하지 않습니다.");
                    tvWrongPasswordRe.setText("새 비밀번호가 서로 일치하지 않습니다.");
                }
            }
        } else {
            etCurrentPassword.setBackgroundDrawable(appContext.getDrawable(R.drawable.btn_border_radius_15_stroke_red));
            tvWrongCurrentPassword.setVisibility(View.VISIBLE);
            tvWrongCurrentPassword.setText("현재 비밀번호가 일치하지 않습니다.");

        }

    }

    @OnClick(R.id.lay_root)
    void clickLayRoot() {
        hideAndClearFocus(appContext, etCurrentPassword);
        hideAndClearFocus(appContext, etNewPassword);
        hideAndClearFocus(appContext, etNewPasswordRe);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_change_password;
    }

    @Override
    protected void initFragment() {
        if (null == mPrefGlobal) {
            mPrefGlobal = new Gayo_SharedPreferences(appContext, Gayo_Preferences.GLOBAL_INFO);
        }
    }

    @Override
    protected void initFragmentView(View v) {

        etNewPasswordRe.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideAndClearFocus(appContext, etNewPasswordRe);
                    return true;
                } else {
                    return false;
                }

            }
        });

        Typeface originalTypeface = ResourcesCompat.getFont(activity, R.font.pretendard_regular);

        etCurrentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    etCurrentPassword.setTypeface(originalTypeface, Typeface.BOLD);
                } else {
                    etCurrentPassword.setTypeface(originalTypeface, Typeface.NORMAL);
                }
                etCurrentPassword.setBackgroundDrawable(activity.getDrawable(R.drawable.btn_border_radius_15));
                tvWrongCurrentPassword.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    etNewPassword.setTypeface(originalTypeface, Typeface.BOLD);
                } else {
                    etNewPassword.setTypeface(originalTypeface, Typeface.NORMAL);
                }

                etNewPassword.setBackgroundDrawable(activity.getDrawable(R.drawable.btn_border_radius_15));
                tvWrongPassword.setVisibility(View.INVISIBLE);
                etNewPasswordRe.setBackgroundDrawable(activity.getDrawable(R.drawable.btn_border_radius_15));
                tvWrongPasswordRe.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etNewPasswordRe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    etNewPasswordRe.setTypeface(originalTypeface, Typeface.BOLD);
                } else {
                    etNewPasswordRe.setTypeface(originalTypeface, Typeface.NORMAL);
                }
                etNewPassword.setBackgroundDrawable(activity.getDrawable(R.drawable.btn_border_radius_15));
                tvWrongPassword.setVisibility(View.INVISIBLE);
                etNewPasswordRe.setBackgroundDrawable(activity.getDrawable(R.drawable.btn_border_radius_15));
                tvWrongPasswordRe.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

}
