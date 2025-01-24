package bluen.homein.gayo_security.rest;

public class RetrofitURL {
// https://documenter.getpostman.com/view/11376758/2sAYJ9BJbj#3f4384e1-de42-4e57-91cf-ba9f45d67d7b // 포스트맨 문서 url

   public final static String BASE_URL = "https://gayo-smarthome-guard.bluen.co.kr/ "; // REAL SERVER
   public final static String BASE_TEST_URL = "https://gayo-smarthome-guard-dev.azurewebsites.net/"; // TEST SERVER

    //** 로그인 및 메인
    public final static String LOGIN_URL = "dev/login";
    public final static String NETWORK_DATA_INFO = "device/network/info";
    public final static String SETTING_DATA_INFO = "device/setting/info";
    public final static String WEATHER_INFO_URL = "device/wt/info";
    public final static String CURRENT_WORKER_INFO = "main/work/manager/info";

    //** 근무자 변경
    public final static String WORKER_LIST = "work/manager/list";
    public final static String WORKER_CHANGE = "work/update/manager/2";

    //** 근무 기록
    public final static String WORK_RECORD_LIST = "work/manager/his/list";

    //**공통 코드
    public final static String WORK_TYPE_LIST = "etc/worktype/list";
    public final static String PATROL_TYPE_LIST = "etc/patrol/list";
    public final static String BELL_SOUND_TYPE_LIST = "etc/patrol/list";
    public final static String CALL_STATE_TYPE_LIST = "etc/call/state/log/info";
    public final static String VISITOR_TYPE_TYPE_LIST = "etc/ent/type/info";

    //** 사용자 설정
    public final static String SAVE_DEVICE_DATA_INFO = "device/update/setting";
}
