package bluen.homein.gayo_security.activity.preferences;

import android.os.Bundle;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.base.BaseActivity;
import butterknife.OnClick;

public class PreferencesActivity extends BaseActivity {

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        finish();
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


    }

}