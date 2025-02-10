package bluen.homein.gayo_security;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.base.BaseRecyclerAdapter;
import butterknife.BindView;
import butterknife.OnClick;

public class PreferencesActivity extends BaseActivity {

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        finish();
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