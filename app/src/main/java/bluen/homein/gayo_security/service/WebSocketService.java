package bluen.homein.gayo_security.service;

import static bluen.homein.gayo_security.activity.call.CallActivity.CALL_STATUS_ACTIVE;
import static bluen.homein.gayo_security.activity.call.CallActivity.CALL_STATUS_IDLE;
import static bluen.homein.gayo_security.global.GlobalApplication.callStatus;
import static bluen.homein.gayo_security.preference.Gayo_Preferences.DEVICE_NOT_CONNECTED;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.call.CallActivity;
import bluen.homein.gayo_security.rest.MyGson;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketService extends Service {
    private static String TAG = "WebSocketService";
    private static WebSocket webSocket;  // 전역(정적 아님) or Singleton으로 관리
    private OkHttpClient client;
    private String auth = "";
    //    public static final String SIGNAL_SERVER_URL = "wss://gayo-smarthome-guard-signalserver.bluen.co.kr/guard/ws"; // OLD
    public static final String SIGNAL_SERVER_URL = "wss://smarthomesignalserver.bluen.co.kr:28088/ws"; // NEW
    //    public static final String SIGNAL_WALLPAD_SERVER_URL = "";
    private RequestDataFormat.DeviceBody deviceBody;
    private static boolean isConnected = false;
    private final List<String> pendingMessages = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service started");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            Log.d(TAG, "onStartCommand");
            String jsonText = intent.getStringExtra("websocketMessage");
            if (jsonText != null) {
                RequestDataFormat.DeviceBody _deviceBody =
                        (RequestDataFormat.DeviceBody) intent.getSerializableExtra("deviceBody");
                deviceBody = _deviceBody;
                auth = intent.getStringExtra("authorization");
                if (!isConnected) {
                    startDeviceWebSocketConnection(deviceBody, jsonText);
                    if (!jsonText.isEmpty()) {
                        pendingMessages.add(jsonText);
                    }
                } else {
                    startCallActivity(jsonText);

                }
            }
        }

        return START_STICKY;
    }

    public boolean isAppInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        if (processes == null) return false;

        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && processInfo.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private void startDeviceWebSocketConnection(RequestDataFormat.DeviceBody deviceBody, String jsonText) {
        client = new OkHttpClient();

        WebSocketService.WebSocketBody body =
                new Gson().fromJson(jsonText.toString(), WebSocketService.WebSocketBody.class);

        Request request = new Request.Builder().url(SIGNAL_SERVER_URL)
                .addHeader("Authorization", auth)
                .addHeader("BuilCode", deviceBody.getBuildingCode())
                .addHeader("SerialCode", deviceBody.getSerialCode())
                .addHeader("MacAddr", deviceBody.getDeviceNetworkBody().getMacAddress())
                .addHeader("IPAddr", deviceBody.getDeviceNetworkBody().getIpAddress()).build();
        String message = body.getMessage() != null ? body.getMessage() : "";

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                Log.d(TAG, "onOpen: WebSocket connected");

                isConnected = true;
                if (pendingMessages.isEmpty()) {
                    if (jsonText != null && !jsonText.isEmpty()) {

                        if (callStatus == CALL_STATUS_IDLE && body.getMethod().equals("invite") && !body.getDevice().equals("guard") || callStatus == CALL_STATUS_IDLE && body.getMethod().equals("OK")
                                || callStatus != CALL_STATUS_IDLE || message.equals(DEVICE_NOT_CONNECTED)) {
                            // 2) 앱이 포그라운드인지 백그라운드인지 판단
                            if (isAppInForeground(getApplicationContext())) {
                                // 앱이 현재 실행중(포그라운드)이면 곧바로 CallActivity로 이동
                                startCallActivity(jsonText);
                            }
                        }

                    }
                } else {
                    for (String m : pendingMessages) startCallActivity(jsonText);
                    pendingMessages.clear();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                // 수신부!!!!!
                Log.d(TAG, "새 메세지가 도착했습니다 onMessage: " + text); //

                if ("pong".equals(text)) {
                    Log.d(TAG, "Heartbeat pong received");
                } else {
                    try {
                        handleServerMessage(text);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                Log.e(TAG, "onFailure: " + t.getMessage());
//                reconnectWebSocketWithDelay(jsonText);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                Log.d(TAG, "onClosing: 연결 종료 예정, reason: " + reason);
//                reconnectWebSocketWithDelay(jsonText);

            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d(TAG, "onClosed: code=" + code + ", reason=" + reason);
//                reconnectWebSocketWithDelay(jsonText);

            }
        });

        // 참고: client가 종료되지 않도록, onDestroy()에서 종료 처리
    }

    public static void disconnectWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Client requested disconnect");  // 정상 종료 코드
            webSocket = null;
            isConnected = false;

            Log.d(TAG, "WebSocket disconnected (service still running)");
        }
    }

    // 재연결 로직 메서드 추가
    private void reconnectWebSocketWithDelay(String jsonText) {
        Log.d(TAG, "WebSocket disconnected, trying to reconnect in 5 seconds...");
        new Handler(Looper.getMainLooper())
                .postDelayed(() -> startDeviceWebSocketConnection(deviceBody, jsonText), 5000);

    }

    private void handleServerMessage(String jsonText) throws JSONException {
        // 1) JSON 파싱 (간단 예시)
        //    실제로는 JSONObject 를 이용해서 "method" 필드를 확인
        //    예: {"method":"invite", "roomId":"47263086", ...}

        WebSocketBody body = new Gson().fromJson(jsonText, WebSocketBody.class);
        String message = body.getMessage() != null ? body.getMessage() : "";

        if (callStatus == CALL_STATUS_IDLE && body.getMethod().equals("invite") || callStatus == CALL_STATUS_IDLE && body.getMethod().equals("OK")
                || callStatus != CALL_STATUS_IDLE || message.equals(DEVICE_NOT_CONNECTED)) {
            // 2) 앱이 포그라운드인지 백그라운드인지 판단
            if (isAppInForeground(getApplicationContext())) {
                // 앱이 현재 실행중(포그라운드)이면 곧바로 CallActivity로 이동
                startCallActivity(jsonText);
            }
        }

    }

    private void startCallActivity(String jsonText) {
        Intent callIntent = new Intent(getApplicationContext(), CallActivity.class);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        callIntent.putExtra("jsonText", jsonText);

        getApplicationContext().startActivity(callIntent); // 가서 파싱
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Service stopped");
        if (webSocket != null) {
            webSocket.cancel();
            webSocket.close(1000, "Service destroyed");
            webSocket = null;
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }

    /**
     * 외부(예: CallActivity)에서 호출 가능한 send 메서드
     */

    public static void sendWebSocketMessage(String message) {
        if (webSocket != null) {

            boolean isSent = webSocket.send(message);
            if (isSent) {
                Log.d("WebSocketService", "웹소켓 전송 성공 JSON: " + message);

            } else {
                Log.e("WebSocketService", "웹소켓 전송 실패 (연결 끊김): " + message);

            }
        } else {
            Log.e("WebSocketService", "webSocket 객체가 null입니다. 연결되지 않음.");
        }
    }

    public static class WebSocketBody extends MyGson implements Serializable {

        @SerializedName("method")
        private String method;
        @SerializedName("reSerialCode")
        private String reSerialCode;
        @SerializedName("seSerialCode")
        private String seSerialCode;
        @SerializedName("serialCode")
        private String serialCode;
        @SerializedName("sender")
        private String sender;
        @SerializedName("receiver")
        private String receiver;
        @SerializedName("code")
        private String code = "100";
        @SerializedName("device")
        private String device = "guard";
        @SerializedName("roomid")
        private String roomid;
        @SerializedName("clientid")
        private String clientid;
        @SerializedName("title")
        private String title;
        @SerializedName("builCode")
        private String builCode;
        @SerializedName("builDong")
        private String builDong;
        @SerializedName("builHo")
        private String builHo;
        @SerializedName("userIdx")
        private String userIdx;
        @SerializedName("candidate")
        private String candidate;
        @SerializedName("callDevice")
        private String callDevice;
        @SerializedName("content")
        private String content;
        @SerializedName("sdp")
        private String sdp;
        @SerializedName("message")
        private String message;
        @SerializedName("id")
        private String id;
        @SerializedName("label")
        private int label;
        @SerializedName("lobbyPhoneSerialCode")
        private String lobbyPhoneSerialCode;
        @SerializedName("guardSerialCode")
        private String guardSerialCode;


        public WebSocketBody(String method, String reSerialCode) {
            this.method = method;
            this.reSerialCode = reSerialCode;
        }

        public WebSocketBody(String method, String recipientSerialCode, String senderSerialCode, String currentRoomId, String currentClientId, String receivedSdp) {
            this.method = method;
            this.reSerialCode = recipientSerialCode;
            this.seSerialCode = senderSerialCode;
            this.roomid = currentRoomId;
            this.clientid = currentClientId;
            this.sender = "rtc:" + seSerialCode + "@" + SIGNAL_SERVER_URL;
            this.receiver = "rtc:" + reSerialCode + "@" + SIGNAL_SERVER_URL;
            this.sdp = receivedSdp;
        }

        // 1) 기본 생성자, 혹은 필요한 생성자
//경비실기 - 경비실기
        public WebSocketBody(String method, String recipientSerialCode, String senderSerialCode) {
            this.method = method;// invite, invite-away, invite-ack, accept,accept-ack, offer, answer, candidate, bye, call-bye,no-answer
            this.reSerialCode = recipientSerialCode;
            this.seSerialCode = senderSerialCode;
            this.sender = "rtc:" + seSerialCode + "@" + SIGNAL_SERVER_URL;
            this.receiver = "rtc:" + reSerialCode + "@" + SIGNAL_SERVER_URL;
            // 8자리 랜덤 숫자: 10000000 ~ 99999999 범위
        }


        public WebSocketBody(String method, String _reSerialCode, String seSerialCode, String roomId, String clientId) {
            this.method = method;// invite, invite-away, invite-ack, accept,accept-ack, offer, answer, candidate, bye, call-bye,no-answer
            this.reSerialCode = _reSerialCode;
            this.seSerialCode = seSerialCode;
            this.sender = "rtc:" + seSerialCode + "@" + SIGNAL_SERVER_URL;
            this.receiver = "rtc:" + _reSerialCode + "@" + SIGNAL_SERVER_URL;
            // 8자리 랜덤 숫자: 10000000 ~ 99999999 범위
            this.roomid = roomId;
            this.clientid = clientId;
        }

        public WebSocketBody(String method, String reSerialCode, String seSerialCode, String roomId, String clientId, int label, String id, String candidate) {
            this.method = method;// invite, invite-away, invite-ack, accept,accept-ack, offer, answer, candidate, bye, call-bye,no-answer
            this.reSerialCode = reSerialCode;
            this.seSerialCode = seSerialCode;
            this.sender = "rtc:" + seSerialCode + "@" + SIGNAL_SERVER_URL;
            this.receiver = "rtc:" + reSerialCode + "@" + SIGNAL_SERVER_URL;
            // 8자리 랜덤 숫자: 10000000 ~ 99999999 범위
            this.roomid = roomId;
            this.clientid = clientId;
            this.id = id;
            this.label = label;
            this.candidate = candidate;
        }

        public WebSocketBody(String method, String reSerialCode, String seSerialCode, String roomId, String clientId, String builCode, String builDong, String builHo, String sdp) {
            this.method = method;// invite, invite-away, invite-ack, accept,accept-ack, offer, answer, candidate, bye, call-bye,no-answer
            this.reSerialCode = reSerialCode;
            this.seSerialCode = seSerialCode;
            this.sender = "rtc:" + seSerialCode + "@" + SIGNAL_SERVER_URL;
            this.receiver = "rtc:" + reSerialCode + "@" + SIGNAL_SERVER_URL;
            this.roomid = roomId;
            this.clientid = clientId;
            this.builCode = builCode;
            this.builDong = builDong;
            this.builHo = builHo;
            this.sdp = sdp;

        }

        public WebSocketBody(String method, String reSerialCode, String seSerialCode, String roomId, String clientId, String builCode, String builDong, String builHo) {
            this.method = method;// invite, invite-away, invite-ack, accept,accept-ack, offer, answer, candidate, bye, call-bye,no-answer
            this.sender = "rtc:" + seSerialCode + "@" + SIGNAL_SERVER_URL;
            this.receiver = "rtc:" + reSerialCode + "@" + SIGNAL_SERVER_URL;
            this.roomid = roomId;
            this.clientid = clientId;
            this.builCode = builCode;
            this.builDong = builDong;
            this.builHo = builHo;
        }

        // 경비실기 -> 가요앱, reSerialCode는 그냥 더미용
        public WebSocketBody(String method, String seSerialCode, String roomId, String builCode) {
            this.method = method;
            this.seSerialCode = seSerialCode;
            this.roomid = roomId;
            this.builCode = builCode;
        }

        public void setCallDevice(String callDevice) {
            this.callDevice = callDevice;
        }

        public void setLobbyPhoneSerialCode(String lobbyPhoneSerialCode) {
            this.lobbyPhoneSerialCode = lobbyPhoneSerialCode;
        }
        public void setGuardSerialCode(String guardSerialCode) {
            this.guardSerialCode = guardSerialCode;
        }

        public void setReSerialCode(String reSerialCode) {
            this.reSerialCode = reSerialCode;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public void setBuilDong(String builDong) {
            this.builDong = builDong;
        }

        public void setBuilHo(String builHo) {
            this.builHo = builHo;
        }

        public void setUserIdx(String userIdx) {
            this.userIdx = userIdx;
        }

        public void setSdp(String sdp) {
            this.sdp = sdp;
        }

        public String getSerialCode() {
            return serialCode;
        }

        public String getSeSerialCode() {
            return seSerialCode;
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

        public String getCandidate() {
            return candidate;
        }

        public String getMethod() {
            return method;
        }

        public void setRoomId(String roomId) {
            this.roomid = roomId;
        }

        public void setSerialCode(String serialCode) {
            this.serialCode = serialCode;
        }

        public String getCallDevice() {
            return callDevice;
        }

        public String getId() {
            return id;
        }

        public int getLabel() {
            return label;
        }

        public String getReSerialCode() {
            return reSerialCode;
        }


        public String getSender() {
            return sender;
        }

        public String getReceiver() {
            return receiver;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDevice() {
            return device;
        }

        public String getSdp() {
            return sdp;
        }

        public String getMessage() {
            return message;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String toJson() {
            return new Gson().toJson(this);
        }
    }
}