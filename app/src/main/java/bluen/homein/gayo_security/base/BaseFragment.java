package bluen.homein.gayo_security.base;

import static android.content.Context.INPUT_METHOD_SERVICE;

import static bluen.homein.gayo_security.base.BaseActivity.mContext;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    protected BaseActivity activity = null;

    protected Context appContext = null;
    protected Context fragmentContext = null;
    protected String myPassword = "";
    protected Gayo_SharedPreferences mPrefGlobal = null;

    protected abstract int getLayoutResId();

    protected abstract void initFragment();

    protected abstract void initFragmentView(View v);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        activity = (BaseActivity) getActivity();

        if (activity != null) {
            appContext = mContext;
            mPrefGlobal = activity.mPrefGlobal;
        }

        initFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(getLayoutResId(), container, false);

        ButterKnife.bind(this, v);

        initFragmentView(v);
        if (Gayo_SharedPreferences.PrefDeviceData.prefItem != null && Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceUIBody() != null) {
            setScreenBrightness(Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceUIBody().getBrightness() / 100f);
        }
        return v;
    }

    protected void setScreenBrightness(float brightness) {
        Window window = getActivity().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = brightness;
        window.setAttributes(layoutParams);
    }

    public void hideAndClearFocus(Context _context, EditText _editText) {
        InputMethodManager inputManager = (InputMethodManager) _context.getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(_editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        _editText.clearFocus();
    }
}
