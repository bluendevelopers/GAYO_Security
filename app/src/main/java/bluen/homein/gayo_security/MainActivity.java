package bluen.homein.gayo_security;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.navigation.ui.AppBarConfiguration;

import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_mode;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}