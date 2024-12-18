package bluen.homein.gayo_security.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    protected BaseActivity activity = null;

    protected Context appContext = null;
    protected Context fragmentContext = null;

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
}
