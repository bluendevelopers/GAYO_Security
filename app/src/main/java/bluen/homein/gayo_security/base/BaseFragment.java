package bluen.homein.gayo_security.base;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    protected String serialCode = "";
    protected String buildingCode = "";
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
            appContext = activity.getApplicationContext();
            serialCode = activity.serialCode;
            buildingCode = activity.buildingCode;
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

        return v;
    }


    public void hideAndClearFocus(Context _context, EditText _editText) {
        InputMethodManager inputManager = (InputMethodManager) _context.getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(_editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        _editText.clearFocus();
    }
}
