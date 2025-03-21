package bluen.homein.gayo_security.activity.call;

import static bluen.homein.gayo_security.activity.addFacilityContact.AddContactActivity.RESULT_CODE_ADD_CONTACT;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.util.Log;
import android.view.View;
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
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.RtpTransceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.addFacilityContact.AddContactActivity;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.dialog.PopupDialog;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.service.WebSocketService;
import butterknife.BindView;
import butterknife.OnClick;

public class CallActivity extends BaseActivity {

    private static final int REQUEST_CODE_ADD_CONTACT = 1;

    @BindView(R.id.rv_facility_list)
    RecyclerView rvFacilityList;
    @BindView(R.id.lay_car_number_info)
    ConstraintLayout layCarNumberInfo;
    @BindView(R.id.lay_call_info)
    ConstraintLayout layCallInfo;
    @BindView(R.id.tv_car_number)
    TextView tvCarNumber;

    @BindView(R.id.lay_call_view)
    ConstraintLayout layCallView;
    @BindView(R.id.lay_button)
    ConstraintLayout layButton;

    @BindView(R.id.tv_call_message)
    TextView tvCallMessage;
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

    // WebRTC 관련 변수
    @BindView(R.id.remote_view)
    SurfaceViewRenderer remoteView;
    // @BindView(R.id.local_view)
    // SurfaceViewRenderer localView;  // 필요하다면..?
    private EglBase rootEglBase;
    private MediaConstraints sdpMediaConstraints;
    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;
    private VideoCapturer videoCapturer;

    // 통화 상태 상수
    public static final int CALL_STATUS_IDLE = 0;
    public static final int CALL_STATUS_INCOMING = 1;
    public static final int CALL_STATUS_REJECTED = 2;
    public static final int CALL_STATUS_CONNECTING = 3;
    public static final int CALL_STATUS_ACTIVE = 4;

    // 현재 상태
    private int callStatus = CALL_STATUS_IDLE;

    // 발신/수신 구분
    // isSender = true  => “전화를 거는 기기”(발신자) => Answer 생성
    // isSender = false => “전화를 받는 기기”(수신자) => Offer 생성
    private boolean isSender = false;

    // 기타
    private FacilityContactsButtonListAdapter facilityContactsButtonListAdapter;

    private String currentRoomId = "";
    private String currentClientId = "";
    private String receivedSdp = "";
    private String senderSerialCode = "";
    private String recipientSerialCode = "";
    boolean isWithGayoApp = false;

    private String dongNumber = "";
    private String hoNumber = "";
    private boolean isDongFinal = false;
    private boolean isHoFinal = false;

    @OnClick({
            R.id.tv_number_0_btn, R.id.tv_number_1_btn, R.id.tv_number_2_btn, R.id.tv_number_3_btn,
            R.id.tv_number_4_btn, R.id.tv_number_5_btn, R.id.tv_number_6_btn, R.id.tv_number_7_btn,
            R.id.tv_number_8_btn, R.id.tv_number_9_btn
    })
    void onNumberClick(TextView clickedView) {
        String digit = clickedView.getText().toString();
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

        // 아직 "동" 입력이 완료되지 않았다면, 동 번호에 숫자를 추가
        if (!isDongFinal) {
            // 동 번호가 아직 4자리가 안 채워졌을 때만 입력 가능
            if (dongNumber.length() < 4) {
                dongNumber += digit;
                tvAptDongNumber.setText(dongNumber);

                // 동 번호가 4자리가 되면 자동으로 동 TextView를 visible, 동 입력 확정
                if (dongNumber.length() == 4) {
                    tvAptDong.setVisibility(View.VISIBLE);
                    isDongFinal = true;
                }
            }
        }
        // "동" 입력이 끝난 상태라면 "호" 번호에 입력
        else if (!isHoFinal) {
            // 호 번호가 아직 4자리가 안 채워졌을 때만 입력 가능
            if (hoNumber.length() < 4) {
                hoNumber += digit;
                tvAptHoNumber.setText(hoNumber);

                // 호 번호가 4자리가 되면 자동으로 호 TextView를 visible, 호 입력 확정
                if (hoNumber.length() == 4) {
                    tvAptHo.setVisibility(View.VISIBLE);
                    isHoFinal = true;
                }
            }
        }
        // isDongFinal과 isHoFinal 모두 true인 경우(동/호 모두 입력 완료) 더 이상 입력되지 않음
    }

    @OnClick(R.id.tv_dong_btn)
    void onDongBtnClick() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        // 동 번호가 비어있지 않으면서 아직 확정되지 않은 상태인 경우만
        if (!dongNumber.isEmpty() && !isDongFinal) {
            tvAptDong.setVisibility(View.VISIBLE);
            isDongFinal = true;
        }
    }

    @OnClick(R.id.tv_ho_btn)
    void onHoBtnClick() {
        if (!hoNumber.isEmpty() && !isHoFinal) {
            tvAptHo.setVisibility(View.VISIBLE);
            isHoFinal = true;
        }
    }

    @OnClick(R.id.lay_delete_btn)
    void onDeleteClick() {
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
    }

    // ------------------------------------------------------
    // 통화 버튼 (예: 발신 측)
    // ------------------------------------------------------
    @OnClick(R.id.lay_call_btn)
    void onCallBtnClick() {
        Log.i(TAG, "onCallBtnClick()");
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

        isSender = true;

        // 예: 서버에 'invite' 전송
        recipientSerialCode = "testserialCode6"; // 임시
        senderSerialCode = serialCode;

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

    // ------------------------------------------------------
    // 통화 받기 버튼 (예: 수신 측)
    // ------------------------------------------------------
    @OnClick(R.id.tv_connect_call_btn)
    void onConnectCallBtnClick() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

        startCallAndReleaseResources();

        WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                "accept",
                recipientSerialCode,
                senderSerialCode,
                currentRoomId,
                currentClientId
        );

        WebSocketService.sendWebSocketMessage(body.toJson());

    }

    // ------------------------------------------------------
    // 통화 거절 버튼 (예: 수신 측)
    // ------------------------------------------------------
    @OnClick(R.id.tv_reject_call_btn)
    void onRejectCallBtnClick() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        callStatus = CALL_STATUS_REJECTED;
        WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                "invite-away",
                recipientSerialCode,
                senderSerialCode,
                currentRoomId,
                currentClientId
        );

        WebSocketService.sendWebSocketMessage(body.toJson());
    }

    // ------------------------------------------------------
    // 통화 종료 버튼 (예: 수신/발신 측)
    // ------------------------------------------------------
    @OnClick(R.id.lay_hang_up_btn)
    void onHangUpBtnClick() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        if (callStatus < 3) {
            hangUpCall("bye");
        } else {
            hangUpCall("call-bye"); // 통화 연결 중 끊기!
        }

    }

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        if (callStatus == CALL_STATUS_INCOMING || callStatus == CALL_STATUS_ACTIVE) {
            showPopupDialog("현재 통화중인데 나가면 통화 종료됨.", "확 인");
        } else {
            finish();
        }
    }

    @OnClick(R.id.lay_add_contact_btn)
    void clickAddContactBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

        if (callStatus == CALL_STATUS_INCOMING || callStatus == CALL_STATUS_ACTIVE) {
            showPopupDialog("현재 통화 중인데 나가면 통화 종료됨.", "확 인");
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
                onHangUpBtnClick();
                finish();
            }

            @Override
            public void onNextStep() {

            }
        });

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
                }
            });

            facilityContactsButtonListAdapter.setItems(mPrefGlobal.getContactsList());
            rvFacilityList.setAdapter(facilityContactsButtonListAdapter);
        }

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
        String roomId = json.optString("roomId", "");
        String seSerialCode = json.optString("seSerialCode", "");
        String clientId = json.optString("clientId", "");
        String id = json.optString("id", "");
        int label = json.optInt("label", -1);
        String candidate = json.optString("candidate", "");

        if (!method.isEmpty()) {
            switch (method) {
                case "OK": // 서버로 부터 응답
                    if (isSender && callStatus == CALL_STATUS_IDLE) {
                        if (!clientId.isEmpty()) {
                            callStatus = CALL_STATUS_INCOMING;
                            currentClientId = clientId;
                            changeViewCalling();
                        }
                    }

                    if (!isSender && callStatus == CALL_STATUS_REJECTED) {
                        callStatus = CALL_STATUS_IDLE;
                        currentRoomId = "";
                        currentClientId = "";
                        senderSerialCode = "";
                        recipientSerialCode = "";
                        changeViewIdle(""); // 수신자 쪽에서 바로 끊는 것임.
                    }

                    break;
                case "invite":
                    onInviteReceived(roomId, clientId, seSerialCode);
                    break;

                case "accept":
                    if (isSender) {
                        startCallAndReleaseResources();
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
                    callStatus = CALL_STATUS_ACTIVE;
                    if (!isSender) {
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
                case "call-bye": //상대가 끊었음.
                    isHangUpThePhone(method);
                case "invite-calling":
                    isHangUpThePhone(method);
                    break;
                case "bye": // 통화걸었다가 바로 끊음
                    break;
                //** 가요 앱으로 부터
                case "gayo-invite":
                    break;
                case "gayo-invite-away":
                    break;
                case "gayo-invite-ack":
                    break;
                case "gayo-accept-ack":
                    break;
                case "gayo-bye":
                    break;
                case "gayo-call-bye":
                    break;
                case "gayo-no-answer":
                    break;

            }
        } else if (!code.equals("100")) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT);
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

        String jsonString = webSocketBody.toJson();
        WebSocketService.sendWebSocketMessage(jsonString);
    }

    // ------------------------------------------------------
    // 수신자가 초대(invite) 받았을 때 → Offer 생성 플로우
    // ------------------------------------------------------
    private void onInviteReceived(String roomId, String clientId, String seSerialCode) {
        // 이 기기 = 수신자(Offer 만드는 쪽)
        callStatus = CALL_STATUS_INCOMING;
        currentRoomId = roomId;
        currentClientId = clientId;
        senderSerialCode = seSerialCode;
        recipientSerialCode = serialCode;

        // UI 갱신
        changeViewCalling();

        // 벨 울림 (미구현)
        startRingMyBell();

        // 서버에 “invite-ack” 보내는 예시
        WebSocketService.WebSocketBody webSocketBody = new WebSocketService.WebSocketBody(
                "invite-ack",
                recipientSerialCode,
                senderSerialCode,
                currentRoomId,
                currentClientId
        );
        WebSocketService.sendWebSocketMessage(webSocketBody.toJson());

    }

    private void startRingMyBell() {

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

                WebSocketService.WebSocketBody webSocketBody = new WebSocketService.WebSocketBody(
                        "offer",
                        recipientSerialCode,
                        senderSerialCode,
                        currentRoomId,
                        currentClientId,
                        buildingCode, "", "", // 필요 파라미터
                        offerSdp
                );

                WebSocketService.sendWebSocketMessage(webSocketBody.toJson());

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
                        buildingCode,
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
                            case FAILED:
                                callStatus = CALL_STATUS_IDLE;
                                break;
                            case CONNECTED:
                                callStatus = CALL_STATUS_ACTIVE;
                                break;
                            case CHECKING:
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
                        WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                                "candidate",
                                isSender ? recipientSerialCode : "",
                                isSender ? "" : senderSerialCode,
                                currentRoomId,
                                currentClientId,
                                candidate.sdpMLineIndex,
                                candidate.sdpMid,
                                candidate.sdp
                        );

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
                            VideoTrack remoteVideoTrack = (VideoTrack) transceiver.getReceiver().track();
                            remoteVideoTrack.setEnabled(true);
                            runOnUiThread(() -> remoteVideoTrack.addSink(remoteView));

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

        if (localVideoTrack != null) {
            peerConnection.addTrack(localVideoTrack, mediaStreamList);
        } else {
            Log.e(TAG, "localVideoTrack이 null이라 추가 실패");
        }
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
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("offerToReceiveAudio", "true"));

        //오디오
        AudioSource audioSource = peerConnectionFactory.createAudioSource(sdpMediaConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack("AUDIO_TRACK_ID", audioSource);
        localAudioTrack.setEnabled(true);

        //비디오
        videoCapturer = createCameraCapturer();
        if (videoCapturer == null) {
            Log.e(TAG, "videoCapturer is null!");
            return;
        }

        VideoSource videoSource = peerConnectionFactory.createVideoSource(false);
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());

        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());

        try {
            videoCapturer.startCapture(640, 480, 30); // 임시 크기
        } catch (Exception e) {
            e.printStackTrace();
        }

        localVideoTrack = peerConnectionFactory.createVideoTrack("VIDEO_TRACK_ID", videoSource);
        localVideoTrack.setEnabled(true);

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
    public void hangUpCall(String method) {
        // 시그널 서버에 통화 종료 알림
        WebSocketService.WebSocketBody body;
        if (method.equals("call-bye")) {
            body = new WebSocketService.WebSocketBody(
                    method,
                    isSender ? recipientSerialCode : "",
                    isSender ? "" : senderSerialCode,
                    currentRoomId,
                    currentClientId,
                    receivedSdp
            );
        } else {
            body = new WebSocketService.WebSocketBody(
                    method,
                    recipientSerialCode,
                    senderSerialCode,
                    currentRoomId,
                    currentClientId
            );
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

        if (isSender) {
            layCallBtn.setVisibility(View.INVISIBLE);
            layHangUpBtn.setVisibility(View.VISIBLE);
            tvCallMessage.setText("으로 통화를 걸고 있습니다.");
        } else {
            layCallReceptionSelect.setVisibility(View.VISIBLE);
            layButton.setAlpha(0.3F);
            tvCallMessage.setText("으로 부터 통화가 왔습니다.");
        }
    }

    //전화 연결 완료...
    private void changeViewCallingStart() {
        remoteView.setVisibility(View.VISIBLE);
        layCallTime.setVisibility(View.VISIBLE);
        layButton.setAlpha(1F);
        layCallReceptionSelect.setVisibility(View.GONE);
        layHangUpBtn.setVisibility(View.VISIBLE);

//TODO 여기 세대호출인지 관리실 통화인지에 따라 tvFacilityInfo or lay_dong_ho_info 중 setVisibility(View.VISIBLE);
        tvFacilityInfo.setVisibility(View.VISIBLE);
        layCallInfo.setVisibility(View.GONE);
        //** 현재 통화중인 세대 ? 또는 시설 명 보이게 끔하기 오른쪽뷰에

    }


    private void isHangUpThePhone(String method) {
        callStatus = CALL_STATUS_IDLE;

        // 통화 상태 정리
        isSender = false;
        currentRoomId = "";
        currentClientId = "";
        senderSerialCode = "";
        recipientSerialCode = "";
        receivedSdp = "";

        endCallAndReleaseResources();

        // UI 복원 등
        changeViewIdle(method);
    }

    private void changeViewIdle(String method) {
        // 통화 종료 UI
        remoteView.setVisibility(View.GONE);
        layHangUpBtn.setVisibility(View.GONE);
        switch (method) {
            case "invite-away":
            case "no-answer":
            case "invite-norespone":
                tvCallMessage.setText("과(와) 연결이 되지 않습니다.");
                new Handler(Looper.getMainLooper()).postDelayed(() -> changeViewReset(), 2000);
                break;
            case "invite-calling":
                tvCallMessage.setText("이(가) 통화중 입니다.");
                new Handler(Looper.getMainLooper()).postDelayed(() -> changeViewReset(), 2000);
                break;

            case "call-bye":
                tvCallMessage.setText("과(와) 통화가 종료 되었습니다.");
                new Handler(Looper.getMainLooper()).postDelayed(() -> changeViewReset(), 2000);
                break;
            default:
                changeViewReset(); //부재중 ("bye")
                break;

        }

    }

    private void changeViewReset() {
        layCallBtn.setVisibility(View.VISIBLE);
        layCallInfo.setVisibility(View.VISIBLE);
        remoteView.setVisibility(View.GONE);
        layCallView.setVisibility(View.GONE);
        tvFacilityInfo.setVisibility(View.GONE);
        layCallTime.setVisibility(View.GONE);
        layCallReceptionSelect.setVisibility(View.GONE);
        layButton.setAlpha(1);
    }

    private void startCallAndReleaseResources() {
        // 1) OpenGL 초기화
        rootEglBase = EglBase.create();

        // 2) SurfaceViewRenderer 설정 (remoteView만 사용 예시)
        remoteView.init(rootEglBase.getEglBaseContext(), null);
        remoteView.setMirror(false);
        remoteView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

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
}
