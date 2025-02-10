package bluen.homein.gayo_security;

import android.os.Bundle;

import bluen.homein.gayo_security.base.BaseActivity;
import butterknife.OnClick;

public class ChangeWorkerActivity extends BaseActivity {

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        finish();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_change_worker;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        hideNavigationBar();

    }

}