package bluen.homein.gayo_security.activity.call;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static bluen.homein.gayo_security.activity.addFacilityContact.AddContactActivity.RESULT_CODE_ADD_CONTACT;
import static bluen.homein.gayo_security.global.GlobalApplication.callStatus;
import static bluen.homein.gayo_security.preference.Gayo_Preferences.DEVICE_NOT_CONNECTED;
import static bluen.homein.gayo_security.service.WebSocketService.SIGNAL_SERVER_URL;
import static bluen.homein.gayo_security.service.WebSocketService.disconnectWebSocket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.androidslidr.Slidr;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera2Enumerator;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.RtpTransceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.addFacilityContact.AddContactActivity;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.dialog.PopupDialog;
import bluen.homein.gayo_security.preference.Gayo_Preferences;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.service.WebSocketService;
import butterknife.BindView;
import butterknife.OnClick;

public class CallActivity extends BaseActivity {

    private static final int REQUEST_CODE_ADD_CONTACT = 1;
    public static final int RESULT_CODE_ADD_CONTACT_REFRESH = 4;

    @BindView(R.id.rv_facility_list)
    RecyclerView rvFacilityList;
    @BindView(R.id.lay_car_number_info)
    ConstraintLayout layCarNumberInfo;
    @BindView(R.id.lay_call_info)
    ConstraintLayout layCallInfo;
    @BindView(R.id.lay_clock)
    ConstraintLayout layClock;
    @BindView(R.id.tv_car_number)
    TextView tvCarNumber;

    @BindView(R.id.lay_call_view)
    ConstraintLayout layCallView;
    @BindView(R.id.lay_button)
    ConstraintLayout layButton;

    @BindView(R.id.tv_sender_apt_dong_number)
    TextView tvSenderAptDongNumber;
    @BindView(R.id.tv_sender_apt_ho_number)
    TextView tvSenderAptHoNumber;

    @BindView(R.id.tv_call_message)
    TextView tvCallMessage;
    @BindView(R.id.tv_call_time)
    TextView tvCallTime;
    @BindView(R.id.tv_facility_name)
    TextView tvFacilityName;
    @BindView(R.id.tv_facility_info)
    TextView tvFacilityInfo;
    @BindView(R.id.tv_apt_dong_number)
    TextView tvAptDongNumber;
    @BindView(R.id.tv_apt_dong)
    TextView tvAptDong;
    @BindView(R.id.tv_apt_ho_number)
    TextView tvAptHoNumber;
    @BindView(R.id.tv_apt_ho)
    TextView tvAptHo;
    @BindView(R.id.slidr_volume)
    Slidr slidrVolume;
    @BindView(R.id.lay_call_btn)
    LinearLayout layCallBtn;
    @BindView(R.id.lay_call_reception_select)
    ConstraintLayout layCallReceptionSelect;
    @BindView(R.id.lay_dong_ho_info)
    ConstraintLayout layDongHoInfo;
    @BindView(R.id.lay_call_time)
    ConstraintLayout layCallTime;
    @BindView(R.id.lay_hang_up_btn)
    LinearLayout layHangUpBtn;
    @BindView(R.id.lay_sender_apt_info)
    ConstraintLayout laySenderAptInfo;

    // WebRTC 관련 변수
    @BindView(R.id.remote_view)
    SurfaceViewRenderer remoteView;
    private EglBase rootEglBase;
    private MediaConstraints sdpMediaConstraints;
    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;
    private VideoCapturer videoCapturer;

    // 통화 상태 상수
    public static final int CALL_STATUS_IDLE = -1;
    public static final int CALL_STATUS_INCOMING = 0; // 전화 걸려오거나 거는 상태
    public static final int CALL_STATUS_REJECTED = 1;
    public static final int CALL_STATUS_ACCEPT_CALL = 2;
    public static final int CALL_STATUS_CONNECTING = 3;
    public static final int CALL_STATUS_ACTIVE = 4;

    // 현재 상태

    // 발신/수신 구분
    // isSender = true  => “전화를 거는 기기”(발신자) => Answer 생성
    // isSender = false => “전화를 받는 기기”(수신자) => Offer 생성
    private boolean isSender = false;

    // 통화 볼륨 관련
    private AudioManager audioManager;

    // 기타
    private View dialogView;
    private FacilityContactsButtonListAdapter facilityContactsButtonListAdapter;

    private String currentRoomId = "";
    private String currentClientId = "";
    private String currentCallDevice = "";
    private String receivedSdp = "";
    private String senderSerialCode = "";
    private String recipientSerialCode = "";
    public boolean isVideoCall = false;
    private String dongNumber = "";
    private String hoNumber = "";
    private String carNumber = "";
    private boolean isDongFinal = false;
    private boolean isHoFinal = false;
    private boolean isRemoteViewOn = false;
    private GayoAppUserBody gayoAppUserBody = null; //가요앱과 통화시 유저 정보
    private Long callStartMs;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Runnable callTimerRunnable;

    private void startCallTimer() {
        Log.e(TAG, "startCallTimer()!!!");

        callStartMs = SystemClock.elapsedRealtime();
        callTimerRunnable = new Runnable() {
            @Override
            public void run() {
                if (callStatus == CALL_STATUS_ACTIVE || callStatus == CALL_STATUS_CONNECTING) {
                    long elapsed = (SystemClock.elapsedRealtime() - callStartMs) / 1000;
                    int mins = (int) (elapsed / 60);
                    int secs = (int) (elapsed % 60);
                    tvCallTime.setText(String.format(Locale.KOREA, "%02d:%02d", mins, secs));

                    mainHandler.postDelayed(this, 1000);
                } else {
                    Log.e(TAG, "callStatus: " + callStatus);
                }
            }
        };
        mainHandler.post(callTimerRunnable);
    }

    private void stopCallTimer() {
        if (mainHandler != null && callTimerRunnable != null) {
            mainHandler.removeCallbacks(callTimerRunnable);
        }
        if (tvCallTime != null) {
            runOnUiThread(() -> tvCallTime.setText("00:00"));
        }
    }

    private Handler callTimeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable callTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (callStatus == 0) {
                WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                        gayoAppUserBody == null ? "no-answer" : "gayo-no-answer",
                        recipientSerialCode,
                        senderSerialCode,
                        currentRoomId,
                        currentClientId
                );
                WebSocketService.sendWebSocketMessage(body.toJson());
            }
        }
    };


    public void startCallTimeoutHandler() {
        callTimeoutHandler.postDelayed(callTimeoutRunnable, 60000); // 60000ms = 1분
    }

    public void cancelCallTimeoutHandler() {
        callTimeoutHandler.removeCallbacks(callTimeoutRunnable);
    }


    @OnClick({
            R.id.tv_number_0_btn, R.id.tv_number_1_btn, R.id.tv_number_2_btn, R.id.tv_number_3_btn,
            R.id.tv_number_4_btn, R.id.tv_number_5_btn, R.id.tv_number_6_btn, R.id.tv_number_7_btn,
            R.id.tv_number_8_btn, R.id.tv_number_9_btn
    })
    void onNumberClick(TextView clickedView) {
        if (callStatus == CALL_STATUS_IDLE || (callStatus == CALL_STATUS_ACTIVE && currentCallDevice.equalsIgnoreCase("lobby"))) {
            String digit = clickedView.getText().toString();
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

            // 아직 "동" 입력이 완료되지 않았다면, 동 번호에 숫자를 추가
            if (!isDongFinal) {
                if (dongNumber.length() < 4) {
                    if (dongNumber.length() == 0 && digit.equals("0")) {
                        return;
                    }
                    dongNumber += digit;
                    tvAptDongNumber.setText(dongNumber);

                    if (dongNumber.length() == 4) {
                        tvAptDong.setVisibility(View.VISIBLE);
                        isDongFinal = true;
                    }
                }
            }
            // "동" 입력이 끝난 상태라면 "호" 번호에 입력
            else if (!isHoFinal) {
                if (hoNumber.length() < 4) {
                    if (hoNumber.length() == 0 && digit.equals("0")) {
                        return;
                    }
                    hoNumber += digit;
                    tvAptHoNumber.setText(hoNumber);

                    if (hoNumber.length() == 4) {
                        tvAptHo.setVisibility(View.VISIBLE);
                        isHoFinal = true;
                    }
                }
            }
            // 둘 다 완료된 상태이면 아무 동작도 하지 않음
            else {
                Log.e(TAG, "callStatus: " + callStatus);

                // 차량번호 입력
                if (layCarNumberInfo.getVisibility() == View.VISIBLE) {
                    String digitCar = clickedView.getText().toString();
                    if (carNumber.length() < 4) {
                        carNumber += digitCar;
                        tvCarNumber.setText(carNumber);
                    }
                }
            }
        }

    }

    @OnClick(R.id.tv_dong_btn)
    void onDongBtnClick() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        if (callStatus == CALL_STATUS_IDLE) {
            // 동 번호가 비어 있지 않으면서 아직 확정 되지 않은 상태인 경우만
            if (!dongNumber.isEmpty() && !isDongFinal) {
                tvAptDong.setVisibility(View.VISIBLE);
                isDongFinal = true;
            }
        }
    }

    @OnClick(R.id.tv_ho_btn)
    void onHoBtnClick() {
        if (callStatus == CALL_STATUS_IDLE) {
            if (!hoNumber.isEmpty() && !isHoFinal) {
                tvAptHo.setVisibility(View.VISIBLE);
                isHoFinal = true;
            }
        }
    }

    @OnClick(R.id.lay_volume_down)
    void onVolumeDownClick() {
        int newValue = getCurrentSlidrVolume() - 1;

        slidrVolume.setCurrentValue(newValue);
    }

    @OnClick(R.id.lay_volume_up)
    void onVolumeUpClick() {
        int newValue = getCurrentSlidrVolume() + 1;

        slidrVolume.setCurrentValue(newValue);
    }

    @OnClick(R.id.lay_car_number_btn)
    void onCarNumberBtnClick() {
        if (callStatus >= CALL_STATUS_CONNECTING) {
            boolean isCarInfoVisible = layCarNumberInfo.getVisibility() == View.VISIBLE;
            isCarInfoVisible = !isCarInfoVisible;
            layCarNumberInfo.setVisibility(isCarInfoVisible ? View.VISIBLE : View.GONE);
        } else {
            Toast.makeText(this, "차량 번호는 통화 중에 입력 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.lay_door_open_btn)
    void onOpenDoorBtnClick() {
        if (currentCallDevice.equalsIgnoreCase("lobby")) { // 경비실기 또는 월패드
            WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                    "open/gate",
                    recipientSerialCode
            );
            WebSocketService.sendWebSocketMessage(body.toJson());

        } else {
//            showWarningDialog("현재 연결된 로비폰이 없습니다.\n세대 정보를 입력 후 시도해주세요.", getString(R.string.confirm));
            Toast.makeText(this, "현재 연결된 로비폰이 없습니다. 세대 정보를 입력 후 시도해주세요.", Toast.LENGTH_SHORT).show();

        }
    }

    @OnClick(R.id.lay_delete_btn)
    void onDeleteClick() {
        if (callStatus == CALL_STATUS_IDLE) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

            if (!hoNumber.isEmpty()) {
                hoNumber = hoNumber.substring(0, hoNumber.length() - 1);
                tvAptHoNumber.setText(hoNumber);

                if (hoNumber.length() < 4) {
                    tvAptHo.setVisibility(View.INVISIBLE);
                    isHoFinal = false;
                }
            } else if (!dongNumber.isEmpty()) {
                dongNumber = dongNumber.substring(0, dongNumber.length() - 1);
                tvAptDongNumber.setText(dongNumber);

                if (dongNumber.length() < 4) {
                    tvAptDong.setVisibility(View.INVISIBLE);
                    isDongFinal = false;
                }
            }
        } else {
            if (layCarNumberInfo.getVisibility() == View.VISIBLE) {
                if (!carNumber.isEmpty()) {
                    carNumber = carNumber.substring(0, carNumber.length() - 1);
                    tvCarNumber.setText(carNumber);

                }

            }
        }
    }

    // ------------------------------------------------------
    // 통화 버튼 (예: 발신 측)
    // ------------------------------------------------------
    @OnClick(R.id.lay_call_btn)
    void onCallBtnClick() {
        Log.i(TAG, "onCallBtnClick()");
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

        if (tvAptDongNumber.getText().toString().isEmpty() || tvAptHoNumber.getText().toString().isEmpty()) {
//            showPopupDialog(null, "동 호 수를 정확히\n입력 해주세요.", getString(R.string.confirm));
            Toast.makeText(this, "동 호 수를 정확히 입력 해주세요.", Toast.LENGTH_SHORT).show();

            return;
        }

        if (tvAptHo.getVisibility() == View.INVISIBLE) {
            tvAptHo.setVisibility(View.VISIBLE);
        }

        // 통화 전 수신자 선택 팝업

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        dialogView = inflater.inflate(R.layout.dialog_call_type, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View decorView = dialog.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    hideNavigationBar();
                }
            }
        });

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());

            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            window.setAttributes(layoutParams);
        }

        Button btnWallPadAndPhone = dialogView.findViewById(R.id.btn_wall_pad_and_phone);
        Button btnOnlyWallPad = dialogView.findViewById(R.id.btn_only_wall_pad);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel_);
        TextView tvDong = dialogView.findViewById(R.id.tv_dong_number);
        TextView tvHo = dialogView.findViewById(R.id.tv_ho_number);

        tvDong.setText(tvAptDongNumber.getText().toString());
        tvHo.setText(tvAptHoNumber.getText().toString());

        btnOnlyWallPad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callStatus == CALL_STATUS_IDLE && !tvAptDongNumber.getText().toString().isEmpty() && !tvAptHoNumber.getText().toString().isEmpty()) {
                    isSender = true;

                    // 예: 서버에 'invite' 전송
                    recipientSerialCode = ""; // TODO 임시 입니다!!! 월패드의 시리얼 코드 적어야 함....
                    senderSerialCode = Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode();

                    // 임의로 roomId 생성
                    int roomId = (int) (Math.random() * 90000000) + 10000000;
                    currentRoomId = String.valueOf(roomId);

                    WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                            "invite",
                            recipientSerialCode, // 수신자
                            senderSerialCode     // 발신자
                    );
                    body.setRoomId(currentRoomId);

                    // 실제 전송
                    String jsonString = body.toJson();
                    WebSocketService.sendWebSocketMessage(jsonString);
                }
            }
        });

        btnWallPadAndPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callStatus == CALL_STATUS_IDLE && !tvAptDongNumber.getText().toString().isEmpty() && !tvAptHoNumber.getText().toString().isEmpty()) {
                    isSender = true;

                    // 예: 서버에 'invite' 전송
                    recipientSerialCode = ""; // TODO 로비폰, 가요 유저의 시리얼 코드를 아는 방법 없음
                    senderSerialCode = Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode();

                    // 임의로 roomId 생성
                    int roomId = (int) (Math.random() * 90000000) + 10000000;
                    currentRoomId = String.valueOf(roomId);

                    //월패드 수신자
                    WebSocketService.WebSocketBody body1 = new WebSocketService.WebSocketBody(
                            "invite",
                            senderSerialCode,    // 발신자
                            currentRoomId,
                            Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode()
                    );
                    body1.setSender("rtc:" + body1.getSender() + "@" + SIGNAL_SERVER_URL);
                    body1.setReceiver("rtc:" + body1.getBuilDong() + "B" + body1.getBuilHo() + "U@" + SIGNAL_SERVER_URL);

                    //가요 수신자(임시 코드입니다!!!!!!) 로비폰, 가요 유저의 시리얼 코드를 아는 방법 없음!
                    WebSocketService.WebSocketBody body2 = new WebSocketService.WebSocketBody(
                            "gayo-invite",
                            "",
                            currentRoomId,
                            Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode()

                    );
//                    this.receiver = "rtc:" + builDong + "B" + builHo + "@" + SIGNAL_SERVER_URL;

                    body2.setBuilDong(tvDong.getText().toString().trim());
                    body2.setBuilHo(tvHo.getText().toString().trim());
                    body2.setSerialCode(senderSerialCode);
                    body2.setSender("rtc:" + body2.getSeSerialCode() + "@" + SIGNAL_SERVER_URL);
                    body2.setReceiver("rtc:" + body2.getBuilDong() + "B" + body2.getBuilHo() + "U@" + SIGNAL_SERVER_URL);


                    String jsonString1 = body1.toJson();
                    String jsonString2 = body2.toJson();
//                    WebSocketService.sendWebSocketMessage(jsonString1); // 잠시 주석
                    WebSocketService.sendWebSocketMessage(jsonString2);
                    dialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    // ------------------------------------------------------
    // 통화 받기 버튼 (예: 수신 측)
    // ------------------------------------------------------
    @OnClick(R.id.tv_connect_call_btn)
    void onConnectCallBtnClick() {
        if (callStatus == CALL_STATUS_INCOMING) {
            cancelCallTimeoutHandler();
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            if (!isRemoteViewOn) {
                startCallAndReleaseResources();
            }

            if (gayoAppUserBody == null) { // 경비실기 또는 월패드
                WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                        "accept",
                        recipientSerialCode,
                        senderSerialCode,
                        currentRoomId,
                        currentClientId
                );

                if (!currentCallDevice.isEmpty()) {
                    body.setCallDevice(currentCallDevice);
                    body.setLobbyPhoneSerialCode(senderSerialCode); // 삭제예정
                    body.setGuardSerialCode(recipientSerialCode); // 삭제예정

                }
                WebSocketService.sendWebSocketMessage(body.toJson());

            } else { // gayo app // 삭제예정 통합되니까
                WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                        "gayo-accept",
                        recipientSerialCode,
                        senderSerialCode,
                        currentRoomId,
                        currentClientId,
                        gayoAppUserBody.getBuilCode(),
                        gayoAppUserBody.getBuilDong(),
                        gayoAppUserBody.getBuilHo()
                );
                body.setUserIdx(gayoAppUserBody.getUserIdx());
                WebSocketService.sendWebSocketMessage(body.toJson());

            }
        }
    }

    // ------------------------------------------------------
    // 통화 거절 버튼 (예: 수신 측)
    // ------------------------------------------------------
    @OnClick(R.id.tv_reject_call_btn)
    void onRejectCallBtnClick() {
        if (callStatus == CALL_STATUS_INCOMING) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

            if (mainHandler != null && callTimerRunnable != null) {
                mainHandler.removeCallbacks(callTimerRunnable);
            }

            callStatus = CALL_STATUS_REJECTED;
            if (gayoAppUserBody == null) {
                WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                        "invite-away",
                        recipientSerialCode,
                        senderSerialCode,
                        currentRoomId,
                        currentClientId
                );

                if (!currentCallDevice.isEmpty()) {
                    body.setCallDevice(currentCallDevice);
                    body.setLobbyPhoneSerialCode(senderSerialCode); // 삭제예정
                    body.setGuardSerialCode(recipientSerialCode); // 삭제예정

                }

                WebSocketService.sendWebSocketMessage(body.toJson());
            } else {
                WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                        "gayo-invite-away",
                        recipientSerialCode,
                        senderSerialCode,
                        currentRoomId,
                        currentClientId,
                        gayoAppUserBody.getBuilCode(),
                        gayoAppUserBody.getBuilDong(),
                        gayoAppUserBody.getBuilHo()
                );
                body.setUserIdx(gayoAppUserBody.getUserIdx());

                WebSocketService.sendWebSocketMessage(body.toJson());
            }

        }
    }

    // ------------------------------------------------------
    // 통화 종료 버튼 (예: 수신/발신 측)
    // ------------------------------------------------------
    @OnClick(R.id.lay_hang_up_btn)
    void onHangUpBtnClick() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

        if (callStatus != CALL_STATUS_IDLE) {
            if (callStatus < 3) { // 통화 연결 전 끊기
                if (gayoAppUserBody == null) {
                    if (isSender) {
                        hangUpCall("bye");
                    }
                } else {
                    hangUpCall("gayo-bye");
                }
            } else { // 통화 연결 중 끊기
                if (gayoAppUserBody == null) {
                    hangUpCall("call-bye");
                } else {
                    hangUpCall("gayo-call-bye");

                }
            }
        }

    }

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        if (callStatus == CALL_STATUS_INCOMING || callStatus == CALL_STATUS_ACTIVE) {
            showPopupDialog(null, "이 페이지에서 나가시면\n현재 통화가 종료됩니다.", "취 소", "확 인");
        } else {
            callStatus = CALL_STATUS_IDLE;
            disconnectWebSocket();
            finish();
        }
    }

    @OnClick(R.id.lay_add_contact_btn)
    void clickAddContactBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

        if (callStatus == CALL_STATUS_INCOMING || callStatus == CALL_STATUS_ACTIVE) {
            showPopupDialog(null, "이 페이지에서 나가시면\n현재 통화가 종료됩니다.", "취 소", "확 인");
        } else {
            Intent intent = new Intent(CallActivity.this, AddContactActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_CONTACT);
        }
    }


    // ------------------------------------------------------
    // Activity LifeCycle
    // ------------------------------------------------------


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 여기서 새로 들어온 인텐트 데이터 처리
        // (예: 다시 한 번 "전화 요청" 데이터가 왔을 때 UI 갱신 등)

        if (intent != null && intent.getStringExtra("jsonText") != null) {
            String msg = intent.getStringExtra("jsonText");

            try {
                Log.d(TAG, msg);
                handleIntent(msg);

            } catch (JSONException e) {
                Log.e(TAG, "JSON 파싱 오류: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CODE_ADD_CONTACT) {
            finish();
        } else if (resultCode == RESULT_CODE_ADD_CONTACT_REFRESH) {
            if (mPrefGlobal.getContactsList() != null) {
                facilityContactsButtonListAdapter.setItems(mPrefGlobal.getContactsList());
                rvFacilityList.setAdapter(facilityContactsButtonListAdapter);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endCallAndReleaseResources();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_call;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        TAG = "WebSocketService CallActivity";
        popupDialog.onCallBack(new PopupDialog.DialogCallback() {
            @Override
            public void onFinish() {
                Log.e(TAG, "onFinish");
            }

            @Override
            public void onNextStep() {
                Log.e(TAG, "onNextStep");
                onHangUpBtnClick();
                disconnectWebSocket();
            }
        });

        // 볼륨 관련
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);
        sliderUiSet(slidrVolume);


        if (getIntent() != null && getIntent().getStringExtra("jsonText") != null) {
            String msg = getIntent().getStringExtra("jsonText");

            try {
                Log.d(TAG, msg);

                handleIntent(msg);

            } catch (JSONException e) {
                Log.e(TAG, "JSON 파싱 오류: " + e.getMessage());
            }
        }

        if (mPrefGlobal.getContactsList() != null) {
            facilityContactsButtonListAdapter = new FacilityContactsButtonListAdapter(CallActivity.this, R.layout.item_facility_list, new FacilityContactsButtonListAdapter.OnContactsClickListener() {
                @Override
                public void clickContacts(ResponseDataFormat.FacilityContactListBody.FacilityContactInfo facilityContactInfo) {
                    // call code
                    if (callStatus == CALL_STATUS_IDLE) {
                        isSender = true;
                        laySenderAptInfo.setVisibility(View.GONE);
                        tvFacilityName.setVisibility(View.VISIBLE);
                        tvAptDongNumber.setText("");
                        tvAptHoNumber.setText("");
                        dongNumber = "";
                        currentCallDevice = "guard";
                        hoNumber = "";
                        tvAptDong.setVisibility(View.INVISIBLE);
                        tvAptHo.setVisibility(View.INVISIBLE);
                        isDongFinal = false;
                        isHoFinal = false;

                        // 예: 서버에 'invite' 전송
                        recipientSerialCode = facilityContactInfo.getConnSerialCode();
                        senderSerialCode = Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode();
                        tvFacilityInfo.setText(facilityContactInfo.getFacilityName());
                        tvFacilityName.setText(facilityContactInfo.getFacilityName());
                        // 임의로 roomId 생성
                        int roomId = (int) (Math.random() * 90000000) + 10000000;
                        currentRoomId = String.valueOf(roomId);

                        WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                                "invite",
                                recipientSerialCode, // 수신자
                                senderSerialCode     // 발신자
                        );
                        body.setRoomId(currentRoomId);
                        body.setCallDevice("guard"); // 연락처 리스트들은 경비실기 니깐

                        Intent serviceIntent = new Intent(getApplicationContext(), WebSocketService.class);
                        serviceIntent.putExtra("deviceBody", Gayo_SharedPreferences.PrefDeviceData.prefItem);
                        serviceIntent.putExtra("authorization", mPrefGlobal.getAuthorization());
                        serviceIntent.putExtra("websocketMessage", body.toString());

                        startService(serviceIntent);

                    }

                }
            });

            facilityContactsButtonListAdapter.setItems(mPrefGlobal.getContactsList());
            rvFacilityList.setAdapter(facilityContactsButtonListAdapter);
        }

    }

    private int getCurrentSlidrVolume() {

        return (int) slidrVolume.getCurrentValue();
    }

    // ------------------------------------------------------
    // WebSocket 메시지 수신/처리
    // ------------------------------------------------------
    private void handleIntent(String msg) throws JSONException {
        JSONObject json = new JSONObject(msg);
        String method = json.optString("method", "");
        String device = json.optString("device", "");
        String sdp = json.optString("sdp", "");
        String code = json.optString("code", "");
        String message = json.optString("message", "");
        String roomId = json.optString("roomid", "");
        String seSerialCode = json.optString("seSerialCode", "");
        String clientId = json.optString("clientid", "");
        String id = json.optString("id", "");
        int label = json.optInt("label", -1);
        String candidate = json.optString("candidate", "");
        String buildingCode = json.optString("builCode", "");
        String buildingDong = json.optString("builDong", "");
        String buildingHo = json.optString("builHo", "");
        String userIdx = json.optString("userIdx", "");
        String canSendVideo = json.optString("canSendVideo", "N");
        String callDevice = json.optString("callDevice", "N");
        String lobbyPhoneDeviceName = json.optString("lobbyPhoneDeviceName", "");

        if (!method.isEmpty()) {
            switch (method.toLowerCase()) {
                case "ok": // 서버로 부터 응답
                    if (isSender) {
                        if (!clientId.isEmpty()) {
                            callStatus = CALL_STATUS_INCOMING;
                            currentClientId = clientId;
                            changeViewCalling();
                        }
                    } else if (!isSender && callStatus == CALL_STATUS_REJECTED) {
                        stopRingMyBell();
                        callStatus = CALL_STATUS_IDLE;
                        currentRoomId = "";
                        currentClientId = "";
                        currentCallDevice = "";
                        senderSerialCode = "";
                        recipientSerialCode = "";
                        isVideoCall = false;
                        isHangUpThePhone(""); // 수신자 쪽에서 바로 끊는 것임.
                    }
                    break;

                case "invite":
                    if (callStatus == CALL_STATUS_IDLE) {
                        gayoAppUserBody = null;
                        onInviteReceived(json);
                    } else {
                        //통화중이라서 전화 거절 메세지 보내는 부분 메서드가 정해지지 않았음!!!
                        // -> "invite-away" 로 통일~

                        WebSocketService.WebSocketBody webSocketBody;

                        webSocketBody = new WebSocketService.WebSocketBody(
                                "invite-away",
                                Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceNetworkBody().getSerialCode(),
                                seSerialCode,
                                roomId,
                                clientId
                        );

                        if (!currentCallDevice.isEmpty()) {
                            webSocketBody.setCallDevice(currentCallDevice);
                            webSocketBody.setLobbyPhoneSerialCode(senderSerialCode); // 삭제예정
                            webSocketBody.setGuardSerialCode(recipientSerialCode); // 삭제예정

                        } else {
                            if (!callDevice.isEmpty()) {
                                webSocketBody.setCallDevice(callDevice);
                            }
                        }
                        String jsonString = webSocketBody.toJson();
                        WebSocketService.sendWebSocketMessage(jsonString);
                    }
                    break;

                case "accept":
                    if (isSender) {
                        if (!isRemoteViewOn) {
                            startCallAndReleaseResources();
                        }
                        sendAcceptAck();
                    } else {
                        if (device.equals("signalServer") && code.equals("100")) {
                            // peerConnection 생성
                            runOnUiThread(() -> changeViewCallingStart());

                            callStatus = CALL_STATUS_CONNECTING;
                        }
                    }
                    createPeerConnection();

                    break;

                case "accept-ack":
                    callStatus = CALL_STATUS_ACCEPT_CALL;
                    isVideoCall = canSendVideo.equals("Y");

                    if (!isRemoteViewOn) {
                        startCallAndReleaseResources();
                    }
                    if (!isSender) {
                        if (peerConnection == null) {
                            createPeerConnection();
                        }
                        startCallOffer();
                    }
                    break;
                case "gayo-accept-ack":
//                    startCallAndReleaseResources();
                    if (!isSender) {
                        if (peerConnection == null) {
                            createPeerConnection();
                        }
                        startCallOffer();
                    }
                    break;

                case "offer":

                    if (!sdp.isEmpty()) {
                        receivedSdp = sdp;
                        createAnswer(); // (발신자)
                    }

                    if (device.equals("signalServer") && code.equals("100")) {
                        runOnUiThread(() -> changeViewCallingStart());

                    }

                    break;

                case "answer":
                    // “수신자”가 만든 answer를 이 쪽(수신자= false)이 아니라
                    // 원래는 caller가 answer를 만드는 게 일반적이지만
                    // 여기서는 반대 구조이므로, offer 만든 기기가 answer를 수신
                    if (!sdp.isEmpty()) {
                        // 이 기기는 “offer를 보낸 기기” → “answer를 받아 setRemoteDescription”
                        receivedSdp = sdp;
                        applyRemoteAnswer(sdp);
                    }

                    if (device.equals("signalServer") && code.equals("100")) {
                        // peerConnection 생성
                        runOnUiThread(() -> changeViewCallingStart());

                        callStatus = CALL_STATUS_CONNECTING;
                    }
                    break;

                case "candidate":
                    if (!candidate.isEmpty()) {
                        if (!id.isEmpty() && label != -1) {
                            onRemoteIceCandidateReceived(id, label, candidate);

                        }
                    }
                    break;
                case "invite-away":  // 상대방이 받지 않음....(부재중)
                case "no-answer":
                case "gayo-no-answer":
                case "invite-calling":
                case "call-bye": //상대가 끊었음.
                case "bye": // 통화걸었다가 바로 끊음.
                    stopRingMyBell();
                    isHangUpThePhone(method);
                    break;
                //** 가요 앱으로 부터
                case "gayo-invite":
                    if (callStatus == -1) {
                        if (Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode().equals(buildingCode)) {
                            gayoAppUserBody = new GayoAppUserBody(buildingCode, buildingDong, buildingHo, userIdx);
                            onInviteReceived(json);
                        }
                    } else {
                        //통화중!!!!!
                    }
                    break;
                case "gayo-answer":
                    if (!sdp.isEmpty()) {
                        // 이 기기는 “offer를 보낸 기기” → “answer를 받아 setRemoteDescription”
                        receivedSdp = sdp;
                        applyRemoteAnswer(sdp);
                    }
                    break;
                case "gayo-invite-ack":
                    break;
                case "gayo-bye": // 가요에서 전화 걸었다가 가요에서 전화 종료.
                case "gayo-call-bye":
                    isHangUpThePhone(method);
                    break;

            }
        } else {
            Log.d(TAG, "method is empty !!!!");
            if (message.isEmpty()) {
                callStatus = CALL_STATUS_IDLE;
                isHangUpThePhone("");
                Log.d(TAG, "error");
            } else {
                if (message.equals(DEVICE_NOT_CONNECTED)) {
                    isSender = false;
                    recipientSerialCode = "";
                    senderSerialCode = "";
                    currentClientId = "";
                    currentRoomId = "";
                    currentCallDevice = "";
                    laySenderAptInfo.setVisibility(View.VISIBLE);
                    tvFacilityName.setVisibility(View.GONE);

                    Toast.makeText(CallActivity.this, "해당 기기와 연결이 되지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void sendAcceptAck() {
        callStatus = CALL_STATUS_CONNECTING;
        WebSocketService.WebSocketBody webSocketBody = new WebSocketService.WebSocketBody(
                "accept-ack",
                recipientSerialCode,
                senderSerialCode,
                currentRoomId,
                currentClientId
        );
        if (!currentCallDevice.isEmpty()) {
            webSocketBody.setCallDevice(currentCallDevice);
            webSocketBody.setLobbyPhoneSerialCode(senderSerialCode); // 삭제예정
            webSocketBody.setGuardSerialCode(recipientSerialCode); // 삭제예정
        }
        if (isVideoCall) {
            remoteView.setVisibility(View.VISIBLE);
        } else {
            remoteView.setVisibility(View.GONE);
        }
        String jsonString = webSocketBody.toJson();
        WebSocketService.sendWebSocketMessage(jsonString);
    }

    // ------------------------------------------------------
    // 수신자가 초대(invite) 받았을 때 → Offer 생성 플로우
    // ------------------------------------------------------
    private void onInviteReceived(JSONObject json) {

        String method = json.optString("method", "");
        String roomId = json.optString("roomid", "");
        String seSerialCode = json.optString("seSerialCode", "");
        String lobbySerialCode = json.optString("serialCode", "");
        String clientId = json.optString("clientid", "");
        String buildingDong = json.optString("builDong", "");
        String buildingHo = json.optString("builHo", "");
        String callDevice = json.optString("callDevice", "");
        String lobbyPhoneDeviceName = json.optString("lobbyPhoneDeviceName", "");

        // 이 기기 = 수신자(Offer 만드는 쪽)
        callStatus = CALL_STATUS_INCOMING;
        currentRoomId = roomId;
        currentClientId = clientId;
        currentCallDevice = callDevice;
        if (!seSerialCode.isEmpty()) {
            senderSerialCode = seSerialCode;
        } else {
            senderSerialCode = lobbySerialCode;
        }
        recipientSerialCode = Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode();
        // UI 갱신
        layCarNumberInfo.setVisibility(View.GONE);
        boolean isContacted = false;

        tvAptDongNumber.setText("");
        tvAptHoNumber.setText("");
        dongNumber = "";
        hoNumber = "";
        isDongFinal = false;
        isHoFinal = false;
        tvAptDong.setVisibility(View.INVISIBLE);
        tvAptHo.setVisibility(View.INVISIBLE);

        switch (currentCallDevice) {
            case "guard":
                for (int i = 0; i < mPrefGlobal.getContactsList().size(); i++) {
                    if (seSerialCode.equals(mPrefGlobal.getContactsList().get(i).getConnSerialCode())) {
                        tvFacilityName.setText(mPrefGlobal.getContactsList().get(i).getFacilityName());
                        isContacted = true;
                        break;
                    }
                }
                if (!isContacted) {
                    tvFacilityName.setText("미확인 기기");
                    tvFacilityInfo.setText("미확인 기기");
                }
                break;
            case "lobby":
                tvFacilityName.setText(lobbyPhoneDeviceName);
                tvFacilityInfo.setText(lobbyPhoneDeviceName);
                break;
            // TODO 가요앱은 뭘로 오는지 추가 해야함....

        }


        tvSenderAptDongNumber.setText(buildingDong);
        tvSenderAptHoNumber.setText(buildingHo);

        if (method.equals("invite")) {
            tvFacilityName.setVisibility(View.VISIBLE);
            laySenderAptInfo.setVisibility(View.GONE);
        } else {
            tvFacilityName.setVisibility(View.GONE);
            laySenderAptInfo.setVisibility(View.VISIBLE);

        }

        //20초 이내에 안받으면 다시
        mainHandler.postDelayed(callTimerRunnable = () -> {
            if (callStatus == CALL_STATUS_INCOMING) {
                stopRingMyBell();

                Log.e(TAG, "전화를 받지 않아서 끊어짐..!");
                hangUpCall("no-answer");
            }
        }, 20_000);

        startRingMyBell();
        changeViewCalling();


        WebSocketService.WebSocketBody webSocketBody;

        webSocketBody = new WebSocketService.WebSocketBody(
                method.equals("gayo-invite") ? "gayo-invite-ack" : "invite-ack",
                recipientSerialCode,
                senderSerialCode,
                currentRoomId,
                currentClientId
        );

        if (method.equals("gayo-invite")) {
            recipientSerialCode = Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode();
            webSocketBody.setBuilDong(gayoAppUserBody.getBuilDong());
            webSocketBody.setBuilHo(gayoAppUserBody.getBuilHo());
        }

        if (!currentCallDevice.isEmpty()) {
            webSocketBody.setCallDevice(currentCallDevice);
            webSocketBody.setLobbyPhoneSerialCode(senderSerialCode); // 삭제예정
            webSocketBody.setGuardSerialCode(recipientSerialCode); // 삭제예정
        }

        WebSocketService.sendWebSocketMessage(webSocketBody.toJson());

        startCallTimeoutHandler();
    }

    private Ringtone ringtone;

    public void startRingMyBell() {

        // 시스템 기본 Ringtone

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(CallActivity.this, ringtoneUri);
        // 이미 재생 중이 아니면 재생 시작
        if (ringtone != null && !ringtone.isPlaying()) {
            ringtone.play();
        }

        Log.e(TAG, "벨이 울리고 있습니다~!~!~!~!!~~!~!~~!!~ ");

    }

    public void stopRingMyBell() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    // ------------------------------------------------------
    // 수신자 쪽에서 Offer 생성
    // ------------------------------------------------------
    public void startCallOffer() {
        // Offer 생성 - (수신자)
        Log.d(TAG, "startCallOffer()");

        peerConnection.createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "Offer SDP:\n" + sessionDescription.description);
                // 로컬 SDP 설정
                peerConnection.setLocalDescription(new SimpleSdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        Log.d(TAG, "Local Offer set 성공");
                    }
                }, sessionDescription);

                // 이 Offer를 서버로 전송
                String offerSdp = sessionDescription.description;

                if (gayoAppUserBody == null) {
                    WebSocketService.WebSocketBody webSocketBody = new WebSocketService.WebSocketBody(
                            "offer",
                            recipientSerialCode,
                            senderSerialCode,
                            currentRoomId,
                            currentClientId,
                            Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode(), tvAptDongNumber.getText().toString(), tvAptHoNumber.getText().toString(), // 필요 파라미터
                            offerSdp
                    );
                    if (!currentCallDevice.isEmpty()) {
                        webSocketBody.setCallDevice(currentCallDevice);
                        webSocketBody.setLobbyPhoneSerialCode(senderSerialCode); // 삭제예정
                        webSocketBody.setGuardSerialCode(recipientSerialCode); // 삭제예정
                    }
                    WebSocketService.sendWebSocketMessage(webSocketBody.toJson());
                } else {
                    WebSocketService.WebSocketBody webSocketBody = new WebSocketService.WebSocketBody(
                            "gayo-offer",
                            recipientSerialCode,
                            senderSerialCode,
                            currentRoomId,
                            currentClientId,
                            gayoAppUserBody.getBuilCode(), gayoAppUserBody.getBuilDong(), gayoAppUserBody.getBuilHo(),
                            offerSdp
                    );

                    webSocketBody.setUserIdx(gayoAppUserBody.getUserIdx());
                    if (!currentCallDevice.isEmpty()) {
                        webSocketBody.setCallDevice(currentCallDevice);
                        webSocketBody.setLobbyPhoneSerialCode(senderSerialCode); // 삭제예정
                        webSocketBody.setGuardSerialCode(recipientSerialCode); // 삭제예정

                    }
                    WebSocketService.sendWebSocketMessage(webSocketBody.toJson());
                }

            }

            @Override
            public void onCreateFailure(String s) {
            }

            @Override
            public void onSetSuccess() {
            }

            @Override
            public void onSetFailure(String s) {
            }
        }, sdpMediaConstraints);
    }

    // ------------------------------------------------------
    // 발신자 쪽에서 Answer 생성
    // ------------------------------------------------------

    /**
     * Answer 생성 - (발신자)
     **/
    public void createAnswer() {

        // 1) 우선 원격 Offer를 setRemoteDescription
        SessionDescription remoteOffer =
                new SessionDescription(SessionDescription.Type.OFFER, receivedSdp);

        peerConnection.setRemoteDescription(new SdpObserver() {
            @Override
            public void onSetSuccess() {
                // 이제 local answer 만들기
                createLocalAnswer();
            }

            @Override
            public void onCreateSuccess(SessionDescription sdp) {
            }

            @Override
            public void onCreateFailure(String s) {
            }

            @Override
            public void onSetFailure(String s) {
                Log.e(TAG, "setRemoteDescription 실패: " + s);
            }
        }, remoteOffer);
    }

    private void createLocalAnswer() {

        // Answer 생성
        peerConnection.createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                // 로컬 SDP 설정
                peerConnection.setLocalDescription(new SimpleSdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        Log.d(TAG, "setLocalDescription(Answer) 성공");
                    }

                    @Override
                    public void onSetFailure(String error) {
                        Log.e(TAG, "setLocalDescription(Answer) 실패: " + error);
                    }
                }, sessionDescription);
                Log.d(TAG, "Answer SDP:\n" + sessionDescription.description);

                String answerSdp = sessionDescription.description;

                // (5) WebSocket 전송
                WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                        "answer",
                        recipientSerialCode,   // 수신자
                        senderSerialCode,      // 발신자
                        currentRoomId,
                        currentClientId,
                        Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode(),
                        "", // dong
                        "", // ho
                        answerSdp
                );
                WebSocketService.sendWebSocketMessage(body.toJson());

                runOnUiThread(() -> changeViewCallingStart());
            }

            @Override
            public void onSetSuccess() {
            }

            @Override
            public void onCreateFailure(String error) {
                Log.e(TAG, "Answer 생성 실패: " + error);
            }

            @Override
            public void onSetFailure(String error) {
            }
        }, sdpMediaConstraints);
    }

    // ------------------------------------------------------
    // “offer”를 보낸 쪽(수신자)이 “answer”를 받았을 때 적용
    // ------------------------------------------------------
    private void applyRemoteAnswer(String answerSdp) {
        SessionDescription remoteAnswer = new SessionDescription(SessionDescription.Type.ANSWER, answerSdp);
        peerConnection.setRemoteDescription(new SimpleSdpObserver() {
            @Override
            public void onSetSuccess() {
                Log.d(TAG, "Remote Answer set 성공");
                // 이제 서로 영상/오디오 교환 가능
            }

            @Override
            public void onSetFailure(String s) {
                Log.e(TAG, "Remote Answer set 실패: " + s);
            }
        }, remoteAnswer);
    }

    // ------------------------------------------------------
    // PeerConnection 생성 & 로컬 트랙 추가
    // ------------------------------------------------------

    private void createPeerConnection() {

        Log.e(TAG, "createPeerConnection()");

        // 1) ICE 서버 설정
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(
                PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
                        .createIceServer()
        );

        List<String> mediaStreamList = Collections.singletonList("ARDAMS");

        // 2) RTCConfiguration
        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;

        // 3) PeerConnection 생성
        peerConnection = peerConnectionFactory.createPeerConnection(
                rtcConfig,
                new PeerConnection.Observer() {
                    @Override
                    public void onSignalingChange(PeerConnection.SignalingState newState) {
                    }

                    @Override
                    public void onIceConnectionChange(PeerConnection.IceConnectionState newState) {
                        Log.d(TAG, "ICE Connection State: " + newState);
                        switch (newState) {
                            case DISCONNECTED:
                                callStatus = CALL_STATUS_IDLE;
                                stopCallTimer();
                                break;
                            case FAILED:
                                runOnUiThread(() -> isHangUpThePhone("error"));
                                break;
                            case CONNECTED:
                                if (callStatus != CALL_STATUS_ACTIVE) {
                                    WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                                            "call-start",
                                            recipientSerialCode,   // 수신자
                                            senderSerialCode,      // 발신자
                                            currentRoomId,
                                            currentClientId
                                    );
                                    body.setCallDevice(currentCallDevice);
                                    WebSocketService.sendWebSocketMessage(body.toJson());
                                    stopRingMyBell();
                                    startCallTimer();
                                }
                                callStatus = CALL_STATUS_ACTIVE;
                                runOnUiThread(() -> tvCallMessage.setText("통화중 입니다."));
                                break;
                            case CHECKING:
                                runOnUiThread(() -> tvCallMessage.setText("통화 연결중 입니다."));
                                callStatus = CALL_STATUS_CONNECTING;
                                break;
                        }


                    }

                    @Override
                    public void onIceConnectionReceivingChange(boolean b) {
                    }

                    @Override
                    public void onIceGatheringChange(PeerConnection.IceGatheringState newState) {
                    }

                    // ICE Candidate가 생길 때마다 시그널 서버로 전송
                    @Override
                    public void onIceCandidate(IceCandidate candidate) {
                        WebSocketService.WebSocketBody body;
                        if (gayoAppUserBody == null) {
                            body = new WebSocketService.WebSocketBody(
                                    "candidate",
                                    isSender ? recipientSerialCode : "",
                                    isSender ? "" : senderSerialCode,
                                    currentRoomId,
                                    currentClientId,
                                    candidate.sdpMLineIndex,
                                    candidate.sdpMid,
                                    candidate.sdp
                            );
                        } else {
                            body = new WebSocketService.WebSocketBody(
                                    "gayo-candidate",
                                    recipientSerialCode,
                                    isSender ? "" : senderSerialCode,
                                    currentRoomId,
                                    currentClientId,
                                    candidate.sdpMLineIndex,
                                    candidate.sdpMid,
                                    candidate.sdp
                            );

                            body.setUserIdx(gayoAppUserBody.getUserIdx());
                            body.setBuilDong(gayoAppUserBody.getBuilDong());
                            body.setBuilHo(gayoAppUserBody.getBuilHo());

                        }

                        if (!currentCallDevice.isEmpty()) {
                            body.setCallDevice(currentCallDevice);
                            body.setLobbyPhoneSerialCode(senderSerialCode); // 삭제예정
                            body.setGuardSerialCode(recipientSerialCode); // 삭제예정
                        }

                        WebSocketService.sendWebSocketMessage(body.toJson());
                    }

                    @Override
                    public void onIceCandidatesRemoved(IceCandidate[] candidates) {
                    }

                    @Override
                    public void onAddStream(MediaStream mediaStream) {

                    }

                    @Override
                    public void onRemoveStream(MediaStream mediaStream) {
                    }

                    @Override
                    public void onDataChannel(DataChannel dataChannel) {
                    }

                    @Override
                    public void onRenegotiationNeeded() {
                    }

                    @Override
                    public void onTrack(RtpTransceiver transceiver) {
                        Log.i(TAG, "onTrack: receiver track => " + transceiver.getReceiver().track().kind());

                        if (transceiver.getReceiver().track() instanceof VideoTrack) {
                            // 비디오 처리
                            // 로비폰과의 통화에서만 영상 통화를 한다.
                            if (isVideoCall) {
                                VideoTrack remoteVideoTrack = (VideoTrack) transceiver.getReceiver().track();
                                remoteVideoTrack.setEnabled(true);
                                runOnUiThread(() -> remoteVideoTrack.addSink(remoteView));
                            }

                        } else if (transceiver.getReceiver().track() instanceof AudioTrack) {
                            // 오디오 처리
                            AudioTrack remoteAudioTrack = (AudioTrack) transceiver.getReceiver().track();
                            remoteAudioTrack.setEnabled(true);
                        } else {
                            Log.w(TAG, "Unknown track type: " + transceiver.getReceiver().track().kind());
                        }
                    }
                }
        );

        if (peerConnection == null) {
            Log.e(TAG, "peerConnection create failed!");

            return;
        }

        if (localAudioTrack != null) {
            peerConnection.addTrack(localAudioTrack, mediaStreamList);
        } else {
            Log.e(TAG, "localAudioTrack이 null이라 추가 실패");
        }

        //경비 실기는 영상을 받기만 한다.
        RtpTransceiver.RtpTransceiverInit recvOnlyInit =
                new RtpTransceiver.RtpTransceiverInit(
                        RtpTransceiver.RtpTransceiverDirection.RECV_ONLY
                );
        peerConnection.addTransceiver(
                MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO,
                recvOnlyInit
        );
// 이건 예전 영상통화 테스트 할 때
//        if (localVideoTrack != null) {
//            peerConnection.addTrack(localVideoTrack, mediaStreamList);
//        } else {
//            Log.e(TAG, "localVideoTrack이 null이라 추가 실패");
//        }
    }

    // ------------------------------------------------------
    // PeerConnectionFactory 초기화
    // ------------------------------------------------------

    private void initPeerConnectionFactory() {
        // 전역 WebRTC 초기화
        PeerConnectionFactory.InitializationOptions initOptions =
                PeerConnectionFactory.InitializationOptions.builder(this)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initOptions);

        // (HW 가속 코덱 지원)
        DefaultVideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(
                rootEglBase.getEglBaseContext(), /* enableIntelVp8Encoder */ true, /* enableH264HighProfile */ true
        );
        DefaultVideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());

        // Factory 옵션
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        // 최종 Factory 생성
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();

        createLocalMediaTracks();

    }

    // ------------------------------------------------------
    // 로컬 오디오/비디오 트랙 생성
    // ------------------------------------------------------

    private void createLocalMediaTracks() {
        Log.e(TAG, "createLocalMediaTracks()");

        sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("offerToReceiveAudio", "true"));

        //오디오
        AudioSource audioSource = peerConnectionFactory.createAudioSource(sdpMediaConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack("AUDIO_TRACK_ID", audioSource);
        localAudioTrack.setEnabled(true);

        //비디오
//        videoCapturer = createCameraCapturer();
//        if (videoCapturer == null) {
//            Log.e(TAG, "videoCapturer is null!");
//            return;
//        }
//
//        VideoSource videoSource = peerConnectionFactory.createVideoSource(false);
//        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
//
//        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());
//
//        try {
//            videoCapturer.startCapture(640, 480, 30); // 임시 크기
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        localVideoTrack = peerConnectionFactory.createVideoTrack("VIDEO_TRACK_ID", videoSource);
//        localVideoTrack.setEnabled(true);

    }

    private VideoCapturer createCameraCapturer() {
        // Camera2Enumerator 예시
        Camera2Enumerator enumerator = new Camera2Enumerator(this);
        final String[] deviceNames = enumerator.getDeviceNames();

        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null);
            }
        }

        return null;
    }

    // ------------------------------------------------------
    // 통화 종료
    // ------------------------------------------------------
    public void
    hangUpCall(String method) {
        // 시그널 서버에 통화 종료 알림
        WebSocketService.WebSocketBody body;

        body = new WebSocketService.WebSocketBody(
                method,
                isSender ? recipientSerialCode : "",
                isSender ? "" : senderSerialCode,
                currentRoomId,
                currentClientId
        );

        if (method.equals("call-bye")) {
            body.setReceiver("rtc:" + recipientSerialCode + "@" + SIGNAL_SERVER_URL);
        }

        if (method.equals("gayo-call-bye")) {
            body.setReSerialCode(recipientSerialCode);
            body.setUserIdx(gayoAppUserBody.getUserIdx());
            body.setBuilDong(gayoAppUserBody.getBuilDong());
            body.setBuilHo(gayoAppUserBody.getBuilHo());
        }

        if (!currentCallDevice.isEmpty()) {
            body.setCallDevice(currentCallDevice);
            body.setLobbyPhoneSerialCode(senderSerialCode); // 삭제예정
            body.setGuardSerialCode(recipientSerialCode); // 삭제예정

        }

        if (!receivedSdp.isEmpty()) {
            body.setSdp(receivedSdp);
        }
        WebSocketService.sendWebSocketMessage(body.toJson());
        // UI 갱신 및 WebRTC 초기화 등
        isHangUpThePhone(method);
    }

    // ------------------------------------------------------
    // 콜백/헬퍼
    // ------------------------------------------------------
    private void changeViewCalling() {
        layCallView.setVisibility(View.VISIBLE);
        layClock.setVisibility(View.GONE);
        if (isSender) {
            layCallBtn.setVisibility(View.INVISIBLE);
            layHangUpBtn.setVisibility(View.VISIBLE);
            tvCallMessage.setText("(으)로 통화를 걸고 있습니다.");
        } else {
            layCallReceptionSelect.setVisibility(View.VISIBLE);
            layButton.setAlpha(0.3F);
            tvCallMessage.setText("(으)로 부터 통화가 왔습니다.");
        }

    }

    //전화 연결 완료...
    private void changeViewCallingStart() {
        Log.e(TAG, "changeViewCallingStart() 통화 시작 UI 변경!");

        if (isVideoCall) {
            remoteView.setVisibility(View.VISIBLE);
            layCallInfo.setVisibility(View.GONE);
            tvCallMessage.setVisibility(View.GONE);

        } else {
            remoteView.setVisibility(View.GONE);
            layCallInfo.setVisibility(View.VISIBLE);
            tvCallMessage.setVisibility(View.VISIBLE);
//            tvCallMessage.setText("통화중 입니다."); ->> ICE CONNECTION 에서 UI 처리
        }

        layCallTime.setVisibility(View.VISIBLE);
        tvCallTime.setVisibility(View.VISIBLE);
        layButton.setAlpha(1F);
        layCallReceptionSelect.setVisibility(View.GONE);
        layHangUpBtn.setVisibility(View.VISIBLE);

//TODO 여기 세대 호출인지 앱통화인지 관리실 통화인 지에 따라 tvFacilityInfo or lay_dong_ho_info 중 setVisibility(View.VISIBLE);
//        tvFacilityInfo.setVisibility(View.VISIBLE);
//        layCallInfo.setVisibility(View.GONE);
        //** 현재 통화중인 세대 ? 또는 시설 명 보이게 끔하기 오른쪽뷰에

    }


    private void isHangUpThePhone(String method) {
        Log.e(TAG, "isHangUpThePhone(" + method + ")");


        // 통화 상태 정리
        isSender = false;
        currentRoomId = "";
        currentClientId = "";
        currentCallDevice = "";
        senderSerialCode = "";
        recipientSerialCode = "";
        receivedSdp = "";
        carNumber = "";
        gayoAppUserBody = null;
        isVideoCall = false;
        isRemoteViewOn = false;
        endCallAndReleaseResources();

        // UI 복원 등
        changeViewIdle(method);
    }

    private void changeViewIdle(String method) {

        Log.e(TAG, "changeViewIdle(" + method + ")");

        // 통화 종료 UI
        remoteView.setVisibility(View.GONE);
        layCallInfo.setVisibility(View.VISIBLE);
        layCallView.setVisibility(View.VISIBLE);
        layButton.setAlpha(0.3F);

        if (callStatus > 1) { // 통화 요청 수락 후
            layCallTime.setVisibility(View.VISIBLE);
            tvCallTime.setVisibility(View.VISIBLE);
        }
//        if(tvFacilityName.getVisibility() == View.VISIBLE)
        tvCallMessage.setVisibility(View.VISIBLE);

        callStatus = CALL_STATUS_IDLE;

        switch (method) {
            case "invite-away": //부재중
            case "no-answer":
            case "gayo-no-answer":
            case "invite-norespone":
                layCallReceptionSelect.setVisibility(View.GONE);
                tvCallMessage.setText("과(와) 연결이 되지 않습니다.");
                mainHandler.postDelayed(() -> changeViewReset(), 2000);
                break;
            case "invite-calling":
                tvCallMessage.setText("이(가) 통화중 입니다.");
                mainHandler.postDelayed(() -> changeViewReset(), 2000);
                break;
            case "gayo-bye":
            case "call-bye":
            case "bye":
            case "gayo-call-bye":
                tvCallMessage.setText("과(와) 통화가 종료 되었습니다.");
                // 2초 뒤 changeViewReset 스케줄
                mainHandler.postDelayed(this::changeViewReset, 2000);

                break;
            default:
                changeViewReset();
                break;

        }

    }

    private void changeViewReset() {
        layCallBtn.setVisibility(View.VISIBLE);
        layCallInfo.setVisibility(View.VISIBLE);
        layHangUpBtn.setVisibility(View.GONE);
        remoteView.setVisibility(View.GONE);
        layCallView.setVisibility(View.GONE);
        tvFacilityInfo.setVisibility(View.GONE);
        layCallTime.setVisibility(View.GONE);
        tvCallTime.setVisibility(View.GONE);
        tvCallTime.setText("00:00");
        tvAptDongNumber.setText("");
        tvAptHoNumber.setText("");
        dongNumber = "";
        hoNumber = "";
        layCarNumberInfo.setVisibility(View.GONE);
        tvCarNumber.setText("");
        carNumber = "";
        isDongFinal = false;
        isHoFinal = false;
        layClock.setVisibility(View.VISIBLE);
        layCallReceptionSelect.setVisibility(View.GONE);
        layButton.setAlpha(1);
        tvFacilityName.setVisibility(View.VISIBLE);

        callStatus = CALL_STATUS_IDLE; // 혹시나 안됐을 수도 있어서 확실히
    }

    private void startCallAndReleaseResources() {
        isRemoteViewOn = true;
        // 1) OpenGL 초기화
        rootEglBase = EglBase.create();

        // 2) SurfaceViewRenderer 설정 (remoteView만 사용 예시)
        remoteView.init(rootEglBase.getEglBaseContext(), null);
        remoteView.setMirror(false);
        remoteView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        remoteView.setEnableHardwareScaler(false);

        // (선택) localView도 쓰려면 init
        // localView.init(rootEglBase.getEglBaseContext(), null);
        // localView.setMirror(true);
        // localView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

        // 3) PeerConnectionFactory 초기화 (코덱 팩토리 설정 포함 권장)
        initPeerConnectionFactory();

    }

    private void endCallAndReleaseResources() {
        if (localVideoTrack != null) {
            localVideoTrack.setEnabled(false);
            localVideoTrack.dispose();
            localVideoTrack = null;
        }

        if (localAudioTrack != null) {
            localAudioTrack.setEnabled(false);
            localAudioTrack.dispose();
            localAudioTrack = null;
        }

        if (videoCapturer != null) {
            try {
                videoCapturer.stopCapture();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            videoCapturer.dispose();
            videoCapturer = null;
        }

        if (remoteView != null) {
            remoteView.clearImage();
            remoteView.release();
        }

        if (peerConnection != null) {
            peerConnection.close();
            peerConnection.dispose();
            peerConnection = null;
        }

        if (peerConnectionFactory != null) {
            peerConnectionFactory.dispose();
            peerConnectionFactory = null;
        }

        if (rootEglBase != null) {
            rootEglBase.release();
            rootEglBase = null;
        }

        disconnectWebSocket();

    }


    // ------------------------------------------------------
    //  시그널 서버에서 수신한 ICE Candidate 적용
    // ------------------------------------------------------
    public void onRemoteIceCandidateReceived(String sdpMid, int sdpMLineIndex, String candidate) {
        IceCandidate iceCandidate = new IceCandidate(sdpMid, sdpMLineIndex, candidate);
        if (peerConnection != null) {
            peerConnection.addIceCandidate(iceCandidate);
        }
    }

    // ------------------------------------------------------
    // 간단한 SdpObserver 구현 예시
    // ------------------------------------------------------
    private static class SimpleSdpObserver implements SdpObserver {
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
        }

        @Override
        public void onSetSuccess() {
        }

        @Override
        public void onCreateFailure(String s) {
        }

        @Override
        public void onSetFailure(String s) {
        }
    }

    private void sliderUiSet(Slidr _slidr) {

        _slidr.setMax(6);
        _slidr.setMin(0);
        _slidr.setCurrentValue(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) + 1);

        _slidr.setListener(new Slidr.Listener() {
            @Override
            public void valueChanged(Slidr slidr, float newValue) {
                int newIntValue = (int) newValue;
                if (newIntValue <= 1) {
                    slidrVolume.setCurrentValue(1);
                    audioManager.setStreamVolume(
                            AudioManager.STREAM_VOICE_CALL,
                            0,
                            AudioManager.FLAG_VIBRATE
                    );
                } else {
                    if (newIntValue > 6) {
                        newIntValue = 6;
                    }
                    audioManager.setStreamVolume(
                            AudioManager.STREAM_VOICE_CALL,
                            newIntValue - 1,
                            AudioManager.FLAG_VIBRATE
                    );
                    slidrVolume.setCurrentValue(newIntValue);
                }
            }

            @Override
            public void bubbleClicked(Slidr slidr) {

            }
        });
        _slidr.setRegionTextFormatter(new Slidr.RegionTextFormatter() {
            @Override
            public String format(int region, float value) {
                return String.format("", (int) value);
            }
        });
    }

    public static class GayoAppUserBody {

        private String builCode;
        private String builDong;
        private String builHo;
        private String userIdx;

        public GayoAppUserBody(String builCode, String builDong, String builHo, String userIdx) {
            this.builCode = builCode;
            this.builDong = builDong;
            this.builHo = builHo;
            this.userIdx = userIdx;
        }

        public String getBuilCode() {
            return builCode;
        }

        public String getBuilDong() {
            return builDong;
        }

        public String getBuilHo() {
            return builHo;
        }

        public String getUserIdx() {
            return userIdx;
        }
    }
}
