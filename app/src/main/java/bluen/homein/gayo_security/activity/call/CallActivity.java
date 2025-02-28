package bluen.homein.gayo_security.activity.call;

import static bluen.homein.gayo_security.activity.addFacilityContact.AddContactActivity.RESULT_CODE_ADD_CONTACT;

import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.androidslidr.Slidr;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera2Enumerator;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
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

    @OnClick(R.id.lay_volume_up)
    void onVolumeUpClick() {
        //code

    }

    @OnClick(R.id.lay_volume_down)
    void onVolumeDownClick() {
        //code

    }

    @OnClick(R.id.lay_call_btn)
    void onCallBtnClick() {
        // 테스트용 더미 데이터입니다. invite gogogogo
        String recipientSerialCode = "testserialCode6";
        WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                "invite",
                recipientSerialCode, // reSerialCode
                serialCode, // sender
                "100",              // code
                "guard"              // device
        );

        String jsonString = body.toJson();  // Gson이 자동으로 { "method":"invite", ... } 생성
        WebSocketService.sendWebSocketMessage(jsonString);
    }

    // WebRTC 관련
    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;
    private EglBase rootEglBase;

    @BindView(R.id.remote_view)
    SurfaceViewRenderer remoteView;

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        finish();
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
        String method = intent.getStringExtra("jsonText");
        switch (method) {
            case "invite":
                onInviteReceived();
                break;
            case "OK":
                break;
            case "invite-ack": //상대방이 통화 요청이 잘 보내진 것을 확인

                break;
            case "accept":
                break;
            case "accept-ack":
                break;
            case "offer":
                break;
            case "answer":
                break;
            case "candidate":
                break;
            case "invite-away":
                break;
            case "bye":
                break;
            case "call-bye":
                break;
            case "no-answer":
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

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_call;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        hideNavigationBar();
        initViews();
        initPeerConnectionFactory();  // WebRTC 초기화
        createLocalMediaTracks();     // 로컬 오디오 트랙 생성
        createPeerConnection();       // PeerConnection 생성

    }

    private void initViews() {

        // 1) EglBase (OpenGL) 초기화
        rootEglBase = EglBase.create();

//        localView.init(rootEglBase.getEglBaseContext(), null);
//        localView.setMirror(true);
//        localView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

        remoteView.init(rootEglBase.getEglBaseContext(), null);
        remoteView.setMirror(false);
        remoteView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
    }

    private void initPeerConnectionFactory() {
        // 전역 초기화
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(this)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);

        // 옵션 설정
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        // HW 가속 사용을 위한 encoder/decoder factory 등
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .createPeerConnectionFactory();
    }

    private void createLocalMediaTracks() {
        // 1) Audio
        MediaConstraints audioConstraints = new MediaConstraints();
        AudioSource audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack("AUDIO_TRACK_ID", audioSource);

        // 2) Video
//        VideoCapturer videoCapturer = createCameraCapturer();
//        if (videoCapturer == null) {
//            Log.d(TAG, "Failed to create VideoCapturer.");
//            return;
//        }
//
//        MediaConstraints videoConstraints = new MediaConstraints();
//        VideoSource videoSource = peerConnectionFactory.createVideoSource(false);
//        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
//        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());

        // 원하는 해상도/프레임
//        try {
//            videoCapturer.startCapture(640, 480, 30);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // 로컬은 영상은 아직 넣을 생각 없음.
//        localVideoTrack = peerConnectionFactory.createVideoTrack("VIDEO_TRACK_ID", videoSource);
//        // 로컬 프리뷰에 표시
//        localVideoTrack.addSink(localView);
    }

    private VideoCapturer createCameraCapturer() {
        // Camera2Enumerator 사용 가능 여부 체크
        Camera2Enumerator enumerator = new Camera2Enumerator(this);
        final String[] deviceNames = enumerator.getDeviceNames();

        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null);
            }
        }
        // 혹은 후면 카메라
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null);
            }
        }
        return null;
    }

    private void createPeerConnection() {
        // ICE 서버 설정 (예: 구글 STUN 서버)
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(
                PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
                        .createIceServer()
        );

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;
        // 필요 시 설정 추가 (tcpCandidatePolicy, bundlePolicy 등)

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
                // --> 시그널 서버에 candidate 전송
                // (candidate.sdpMid, candidate.sdpMLineIndex, candidate.sdp)

            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] candidates) {
            }

            @Override
            public void onAddStream(org.webrtc.MediaStream mediaStream) {
            }

            @Override
            public void onRemoveStream(org.webrtc.MediaStream mediaStream) {
            }

            @Override
            public void onDataChannel(org.webrtc.DataChannel dataChannel) {
            }

            @Override
            public void onRenegotiationNeeded() {
            }

            @Override
            public void onAddTrack(org.webrtc.RtpReceiver rtpReceiver, org.webrtc.MediaStream[] mediaStreams) {
                // Unified Plan일 경우 onAddTrack()이 들어옵니다.
                // remoteTrack일 경우 -> remoteView에 붙이기
                if (rtpReceiver.track() instanceof VideoTrack) {
                    final VideoTrack remoteVideoTrack = (VideoTrack) rtpReceiver.track();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            remoteVideoTrack.addSink(remoteView);
                        }
                    });
                }
            }
        });

        // 오디오, 비디오 트랙 추가
        // Unified Plan 기준으로 addTransceiver()를 쓰는 방법도 있음
        peerConnection.addTrack(localAudioTrack);
//        peerConnection.addTrack(localVideoTrack);
    }


    //** view Change

    //전화 거는 중...
    private void changeViewCalling() {

    }

    //전화 연결 완료...
    private void changeViewCallingStart() {
    }

    //전화 통화 종료...
    private void changeViewIdle() {
    }

    private void onInviteReceived() {
        // 1) UI: 전화가 걸려오는 화면 보여주기
        changeViewCalling();

        // 2) Server로 invite-ack 보내기
//        WebSocketService.WebSocketBody ackBody = new WebSocketService.WebSocketBody(
//                "invite-ack",
//                fromSerialCode,      // 보내 준 쪽 = A
//                mySerialCode,        // 지금 이 단말 = B
//                "someDataOrNull",
//                "guard"
//        );
//        WebSocketService.sendWebSocketMessage(ackBody.toJson());
    }

    //--------------------------------------------------------------------------
    // 시그널 서버에서 사용하는 Offer / Answer / ICE Candidate 교환
    //--------------------------------------------------------------------------

    /**
     * Offer 생성 - (발신자)
     **/
    public void startCallOffer() {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        );
        sdpMediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
        );

        peerConnection.createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                // 1) LocalDescription 설정
                peerConnection.setLocalDescription(new SdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sdp) {
                    }

                    @Override
                    public void onSetSuccess() {
                        // 2) 시그널 서버에 이 SDP를 전송 -> "invite" 메시지
                        // sessionDescription.type = Type.OFFER
                        // sessionDescription.description = SDP

                        // 2) 시그널 서버에 "invite" 메시지로 Offer SDP 전송
                        //    (서버 프로토콜에 맞춰서 key: "sdp", "type", etc.)

                        String offerSdp = sessionDescription.description;  // 실제 SDP 문자열
                        String recipientSerialCode = "testserialCode6";
                        WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                                "invite",
                                recipientSerialCode, // seSerialCode
                                serialCode, // sender
                                "100",              // code
                                "guard"              // device
                        );
                        // 추가로 SDP, type 등을 넣어야 하는 경우:
                        body.setSdp(offerSdp);

                        String jsonString = body.toJson();
                        WebSocketService.sendWebSocketMessage(jsonString);

                        // 3) UI를 "호출 중(Ringing)" 상태로 변경
                        changeViewCalling();
                    }

                    @Override
                    public void onCreateFailure(String s) {
                    }

                    @Override
                    public void onSetFailure(String s) {
                    }
                }, sessionDescription);
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
        }, sdpMediaConstraints);
    }


    /**
     * Answer 생성 - (수신자)
     **/
    public void createAnswer() {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        );
        sdpMediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
        );

        peerConnection.createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                peerConnection.setLocalDescription(new SdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        // 시그널 서버에 answer SDP 보냄
                        // 2) "answer" 메시지 + SDP를 서버로 전송
                        String answerSdp = sessionDescription.description;
                        String sender = "rtc:B@gayo-smarthome...";
                        String receiver = "rtc:A@gayo-smarthome...";

                        WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                                "answer",
                                "testSerialA",
                                sender,
                                "100",
                                "guard"
                        );
                        body.setSdp(answerSdp);

                        String jsonString = body.toJson();
                        WebSocketService.sendWebSocketMessage(jsonString);

                        // 3) B 화면: “통화 연결됨(CallingStart)” UI
                        changeViewCallingStart();
                    }

                    @Override
                    public void onSetFailure(String s) {
                    }

                    @Override
                    public void onCreateSuccess(SessionDescription sdp) {
                    }

                    @Override
                    public void onCreateFailure(String s) {
                    }
                }, sessionDescription);
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
        }, sdpMediaConstraints);
    }

    /**
     * 시그널 서버에서 받은 remoteOffer/remoteAnswer 를 적용
     **/

    public void hangUpCall() {
        // 1) 시그널 서버에 "bye" 보냄
        WebSocketService.WebSocketBody body = new WebSocketService.WebSocketBody(
                "bye",
                "testSerialB",
                "rtc:A@gayo-smarthome-...",
                "100",
                "guard"
        );
        // 필요시 roomId, 또는 다른 정보도 포함
        WebSocketService.sendWebSocketMessage(body.toJson());

        // 2) WebRTC PeerConnection 종료
        if (peerConnection != null) {
            peerConnection.close();
            peerConnection = null;
        }
        // 3) UI → 통화 종료 상태
        changeViewIdle();
    }


    public void onRemoteSessionReceived(String type, String sdp) {
        // type = "offer" or "answer"
        SessionDescription remoteDescription = new SessionDescription(
                SessionDescription.Type.fromCanonicalForm(type),
                sdp
        );

        peerConnection.setRemoteDescription(new SdpObserver() {
            @Override
            public void onSetSuccess() {
                // 만약 remoteOffer 였으면 -> createAnswer() 호출해서 보냄
                // 만약 remoteAnswer 였으면 -> 연결 진행
                // 예: remote가 "answer"면 통화 연결
                if ("answer".equals(type)) {
                    changeViewCallingStart();
                }
            }

            @Override
            public void onSetFailure(String s) {
            }

            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
            }

            @Override
            public void onCreateFailure(String s) {
            }
        }, remoteDescription);
    }

    /**
     * 시그널 서버에서 수신한 ICE Candidate 적용
     **/
    public void onRemoteIceCandidateReceived(String sdpMid, int sdpMLineIndex, String candidate) {
        IceCandidate iceCandidate = new IceCandidate(sdpMid, sdpMLineIndex, candidate);
        peerConnection.addIceCandidate(iceCandidate);
    }

    //--------------------------------------------------------------------------
    // Activity 종료시 정리
    //--------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (peerConnection != null) {
            peerConnection.close();
            peerConnection = null;
        }
//        if (localView != null) {
//            localView.release();
//        }
        if (remoteView != null) {
            remoteView.release();
        }
        if (peerConnectionFactory != null) {
            peerConnectionFactory.dispose();
            peerConnectionFactory = null;
        }
        if (rootEglBase != null) {
            rootEglBase.release();
        }
    }
}
