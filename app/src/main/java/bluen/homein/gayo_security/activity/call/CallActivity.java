package bluen.homein.gayo_security.activity.call;

import static bluen.homein.gayo_security.activity.addFacilityContact.AddContactActivity.RESULT_CODE_ADD_CONTACT;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.androidslidr.Slidr;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
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
import org.webrtc.RtpReceiver;
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
import bluen.homein.gayo_security.service.WebSocketService;
import butterknife.BindView;
import butterknife.OnClick;

public class CallActivity extends BaseActivity {

    private static final int REQUEST_CODE_ADD_CONTACT = 1;

    @BindView(R.id.rv_facility_list)
    RecyclerView rvFacilityList;
    @BindView(R.id.lay_car_number_info)
    ConstraintLayout layCarNumberInfo;
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
    TextView tvDongNumber;
    @BindView(R.id.tv_apt_dong)
    TextView tvDong;
    @BindView(R.id.tv_apt_ho_number)
    TextView tvHoNumber;
    @BindView(R.id.tv_apt_ho)
    TextView tvHo;
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

    // 통화 상태 상수
    public static final int CALL_STATUS_IDLE = 0;
    public static final int CALL_STATUS_INCOMING = 1;
    public static final int CALL_STATUS_CONNECTING = 2;
    public static final int CALL_STATUS_ACTIVE = 3;

    // 현재 상태
    private int callStatus = CALL_STATUS_IDLE;

    // 발신/수신 구분
    // isSender = true  => “전화를 거는 기기”(발신자) => Answer 생성
    // isSender = false => “전화를 받는 기기”(수신자) => Offer 생성
    private boolean isSender = false;

    // 기타
    private String currentRoomId = "";
    private String currentClientId = "";
    private String receivedSdp = "";
    private String senderSerialCode = "";
    private String recipientSerialCode = "";
    boolean isWithGayoApp = false;


    // WebRTC 관련
    private EglBase rootEglBase;
    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;

    @BindView(R.id.remote_view)
    SurfaceViewRenderer remoteView;
    // @BindView(R.id.local_view)
    // SurfaceViewRenderer localView;  // 필요하다면 추가

    // ------------------------------------------------------
    // Activity LifeCycle
    // ------------------------------------------------------
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_call;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        TAG = "CallActivity";
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

        if (getIntent() != null && getIntent().getStringExtra("jsonText") != null) {
            String msg = getIntent().getStringExtra("jsonText");

            try {
                Log.d(TAG, msg);

                handleIntent(msg);

            } catch (JSONException e) {
                Log.e(TAG, "JSON 파싱 오류: " + e.getMessage());
            }
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
    }

    // ------------------------------------------------------
    // 통화 버튼 (예: 발신 측)
    // ------------------------------------------------------
    @OnClick(R.id.lay_call_btn)
    void onCallBtnClick() {
        // “전화를 걸기” → 발신자 역할
        // 이 기기(isSender = true) → Answer를 생성해야 하는 기기
        isSender = true;
        callStatus = CALL_STATUS_INCOMING;

        // 예: 서버에 'invite' 전송
        recipientSerialCode = "testserialCode6";
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

        // UI 갱신 등
        changeViewCalling();
    }

    @OnClick(R.id.lay_hang_up_btn)
    void onHangUpBtnClick() {

        if (callStatus < 2) {
            hangUpCall("bye");
        } else {
            hangUpCall("call-bye");
        }

    }

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        if (callStatus == CALL_STATUS_INCOMING || callStatus == CALL_STATUS_ACTIVE) {
            showPopupDialog("현재 통화중인데 나가면 통화 종료됨.", "확 인");
        } else {
            finish();

        }
    }

    @OnClick(R.id.lay_add_contact_btn)
    void clickChangeWorkerBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        Intent intent = new Intent(CallActivity.this, AddContactActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_CONTACT);
    }

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
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    // ------------------------------------------------------
    // WebSocket 메시지 수신/처리
    // ------------------------------------------------------
    private void handleIntent(String msg) throws JSONException {
        JSONObject json = new JSONObject(msg);
        String method = json.optString("method", "");
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
                case "OK":
                    if (!clientId.isEmpty()) {
                        currentClientId = clientId;
                    }
                    break;
                case "invite":
                    onInviteReceived(roomId, clientId, seSerialCode);
                    break;

                case "accept":
                    if (isSender) {
                        sendAcceptAck();
                    }
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
                    }
                    createLocalMediaTracks();
                    createPeerConnection();
                    createAnswer(); // (발신자)
                    break;

                case "answer":
                    // “수신자”가 만든 answer를 이 쪽(수신자= false)이 아니라
                    // 원래는 caller가 answer를 만드는 게 일반적이지만
                    // 여기서는 반대 구조이므로, offer 만든 기기가 answer를 수신
                    if (!sdp.isEmpty()) {
                        // 이 기기는 “offer를 보낸 기기” → “answer를 받아 setRemoteDescription”
                        applyRemoteAnswer(sdp);
                    }
                    break;

                case "candidate":
                    onRemoteIceCandidateReceived(id, label, candidate);
                    break;
                case "invite-away":  // 상대방이 받지 않음....(부재중)
                case "no-answer":
                case "call-bye":
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

        // 여기서 사용자가 “수락”을 누르면(예: onConnectCallBtnClick),
        // createLocalMediaTracks → createPeerConnection → startCallOffer() 실행
    }

    private void startRingMyBell() {

    }

    @OnClick(R.id.tv_connect_call_btn)
    void onConnectCallBtnClick() {
        // 수신자가 실제로 “수락” 버튼을 누른 상황
        WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                "accept",
                recipientSerialCode,
                senderSerialCode,
                currentRoomId,
                currentClientId
        );
        WebSocketService.sendWebSocketMessage(body.toJson());

        // local 오디오/비디오 트랙 생성
        createLocalMediaTracks();
        // peerConnection 생성
        createPeerConnection();

        // **이제 수신자 입장에서 Offer 생성**
        startCallOffer(); // (수신자)
        callStatus = CALL_STATUS_CONNECTING;
    }

    // ------------------------------------------------------
    // 수신자 쪽에서 Offer 생성
    // ------------------------------------------------------
    public void startCallOffer() {
        // Offer 생성 - (수신자)
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));

        peerConnection.createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
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

                runOnUiThread(() -> changeViewCallingStart());
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
    public void createAnswer() {
        // Answer 생성 - (발신자)
        SessionDescription remoteOffer = new SessionDescription(SessionDescription.Type.OFFER, receivedSdp);
        peerConnection.setRemoteDescription(new SimpleSdpObserver() {
            @Override
            public void onSetSuccess() {
                // 이제 로컬에서 answer 생성
                doCreateLocalAnswer();
            }

            @Override
            public void onSetFailure(String s) {
                Log.e(TAG, "setRemoteDescription 실패: " + s);
            }
        }, remoteOffer);
    }

    private void doCreateLocalAnswer() {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));

        peerConnection.createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                // 로컬 SDP 설정
                peerConnection.setLocalDescription(new SimpleSdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        Log.d(TAG, "Local Answer set 성공");
                    }
                }, sessionDescription);

                // 서버로 전송
                String answerSdp = sessionDescription.description;
                WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                        "answer",
                        recipientSerialCode,
                        senderSerialCode,
                        currentRoomId,
                        currentClientId,
                        buildingCode, "", "", // 필요 파라미터
                        answerSdp
                );
                WebSocketService.sendWebSocketMessage(body.toJson());

                runOnUiThread(() -> changeViewCallingStart());
            }

            @Override
            public void onCreateFailure(String error) {
                Log.e(TAG, "Answer 생성 실패: " + error);
            }

            @Override
            public void onSetSuccess() {
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
        // ICE 서버
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;

        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState newState) {
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState newState) {
            }

            @Override
            public void onIceCandidate(IceCandidate candidate) {
                // 서버 전송
                WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                        "candidate",
                        recipientSerialCode,
                        senderSerialCode,
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
            // Unified Plan에서는 onAddStream 대신 onTrack 콜백을 쓰는 경우가 많음.

            @Override
            public void onDataChannel(DataChannel dataChannel) {
            }

            @Override
            public void onRenegotiationNeeded() {
            }

            // Unified Plan에서는 onTrack이 중요
            @Override
            public void onTrack(RtpTransceiver transceiver) {
                // 원격 트랙을 받았을 때 → remoteView에 붙이기
                if (transceiver.getReceiver().track() instanceof VideoTrack) {
                    VideoTrack remoteVideoTrack = (VideoTrack) transceiver.getReceiver().track();
                    runOnUiThread(() -> {
                        remoteVideoTrack.addSink(remoteView);
                    });
                }
                if (transceiver.getReceiver().track() instanceof AudioTrack) {
                    // 필요 시 오디오 처리
                }
            }
        });

        // (1) 오디오 트랜시버
        RtpTransceiver audioTransceiver = peerConnection.addTransceiver(
                MediaStreamTrack.MediaType.MEDIA_TYPE_AUDIO,
                new RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.SEND_RECV)
        );
        if (localAudioTrack != null) {
            audioTransceiver.getSender().setTrack(localAudioTrack, false);
        }

        // (2) 비디오 트랜시버
        RtpTransceiver videoTransceiver = peerConnection.addTransceiver(
                MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO,
                new RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.SEND_RECV)
        );
        if (localVideoTrack != null) {
            videoTransceiver.getSender().setTrack(localVideoTrack, false);
        }
    }

    // ------------------------------------------------------
    // 로컬 오디오/비디오 트랙 생성
    // ------------------------------------------------------
    private void createLocalMediaTracks() {
        // 오디오
        AudioSource audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        localAudioTrack = peerConnectionFactory.createAudioTrack("AUDIO_TRACK_ID", audioSource);

        // 비디오
        VideoCapturer videoCapturer = createCameraCapturer();
        VideoSource videoSource = peerConnectionFactory.createVideoSource(false);

        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());
        try {
            videoCapturer.startCapture(640, 480, 30);
        } catch (Exception e) {
            e.printStackTrace();
        }
        localVideoTrack = peerConnectionFactory.createVideoTrack("VIDEO_TRACK_ID", videoSource);

        // 필요하다면 localView에 자신의 영상 표시
        // localVideoTrack.addSink(localView);
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
        // 후면 찾기
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
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
        WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                method,
                recipientSerialCode,
                senderSerialCode,
                currentRoomId,
                currentClientId
        );
        WebSocketService.sendWebSocketMessage(body.toJson());

        // PeerConnection 종료
        if (peerConnection != null) {
            peerConnection.close();
            peerConnection = null;
        }
        // UI 갱신 등
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
        layCallReceptionSelect.setVisibility(View.GONE);

//TODO 여기 세대호출인지 관리실 통화인지에 따라 tvFacilityInfo or lay_dong_ho_info 중 setVisibility(View.VISIBLE);
        tvFacilityInfo.setVisibility(View.VISIBLE);
        //** 현재 통화중인 세대 ? 또는 시설 명 보이게 끔하기 오른쪽뷰에

    }


    private void isHangUpThePhone(String method) {
        // 통화 상태 정리
        callStatus = CALL_STATUS_IDLE;
        isSender = false;
        currentRoomId = "";
        currentClientId = "";
        senderSerialCode = "";
        recipientSerialCode = "";

        // UI 복원 등
        changeViewIdle(method);
    }

    private void changeViewIdle(String method) {
        // 통화 종료 UI

        remoteView.setVisibility(View.GONE);
        switch (method) {
            case "invite-away":
            case "no-answer":
            case "invite-norespone":
                tvCallMessage.setText("과(와) 연결이 되지 않습니다.");
                break;
            case "invite-calling":
                tvCallMessage.setText("이(가) 통화중 입니다.");
                break;

            case "call-bye":
                tvCallMessage.setText("과(와) 통화가 종료 되었습니다.");
                break;
            default:
                changeViewReset(); //부재중 ("bye")
                break;

        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> changeViewReset(), 2000);

    }

    private void changeViewReset() {
        layCallBtn.setVisibility(View.VISIBLE);
        remoteView.setVisibility(View.GONE);
        layCallView.setVisibility(View.GONE);
        layCallTime.setVisibility(View.GONE);
        layHangUpBtn.setVisibility(View.GONE);
        layCallReceptionSelect.setVisibility(View.GONE);
        layButton.setAlpha(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (peerConnection != null) {
            peerConnection.close();
            peerConnection = null;
        }
        if (peerConnectionFactory != null) {
            peerConnectionFactory.dispose();
            peerConnectionFactory = null;
        }
        if (rootEglBase != null) {
            rootEglBase.release();
        }
    }

    // ------------------------------------------------------
    //  시그널 서버에서 수신한 ICE Candidate 적용
    // ------------------------------------------------------
    public void onRemoteIceCandidateReceived(String sdpMid, int sdpMLineIndex, String candidate) {
        IceCandidate iceCandidate = new IceCandidate(sdpMid, sdpMLineIndex, candidate);
        peerConnection.addIceCandidate(iceCandidate);
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
