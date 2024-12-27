package bluen.homein.gayo_security.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import bluen.homein.gayo_security.R;

public class PopupDialog extends Dialog implements View.OnClickListener {
    private AlertDialog.Builder mBuilder;
    private Dialog mDialog;
    private Context mContext;

    private DialogCallback dialogListener;


    public interface DialogCallback {
        void onFinish();

        void onNextStep();
    }

    public void onCallBack(DialogCallback dialogListener) {
        this.dialogListener = dialogListener;
    }

    public PopupDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    private void setDialog() {
        mBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();

        mBuilder.setView(inflater.inflate(R.layout.dialog_base, findViewById(R.id.dialog_root_lay)))
                .setCancelable(false);

        mDialog = mBuilder.show();
    }

    private void setDialog(int _activityId, int _layoutId) {
        mBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();

        mBuilder.setView(inflater.inflate(_activityId, findViewById(_layoutId)))
                .setCancelable(false);

        mDialog = mBuilder.show();
    }

    private void setDisplay() {
        if (null != mDialog) {
            Window dialogWindow = mDialog.getWindow();
            Window layoutWindow = getWindow();
            if (null != dialogWindow && null != layoutWindow) {
                WindowManager.LayoutParams params = dialogWindow.getAttributes();

                Display display = layoutWindow.getWindowManager().getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);

                params.width = (int) (point.x * 0.9f);
                dialogWindow.setAttributes(params);
            }

//            Window window = mDialog.getWindow();
//            if (null != window) {
//                int x = (int) (point.x * 0.8f);
//                int y = (int) (point.y * 0.5f);
//
//                window.setLayout(x, y);
//            }
        }
    }

    //------------------------------ show

    public void showAlertDialog(String _cancel, String _confirm, String _title, String _sub) {
        if (null == mDialog) {
            setDialog();
            setDisplay();

            Button confirm = mDialog.findViewById(R.id.dialog_btn_confirm);
            Button cancel = mDialog.findViewById(R.id.dialog_btn_cancel);
            TextView title = mDialog.findViewById(R.id.dialog_tv_msg);
            TextView sub = mDialog.findViewById(R.id.dialog_tv_msg);

            if (_confirm != null) {
                confirm.setText(_confirm);
                confirm.setOnClickListener(v -> {
                    dismiss();
                    dialogListener.onNextStep();
                });
            } else {
                confirm.setVisibility(View.GONE);
            }

            cancel.setText(_cancel);
            cancel.setOnClickListener(this);

            if (_title != null) {
                title.setText(_title);
            } else {
                title.setVisibility(View.GONE);
            }

            if (_sub != null) {
                sub.setText(_sub);
            } else {
                sub.setVisibility(View.GONE);
            }
        }
    }

    public void showAlertDialog(int _activityId, int _layoutId, String _cancel, String _confirm) {  // custom layout + cancel & confirm Dialog
        if (null == mDialog) {
            setDialog(_activityId, _layoutId);

            Button confirm = mDialog.findViewById(R.id.dialog_btn_confirm);
            Button cancel = mDialog.findViewById(R.id.dialog_btn_cancel);

            if (null != _confirm) {
                confirm.setText(_confirm);
                confirm.setOnClickListener(v -> {
                    dismiss();
                    dialogListener.onNextStep();
                });
            } else {
                confirm.setVisibility(View.GONE);
            }

            cancel.setText(_cancel);
            cancel.setOnClickListener(this);
        }
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public void dismiss() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_btn_confirm:
                dismiss();
                break;
            case R.id.dialog_btn_cancel:
                dismiss();
                dialogListener.onFinish();
                break;
        }
    }
}