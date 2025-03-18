package bluen.homein.gayo_security.service;

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
import java.util.List;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.call.CallActivity;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketService extends Service {
    String TAG = "WebSocketService";
    private static WebSocket webSocket;  // 전역(정적 아님) or Singleton으로 관리
    private OkHttpClient client;
    private String auth = "";
    private static final String SIGNAL_SERVER_URL = "wss://gayo-smarthome-guard-signalserver.bluen.co.kr/guard/ws";
    private RequestDataFormat.DeviceBody deviceBody;

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

        if (intent != null) {
            Log.d(TAG, "onStartCommand: startWebSocketConnection");

            RequestDataFormat.DeviceBody _deviceBody =
                    (RequestDataFormat.DeviceBody) intent.getSerializableExtra("deviceBody");
            deviceBody = _deviceBody;
            auth = intent.getStringExtra("authorization");

            if (!auth.isEmpty()) {
                startWebSocketConnection(_deviceBody);
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

    private void startWebSocketConnection(RequestDataFormat.DeviceBody deviceBody) {
        client = new OkHttpClient();
        Request request = new Request.Builder().url(SIGNAL_SERVER_URL)
                .addHeader("Authorization", auth)
                .addHeader("BuilCode", deviceBody.getBuildingCode())
                .addHeader("SerialCode", deviceBody.getSerialCode())
                .addHeader("MacAddr", deviceBody.getDeviceNetworkBody().getMacAddress())
                .addHeader("IPAddr", deviceBody.getDeviceNetworkBody().getIpAddress()).build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                Log.d(TAG, "onOpen: WebSocket connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                // 수신부!!!!!
                Log.d(TAG, "onMessage: " + text); //

                try {
                    handleServerMessage(text);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                // 필요 없으면 생략
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                Log.e(TAG, "onFailure: " + t.getMessage());
                // 연결이 실패하면 일정 시간 후 재연결 시도
                reconnectWebSocketWithDelay();
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d(TAG, "onClosed: code=" + code + ", reason=" + reason);
            }
        });

        // 참고: client가 종료되지 않도록, onDestroy()에서 종료 처리
    }

    // 재연결 로직 메서드 추가
    private void reconnectWebSocketWithDelay() {
        Log.d(TAG, "WebSocket disconnected, trying to reconnect in 5 seconds...");

        new Handler(Looper.getMainLooper())
                .postDelayed(() -> startWebSocketConnection(deviceBody), 5000);
    }

    private void handleServerMessage(String jsonText) throws JSONException {
        // 1) JSON 파싱 (간단 예시)
        //    실제로는 JSONObject 를 이용해서 "method" 필드를 확인
        //    예: {"method":"invite", "roomId":"47263086", ...}

        WebSocketBody body = new Gson().fromJson(jsonText, WebSocketBody.class);

        String method = body.getMethod();    // null 일 수도 있음
        String code = body.getCode();      // "Error"
        String device = body.getDevice();    // "signalServer"
        String msg = body.getMessage();   // "오류가 발생 했습니다." (필드 추가했다면)

        if ("Error".equals(code)) {
            Log.e(TAG, "서버에서 오류 수신: " + msg);
            // 에러 로직 처리
            return;
        }

        // 2) 앱이 포그라운드인지 백그라운드인지 판단
        if (isAppInForeground(getApplicationContext())) {
            // 앱이 현재 실행중(포그라운드)이면 곧바로 CallActivity로 이동
            startCallActivity(jsonText);
        } else {
            showIncomingCallNotification(jsonText);
        }

    }

    private void showIncomingCallNotification(String jsonText) {
        // 1) Notification 클릭 시 이동할 Intent
        Intent callIntent = new Intent(getApplicationContext(), CallActivity.class);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        callIntent.putExtra("jsonText", jsonText);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                callIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 2) Notification 구성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CALL_CHANNEL_ID")
                .setSmallIcon(R.drawable.call_icon) //임시
                .setContentTitle("Incoming Call")
                .setContentText("상대방이 전화를 걸어왔습니다.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // 3) Notification 매니저로 표시
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(
                "CALL_CHANNEL_ID",
                "Call Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(1001, builder.build());
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
            Log.d("WebSocketService", "웹소켓 전송 직전 JSON: " + message);
            webSocket.send(message);
        }
    }

    public static class WebSocketBody implements Serializable {

        @SerializedName("method")
        private String method;
        @SerializedName("reSerialCode")
        private String reSerialCode;
        @SerializedName("seSerialCode")
        private String seSerialCode;
        @SerializedName("sender")
        private final String sender;
        @SerializedName("receiver")
        private String receiver;
        @SerializedName("code")
        private String code = "100";
        @SerializedName("device")
        private String device = "guard";
        @SerializedName("roomId")
        private String roomId;
        @SerializedName("clientId")
        private String clientId;
        @SerializedName("title")
        private String title;
        @SerializedName("builCode")
        private String builCode;
        @SerializedName("builDong")
        private String builDong;
        @SerializedName("builHo")
        private String builHo;
        @SerializedName("candidate")
        private String candidate;
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

        public String getId() {
            return id;
        }

        public int getLabel() {
            return label;
        }

        // 1) 기본 생성자, 혹은 필요한 생성자
//경비실기 - 경비실기
        public WebSocketBody(String method, String reSerialCode, String seSerialCode) {
            this.method = method;// invite, invite-away, invite-ack, accept,accept-ack, offer, answer, candidate, bye, call-bye,no-answer
            this.reSerialCode = reSerialCode;
            this.seSerialCode = seSerialCode;
            this.sender = "rtc:" + seSerialCode + "@" + SIGNAL_SERVER_URL;
            this.receiver = "rtc:" + reSerialCode + "@" + SIGNAL_SERVER_URL;
            // 8자리 랜덤 숫자: 10000000 ~ 99999999 범위
        }

        public WebSocketBody(String method, String reSerialCode, String seSerialCode, String roomId, String clientId) {
            this.method = method;// invite, invite-away, invite-ack, accept,accept-ack, offer, answer, candidate, bye, call-bye,no-answer
            this.reSerialCode = reSerialCode;
            this.seSerialCode = seSerialCode;
            this.sender = "rtc:" + seSerialCode + "@" + SIGNAL_SERVER_URL;
            this.receiver = "rtc:" + reSerialCode + "@" + SIGNAL_SERVER_URL;
            // 8자리 랜덤 숫자: 10000000 ~ 99999999 범위
            this.roomId = roomId;
            this.clientId = clientId;
        }

        public WebSocketBody(String method, String reSerialCode, String seSerialCode, String roomId, String clientId, int label, String id, String candidate) {
            this.method = method;// invite, invite-away, invite-ack, accept,accept-ack, offer, answer, candidate, bye, call-bye,no-answer
            this.reSerialCode = reSerialCode;
            this.seSerialCode = seSerialCode;
            this.sender = "rtc:" + seSerialCode + "@" + SIGNAL_SERVER_URL;
            this.receiver = "rtc:" + reSerialCode + "@" + SIGNAL_SERVER_URL;
            // 8자리 랜덤 숫자: 10000000 ~ 99999999 범위
            this.roomId = roomId;
            this.clientId = clientId;
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
            this.roomId = roomId;
            this.clientId = clientId;
            this.builCode = builCode;
            this.builDong = builDong;
            this.builHo = builHo;
            this.sdp = sdp;

        }

        public void setSdp(String sdp) {
            this.sdp = sdp;
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
            this.roomId = roomId;
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